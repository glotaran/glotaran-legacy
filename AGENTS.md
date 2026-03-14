# AGENTS

Glotaran 1.5.x is a legacy Java desktop application built as a Maven multi-module reactor on NetBeans Platform RELEASE802.

- Use JDK 8 and Maven 3.x. Keep branch assumptions aligned with the legacy 1.5.x line and NetBeans 8.0.2.
- Default validation from the repository root: `mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress`
- Use `mvn -T 1C clean install` only when you need this reactor's artifacts installed locally.
- Run the application from `application/`: `mvn nbm:cluster-app nbm:run-platform`
- For Maven conventions, see `docs/MAVEN.md`.
- For module and NetBeans Platform patterns, see `docs/NETBEANS_PLATFORM.md` and `docs/ARCHITECTURE.md`.
- For testing, packaging, and local run/debug notes, see `docs/TESTING.md`, `docs/PACKAGING_AND_RELEASE.md`, and `docs/LOCAL_DEVELOPMENT.md`.