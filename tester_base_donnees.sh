 #!/bin/bash

echo "════════════════════════════════════════════════════════════════"
echo "🔍 TEST DE CONNEXION À LA BASE DE DONNÉES"
echo "════════════════════════════════════════════════════════════════"
echo ""

echo "1. Configuration de la base de données:"
echo "   URL: jdbc:mysql://localhost:3306/smart_farm"
echo "   USER: root"
echo "   PASSWORD: (vide)"
echo ""

echo "2. Vérification MySQL:"
echo "   Assurez-vous que:"
echo "   ✓ MySQL est installé et lancé"
echo "   ✓ La base 'smart_farm' existe"
echo "   ✓ L'utilisateur 'root' existe avec mot de passe vide"
echo ""

echo "3. Pour vérifier la connexion:"
mysql -u root smart_farm -e "SELECT 1;" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Connexion à MySQL réussie!"
else
    echo "❌ Erreur de connexion à MySQL"
fi

echo ""
echo "4. Vérification de la table 'rendement':"
mysql -u root smart_farm -e "DESCRIBE rendement;" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Table 'rendement' existe!"
else
    echo "❌ Table 'rendement' n'existe pas"
fi

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "✅ TEST TERMINÉ"
echo "════════════════════════════════════════════════════════════════"

