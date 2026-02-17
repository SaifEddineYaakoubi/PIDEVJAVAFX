@echo off
REM ============================================
REM SMART FARM - SCRIPT DE LANCEMENT
REM ============================================

cd /d %~dp0

echo.
echo ========================================
echo   🌾 SMART FARM - Application Launcher
echo ========================================
echo.
echo Options:
echo   1. Lancer le Dashboard GUI (JavaFX)
echo   2. Lancer le Launcher Console
echo   3. Compiler le projet
echo   4. Quitter
echo.

set /p choice="Choisissez une option (1-4): "

if "%choice%"=="1" (
    echo.
    echo ⏳ Lancement du Dashboard...
    echo.
    call mvnw javafx:run
) else if "%choice%"=="2" (
    echo.
    echo ⏳ Lancement du Launcher Console...
    echo.
    call mvnw exec:java -Dexec.mainClass="org.example.pidev.test.Launcher"
) else if "%choice%"=="3" (
    echo.
    echo ⏳ Compilation en cours...
    echo.
    call mvnw clean compile
    echo.
    echo ✅ Compilation terminée!
    echo.
) else if "%choice%"=="4" (
    echo.
    echo 👋 Au revoir!
    exit /b 0
) else (
    echo.
    echo ❌ Option invalide!
    echo.
)

pause

