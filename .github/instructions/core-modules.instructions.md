---
applyTo: "Core*/**"
---
# Core Modules Guidance

Applies to Core modules such as `CoreInterfaces`, `CoreMessages`, `CoreModels`, `CoreAnalysis`, `CoreUI`, and related `Core*` packages.

## Editing rules

- Keep module boundaries stable; avoid moving responsibilities across Core modules unless required.
- When changing exported APIs, verify corresponding module export settings (`publicPackages`) in the module POM.
- Prefer minimal, localized fixes over cross-module refactors in maintenance work.

## Validation expectations

- After non-trivial Core changes, run reactor validation from the repository root:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```

- If compile impact spans multiple modules, ensure downstream Core modules still build cleanly.

## NetBeans specifics

- Maintain compatibility with NetBeans 8 module conventions.
- Avoid introducing APIs that are only available in newer NetBeans branches.
