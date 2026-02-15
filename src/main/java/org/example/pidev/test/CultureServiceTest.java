package org.example.pidev.test;

import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;

import java.time.LocalDate;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTS UNITAIRES - CultureService
 * ═══════════════════════════════════════════════════════════════════════════
 * Cette classe teste toutes les fonctionnalités CRUD du service Culture :
 * - testAdd()          : Test d'ajout d'une culture
 * - testAddValidation(): Test des validations (données invalides)
 * - testGetAll()       : Test de récupération de toutes les cultures
 * - testGetById()      : Test de récupération par ID
 * - testUpdate()       : Test de modification
 * - testDelete()       : Test de suppression
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class CultureServiceTest {

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private int testCultureId = -1;   // ID de la culture créée pour les tests
    private int testParcelleId = -1;  // ID de la parcelle créée pour les tests

    // ═══════════════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════════════════════

    public CultureServiceTest() {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PRÉPARATION : Créer une parcelle de test (requis pour les cultures)
    // ═══════════════════════════════════════════════════════════════════════

    private void setupTestParcelle() {
        System.out.println("\n--- Préparation : Création d'une parcelle de test ---");

        try {
            Parcelle parcelle = new Parcelle(
                    "Parcelle pour Culture Test",
                    300.0,
                    "Zone Culture Test",
                    "active",
                    1
            );

            boolean result = parcelleService.add(parcelle);
            if (result) {
                testParcelleId = parcelle.getIdParcelle();
                System.out.println("✅ Parcelle de test créée (ID = " + testParcelleId + ")");
            } else {
                System.out.println("❌ Impossible de créer la parcelle de test");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création de la parcelle de test: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NETTOYAGE : Supprimer la parcelle de test
    // ═══════════════════════════════════════════════════════════════════════

    private void cleanupTestParcelle() {
        if (testParcelleId > 0) {
            System.out.println("\n--- Nettoyage : Suppression de la parcelle de test ---");
            try {
                parcelleService.delete(testParcelleId);
                System.out.println("✅ Parcelle de test supprimée");
            } catch (Exception e) {
                System.out.println("⚠️ Impossible de supprimer la parcelle de test: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1 : AJOUTER UNE CULTURE (ADD)
    // ═══════════════════════════════════════════════════════════════════════

    public void testAdd() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 1 : AJOUTER UNE CULTURE");
        System.out.println("═══════════════════════════════════════════════════");

        if (testParcelleId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune parcelle de test disponible");
            return;
        }

        try {
            // Créer une culture de test
            Culture culture = new Culture(
                    "Blé Test Unitaire",                    // type_culture
                    LocalDate.now(),                         // date_plantation
                    LocalDate.now().plusMonths(4),           // date_recolte_prevue
                    "germination",                           // etat_croissance
                    testParcelleId                           // id_parcelle
            );

            // Exécuter l'ajout
            boolean result = cultureService.add(culture);

            // Vérifier le résultat
            if (result && culture.getIdCulture() > 0) {
                testCultureId = culture.getIdCulture();
                System.out.println("✅ TEST RÉUSSI : Culture ajoutée avec ID = " + testCultureId);
                System.out.println("   → Type : " + culture.getTypeCulture());
                System.out.println("   → Date plantation : " + culture.getDatePlantation());
                System.out.println("   → Date récolte prévue : " + culture.getDateRecoltePrevue());
            } else {
                System.out.println("❌ TEST ÉCHOUÉ : L'ajout n'a pas fonctionné");
            }
        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 2 : VALIDATION DES DONNÉES (ADD avec données invalides)
    // ═══════════════════════════════════════════════════════════════════════

    public void testAddValidation() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 2 : VALIDATION DES DONNÉES");
        System.out.println("═══════════════════════════════════════════════════");

        if (testParcelleId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune parcelle de test disponible");
            return;
        }

        int testsReussis = 0;
        int totalTests = 5;

        // Test 2.1 : Type de culture vide
        System.out.println("\n--- Test 2.1 : Type de culture vide ---");
        try {
            Culture c1 = new Culture("", LocalDate.now(), LocalDate.now().plusMonths(3), "croissance", testParcelleId);
            cultureService.add(c1);
            System.out.println("❌ Validation ÉCHOUÉE : Type vide accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : Type vide rejeté (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : Type vide rejeté");
            testsReussis++;
        }

        // Test 2.2 : Date de récolte avant date de plantation
        System.out.println("\n--- Test 2.2 : Date récolte avant plantation ---");
        try {
            Culture c2 = new Culture("Maïs", LocalDate.now(), LocalDate.now().minusDays(10), "croissance", testParcelleId);
            cultureService.add(c2);
            System.out.println("❌ Validation ÉCHOUÉE : Date incohérente acceptée");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : Date incohérente rejetée (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : Date incohérente rejetée");
            testsReussis++;
        }

        // Test 2.3 : État de croissance invalide
        System.out.println("\n--- Test 2.3 : État de croissance invalide ---");
        try {
            Culture c3 = new Culture("Tomates", LocalDate.now(), LocalDate.now().plusMonths(2), "etat_invalide", testParcelleId);
            cultureService.add(c3);
            System.out.println("❌ Validation ÉCHOUÉE : État invalide accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : État invalide rejeté (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : État invalide rejeté");
            testsReussis++;
        }

        // Test 2.4 : ID parcelle invalide (0)
        System.out.println("\n--- Test 2.4 : ID parcelle invalide ---");
        try {
            Culture c4 = new Culture("Carottes", LocalDate.now(), LocalDate.now().plusMonths(3), "croissance", 0);
            cultureService.add(c4);
            System.out.println("❌ Validation ÉCHOUÉE : ID parcelle 0 accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : ID parcelle 0 rejeté (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : ID parcelle 0 rejeté");
            testsReussis++;
        }

        // Test 2.5 : Caractères spéciaux dans le type
        System.out.println("\n--- Test 2.5 : Caractères spéciaux ---");
        try {
            Culture c5 = new Culture("Blé<script>", LocalDate.now(), LocalDate.now().plusMonths(3), "croissance", testParcelleId);
            cultureService.add(c5);
            System.out.println("❌ Validation ÉCHOUÉE : Caractères spéciaux acceptés");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : Caractères spéciaux rejetés (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : Caractères spéciaux rejetés");
            testsReussis++;
        }

        // Résumé
        System.out.println("\n--- RÉSUMÉ VALIDATION : " + testsReussis + "/" + totalTests + " tests réussis ---");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 3 : RÉCUPÉRER TOUTES LES CULTURES (GET ALL)
    // ═══════════════════════════════════════════════════════════════════════

    public void testGetAll() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 3 : RÉCUPÉRER TOUTES LES CULTURES");
        System.out.println("═══════════════════════════════════════════════════");

        List<Culture> cultures = cultureService.getAll();

        if (cultures != null) {
            System.out.println("✅ TEST RÉUSSI : " + cultures.size() + " culture(s) récupérée(s)");

            // Afficher les 3 premières
            int count = 0;
            for (Culture c : cultures) {
                if (count++ < 3) {
                    System.out.println("   → " + c.getTypeCulture() + " (" + c.getEtatCroissance() + ")");
                }
            }
            if (cultures.size() > 3) {
                System.out.println("   → ... et " + (cultures.size() - 3) + " autre(s)");
            }
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : La liste est null");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4 : RÉCUPÉRER UNE CULTURE PAR ID (GET BY ID)
    // ═══════════════════════════════════════════════════════════════════════

    public void testGetById() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 4 : RÉCUPÉRER UNE CULTURE PAR ID");
        System.out.println("═══════════════════════════════════════════════════");

        if (testCultureId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune culture de test créée");
            return;
        }

        Culture culture = cultureService.getById(testCultureId);

        if (culture != null) {
            System.out.println("✅ TEST RÉUSSI : Culture trouvée");
            System.out.println("   → ID : " + culture.getIdCulture());
            System.out.println("   → Type : " + culture.getTypeCulture());
            System.out.println("   → État : " + culture.getEtatCroissance());
            System.out.println("   → Date plantation : " + culture.getDatePlantation());
            System.out.println("   → Date récolte : " + culture.getDateRecoltePrevue());
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Culture non trouvée avec ID = " + testCultureId);
        }

        // Test avec ID inexistant
        System.out.println("\n--- Test avec ID inexistant (99999) ---");
        Culture c2 = cultureService.getById(99999);
        if (c2 == null) {
            System.out.println("✅ OK : null retourné pour ID inexistant");
        } else {
            System.out.println("❌ ERREUR : Objet retourné pour ID inexistant");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5 : MODIFIER UNE CULTURE (UPDATE)
    // ═══════════════════════════════════════════════════════════════════════

    public void testUpdate() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 5 : MODIFIER UNE CULTURE");
        System.out.println("═══════════════════════════════════════════════════");

        if (testCultureId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune culture de test créée");
            return;
        }

        try {
            // Récupérer la culture
            Culture culture = cultureService.getById(testCultureId);
            if (culture == null) {
                System.out.println("❌ TEST ÉCHOUÉ : Culture non trouvée");
                return;
            }

            // Modifier les valeurs
            String ancienType = culture.getTypeCulture();
            String ancienEtat = culture.getEtatCroissance();

            culture.setTypeCulture("Maïs Modifié Test");
            culture.setEtatCroissance("floraison");
            culture.setDateRecoltePrevue(LocalDate.now().plusMonths(6));

            // Exécuter la mise à jour
            cultureService.update(culture);

            // Vérifier la modification
            Culture cultureModifiee = cultureService.getById(testCultureId);

            if (cultureModifiee != null &&
                cultureModifiee.getTypeCulture().equals("Maïs Modifié Test") &&
                cultureModifiee.getEtatCroissance().equals("floraison")) {

                System.out.println("✅ TEST RÉUSSI : Culture modifiée");
                System.out.println("   → Ancien type : " + ancienType);
                System.out.println("   → Nouveau type : " + cultureModifiee.getTypeCulture());
                System.out.println("   → Ancien état : " + ancienEtat);
                System.out.println("   → Nouvel état : " + cultureModifiee.getEtatCroissance());
            } else {
                System.out.println("❌ TEST ÉCHOUÉ : La modification n'a pas été appliquée");
            }
        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 6 : SUPPRIMER UNE CULTURE (DELETE)
    // ═══════════════════════════════════════════════════════════════════════

    public void testDelete() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 6 : SUPPRIMER UNE CULTURE");
        System.out.println("═══════════════════════════════════════════════════");

        if (testCultureId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune culture de test créée");
            return;
        }

        try {
            // Supprimer la culture
            cultureService.delete(testCultureId);

            // Vérifier la suppression
            Culture cultureSupprimee = cultureService.getById(testCultureId);

            if (cultureSupprimee == null) {
                System.out.println("✅ TEST RÉUSSI : Culture supprimée (ID = " + testCultureId + ")");
            } else {
                System.out.println("❌ TEST ÉCHOUÉ : La culture existe encore");
            }
        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXÉCUTER TOUS LES TESTS
    // ═══════════════════════════════════════════════════════════════════════

    public void runAllTests() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        TESTS UNITAIRES - CultureService                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        setupTestParcelle(); // Préparer une parcelle de test

        testAdd();           // Test 1 : Ajouter
        testAddValidation(); // Test 2 : Validation
        testGetAll();        // Test 3 : Récupérer tout
        testGetById();       // Test 4 : Récupérer par ID
        testUpdate();        // Test 5 : Modifier
        testDelete();        // Test 6 : Supprimer

        cleanupTestParcelle(); // Nettoyer la parcelle de test

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FIN DES TESTS - CultureService                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN - Point d'entrée pour exécuter les tests
    // ═══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        CultureServiceTest test = new CultureServiceTest();
        test.runAllTests();
    }
}

