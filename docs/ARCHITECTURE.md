# Architecture

Glotaran is a set of NetBeans modules grouped by responsibility. Modules register their contributions through module manifests, `layer.xml`, service-provider files, and Lookup; there is no `main` class that represents the application.

## Reactor

The root `pom.xml` is the parent of 33 reactor projects: 31 Glotaran modules, the branding module, and the application assembler. Maven determines the build order from the dependencies between modules; the order of the module list does not matter.

| Path | Packaging | Role |
| --- | --- | --- |
| `pom.xml` | `pom` | Parent: dependency and plugin management, profiles, module list |
| `branding/` | `nbm` | NetBeans branding overlays, icons, splash screen, titles |
| `application/` | `nbm-application` | Selects the modules and assembles the runnable product |

The application POM depends on the Glotaran modules and on the NetBeans Platform clusters; its exclusions remove platform modules that Glotaran does not ship. A new reactor module is only part of the product if it is a runtime dependency of `application/`, directly or through another module.

Modules:

- Core and shared APIs: `CoreInterfaces`, `CoreModels`, `CoreMessages`, `CoreMain`, `CoreAnalysis`, `CoreSimulation`, `CoreUI`, `CoreOptions`, `CoreAUC`, `CoreDataDisplayers`, `CoreResultDisplayers`, and `CoreVisualModelling`.
- File and model support: `AnalysisOverviewFileSupport`, `GtaFileSupport`, `TGMFileSupport`, and `SimSupport`.
- Data loaders: `ASCIIDataloader`, `AVGDataloader`, `csvdataloader`, `imgdataloader_new`, `PQDataloader`, `RawDataloader`, `SDTDataloader`, `ExampleDataloader`, and `LabmonkeyDataloader`. `AVGDataloader` and `ExampleDataloader` are disabled by default in the assembled application.
- R integration: `JRConnect` and `TIMPController`.
- Library wrappers: `jfreechart`, `JFreeChartCustom`, `jide-oss`, and `UJMPwithdep`.

`JAXBWizzardOutput/` has a POM but is not in the reactor. Its generated sources were the input for the JAXB classes in `CoreModels`. The `JRI/` and `REngine/` directories are not reactor modules either.

## Module groups

- `Core*` modules hold the shared APIs, data models, analysis and simulation logic, UI infrastructure, and result displays.
- Dataloader modules each read one file format. Keep their dependencies limited to what the parser needs.
- Support modules provide file-type integration (`.gta`, `.tgm`, simulation files), the R/TIMP connection (`JRConnect`, `TIMPController`), and wrappers for third-party libraries (`jfreechart`, `jide-oss`, `UJMPwithdep`).
- `application/` and `branding/` assemble the NetBeans Platform distribution.

## Consequences for changes

- A change to an exported API or a registration affects every module that uses it; build from the root after such changes.
- A change in a loader can alter what `Core*` modules and the UI receive, even if the edit is confined to the parser.
- A failure during application assembly is often caused by module metadata, registration, or dependency changes in another module.
