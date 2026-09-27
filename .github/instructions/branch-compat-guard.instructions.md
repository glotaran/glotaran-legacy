---
applyTo: "**"
---
# Branch Compatibility Guard (1.5.x)

Treat this workspace as legacy branch behavior, not `main` or `maintenance/v1.6.x`.

## Hard invariants

- Keep NetBeans Platform pinned to `RELEASE802` (NetBeans 8.0.2).
- Do not introduce NetBeans 12.x APIs, module IDs, or migration assumptions.
- Do not perform Java/runtime modernization unless the user explicitly asks for it in this branch.
- Preserve Maven/JDK compatibility expected by this branch (JDK 8 toolchain).

## Required checks before proposing changes

- Verify version-sensitive assumptions against [pom.xml](../../pom.xml) and [application/pom.xml](../../application/pom.xml).
- Prefer branch-local docs and config over generic NetBeans guidance from newer branches.
- If a requested change appears to require platform modernization, pause and confirm scope before editing.

## Prohibited assumptions

- Assuming NetBeans 12.6+ module behavior.
- Assuming Java 11/17+ baseline.
- Replacing legacy packaging/run flow with modernization defaults.
