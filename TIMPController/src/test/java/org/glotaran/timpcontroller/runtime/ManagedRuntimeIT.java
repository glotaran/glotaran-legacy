package org.glotaran.timpcontroller.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.rosuda.REngine.Rserve.RConnection;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

/**
 * Installs the bundled R runtime of this platform with micromamba, starts Rserve and connects with Glotaran's Java
 * client. It downloads micromamba and about 300 MB of packages, so it is not a unit test (Surefire runs only *Test
 * classes by default); run it with {@code -DskipTests=false -Dtest=ManagedRuntimeIT}.
 *
 * <p>
 * The bundled runtime has no TIMP until r-timp is on conda-forge, and the check in {@link RRuntime#ensureReady}
 * requires TIMP. Until then this test runs the installation steps itself and checks R and Rserve only. Once r-timp is
 * in the lock, replace them with {@code ensureReady()} and run a TIMP fit over the connection.
 */
public class ManagedRuntimeIT {

    private static final String VERSIONS = "cat('R=', as.character(getRversion()), "
            + "'\\nRserve=', as.character(packageVersion('Rserve')), '\\n', sep='')";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void installsRuntimeAndServesR() throws Exception {
        String platform = Micromamba.condaPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
        assumeNotNull(platform);
        RRuntime runtime = new RRuntime(temp.getRoot().toPath().resolve("r-runtime"), platform);

        Path micromamba = Micromamba.download(platform, runtime.getDirectory(), () -> false);
        Path spec = Files.write(runtime.getDirectory().resolve("spec.txt"), runtime.readSpec());
        run(runtime, runtime.createCommand(micromamba, spec));
        Map<String, String> versions = RRuntime.parseVersions(
                run(runtime, runtime.runCommand(micromamba, "Rscript", "--vanilla", "-e", VERSIONS)));
        assertEquals("4.4.3", versions.get("R"));
        assertNotNull("Rserve version", versions.get("Rserve"));
        writeMarker(runtime);
        assertEquals(RRuntime.State.READY, runtime.getState());

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
                    assertEquals(versions.get("R"), connection.eval("as.character(getRversion())").asString());
                    String[] libraries = connection.eval(".libPaths()").asStrings();
                    assertEquals("only the environment's library: " + String.join(", ", libraries), 1, libraries.length);
                    assertTrue(libraries[0], Path.of(libraries[0]).toRealPath().startsWith(runtime.getEnvironment().toRealPath()));
                    assertEquals(runtime.getNoUserLibrary().toString(), connection.eval("Sys.getenv('R_LIBS_USER')").asString());
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

    /** Runs a command like RRuntime does and returns its output lines. */
    private static List<String> run(RRuntime runtime, List<String> command) throws IOException, InterruptedException {
        System.out.println("> " + String.join(" ", command));
        ProcessBuilder builder = new ProcessBuilder(command).directory(runtime.getDirectory().toFile()).redirectErrorStream(true);
        runtime.configureEnvironment(builder.environment());
        Process process = builder.start();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), ProcessTree.outputCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lines.add(line);
            }
        }
        assertEquals(command.get(1) + " exit code", 0, process.waitFor());
        return lines;
    }

    /** The marker RRuntime writes after its check, which also needs TIMP. */
    private static void writeMarker(RRuntime runtime) throws IOException {
        Properties marker = new Properties();
        marker.setProperty("runtime.id", runtime.getRuntimeId());
        try (Writer writer = Files.newBufferedWriter(runtime.getMarker(), StandardCharsets.UTF_8)) {
            marker.store(writer, null);
        }
    }
}
