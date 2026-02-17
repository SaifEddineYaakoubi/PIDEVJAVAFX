#!/bin/bash

echo "════════════════════════════════════════════════════════════════"
echo "🔍 VÉRIFICATION DU PROJET - Recherche d'Erreurs"
echo "════════════════════════════════════════════════════════════════"
echo ""

echo "1️⃣  Compilation du projet..."
echo "─────────────────────────────────────────────────────────────"
mvn clean compile -X 2>&1 > /tmp/build.log

if [ $? -eq 0 ]; then
    echo "✅ COMPILATION RÉUSSIE"
else
    echo "❌ ERREURS DÉTECTÉES"
    echo ""
    echo "ERREURS:"
    grep -i "ERROR" /tmp/build.log
    echo ""
    echo "AVERTISSEMENTS:"
    grep -i "WARNING" /tmp/build.log
fi

echo ""
echo "2️⃣  Vérification des fichiers FXML..."
echo "─────────────────────────────────────────────────────────────"

files=(
    "src/main/resources/AddRendement.fxml"
    "src/main/java/org/example/pidev/controllers/AddRendementController.java"
    "src/main/java/org/example/pidev/controllers/RendementController.java"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file EXISTE"
    else
        echo "❌ $file MANQUANT"
    fi
done

echo ""
echo "3️⃣  Vérification des méthodes clés..."
echo "─────────────────────────────────────────────────────────────"

# Vérifier la méthode showAjouterDialog
if grep -q "private void showAjouterDialog" src/main/java/org/example/pidev/controllers/RendementController.java; then
    echo "✅ Méthode showAjouterDialog() TROUVÉE"
else
    echo "❌ Méthode showAjouterDialog() MANQUANTE"
fi

# Vérifier la méthode setStage
if grep -q "public void setStage" src/main/java/org/example/pidev/controllers/AddRendementController.java; then
    echo "✅ Méthode setStage() TROUVÉE"
else
    echo "❌ Méthode setStage() MANQUANTE"
fi

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "✅ VÉRIFICATION TERMINÉE"
echo "════════════════════════════════════════════════════════════════"

