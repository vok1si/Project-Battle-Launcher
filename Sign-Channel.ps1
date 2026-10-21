param(
 [Parameter(Mandatory=$true)][string]$Version,
 [Parameter(Mandatory=$true)][string]$PackagePath,
 [Parameter(Mandatory=$true)][string]$DownloadUrl,
 [Parameter(Mandatory=$true)][string]$Entry,
 [Parameter(Mandatory=$true)][string]$Notes,
 [Parameter(Mandatory=$true)][string]$OutputPath
)
$ErrorActionPreference='Stop'
if($DownloadUrl -notmatch '^https://github\.com/vok1si/project-battle-pack-NOW/releases/download/') { throw 'Package URL must point to the Project Battle GitHub releases.' }
if(!(Test-Path -LiteralPath $PackagePath -PathType Leaf)) { throw 'Package file not found.' }
$keyPath=Join-Path $env:LOCALAPPDATA 'ProjectBattleRelease\launcher-channel-key.dpapi'
if(!(Test-Path -LiteralPath $keyPath -PathType Leaf)) { throw 'Local protected release key not found.' }
$sealed=[IO.File]::ReadAllBytes($keyPath)
$plain=[Security.Cryptography.ProtectedData]::Unprotect($sealed,$null,[Security.Cryptography.DataProtectionScope]::CurrentUser)
$privateXml=[Text.Encoding]::UTF8.GetString($plain)
$rsa=[Security.Cryptography.RSACryptoServiceProvider]::new()
try {
 $rsa.FromXmlString($privateXml)
 $hash=(Get-FileHash -LiteralPath $PackagePath -Algorithm SHA256).Hash.ToLowerInvariant()
 $size=(Get-Item -LiteralPath $PackagePath).Length
 $payload=[ordered]@{version=$Version;url=$DownloadUrl;sha256=$hash;entry=$Entry;notes=$Notes;size=$size} | ConvertTo-Json -Compress
 $bytes=[Text.Encoding]::UTF8.GetBytes($payload)
 $signature=$rsa.SignData($bytes,[Security.Cryptography.CryptoConfig]::MapNameToOID('SHA256'))
 $envelope=[ordered]@{payload=[Convert]::ToBase64String($bytes);signature=[Convert]::ToBase64String($signature)} | ConvertTo-Json -Compress
 [IO.File]::WriteAllText($OutputPath,$envelope,[Text.UTF8Encoding]::new($false))
 [pscustomobject]@{Version=$Version;Size=$size;SHA256=$hash;ChannelFile=$OutputPath}
} finally { $rsa.Dispose(); [Array]::Clear($plain,0,$plain.Length) }
