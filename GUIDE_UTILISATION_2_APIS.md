# 🎯 GUIDE D'UTILISATION COMPLET - SMART FARM AVEC 2 APIs

## 📖 Table des Matières
1. [Démarrage rapide](#démarrage-rapide)
2. [API Prédictions](#api-1--prédictions-de-rendement)
3. [API Recommandations](#api-2--recommandations-doptimisation)
4. [Interface Utilisateur](#interface-utilisateur)
5. [FAQ et Dépannage](#faq-et-dépannage)

---

## 🚀 Démarrage Rapide

### Installation et Lancement

```bash
# 1. Ouvrir le terminal PowerShell
# 2. Aller dans le dossier du projet
cd C:\Users\admin\Desktop\PIDEVJAVAFX-maramdh

# 3. Compiler le projet
.\mvnw clean compile

# 4. Exécuter les tests
.\mvnw test

# 5. Lancer l'application
.\mvnw javafx:run
```

### Premier Accès
1. L'application démarre avec le **Dashboard** par défaut
2. Vous voyez les statistiques : 🌾 Récoltes | 📊 Rendements | 🌍 Surface
3. Plus bas, apparaît le widget **🤖 Prédictions Intelligentes**
4. Vous pouvez cliquer sur "🤖 Prédictions" pour voir la page complète

---

## 🎯 API #1 : Prédictions de Rendement

### 📊 Qu'est-ce que c'est ?

L'API Prédiction utilise vos **données historiques** pour prédire les récoltes futures.

### 🔍 Comment ça fonctionne ?

```
Données en DB: 
  Tomate (500 kg le 01/12)
  Tomate (550 kg le 01/01)
  Tomate (480 kg le 01/02)
        ↓
  Moyenne = (500 + 550 + 480) / 3 = 510 kg
        ↓
  Prédiction = 510 kg pour la prochaine Tomate
```

### 📍 Où les voir ?

#### Option 1: Sur le Dashboard (Widget compact)
```
1. Cliquer sur "📊 Dashboard" dans le sidebar
2. Scroller vers le bas
3. Voir "🤖 Prédictions Intelligentes"
   ├─ Cartes compactes (2 colonnes)
   ├─ Info rapide par culture
   └─ Bouton 🔄 Rafraîchir
```

#### Option 2: Page complète dédiée
```
1. Cliquer sur "🤖 Prédictions" dans le sidebar
2. Voir page avec grille 3 colonnes
3. Chaque culture en carte détaillée:
   ├─ 🌾 Nom de la culture
   ├─ 📦 Quantité prédite (kg)
   ├─ 📊 Rendement (kg/ha)
   ├─ 📈 Tendance
   └─ 📅 Historique (nb récoltes)
```

### 📈 Les 4 Prédictions

#### 1️⃣ Quantité Prédite
```
📦 Quantité prédite: 510.00 kg

Calcul: Moyenne des 3 dernières récoltes
Exemple:
  - Tomate 1: 500 kg
  - Tomate 2: 550 kg
  - Tomate 3: 480 kg
  ─────────────────────
  Moyenne = 510 kg
```

#### 2️⃣ Rendement Prédit
```
📊 Rendement prédit: 25.5 kg/ha

Calcul: Quantité moyenne ÷ Surface moyenne
Exemple:
  - Quantité moyenne: 510 kg
  - Surface moyenne: 20 ha
  ─────────────────────
  Rendement = 510 ÷ 20 = 25.5 kg/ha
```

#### 3️⃣ Tendance
```
Détecte si la culture s'améliore ou se détériore

📈 Augmentation (> 5%)
  → La dernière récolte est 5% + plus grande
  Couleur: Orange

📉 Diminution (< -5%)
  → La dernière récolte est 5% + plus petite
  Couleur: Orange

→ Stable (±5%)
  → Pas de changement significatif
  Couleur: Gris
```

#### 4️⃣ Historique
```
📅 Récoltes enregistrées: 15

Nombre total de récoltes pour cette culture
Utile pour évaluer la fiabilité de la prédiction
  - 0-2 récoltes: Peu fiable
  - 3-5 récoltes: Modérément fiable
  - 6+ récoltes: Très fiable
```

### 💡 Exemple Complet

Pour la culture "Olive":
```
│ Type Culture │ Olive                    │
│ Quantité     │ 620.5 kg                 │
│ Rendement    │ 31.0 kg/ha               │
│ Tendance     │ 📈 Augmentation (12%)    │
│ Historique   │ 12 récoltes enregistrées │
```

**Interprétation:**
- Les Olives vont bien (augmentation)
- La prédiction de 620 kg est fiable (12 récoltes)
- Si vous avez 20 ha, vous devez récolter ~620 kg
- En rendement, c'est 31 kg par hectare

---

## 🤖 API #2 : Recommandations d'Optimisation

### 📊 Qu'est-ce que c'est ?

L'API Recommandations **analyse vos données** et vous propose des **actions concrètes** pour améliorer votre production.

### 🔍 Types d'Analyses

#### 1. Analyse de Qualité
```
Détecte: La qualité moyenne de vos récoltes

Exemple:
  Vos récoltes:
  - Tomate 1: Bonne qualité
  - Tomate 2: Excellente qualité
  - Tomate 3: Bonne qualité
  
  Qualité moyenne = Bonne (pas Excellente)
  
  Recommandation:
  🔴 HAUTE PRIORITÉ
  Titre: "🎯 Améliorer la qualité"
  Action: "Optimiser pratiques agricoles"
  Impact: "Augmenter la valeur marchande de 20%"
```

#### 2. Analyse de Productivité
```
Détecte: Si votre productivité est sous les prédictions

Exemple:
  Prédiction: 510 kg
  Productivité réelle: 400 kg
  Écart: 21.6% (GRAVE!)
  
  Recommandation:
  🔴 HAUTE PRIORITÉ
  Titre: "📦 Augmenter les rendements"
  Action: "Investir en irrigation ou engrais"
  Impact: "Gagner 110 kg par récolte"
```

#### 3. Analyse de Saisonnalité
```
Détecte: Quel mois est le meilleur pour chaque culture

Exemple:
  Tomates:
  - Janvier: 280 kg (mauvais)
  - Juillet: 550 kg (excellent)
  - Décembre: 300 kg (mauvais)
  
  Recommandation:
  🟠 PRIORITÉ MOYENNE
  Titre: "📅 Optimiser selon la saison"
  Action: "Planifier récoltes pour juillet"
  Impact: "Augmenter de 270 kg/mois"
```

### 🔴 Système de Priorité

```
🔴 HAUTE (Priorité 1)
   ├─ Couleur: Rouge (#C62828)
   ├─ Urgence: Immédiate
   ├─ Exemples: Qualité faible, Productivité très basse
   └─ Action: À faire cette semaine!

🟠 MOYENNE (Priorité 2)
   ├─ Couleur: Orange (#FF9800)
   ├─ Urgence: Court terme (1-2 mois)
   ├─ Exemples: Saisonnalité, Variabilité
   └─ Action: À planifier ce mois

🔵 BASSE (Priorité 3)
   ├─ Couleur: Bleu (#2196F3)
   ├─ Urgence: Long terme (3-6 mois)
   ├─ Exemples: Optimisations mineures
   └─ Action: À noter pour plus tard
```

### 💡 Exemple de Recommandation

```
╔═══════════════════════════════════════════════════════════════╗
║  🔴 PRIORITÉ HAUTE - Améliorer la qualité                    ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  📋 Titre:                                                    ║
║     🎯 Améliorer la qualité des récoltes                     ║
║                                                               ║
║  📝 Description:                                              ║
║     Votre qualité moyenne est: Bonne                          ║
║     Cherchez à atteindre l'excellence                         ║
║                                                               ║
║  💰 Impact:                                                   ║
║     Augmenter la valeur marchande de 15%                      ║
║                                                               ║
║  ✅ Action à prendre:                                         ║
║     1. Optimiser les pratiques agricoles                      ║
║     2. Améliorer les conditions de stockage                   ║
║     3. Former le personnel aux normes qualité                 ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
```

### 📍 Où les voir ?

(Actuellement accès par code, intégration UI prévue)

```java
// Récupérer les recommandations
OptimizationService service = new OptimizationService();
List<Recommendation> recs = service.getRecommendations("Tomate");

// Afficher triées par priorité
for (Recommendation rec : recs) {
    System.out.println(rec.getTitle());
    System.out.println("→ " + rec.getAction());
}
```

---

## 🎨 Interface Utilisateur

### 🗺️ Navigation Principale

```
┌─────────────────────────────────────────┐
│ SIDEBAR                                 │
├─────────────────────────────────────────┤
│  🌱 SMART FARM                          │
│                                         │
│  📊 Dashboard ←─── Active par défaut    │
│  🌾 Récoltes  ←─── Sidebar devient VERT │
│  📈 Rendements←─── Sidebar devient MARRON
│  🤖 Prédictions ←─ Page IA nouvelle     │
│                                         │
│  ❌ Quitter                             │
└─────────────────────────────────────────┘
```

### 🎯 Dashboard (Par défaut)

```
┌──────────────────────────────────────────────────────────┐
│ 📊 Statistiques Globales                                 │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ 🌾                    📊                    🌍          │
│ Total Récoltes        Total Rendements    Surface      │
│ 15                    12                   45.5 ha      │
│ récoltes              rendements           hectares     │
│                                                          │
├─────────────���────────────────────────────────────────────┤
│ 🤖 Prédictions Intelligentes                             │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ 🌾 Tomate              🌾 Blé                           │
│ 📦 510.0 kg            📦 1200.0 kg                     │
│ 📊 25.5 kg/ha          📊 45.0 kg/ha                    │
│ 📈 Augmentation (12%)  → Stable                         │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 🔄 Interaction avec le Widget

```
Bouton "🔄 Rafraîchir"
    ↓
Les prédictions se mettent à jour
(Utile si on vient d'ajouter une récolte)
```

### 🎨 Couleurs de la Sidebar

```
État DÉFAUT (Dashboard / Prédictions)
├─ Fond: Vert gradient (#2E7D32 → #1B5E20)
└─ Texte: Blanc

État RÉCOLTES (Clic 🌾 Récoltes)
├─ Fond: Vert gradient (#2E7D32 → #1B5E20)
└─ Impression: Culture active (Vert)

État RENDEMENTS (Clic 📈 Rendements)
├─ Fond: Marron gradient (#522819 → #592c1f)
└─ Impression: Production active (Marron)
```

---

## ❓ FAQ et Dépannage

### Q: Les prédictions ne s'affichent pas
**R:** Vérifiez que vous avez des récoltes dans la base de données
```bash
# Ajouter une récolte test:
1. Cliquer sur "🌾 Récoltes"
2. Cliquer "➕ Ajouter une récolte"
3. Remplir le formulaire
4. Cliquer "Enregistrer"
5. Retour Dashboard → Prédictions mises à jour
```

### Q: Les tests ne passent pas
**R:** Vérifiez l'environnement Java
```bash
# Vérifier Java version
java -version
# Doit être Java 17+

# Nettoyer et recompiler
.\mvnw clean compile
.\mvnw test
```

### Q: L'application ne démarre pas
**R:** Vérifier les ressources FXML
```bash
# Vérifier que Dashboard.fxml existe
ls src/main/resources/Dashboard.fxml

# Si absent, restaurer depuis backup
# Sinon, recompiler:
.\mvnw clean javafx:run
```

### Q: Les prédictions sont 0
**R:** Pas assez de données historiques
```
Minimal requis:
- Pour prédictions: Au moins 1 récolte
- Pour tendance: Au moins 2 récoltes
- Fiable: Au moins 3 récoltes
```

### Q: Comment modifier les prédictions ?
**R:** Les prédictions se basent sur les données
```
Pour modifier:
1. Ajouter plus de récoltes (improve data)
2. Modifier récoltes existantes (RendementDetail)
3. Cliquer "🔄 Rafraîchir" (reload predictions)
```

---

## 🎓 Concepts Clés

### Algorithme de Prédiction
```
1. Récupérer toutes récoltes du type
2. Trier par date (récentes d'abord)
3. Prendre les 3 dernières
4. Calculer la moyenne
5. Arrondir à 2 décimales
6. Retourner la prédiction
```

### Indicateurs de Fiabilité
```
Score fiabilité = nombre récoltes / 10

0-1 récolte: ⚠️ Très peu fiable (≤ 10%)
2-3 récoltes: ⚠️ Peu fiable (20-30%)
4-6 récoltes: ✓ Modérément fiable (40-60%)
7-10 récoltes: ✓ Fiable (70-100%)
10+ récoltes: ✓✓ Très fiable (100%+)
```

---

## 📚 Ressources

### Documentation Complète
- `DOCUMENTATION_2_APIS.md` - Vue complète des 2 APIs
- `INTEGRATION_PREDICTION_API.md` - Détails API Prédiction
- `RESUME_FINAL_IMPLEMENTATION.md` - Résumé technique

### Fichiers Importants
```
Services:
  src/main/java/org/example/pidev/services/
  ├── PredictionService.java
  └── OptimizationService.java

Contrôleurs:
  src/main/java/org/example/pidev/controllers/
  ├── PredictionController.java
  └── PredictionWidgetController.java

Interfaces:
  src/main/resources/
  ├── Predictions.fxml
  └── PredictionWidget.fxml

Tests:
  src/test/java/org/example/pidev/services/
  ├── PredictionServiceTest.java
  └── OptimizationServiceTest.java
```

---

## ✨ Conseils d'Utilisation

### 1. Pour les Débutants
```
1. Lancer l'application
2. Cliquer sur "📊 Dashboard"
3. Voir les cartes statistiques
4. Scroller pour voir les prédictions
5. Cliquer "🤖 Prédictions" pour détails
```

### 2. Pour Optimiser la Production
```
1. Consulter les prédictions régulièrement
2. Comparer prédit vs réalisé
3. Lire les recommandations
4. Agir sur les priorités HAUTE
5. Mesurer l'impact (ajouter récolte)
```

### 3. Pour Analyser les Tendances
```
1. Aller dans "🤖 Prédictions"
2. Observer la tendance (📈/📉/→)
3. Cliquer sur la culture pour détails
4. Analyser l'historique (nb récoltes)
5. Planifier en conséquence
```

---

## 🎯 Prochaines Étapes

### Maintenant (Immédiat)
- ✅ Tester les 2 APIs
- ✅ Ajouter des récoltes test
- ✅ Observer les prédictions
- ✅ Lire les recommandations

### Semaine 1
- [ ] Valider prédictions vs résultats réels
- [ ] Mettre en place actions recommandées
- [ ] Rafraîchir prédictions avec nouvelles données

### Mois 1
- [ ] Intégrer recommandations dans UI
- [ ] Ajouter graphiques de tendances
- [ ] Exporter recommandations en PDF
- [ ] Créer alertes si écarts

### Mois 3+
- [ ] Machine Learning avancé
- [ ] Prédictions par saison
- [ ] Intégration données météo
- [ ] API REST publique

---

**Guide créé:** 2026-02-20  
**Version:** 1.0  
**Auteur:** Smart Farm Team

