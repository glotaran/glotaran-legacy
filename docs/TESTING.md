# Testing

Six modules contain tests (`application`, `ExampleDataloader`, `JFreeChartCustom`, `JRConnect`, `LabmonkeyDataloader`, `TIMPController`), and the default build does not run them.

## Default behaviour

- The root POM sets the property `skipTests=true`, so `clean verify` compiles, packages, and checks module dependencies without running tests. Pass `-DskipTests=false` to run them.
- Use the root `clean verify` as the baseline check unless the task requires running tests.
- CI (`.github/workflows/maven.yml`) runs only the application smoke test, on Linux, Windows and macOS:

  ```sh
  mvn -B -T 1C clean verify -Pdeployment -DskipTests=false -Dtest=ApplicationTest -DfailIfNoTests=false
  ```

  `ApplicationTest` starts the assembled platform with all clusters through `NbModuleSuite` and fails if anything is logged at WARNING level or above. On Linux without a display, run it under `xvfb-run -a`.
- The release workflow (`.github/workflows/release.yml`) installs the Windows installer, the `.deb` and both DMGs (not the `.rpm`) and starts Glotaran from each with `.github/scripts/smoke-test.sh`, which checks `messages.log` for the bundled runtime and the enabled Glotaran modules, and runs the bundled micromamba.
- The `r-runtime` job in `maven.yml` runs `ManagedRuntimeIT` (see below) on Ubuntu, Windows, macOS arm64 and macOS x64.

## Managed R runtime (TIMPController)

- Unit tests, no network or R needed:

  ```sh
  mvn -B -T 1C verify -pl TIMPController -am -DskipTests=false -Dtest=MicromambaTest,RRuntimeTest,RserveProcessTest,ExistingRTest -DfailIfNoTests=false
  ```

- `ManagedRuntimeIT` installs the bundled explicit file of the current platform into a temporary folder with `RRuntime.ensureReady()` (downloads micromamba and about 300 MB of packages), starts Rserve through `RserveProcess`, connects twice with the Java client, fits TIMP's two-decay example and compares the rates with the known result, and checks that stopping Rserve ends R. Surefire does not pick it up by default because the class name does not end in `Test`; run it with `-DskipTests=false -Dtest=ManagedRuntimeIT -DfailIfNoTests=false`. Add `-Dglotaran.rruntime.spec=<file>` to test another explicit file.
- The `r-timp packages` workflow (`r-timp.yml`) builds `r-timp` on each platform and runs the recipe's test (`library('TIMP')`); it runs on pull requests that change the recipe.
- `TIMPController/pom.xml` pins the Surefire JUnit 4 provider, because TestNG is on its test classpath as well.
- Manual test in the application: run from `application/` (see [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md)), start an analysis, and check the first-run question (shown only when an R installation or an Rserve on `localhost:6311` is found), the installation progress, the "R runtime" and "Rserve" output tabs, *Tools > Options > Glotaran > R runtime*, and that no `Rscript` or `micromamba` process is left after closing Glotaran. Use a fresh user directory (`mvn clean` in `application/`) to see the first-run behaviour again.

## Running tests

- Enable tests explicitly on the Maven command line, for the module you changed or for `application/`.
- Application-level tests use the NetBeans test infrastructure, for example `NbModuleSuite` in `application/src/test/java/org/glotaran/ApplicationTest.java`. `application/pom.xml` pins the Surefire JUnit 4 provider, because TestNG is also on the test classpath and Surefire would otherwise pick it and run no tests.
- Some modules keep tests in `src/test/java`, others in the older `test/unit` layout.
- The `LabmonkeyDataloader` tests are a copy of the `ExampleDataloader` tests and fail (they expect the `example` file extension).

## Guidance

- Run the tests of the modules you changed. Apart from `ApplicationTest` and the `LabmonkeyDataloader` tests above, whether the other modules' tests pass is unknown.
- When changing loader behaviour or shared APIs, add or update tests in modules that already have tests.
- The default build does not verify functionality. If a task needs functional coverage, agree on the scope first.
