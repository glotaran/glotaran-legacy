package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class RserveProcessTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void freePortCanBeBoundOnLoopback() throws IOException {
        int port = RserveProcess.freePort();
        assertTrue(port > 0 && port < 65536);
        try (ServerSocket socket = new ServerSocket(port, 1, InetAddress.getLoopbackAddress())) {
            assertEquals(port, socket.getLocalPort());
        }
    }

    @Test
    public void startsRserveForLocalConnectionsOnly() throws IOException {
        RRuntime runtime = new RRuntime(temp.getRoot().toPath(), "linux-64");
        Path micromamba = Path.of("micromamba");
        List<String> command = new RserveProcess(runtime, new PrintWriter(new StringWriter())).command(micromamba, 40123);
        assertEquals(runtime.runCommand(micromamba, "Rscript", "--vanilla", "-e",
                "Rserve::run.Rserve(port=40123L, remote=FALSE, auth=FALSE)"), command);
    }

    @Test
    public void recognisesRserveIdString() throws Exception {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            Thread rserve = new Thread(() -> {
                try (Socket client = server.accept()) {
                    client.getOutputStream().write("Rsrv0103QAP1\r\n\r\n--------------\r\n".getBytes(StandardCharsets.US_ASCII));
                } catch (IOException e) {
                    // The test fails on the assertion below.
                }
            });
            rserve.start();
            assertTrue(RserveProcess.answers(server.getLocalPort()));
            rserve.join(5000);
        }
        assertFalse(RserveProcess.answers(RserveProcess.freePort()));
    }

    @Test
    public void doesNotStartWithoutInstalledRuntime() {
        RserveProcess rserve = new RserveProcess(new RRuntime(temp.getRoot().toPath(), "win-64"), new PrintWriter(new StringWriter()));
        IOException e = assertThrows(IOException.class, rserve::ensureStarted);
        assertTrue(e.getMessage(), e.getMessage().contains("not installed"));
        assertFalse(rserve.isRunning());
    }
}
