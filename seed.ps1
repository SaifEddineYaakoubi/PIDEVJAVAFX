# ============================================================
# SMART FARM - Seed Database (PowerShell)
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  SMART FARM - REMPLISSAGE BASE DE DONNEES" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""

Set-Location $PSScriptRoot

# 1. Compiler
Write-Host "[1/3] Compilation..." -ForegroundColor Yellow
& .\mvnw.cmd compile -q 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Compilation echouee" -ForegroundColor Red
    Read-Host "Appuyez sur Entree"
    exit 1
}
Write-Host "  OK" -ForegroundColor Green

# 2. Trouver MySQL JAR
Write-Host "[2/3] Recherche du MySQL connector..." -ForegroundColor Yellow
$mysqlJar = Get-ChildItem -Path "$env:USERPROFILE\.m2\repository\com\mysql" -Recurse -Filter "mysql-connector-j-*.jar" -ErrorAction SilentlyContinue | Select-Object -First 1

if (-not $mysqlJar) {
    Write-Host ""
    Write-Host "MySQL JAR non trouve. ALTERNATIVE :" -ForegroundColor Red
    Write-Host "  1. Ouvrez http://localhost/phpmyadmin" -ForegroundColor Cyan
    Write-Host "  2. Selectionnez la base 'smartfarm'" -ForegroundColor Cyan
    Write-Host "  3. Onglet 'Importer'" -ForegroundColor Cyan
    Write-Host "  4. Choisissez: sql\seed_data.sql" -ForegroundColor Cyan
    Write-Host "  5. Cliquez 'Executer'" -ForegroundColor Cyan
    Read-Host "Appuyez sur Entree"
    exit 1
}

Write-Host "  MySQL JAR: $($mysqlJar.FullName)" -ForegroundColor Green

# 3. Exécuter
Write-Host "[3/3] Execution du seed..." -ForegroundColor Yellow
$cp = "target\classes;$($mysqlJar.FullName)"
java --add-modules ALL-MODULE-PATH -cp $cp org.example.pidev.test.SeedDatabase

Write-Host ""
Read-Host "Appuyez sur Entree pour fermer"

