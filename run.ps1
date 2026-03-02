# ============================================================
# Smart Farm - Lancement avec OpenCV (PowerShell)
# ============================================================
# Usage:
#   .\run.ps1
# ou
#   powershell -ExecutionPolicy Bypass -File run.ps1

$OpenCVPaths = @(
    "C:\opencv\build\bin\Release",
    "C:\opencv\build\bin",
    "C:\opencv\build\x64\vc15\bin",
    "C:\opencv\build\Release\bin"
)

Write-Host ""
Write-Host "============================================================"
Write-Host "   Smart Farm - OpenCV Setup & Launch"
Write-Host "============================================================"
Write-Host ""

# Chercher la DLL OpenCV
$OpenCVPath = $null
foreach ($path in $OpenCVPaths) {
    if (Test-Path "$path\opencv_java490.dll") {
        Write-Host "[Found] OpenCV DLL at: $path" -ForegroundColor Green
        $OpenCVPath = $path
        break
    }
}

if ($null -eq $OpenCVPath) {
    Write-Host "[Warning] OpenCV DLL not found in standard locations!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "[Checked paths:]"
    foreach ($path in $OpenCVPaths) {
        Write-Host "   - $path"
    }
    Write-Host ""
    Write-Host "[Solution] OpenCV 4.9.0 can be downloaded from:" -ForegroundColor Cyan
    Write-Host "   https://opencv.org/releases/" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "[Note] Application will start without OpenCV, Face ID will be disabled" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "[Info] Launching Smart Farm application..." -ForegroundColor Cyan
Write-Host ""

# Lancer l'application
if ($null -ne $OpenCVPath) {
    Write-Host "[Command] mvn clean javafx:run -Djava.library.path=""$OpenCVPath""" -ForegroundColor Gray
    Write-Host ""
    & mvn clean javafx:run -Djava.library.path="$OpenCVPath"
} else {
    Write-Host "[Command] mvn clean javafx:run (without OpenCV)" -ForegroundColor Gray
    Write-Host ""
    & mvn clean javafx:run
}

Write-Host ""
Write-Host "============================================================"
if ($LASTEXITCODE -eq 0) {
    Write-Host "[Success] Application exited successfully" -ForegroundColor Green
} else {
    Write-Host "[Error] Application exited with error code: $LASTEXITCODE" -ForegroundColor Red
}
Write-Host "============================================================"
Write-Host ""

# Pause pour voir les messages
Read-Host "Press Enter to continue"

