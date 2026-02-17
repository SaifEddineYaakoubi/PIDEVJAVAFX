@echo off
echo ════════════════════════════════════════════════════════════════
echo 🔍 VERIFICATION DU PROJET - Recherche d'Erreurs
echo ════════════════════════════════════════════════════════════════
echo.

echo 1️⃣  Compilation du projet...
echo ─────────────────────────────────────────────────────────────
mvn clean compile -q 2>&1

if %ERRORLEVEL% EQU 0 (
    echo ✅ COMPILATION REUSSIE
) else (
    echo ❌ ERREURS DETECTEES
    mvn clean compile 2>&1 | findstr "ERROR error"
)

echo.
echo 2️⃣  Verification des fichiers...
echo ─────────────────────────────────────────────────────────────

if exist "src\main\resources\AddRendement.fxml" (
    echo ✅ AddRendement.fxml EXISTE
) else (
    echo ❌ AddRendement.fxml MANQUANT
)

if exist "src\main\java\org\example\pidev\controllers\AddRendementController.java" (
    echo ✅ AddRendementController.java EXISTE
) else (
    echo ❌ AddRendementController.java MANQUANT
)

echo.
echo 3️⃣  Verification des methodes...
echo ─────────────────────────────────────────────────────────────

findstr /M "showAjouterDialog" "src\main\java\org\example\pidev\controllers\RendementController.java" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Methode showAjouterDialog TROUVEE
) else (
    echo ❌ Methode showAjouterDialog MANQUANTE
)

findstr /M "setStage" "src\main\java\org\example\pidev\controllers\AddRendementController.java" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Methode setStage TROUVEE
) else (
    echo ❌ Methode setStage MANQUANTE
)

echo.
echo ════════════════════════════════════════════════════════════════
echo ✅ VERIFICATION TERMINEE
echo ════════════════════════════════════════════════════════════════
echo.
echo Pour tester l'application, executez:
echo   mvn javafx:run
echo.
pause

