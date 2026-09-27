# Testing

Five modules contain tests (`application`, `ExampleDataloader`, `JFreeChartCustom`, `JRConnect`, `LabmonkeyDataloader`), and the default build does not run them.

## Default behaviour

- The root POM sets `skipTests=true`, so `clean verify` compiles, packages, and checks module dependencies without running tests.
- Use the root `clean verify` as the baseline check unless the task requires running tests.

## Running tests

- Enable tests explicitly on the Maven command line, for the module you changed or for `application/`.
- Application-level tests use the NetBeans test infrastructure, for example `NbModuleSuite` in `application/src/test/java/org/glotaran/ApplicationTest.java`.
- Some modules keep tests in `src/test/java`, others in the older `test/unit` layout.

## Guidance

- Run the tests of the modules you changed. Whether the other modules' tests pass is unknown.
- When changing loader behaviour or shared APIs, add or update tests in modules that already have tests.
- The default build does not verify functionality. If a task needs functional coverage, agree on the scope first.
