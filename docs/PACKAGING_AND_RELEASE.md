# Packaging And Release

[BUILD_AND_DISTRIBUTION.md](BUILD_AND_DISTRIBUTION.md) describes the full packaging and release setup, including which parts were verified to work. The portable ZIP and update site build correctly; the NBI installers and the macOS DMG do not.

## Everyday packaging

- `application/` (packaging `nbm-application`) assembles the runnable application.
- Run the application locally with `mvn nbm:cluster-app nbm:run-platform` from `application/`.
- The default build does not create the standalone ZIP; the `deployment` profile enables it.

## Release profiles and goals

- The application module's `deployment` profile creates the standalone ZIP and the autoupdate site.
- The NBI installers are built by the separate goal `nbm:build-installers`, which no profile or lifecycle phase runs. On 2026-07-04 it staged the build and exited successfully without producing an installer.
- The application module has further profiles for building a DMG, attaching a DMG, and replacing the Windows launcher icons. They depend on tools and settings on the original release machine, and CI does not run them.
- The root POM has release and export profiles for sources, Javadoc, and deployment metadata.

## Working with these profiles

- Do not use the release profiles in the normal edit-build-test cycle.
- Packaging changes usually involve the application module, the branding assets, and module metadata.
- Before changing installers, autoupdate output, or branded launchers, read both POMs and [BUILD_AND_DISTRIBUTION.md](BUILD_AND_DISTRIBUTION.md).
