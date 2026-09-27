# Architecture

Glotaran is a set of NetBeans modules grouped by responsibility. [BUILD_AND_DISTRIBUTION.md](BUILD_AND_DISTRIBUTION.md) lists every module.

## Module groups

- `Core*` modules hold the shared APIs, data models, analysis and simulation logic, UI infrastructure, and result displays.
- Dataloader modules each read one file format. Keep their dependencies limited to what the parser needs.
- Support modules provide file-type integration (`.gta`, `.tgm`, simulation files), the R/TIMP connection (`JRConnect`, `TIMPController`), and wrappers for third-party libraries (`jfreechart`, `jide-oss`, `UJMPwithdep`).
- `application/` and `branding/` assemble the NetBeans Platform distribution.

## Consequences for changes

- A change to an exported API or a registration affects every module that uses it; build from the root after such changes.
- A change in a loader can alter what `Core*` modules and the UI receive, even if the edit is confined to the parser.
- A failure during application assembly is often caused by module metadata, registration, or dependency changes in another module.
