# Local Development

## Toolchain

- Use JDK 8 and Maven 3.x for this branch.

## Running locally

- Run the application from the application module with `mvn nbm:cluster-app nbm:run-platform`.
- Built Windows launchers are generated under `application/target/glotaran/bin/` after packaging.

## Debugging environment issues

- If the launcher reports an invalid `jdkhome`, fix `JAVA_HOME` first and rerun the Maven application build before editing repo files.
- Prefer diagnosing local environment problems before changing packaging or launcher configuration.