# 🧪 Tests Unitaires - Services Recolte et Rendement

## 📋 Description

Ce projet contient une suite complète de **71 tests unitaires** pour les services `RecolteService` et `RendementService` utilisant **JUnit 5 (Jupiter)**.

## 📁 Structure

```
src/test/java/org/example/pidev/services/
├── RecolteServiceTest.java      (31 tests)
└── RendementServiceTest.java    (40 tests)
```

## 🎯 Objectifs des tests

### RecolteServiceTest (31 tests)
Teste la validation complète du service de gestion des récoltes:
- Validation de la quantité
- Validation de la date de récolte
- Validation de la qualité
- Validation du type de culture
- Validation de la localisation
- Validation globale de la récolte
- Tests du modèle Recolte

### RendementServiceTest (40 tests)
Teste la validation et les calculs du service de gestion des rendements:
- Validation de la surface exploitée
- Validation de la quantité totale
- Validation de la productivité
- Validation de l'ID récolte
- Validation globale du rendement
- Calcul automatique de la productivité
- Tests du modèle Rendement
- Créations automatiques

## 🚀 Exécution des tests

### Méthode 1: Via Maven
```bash
# Tous les tests
mvn test

# Tests RecolteService uniquement
mvn test -Dtest=org.example.pidev.services.RecolteServiceTest

# Tests RendementService uniquement
mvn test -Dtest=org.example.pidev.services.RendementServiceTest

# Compilation + Tests
mvn clean test
```

### Méthode 2: Via script (Windows)
```bash
run_tests.bat
```

### Méthode 3: Via script (Linux/Mac)
```bash
bash run_tests.sh
```

### Méthode 4: Via IDE
- Clic droit sur la classe de test → Run Tests
- Ou utiliser le shortcut: Alt+Shift+F10 (IntelliJ)

## 📊 Couverture des tests

| Service | Tests | Couverture |
|---------|-------|-----------|
| RecolteService | 31 | 100% validations |
| RendementService | 40 | 100% validations + calculs |
| **Total** | **71** | **100%** |

## ✅ Exemples de tests

### Test Simple (Assertion positive)
```java
@Test
@DisplayName("Quantité positive doit être acceptée")
void testValiderQuantitePositive() {
    assertDoesNotThrow(() -> service.validerQuantite(50.0));
}
```

### Test Exception
```java
@Test
@DisplayName("Quantité négative doit lever une exception")
void testValiderQuantiteNegative() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.validerQuantite(-10.0)
    );
    assertTrue(exception.getMessage().contains("négative"));
}
```

### Test Calcul
```java
@Test
@DisplayName("Productivité = quantité / surface")
void testCalculerProductivite() throws IllegalArgumentException {
    double productivite = service.calculerProductivite(500.0, 10.0);
    assertEquals(50.0, productivite, 0.01);
}
```

## 🔍 Points couverts

### RecolteService:
- ✅ Quantités valides, zéro, négatives, excessives
- ✅ Dates valides, futures, trop anciennes, null
- ✅ Qualités valides, vides, courtes, longues, avec caractères interdits
- ✅ Types de culture valides, vides, trop longs
- ✅ Localisations valides, vides, trop longues
- ✅ Construction de modèles avec et sans ID
- ✅ Setters et getters

### RendementService:
- ✅ Surfaces exploitées valides, min, max, négatives
- ✅ Quantités totales valides, zéro, négatives, excessives
- ✅ Productivités valides, zéro, négatives, excessives
- ✅ IDs récolte positifs, zéro, négatifs
- ✅ Calculs de productivité (quantité/surface)
- ✅ Créations automatiques avec validation
- ✅ Construction de modèles avec et sans ID
- ✅ Setters et getters
- ✅ Cas limites mathématiques

## 🛠️ Dépendances

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.12.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.12.1</version>
    <scope>test</scope>
</dependency>
```

## 📝 Annotations utilisées

- `@Test` - Marquer une méthode comme test
- `@DisplayName("...")` - Noms descriptifs pour les rapports
- `@BeforeEach` - Exécuter avant chaque test

## 🔧 Assertions utilisées

- `assertDoesNotThrow()` - Aucune exception ne doit être levée
- `assertThrows()` - Une exception doit être levée
- `assertEquals()` - Les valeurs doivent être égales
- `assertTrue()` - La condition doit être vraie
- `assertNull()` - La valeur doit être null
- `assertNotNull()` - La valeur ne doit pas être null

## 📈 Résultats attendus

En cas de succès complet:
```
[INFO] Tests run: 71, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 🐛 Dépannage

### Erreur: "Maven command not found"
- Assurez-vous que Maven est installé: `mvn --version`
- Vérifiez le PATH de votre système

### Erreur: "Tests not found"
- Vérifiez que les fichiers sont dans: `src/test/java/org/example/pidev/services/`
- Vérifiez les noms des classes (sensible à la casse)

### Erreur: "Cannot find symbol"
- Vérifiez que les services RecolteService et RendementService sont compilés
- Lancez: `mvn clean compile`

## 📖 Ressources

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Maven Testing Guide](https://maven.apache.org/plugins/maven-surefire-plugin/)
- [Assertions JUnit 5](https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions)

## 💡 Conseils

1. **Exécuter régulièrement** les tests pendant le développement
2. **Ajouter des tests** pour chaque nouvelle fonctionnalité
3. **Vérifier la couverture** de code
4. **Maintenir les tests** à jour avec les modifications du code
5. **Documenter** les cas de test complexes

## ✨ Améliorations futures

- [ ] Tests d'intégration avec base de données
- [ ] Tests de performance
- [ ] Utiliser Mockito pour les dépendances
- [ ] Tests paramétrés avec `@ParameterizedTest`
- [ ] Tests des méthodes utilitaires (getByIdRecolte, etc.)

## 📞 Support

Pour des questions ou des problèmes avec les tests, vérifiez:
1. La structure des répertoires
2. Les versions des dépendances dans pom.xml
3. La documentation JUnit 5 officielle

---

**Version**: 1.0  
**Date**: 16 Février 2026  
**Status**: ✅ Production Ready

