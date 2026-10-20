package org.glotaran.timpcontroller.runtime;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/** Options panel: which R Glotaran uses, and the state of the R that Glotaran installs. */
final class RRuntimePanel extends JPanel {

    private static final RequestProcessor RP = new RequestProcessor(RRuntimePanel.class.getName(), 1);

    private final RRuntime runtime = RRuntime.getDefault();
    private final JRadioButton managed = new JRadioButton("Installed by Glotaran");
    private final JRadioButton ownR = new JRadioButton("My own R (Rserve on localhost:6311)");
    private final ButtonGroup modes = new ButtonGroup();
    private final JLabel status = new JLabel();
    private final JLabel versions = new JLabel();
    private final JLabel location = new JLabel();
    private final JLabel size = new JLabel();
    private final JButton install = new JButton("Install");
    private final JButton remove = new JButton("Remove");
    private final JButton openLog = new JButton("Open install log");
    /** An install or remove started from this panel is running. */
    private boolean busy;

    RRuntimePanel(RRuntimeOptionsPanelController controller) {
        super(new GridBagLayout());
        modes.add(managed);
        modes.add(ownR);
        managed.addActionListener(e -> controller.changed());
        ownR.addActionListener(e -> controller.changed());
        install.addActionListener(e -> install());
        remove.addActionListener(e -> remove());
        openLog.addActionListener(e -> openLog());

        JPanel mode = new JPanel(new GridBagLayout());
        mode.setBorder(BorderFactory.createTitledBorder("Which R Glotaran uses"));
        addRow(mode, 0, managed, null);
        addRow(mode, 1, ownR, null);

        JPanel state = new JPanel(new GridBagLayout());
        state.setBorder(BorderFactory.createTitledBorder("R installed by Glotaran"));
        addRow(state, 0, new JLabel("Status:"), status);
        addRow(state, 1, new JLabel("Versions:"), versions);
        addRow(state, 2, new JLabel("Location:"), location);
        addRow(state, 3, new JLabel("Size on disk:"), size);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        buttons.add(install);
        buttons.add(remove);
        buttons.add(openLog);
        addRow(state, 4, buttons, null);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        add(mode, c);
        c.weighty = 1;
        add(state, c);
    }

    private static void addRow(JPanel panel, int row, javax.swing.JComponent first, javax.swing.JComponent second) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 4, 2, 8);
        if (second == null) {
            c.gridwidth = 2;
            c.weightx = 1;
            panel.add(first, c);
            return;
        }
        panel.add(first, c);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(second, c);
    }

    void load() {
        RMode mode = RMode.get();
        modes.clearSelection();
        managed.setSelected(mode == RMode.MANAGED);
        ownR.setSelected(mode == RMode.OWN_R);
        refresh();
    }

    void store() {
        RMode selected = managed.isSelected() ? RMode.MANAGED : ownR.isSelected() ? RMode.OWN_R : null;
        if (selected != null && selected != RMode.get()) {
            RMode.set(selected);
        }
    }

    private void refresh() {
        boolean supported = runtime.isSupported();
        managed.setEnabled(supported);
        RRuntime.State state = supported ? runtime.getState() : RRuntime.State.NOT_INSTALLED;
        Properties installed = runtime.getInstalledVersions();
        if (!supported) {
            status.setText("Glotaran cannot install R on this operating system or processor. Use your own R.");
        } else if (busy || state == RRuntime.State.INSTALLING) {
            status.setText("Working; the progress is shown in the status bar of the main window");
        } else if (state == RRuntime.State.READY) {
            status.setText("Installed");
        } else if (state == RRuntime.State.FAILED) {
            status.setText("<html>Installation failed: " + escape(runtime.getFailure()) + "</html>");
        } else {
            status.setText("Not installed; download about " + runtime.getDownloadSize() + " MB, about 1.2 GB on disk");
        }
        versions.setText(installed == null ? "-" : "R " + installed.getProperty("r.version")
                + ", TIMP " + installed.getProperty("timp.version") + ", Rserve " + installed.getProperty("rserve.version"));
        location.setText(supported ? runtime.getDirectory().toString() : "-");
        boolean idle = supported && !busy && state != RRuntime.State.INSTALLING;
        install.setText(installed == null ? "Install" : "Reinstall");
        install.setEnabled(idle);
        remove.setEnabled(idle && Files.isDirectory(runtime.getRoot()));
        openLog.setEnabled(supported && Files.isRegularFile(runtime.getLog()));
        size.setText("-");
        if (supported && !busy && state != RRuntime.State.INSTALLING) {
            Path directory = runtime.getDirectory();
            RP.post(() -> {
                String text = sizeOf(directory);
                SwingUtilities.invokeLater(() -> size.setText(text));
            });
        }
    }

    private static String sizeOf(Path directory) {
        if (!Files.isDirectory(directory)) {
            return "-";
        }
        try (Stream<Path> files = Files.walk(directory)) {
            long bytes = files.filter(Files::isRegularFile).mapToLong(file -> {
                try {
                    return Files.size(file);
                } catch (IOException e) {
                    return 0;
                }
            }).sum();
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        } catch (IOException | RuntimeException e) {
            return "-";
        }
    }

    private void install() {
        if (runtime.getInstalledVersions() != null && !confirm("Glotaran deletes its R and downloads and installs it again "
                + "(about " + runtime.getDownloadSize() + " MB). A running analysis stops.")) {
            return;
        }
        run(ManagedR::install);
    }

    private void remove() {
        if (!confirm("Glotaran deletes its R (" + runtime.getDirectory() + "). A running analysis stops. "
                + "If Glotaran uses this R, the next analysis installs it again.")) {
            return;
        }
        run(ManagedR::remove);
    }

    private interface Action {

        void run() throws IOException;
    }

    /** Runs the action in the background and refreshes the panel when it is done. */
    private void run(Action action) {
        busy = true;
        refresh();
        RP.post(() -> {
            try {
                action.run();
            } catch (CancellationException e) {
                // The status shows the result.
            } catch (IOException e) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(e.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    busy = false;
                    refresh();
                });
            }
        });
    }

    private void openLog() {
        Path log = runtime.getLog();
        RP.post(() -> {
            try {
                Desktop.getDesktop().open(log.toFile());
            } catch (IOException | RuntimeException e) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Cannot open " + log + ": " + e.getMessage(),
                        NotifyDescriptor.ERROR_MESSAGE));
            }
        });
    }

    private static boolean confirm(String message) {
        NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(message, "R runtime", NotifyDescriptor.OK_CANCEL_OPTION);
        return DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION;
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;");
    }
}
