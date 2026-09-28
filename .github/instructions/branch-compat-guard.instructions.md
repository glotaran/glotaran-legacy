---
applyTo: "**"
---
# Branch Compatibility Guard (1.6.x)

Treat this workspace as the 1.6.x branch, not `main` or `maintenance/v1.5.x`.

## Hard invariants

- Keep NetBeans Platform pinned to `RELEASE310` (Apache NetBeans 31).
- Build with JDK 25; sources compile with `--release 17`. Do not use language features or APIs newer than Java 17.
- Do not upgrade the platform or the Java runtime unless the user explicitly asks for it in this branch.

## Required checks before proposing changes

- Verify version-sensitive assumptions against [pom.xml](../../pom.xml) and [application/pom.xml](../../application/pom.xml).
- Prefer branch-local docs and config over NetBeans guidance written for other versions.
- If a requested change appears to require a platform upgrade, pause and confirm scope before editing.

## Prohibited assumptions

- Assuming NetBeans 8.0.2 module names, clusters, or `org.codehaus.mojo:nbm-maven-plugin` behavior.
- Assuming a Java 8 toolchain.
- Replacing the packaging and run flow described in [RELEASING.md](../../RELEASING.md) without explicit request.
