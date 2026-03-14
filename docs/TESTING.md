# Testing

Testing in this branch is legacy and uneven. Keep expectations explicit instead of assuming a modern, always-on reactor test suite.

## Default behavior

- The root build config skips tests by default, so `clean verify` is primarily a compile and packaging validation step.
- Use that root verify command as the baseline check unless the task needs test execution.

## When you need tests

- Re-enable tests deliberately with Maven, usually in the module you changed or in the application module.
- Application-level tests use NetBeans test infrastructure such as `NbModuleSuite`.
- Some modules use conventional `src/test/java`; others use older layouts such as `test/unit`.

## Practical guidance

- Prefer targeted test runs over assuming the entire reactor test story is healthy.
- When changing loader behavior or shared APIs, add or update focused tests where the module already has a test pattern.
- If a task depends on full functional coverage, confirm the intended scope because the default build does not provide it.