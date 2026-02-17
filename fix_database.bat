@echo off
REM ================================================================
REM SCRIPT DE CORRECTION - Ajouter les colonnes manquantes à recolte
REM ================================================================
REM
REM Ce script exécute la migration SQL sur la base de données MySQL
REM
REM USAGE:
REM   1. Modifiez les variables ci-dessous avec vos paramètres MySQL
REM   2. Sauvegardez ce fichier en tant que fix_database.bat
REM   3. Double-cliquez sur le fichier pour exécuter
REM
REM ================================================================

REM Configuration MySQL
SET MYSQL_USER=root
SET MYSQL_PASSWORD=
SET MYSQL_HOST=localhost
SET MYSQL_PORT=3306
SET DATABASE_NAME=pidev
SET SCRIPT_PATH=%~dp0migration_recolte.sql

REM Afficher les informations
cls
echo ================================================================
echo CORRECTION DE LA BASE DE DONNEES
echo ================================================================
echo.
echo Utilisateur MySQL: %MYSQL_USER%
echo Hôte: %MYSQL_HOST%:%MYSQL_PORT%
echo Base de données: %DATABASE_NAME%
echo Script SQL: %SCRIPT_PATH%
echo.

REM Vérifier si le fichier SQL existe
if not exist "%SCRIPT_PATH%" (
    echo ERREUR: Le fichier %SCRIPT_PATH% n'existe pas!
    pause
    exit /b 1
)

echo Exécution du script de migration...
echo.

REM Exécuter le script MySQL
if "%MYSQL_PASSWORD%"=="" (
    mysql -u %MYSQL_USER% -h %MYSQL_HOST% -P %MYSQL_PORT% %DATABASE_NAME% < "%SCRIPT_PATH%"
) else (
    mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -h %MYSQL_HOST% -P %MYSQL_PORT% %DATABASE_NAME% < "%SCRIPT_PATH%"
)

REM Vérifier le résultat
if %ERRORLEVEL% equ 0 (
    echo.
    echo ================================================================
    echo ✅ SUCCÈS! La migration s'est exécutée correctement.
    echo.
    echo Les colonnes type_culture et localisation ont été ajoutées à
    echo la table recolte.
    echo.
    echo Vous pouvez maintenant redémarrer votre application Java.
    echo ================================================================
) else (
    echo.
    echo ================================================================
    echo ❌ ERREUR! La migration a échoué.
    echo.
    echo Vérifiez:
    echo - Les paramètres MySQL (utilisateur, mot de passe, hôte)
    echo - Que le serveur MySQL est en train de fonctionner
    echo - Que vous avez les droits ALTER TABLE sur la base de données
    echo ================================================================
)

pause

