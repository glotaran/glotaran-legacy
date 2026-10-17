package org.glotaran.timpcontroller.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class RRuntimeTest {

    private static final String[] PLATFORMS = {"win-64", "linux-64", "osx-64", "osx-arm64"};

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    private Path dir;
    private RRuntime runtime;

    @Before
    public void setUp() throws IOException {
        dir = temp.newFolder("r-runtime").toPath();
        runtime = new RRuntime(dir, "win-64");
    }

    @Test
    public void bundlesExplicitFileForEveryPlatform() throws Exception {
        for (String platform : PLATFORMS) {
            String spec = new String(new RRuntime(dir, platform).readSpec(), StandardCharsets.UTF_8);
            assertTrue(platform, spec.contains("# platform: " + platform + "\n@EXPLICIT\n"));
            assertTrue(platform, spec.contains("/r-base-4.4.3-"));
            assertTrue(platform, spec.contains("/r-rserve-"));
            assertFalse(platform, spec.contains("\r"));
        }
    }

    @Test
    public void runtimeIdIsSha256OfExplicitFile() throws Exception {
        byte[] spec;
        try (InputStream in = RRuntime.class.getResourceAsStream("win-64.txt")) {
            spec = in.readAllBytes();
        }
        String expected = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(spec));
        assertEquals(expected, runtime.getRuntimeId());
        assertEquals(dir.resolve("env-" + expected.substring(0, 12)), runtime.getEnvironment());
        assertNotEquals(expected, new RRuntime(dir, "linux-64").getRuntimeId());
    }

    @Test
    public void specPropertyReplacesBundledFile() throws Exception {
        Path spec = Files.writeString(temp.newFile("dev.txt").toPath(), "@EXPLICIT\nfile:///C:/build/r-timp.conda#00\n");
        System.setProperty(RRuntime.SPEC_PROPERTY, spec.toString());
        try {
            String expected = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(spec)));
            assertEquals(expected, new RRuntime(dir, "win-64").getRuntimeId());
        } finally {
            System.clearProperty(RRuntime.SPEC_PROPERTY);
        }
    }

    @Test
    public void buildsMicromambaCommands() throws IOException {
        Path micromamba = Path.of("mm", "micromamba.exe");
        Path spec = dir.resolve("spec.txt");
        String root = dir.resolve("root").toString();
        String env = runtime.getEnvironment().toString();
        assertEquals(List.of(micromamba.toString(), "create", "-y", "--no-rc", "-r", root, "-p", env, "-f", spec.toString()),
                runtime.createCommand(micromamba, spec));
        assertEquals(List.of(micromamba.toString(), "run", "-r", root, "-p", env, "Rscript", "--vanilla", "-e", "1"),
                runtime.runCommand(micromamba, "Rscript", "--vanilla", "-e", "1"));
        assertFalse("cmd.exe would end the argument", RRuntime.CHECK_EXPRESSION.contains("\""));
        assertFalse("cmd.exe would expand it", RRuntime.CHECK_EXPRESSION.contains("%"));
    }

    @Test
    public void runsREnvironmentProgramsDirectlyOnLinuxAndMacOS() throws IOException {
        for (String platform : new String[]{"linux-64", "osx-64", "osx-arm64"}) {
            RRuntime unix = new RRuntime(dir, platform);
            assertEquals(platform, List.of(unix.getEnvironment().resolve("bin").resolve("Rscript").toString(), "--vanilla", "-e", "1"),
                    unix.runCommand(Path.of("micromamba"), "Rscript", "--vanilla", "-e", "1"));
        }
    }

    @Test
    public void keepsRuntimeOutOfPathsWithSpacesOnLinuxAndMacOS() {
        Path home = Path.of("/Users/user");
        Path macUserDir = home.resolve("Library").resolve("Application Support").resolve("glotaran").resolve("1.7");
        assertEquals(home.resolve(".glotaran").resolve("1.7").resolve("r-runtime"),
                RRuntime.runtimeDirectory(macUserDir, home.toString(), "osx-arm64"));
        Path linuxUserDir = home.resolve(".glotaran").resolve("1.7");
        assertEquals(linuxUserDir.resolve("r-runtime"), RRuntime.runtimeDirectory(linuxUserDir, home.toString(), "linux-64"));
        Path windowsUserDir = Path.of("C:/Users/John Doe/.glotaran/1.7");
        assertEquals("spaces work on Windows", windowsUserDir.resolve("r-runtime"),
                RRuntime.runtimeDirectory(windowsUserDir, "C:/Users/John Doe", "win-64"));
    }

    @Test
    public void refusesInstallInPathWithSpacesOnLinuxAndMacOS() throws IOException {
        RRuntime runtime = new RRuntime(temp.newFolder("home with space").toPath().resolve("r-runtime"), "linux-64");
        IOException e = assertThrows(IOException.class, () -> runtime.ensureReady(null, null));
        assertTrue(e.getMessage(), e.getMessage().contains("must not contain spaces"));
    }

    @Test
    public void keepsOtherRLibrariesOut() {
        Map<String, String> environment = new HashMap<>(Map.of("R_LIBS", "a", "R_LIBS_SITE", "b", "R_LIBS_USER", "c", "PATH", "p"));
        runtime.configureEnvironment(environment);
        assertEquals(Map.of("R_LIBS_USER", dir.resolve("no-user-library").toString(), "PATH", "p"), environment);
        assertFalse(Files.exists(dir.resolve("no-user-library")));
    }

    @Test
    public void readyOnlyWithMarkerOfThisRuntime() throws IOException {
        assertEquals(RRuntime.State.NOT_INSTALLED, runtime.getState());
        assertNull(runtime.getInstalledVersions());

        writeMarker("0123456789");
        assertEquals("marker of another lock", RRuntime.State.NOT_INSTALLED, runtime.getState());

        writeMarker(runtime.getRuntimeId());
        assertEquals(RRuntime.State.READY, runtime.getState());
        assertEquals("4.4.3", runtime.getInstalledVersions().getProperty("r.version"));
    }

    @Test
    public void parsesVersionsFromCheckOutput() {
        assertEquals(Map.of("R", "4.4.3", "Rserve", "1.8.14", "TIMP", "1.13.6"),
                RRuntime.parseVersions(List.of("noise", "R=4.4.3", "Rserve=1.8.14\r", "TIMP=1.13.6", "R = x")));
    }

    @Test
    public void removeDeletesEnvironmentsRootAndWorkButKeepsLog() throws IOException {
        writeMarker(runtime.getRuntimeId());
        Files.createDirectories(dir.resolve("env-000000000000").resolve("lib"));
        Files.createDirectories(dir.resolve("root").resolve("pkgs"));
        Files.createDirectories(dir.resolve("work"));
        Path readOnly = Files.createFile(runtime.getEnvironment().resolve("read-only"));
        assertTrue(readOnly.toFile().setWritable(false));
        Files.writeString(runtime.getLog(), "log");

        runtime.remove();

        try (var left = Files.list(dir)) {
            assertEquals(List.of(runtime.getLog()), left.toList());
        }
        assertEquals(RRuntime.State.NOT_INSTALLED, runtime.getState());
    }

    @Test
    public void removeWithoutRuntimeDirectoryDoesNothing() throws IOException {
        new RRuntime(dir.resolve("missing"), "win-64").remove();
    }

    @Test
    public void unsupportedPlatformCannotInstall() {
        RRuntime unsupported = new RRuntime(dir, null);
        assertFalse(unsupported.isSupported());
        assertNull(unsupported.findMicromamba());
        assertNull(unsupported.getInstalledVersions());
        assertThrows(IOException.class, () -> unsupported.ensureReady(null, null));
    }

    private void writeMarker(String runtimeId) throws IOException {
        Path marker = runtime.getMarker();
        Files.createDirectories(marker.getParent());
        Properties properties = new Properties();
        properties.setProperty("runtime.id", runtimeId);
        properties.setProperty("r.version", "4.4.3");
        try (Writer writer = Files.newBufferedWriter(marker, StandardCharsets.UTF_8)) {
            properties.store(writer, null);
        }
    }
}
