# AGENTS

Glotaran 1.5.x is a legacy Java desktop application built as a Maven multi-module reactor on NetBeans Platform RELEASE802.

- Build with JDK 8 and Maven 3.x. Sources compile at Java 7 level (`-source/-target 1.7`); do not use Java 8 language features or APIs.
- Default validation from the repository root: `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`. The root POM sets `skipTests=true`, so this checks compilation, packaging, and module dependencies only.
- To run the application, first run `mvn -T 1C clean install` from the repository root, then from `application/`: `mvn nbm:cluster-app nbm:run-platform`. The run step resolves the modules from the local Maven repository.
- `repo/` is a checked-in Maven repository with the RELEASE802 artifacts and libraries not available from Maven Central. Change it only as part of dependency maintenance.
- For a quick start, see `docs/DEVELOPMENT.md`.
- For Maven conventions, see `docs/MAVEN.md`.
- For module and NetBeans Platform patterns, see `docs/NETBEANS_PLATFORM.md` and `docs/ARCHITECTURE.md`.
- For testing, packaging, and local run/debug notes, see `docs/TESTING.md`, `docs/PACKAGING_AND_RELEASE.md`, and `docs/LOCAL_DEVELOPMENT.md`.
