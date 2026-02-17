@echo off
REM ================================================
REM Script pour démarrer SmartFarm Application
REM ================================================

setlocal enabledelayedexpansion

REM Configuration
set JAVA_HOME=C:\Users\admin\.jdks\temurin-17.0.18
set MAVEN_PROJECT_DIR=%~dp0
set JAR_FILE=%MAVEN_PROJECT_DIR%target\pidev-1.0-SNAPSHOT.jar

echo.
echo ================================================
echo   SmartFarm - Application Desktop
echo ================================================
echo.

REM Vérifier le JAR
if not exist "%JAR_FILE%" (
    echo ❌ ERREUR: JAR non trouvé: %JAR_FILE%
    echo.
    echo Veuillez d'abord compiler le projet:
    echo   mvnw.cmd clean package -DskipTests
    echo.
    pause
    exit /b 1
)

echo ✅ JAR trouvé: %JAR_FILE%
echo.

REM Construire le CLASSPATH
set CLASSPATH=%JAR_FILE%

REM Ajouter les dépendances Maven
set M2_REPO=%USERPROFILE%\.m2\repository
set "DEPS="
for /f "delims=" %%F in ('dir /b "%M2_REPO%\com\mysql\mysql-connector-j\*\*.jar"') do (
    set "DEPS=!DEPS!%M2_REPO%\com\mysql\mysql-connector-j\%%F;"
)

set "CLASSPATH=!CLASSPATH!!DEPS!"

echo Démarrage de l'application...
echo.

REM Exécuter l'application
"%JAVA_HOME%\bin\java.exe" ^
    -Dfile.encoding=UTF-8 ^
    -classpath "%CLASSPATH%" ^
    org.example.pidev.test.LauncherGUI

echo.
pause

