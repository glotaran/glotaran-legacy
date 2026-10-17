#!/usr/bin/env bash
# Downloads the micromamba that Micromamba.java pins for a conda platform and checks its SHA-256,
# so the installers bundle the same binary that Glotaran would otherwise download itself.
#
# Usage: fetch-micromamba.sh PLATFORM TARGET
#   PLATFORM  win-64, linux-64, osx-64 or osx-arm64
#   TARGET    file to write, for example glotaran/micromamba/micromamba.exe
set -euo pipefail

PLATFORM="$1"
TARGET="$2"
SRC="$(dirname "$0")/../../TIMPController/src/main/java/org/glotaran/timpcontroller/runtime/Micromamba.java"
VERSION=$(sed -n 's/.*String VERSION = "\([^"]*\)";.*/\1/p' "$SRC")
SHA256=$(sed -n "s/.*\"$PLATFORM\", \"\([0-9a-f]\{64\}\)\".*/\1/p" "$SRC")
if [ -z "$VERSION" ] || [ -z "$SHA256" ]; then
  echo "::error::No micromamba pin for $PLATFORM in $SRC"
  exit 1
fi

mkdir -p "$(dirname "$TARGET")"
curl -fsSL --retry 3 -o "$TARGET" \
  "https://github.com/mamba-org/micromamba-releases/releases/download/$VERSION/micromamba-$PLATFORM"
if command -v sha256sum > /dev/null; then
  echo "$SHA256  $TARGET" | sha256sum -c -
else
  echo "$SHA256  $TARGET" | shasum -a 256 -c -
fi
chmod 755 "$TARGET"
echo "micromamba $VERSION for $PLATFORM: $TARGET"
