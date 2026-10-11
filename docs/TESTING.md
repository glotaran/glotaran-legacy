# Testing

Five modules contain tests (`application`, `ExampleDataloader`, `JFreeChartCustom`, `JRConnect`, `LabmonkeyDataloader`), and the default build does not run them.

## Default behaviour

- The root POM sets the property `skipTests=true`, so `clean verify` compiles, packages, and checks module dependencies without running tests. Pass `-DskipTests=false` to run them.
- Use the root `clean verify` as the baseline check unless the task requires running tests.
- CI (`.github/workflows/maven.yml`) runs only the application smoke test, on Linux, Windows and macOS:

  ```sh
  mvn -B -T 1C clean verify -Pdeployment -DskipTests=false -Dtest=ApplicationTest -DfailIfNoTests=false
  ```

  `ApplicationTest` starts the assembled platform with all clusters through `NbModuleSuite` and fails if anything is logged at WARNING level or above. On Linux without a display, run it under `xvfb-run -a`.
- The release workflow (`.github/workflows/release.yml`) installs each installer and starts Glotaran from it with `.github/scripts/smoke-test.sh`, which checks `messages.log` for the bundled runtime and the enabled Glotaran modules.

## Running tests

- Enable tests explicitly on the Maven command line, for the module you changed or for `application/`.
- Application-level tests use the NetBeans test infrastructure, for example `NbModuleSuite` in `application/src/test/java/org/glotaran/ApplicationTest.java`. `application/pom.xml` pins the Surefire JUnit 4 provider, because TestNG is also on the test classpath and Surefire would otherwise pick it and run no tests.
- Some modules keep tests in `src/test/java`, others in the older `test/unit` layout.
- The `LabmonkeyDataloader` tests are a copy of the `ExampleDataloader` tests and fail (they expect the `example` file extension).

## Guidance

- Run the tests of the modules you changed. Apart from `ApplicationTest` and the `LabmonkeyDataloader` tests above, whether the other modules' tests pass is unknown.
- When changing loader behaviour or shared APIs, add or update tests in modules that already have tests.
- The default build does not verify functionality. If a task needs functional coverage, agree on the scope first.
