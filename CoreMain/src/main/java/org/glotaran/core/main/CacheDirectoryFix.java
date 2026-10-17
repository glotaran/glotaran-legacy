package org.glotaran.core.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.modules.OnStart;
import org.openide.modules.Places;

/**
 * Sets the cache directory for Linux and macOS installations whose {@code etc/glotaran.conf} has no
 * {@code default_cachedir} (Glotaran 1.6.0). Their launcher passes an empty {@code --cachedir}, so NetBeans puts its
 * cache in the working directory, which is {@code /} when Glotaran is started from Finder or a desktop menu. The update
 * center cannot replace {@code etc/glotaran.conf}, but the launcher also reads {@code <userdir>/etc/glotaran.conf}
 * after it has set {@code cachedir}, so a {@code cachedir} line there takes effect from the next start.
 */
@OnStart
public final class CacheDirectoryFix implements Runnable {

    private static final Logger LOG = Logger.getLogger(CacheDirectoryFix.class.getName());
    static final String COMMENT = "# Added by Glotaran: this installation's etc/glotaran.conf sets no default_cachedir";

    @Override
    public void run() {
        String netbeansHome = System.getProperty("netbeans.home");
        File userDirectory = Places.getUserDirectory();
        if (System.getProperty("os.name", "").startsWith("Windows") || netbeansHome == null || userDirectory == null) {
            return;
        }
        Path installation = Paths.get(netbeansHome).toAbsolutePath().getParent();
        if (installation == null) {
            return;
        }
        Path installationConf = installation.resolve("etc").resolve("glotaran.conf");
        Path userConf = userDirectory.toPath().resolve("etc").resolve("glotaran.conf");
        try {
            if (!Files.isRegularFile(installationConf)) {
                return;
            }
            List<String> userLines = Files.isRegularFile(userConf) ? Files.readAllLines(userConf) : List.of();
            String append = linesToAppend(Files.readAllLines(installationConf), userLines, userDirectory.getName());
            if (append == null) {
                return;
            }
            Files.createDirectories(userConf.getParent());
            Files.writeString(userConf, append, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOG.log(Level.INFO, "Set the cache directory in {0}; it applies from the next start", userConf);
        } catch (IOException | RuntimeException e) {
            LOG.log(Level.INFO, "Could not set the cache directory in " + userConf, e);
        }
    }

    /**
     * @param installationConf lines of the installation's {@code etc/glotaran.conf}
     * @param userConf lines of {@code <userdir>/etc/glotaran.conf}, empty if it does not exist
     * @param version name of the user directory, the release line ({@code 1.6})
     * @return the text to append to the user configuration, or {@code null} if the cache directory is already set
     */
    static String linesToAppend(List<String> installationConf, List<String> userConf, String version) {
        if (sets(installationConf, "default_cachedir") || sets(userConf, "cachedir")) {
            return null;
        }
        String separator = userConf.isEmpty() ? "" : "\n";
        return separator + COMMENT + "\ncachedir=\"${DEFAULT_CACHEDIR_ROOT}/" + version + "\"\n";
    }

    private static boolean sets(List<String> lines, String variable) {
        return lines.stream().map(String::trim).anyMatch(line -> line.startsWith(variable + "="));
    }
}
