param(
  [Parameter(Mandatory = $true)][string]$BaseJar,
  [Parameter(Mandatory = $true)][string]$OutputJar
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$base = (Resolve-Path -LiteralPath $BaseJar).Path
$output = [System.IO.Path]::GetFullPath($OutputJar)
if ($base -eq $output) { throw 'OutputJar must differ from BaseJar.' }
$javac = (Get-Command javac -ErrorAction Stop).Source
$jar = (Get-Command jar -ErrorAction Stop).Source
$version = (& $javac -version 2>&1 | Out-String)
if ($version -notmatch 'javac (2[1-9]|[3-9][0-9])\.') { throw 'JDK 21 or newer is required.' }

$build = Join-Path $repo 'legacy-java\.build'
$resolvedBuild = [System.IO.Path]::GetFullPath($build)
if (-not $resolvedBuild.StartsWith($repo + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
  throw 'Build directory is outside the repository.'
}
if (Test-Path -LiteralPath $resolvedBuild) { Remove-Item -LiteralPath $resolvedBuild -Recurse -Force }
New-Item -ItemType Directory -Force -Path $resolvedBuild | Out-Null

$source = Join-Path $repo 'legacy-java\src\main\java\com\warproject\launcher'
$files = @('ProjectBattleAlpha.java', 'DiscordPresenceService.java', 'PackMaintenance.java', 'PackDownloadManager.java') |
  ForEach-Object { Join-Path $source $_ }
& $javac -encoding UTF-8 -cp $base -d $resolvedBuild @files
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }

$parent = Split-Path -Parent $output
New-Item -ItemType Directory -Force -Path $parent | Out-Null
Copy-Item -LiteralPath $base -Destination $output -Force
& $jar uf $output -C $resolvedBuild com/warproject/launcher
if ($LASTEXITCODE -ne 0) { throw 'JAR update failed.' }
$resources = Join-Path $repo 'legacy-java\src\main\resources'
& $jar uf $output -C $resources assets/project-battle-parallax-background.png `
  -C $resources assets/project-battle-parallax-foreground.png `
  -C $resources assets/zero-point-games-logo.png `
  -C $resources assets/project-battle-menu-logo.png `
  -C $resources assets/profile-pixel-avatar.png
if ($LASTEXITCODE -ne 0) { throw 'JAR asset update failed.' }
Write-Output $output
