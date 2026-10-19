# Glotaran - A tool for Global and Target Analysis

[Glotaran](http://glotaran.org) is a free software program developed for global and target analysis of time-resolved spectroscopy and microscopy data.

A publication about Glotaran, with the title "Glotaran: a Java-based graphical user interface for the R-package TIMP" has been published in the Journal of Statistical Software, on July 1st 2012 and can be found here: http://www.jstatsoft.org/v49/i03/

Glotaran is open-source software and therefore free to download and free to use. If you use the software for any scientific publication we do however request that you cite our JSS publication (see below). Also, if you use Glotaran in any way - in research, industry or education - we are always happy to hear how!

## Install and use Glotaran

Download the installer for your system from the [GitHub Releases page](https://github.com/glotaran/glotaran-legacy/releases). The release notes describe how to install it and how to set up R, TIMP and Rserve for analyses.

Get started with the [Wiki](http://glotaran.org/wiki.html) and watch a [Screencast](http://glotaran.org/demonstration.html).

## Development

To build, run, and debug the application, see [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md). Contributor guidelines are in [CONTRIBUTING.md](CONTRIBUTING.md).

Packaging, installers, and the release process are described in [RELEASING.md](RELEASING.md).

Releases 1.5.3 to 2.0 are described in [docs/ROADMAP.md](docs/ROADMAP.md).

## Deprecation notice

The 1.0.0 version of Glotaran was released over a decade ago, and the creators of Glotaran think it's time for a change.

For the past few years they have been working on a (spiritual) successor to  Glotaran, re-written from the ground up in Python, called [pyglotaran](https://github.com/glotaran/pyglotaran). Please check it out!

## Latest releases

Latest stable release is version 1.7.0: Glotaran downloads and installs R, TIMP and Rserve itself and starts R when an analysis needs it, so you no longer install R or start Rserve yourself. The installers for Windows, Linux and macOS include Java. Download it from the [GitHub Releases page](https://github.com/glotaran/glotaran-legacy/releases).

`main` contains the latest release. The code and documentation of each release line live on their own branch:

| Release line | Platform | Branch |
| --- | --- | --- |
| 1.7.x (latest: 1.7.0) | Apache NetBeans 31, Java 25 | [maintenance/v1.7.x](https://github.com/glotaran/glotaran-legacy/tree/maintenance/v1.7.x) |
| 1.6.x (latest: 1.6.0) | Apache NetBeans 31, Java 25 | [maintenance/v1.6.x](https://github.com/glotaran/glotaran-legacy/tree/maintenance/v1.6.x) |
| 1.5.x (final: 1.5.3) | NetBeans 8.0.2, Java 8 | [maintenance/v1.5.x](https://github.com/glotaran/glotaran-legacy/tree/maintenance/v1.5.x) |

Version 1.5.3 was delivered as an update to 1.5.1 installations through Tools | Plugins. Version 1.5.1 and earlier are available on [glotaran.org](http://glotaran.org/downloads). Version 1.5.2 was never officially released; snapshot builds labelled 1.5.2 are superseded by 1.5.3.

### Planned releases

| Release | Branch | Main change |
| --- | --- | --- |
| 2.0.0 | `main` | pyglotaran replaces R and TIMP as the computational core |

Scope, branch setup, and acceptance criteria for each release are in [docs/ROADMAP.md](docs/ROADMAP.md).

## Building release artifacts

See [RELEASING.md](RELEASING.md) for building the autoupdate site, the generic zip and the Windows, Linux and macOS installers.

## License

GPL v2 or later
