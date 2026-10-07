# NetBeans Platform

This branch uses Apache NetBeans Platform 31 (`RELEASE310`). Module names and clusters differ from NetBeans 8.0.2, which the 1.5.x branch uses.

## Module boundaries

- Most modules in this repository are NetBeans modules that are assembled into one desktop application.
- Keep code in the module that owns the responsibility. Move a boundary only when the task requires it.
- If you add to a module's public API, update its exported packages and build the modules that depend on it.

## Registration

- Modules register their contributions through `layer.xml`, `@ServiceProvider`, and `Lookup`.
- Actions, file types, editors, and windows are registered with the NetBeans registration mechanisms; there is no central startup code that wires them together.
- Add new contributions through the registration point that already exists for that kind of contribution.

## Application assembly

- `application/` assembles the branded application.
- Branding and packaging run as part of the build. A registration error can therefore surface during application assembly even though every module compiles.

## Scope of changes

- Leave the platform setup unchanged unless the task is about packaging or release.
- If a change appears to need a different platform version, confirm the scope first; see [ROADMAP.md](ROADMAP.md).
