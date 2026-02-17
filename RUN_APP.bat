@echo off
REM ═══════════════════════════════════════════════════════════════
REM SCRIPT DE COMPILATION ET EXÉCUTION - SmartFarm Application
REM ═══════════════════════════════════════════════════════════════

setlocal enabledelayedexpansion

set MAVEN_HOME=
set JAVA_HOME=C:\Users\admin\.jdks\temurin-17.0.18

cls
echo.
echo ═══════════════════════════════════════════════════════════════
echo   SMARTFARM - Application Desktop JavaFX
echo ═══════════════════════════════════════════════════════════════
echo.

REM Vérifier si Maven est disponible
where /q mvnw.cmd
if errorlevel 1 (
    echo ❌ Maven Wrapper (mvnw.cmd) non trouvé
    echo.
    pause
    exit /b 1
)

REM Menu de sélection
echo Que voulez-vous faire?
echo.
echo 1 - Compiler le projet (mvn clean compile)
echo 2 - Packager le projet (mvn clean package -DskipTests)
echo 3 - Lancer l'application avec Maven (mvn javafx:run)
echo 4 - Compiler et lancer (mvn clean compile javafx:run)
echo 5 - Nettoyer les fichiers temporaires (mvn clean)
echo 6 - Quitter
echo.

set /p choice="Votre choix (1-6): "

if "%choice%"=="1" goto compile
if "%choice%"=="2" goto package
if "%choice%"=="3" goto run_maven
if "%choice%"=="4" goto compile_and_run
if "%choice%"=="5" goto clean
if "%choice%"=="6" goto end
goto invalid_choice

:compile
echo.
echo ⏳ Compilation du projet...
echo.
call .\mvnw.cmd clean compile
goto end

:package
echo.
echo ⏳ Packaging du projet...
echo.
call .\mvnw.cmd clean package -DskipTests
echo.
echo ✅ JAR créé: target\pidev-1.0-SNAPSHOT.jar
goto end

:run_maven
echo.
echo ⏳ Lancement de l'application avec Maven...
echo.
call .\mvnw.cmd javafx:run
goto end

:compile_and_run
echo.
echo ⏳ Compilation et lancement de l'application...
echo.
call .\mvnw.cmd clean compile javafx:run
goto end

:clean
echo.
echo ⏳ Nettoyage des fichiers temporaires...
echo.
call .\mvnw.cmd clean
echo.
echo ✅ Nettoyage terminé
goto end

:invalid_choice
echo.
echo ❌ Choix invalide. Veuillez entrer un numéro entre 1 et 6.
echo.
pause
goto %0

:end
echo.
echo ═══════════════════════════════════════════════════════════════
echo   FIN DU SCRIPT
echo ═══════════════════════════════════════════════════════════════
echo.
pause

