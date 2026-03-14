# Architecture

The codebase is organized around a small set of stable responsibilities rather than a deep framework hierarchy.

## High-level areas

- `Core*` modules hold shared APIs, domain models, analysis logic, UI infrastructure, and result presentation.
- Dataloader modules are format-specific adapters. They should stay narrow and avoid pulling in broad platform dependencies unless required.
- Support modules handle file formats, controller integration, and third-party library packaging.
- The application and branding modules assemble the final NetBeans Platform distribution.

## Practical implications

- Cross-module edits are higher risk than localized fixes because exported APIs and registrations affect multiple modules.
- Loader behavior changes can affect both Core consumers and UI flows, even when the change looks parser-local.
- Packaging failures are often downstream symptoms of module metadata, registration, or dependency changes made elsewhere.