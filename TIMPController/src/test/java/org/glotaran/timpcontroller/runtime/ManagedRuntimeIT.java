package org.glotaran.timpcontroller.runtime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netbeans.api.progress.ProgressHandle;
import org.rosuda.REngine.Rserve.RConnection;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

/**
 * Installs the bundled R runtime of this platform as Glotaran does ({@link RRuntime#ensureReady}, which downloads
 * micromamba and about 300 MB of packages), starts Rserve and fits a small kinetic model with TIMP over Glotaran's
 * Java client. It is not a unit test (Surefire runs only *Test classes by default); run it with
 * {@code -DskipTests=false -Dtest=ManagedRuntimeIT}.
 */
public class ManagedRuntimeIT {

    /** TIMP's fitModel example with two exponential decays (rates 0.5 and 1) and fixed noise. */
    private static final String FIT = "suppressMessages(library(TIMP)); set.seed(42); "
            + "C <- matrix(nrow = 51, ncol = 2); k <- c(.5, 1); t <- seq(0, 2, by = 2/50); "
            + "C[, 1] <- exp(-k[1] * t); C[, 2] <- exp(-k[2] * t); "
            + "E <- matrix(nrow = 51, ncol = 2); wavenum <- seq(18000, 28000, by = 200); "
            + "location <- c(25000, 20000); delta <- c(5000, 7000); amp <- c(1, 2); "
            + "E[, 1] <- amp[1] * exp(-log(2) * (2 * (wavenum - location[1])/delta[1])^2); "
            + "E[, 2] <- amp[2] * exp(-log(2) * (2 * (wavenum - location[2])/delta[2])^2); "
            + "psi <- C %*% t(E) + .001 * rnorm(nrow(C) * nrow(E)); "
            + "d <- dat(psi.df = psi, x = t, nt = length(t), x2 = wavenum, nl = length(wavenum)); "
            + "m <- initModel(mod_type = 'kin', seqmod = FALSE, kinpar = c(.1, 2)); "
            + "invisible(capture.output(f <- fitModel(data = list(d), modspec = list(m), opt = kinopt(iter = 50, plot = FALSE)))); "
            + "f$currTheta[[1]]@kinpar";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void installsRuntimeAndFitsWithTimp() throws Exception {
        String platform = Micromamba.condaPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
        assumeNotNull(platform);
        RRuntime runtime = new RRuntime(temp.getRoot().toPath().resolve("r-runtime"), platform);

        StringWriter installOutput = new StringWriter();
        ProgressHandle progress = ProgressHandle.createHandle("Installing R");
        progress.start();
        try {
            runtime.ensureReady(progress, new PrintWriter(installOutput, true));
        } finally {
            progress.finish();
            System.out.println("----- install output -----");
            System.out.println(installOutput);
        }
        assertEquals(RRuntime.State.READY, runtime.getState());
        Properties versions = runtime.getInstalledVersions();
        assertEquals("4.4.3", versions.getProperty("r.version"));
        assertEquals("1.13.6", versions.getProperty("timp.version"));
        assertEquals(Micromamba.downloadTarget(platform, runtime.getDirectory()), runtime.findMicromamba());

        StringWriter rOutput = new StringWriter();
        RserveProcess rserve = new RserveProcess(runtime, new PrintWriter(rOutput, true));
        int port = 0;
        int pid = 0;
        try {
            port = rserve.ensureStarted();
            assertEquals("a running Rserve is reused", port, rserve.ensureStarted());
            // Two connections in a row: Rserve on Windows serves one connection at a time
            for (int i = 0; i < 2; i++) {
                RConnection connection = new RConnection("127.0.0.1", port);
                try {
                    String[] libraries = connection.eval(".libPaths()").asStrings();
                    assertEquals("only the environment's library: " + String.join(", ", libraries), 1, libraries.length);
                    assertTrue(libraries[0], Path.of(libraries[0]).toRealPath().startsWith(runtime.getEnvironment().toRealPath()));
                    assertEquals(runtime.getNoUserLibrary().toString(), connection.eval("Sys.getenv('R_LIBS_USER')").asString());
                    double[] rates = connection.eval(FIT).asDoubles();
                    // Rates fitted by TIMP 1.13.6 with CRAN R 4.5.3 and 4.6.1 and with this runtime on Windows 11
                    assertArrayEquals(new double[]{0.501815030821413, 1.00102018140514}, rates, 1e-7);
                    pid = connection.eval("Sys.getpid()").asInteger();
                } finally {
                    connection.close();
                }
            }
        } finally {
            rserve.stop();
            System.out.println("----- R output -----");
            System.out.println(rOutput);
        }
        assertFalse(rserve.isRunning());
        assertFalse("port closed", RserveProcess.answers(port));
        final int rPid = pid;
        assertFalse("R process stopped", ProcessHandle.of(rPid).map(ProcessHandle::isAlive).orElse(false));
    }
}
