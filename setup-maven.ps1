$mvnDir  = Join-Path $PSScriptRoot '.mvn\wrapper\dist'
$mvnZip  = Join-Path $PSScriptRoot '.mvn\wrapper\maven.zip'
$mvnUrl  = 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip'
$mvnBin  = Join-Path $mvnDir 'apache-maven\bin\mvn.cmd'

if (Test-Path $mvnBin) {
    Write-Host '[maven] Already present at' $mvnBin
    exit 0
}

Write-Host '[maven] Downloading Apache Maven 3.9.9...'
New-Item -ItemType Directory -Force -Path $mvnDir | Out-Null

try {
    Invoke-WebRequest -Uri $mvnUrl -OutFile $mvnZip -UseBasicParsing
} catch {
    Write-Host '[maven] Download failed:' $_.Exception.Message
    exit 1
}

Write-Host '[maven] Extracting...'
Expand-Archive -Force -Path $mvnZip -DestinationPath $mvnDir
$extracted = Join-Path $mvnDir 'apache-maven-3.9.9'
$target    = Join-Path $mvnDir 'apache-maven'
if (Test-Path $extracted) {
    if (Test-Path $target) { Remove-Item $target -Recurse -Force }
    Rename-Item $extracted $target
}
Remove-Item $mvnZip -ErrorAction SilentlyContinue
Write-Host '[maven] Done ->' $target
