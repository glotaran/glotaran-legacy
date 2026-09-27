# Glotaran - A tool for Global and Target Analysis

[Glotaran](http://glotaran.org) is a free software program developed for global and target analysis of time-resolved spectroscopy and microscopy data.

A publication about Glotaran, with the title "Glotaran: a Java-based graphical user interface for the R-package TIMP" has been published in the Journal of Statistical Software, on July 1st 2012 and can be found here: http://www.jstatsoft.org/v49/i03/

Glotaran is open-source software and therefore free to download and free to use. If you use the software for any scientific publication we do however request that you cite our JSS publication (see below). Also, if you use Glotaran in any way - in research, industry or education - we are always happy to hear how!

## Install and use Glotaran

Download and [Install](http://glotaran.org/wiki/doku.php?id=installation) Glotaran on your computer.

Get started with the [Wiki](http://glotaran.org/wiki.html) and watch a [Screencast](http://glotaran.org/demonstration.html).

## Development

To build, run, and debug the application, see [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md). Contributor guidelines are in [CONTRIBUTING.md](CONTRIBUTING.md).

The Maven build, packaging, installers, CI, and release process are described in [docs/BUILD_AND_DISTRIBUTION.md](docs/BUILD_AND_DISTRIBUTION.md).

Planned releases (1.5.3, 1.6, 1.7, 2.0) are described in [docs/ROADMAP.md](docs/ROADMAP.md).

## Deprecation notice

The 1.0.0 version of Glotaran was released over a decade ago, and the creators of Glotaran think it's time for a change.

For the past few years they have been working on a (spiritual) successor to  Glotaran, re-written from the ground up in Python, called [pyglotaran](https://github.com/glotaran/pyglotaran). Please check it out!

## Latest releases

### Stable (1.5.x)

Latest stable release on [glotaran.org](http://glotaran.org/downloads) is version 1.5.2, the code lives on the [1.5.x](https://github.com/glotaran/glotaran-legacy/tree/maintenance/v1.5.x) branch.

The next release from this branch, 1.5.3, is the final maintenance release on NetBeans Platform 8.0.2 and Java 8.

### Planned releases

| Release | Branch | Main change |
| --- | --- | --- |
| 1.6.0 | [maintenance/v1.6.x](https://github.com/glotaran/glotaran-legacy/tree/maintenance/v1.6.x) | Port to Apache NetBeans 31 or later with a bundled Java 21 runtime |
| 1.7.0 | `maintenance/v1.7.x` | Glotaran installs and manages its own R, TIMP, and Rserve |
| 2.0.0 | `main` | pyglotaran replaces R and TIMP as the computational core |

Scope, branch setup, and acceptance criteria for each release are in [docs/ROADMAP.md](docs/ROADMAP.md).

## License

GPL v2 or later
