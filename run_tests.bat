@echo off
REM Script pour exécuter les tests unitaires sur Windows

echo ==========================================
echo 🧪 EXECUTION DES TESTS UNITAIRES
echo ==========================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Maven n'est pas installe ou non accessible dans le PATH
    exit /b 1
)

echo 📦 Compilation du projet...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur lors de la compilation
    exit /b 1
)

echo.
echo ==========================================
echo 🧪 Execution de tous les tests
echo ==========================================
call mvn test

echo.
echo ==========================================
echo ✅ Tests termines!
echo ==========================================
pause

