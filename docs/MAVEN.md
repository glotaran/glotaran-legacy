# Maven

This repository is a Maven multi-module reactor. The root `pom.xml` defines version properties, plugin defaults, profiles, and the module list.

## Default workflow

- Validate from the repository root with `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`.
- Use `mvn -T 1C clean install` only when a later, separate Maven invocation needs the reactor artifacts in the local Maven repository.
- Shared build behaviour belongs in the root POM.

## Branch-specific facts

- The build uses Maven 3.x and `org.codehaus.mojo:nbm-maven-plugin` 3.14.
- The build runs on JDK 8; the compiler settings produce Java 7 bytecode.
- `repo/` is a checked-in Maven repository with the NetBeans `RELEASE802` artifacts and libraries that are not available from Maven Central. Change it only as part of dependency maintenance.

## Multi-module work

- Most modules are NetBeans modules. Changing an API used by another module usually requires changes to both code and module metadata.
- When you change an exported API, check `publicPackages` in the module POM and build the modules that use it.
- Build from the root after any change that can affect more than one module.

## Profiles

- `deployment` builds the portable ZIP and the autoupdate site, and attaches sources and Javadoc for `deploy`.
- `release`, `release-extra`, `export-javadoc`, and `export-sources` are for publishing and are not needed during development.
- The application module's packaging profiles are described in [PACKAGING_AND_RELEASE.md](PACKAGING_AND_RELEASE.md).
