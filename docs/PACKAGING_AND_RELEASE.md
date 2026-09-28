# Packaging And Release

[RELEASING.md](../RELEASING.md) describes how the 1.6.x release artifacts are built: the autoupdate site, the generic ZIP, and the Windows, Linux, and macOS installers with a bundled Java runtime. The `Release installers` workflow (`.github/workflows/release.yml`) runs the same steps in CI. [BUILD_AND_DISTRIBUTION.md](BUILD_AND_DISTRIBUTION.md) describes the 1.5.x setup.

## Everyday packaging

- `application/` (packaging `nbm-application`) assembles the runnable application.
- Run the application locally with `mvn nbm:cluster-app nbm:run-platform` from `application/`.
- The default build does not create the standalone ZIP; the `deployment` profile enables it.

## Release profiles and goals

- The application module's `deployment` profile creates the standalone ZIP and the autoupdate site.
- Installers are built from the generic ZIP with Apache NetBeans nbpackage, outside Maven. The NBI goal `build-installers` does not exist in `org.apache.netbeans.utilities:nbm-maven-plugin` 14.5; the NBI settings left in the root POM are unused.
- The application module has further profiles for building a DMG, attaching a DMG, and replacing the Windows launcher icons. They depend on tools and settings on the original release machine, and CI does not run them.
- The root POM has release and export profiles for sources, Javadoc, and deployment metadata.

## Working with these profiles

- Do not use the release profiles in the normal edit-build-test cycle.
- Packaging changes usually involve the application module, the branding assets, and module metadata.
- Before changing installers, autoupdate output, or branded launchers, read both POMs and [RELEASING.md](../RELEASING.md).
