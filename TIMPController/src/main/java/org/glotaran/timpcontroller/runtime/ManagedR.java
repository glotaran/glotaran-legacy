package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CancellationException;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/** Chooses which R Glotaran uses and runs the managed one for TimpController. */
public final class ManagedR {

    /** Where the user changes the mode and reinstalls the runtime. */
    public static final String OPTIONS_LOCATION = "Tools > Options > Options > R runtime";
    private static final String USE_OWN_R = "Use my own R installation";
    private static final String INSTALL_R = "Download and install R for Glotaran";

    private static volatile RserveProcess rserve;

    private ManagedR() {
    }

    /**
     * Returns the stored mode. On the first call it decides: if an R or an Rserve of the user is found, it asks the
     * user, otherwise it chooses {@link RMode#MANAGED}. On platforms without a managed runtime it returns
     * {@link RMode#OWN_R} without storing it.
     *
     * @return the mode, or {@code null} if the user cancelled the question (nothing is stored)
     */
    public static synchronized RMode resolveMode() {
        RMode mode = RMode.get();
        if (mode != null) {
            return mode;
        }
        RRuntime runtime = RRuntime.getDefault();
        if (!runtime.isSupported()) {
            return RMode.OWN_R;
        }
        String found = ExistingR.find();
        if (found != null) {
            mode = ask(found, runtime);
            if (mode == null) {
                return null;
            }
        } else {
            mode = RMode.MANAGED;
        }
        RMode.set(mode);
        return mode;
    }

    private static RMode ask(String found, RRuntime runtime) {
        String message = found + "\n\n"
                + USE_OWN_R + ": Glotaran connects to an Rserve that you start on localhost:6311, as in Glotaran 1.6. "
                + "R needs the packages TIMP and Rserve.\n\n"
                + INSTALL_R + ": Glotaran downloads about " + runtime.getDownloadSize() + " MB and installs its own R "
                + "with TIMP and Rserve (about 1.2 GB on disk). It starts and stops this R itself.\n\n"
                + "You can change this later in " + OPTIONS_LOCATION + ".";
        NotifyDescriptor descriptor = new NotifyDescriptor(message, "Which R should Glotaran use?",
                NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.QUESTION_MESSAGE,
                new Object[]{USE_OWN_R, INSTALL_R, NotifyDescriptor.CANCEL_OPTION}, USE_OWN_R);
        Object choice = DialogDisplayer.getDefault().notify(descriptor);
        if (USE_OWN_R.equals(choice)) {
            return RMode.OWN_R;
        }
        return INSTALL_R.equals(choice) ? RMode.MANAGED : null;
    }

    /**
     * Installs the managed runtime if needed, with a notification and a cancellable progress bar, and starts Rserve.
     * Call from a background thread.
     *
     * @param rserveOutput receives the output of R
     * @return the port of Rserve on the loopback interface
     * @throws IOException if a step fails; the message names the step
     * @throws CancellationException if the user cancelled the installation
     */
    public static synchronized int start(PrintWriter rserveOutput) throws IOException {
        RRuntime runtime = RRuntime.getDefault();
        if (runtime.findMicromamba() == null || !runtime.isReady()) {
            install(runtime);
        }
        if (rserve == null) {
            rserve = new RserveProcess(runtime, rserveOutput);
        }
        return rserve.ensureStarted();
    }

    /**
     * Stops the managed Rserve, deletes the runtime and installs it again, with a notification and a cancellable
     * progress bar. Used by the options panel. Call from a background thread.
     *
     * @throws CancellationException if the user cancelled the installation
     */
    public static synchronized void install() throws IOException {
        RRuntime runtime = RRuntime.getDefault();
        stopRserve();
        runtime.remove();
        install(runtime);
    }

    /** Stops the managed Rserve and deletes the runtime. Call from a background thread. */
    public static synchronized void remove() throws IOException {
        stopRserve();
        RRuntime.getDefault().remove();
    }

    private static void install(RRuntime runtime) throws IOException {
        NotificationDisplayer.getDefault().notify("Installing R for Glotaran",
                ImageUtilities.loadImageIcon("org/netbeans/modules/dialogs/info.png", false),
                "Glotaran downloads about " + runtime.getDownloadSize() + " MB and installs R, TIMP and Rserve "
                + "(about 1.2 GB on disk). The analysis starts when the installation is complete.", null);
        InputOutput io = IOProvider.getDefault().getIO("R runtime", false);
        ProgressHandle progress = ProgressHandle.createHandle("Installing R for Glotaran", () -> {
            runtime.cancel();
            return true;
        });
        progress.start();
        try {
            runtime.ensureReady(progress, io.getOut());
        } finally {
            progress.finish();
        }
    }

    /** @return {@code true} if the Rserve started by {@link #start} is running */
    public static boolean isRunning() {
        RserveProcess process = rserve;
        return process != null && process.isRunning();
    }

    /** Cancels a running installation and stops the Rserve started by {@link #start}. Does not wait for {@link #start}. */
    public static void stop() {
        RRuntime.getDefault().cancel();
        stopRserve();
    }

    private static void stopRserve() {
        RserveProcess process = rserve;
        if (process != null) {
            process.stop();
        }
    }
}
