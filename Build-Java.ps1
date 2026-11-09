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
$packageJar = Join-Path $resolvedBuild 'launcher-candidate.jar'
Copy-Item -LiteralPath $base -Destination $packageJar -Force
& $jar uf $packageJar -C $resolvedBuild com/warproject/launcher
if ($LASTEXITCODE -ne 0) { throw 'JAR update failed.' }
$resources = Join-Path $repo 'legacy-java\src\main\resources'
& $jar uf $packageJar -C $resources assets/project-battle-parallax-background.png `
  -C $resources assets/project-battle-parallax-foreground.png `
  -C $resources assets/zero-point-games-logo.png `
  -C $resources assets/project-battle-menu-logo.png `
  -C $resources assets/profile-pixel-avatar.png `
  -C $resources assets/account-identity-landscape.png `
  -C $resources assets/project-battle-development-0.1.png `
  -C $resources assets/project-battle-development-0.1.html `
  -C $resources assets/fonts/minecraft-regular.ttf `
  -C $resources assets/fonts/minecraft-ten.ttf `
  -C $resources assets/fonts/PressStart2P-Regular.ttf `
  -C $resources assets/fonts/OFL-PressStart2P.txt
if ($LASTEXITCODE -ne 0) { throw 'JAR asset update failed.' }
Move-Item -LiteralPath $packageJar -Destination $output -Force -ErrorAction Stop
Write-Output $output
