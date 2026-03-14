# Packaging And Release

Packaging in this repository is profile-driven and centered on the NetBeans application module. Keep routine development commands separate from release commands.

## Everyday packaging

- The runnable application is assembled through the `nbm-application` module in `application/`.
- Local application runs use `mvn nbm:cluster-app nbm:run-platform` from that module.
- The default application build disables the standalone zip goal; release profiles turn it back on.

## Release-oriented profiles

- The application module's `deployment` profile enables standalone zip creation and autoupdate generation.
- The application module also carries extra release profiles for DMG creation, DMG attachment, and Windows icon replacement.
- The root reactor has separate release and export profiles for sources, javadocs, and deployment metadata.

## What this means in practice

- Do not treat release profiles as part of normal edit-build-test loops.
- Packaging changes often span the application module, branding assets, and module metadata together.
- If the task is about installers, autoupdate output, or branded launchers, review both the root and application POM profiles before editing.