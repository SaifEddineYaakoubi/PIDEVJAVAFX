@echo off
echo ════════════════════════════════════════════════════════════════
echo 🔍 TEST DE CONNEXION À LA BASE DE DONNEES
echo ════════════════════════════════════════════════════════════════
echo.

echo 1. Configuration de la base de données:
echo    URL: jdbc:mysql://localhost:3306/smart_farm
echo    USER: root
echo    PASSWORD: (vide)
echo.

echo 2. Pour tester la connexion en Java:
echo    Vous verrez en console lors du lancement:
echo    ✓ "✅ Connected to database successfully"
echo    ✗ "❌ Database connection error: ..."
echo.

echo 3. Instructions de débogage:
echo.
echo    ÉTAPE 1: Compiler le projet
echo    $ mvn clean compile
echo.
echo    ÉTAPE 2: Lancer l'application
echo    $ mvn javafx:run
echo.
echo    ÉTAPE 3: Ouvrir le formulaire "Ajouter Rendement"
echo.
echo    ÉTAPE 4: Observer la console:
echo    - Regardez les logs de débogage
echo    - Identifiez où s'arrête l'exécution
echo.
echo    ÉTAPE 5: Copier les logs de console
echo.

echo 4. Problèmes possibles:
echo.
echo    ❌ "Database connection error"
echo       → MySQL n'est pas lancé
echo       → Base de données 'smart_farm' n'existe pas
echo.
echo    ❌ "Aucune récolte disponible"
echo       → Aucune récolte en base de données
echo       → Créer une récolte d'abord
echo.
echo    ❌ "Erreur lors de l'ajout"
echo       → Regardez la console pour plus de détails
echo       → Vérifiez les logs de validation
echo       → Vérifiez la récolte sélectionnée
echo.

echo ════════════════════════════════════════════════════════════════
pause

