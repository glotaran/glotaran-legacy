# Maven

This repository is a Maven reactor, not a single-module Java project. The root `pom.xml` owns version properties, plugin defaults, profile wiring, and module order.

## Default workflow

- Use `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress` from the repository root for routine validation.
- Use `mvn -T 1C clean install` only when another step needs the reactor artifacts in the local Maven cache.
- Treat the root reactor as the source of truth for module order and shared build behavior.

## Branch-specific facts

- The branch uses Maven 3.x with the legacy `nbm-maven-plugin` flow.
- The build runs on JDK 8, while compiler settings target Java 7 bytecode for compatibility.
- The repository includes a checked-in local Maven repository for legacy artifacts that are not expected from Central. Avoid changing it unless the task is dependency maintenance.

## Multi-module work

- Many modules are NetBeans modules, so cross-module API changes often need both code changes and module metadata updates.
- When you change an exported API, check the affected module POM for `publicPackages` and validate downstream consumers.
- Prefer validating from the root after changes that could affect more than one module.

## Profiles worth knowing

- `deployment` enables release-oriented artifacts and publishing behavior.
- `release`, `release-extra`, `export-javadoc`, and `export-sources` are publishing flows, not routine development commands.
- Application packaging profiles live in the application module and are described in `PACKAGING_AND_RELEASE.md`.