# 🚀 DÉMARRAGE RAPIDE - Tests Unitaires

## ⏱️ 30 Secondes pour Commencer

### Option 1: Command Line (La plus rapide)
```bash
cd C:\Users\admin\Desktop\PIDEVJAVAFX-maramdh
mvn test
```

### Option 2: Script Windows
```bash
# Double-cliquez sur run_tests.bat
# Ou ouvrez PowerShell et tapez:
run_tests.bat
```

### Option 3: IDE (IntelliJ IDEA)
1. Ouvrir le projet
2. Faire un clic droit sur `src/test/java/org/example/pidev/services/`
3. Sélectionner "Run Tests"

---

## 📋 Fichiers Créés

| Fichier | Type | Emplacement |
|---------|------|-----------|
| **RecolteServiceTest.java** | Test JUnit 5 | `src/test/java/.../services/` |
| **RendementServiceTest.java** | Test JUnit 5 | `src/test/java/.../services/` |
| **GUIDE_TESTS_UNITAIRES.md** | Documentation | Racine du projet |
| **TESTS_UNITAIRES_RAPPORT.md** | Rapport | Racine du projet |
| **RESUME_FINAL_TESTS.md** | Résumé | Racine du projet |
| **VERIFICATION_TESTS.md** | Vérification | Racine du projet |
| **run_tests.bat** | Script | Racine du projet |
| **run_tests.sh** | Script | Racine du projet |

---

## 🧪 71 Tests Créés

### RecolteService: 31 tests
- Validation quantité (4)
- Validation date (5)
- Validation qualité (7)
- Validation type culture (4)
- Validation localisation (4)
- Validation globale (4)
- Tests modèle (3)

### RendementService: 40 tests
- Validation surface (6)
- Validation quantité (4)
- Validation productivité (4)
- Validation ID récolte (3)
- Validation globale (5)
- Calcul productivité (6)
- Tests modèle (4)
- Création auto (2)
- Supplémentaires (3)
- Autres (3)

---

## ✅ Résultat Attendu

Après `mvn test`, vous devriez voir:

```
[INFO] Running org.example.pidev.services.RecolteServiceTest
[INFO] Tests run: 31, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running org.example.pidev.services.RendementServiceTest
[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
```

---

## 🎯 Commandes Utiles

### Tous les tests
```bash
mvn test
```

### Tests RecolteService uniquement
```bash
mvn test -Dtest=RecolteServiceTest
```

### Tests RendementService uniquement
```bash
mvn test -Dtest=RendementServiceTest
```

### Un test spécifique
```bash
mvn test -Dtest=RecolteServiceTest#testValiderQuantiteNegative
```

### Avec rapports
```bash
mvn test -q
mvn test -v
```

### Nettoyer et tester
```bash
mvn clean test
```

---

## 📖 Documentation

- **Débutant?** → Lire `GUIDE_TESTS_UNITAIRES.md`
- **Veux voir tous les tests?** → Lire `TESTS_UNITAIRES_RAPPORT.md`
- **Résumé exécutif?** → Lire `RESUME_FINAL_TESTS.md`
- **Vérifier l'intégrité?** → Lire `VERIFICATION_TESTS.md`

---

## 🔧 Configuration

Les tests utilisent:
- **JUnit 5** (5.12.1) - Déjà dans pom.xml
- **Java 17+**
- **Maven 3.x**

Aucune installation supplémentaire requise! ✅

---

## 💡 Astuces

1. **Exécutez régulièrement** les tests pendant le dev
2. **Lisez les assertions** pour comprendre ce qui est testé
3. **Ajoutez vos propres tests** si vous modifiez le code
4. **Consultez la documentation** pour les détails

---

## 🚨 Dépannage

### Maven not found?
```bash
mvn --version
# Si erreur: installer Maven
```

### Tests ne compilent pas?
```bash
mvn clean compile
# Puis mvn test
```

### Voir plus de détails?
```bash
mvn test -X
```

---

## 📞 Besoin d'aide?

1. Vérifier `GUIDE_TESTS_UNITAIRES.md`
2. Lire `VERIFICATION_TESTS.md`
3. Consulter la [documentation JUnit 5](https://junit.org/junit5/)

---

**Créé le**: 16 Février 2026  
**Statut**: ✅ Complet et opérationnel

