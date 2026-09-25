param(
  [switch]$Skip3d
)
$ErrorActionPreference = "Stop"
$firebaseDir = Split-Path $PSScriptRoot -Parent
$root = Split-Path $firebaseDir -Parent

$envMap = @{}
Get-Content (Join-Path $firebaseDir "local.env") | ForEach-Object {
  if ($_ -match "^(\w+)=(.*)$") { $envMap[$matches[1]] = $matches[2] }
}
if (-not $envMap.CI_API_KEY -or -not $envMap.CI_API_SECRET) {
  throw "Falta firebase/local.env con CI_API_KEY y CI_API_SECRET"
}

$vehicles = @(
  @{ id = "porsche";  make = "porsche";     model = "911";     gen = "992-2024-now" },
  @{ id = "bmw";      make = "bmw";         model = "m4";      gen = "g82-g83-2024-now" },
  @{ id = "mustang";  make = "ford";        model = "mustang"; gen = "s650-2024-now" },
  @{ id = "ferrari";  make = "ferrari";     model = "296-gtb"; gen = "2022-now" },
  @{ id = "huracan";  make = "lamborghini"; model = "huracan"; gen = "2019-2024" },
  @{ id = "mclaren";  make = "mclaren";     model = "750s";    gen = "2023-now" },
  @{ id = "r8";       make = "audi";        model = "r8";      gen = "4s-2019-2023" },
  @{ id = "corvette"; make = "chevrolet";   model = "corvette"; gen = "c8-2020-now" },
  @{ id = "gtr";      make = "nissan";      model = "gt-r";    gen = "r35-2023-now" }
)

$drawable = Join-Path $root "app\src\main\res\drawable-nodpi"
$glbDir   = Join-Path $root "app\src\main\assets\3d"
New-Item -ItemType Directory -Force -Path $drawable | Out-Null
New-Item -ItemType Directory -Force -Path $glbDir | Out-Null

$headers = @{ "X-Api-Secret" = $envMap.CI_API_SECRET }
$total = 0

foreach ($v in $vehicles) {
  foreach ($view in "front34", "side", "rear") {
    $url = "https://carimagesapi.com/api/v1/vehicles/$($v.make)/$($v.model)/$($v.gen)/image?redirect=1&format=webp&view=$view&size=1200&api_key=$($envMap.CI_API_KEY)"
    $out = Join-Path $drawable "ci_$($v.id)_$view.webp"
    Invoke-WebRequest -Uri $url -Headers $headers -OutFile $out -MaximumRedirection 10
    $kb = [math]::Round((Get-Item $out).Length / 1KB, 1)
    $total += [int](Get-Item $out).Length
    Write-Host "foto  ci_$($v.id)_$view.webp  $kb KB"
  }
  if (-not $Skip3d) {
    $url3 = "https://carimagesapi.com/api/v1/vehicles/$($v.make)/$($v.model)/$($v.gen)/model?redirect=1&api_key=$($envMap.CI_API_KEY)"
    $out3 = Join-Path $glbDir "vehicle_$($v.id).glb"
    Invoke-WebRequest -Uri $url3 -Headers $headers -OutFile $out3 -MaximumRedirection 10
    $mb = [math]::Round((Get-Item $out3).Length / 1MB, 1)
    $total += [int](Get-Item $out3).Length
    Write-Host "glb   vehicle_$($v.id).glb  $mb MB"
  }
}
Write-Host ("Total descargado: {0:N1} MB" -f ($total / 1MB))