# 🚀 INSTRUCTIONS D'EXÉCUTION - AddRendement.fxml

## ⏱️ Temps Estimé: 2 Minutes

---

## ÉTAPE 1: Compiler le Projet

Ouvrez un terminal dans le répertoire du projet:

```bash
cd C:\Users\admin\Desktop\PIDEVJAVAFX-maramdh
```

Compilez:

```bash
mvn clean compile
```

**Résultat attendu:**
```
[INFO] BUILD SUCCESS
```

---

## ÉTAPE 2: Lancer l'Application

```bash
mvn javafx:run
```

L'application se lancera.

---

## ÉTAPE 3: Tester le Formulaire

1. **Naviguer vers la section Rendements**
   - Cliquer sur "Rendement" dans le menu

2. **Cliquer sur "Ajouter Rendement"**
   - Un bouton avec le texte "➕ Ajouter" ou "Ajouter Rendement"
   - Le formulaire AddRendement.fxml doit s'afficher

3. **Observer la Console**
   - Vous devriez voir:
   ```
   ➕ Ajouter rendement cliqué
   📂 Chargement du formulaire AddRendement.fxml...
   ✓ Formulaire chargé avec succès
   ✓ Contrôleur récupéré: org.example.pidev.controllers.AddRendementController
   ✓ Stage configurée
   ```

---

## ÉTAPE 4: Remplir le Formulaire

### 4.1: Sélectionner une Récolte
- Cliquer sur le ComboBox "Sélectionnez une récolte..."
- Choisir une récolte disponible
- Les informations de la récolte s'affichent

### 4.2: Entrer la Surface
- Cliquer sur le champ "Surface Exploitée (ha)"
- Entrer une valeur (ex: **10.5**)
- Note: Minimum 0.01, Maximum 10000

### 4.3: Entrer la Quantité
- Cliquer sur le champ "Quantité Totale (Kg)"
- Entrer une valeur (ex: **500**)
- Note: Maximum 1,000,000 Kg

### 4.4: Vérifier la Productivité Automatique
- La case "Calculer automatiquement" est cochée par défaut
- La productivité se calcule: **500 / 10.5 = 47.62 Kg/ha**
- Le résumé s'affiche:
  ```
  Surface: 10.50 ha
  Quantité: 500.00 Kg
  Productivité: 47.62 Kg/ha
  ```

---

## ÉTAPE 5: Enregistrer le Rendement

1. **Vérifier que tous les champs sont remplis**
   - Récolte: Sélectionnée ✓
   - Surface: Remplie ✓
   - Quantité: Remplie ✓
   - Productivité: Calculée ✓

2. **Cliquer sur "✓ Enregistrer"**

3. **Une alerte s'affiche:**
   ```
   Succès
   Rendement enregistré avec succès! ID: 15
   ```

4. **Cliquer OK**

5. **Observer:**
   - Le formulaire se ferme automatiquement
   - La console affiche:
   ```
   ✅ Rendement ajouté avec succès (ID: 15)
   ✓ Formulaire fermé
   ✓ Table rafraîchie
   ```
   - La table des rendements se rafraîchit
   - Le nouveau rendement est visible dans la table

---

## 🧪 Test Complet

### Scénario 1: Ajout Simple
```
1. Cliquer "Ajouter Rendement"
2. Sélectionner une récolte
3. Surface: 5.0
4. Quantité: 250
5. Productivité calculée: 50.0
6. Cliquer "Enregistrer"
7. Vérifier le nouveau rendement dans la table
```

### Scénario 2: Réinitialisation
```
1. Cliquer "Ajouter Rendement"
2. Remplir le formulaire complètement
3. Cliquer "🔄 Réinitialiser"
4. Tous les champs se vident
5. Le résumé réinitialise (-- ha, -- Kg, -- Kg/ha)
6. Cliquer "✕ Fermer"
```

### Scénario 3: Validation d'Erreur
```
1. Cliquer "Ajouter Rendement"
2. Ne pas sélectionner de récolte
3. Entrer: Surface: 1000000 (dépasse maximum)
4. Cliquer "Enregistrer"
5. Une alerte d'erreur s'affiche
6. Le formulaire reste ouvert
```

---

## 📋 Résultats Attendus

### Console après "Ajouter":
```
➕ Ajouter rendement cliqué
📂 Chargement du formulaire AddRendement.fxml...
✓ Formulaire chargé avec succès
✓ Contrôleur récupéré: org.example.pidev.controllers.AddRendementController
✓ Stage configurée
```

### Formulaire Affiché:
```
┌──────────────────────────────────────────┐
│ 📊 Smart Farm - Rendements   [← Retour] │
├──────────────────────────────────────────┤
│                                          │
│ Ajouter un Rendement                     │
│ Enregistrez un nouveau rendement...      │
│                                          │
│ 🌾 Récolte Associée *                   │
│ [Sélectionnez une récolte...] [Refresh] │
│ Aucune récolte sélectionnée              │
│                                          │
│ 🌍 Surface Exploitée (ha) *             │
│ [________________]                       │
│ Minimum: 0.01 ha | Maximum: 10000 ha    │
│                                          │
│ 📦 Quantité Totale (Kg) *               │
│ [________________]                       │
│                                          │
│ ⚡ Productivité (Kg/ha)                 │
│ [☑] Calculer automatiquement            │
│ [________________] (désactivé)           │
│                                          │
│ 📊 Résumé du Rendement                  │
│ Surface: -- ha                           │
│ Quantité: -- Kg                          │
│ Productivité: -- Kg/ha                  │
│                                          │
│ [✓ Enreg] [🔄 Réinit] [✕ Fermer]       │
│                                          │
│ ℹ️ Informations                          │
│ • Les champs marqués avec * sont oblig.  │
│ • Productivité calculée: Quantité ÷ Surf│
│                                          │
├──────────────────────────────────────────┤
│ ✓ Prêt à enregistrer un nouveau rendement│
│                                     Rend.│
└──────────────────────────────────────────┘
```

---

## ✅ Checklist de Test

### Avant de Tester:
- [ ] Code compilé sans erreurs
- [ ] Application lancée
- [ ] Console visible

### Test du Formulaire:
- [ ] Le formulaire s'ouvre au clic
- [ ] Les récoltes se chargent
- [ ] La sélection de récolte affiche les infos
- [ ] La productivité se calcule automatiquement
- [ ] Le résumé s'affiche correctement
- [ ] L'enregistrement fonctionne
- [ ] La table se rafraîchit
- [ ] Le nouveau rendement est visible

### Messages:
- [ ] Pas d'erreur dans la console
- [ ] Logs de débogage affichés
- [ ] Alerte de succès au clic "Enregistrer"

---

## 🎉 C'est Terminé!

Si tous les tests passent, l'intégration d'AddRendement.fxml est **COMPLÈTE ET FONCTIONNELLE**.

---

**Créé le**: 16 Février 2026
**Status**: ✅ INSTRUCTIONS D'EXÉCUTION COMPLÈTES

