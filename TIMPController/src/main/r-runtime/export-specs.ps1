# Exports pixi.lock to the conda explicit files that Glotaran installs with micromamba:
# TIMPController/src/main/resources/org/glotaran/timpcontroller/runtime/<platform>.txt
#
# Requires pixi (https://pixi.sh). Maven does not run this script; commit pixi.toml,
# pixi.lock and the exported files together.
#
#   pwsh export-specs.ps1           export the current lock
#   pwsh export-specs.ps1 -Update   re-solve the lock first (pixi update --no-install)
#
# A changed explicit file changes the runtime ID, so users get a new environment.

param(
    [switch] $Update,
    [string] $Pixi = 'pixi'
)

$ErrorActionPreference = 'Stop'
$platforms = 'win-64', 'linux-64', 'osx-64', 'osx-arm64'
$manifest = Join-Path $PSScriptRoot 'pixi.toml'
$target = Join-Path $PSScriptRoot '../resources/org/glotaran/timpcontroller/runtime'
$staging = Join-Path ([IO.Path]::GetTempPath()) "glotaran-r-runtime-$PID"

if ($Update) {
    & $Pixi update --no-install --manifest-path $manifest
    if ($LASTEXITCODE -ne 0) { throw "pixi update failed ($LASTEXITCODE)" }
}

New-Item -ItemType Directory -Force -Path $staging, $target | Out-Null
try {
    # --locked fails when pixi.lock does not match pixi.toml instead of re-solving.
    & $Pixi workspace export conda-explicit-spec --locked --manifest-path $manifest $staging
    if ($LASTEXITCODE -ne 0) { throw "pixi workspace export failed ($LASTEXITCODE)" }
    foreach ($platform in $platforms) {
        $spec = Join-Path $staging "default_${platform}_conda_spec.txt"
        if (-not (Test-Path $spec)) { throw "pixi did not export $platform" }
        Copy-Item $spec (Join-Path $target "$platform.txt") -Force
    }
}
finally {
    Remove-Item -Recurse -Force $staging -ErrorAction SilentlyContinue
}

Get-ChildItem $target -Filter '*.txt' | Format-Table Name, Length
