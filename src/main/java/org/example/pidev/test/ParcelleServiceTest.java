package org.example.pidev.test;

import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTS UNITAIRES - ParcelleService
 * ═══════════════════════════════════════════════════════════════════════════
 * Cette classe teste toutes les fonctionnalités CRUD du service Parcelle :
 * - testAdd()          : Test d'ajout d'une parcelle
 * - testAddValidation(): Test des validations (données invalides)
 * - testGetAll()       : Test de récupération de toutes les parcelles
 * - testGetById()      : Test de récupération par ID
 * - testUpdate()       : Test de modification
 * - testDelete()       : Test de suppression
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class ParcelleServiceTest {

    private ParcelleService parcelleService;
    private int testParcelleId = -1; // ID de la parcelle créée pour les tests

    // ═══════════════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════════════════════

    public ParcelleServiceTest() {
        parcelleService = new ParcelleService();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1 : AJOUTER UNE PARCELLE (ADD)
    // ═══════════════════════════════════════════════════════════════════════

    public void testAdd() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 1 : AJOUTER UNE PARCELLE");
        System.out.println("═══════════════════════════════════════════════════");

        try {
            // Créer une parcelle de test
            Parcelle parcelle = new Parcelle(
                    "Parcelle Test Unitaire",  // nom
                    500.0,                      // superficie
                    "Zone Test - Tunis",        // localisation
                    "active",                   // état
                    1                           // id_user
            );

            // Exécuter l'ajout
            boolean result = parcelleService.add(parcelle);

            // Vérifier le résultat
            if (result && parcelle.getIdParcelle() > 0) {
                testParcelleId = parcelle.getIdParcelle();
                System.out.println("✅ TEST RÉUSSI : Parcelle ajoutée avec ID = " + testParcelleId);
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

        int testsReussis = 0;
        int totalTests = 4;

        // Test 2.1 : Nom vide
        System.out.println("\n--- Test 2.1 : Nom vide ---");
        try {
            Parcelle p1 = new Parcelle("", 100.0, "Localisation", "active", 1);
            parcelleService.add(p1);
            System.out.println("❌ Validation ÉCHOUÉE : Nom vide accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : Nom vide rejeté (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : Nom vide rejeté");
            testsReussis++;
        }

        // Test 2.2 : Superficie négative
        System.out.println("\n--- Test 2.2 : Superficie négative ---");
        try {
            Parcelle p2 = new Parcelle("Test", -50.0, "Localisation", "active", 1);
            parcelleService.add(p2);
            System.out.println("❌ Validation ÉCHOUÉE : Superficie négative acceptée");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : Superficie négative rejetée (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : Superficie négative rejetée");
            testsReussis++;
        }

        // Test 2.3 : État invalide
        System.out.println("\n--- Test 2.3 : État invalide ---");
        try {
            Parcelle p3 = new Parcelle("Test", 100.0, "Localisation", "etat_invalide", 1);
            parcelleService.add(p3);
            System.out.println("❌ Validation ÉCHOUÉE : État invalide accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation OK : État invalide rejeté (" + e.getMessage() + ")");
            testsReussis++;
        } catch (Exception e) {
            System.out.println("✅ Validation OK : État invalide rejeté");
            testsReussis++;
        }

        // Test 2.4 : Caractères spéciaux dans le nom
        System.out.println("\n--- Test 2.4 : Caractères spéciaux ---");
        try {
            Parcelle p4 = new Parcelle("Test<script>", 100.0, "Localisation", "active", 1);
            parcelleService.add(p4);
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
    // TEST 3 : RÉCUPÉRER TOUTES LES PARCELLES (GET ALL)
    // ═══════════════════════════════════════════════════════════════════════

    public void testGetAll() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 3 : RÉCUPÉRER TOUTES LES PARCELLES");
        System.out.println("═══════════════════════════════════════════════════");

        List<Parcelle> parcelles = parcelleService.getAll();

        if (parcelles != null) {
            System.out.println("✅ TEST RÉUSSI : " + parcelles.size() + " parcelle(s) récupérée(s)");

            // Afficher les 3 premières
            int count = 0;
            for (Parcelle p : parcelles) {
                if (count++ < 3) {
                    System.out.println("   → " + p.getNom() + " (" + p.getSuperficie() + " m²)");
                }
            }
            if (parcelles.size() > 3) {
                System.out.println("   → ... et " + (parcelles.size() - 3) + " autre(s)");
            }
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : La liste est null");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4 : RÉCUPÉRER UNE PARCELLE PAR ID (GET BY ID)
    // ═══════════════════════════════════════════════════════════════════════

    public void testGetById() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 4 : RÉCUPÉRER UNE PARCELLE PAR ID");
        System.out.println("═══════════════════════════════════════════════════");

        if (testParcelleId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune parcelle de test créée");
            return;
        }

        Parcelle parcelle = parcelleService.getById(testParcelleId);

        if (parcelle != null) {
            System.out.println("✅ TEST RÉUSSI : Parcelle trouvée");
            System.out.println("   → ID : " + parcelle.getIdParcelle());
            System.out.println("   → Nom : " + parcelle.getNom());
            System.out.println("   → Superficie : " + parcelle.getSuperficie() + " m²");
            System.out.println("   → Localisation : " + parcelle.getLocalisation());
            System.out.println("   → État : " + parcelle.getEtat());
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Parcelle non trouvée avec ID = " + testParcelleId);
        }

        // Test avec ID inexistant
        System.out.println("\n--- Test avec ID inexistant (99999) ---");
        Parcelle p2 = parcelleService.getById(99999);
        if (p2 == null) {
            System.out.println("✅ OK : null retourné pour ID inexistant");
        } else {
            System.out.println("❌ ERREUR : Objet retourné pour ID inexistant");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5 : MODIFIER UNE PARCELLE (UPDATE)
    // ═══════════════════════════════════════════════════════════════════════

    public void testUpdate() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 5 : MODIFIER UNE PARCELLE");
        System.out.println("═══════════════════════════════════════════════════");

        if (testParcelleId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune parcelle de test créée");
            return;
        }

        try {
            // Récupérer la parcelle
            Parcelle parcelle = parcelleService.getById(testParcelleId);
            if (parcelle == null) {
                System.out.println("❌ TEST ÉCHOUÉ : Parcelle non trouvée");
                return;
            }

            // Modifier les valeurs
            String ancienNom = parcelle.getNom();
            parcelle.setNom("Parcelle Modifiée Test");
            parcelle.setSuperficie(750.5);
            parcelle.setEtat("repos");

            // Exécuter la mise à jour
            parcelleService.update(parcelle);

            // Vérifier la modification
            Parcelle parcelleModifiee = parcelleService.getById(testParcelleId);

            if (parcelleModifiee != null &&
                parcelleModifiee.getNom().equals("Parcelle Modifiée Test") &&
                parcelleModifiee.getSuperficie() == 750.5 &&
                parcelleModifiee.getEtat().equals("repos")) {

                System.out.println("✅ TEST RÉUSSI : Parcelle modifiée");
                System.out.println("   → Ancien nom : " + ancienNom);
                System.out.println("   → Nouveau nom : " + parcelleModifiee.getNom());
                System.out.println("   → Nouvelle superficie : " + parcelleModifiee.getSuperficie() + " m²");
                System.out.println("   → Nouvel état : " + parcelleModifiee.getEtat());
            } else {
                System.out.println("❌ TEST ÉCHOUÉ : La modification n'a pas été appliquée");
            }
        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 6 : SUPPRIMER UNE PARCELLE (DELETE)
    // ═══════════════════════════════════════════════════════════════════════

    public void testDelete() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 6 : SUPPRIMER UNE PARCELLE");
        System.out.println("═══════════════════════════════════════════════════");

        if (testParcelleId <= 0) {
            System.out.println("⚠️ TEST IGNORÉ : Aucune parcelle de test créée");
            return;
        }

        try {
            // Supprimer la parcelle
            parcelleService.delete(testParcelleId);

            // Vérifier la suppression
            Parcelle parcelleSupprimee = parcelleService.getById(testParcelleId);

            if (parcelleSupprimee == null) {
                System.out.println("✅ TEST RÉUSSI : Parcelle supprimée (ID = " + testParcelleId + ")");
            } else {
                System.out.println("❌ TEST ÉCHOUÉ : La parcelle existe encore");
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
        System.out.println("║        TESTS UNITAIRES - ParcelleService                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        testAdd();           // Test 1 : Ajouter
        testAddValidation(); // Test 2 : Validation
        testGetAll();        // Test 3 : Récupérer tout
        testGetById();       // Test 4 : Récupérer par ID
        testUpdate();        // Test 5 : Modifier
        testDelete();        // Test 6 : Supprimer

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FIN DES TESTS - ParcelleService                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN - Point d'entrée pour exécuter les tests
    // ═══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        ParcelleServiceTest test = new ParcelleServiceTest();
        test.runAllTests();
    }
}

