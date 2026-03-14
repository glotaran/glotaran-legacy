# Contributing

## Project shape

This repository is a legacy Java application built as a Maven multi-module reactor on top of the NetBeans Platform.

- The reactor root is `pom.xml`.
- The runnable application module is `application/`.
- This is not a single `main`-class project.

## Toolchain

Use the toolchain that matches the current maintenance branch behavior:

- JDK 8
- Maven 3.x

The current CI workflow uses Temurin 8 and runs from the repository root.

On Windows, set `JAVA_HOME` to the JDK root directory, not to its `bin` directory, and do not include a trailing backslash. For example:

```text
C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot
```

## Build

From the repository root, the CI-style validation command is:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```

That is the best default command when you want to validate a change without installing this repository's own artifacts into the local Maven cache.

If you want the full local artifact flow used by the current VS Code build task, run:

```sh
mvn -T 1C clean install
```

## Run

To run the application through Maven, start from `application/`:

```sh
cd application
mvn nbm:cluster-app nbm:run-platform
```

Running `nbm:run-platform` from the repository root is not the right workflow for this repo. The runnable module is `application/`.

If the NetBeans launcher reports an invalid `jdkhome` on Windows, check `JAVA_HOME` first. A trailing backslash or a `...\bin` path can cause the generated launcher config to point at the wrong location.

After a successful build, Windows launchers are generated under:

```text
application/target/glotaran/bin/
```

The 64-bit launcher is typically:

```text
application/target/glotaran/bin/glotaran64.exe
```

## VS Code

This repository includes workspace tasks in `.vscode/tasks.json` for:

- building the full reactor
- running the application through Maven
- running the built Windows launcher

Those tasks currently pin JDK 8 and Maven on Windows.

## Contribution expectations

- Keep changes focused and avoid unrelated refactors.
- Validate with Maven before opening a pull request.
- Prefer the CI-style `verify` command for routine validation.
- Use `install` only when you actually need the locally installed artifacts.
- Be careful with assumptions that may only apply to other branches such as `main` or `maintenance/v1.6.x`.

## CI

The current GitHub Actions workflow lives in `.github/workflows/maven.yml` and uses:

- `actions/setup-java@v4`
- Temurin JDK 8
- `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`