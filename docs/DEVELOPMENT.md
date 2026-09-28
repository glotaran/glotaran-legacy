# Development quick start

Glotaran 1.5.x is a NetBeans Platform application built as a Maven multi-module reactor. It requires JDK 8 and Maven 3.x.

## 1. Check the toolchain

```sh
java -version
mvn -version
```

Both commands must report JDK 8. On Windows, set `JAVA_HOME` to the JDK root directory, without `bin` and without a trailing backslash:

```text
C:\Program Files\Eclipse Adoptium\jdk-8.x-hotspot
```

See [Local development](LOCAL_DEVELOPMENT.md) and [Maven conventions](MAVEN.md) for more detail.

## 2. Build for local development

From the repository root:

```sh
mvn -T 1C clean install
```

Running `application/` in a separate Maven invocation (step 3) resolves the reactor modules from the local Maven repository, which is why this step uses `install`.

To validate a change the way CI does, without installing artifacts:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```

The default build skips tests. See [Testing](TESTING.md).

## 3. Run Glotaran

Run from the application module:

```sh
cd application
mvn nbm:cluster-app nbm:run-platform
```

`nbm:run-platform` does not work from the repository root. The assembled application is written to `application/target/glotaran/` and its temporary user directory to `application/target/userdir/`.

On Windows, you can also start the built application directly:

```powershell
.\application\target\glotaran\bin\glotaran64.exe
```

The UI starts with Java alone. Running an analysis also requires R with the TIMP and Rserve packages.

## 4. Debug the running application

From `application/`, start the platform suspended with a debugger listening on port 5005:

```sh
mvn nbm:cluster-app nbm:run-platform -Dnetbeans.run.params.debug=true
```

Attach a Java debugger to `localhost:5005`; startup continues once it is attached. Set breakpoints in the source of the module that owns the code.

To start with a fresh runtime state, stop Glotaran and run a clean build; this removes the user directory in `application/target/userdir/`.

## Common failures

- **Artifacts cannot be resolved when running `application/`:** run `mvn -T 1C clean install` once from the repository root.
- **Invalid `jdkhome`, or the launcher exits immediately:** correct `JAVA_HOME`, then rebuild the application.
- **Changes do not appear in the running application:** rebuild and install from the root before running the application module again.
- **Analysis cannot connect to R:** check R, TIMP, and Rserve separately from Glotaran, for example by running `library(TIMP); library(Rserve)` in R.

For modules, clusters, packaging, installers, CI, and releases, see [Build and distribution](BUILD_AND_DISTRIBUTION.md). For NetBeans module registration and architecture, see [NetBeans Platform](NETBEANS_PLATFORM.md) and [Architecture](ARCHITECTURE.md).
