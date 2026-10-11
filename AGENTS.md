# AGENTS

Glotaran 1.6.x is a legacy Java desktop application built as a Maven multi-module reactor on Apache NetBeans Platform 31 (`RELEASE310`).

- Build with JDK 25 and Maven 3.9+. Sources compile with `--release 17` (`glotaran.javac.release` in the root POM); do not use language features or APIs newer than Java 17.
- Default validation from the repository root: `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`. The root POM sets `skipTests=true`, so this checks compilation, packaging, and module dependencies only.
- To run the application, first run `mvn -T 1C clean install` from the repository root, then from `application/`: `mvn nbm:cluster-app nbm:run-platform`. The run step resolves the modules from the local Maven repository.
- NetBeans artifacts come from Maven Central. `repo/` is a checked-in Maven repository for the libraries not available there (Jama, ojalgo, UJMP). Change it only as part of dependency maintenance.
- For release artifacts (autoupdate site, generic zip, installers), see `RELEASING.md`.
- For a quick start, see `docs/DEVELOPMENT.md`.
- For Maven conventions, see `docs/MAVEN.md`.
- For module and NetBeans Platform patterns, see `docs/NETBEANS_PLATFORM.md` and `docs/ARCHITECTURE.md`.
- For testing, packaging, and local run/debug notes, see `docs/TESTING.md`, `docs/PACKAGING_AND_RELEASE.md`, and `docs/LOCAL_DEVELOPMENT.md`.
