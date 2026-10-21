param([string]$Configuration='Release')
$ErrorActionPreference='Stop'
$root=$PSScriptRoot
$out=Join-Path $root 'dist'
New-Item -ItemType Directory -Force $out | Out-Null
$csc="$env:WINDIR\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
if(!(Test-Path -LiteralPath $csc)){throw "C# compiler not found: $csc"}
& $csc /nologo /target:winexe /platform:anycpu /codepage:65001 /optimize+ "/out:$out\Project Battle.exe" "/win32icon:$root\pb.ico" /reference:System.Drawing.dll /reference:System.Windows.Forms.dll /reference:System.Net.Http.dll /reference:System.Web.Extensions.dll /reference:System.IO.Compression.dll /reference:System.IO.Compression.FileSystem.dll "/resource:$root\public-key.xml,public-key.xml" "/resource:$root\pb.ico,pb.ico" "$root\Bootstrap.cs"
if($LASTEXITCODE -ne 0){throw 'Bootstrap compilation failed'}
& $csc /nologo /target:winexe /platform:anycpu /codepage:65001 /optimize+ "/out:$out\Project Battle Host.exe" "/win32icon:$root\pb.ico" /reference:System.Management.dll /reference:System.Windows.Forms.dll /reference:System.Web.Extensions.dll "$root\Host.cs"
if($LASTEXITCODE -ne 0){throw 'Host compilation failed'}
Get-ChildItem -LiteralPath $out -Filter '*.exe' | ForEach-Object { Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256 | Select-Object Path,Hash }

