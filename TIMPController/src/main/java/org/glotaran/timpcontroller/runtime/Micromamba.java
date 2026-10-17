package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Finds the micromamba executable that installs and runs the managed R runtime, and downloads a pinned version when
 * none is bundled.
 */
final class Micromamba {

    /** Development override: path to a micromamba executable. */
    static final String PROPERTY = "glotaran.micromamba";
    static final String VERSION = "2.9.0-0";
    private static final Map<String, String> SHA256 = Map.of(
            "win-64", "a6d804394b2418991c4e29562853eaace2f2ce9d9da661a98e74e02e8dbb44b0",
            "linux-64", "366cd9cd8be14df1ab8ed50352a82111082a36686b2d389fdb79a92c3fafb3e3",
            "osx-64", "1e71054bb3ac9a076e21f7ec48acfef536f9b3f1408f371a942784bf5ef83d8a",
            "osx-arm64", "ec2a072f028e1a7cf20f3e2e74d5a8127cf5a5f27636375b5359811565f4e5be");

    private Micromamba() {
    }

    /**
     * @return the conda platform for the given {@code os.name} and {@code os.arch}, or {@code null} when there is no
     * managed runtime for it
     */
    static String condaPlatform(String osName, String osArch) {
        String os = osName.toLowerCase(Locale.ROOT);
        String arch = osArch.toLowerCase(Locale.ROOT);
        boolean x64 = arch.equals("amd64") || arch.equals("x86_64");
        if (os.startsWith("windows") && x64) {
            return "win-64";
        }
        if (os.startsWith("linux") && x64) {
            return "linux-64";
        }
        if (os.startsWith("mac") && x64) {
            return "osx-64";
        }
        if (os.startsWith("mac") && (arch.equals("aarch64") || arch.equals("arm64"))) {
            return "osx-arm64";
        }
        return null;
    }

    static String executableName(String platform) {
        return platform.startsWith("win-") ? "micromamba.exe" : "micromamba";
    }

    /** Where {@link #download} puts micromamba. */
    static Path downloadTarget(String platform, Path runtimeDir) {
        return runtimeDir.resolve("micromamba").resolve(executableName(platform));
    }

    static String downloadUrl(String platform) {
        return "https://github.com/mamba-org/micromamba-releases/releases/download/" + VERSION + "/micromamba-" + platform;
    }

    static String sha256(String platform) {
        return SHA256.get(platform);
    }

    /** Looks for micromamba with the system properties of this JVM. */
    static Path find(String platform, Path runtimeDir) {
        return find(platform, System.getProperty(PROPERTY), System.getProperty("netbeans.home"), runtimeDir);
    }

    /**
     * Lookup order: the {@value #PROPERTY} property (if the file exists), {@code <installation>/micromamba/} next to the platform cluster
     * ({@code netbeans.home}) as bundled by the installers, and the download in the runtime directory.
     *
     * @return the executable, or {@code null} if none is found
     */
    static Path find(String platform, String property, String netbeansHome, Path runtimeDir) {
        if (property != null && !property.isBlank() && Files.isRegularFile(Paths.get(property))) {
            return Paths.get(property);
        }
        if (netbeansHome != null) {
            Path installation = Paths.get(netbeansHome).toAbsolutePath().getParent();
            if (installation != null) {
                Path bundled = installation.resolve("micromamba").resolve(executableName(platform));
                if (Files.isRegularFile(bundled)) {
                    return bundled;
                }
            }
        }
        Path downloaded = downloadTarget(platform, runtimeDir);
        return Files.isRegularFile(downloaded) ? downloaded : null;
    }

    /**
     * Downloads the pinned micromamba for the platform to {@link #downloadTarget} and checks its SHA-256.
     */
    static Path download(String platform, Path runtimeDir, BooleanSupplier cancelled) throws IOException {
        Path target = downloadTarget(platform, runtimeDir);
        Path part = target.resolveSibling(target.getFileName() + ".part");
        Files.createDirectories(target.getParent());
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl(platform)).openConnection();
        connection.setConnectTimeout(30_000);
        connection.setReadTimeout(60_000);
        try {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Downloading " + downloadUrl(platform) + " failed: HTTP " + status);
            }
            try (InputStream in = connection.getInputStream();
                    OutputStream out = new DigestOutputStream(Files.newOutputStream(part), digest)) {
                byte[] buffer = new byte[64 * 1024];
                int n;
                while ((n = in.read(buffer)) >= 0) {
                    if (cancelled.getAsBoolean()) {
                        throw new InterruptedIOException("Download cancelled");
                    }
                    out.write(buffer, 0, n);
                }
            }
            String actual = HexFormat.of().formatHex(digest.digest());
            if (!actual.equals(sha256(platform))) {
                throw new IOException("Downloaded micromamba has SHA-256 " + actual + ", expected " + sha256(platform));
            }
            Files.move(part, target, StandardCopyOption.REPLACE_EXISTING);
            if (!target.toFile().setExecutable(true)) {
                throw new IOException("Cannot make " + target + " executable");
            }
            return target;
        } finally {
            connection.disconnect();
            Files.deleteIfExists(part);
        }
    }
}
