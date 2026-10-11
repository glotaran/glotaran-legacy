# Maven

This repository is a Maven multi-module reactor. The root `pom.xml` defines version properties, plugin defaults, profiles, and the module list.

## Default workflow

- Validate from the repository root with `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`.
- Use `mvn -T 1C clean install` only when a later, separate Maven invocation needs the reactor artifacts in the local Maven repository.
- Shared build behaviour belongs in the root POM.

## Branch-specific facts

- The build uses Maven 3.9+ and `org.apache.netbeans.utilities:nbm-maven-plugin` 14.5.
- The build runs on JDK 25; the compiler uses `--release 17` (`glotaran.javac.release`).
- NetBeans Platform artifacts (`RELEASE310`) are resolved from Maven Central.
- `repo/` is a checked-in Maven repository with the libraries that are not available from a public repository: Jama 1.0.2, ojalgo, and UJMP (`ujmp-complete`). Change it only as part of dependency maintenance.

## Multi-module work

- Most modules are NetBeans modules. Changing an API used by another module usually requires changes to both code and module metadata.
- When you change an exported API, check `publicPackages` in the module POM and build the modules that use it.
- Build from the root after any change that can affect more than one module.

## Profiles

- `deployment` builds the portable ZIP and the autoupdate site, and attaches sources and Javadoc for `deploy`.
- `release`, `release-extra`, `export-javadoc`, and `export-sources` are for publishing and are not needed during development.
- The application module's packaging profiles are described in [PACKAGING_AND_RELEASE.md](PACKAGING_AND_RELEASE.md).

## Known defects in the publishing configuration

None of these affects `verify`, `install`, or the `deployment` profile.

- The release plugin requests a profile named `releases`; the profile is named `release`.
- Distribution management needs `${github.maven.repository}`, which is not defined in the repository, so `deploy` fails.
- `export-javadoc` and `export-sources` refer to `src/assemble/javadoc.xml` and `src/assemble/sources.xml`, which do not exist.
- NBM signing refers to `src/keystore/keystore.ks`, which is not in the repository; the build warns and produces unsigned NBMs.
- The NBM license is configured as `../../COPYING.txt`, which resolves outside the checkout, so every build warns that it is missing. The license files are in `license/`.
