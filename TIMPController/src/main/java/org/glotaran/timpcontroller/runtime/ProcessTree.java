package org.glotaran.timpcontroller.runtime;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Helpers for the micromamba processes, which start R as a grandchild. */
final class ProcessTree {

    private ProcessTree() {
    }

    /**
     * Destroys the process and all its descendants ({@code micromamba} → {@code cmd.exe} → {@code Rscript} on
     * Windows), waiting up to 10 seconds before killing what is left.
     */
    static void destroy(Process process) {
        List<ProcessHandle> tree = process.descendants().toList();
        tree.forEach(ProcessHandle::destroy);
        process.destroy();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        for (ProcessHandle handle : tree) {
            waitFor(handle, deadline);
        }
        waitFor(process.toHandle(), deadline);
    }

    private static void waitFor(ProcessHandle handle, long deadline) {
        try {
            long remaining = deadline - System.nanoTime();
            if (remaining > 0) {
                handle.onExit().get(remaining, TimeUnit.NANOSECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Timed out; killed below.
        }
        if (handle.isAlive()) {
            handle.destroyForcibly();
        }
    }

    /** Encoding of the output of native programs (the ANSI code page on Windows). */
    static Charset outputCharset() {
        String name = System.getProperty("native.encoding");
        return name != null && Charset.isSupported(name) ? Charset.forName(name) : Charset.defaultCharset();
    }
}
