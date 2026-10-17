package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Looks for an R the user installed, to offer it on the first analysis. */
final class ExistingR {

    static final int RSERVE_PORT = 6311;
    private static final List<String> REGISTRY_KEYS = List.of("HKLM\\Software\\R-core\\R", "HKCU\\Software\\R-core\\R");
    /** The locations RserveProcessHelper.checkLocalRserve() looks at. */
    private static final List<String> UNIX_R = List.of("/Library/Frameworks/R.framework/Resources/bin/R",
            "/usr/local/lib/R/bin/R", "/usr/lib/R/bin/R", "/usr/local/bin/R", "/sw/bin/R", "/usr/common/bin/R",
            "/usr/local/lib64/R/bin/R", "/usr/lib64/R/bin/R");

    private ExistingR() {
    }

    /** @return a sentence that names what was found, or {@code null} if there is no R */
    static String find() {
        if (RserveProcess.answers(RSERVE_PORT)) {
            return "An Rserve is running on localhost:" + RSERVE_PORT + ".";
        }
        if (System.getProperty("os.name", "").startsWith("Windows")) {
            for (String key : REGISTRY_KEYS) {
                String path = queryInstallPath(key);
                if (path != null) {
                    return "R is installed in " + path + ".";
                }
            }
            return null;
        }
        for (String path : UNIX_R) {
            if (Files.isExecutable(Paths.get(path))) {
                return "R is installed at " + path + ".";
            }
        }
        return null;
    }

    private static String queryInstallPath(String key) {
        try {
            Process process = new ProcessBuilder("reg", "query", key, "/v", "InstallPath").redirectErrorStream(true).start();
            String output;
            try (InputStream in = process.getInputStream()) {
                output = new String(in.readAllBytes(), ProcessTree.outputCharset());
            }
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return null;
            }
            return process.exitValue() == 0 ? parseInstallPath(output) : null;
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /** @return the value of {@code InstallPath} in the output of {@code reg query}, or {@code null} */
    static String parseInstallPath(String regOutput) {
        for (String line : regOutput.split("\\R")) {
            String[] fields = line.trim().split("\\s+REG_\\w+\\s+", 2);
            if (fields.length == 2 && fields[0].equals("InstallPath") && !fields[1].isBlank()) {
                return fields[1].trim();
            }
        }
        return null;
    }
}
