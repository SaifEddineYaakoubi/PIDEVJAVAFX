@echo off
chcp 65001 >nul
REM ============================================================
REM SMART FARM - Seed Database Script
REM ============================================================
echo.
echo ============================================================
echo   SMART FARM - REMPLISSAGE BASE DE DONNEES
echo ============================================================
echo.

cd /d "%~dp0"

REM Compiler
echo [1/3] Compilation...
call mvnw.cmd compile -q
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Compilation echouee
    pause
    exit /b 1
)
echo OK

REM Trouver le JAR MySQL dans le cache Maven
echo [2/3] Recherche des dependances...
for /r "%USERPROFILE%\.m2\repository\com\mysql" %%f in (mysql-connector-j-*.jar) do set MYSQL_JAR=%%f

if "%MYSQL_JAR%"=="" (
    echo MySQL JAR non trouve dans le cache Maven.
    echo.
    echo ALTERNATIVE: Importez sql\seed_data.sql dans phpMyAdmin :
    echo   1. Ouvrez http://localhost/phpmyadmin
    echo   2. Selectionnez la base "smartfarm"
    echo   3. Onglet "Importer"
    echo   4. Choisissez: sql\seed_data.sql
    echo   5. Cliquez "Executer"
    pause
    exit /b 1
)

echo MySQL JAR: %MYSQL_JAR%

REM Exécuter SeedDatabase
echo [3/3] Execution du seed...
java --add-modules ALL-MODULE-PATH -cp "target\classes;%MYSQL_JAR%" org.example.pidev.test.SeedDatabase

echo.
pause
