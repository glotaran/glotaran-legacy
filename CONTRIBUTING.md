# Contributing

## Project layout

This repository is a Java desktop application built on the NetBeans Platform as a Maven multi-module reactor.

- The reactor root is `pom.xml`.
- The runnable application is assembled by `application/`. There is no single `main` class.

For a step-by-step guide to building, running, and debugging the application, see [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md). Planned releases are described in [docs/ROADMAP.md](docs/ROADMAP.md).

## Toolchain

- JDK 8
- Maven 3.x

CI uses Temurin JDK 8 and builds from the repository root.

On Windows, set `JAVA_HOME` to the JDK root directory, without `bin` and without a trailing backslash. For example:

```text
C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot
```

## Build

To validate a change, run from the repository root:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```

This is the command CI runs. It does not install the reactor's artifacts into your local Maven repository.

To also install the artifacts locally, run:

```sh
mvn -T 1C clean install
```

## Run

To run the application through Maven, start from `application/`:

```sh
cd application
mvn nbm:cluster-app nbm:run-platform
```

`nbm:run-platform` does not work from the repository root.

If the NetBeans launcher reports an invalid `jdkhome` on Windows, check `JAVA_HOME` first. A trailing backslash or a path ending in `\bin` makes the generated launcher configuration point to the wrong directory.

After a successful build, the Windows launchers are in:

```text
application/target/glotaran/bin/
```

The 64-bit launcher is `application/target/glotaran/bin/glotaran64.exe`.

## VS Code

The repository does not include VS Code configuration; `.vscode/` is listed in `.gitignore`. If you want VS Code tasks, create a local `.vscode/tasks.json` with tasks for:

- `mvn -T 1C clean install` from the repository root
- `mvn nbm:cluster-app nbm:run-platform` from `application/`
- `application/target/glotaran/bin/glotaran64.exe`

Set `JAVA_HOME` and `PATH` in the task `options.env` to your local JDK 8 and Maven installations.

## Contribution guidelines

- Keep changes focused and leave unrelated code alone.
- Run the `verify` command above before opening a pull request.
- Use `install` only when you need the artifacts in your local Maven repository.
- Other branches, such as `main` and `maintenance/v1.6.x`, may use different versions and settings; check before applying their conventions here.

## CI

The GitHub Actions workflow `.github/workflows/maven.yml` runs on Ubuntu with:

- `actions/setup-java@v6`
- Temurin JDK 8
- `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`
