package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class MicromambaTest {

    private static final String[] PLATFORMS = {"win-64", "linux-64", "osx-64", "osx-arm64"};

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void mapsOperatingSystemAndArchitectureToCondaPlatform() {
        assertEquals("win-64", Micromamba.condaPlatform("Windows 11", "amd64"));
        assertEquals("linux-64", Micromamba.condaPlatform("Linux", "amd64"));
        assertEquals("osx-64", Micromamba.condaPlatform("Mac OS X", "x86_64"));
        assertEquals("osx-arm64", Micromamba.condaPlatform("Mac OS X", "aarch64"));
        assertNull(Micromamba.condaPlatform("Linux", "aarch64"));
        assertNull(Micromamba.condaPlatform("Windows 11", "x86"));
        assertNull(Micromamba.condaPlatform("FreeBSD", "amd64"));
    }

    @Test
    public void executableHasExeSuffixOnlyOnWindows() {
        assertEquals("micromamba.exe", Micromamba.executableName("win-64"));
        assertEquals("micromamba", Micromamba.executableName("linux-64"));
        assertEquals("micromamba", Micromamba.executableName("osx-arm64"));
    }

    @Test
    public void pinsDownloadAndChecksumForEveryPlatform() {
        for (String platform : PLATFORMS) {
            assertEquals("https://github.com/mamba-org/micromamba-releases/releases/download/" + Micromamba.VERSION
                    + "/micromamba-" + platform, Micromamba.downloadUrl(platform));
            assertTrue(platform, Micromamba.sha256(platform).matches("[0-9a-f]{64}"));
        }
    }

    @Test
    public void findsPropertyThenBundledThenDownloaded() throws IOException {
        Path installation = temp.newFolder("glotaran").toPath();
        String netbeansHome = Files.createDirectory(installation.resolve("platform")).toString();
        Path runtime = temp.newFolder("r-runtime").toPath();
        assertNull(Micromamba.find("win-64", null, netbeansHome, runtime));

        Path downloaded = touch(Micromamba.downloadTarget("win-64", runtime));
        assertEquals(runtime.resolve("micromamba").resolve("micromamba.exe"), downloaded);
        assertEquals(downloaded, Micromamba.find("win-64", null, netbeansHome, runtime));

        Path bundled = touch(installation.resolve("micromamba").resolve("micromamba.exe"));
        assertEquals(bundled, Micromamba.find("win-64", null, netbeansHome, runtime));

        Path configured = touch(temp.getRoot().toPath().resolve("dev").resolve("micromamba.exe"));
        assertEquals(configured, Micromamba.find("win-64", configured.toString(), netbeansHome, runtime));
        assertEquals("a missing configured file falls through", bundled,
                Micromamba.find("win-64", configured.resolveSibling("missing.exe").toString(), netbeansHome, runtime));
    }

    private static Path touch(Path file) throws IOException {
        Files.createDirectories(file.getParent());
        return Files.createFile(file);
    }
}
