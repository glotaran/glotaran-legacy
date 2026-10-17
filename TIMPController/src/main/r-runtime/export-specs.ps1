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
# Until r-timp is on conda-forge, the r-timp package listed in r-timp.txt is appended to each file.

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
        # r-timp is not on conda-forge yet; append the package that r-timp.txt lists for this platform
        $rTimp = Join-Path $PSScriptRoot 'r-timp.txt'
        if (Test-Path $rTimp) {
            $line = Get-Content $rTimp | Where-Object { $_ -match "^$([regex]::Escape($platform))\s+(\S+)$" } | ForEach-Object { $Matches[1] }
            if (-not $line) { throw "r-timp.txt has no package for $platform" }
            [IO.File]::AppendAllText((Join-Path $target "$platform.txt"), "$line`n", [Text.Encoding]::ASCII)
        }
    }
}
finally {
    Remove-Item -Recurse -Force $staging -ErrorAction SilentlyContinue
}

Get-ChildItem $target -Filter '*.txt' | Format-Table Name, Length
