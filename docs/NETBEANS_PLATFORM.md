# NetBeans Platform

This branch stays on NetBeans Platform `RELEASE802` and the legacy NetBeans module model. Do not import assumptions, APIs, or module IDs from newer NetBeans branches.

## Module boundaries

- Most modules in this repository are NetBeans modules assembled into one desktop application.
- Keep responsibilities inside the owning module unless the task clearly requires a boundary change.
- If you widen a module API, update its exported package metadata and build the dependent modules that consume it.

## Common extension patterns

- Module registration commonly happens through `layer.xml`, `@ServiceProvider`, and `Lookup`-based discovery.
- UI actions, file support, editors, and window system contributions usually follow NetBeans platform registration patterns instead of ad hoc bootstrapping.
- Prefer extending the existing registration point over introducing a parallel mechanism.

## Application assembly

- The application module assembles the branded NetBeans application.
- Branding and packaging are part of the build, so changes in module registration can surface at application assembly time even when a single module compiles.

## Change discipline

- Keep the legacy platform flow intact unless the task explicitly targets packaging or release behavior.
- If a change looks like it needs platform modernization, stop and confirm scope first.