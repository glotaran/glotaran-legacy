package org.glotaran.timpcontroller.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Rserve from the managed {@link RRuntime}, started on a free port on the loopback interface. One per Glotaran
 * session.
 */
public final class RserveProcess {

    private static final long START_TIMEOUT_MS = 60_000;

    private final RRuntime runtime;
    private final PrintWriter output;
    private Process process;
    private int port;

    /**
     * @param output receives the output of R
     */
    public RserveProcess(RRuntime runtime, PrintWriter output) {
        this.runtime = runtime;
        this.output = output;
    }

    /**
     * Starts Rserve unless it is running. The runtime must be ready ({@link RRuntime#ensureReady}).
     *
     * @return the port Rserve listens on, on {@link InetAddress#getLoopbackAddress()}
     */
    public synchronized int ensureStarted() throws IOException {
        if (isRunning()) {
            return port;
        }
        Path micromamba = runtime.findMicromamba();
        if (micromamba == null || !runtime.isReady()) {
            throw new IOException("Starting R failed: R for Glotaran is not installed");
        }
        IOException failure = null;
        // A port chosen by freePort() can be taken before Rserve binds it; try a second one.
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                start(micromamba, freePort());
                return port;
            } catch (IOException e) {
                stop();
                if (failure == null) {
                    failure = e;
                }
            }
        }
        throw failure;
    }

    public synchronized boolean isRunning() {
        return process != null && process.isAlive();
    }

    /** @return the port of the running Rserve */
    public synchronized int getPort() {
        return port;
    }

    /** Stops Rserve and the processes micromamba started for it. */
    public synchronized void stop() {
        if (process != null) {
            ProcessTree.destroy(process);
            process = null;
        }
    }

    private void start(Path micromamba, int newPort) throws IOException {
        Path work = runtime.getWorkDirectory();
        Files.createDirectories(work);
        List<String> command = command(micromamba, newPort);
        output.println("> " + String.join(" ", command));
        ProcessBuilder builder = new ProcessBuilder(command).directory(work.toFile()).redirectErrorStream(true);
        runtime.configureEnvironment(builder.environment());
        Process started = builder.start();
        process = started;
        port = newPort;
        Thread pump = new Thread(() -> copy(started.getInputStream()), "Rserve output");
        pump.setDaemon(true);
        pump.start();
        long deadline = System.currentTimeMillis() + START_TIMEOUT_MS;
        while (!answers(newPort)) {
            if (!process.isAlive()) {
                throw new IOException("Starting R failed: Rserve exited with code " + process.exitValue());
            }
            if (System.currentTimeMillis() > deadline) {
                throw new IOException("Starting R failed: Rserve did not answer on port " + newPort + " within 60 s");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Starting R interrupted", e);
            }
        }
    }

    private void copy(InputStream in) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, ProcessTree.outputCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.println(line);
                output.flush();
            }
        } catch (IOException e) {
            // The process ended.
        }
    }

    List<String> command(Path micromamba, int port) throws IOException {
        return runtime.runCommand(micromamba, "Rscript", "--vanilla", "-e", serveExpression(port));
    }

    static String serveExpression(int port) {
        return "Rserve::run.Rserve(port=" + port + "L, remote=FALSE, auth=FALSE)";
    }

    /** A port that is free on the loopback interface at the time of the call. */
    static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            return socket.getLocalPort();
        }
    }

    /** @return {@code true} if an Rserve on the loopback port sends its ID string ({@code Rsrv0103QAP1}) */
    static boolean answers(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 1000);
            socket.setSoTimeout(2000);
            String id = new String(socket.getInputStream().readNBytes(12), StandardCharsets.US_ASCII);
            return id.startsWith("Rsrv") && id.endsWith("QAP1");
        } catch (IOException e) {
            return false;
        }
    }
}
