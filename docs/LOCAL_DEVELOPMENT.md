# Local Development

For a step-by-step guide, see [DEVELOPMENT.md](DEVELOPMENT.md).

## Toolchain

- Use JDK 8 and Maven 3.x for this branch.

## Running locally

- Run the application from `application/` with `mvn nbm:cluster-app nbm:run-platform`.
- After packaging, the Windows launchers are in `application/target/glotaran/bin/`.

## Environment problems

- If the launcher reports an invalid `jdkhome`, correct `JAVA_HOME` and rebuild the application before editing files in the repository.
- Rule out the local environment (JDK, `JAVA_HOME`, Maven) before changing packaging or launcher configuration.
