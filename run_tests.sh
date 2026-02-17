#!/bin/bash
# Script pour exécuter les tests unitaires

echo "=========================================="
echo "🧪 EXÉCUTION DES TESTS UNITAIRES"
echo "=========================================="
echo ""

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé ou non accessible dans le PATH"
    exit 1
fi

echo "📦 Compilation du projet..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo ""
echo "=========================================="
echo "🧪 Exécution de tous les tests"
echo "=========================================="
mvn test

echo ""
echo "=========================================="
echo "✅ Tests terminés!"
echo "=========================================="

