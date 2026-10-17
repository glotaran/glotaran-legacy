# Building Glotaran release artifacts

This describes how the release artifacts of Glotaran 1.6 and later are produced: the autoupdate site, the generic zip, and the Windows, Linux and macOS installers. The 1.6.0 release was built by the `Release installers` workflow described below. The manual steps were worked out in September 2026 on Windows 11 with WSL2 Ubuntu; the Linux steps run on any Linux machine as well.

## Overview

| Artifact | Tool | Where it runs |
|---|---|---|
| Autoupdate site (`updates.xml` + NBMs) | Maven, `deployment` profile | any OS |
| Generic zip (no Java included) | Maven, `deployment` profile | any OS |
| Windows installer (`.exe`, bundled JRE) | nbpackage `windows-innosetup` + Inno Setup 6 | Windows (or Linux with wine) |
| Linux `.deb` / `.rpm` (bundled JRE) | nbpackage `linux-deb` / `linux-rpm` | Linux or WSL |
| macOS `Glotaran.app` zips (bundled JRE, unsigned) | shell script below | Linux or WSL |

The root pom still contains the old NBI installer settings (`templateFile`, `userSettings`, `installerOs*`). They are unused: the `build-installers` goal was removed from `org.apache.netbeans.utilities:nbm-maven-plugin` and does not exist in 14.5. Installers are built with [Apache NetBeans nbpackage](https://github.com/apache/netbeans-nbpackage) instead.

## Automated build (GitHub Actions)

[`.github/workflows/release.yml`](.github/workflows/release.yml) builds all artifacts on GitHub-hosted runners.

Triggers:

- Pushing a tag matching `v*` (for example `v1.6.0` or `v1.6.0-rc.1`). Creating a release in the GitHub web UI with a new `v*` tag also pushes the tag and starts the workflow. A release created for a tag that already exists does not.
- Manually (Actions > Release installers > Run workflow). GitHub only offers this once the workflow file is on the default branch. Manual runs keep the files as workflow artifacts on the run page and create no release.
- Pull requests that change `.github/workflows/release.yml`, `.github/scripts/smoke-test.sh`, `.github/scripts/fetch-micromamba.sh` or the R runtime's explicit files (`TIMPController/src/main/resources/org/glotaran/timpcontroller/runtime/`). These runs build and test everything and create no release.

Jobs:

| Job | Runner | Output |
|---|---|---|
| `build` | `ubuntu-latest` | `Glotaran-<version>-generic.zip`, `Glotaran-<version>-autoupdate-site.zip` |
| `windows` | `windows-latest` | `Glotaran-<version>-windows-x64-setup.exe` |
| `linux` | `ubuntu-latest` | `Glotaran-<version>-linux-amd64.deb`, `Glotaran-<version>-linux-x86_64.rpm` |
| `macos` | `macos-latest` (Apple silicon) for aarch64, `macos-26-intel` for x64 | `Glotaran-<version>-macos-aarch64.dmg`, `Glotaran-<version>-macos-x64.dmg` |
| `release` | `ubuntu-latest`, tags only | `SHA256SUMS`; uploads everything to a draft release for the tag |

Smoke tests: the `windows` job installs the setup `.exe` silently, the `linux` job installs the `.deb` (and starts it under `xvfb-run`), and each `macos` job mounts its DMG. Each then starts Glotaran the way a user would and runs [`.github/scripts/smoke-test.sh`](.github/scripts/smoke-test.sh). The script waits until the main window is up, stops the application, prints `messages.log`, and fails the job if the bundled runtime was not used, if not all Glotaran modules that are enabled by default were turned on, if anything was logged at SEVERE level, or if the bundled micromamba does not run. The `.rpm` is not installed; it contains the same files as the `.deb`.

The `release` job creates the draft release if none exists for the tag, marks it as a pre-release when the version has a suffix (`-rc.1`), and otherwise replaces the files on the existing release. Review the draft and publish it by hand.

Versions: `<version>` is the tag without the leading `v`; manual runs use the Maven project version. Installer metadata (Inno Setup, deb, rpm, `Info.plist`) gets only the numeric part, so `v1.6.0-rc.1` produces packages that report 1.6.0. The workflow does not change the Maven project version; set it in the poms before tagging.

Runtime: every installer bundles the latest Temurin 25 JRE installed by `actions/setup-java`. The same JRE runs nbpackage.

micromamba: every installer also bundles the micromamba for its platform at `<installation>/micromamba/micromamba[.exe]`, which Glotaran uses to install its R (see [R runtime and micromamba](#r-runtime-and-micromamba)). `.github/scripts/fetch-micromamba.sh` downloads it and checks the SHA-256 pinned in `Micromamba.java`; the `windows` and `linux` jobs add it to a copy of the generic zip before running nbpackage, and the `macos` jobs copy it into `Glotaran.app`. The generic zip has no micromamba; Glotaran downloads the pinned version itself when it is not bundled.

Configuration: set the repository variable `PACKAGE_MAINTAINER` (Settings > Secrets and variables > Actions > Variables) to `Name <email>`. It fills the deb and rpm maintainer fields; without it the `.deb` lacks the mandatory `Maintainer` field.

Not automated: NBM signing, uploading the autoupdate site to glotaran.org, and macOS code signing and notarization.

To try the workflow without making a release, push a pre-release tag such as `v1.6.0-rc.1`, check the draft release, then delete the draft and the tag (`git push --delete origin v1.6.0-rc.1`).

The sections below describe the same steps for a build by hand.

## Prerequisites

- JDK 25 (Temurin) to build. Set `JAVA_HOME` explicitly if another JDK is first on `PATH`.
- Maven 3.9+.
- nbpackage 1.0: `org.apache.netbeans:nbpackage:1.0`, classifier `bin`, type `zip`, from Maven Central (`https://repo1.maven.org/maven2/org/apache/netbeans/nbpackage/1.0/nbpackage-1.0-bin.zip`). Check it against the `.sha1` next to it. Run it with JDK 17+.
- Windows installer: Inno Setup 6 (`winget install JRSoftware.InnoSetup`), which provides `ISCC.exe`.
- Linux packages: `dpkg`, `dpkg-deb`, `fakeroot` for `.deb`; `rpmbuild` (package `rpm` on Ubuntu) for `.rpm`; `zip` for the macOS zips.
- Temurin 25 JREs for Linux x64 and macOS aarch64/x64, from the [Adoptium API](https://api.adoptium.net/v3/assets/latest/25/hotspot?image_type=jre&vendor=eclipse&os=mac&architecture=aarch64) (change `os` / `architecture`). Verify each download against the `checksum` field in the API response.

## Line endings

`application/src/main/resources/glotaran.conf` ends up as `etc/glotaran.conf` in every artifact and is sourced by `/bin/sh` on Linux and macOS. With CRLF line endings the launcher fails with `Cannot find java. Please use the --jdkhome switch.` On a Windows checkout with `core.autocrlf=true` git produces CRLF unless `.gitattributes` says otherwise; the repository's `.gitattributes` forces LF for `*.conf`. If the file was checked out before that rule existed, re-check it out:

```sh
rm application/src/main/resources/glotaran.conf
git checkout -- application/src/main/resources/glotaran.conf
```

Verify after the build: `unzip -p application/target/glotaran-app-*.zip glotaran/etc/glotaran.conf | file -` must not report CRLF.

## Version numbers

The version of a release is set in several places:

- the root version and the parent version in every child POM (`release.yml` does not change them; set them before tagging);
- `glotaran.modules.specification.version` in the root POM: the `OpenIDE-Module-Specification-Version` of every Glotaran module (see the autoupdate notes below);
- `glotaran.branding.version` in the root POM (`1.7`): the user directory `.glotaran/<branding version>` (`Library/Application Support/glotaran/<branding version>` on macOS) set in `application/src/main/resources/glotaran.conf`;
- the update center URL in `CoreAUC/src/main/resources/org/glotaran/auc/Bundle.properties` (`/suc/<branding version>/stable/`);
- `docs/release-notes/<version>.md`, which `release.yml` uses as the release text;
- the README.

Branding sources in `branding/src/main/resources` are filtered into `branding/src/main/nbm-branding` and set the splash screen, main-window title and application name from the project version. The Inno Setup app ID (`Glotaran-<major>.<minor>`) and the installer metadata versions are derived from the tag in `release.yml`.

For a new release line (for example 1.7), change `glotaran.branding.version` and the update center URL as well, so that the new line uses its own user directory and update center and can be installed next to the previous one.

## R runtime and micromamba

Glotaran 1.7 installs R, TIMP and Rserve from conda-forge with micromamba; see [docs/R_RUNTIME_DESIGN.md](docs/R_RUNTIME_DESIGN.md).

- The R runtime is locked in `TIMPController/src/main/r-runtime/pixi.toml` and `pixi.lock`. `export-specs.ps1` in that folder writes one conda explicit file per platform to `TIMPController/src/main/resources/org/glotaran/timpcontroller/runtime/<platform>.txt`, which the build packs into the module. Run `pwsh export-specs.ps1 -Update` after changing `pixi.toml` (needs [pixi](https://pixi.sh)) and commit `pixi.toml`, `pixi.lock` and the four explicit files together. The SHA-256 of an explicit file is the runtime ID: a release with a changed file makes every installation download and install the runtime again on its next analysis, so change the lock only when needed.
- Every explicit file must contain `r-timp`; without it the installation check (`library(TIMP)`) fails for every user. `r-timp` is not on conda-forge yet ([conda-forge/staged-recipes#35140](https://github.com/conda-forge/staged-recipes/pull/35140)), so the lock lists TIMP's dependencies and `export-specs.ps1` appends the `r-timp` package of each platform from `TIMPController/src/main/r-runtime/r-timp.txt`. These packages are built by the `r-timp packages` workflow (`.github/workflows/r-timp.yml`, recipe in `TIMPController/src/main/r-runtime/r-timp/`) and attached to the [glotaran/TIMP v1.13.6 release](https://github.com/glotaran/TIMP/releases/tag/v1.13.6). To replace them: run the workflow, download its artifacts, upload them to that release (`gh release upload v1.13.6 --repo glotaran/TIMP <files>`), put the new URLs and MD5 sums in `r-timp.txt` and run `export-specs.ps1`. Never replace an asset under the same name: installed explicit files refer to it by MD5. Once `r-timp` is on conda-forge, delete `r-timp.txt`, add `r-timp = "1.13.6.*"` to `pixi.toml` in place of TIMP's dependencies and run `export-specs.ps1 -Update`.
- The `r-runtime` job in `maven.yml` installs each platform's explicit file and starts Rserve; it must pass before a release.
- micromamba is pinned in `TIMPController/src/main/java/org/glotaran/timpcontroller/runtime/Micromamba.java`: `VERSION` (a tag of [mamba-org/micromamba-releases](https://github.com/mamba-org/micromamba-releases/releases)) and one SHA-256 per platform, taken from the `.sha256` file next to each release asset. `fetch-micromamba.sh` reads the pin from that file, so the installers and the download in Glotaran always use the same binary.

## 1. Autoupdate site and generic zip

```sh
mvn -B -Pdeployment clean install -DskipTests
```

Outputs in `application/target/`:

- `netbeans_site/` — `updates.xml`, `updates.xml.gz` and all NBMs. Upload the contents to the update center URL the application reads, currently `https://glotaran.org/suc/1.7/stable/updates.xml` (set in `CoreAUC/src/main/resources/org/glotaran/auc/Bundle.properties`; the `glotaran.update.center.*` properties in the root pom are not used for this). For a release, take the site from `Glotaran-<version>-autoupdate-site.zip` on the GitHub release (the files are in its `netbeans_site/` folder) so the modules match the installers. Without a catalog at that URL, every installation of that release line logs a failed update check at startup. Put each release line only in its own folder (`/suc/1.6/`, `/suc/1.7/`, …). Never put modules of 1.6 or later in `/suc/1.5/` or `/uc/1.5/`: 1.5 installations run on Java 8 and NetBeans Platform 8.0.2 and would install modules they cannot load.
- `glotaran-app-<version>.zip` — the generic zip. It is also the input for all installers below.

Signing: the NBMs are signed only when `src/keystore/keystore.ks` exists and `-Dkeystore.password=...` is passed (alias `glotaran`, see the `nbm-maven-plugin` configuration in the root pom). The keystore is not in the repository; without it the build warns and produces unsigned NBMs.

Autoupdate only offers modules whose specification version is higher than the installed one. The Glotaran modules take theirs from `glotaran.modules.specification.version` in the root pom, not from the project version; raise it for every release that should reach existing installs through autoupdate.

Moving the update center to another URL:

- The NetBeans update client does not follow a redirect from HTTP to HTTPS. A URL that installations already use must keep being served as it is.
- NetBeans keeps a URL the user edited (the `url` key under `config/Preferences/org/netbeans/modules/autoupdate/` in the user directory) in preference to the one the module registers. A new URL therefore needs a new update-center key in `CoreAUC`. This is why 1.5.3 replaced `org_glotaran_auc_update_center` (`http://glotaran.org/uc/`) with `org_glotaran_auc_secure_update_center` (`https://glotaran.org/suc/`). glotaran.org still serves the 1.5.3 site at `/uc/1.5/stable/` over plain HTTP, for 1.5.1 installations that have not updated yet.

## 2. Windows installer

The CI workflow bundles the Temurin 25 JRE as downloaded. For a manual build, either extract that JRE or make a runtime with `jlink` from the build JDK, leaving out development tools, Graal and incubator modules (about 136 MB unpacked):

```sh
MODS=$("$JAVA_HOME/bin/java" --list-modules | sed 's/@.*//' \
  | grep -v -E '^jdk\.(jcmd|jconsole|jdeps|jdi|jdwp\.agent|javadoc|jshell|jlink|jpackage|jartool|jstatd|hotspot\.agent|internal\.le|internal\.opt|internal\.ed|editpad|compiler|internal\.jvmstat|random|graal\..*|internal\.vm\.ci|incubator\..*)$' \
  | paste -sd,)
"$JAVA_HOME/bin/jlink" --add-modules "$MODS" --strip-debug --no-man-pages --no-header-files --output rt-win
```

Add micromamba to a copy of the generic zip (Git Bash; `7z` from 7-Zip):

```sh
bash .github/scripts/fetch-micromamba.sh win-64 stage/glotaran/micromamba/micromamba.exe
cp application/target/glotaran-app-<version>.zip glotaran-windows.zip
(cd stage && 7z a -tzip ../glotaran-windows.zip glotaran/micromamba/micromamba.exe)
```

Then pass `glotaran-windows.zip` instead of the generic zip to nbpackage (paths passed to nbpackage must be Windows paths when run on Windows):

```sh
nbpackage-1.0/bin/nbpackage -t windows-innosetup \
  -i glotaran-windows.zip -o out \
  -Pname=Glotaran -Pversion=1.6.0 -Pruntime=rt-win \
  -Ppublisher="Vrije Universiteit Amsterdam" -Purl=https://glotaran.org \
  -Pdescription="Glotaran - global and target analysis of time-resolved spectroscopy data" \
  -Pinnosetup.tool="<path to>/ISCC.exe" \
  -Pinnosetup.appid=Glotaran-1.6 \
  -Pinnosetup.icon=application/src/main/app-resources/graphics/glotaran.ico \
  -Pinnosetup.license=license/gpl-3.0.txt
```

Result: `out/Glotaran-1.6.0.exe`. It installs per machine (admin rights) into `Program Files\Glotaran` and starts `bin\glotaran64.exe --jdkhome <app>\jdk`. `package.version` must be numeric (Inno Setup version info), so pass `1.6.0` rather than `1.6.0-SNAPSHOT`. Keep `innosetup.appid` stable within a release line so upgrades replace the previous install.

The `glotaran.exe` / `glotaran64.exe` launchers keep the NetBeans icon unless ResHacker is configured for the `replace-icon-windows` / `replace-icon-unix` profiles in `application/pom.xml`; the Start menu and desktop shortcuts use `glotaran.ico` either way.

## 3. Linux .deb and .rpm

Run on Linux (or in WSL, working in the Linux file system so permissions are preserved), with the Temurin Linux x64 JRE extracted to `jre/` and nbpackage run with that JRE. First add micromamba to a copy of the generic zip:

```sh
bash .github/scripts/fetch-micromamba.sh linux-64 stage/glotaran/micromamba/micromamba
cp glotaran-app-<version>.zip glotaran-linux.zip
(cd stage && zip -q ../glotaran-linux.zip glotaran/micromamba/micromamba)
```

Then build both packages from `glotaran-linux.zip`:

```sh
export JAVA_HOME=$PWD/jre
G=application/src/main/app-resources/graphics   # adjust to the repository location
COMMON=(-i glotaran-linux.zip -o out -Pname=Glotaran -Pversion=1.6.0 -Pruntime=$PWD/jre
        -Ppublisher="Vrije Universiteit Amsterdam" -Purl=https://glotaran.org
        -Pdescription="Glotaran - global and target analysis of time-resolved spectroscopy data")

nbpackage-1.0/bin/nbpackage -t linux-deb "${COMMON[@]}" -Parch=amd64 \
  -Pdeb.icon=$G/glotaran_icon256.png -Pdeb.svg-icon=$G/glotaran_icon.svg \
  "-Pdeb.category=Science;Education;" "-Pdeb.maintainer=Name <email>"

nbpackage-1.0/bin/nbpackage -t linux-rpm "${COMMON[@]}" -Parch=x86_64 \
  -Prpm.icon=$G/glotaran_icon256.png -Prpm.svg-icon=$G/glotaran_icon.svg \
  "-Prpm.category=Science;Education;" "-Prpm.maintainer=Name <email>" \
  "-Prpm.license=CDDL-1.0 AND GPL-3.0" -Prpm.group=Applications/Engineering
```

Notes:

- Pass `-Parch` explicitly; nbpackage cannot detect it from the JRE and otherwise builds an `all` / `noarch` package that still contains an x64 JRE.
- `deb.maintainer` is mandatory for a valid Debian control file.
- When calling through `wsl.exe` from Windows, put the commands in a script file: `wsl.exe` passes arguments through a shell, which splits on `;` and expands `$VAR` on the Windows side.

The packages install to `/usr/lib/glotaran`, with `/usr/bin/glotaran` and a desktop entry. Quick check: `dpkg -i`, run `/usr/bin/glotaran`, confirm `Java Home = /usr/lib/glotaran/jdk` in `~/.glotaran/<version>/var/log/messages.log`, then `dpkg -r glotaran`.

## 4. macOS app bundles

nbpackage's `macos-pkg` type needs `pkgbuild` and `codesign`, which exist only on macOS. Without a Mac, build an unsigned `Glotaran.app` per architecture with a script launcher and a bundled Temurin macOS JRE, and zip it on Linux (Info-ZIP keeps the executable bits and symlinks):

```sh
APP=Glotaran.app/Contents
mkdir -p "$APP/MacOS" "$APP/Resources/glotaran/jre"
unzip -q glotaran-app-<version>.zip -d "$APP/Resources"
tar xzf OpenJDK25U-jre_<arch>_mac_hotspot_<ver>.tar.gz -C "$APP/Resources/glotaran/jre" --strip-components=1
# osx-arm64 for aarch64, osx-64 for x64
bash .github/scripts/fetch-micromamba.sh osx-arm64 "$APP/Resources/glotaran/micromamba/micromamba"
# Info.plist is generated by the Maven build; set the release version
sed -e 's/1\.6\.0-SNAPSHOT/1.6.0/' application/target/Info.plist > "$APP/Info.plist"
cp application/src/main/app-resources/graphics/glotaran.icns "$APP/Resources/"
cat > "$APP/MacOS/glotaran" <<'EOF'
#!/bin/sh
CONTENTS="$(cd "$(dirname "$0")/.." && pwd)"
exec "$CONTENTS/Resources/glotaran/bin/glotaran" --jdkhome "$CONTENTS/Resources/glotaran/jre/Contents/Home" "$@"
EOF
chmod 755 "$APP/MacOS/glotaran" "$APP/Resources/glotaran/bin/glotaran"
zip -qry Glotaran-1.6.0-macos-<arch>.zip Glotaran.app
```

`<arch>` is `aarch64` (Apple silicon) or `x64` (Intel). `CFBundleExecutable` in `Info.plist` is `glotaran`, matching the launcher name.

The CI workflow builds the same bundle on a macOS runner, with two differences: it copies only the JRE's `Contents/Home` to `glotaran/jre` (so the launcher passes `--jdkhome .../glotaran/jre`), and it packs the bundle plus an `Applications` link into a DMG with `hdiutil create -format UDZO`.

The bundle is not signed or notarized. After downloading, users must run `xattr -dr com.apple.quarantine /Applications/Glotaran.app` (or right-click > Open) before macOS lets it start. These manually built zips have not been tested on a Mac; the DMGs built by the workflow are started on GitHub's macOS runners. On a Mac, `nbpackage -t macos-pkg` with `-Pmacos.codesign-id` / `-Pmacos.pkgbuild-id` produces a signed `.pkg` instead; the `create-dmg` profile in `application/pom.xml` is the older route to a `.dmg`.

## Checksums

Publish SHA-256 checksums with the downloads:

```sh
sha256sum *.zip *.exe *.deb *.rpm > SHA256SUMS
```
