# R runtime provisioning for 1.7 — design

Status: draft for review, 2026-10-16. Decisions and evaluation results are in the 1.7 section of [ROADMAP.md](ROADMAP.md); this document describes how to build it.

## Goal, acceptance, non-goals

Goal: on the first analysis or simulation, Glotaran installs its own R 4.4.3 with TIMP and Rserve from conda-forge, starts Rserve itself and runs the analysis, without administrator rights and without the user running R.

Acceptance:

- On a clean Windows 11, macOS (arm64 and x64) and Linux x64 user account without R, the first analysis of the example project installs the runtime and completes. Results match 1.6.0 within the fit tolerance.
- A user with their own R and Rserve can keep using them (today's behaviour).
- Closing Glotaran stops the R process it started; no R process keeps running.
- R packages in the user's personal R library are never loaded by the managed R.

Non-goals:

- No change to `TimpControllerInterface` or its callers (`AnalysisWorker`, `SimulationWorker`), except that `AnalysisWorker` no longer shows `CoreErrorMessages.noRServeFoundException()` itself (decision 7).
- No R 4.5 or later (see ROADMAP.md).
- No offline installer that contains the R runtime.
- No configuration of a remote Rserve host in the GUI.
- No re-enabling of the registry-based launch of the user's own R in `RserveProcessHelper`; "own R" keeps the 1.6 behaviour (Glotaran connects to an Rserve the user started on `localhost:6311`).
- No code signing or notarization changes.

## Current code (relevant facts)

- `TIMPController/.../TimpController.java` is the `@ServiceProvider` for `TimpControllerInterface`. It connects in its constructor (at first lookup) with `new REngineConnectionFactory().createTwoWayConnection(null)`, which uses the default host and port (`localhost:6311`). `verifyRserve()` checks the connection, TIMP ≥ 1.12.1 and loads TIMP; on problems it writes to the "Rserve" output tab and tells the user to run `install.packages("TIMP")`.
- `AConnectionFactory.createTwoWayConnection(Properties)` already reads `IConnectionFactory.HOST` and `PORT` from the properties, so connecting to another port needs no change in JRConnect.
- `AnalysisAction` runs `AnalysisWorker`/`SimulationWorker` on a `RequestProcessor` (background thread) with a `ProgressHandle`. `AnalysisWorker` calls `isConnected()` and shows `CoreErrorMessages.noRServeFoundException()` when it returns false; `SimulationWorker` calls `runSimulation()` directly.
- `TIMPController/.../Installer.java` (`ModuleInstall`) has `restored()` with the old auto-launch commented out and a `close()` hook.
- `CoreOptions` registers a top-level "Options" category (`@OptionsPanelController.ContainerRegistration(id = "Options")`) with one sub-panel (colour scale) that stores its values with `NbPreferences`.
- The user directory is `~/.glotaran/1.7` (`~/Library/Application Support/glotaran/1.7` on macOS), set in `application/src/main/resources/glotaran.conf`.
- Installers are built by `.github/workflows/release.yml` with nbpackage from the generic zip plus a Temurin JRE per platform.

## Flow on the first analysis

1. `AnalysisWorker` (background thread) calls `TimpController.isConnected()`.
2. The controller reads the R mode from preferences. If no mode is set, it decides once, asking the user only when an existing R is found (see "Choosing the R installation").
3. Managed mode: `RRuntime.ensureReady()` installs the environment if needed, with a notification and its own cancellable progress bar; `RserveProcess.ensureStarted()` starts Rserve on a free local port; the controller connects to `127.0.0.1:<port>`.
4. Own-R mode: the controller connects to `localhost:6311` as in 1.6.
5. `verifyRserve()` runs as today and the analysis continues.

## Components

All new code goes into `TIMPController`, package `org.glotaran.timpcontroller.runtime`, unless noted.

### 1. Runtime lock

- A pixi workspace `TIMPController/src/main/r-runtime/pixi.toml` with `pixi.lock`, for `win-64`, `linux-64`, `osx-64`, `osx-arm64`:

  ```toml
  [dependencies]
  r-base = "4.4.3.*"
  libblas = { version = "*", build = "*netlib" }
  liblapack = { version = "*", build = "*netlib" }
  r-rserve = "*"
  r-timp = "1.13.6.*"   # once on conda-forge
  ```

  Note: `"r-minpack.lm"` and other names with a dot must be quoted in TOML. TIMP's dependencies come in through `r-timp`; until then they are listed explicitly.
- `pixi workspace export conda-explicit-spec` writes one explicit file per platform (`@EXPLICIT`, one URL with MD5 per line, 10–14 KB, 88–120 packages). These are committed as resources `org/glotaran/timpcontroller/runtime/<platform>.txt`. A small script in the same folder regenerates them; Maven does not need pixi.
- micromamba installs an explicit file without solving, so every user gets exactly the locked packages.
- The SHA-256 of the platform's explicit file is the runtime ID. A changed lock (new Glotaran build) means a new environment.
- `r-timp`: releases need it on conda-forge ([staged-recipes#35140](https://github.com/conda-forge/staged-recipes/pull/35140)). For development, the system property `glotaran.rruntime.spec=<path>` replaces the bundled explicit file, so a file with a `file:///` URL to a locally built `r-timp` package can be used.

### 2. Finding micromamba

In this order:

1. System property `glotaran.micromamba=<path>` (development, `-J-D...` in `glotaran.conf` or the run configuration).
2. Bundled with the installer: `<installation>/micromamba/micromamba[.exe]`, where `<installation>` is the parent of the `platform` cluster (`netbeans.home`). `release.yml` adds the pinned micromamba (verified against its published SHA-256) for the target platform to the generic zip before running nbpackage, and to the macOS bundle.
3. Not found (generic zip, update-center installations, `nbm:run-platform`): download the pinned micromamba version for the platform from `github.com/mamba-org/micromamba-releases` into `<runtime>/micromamba/`, and check it against a SHA-256 compiled into the code. This happens as the first step of the runtime installation (section 3), never on its own.

Platform mapping from `os.name`/`os.arch`: Windows + amd64 → `win-64`, Linux + amd64 → `linux-64`, Mac + aarch64 → `osx-arm64`, Mac + x86_64 → `osx-64`. Anything else: managed mode is unavailable and the user is told to use their own R.

### 3. `RRuntime` (install, check, remove)

Layout under the user directory (`<runtime>` = `<userdir>/r-runtime`):

```
<runtime>/
  micromamba/            downloaded micromamba (only in case 3 above)
  root/                  micromamba root prefix; package cache, emptied after install
  env-<runtime ID>/      the environment (about 1.2 GB on Windows); the folder name uses the first 12 characters of the ID
  work/                  working directory for Rserve
  install.log            output of the last install
```

States: `NOT_INSTALLED`, `INSTALLING`, `READY`, `FAILED` (with message). `READY` means `env-<runtime ID>/conda-meta/glotaran-runtime.properties` exists; that file is written last, after a successful check, and records the runtime ID and the R, TIMP and Rserve versions.

`ensureReady(ProgressHandle)` is synchronized and idempotent. It runs in managed mode when the runtime is not `READY` (first installation, or a new runtime ID after a Glotaran update), and from the Install button in the options panel:

1. If `READY`, return. Otherwise show a notification that R is being installed, with the download size (about 265 MB) and disk space (about 1.2 GB on Windows). Before downloading, check that the disk with the user directory has at least 2 GB free; otherwise stop with a message.
2. Create the environment: `micromamba create -y --no-rc -r <runtime>/root -p <env> -f <explicit file>`. `--no-rc` keeps the user's own conda configuration out; proxy environment variables still apply.
3. Check it: `micromamba run -r <root> -p <env> Rscript --vanilla -e "<print R, TIMP and Rserve versions>"` with the environment from section 4.
4. Write the marker; delete `<root>/pkgs` (the package cache, 279 MB in the Windows test; `micromamba clean` does not accept `-r`); remove environments with another runtime ID.
5. On failure or cancel: stop the process tree, delete the partial environment, state `FAILED`, keep `install.log`.

Output goes to an "R runtime" output tab and to `install.log`. The `ProgressHandle` is cancellable and shows the phase ("Downloading and installing R (about 265 MB)", "Checking R"). micromamba's output is not parsed for progress.

`remove()` deletes `env-*`, `root` and `work` (used by the options panel).

`micromamba create -p` also adds the environment to the user's `~/.conda/environments.txt`, so it appears in `conda env list` of users who have conda. micromamba 2.9 has no setting to turn this off; conda ignores entries whose folder no longer exists.

### 4. `RserveProcess`

- One per Glotaran session. `ensureStarted()` starts it if it is not running (or has died) and returns the port.
- Port: bind `new ServerSocket(0, 1, loopback)`, read the port, close the socket, pass the port to Rserve. If Rserve fails to start on it, retry once with a new port.
- Command: `micromamba run -r <root> -p <env> Rscript --vanilla -e "Rserve::run.Rserve(port=<port>L, remote=FALSE, auth=FALSE)"`, working directory `<runtime>/work`.
- Environment of the process: remove `R_LIBS` and `R_LIBS_SITE`, set `R_LIBS_USER=<runtime>/no-user-library` (a path that does not exist). `--vanilla` skips the user's `.Rprofile` and `.Renviron`.
- Readiness: connect to `127.0.0.1:<port>` until the 12-byte Rserve ID string (`Rsrv0103QAP1`) arrives, timeout 60 s.
- Output (stdout and stderr together) goes to the existing "Rserve" output tab.
- `stop()`: collect `process.descendants()`, destroy them and the process, wait up to 10 s, then `destroyForcibly()` for anything left. Called from `Installer.close()` and when switching modes.
- If Glotaran is killed or crashes, R keeps running until it is stopped by hand. Accepted for 1.7.

Rserve on Windows serves one connection at a time and shares one workspace between connections; the controller already uses a single connection and clears the workspace in `cleanup()`.

### 5. Choosing the R installation

Preference `rruntime.mode` (`NbPreferences.forModule(RRuntime.class)`): `MANAGED`, `OWN_R`, or unset.

When unset, at the first analysis or simulation:

- If an Rserve answers on `localhost:6311`, or an R installation is found (registry key `HKLM\Software\R-core\R` or `HKCU\Software\R-core\R`, or the Unix paths from `RserveProcessHelper.checkLocalRserve()`), show a dialog that names what was found and offers:
  - "Use my own R installation": keeps the 1.6 behaviour; the user starts Rserve on `localhost:6311`. Sets `OWN_R`.
  - "Download and install R for Glotaran": states the download size (about 265 MB) and disk space (about 1.2 GB on Windows). Sets `MANAGED` and starts the installation.
  - "Cancel": nothing is stored; the analysis stops, and the dialog appears again at the next analysis.
- Otherwise set `MANAGED` without a dialog. The installation shows a notification and a cancellable progress bar (section 3). Cancelling stops the analysis; the next analysis starts the installation again.

Later changes of mode are made in the options panel. After a Glotaran update with a new runtime ID, managed mode installs the new runtime the same way, with a notification and progress bar.

The dialog runs through `DialogDisplayer.notify()` from the worker thread.

### 6. Changes to `TimpController`

- Remove the connection attempt from the constructor.
- Add `ensureConnection()`: resolve the mode; in managed mode call `RRuntime.ensureReady()` and `RserveProcess.ensureStarted()`, then connect with `Properties` `HOST=127.0.0.1`, `PORT=<port>`; in own-R mode connect as today.
- `isConnected()`, `runAnalysis()` and `runSimulation()` call `ensureConnection()` (the simulation path does not call `isConnected()` today). `runSimulation()` also runs `verifyRserve()` like `runAnalysis()`, because a new managed Rserve has not loaded TIMP.
- `verifyRserve()` flushes the "Rserve" output writer instead of closing it, because the managed Rserve writes to it for the whole session.
- Managed mode: if TIMP is missing or too old, report a broken runtime (with "Reinstall in Options") instead of the `install.packages("TIMP")` advice, which stays for own-R mode.
- The controller shows all connection messages. Managed-mode failures show a message that names the step that failed and the log file; a cancelled installation shows a status-bar message. In own-R mode the controller calls `CoreErrorMessages.noRServeFoundException()`, which `AnalysisWorker` used to call when `isConnected()` returned false.

`Installer.close()` calls `RserveProcess.stop()`. `Installer.restored()` does not start R, so start-up time is unchanged.

### 7. Options panel

A sub-panel "R runtime" in the existing "Options" category (`@OptionsPanelController.SubRegistration(location = "Options", ...)`, in `TIMPController`, which then needs `org-netbeans-modules-options-api`):

- Mode: "Installed by Glotaran" / "My own R (Rserve on localhost:6311)".
- Status: state, R/TIMP/Rserve versions, location and size on disk.
- Buttons: Install / Reinstall, Remove, Open install log.

Install and remove run in the background with a progress bar; the panel only shows state.

### 8. Installers

`release.yml`: for each installer, download the pinned micromamba for that platform (`micromamba-win-64`, `micromamba-linux-64`, `micromamba-osx-64`, `micromamba-osx-arm64`), check the SHA-256, and put it at `glotaran/micromamba/micromamba[.exe]` in the zip given to nbpackage (Windows, Linux) or in the macOS bundle. Executable bit set on Linux and macOS. The generic zip stays platform-neutral and relies on the download in section 2.

## Verified so far

Windows 11, 2026-10-11 and 2026-10-16:

- `pixi update` locks the dependency list above (without `r-timp`) for all four platforms; the exported explicit files install with `micromamba create -y --no-rc -r <root> -p <env> -f <file>` in 20 s with a warm network cache. Result: 1.2 GB environment, 279 MB package cache.
- In that environment, started with `micromamba run` and `R_LIBS_USER` pointing to a non-existent path: R 4.4.3, `.libPaths()` is only the environment's library; fields 17.3, colorspace 2.1.3, deSolve 1.42, gclus 1.3.3, gplots 3.3.0, minpack.lm 1.2.4, nnls 1.6 and Rserve 1.8.14 load.
- With a locally built `r-timp`, Glotaran's Java client (REngine 2.1.0, Rserve 1.8.1) ran TIMP's `fitModel` example over Rserve; rate constants equal CRAN R 4.5.3 and 4.6.1 to about 9 significant digits.
- From Java 25: Rserve started through `micromamba run` answered on a free port after 0.5 s. The process tree is `micromamba.exe` → `cmd.exe` → `Scripts\Rscript.exe` → `lib\R\bin\x64\Rscript.exe`; destroying the descendants and the process left no process running and closed the port.
- First download, from a dry run with an empty cache: 264 MB (Windows), 262 MB (macOS arm64), 272 MB (macOS x64), 305 MB (Linux).

Windows 11, 2026-10-17, with the step 2 classes (`RRuntime`, `RserveProcess`) from a small Java program, in a runtime folder whose path contains a space and with a spec file that adds the locally built `r-timp`:

- micromamba 2.9.0-0 was downloaded and passed the SHA-256 check; `ensureReady` installed and checked the runtime in 58 s (R 4.4.3, Rserve 1.8.14, TIMP 1.13.6) and left no package cache.
- `ensureStarted` had Rserve answering after 0.6 s; two REngine connections in a row ran TIMP's `fitModel` example; `.libPaths()` was only the environment's library. After `stop()` the R process was gone and the port closed.
- A user-set `R_HOME` (pointing to a CRAN R) does not affect the managed R; `micromamba run` sets its own.

Not verified yet: installation and Rserve on macOS and Linux; behaviour behind a proxy; install time on a slow connection.

## Testing

- Unit tests (JUnit, `TIMPController`): platform mapping, presence of the four explicit files, runtime ID, command lines and process environment, state from the marker file, port selection.
- CI job per OS (Windows, Ubuntu, macOS arm64 and x64), `r-runtime` in `maven.yml`, running `ManagedRuntimeIT`: download micromamba, create the environment from the explicit file, start Rserve and connect twice with the Java client. This checks the lock on every platform without the GUI. Until `r-timp` is on conda-forge it checks R and Rserve only; then it should use `ensureReady()` and run a small TIMP fit. Verified locally on Windows 11 and WSL Ubuntu (2026-10-17).
- The installer smoke test (`smoke-test.sh`) runs `micromamba --version` from the installation.
- Manual acceptance on clean user accounts, as listed under Acceptance, including the example project compared with 1.6.0.

## Implementation order

Each step is a commit on `auto_deploy_r_timp_rserve`, reviewed before the next:

1. Runtime lock: pixi workspace, explicit files, export script.
2. `RRuntime`, micromamba lookup and `RserveProcess`, with unit tests.
3. `TimpController` integration, first-run choice, messages; `Installer.close()`.
4. Options panel.
5. `release.yml`: bundle micromamba; CI job for the lock.
6. Documentation: release notes, `TESTING.md`, `RELEASING.md`.

## Decisions (2026-10-16)

1. micromamba is downloaded (pinned version, SHA-256 check) when it is not bundled, so the generic zip, update-center installations and development runs can use the managed runtime.
2. Glotaran asks only when an existing R or Rserve is found; otherwise it installs the runtime with a notification and a cancellable progress bar (section 5).
3. The runtime lives per release line in the user directory, `~/.glotaran/1.7/r-runtime`.
4. "Use my own R installation" stays available with the 1.6 behaviour, for users who have R and want to keep using it.
5. The 1.7 release waits until `r-timp` is published on conda-forge. If the implementation is finished earlier, a CI build of `r-timp` published as a GitHub release file may be used for testing in the meantime, not for the release.
6. Testing on an Intel Mac and on Linux happens last in the development pipeline. Until then, Linux is tested in WSL (Ubuntu) where relevant.
7. (2026-10-17) `TimpController` shows every connection message itself, and `AnalysisWorker` no longer shows the "are you sure you started Rserve?" message when `isConnected()` returns false. Otherwise a managed-mode failure, a cancelled installation or a cancelled first-run question would be followed by that message.
