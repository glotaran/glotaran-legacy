# Build and distribution map

This document describes the build, packaging, and release setup of the Glotaran 1.5.x line: what the POMs are configured to do, and which parts were verified to work on 2026-07-04. The normal application build works. Parts of the release setup are outdated or produce incomplete output without reporting an error.

## Summary

- The repository is a Maven reactor of 34 projects: one parent, 32 NetBeans modules, and one `nbm-application` assembler.
- NetBeans Platform is pinned to `RELEASE802`. Sources and bytecode target Java 7; the build requires JDK 8 and Maven 3.x.
- `mvn clean verify` builds an NBM for every module and assembles a runnable, unpacked application in `application/target/glotaran/`. Tests are skipped by the parent POM configuration.
- `mvn package -Pdeployment` additionally creates the portable distribution ZIP and an autoupdate site. This works.
- `nbm:build-installers` is a separate NetBeans Installer (NBI) goal. It is not part of `package`, `verify`, `install`, or the `deployment` profile.
- The NBI goal is configured for Windows and Linux. It exits successfully and creates a large staging tree, but no `.exe` or `.sh` installer. Its exit code cannot be used to check whether installers were built.
- macOS packaging is a separate Ant-based `.app`/DMG profile. It is unsigned, not notarized, depends on old Finder metadata, and CI does not run it.
- GitHub Actions only builds the ordinary reactor on Linux. It does not build or publish ZIPs, installers, DMGs, update sites, tags, or releases.
- The only GitHub release is `v1.5.1` (2015). The repository contains a Maven release-preparation commit for 1.5.2, but no `v1.5.2` tag and no 1.5.2 GitHub release.

## Overview

```text
32 NBM projects (including the branding module)
                    |
                    | jar + NetBeans metadata + bundled libraries
                    v
              per-module .nbm files
                    |
                    | dependencies of application/pom.xml
                    v
       nbm:cluster-app in application (packaging=nbm-application)
                    |
                    v
 application/target/glotaran/
   bin/       platform launchers
   etc/       glotaran.conf and cluster list
   platform/  NetBeans Platform RELEASE802
   glotaran/  Glotaran modules and branding
   ide/       selected NetBeans IDE modules
                    |
          +---------+------------------+
          |                            |
          | -Pdeployment               | explicit nbm:build-installers
          v                            v
 glotaran-app-VERSION.zip       legacy NBI staging/build
 netbeans_site/                 intended: Windows .exe + Linux .sh
                               current result: no final installers

 separate -Pcreate-dmg path: assembled clusters -> .app -> DMG
```

## Repository roles

### Reactor and application projects

The root `pom.xml` defines shared versions, dependency and plugin management, profiles, and the module list. Maven determines the build order from the dependencies between modules; the order of the module list does not matter.

The special projects are:

| Path | Packaging | Role |
| --- | --- | --- |
| `pom.xml` | `pom` | Parent, dependency/plugin management, profiles, modules |
| `branding/` | `nbm` | NetBeans branding overlays, icons, splash, titles |
| `application/` | `nbm-application` | Selects modules and assembles the runnable product |

The application POM depends directly on the Glotaran modules and on the NetBeans Platform cluster. Its exclusions remove platform modules that the application does not ship. A new reactor module is only included in the product if it is a runtime dependency of `application/`, directly or transitively.

### Glotaran modules

All product code is packaged as NetBeans modules:

- Core and shared APIs: `CoreInterfaces`, `CoreModels`, `CoreMessages`, `CoreMain`, `CoreAnalysis`, `CoreSimulation`, `CoreUI`, `CoreOptions`, `CoreAUC`, `CoreDataDisplayers`, `CoreResultDisplayers`, and `CoreVisualModelling`.
- File/model support: `AnalysisOverviewFileSupport`, `GtaFileSupport`, `TGMFileSupport`, and `SimSupport`.
- Data loaders: `ASCIIDataloader`, `AVGDataloader`, `csvdataloader`, `imgdataloader_new`, `PQDataloader`, `RawDataloader`, `SDTDataloader`, `ExampleDataloader`, and `LabmonkeyDataloader`.
- R integration: `JRConnect` and `TIMPController`.
- Library wrappers: `jfreechart`, `JFreeChartCustom`, `jide-oss`, and `UJMPwithdep`.

`JAXBWizzardOutput/` has a POM but is not in the reactor. Its generated sources were the input for the JAXB classes in `CoreModels`; it is not built. The `JRI/` and `REngine/` directories are also not reactor modules.

Modules register their contributions through module manifests, `layer.xml`, service-provider files, and Lookup. There is no `main` class that represents the application.

## Toolchain and dependencies

### Required versions

- JDK 8 is required to build, and for the installer tools.
- Compilation uses `source=1.7` and `target=1.7`.
- The parent POM requires Maven 3.2.2 or later. The verification below used Maven 3.9.14.
- `nbm-maven-plugin` is pinned to 3.14.
- NetBeans artifacts are pinned to `RELEASE802` (NetBeans 8.0.2).

The NBI goal calls `pack200`, which was removed from the JDK in Java 14, and the old plugins contain Java 5 and Java 7 build logic. Both require JDK 8.

### Checked-in Maven repository

`repo/` is a Maven repository used as a dependency source. It is not a cache and must not be deleted. It contains 788 files (about 45 MB), including 55 JARs, 86 NBMs, and 94 POMs: the RELEASE802 artifacts and a few libraries that are no longer reliably available from public repositories.

The parent declares this repository as:

```xml
<url>file://${basedir}/../repo</url>
```

The path is relative to each child project, so it resolves to `repo/` for projects such as `application/` (`application/../repo`). For the root project it points to a directory above the checkout, which does no harm because the parent has no RELEASE802 dependencies. Every child POM inherits this setting; do not change the path or remove `repo/`.

`libs/` is an older collection of binary libraries. Dependencies are resolved through the POMs, and not every file in `libs/` ends up in the product. `repo/` is the active source for locally stored artifacts.

## What each Maven lifecycle does

### Module build

For `packaging=nbm`, the NBM lifecycle:

1. Filters `src/main/nbm/manifest.mf` to `target/manifest.mf`.
2. Compiles Java to `target/classes` with Java 7 language and bytecode settings.
3. Adds NetBeans module metadata to the manifest.
4. Builds the module JAR.
5. Processes branding content, if any.
6. Builds `target/nbm/<artifact>-<version>.nbm`.

The parent adds a JAR execution to every project, including the parent and the application. The resulting empty JARs for those two projects are unused.

### Application assembly

The `nbm-application` lifecycle binds `nbm:cluster-app` to `package`. It resolves the selected RELEASE802 NBMs and the reactor NBMs, unpacks them into clusters, copies the platform launchers, installs the filtered launcher configuration, and checks that all module dependencies are satisfied.

The result is:

```text
application/target/glotaran/
```

The Windows launchers are:

```text
application/target/glotaran/bin/glotaran.exe
application/target/glotaran/bin/glotaran64.exe
```

The Unix launcher is `application/target/glotaran/bin/glotaran`.

The application POM disables the default `standalone-zip` goal in ordinary builds, so `verify` produces the unpacked application and no ZIP.

### Tests

The parent configures Surefire with `skipTests=true`, and all modules inherit it. `clean verify` compiles, builds the NBMs, assembles the application, and checks module dependencies; it runs no tests.

`application/src/test/java/org/glotaran/ApplicationTest.java` is an `NbModuleSuite` smoke test that the default build skips. Other modules use either `src/test/java` or the older `test/unit` layout.

### Install and deploy

`install` copies the reactor artifacts into the local Maven repository. A build within one reactor does not need it, because Maven resolves reactor projects directly. Use it only when a later, separate Maven invocation needs those artifacts.

`deploy` publishes to the Maven repository in the distribution management section; it does not create a GitHub release. It requires `${github.maven.repository}` and server credentials, neither of which is defined in this repository.

## Everyday commands

From the repository root:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```

This is the command CI runs. To install the artifacts into the local Maven repository:

```sh
mvn -T 1C clean install
```

Run the application from `application/`, after its dependencies were built in the same reactor or installed locally:

```sh
cd application
mvn nbm:cluster-app nbm:run-platform
```

The VS Code tasks in `.vscode/tasks.json` run these commands with fixed Windows paths to the JDK and Maven. They work only on a machine with those paths.

## Portable ZIP and autoupdate site

The `deployment` profile in the application module enables `default-standalone-zip` at `package` and runs `nbm:autoupdate`. The root profile of the same name attaches sources and configures Javadoc for `deploy`.

Run it from the root so that all reactor NBMs are available:

```sh
mvn -B -T 1C package -Pdeployment --file pom.xml --no-transfer-progress
```

For version 1.5.2 this produced:

```text
application/target/glotaran-app-1.5.2.zip
application/target/netbeans_site/updates.xml
application/target/netbeans_site/updates.xml.gz
application/target/netbeans_site/*.nbm
```

The ZIP was about 49 MB. The update site contained 117 NBMs: the Glotaran modules and the RELEASE802 modules selected by the application. The site is only written to the local build directory; nothing uploads it.

The update-center URLs registered in the application are defined in `CoreAUC`, not by the similarly named properties in the parent and branding POMs:

```text
https://glotaran.org/uc/1.5/stable/updates.xml   (enabled)
http://glotaran.org/uc/1.5/beta/updates.xml     (disabled)
```

Publishing an update therefore means copying the generated site to that web location, and deciding whether to keep the HTTP beta URL. No script or workflow in this repository does the copy.

## Windows and Linux installers (NetBeans Installer/NBI)

### Intended workflow

Installers are built with a separate goal, normally after the standalone ZIP exists:

```sh
cd application
mvn nbm:build-installers
```

The goal is configured in the parent NBM plugin for Windows and Linux; Solaris and the NBI macOS installer are disabled. It reads `application/target/glotaran-app-<version>.zip` and uses:

- `application/src/main/app-resources/template.xml`
- `ConfigurationLogic.java` and its resource bundle
- Glotaran icons and installer artwork
- the NBI stub and engine artifacts from the plugin dependencies

The custom template copies the NBI sources into a staging tree, replaces product, version, platform, and install-directory tokens, compiles the custom installer logic, packages the application data (with Pack200 by default), and has NBI generate the platform launchers.

With the current Maven defaults the output files are named after `glotaran-app-<version>`. The 1.5.1 release assets have different names, so they were renamed by hand before upload:

```text
Glotaran-1.5.1.for.Windows.installer.exe
Glotaran-1.5.1.for.Linux.installer.sh
```

### Verified behaviour

On Windows 11 with Temurin JDK 8.0.492 and Maven 3.9.14, the goal:

- found and read the standalone ZIP;
- compiled the NBI engine and the Glotaran configuration logic;
- created `application/target/installer/` and `application/target/installerbuild/`;
- packed the application data and created an intermediate registry;
- logged warnings about missing translation directories and a missing `application/license.txt`;
- exited with code zero;
- produced no `.exe` or `.sh` in `application/target/`.

The installer build is therefore broken, although Maven reports success. A repaired release job must check that the expected files exist and are not empty, and then install and start them on each target OS.

The installers do not include Java, R, TIMP, or Rserve. The installer text asks for an existing JDK; running an analysis also requires R with TIMP and Rserve.

## Windows launcher icons

The standalone application contains 32- and 64-bit Windows launchers with the generic NetBeans icon. Two optional profiles replace the icon with Resource Hacker and then update the standalone ZIP:

- `replace-icon-windows`: runs Resource Hacker directly through `cmd`.
- `replace-icon-unix`: runs Resource Hacker under Wine.

Both profiles are activated by the presence of a file, to work around an old Maven activation problem. They need these properties, normally set in `~/.m2/settings.xml`:

```text
resourcehacker.installdir
resourcehacker.installdir.unix
wine.bottle
```

Neither the properties nor the tools are in the repository or in CI. The profiles modify the launchers in `application/target/glotaran/bin/` and update `glotaran-app-<version>.zip`. They do not build an installer.

## macOS `.app` and DMG

The macOS build is independent of NBI and is enabled with the `create-dmg` profile. In the `pre-integration-test` phase an Ant task:

1. Creates `Glotaran.app/Contents/MacOS` and `Contents/Resources`.
2. Copies the assembled `target/glotaran` tree into the app bundle.
3. Copies `glotaran.icns` and the filtered `Info.plist`.
4. Makes the Unix launcher executable and links it as the app executable.
5. Creates a Finder staging folder with a background image, a version-specific `.DS_Store`, the app, and a link to `/Applications`.
6. On macOS, runs `hdiutil create` to create the DMG.
7. On Linux, runs `mkisofs`, which creates an ISO image with a `.dmg` extension.

The file is named `Glotaran-1.5.dmg`, because the name uses `glotaran.branding.version` instead of the full Maven version. The `deploy-dmg` profile only attaches that file as a Maven artifact; it does not upload it to GitHub.

Code signing is commented out. The signing identity property is the generic `Developer ID Application`, and no team or certificate is configured. There is no notarization or stapling. The bundle signature is `????`. The Finder layout depends on `Glotaran-1.5.DS_Store.bak`. Before this profile can be used for a release, it needs to be repaired and tested on a macOS machine.

## Profiles

| Profile | Location | Effect | Status |
| --- | --- | --- | --- |
| `deployment` | root + application | Sources/Javadoc deploy support; ZIP and update site | ZIP and site verified |
| `release` | root + branding | Selects release repository paths and URLs | Mostly outdated; not needed for the ZIP |
| `release-extra` | root + application | Disables install/deploy/source/Javadoc and ZIP/update-site work | Old helper; does not build installers |
| `create-dmg` | application | Builds `.app` and DMG/ISO via Ant | Not verified; unsigned |
| `deploy-dmg` | application | Attaches an existing DMG to the Maven project | Does not publish |
| `replace-icon-windows` | application | Replaces launcher icons with Resource Hacker | Needs machine-specific settings |
| `replace-icon-unix` | application | Replaces launcher icons with Resource Hacker under Wine | Needs machine-specific settings |
| `export-javadoc` | root | Aggregates, packages, and deploys Javadoc | Refers to a missing assembly descriptor |
| `export-sources` | root | Packages and deploys a source archive | Refers to a missing assembly descriptor |

Known defects in these profiles:

- The release plugin requests a profile named `releases`; the profile is named `release`.
- Distribution management needs `${github.maven.repository}`, which is not defined in the repository.
- The release and export profiles refer to repository ID and URL properties that must be supplied from outside the repository.
- `src/assemble/javadoc.xml` and `src/assemble/sources.xml` do not exist.
- NBM signing refers to `src/keystore/keystore.ks`, which does not exist.
- The NBM license is configured as `../../COPYING.txt`, which resolves outside the checkout, so every build warns that it is missing. The license files are in `license/`.
- Every build warns that the NBMs are unsigned and that the update metadata cannot be validated against its DTD, because no updater JAR is configured.

## Branding and version numbers

Branding sources in `branding/src/main/resources` are filtered in `generate-resources` into `branding/src/main/nbm-branding`. The NBM plugin then puts the locale and branding JARs into the Glotaran cluster. They set the splash screen, main-window title, application name, and some NetBeans UI text.

The launcher configuration `application/src/main/resources/glotaran.conf` is filtered to `application/target/glotaran.conf` and installed as `target/glotaran/etc/glotaran.conf`. It sets:

- the user directory `.glotaran/1.5` (or the equivalent under macOS Application Support);
- the branding token `glotaran`;
- 256 MB initial and 1024 MB maximum heap;
- English/US locale and Swing/Java2D options;
- the Metal look and feel.

The version number of a release is set in several places. Check all of them:

- the root version and the parent version in every child POM;
- the SCM tag in the root POM;
- `glotaran.modules.specification.version` (currently 1.5.1);
- `glotaran.branding.version` (currently 1.5; also used for the user directory and the DMG name);
- the stable and beta update URLs in `CoreAUC`;
- the release text in the README;
- application and installer display names, and the release notes.

The Maven release plugin updates the POM versions only.

## CI and publication

`.github/workflows/maven.yml` runs on pushes and pull requests for `main`, `maintenance/v1.5.x`, and `maintenance/v1.6.x`. It uses Ubuntu, Temurin 8, the Maven dependency cache, and the root `clean verify` command.

It does not:

- enable the `deployment` profile;
- upload the portable ZIP or the update site as workflow artifacts;
- run tests (the POM skips them);
- build NBI installers;
- use Windows or macOS runners;
- sign or notarize artifacts;
- create tags or GitHub releases;
- deploy Maven artifacts or copy the update site to glotaran.org.

All release steps beyond the ordinary build were done by hand.

## Removed build systems

The packaging rewrite of February 2015 (commit `38a3ceb`) introduced the Maven/NBI installer template and the current macOS and branding resources. The same commit removed:

- the top-level NetBeans Ant `build.xml` and its `build-mac` override;
- an `installer/izpack/` tree with IzPack scripts and native helper DLLs;
- the Java Web Start descriptors `master.jnlp` and `branding.jnlp`;
- the older top-level layout for graphics and installer resources.

These explain old release artifacts and documentation that mention `setup.jar`, IzPack, JNLP, or NetBeans Ant suite targets. None of them is part of the current build; the installer implementation is the Maven NBI setup described above.

## Published releases

According to the GitHub release API, the only release is `v1.5.1`, published on 2015-02-11 with four assets:

```text
Glotaran-1.5.1.for.Linux.installer.sh
Glotaran-1.5.1.for.Max.OS.X.app.dmg
Glotaran-1.5.1.for.Windows.installer.exe
Glotaran-1.5.1.OS.independent.Java.zip
```

GitHub also provides its automatic source archives. The misspelling `Max` is in the original asset name.

Commit `ace5cae` is the Maven "prepare release v1.5.2" commit; it changes every module from `1.5.2-SNAPSHOT` to `1.5.2`. There is no `v1.5.2` tag and no 1.5.2 GitHub release. The README calls 1.5.2 the latest stable release, while the legacy page on glotaran.org and the GitHub releases page list 1.5.1. The 1.5.2 source was prepared but never distributed publicly.

## Verified state on 2026-07-04

Environment:

```text
Windows 11 amd64
Temurin OpenJDK 8.0.492
Apache Maven 3.9.14
```

| Check | Result |
| --- | --- |
| Root `clean verify` | Success, all 34 projects |
| NetBeans dependency integrity | Passed |
| Tests | Skipped by configuration |
| Unpacked application | Created |
| `package -Pdeployment` | Success |
| Portable ZIP | Created, about 49 MB |
| Autoupdate site | Created, 117 NBMs plus XML catalogs |
| NBI `build-installers` | Maven success but no installer files |
| macOS DMG | Cannot be built or checked on a Windows host |
| Signing/notarization | Not configured |

The ordinary build also logs many compiler warnings, missing-license warnings, unsigned-NBM warnings, and deprecation warnings from NBM plugin 3.14. None of them fails the build.

The same results were reproduced on 2026-09-27; see [ROADMAP.md](ROADMAP.md).

## Recommended path for a final 1.5.x release

The verified standalone ZIP, published as a GitHub release asset, is the release with the least risk. It runs on every platform that has Java 8; analyses additionally require R with TIMP and Rserve.

For a release with native installers:

1. Make the root build and an enabled smoke test pass.
2. Build with `-Pdeployment` and test the ZIP on Windows, Linux, and macOS.
3. Repair NBI or replace only the installer wrapper. Keep the RELEASE802 application as it is.
4. Add checks that fail the build if an installer file is missing or empty.
5. Build and sign the Windows artifacts on Windows.
6. Rebuild the app bundle on macOS, then sign, notarize, staple, and test it on the supported macOS versions.
7. Decide whether to revive the in-app update center. If so, publish the generated site at the URL in `CoreAUC` and test update discovery from a clean 1.5.1 user directory.
8. Create the Git tag and GitHub release only after all artifact checks pass.

The planned notice pointing users to glotaran.org and pyglotaran can go into an existing Glotaran module and ship with the standalone ZIP without repairing the installers. Platform installers can be added later, for each platform where they can be built and tested.

## Release checklist

### Before building

- Confirm JDK 8 and Maven 3.x.
- Confirm a clean Git worktree on the intended maintenance branch.
- Choose the release version and update every place listed under "Branding and version numbers".
- Decide between ZIP only and native installers.
- Decide whether to publish the update site.
- Add or enable the smoke tests for the user-facing notice.

### Build and inspect

- Run the root `clean verify` and keep the full log.
- Run the root `package -Pdeployment`.
- Inspect the ZIP contents and start the extracted application on each supported OS.
- If updates are in scope, inspect `updates.xml` and test it with a local HTTP server.
- If building NBI installers, check that the `.exe` and `.sh` files exist and are not empty, regardless of the Maven exit code.
- If building a DMG, check the bundle metadata, signing, notarization, Gatekeeper, first launch, and Finder layout on macOS.

### Publish

- Create and push the Git tag.
- Create a GitHub release and upload the artifacts with checksums.
- Publish the update-site files separately, if planned.
- Update glotaran.org and the README so that both name the same latest version.
- Test all public download links from a clean machine.
