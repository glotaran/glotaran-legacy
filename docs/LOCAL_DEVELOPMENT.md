# Local Development

For a step-by-step guide, see [DEVELOPMENT.md](DEVELOPMENT.md).

## Toolchain

- Use JDK 25 and Maven 3.9+ for this branch. If another JDK is first on `PATH`, set `JAVA_HOME` explicitly.

## Running locally

- Run the application from `application/` with `mvn nbm:cluster-app nbm:run-platform`.
- After packaging, the Windows launchers are in `application/target/glotaran/bin/`.

## R in development runs

On the first analysis Glotaran installs its own R into `<userdir>/r-runtime` (in development runs `application/target/userdir/r-runtime`) or uses the user's own Rserve on `localhost:6311`; see [R_RUNTIME_DESIGN.md](R_RUNTIME_DESIGN.md). Two system properties help in development:

- `glotaran.rruntime.spec=<file>`: a conda explicit file that replaces the bundled one for this platform. Until `r-timp` is on conda-forge the bundled files have no TIMP, so the installation check fails without this property. Copy `TIMPController/src/main/resources/org/glotaran/timpcontroller/runtime/<platform>.txt` and append a line with a `file:///` URL to a locally built `r-timp` package and its MD5, for example `file:///C:/build/win-64/r-timp-1.13.6-r44h1cdbda2_0.conda#<md5>`.
- `glotaran.micromamba=<file>`: a micromamba executable to use instead of the bundled or downloaded one.

Pass them to `nbm:run-platform` through `netbeans.run.params`:

```sh
mvn nbm:cluster-app nbm:run-platform "-Dnetbeans.run.params=-J-Dglotaran.rruntime.spec=C:/build/dev-spec.txt"
```

A changed explicit file has a new runtime ID, so Glotaran installs a new environment next to the old one and removes the old one afterwards. To update the lock itself, edit `TIMPController/src/main/r-runtime/pixi.toml` and run `export-specs.ps1 -Update` in that folder (needs [pixi](https://pixi.sh)).

## Environment problems

- If the launcher reports an invalid `jdkhome`, correct `JAVA_HOME` and rebuild the application before editing files in the repository.
- Rule out the local environment (JDK, `JAVA_HOME`, Maven) before changing packaging or launcher configuration.
