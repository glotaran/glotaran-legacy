package org.glotaran.timpcontroller.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.modules.Places;

/**
 * The R runtime that Glotaran installs with micromamba from the locked conda explicit file of the platform, under
 * {@code <userdir>/r-runtime}:
 * <pre>
 * micromamba/        downloaded micromamba, when none is bundled
 * root/              micromamba root prefix; its package cache is deleted after the installation
 * env-&lt;id&gt;/          the environment; id is the start of the runtime ID
 * work/              working directory of Rserve
 * install.log        output of the last installation
 * </pre>
 */
public final class RRuntime {

    public enum State {
        NOT_INSTALLED, INSTALLING, READY, FAILED
    }

    /** Development override: path to a conda explicit file that replaces the bundled one. */
    static final String SPEC_PROPERTY = "glotaran.rruntime.spec";
    static final String MARKER = "glotaran-runtime.properties";
    private static final long MIN_FREE_SPACE = 2L * 1024 * 1024 * 1024;
    /** First download with an empty package cache, measured for the current lock. */
    private static final Map<String, Integer> DOWNLOAD_MB = Map.of("win-64", 264, "linux-64", 305, "osx-64", 272, "osx-arm64", 262);
    private static final Pattern VERSION_LINE = Pattern.compile("^(R|Rserve|TIMP)=(\\S+)$");
    /** Prints the versions in the format of {@link #VERSION_LINE}; single quotes because Windows passes the argument through cmd.exe. */
    static final String CHECK_EXPRESSION = "suppressPackageStartupMessages({library(Rserve); library(TIMP)}); "
            + "cat('R=', as.character(getRversion()), '\\nRserve=', as.character(packageVersion('Rserve')), "
            + "'\\nTIMP=', as.character(packageVersion('TIMP')), '\\n', sep='')";

    private static RRuntime defaultRuntime;

    private final Path directory;
    private final String platform;
    private final Object idLock = new Object();
    private String runtimeId;
    private volatile boolean installing;
    private volatile boolean cancelled;
    private volatile String failure;
    private volatile Process current;

    RRuntime(Path directory, String platform) {
        this.directory = directory;
        this.platform = platform;
    }

    /** The runtime for this machine's platform, see {@link #runtimeDirectory}. */
    public static synchronized RRuntime getDefault() {
        if (defaultRuntime == null) {
            String platform = Micromamba.condaPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
            defaultRuntime = new RRuntime(runtimeDirectory(Places.getUserDirectory().toPath(),
                    System.getProperty("user.home"), platform), platform);
        }
        return defaultRuntime;
    }

    /**
     * {@code <userdir>/r-runtime}, except on Linux and macOS when that path contains whitespace: conda-forge's R
     * start script ({@code lib/R/bin/R}) then fails, because its installation path is written into it unquoted. On
     * macOS the user directory is always under {@code Library/Application Support}, so there the runtime goes to
     * {@code ~/.glotaran/<release line>/r-runtime}, where the release line is the name of the user directory.
     */
    static Path runtimeDirectory(Path userDirectory, String userHome, String platform) {
        Path directory = userDirectory.resolve("r-runtime");
        if (platform == null || platform.startsWith("win-") || !containsWhitespace(directory)) {
            return directory;
        }
        return Paths.get(userHome, ".glotaran", userDirectory.getFileName().toString(), "r-runtime");
    }

    private static boolean containsWhitespace(Path path) {
        return path.toAbsolutePath().toString().chars().anyMatch(Character::isWhitespace);
    }

    /** @return {@code false} when there is no managed runtime for this operating system and processor */
    public boolean isSupported() {
        return platform != null;
    }

    public String getPlatform() {
        return platform;
    }

    /** @return the approximate size of the first download in MB */
    public int getDownloadSize() {
        return DOWNLOAD_MB.getOrDefault(platform, 300);
    }

    public Path getDirectory() {
        return directory;
    }

    Path getRoot() {
        return directory.resolve("root");
    }

    Path getWorkDirectory() {
        return directory.resolve("work");
    }

    public Path getLog() {
        return directory.resolve("install.log");
    }

    /** A path that does not exist, so R loads no packages from the user's personal library. */
    Path getNoUserLibrary() {
        return directory.resolve("no-user-library");
    }

    public Path getEnvironment() throws IOException {
        return directory.resolve("env-" + getRuntimeId().substring(0, 12));
    }

    Path getMarker() throws IOException {
        return getEnvironment().resolve("conda-meta").resolve(MARKER);
    }

    /** The conda explicit file to install: the {@value #SPEC_PROPERTY} file if set, otherwise the bundled one. */
    byte[] readSpec() throws IOException {
        String override = System.getProperty(SPEC_PROPERTY);
        if (override != null && !override.isBlank()) {
            return Files.readAllBytes(Paths.get(override));
        }
        try (InputStream in = RRuntime.class.getResourceAsStream(platform + ".txt")) {
            if (in == null) {
                throw new IOException("No R runtime specification for " + platform);
            }
            return in.readAllBytes();
        }
    }

    /** SHA-256 of the explicit file; a new lock gives a new ID and a new environment. */
    public String getRuntimeId() throws IOException {
        synchronized (idLock) {
            if (runtimeId == null) {
                try {
                    runtimeId = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(readSpec()));
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException(e);
                }
            }
            return runtimeId;
        }
    }

    public State getState() {
        if (installing) {
            return State.INSTALLING;
        }
        if (isReady()) {
            return State.READY;
        }
        return failure != null ? State.FAILED : State.NOT_INSTALLED;
    }

    /** The message of the last failed installation in this session, or {@code null}. */
    public String getFailure() {
        return failure;
    }

    /** @return the marker of the installed environment (runtime ID and R, Rserve and TIMP versions), or {@code null} */
    public Properties getInstalledVersions() {
        if (!isSupported()) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(getMarker(), StandardCharsets.UTF_8)) {
            Properties marker = new Properties();
            marker.load(reader);
            return getRuntimeId().equals(marker.getProperty("runtime.id")) ? marker : null;
        } catch (IOException e) {
            return null;
        }
    }

    boolean isReady() {
        return getInstalledVersions() != null;
    }

    /** @return micromamba, or {@code null} if it is neither configured, bundled nor downloaded */
    public Path findMicromamba() {
        return isSupported() ? Micromamba.find(platform, directory) : null;
    }

    /**
     * Installs the runtime unless it is ready: downloads micromamba if needed, creates the environment, checks that R,
     * Rserve and TIMP load, and writes the marker. Call from a background thread; {@link #cancel()} stops it.
     *
     * @param progress a started handle that shows the current phase; create it cancellable with {@link #cancel()}
     * @param output receives the output of micromamba and R, which also goes to {@link #getLog()}
     * @throws IOException if a step fails; the message names the step
     * @throws CancellationException if {@link #cancel()} was called
     */
    public synchronized void ensureReady(ProgressHandle progress, PrintWriter output) throws IOException {
        if (!isSupported()) {
            throw new IOException("Glotaran cannot install R on " + System.getProperty("os.name") + " ("
                    + System.getProperty("os.arch") + "). Use your own R installation.");
        }
        if (!platform.startsWith("win-") && containsWhitespace(directory)) {
            throw new IOException("Glotaran cannot install R in " + directory + ": on Linux and macOS the path must "
                    + "not contain spaces. Use your own R installation.");
        }
        Path micromamba = findMicromamba();
        if (micromamba != null && isReady()) {
            return;
        }
        installing = true;
        cancelled = false;
        failure = null;
        try {
            Files.createDirectories(directory);
            prepare(micromamba, progress, output);
        } finally {
            installing = false;
        }
    }

    private void prepare(Path micromamba, ProgressHandle progress, PrintWriter output) throws IOException {
        try (PrintWriter out = new PrintWriter(new TeeWriter(Files.newBufferedWriter(getLog(), StandardCharsets.UTF_8), output), true)) {
            try {
                if (!isReady()) {
                    checkFreeSpace();
                }
                if (micromamba == null) {
                    progress.progress("Downloading micromamba");
                    out.println("Downloading " + Micromamba.downloadUrl(platform));
                    micromamba = Micromamba.download(platform, directory, () -> cancelled);
                }
                if (!isReady()) {
                    install(micromamba, progress, out);
                }
            } catch (IOException | RuntimeException e) {
                stopCurrent();
                if (!isReady()) {
                    deleteTree(getEnvironment());
                }
                String message = cancelled ? "Installation of R cancelled" : e.getMessage();
                out.println(message);
                failure = cancelled ? null : message + " (log: " + getLog() + ")";
                if (cancelled) {
                    throw new CancellationException(message);
                }
                throw new IOException(failure, e);
            }
        }
    }

    private void install(Path micromamba, ProgressHandle progress, PrintWriter out) throws IOException {
        Path env = getEnvironment();
        deleteTree(env);
        Path spec = directory.resolve("spec.txt");
        Files.write(spec, readSpec());
        progress.progress("Downloading and installing R (about " + getDownloadSize() + " MB)");
        run(createCommand(micromamba, spec), out, "Installing R");
        progress.progress("Checking R");
        List<String> lines = run(runCommand(micromamba, "Rscript", "--vanilla", "-e", CHECK_EXPRESSION), out, "Checking R");
        Map<String, String> versions = parseVersions(lines);
        if (versions.size() < 3) {
            throw new IOException("Checking R failed: R did not report the versions of R, Rserve and TIMP");
        }
        Properties marker = new Properties();
        marker.setProperty("runtime.id", getRuntimeId());
        marker.setProperty("platform", platform);
        marker.setProperty("r.version", versions.get("R"));
        marker.setProperty("rserve.version", versions.get("Rserve"));
        marker.setProperty("timp.version", versions.get("TIMP"));
        try (Writer writer = Files.newBufferedWriter(getMarker(), StandardCharsets.UTF_8)) {
            marker.store(writer, "Written by Glotaran after a successful installation");
        }
        out.println("Installed R " + versions.get("R") + ", Rserve " + versions.get("Rserve") + ", TIMP " + versions.get("TIMP"));
        // The environment is complete; failing to free space must not fail the installation.
        try {
            deleteTree(getRoot().resolve("pkgs"));
            Files.deleteIfExists(spec);
            try (DirectoryStream<Path> others = Files.newDirectoryStream(directory, "env-*")) {
                for (Path other : others) {
                    if (!other.equals(env)) {
                        deleteTree(other);
                    }
                }
            }
        } catch (IOException e) {
            out.println("Could not remove old files: " + e.getMessage());
        }
    }

    /** Stops a running installation. */
    public void cancel() {
        cancelled = true;
        stopCurrent();
    }

    /** Deletes the environments, the micromamba root and the working directory. Stop Rserve first. */
    public synchronized void remove() throws IOException {
        try (DirectoryStream<Path> envs = Files.newDirectoryStream(directory, "env-*")) {
            for (Path env : envs) {
                deleteTree(env);
            }
        } catch (NoSuchFileException e) {
            return;
        }
        deleteTree(getRoot());
        deleteTree(getWorkDirectory());
        failure = null;
    }

    List<String> createCommand(Path micromamba, Path spec) throws IOException {
        // --no-rc keeps the user's own conda configuration out; only "create" accepts it.
        return List.of(micromamba.toString(), "create", "-y", "--no-rc", "-r", getRoot().toString(),
                "-p", getEnvironment().toString(), "-f", spec.toString());
    }

    /**
     * Runs a program of the environment. On Windows through {@code micromamba run}, which activates the environment;
     * conda R on Windows does not start without its DLL folders on {@code PATH}. On Linux and macOS the program in
     * {@code <env>/bin} is started directly: R finds its libraries without activation, and activation would run the
     * activation scripts of the compiler packages that r-base depends on, which call {@code xcrun} on macOS and make
     * macOS ask users without the Command Line Tools to install them.
     */
    List<String> runCommand(Path micromamba, String... command) throws IOException {
        List<String> line = new ArrayList<>();
        if (platform.startsWith("win-")) {
            line.addAll(List.of(micromamba.toString(), "run", "-r", getRoot().toString(), "-p", getEnvironment().toString(),
                    command[0]));
        } else {
            line.add(getEnvironment().resolve("bin").resolve(command[0]).toString());
        }
        line.addAll(List.of(command).subList(1, command.length));
        return line;
    }

    /** Keeps R libraries outside the environment out of the managed R. */
    void configureEnvironment(Map<String, String> environment) {
        environment.remove("R_LIBS");
        environment.remove("R_LIBS_SITE");
        environment.put("R_LIBS_USER", getNoUserLibrary().toString());
    }

    static Map<String, String> parseVersions(List<String> lines) {
        Map<String, String> versions = new HashMap<>();
        for (String line : lines) {
            Matcher matcher = VERSION_LINE.matcher(line.trim());
            if (matcher.matches()) {
                versions.put(matcher.group(1), matcher.group(2));
            }
        }
        return versions;
    }

    /** Runs a command to completion, copying its output to {@code out}, and returns the output lines. */
    private List<String> run(List<String> command, PrintWriter out, String step) throws IOException {
        if (cancelled) {
            throw new CancellationException();
        }
        out.println("> " + String.join(" ", command));
        ProcessBuilder builder = new ProcessBuilder(command).directory(directory.toFile()).redirectErrorStream(true);
        configureEnvironment(builder.environment());
        Process process = builder.start();
        current = process;
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), ProcessTree.outputCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
                lines.add(line);
            }
            int exitCode = process.waitFor();
            if (cancelled) {
                throw new CancellationException();
            }
            if (exitCode != 0) {
                throw new IOException(step + " failed: " + command.get(1) + " exited with code " + exitCode);
            }
            return lines;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException(step + " interrupted");
        } finally {
            current = null;
        }
    }

    private void stopCurrent() {
        Process process = current;
        if (process != null) {
            ProcessTree.destroy(process);
        }
    }

    private void checkFreeSpace() throws IOException {
        long free = Files.getFileStore(directory).getUsableSpace();
        if (free < MIN_FREE_SPACE) {
            throw new IOException(String.format("Installing R needs 2 GB of free disk space on the drive of %s; %.1f GB is free",
                    directory, free / (1024.0 * 1024 * 1024)));
        }
    }

    static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(root)) {
            paths = walk.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (AccessDeniedException e) {
                // Read-only files cannot be deleted on Windows.
                if (!path.toFile().setWritable(true)) {
                    throw e;
                }
                Files.deleteIfExists(path);
            }
        }
    }

    /** Writes to the log file and to the output window. */
    private static final class TeeWriter extends Writer {

        private final Writer log;
        private final Writer output;

        TeeWriter(Writer log, Writer output) {
            this.log = log;
            this.output = output;
        }

        @Override
        public void write(char[] buffer, int offset, int length) throws IOException {
            log.write(buffer, offset, length);
            output.write(buffer, offset, length);
        }

        @Override
        public void flush() throws IOException {
            log.flush();
            output.flush();
        }

        @Override
        public void close() throws IOException {
            log.close();
            output.flush();
        }
    }
}
