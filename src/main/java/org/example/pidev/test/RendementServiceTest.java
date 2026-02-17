package org.example.pidev.test;

import org.example.pidev.models.Rendement;
import org.example.pidev.services.RendementService;

/**
 * Tests unitaires pour le service RendementService
 * Teste les validations, calculs et la logique métier du service
 */
public class RendementServiceTest {

    private static RendementService service;
    private static Rendement rendement;

    // =====================
    // Tests de validation - Surface exploitée
    // =====================

    public static void testValiderSurfaceExploiteeValide() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(5.0);
            System.out.println("✅ Test PASSED: Surface exploitée valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderSurfaceExploiteeMinimum() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(0.01);
            System.out.println("✅ Test PASSED: Surface au minimum acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderSurfaceExploiteeMaximum() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(10000.0);
            System.out.println("✅ Test PASSED: Surface au maximum acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderSurfaceExploiteeInferieurMinimum() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(0.001);
            System.out.println("❌ Test FAILED: Surface trop petite devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("minimum")) {
                System.out.println("✅ Test PASSED: Exception levée pour surface trop petite");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderSurfaceExploiteeDepasserMaximum() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(10001.0);
            System.out.println("❌ Test FAILED: Surface trop grande devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("dépasser")) {
                System.out.println("✅ Test PASSED: Exception levée pour surface trop grande");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderSurfaceExploiteeNegative() {
        service = new RendementService();
        try {
            service.validerSurfaceExploitee(-5.0);
            System.out.println("❌ Test FAILED: Surface négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("minimum")) {
                System.out.println("✅ Test PASSED: Exception levée pour surface négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - Quantité totale
    // =====================

    public static void testValiderQuantiteTotaleValide() {
        service = new RendementService();
        try {
            service.validerQuantiteTotale(500.0);
            System.out.println("✅ Test PASSED: Quantité totale valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQuantiteTotaleZero() {
        service = new RendementService();
        try {
            service.validerQuantiteTotale(0.0);
            System.out.println("✅ Test PASSED: Quantité totale zéro acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQuantiteTotaleNegative() {
        service = new RendementService();
        try {
            service.validerQuantiteTotale(-100.0);
            System.out.println("❌ Test FAILED: Quantité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQuantiteTotaleTropGrande() {
        service = new RendementService();
        try {
            service.validerQuantiteTotale(2_000_000.0);
            System.out.println("❌ Test FAILED: Quantité trop grande devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("trop grande")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité trop grande");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - Productivité
    // =====================

    public static void testValiderProductiviteValide() {
        service = new RendementService();
        try {
            service.validerProductivite(50.0);
            System.out.println("✅ Test PASSED: Productivité valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderProductiviteZero() {
        service = new RendementService();
        try {
            service.validerProductivite(0.0);
            System.out.println("✅ Test PASSED: Productivité zéro acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderProductiviteNegative() {
        service = new RendementService();
        try {
            service.validerProductivite(-10.0);
            System.out.println("❌ Test FAILED: Productivité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour productivité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderProductiviteTropGrande() {
        service = new RendementService();
        try {
            service.validerProductivite(2_000_000.0);
            System.out.println("❌ Test FAILED: Productivité trop grande devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("trop grande")) {
                System.out.println("✅ Test PASSED: Exception levée pour productivité trop grande");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - ID Récolte
    // =====================

    public static void testValiderIdRecolteValide() {
        service = new RendementService();
        try {
            service.validerIdRecolte(1);
            System.out.println("✅ Test PASSED: ID récolte positif accepté");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderIdRecolteZero() {
        service = new RendementService();
        try {
            service.validerIdRecolte(0);
            System.out.println("❌ Test FAILED: ID récolte zéro devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("positif")) {
                System.out.println("✅ Test PASSED: Exception levée pour ID zéro");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderIdRecolteNegatif() {
        service = new RendementService();
        try {
            service.validerIdRecolte(-1);
            System.out.println("❌ Test FAILED: ID récolte négatif devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("positif")) {
                System.out.println("✅ Test PASSED: Exception levée pour ID négatif");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation globale
    // =====================

    public static void testValiderRendementValide() {
        service = new RendementService();
        rendement = new Rendement(10.0, 500.0, 50.0, 1);
        try {
            service.valider(rendement);
            System.out.println("✅ Test PASSED: Rendement valide accepté");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderRendementSurfaceInvalide() {
        service = new RendementService();
        rendement = new Rendement(-5.0, 500.0, 50.0, 1);
        try {
            service.valider(rendement);
            System.out.println("❌ Test FAILED: Rendement avec surface invalide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("minimum")) {
                System.out.println("✅ Test PASSED: Exception levée pour surface invalide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderRendementQuantiteNegative() {
        service = new RendementService();
        rendement = new Rendement(10.0, -100.0, 50.0, 1);
        try {
            service.valider(rendement);
            System.out.println("❌ Test FAILED: Rendement avec quantité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderRendementProductiviteNegative() {
        service = new RendementService();
        rendement = new Rendement(10.0, 500.0, -20.0, 1);
        try {
            service.valider(rendement);
            System.out.println("❌ Test FAILED: Rendement avec productivité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour productivité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderRendementIdRecolteInvalide() {
        service = new RendementService();
        rendement = new Rendement(10.0, 500.0, 50.0, -1);
        try {
            service.valider(rendement);
            System.out.println("❌ Test FAILED: Rendement avec ID invalide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("positif")) {
                System.out.println("✅ Test PASSED: Exception levée pour ID invalide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de calcul - Productivité
    // =====================

    public static void testCalculerProductivite() {
        service = new RendementService();
        try {
            double productivite = service.calculerProductivite(500.0, 10.0);
            if (Math.abs(productivite - 50.0) < 0.01) {
                System.out.println("✅ Test PASSED: Productivité calculée correctement (50.0)");
            } else {
                System.out.println("❌ Test FAILED: Productivité incorrecte (" + productivite + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testCalculerProductiviteDifferentes() {
        service = new RendementService();
        try {
            double productivite = service.calculerProductivite(1000.0, 20.0);
            if (Math.abs(productivite - 50.0) < 0.01) {
                System.out.println("✅ Test PASSED: Productivité calculée avec valeurs différentes");
            } else {
                System.out.println("❌ Test FAILED: Productivité incorrecte (" + productivite + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testCalculerProductiviteQuantiteZero() {
        service = new RendementService();
        try {
            double productivite = service.calculerProductivite(0.0, 10.0);
            if (Math.abs(productivite - 0.0) < 0.01) {
                System.out.println("✅ Test PASSED: Productivité avec quantité zéro acceptée");
            } else {
                System.out.println("❌ Test FAILED: Productivité incorrecte");
            }
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testCalculerProductiviteSurfaceZero() {
        service = new RendementService();
        try {
            service.calculerProductivite(500.0, 0.0);
            System.out.println("❌ Test FAILED: Surface zéro devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("positive")) {
                System.out.println("✅ Test PASSED: Exception levée pour surface zéro");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testCalculerProductiviteQuantiteNegative() {
        service = new RendementService();
        try {
            service.calculerProductivite(-100.0, 10.0);
            System.out.println("❌ Test FAILED: Quantité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testCalculerProductiviteDepasseLimites() {
        service = new RendementService();
        try {
            service.calculerProductivite(1_000_000_000.0, 0.5);
            System.out.println("❌ Test FAILED: Productivité dépassant limites devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("limites acceptables")) {
                System.out.println("✅ Test PASSED: Exception levée pour productivité excessive");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests du modèle Rendement
    // =====================

    public static void testCreationRendementSansID() {
        rendement = new Rendement(5.0, 250.0, 50.0, 1);
        if (rendement.getSurfaceExploitee() == 5.0 &&
                rendement.getQuantiteTotale() == 250.0 &&
                rendement.getProductivite() == 50.0 &&
                rendement.getIdRecolte() == 1) {
            System.out.println("✅ Test PASSED: Création de rendement sans ID");
        } else {
            System.out.println("❌ Test FAILED: Création de rendement sans ID");
        }
    }

    public static void testCreationRendementAvecID() {
        rendement = new Rendement(1, 15.0, 750.0, 50.0, 2);
        if (rendement.getIdRendement() == 1 &&
                rendement.getSurfaceExploitee() == 15.0 &&
                rendement.getQuantiteTotale() == 750.0 &&
                rendement.getProductivite() == 50.0 &&
                rendement.getIdRecolte() == 2) {
            System.out.println("✅ Test PASSED: Création de rendement avec ID");
        } else {
            System.out.println("❌ Test FAILED: Création de rendement avec ID");
        }
    }

    public static void testSettersGettersRendement() {
        rendement = new Rendement();
        rendement.setIdRendement(5);
        rendement.setSurfaceExploitee(8.5);
        rendement.setQuantiteTotale(425.0);
        rendement.setProductivite(50.0);
        rendement.setIdRecolte(3);

        if (rendement.getIdRendement() == 5 &&
                rendement.getSurfaceExploitee() == 8.5 &&
                rendement.getQuantiteTotale() == 425.0 &&
                rendement.getProductivite() == 50.0 &&
                rendement.getIdRecolte() == 3) {
            System.out.println("✅ Test PASSED: Setters et getters de rendement");
        } else {
            System.out.println("❌ Test FAILED: Setters et getters de rendement");
        }
    }

    public static void testConstructeurParDefautRendement() {
        rendement = new Rendement();
        if (rendement != null) {
            System.out.println("✅ Test PASSED: Constructeur par défaut crée un rendement");
        } else {
            System.out.println("❌ Test FAILED: Constructeur par défaut");
        }
    }

    // =====================
    // Tests de créations automatiques
    // =====================

    public static void testCreerRendementAutomatique() {
        service = new RendementService();
        rendement = service.creerRendementAutomatique(500.0, 10.0, 1);
        if (rendement != null &&
                rendement.getSurfaceExploitee() == 10.0 &&
                rendement.getQuantiteTotale() == 500.0 &&
                Math.abs(rendement.getProductivite() - 50.0) < 0.01 &&
                rendement.getIdRecolte() == 1) {
            System.out.println("✅ Test PASSED: Rendement créé automatiquement avec productivité calculée");
        } else {
            System.out.println("❌ Test FAILED: Création automatique de rendement");
        }
    }

    public static void testCreerRendementAutomatiqueInvalide() {
        service = new RendementService();
        rendement = service.creerRendementAutomatique(500.0, 0.0, 1);
        if (rendement == null) {
            System.out.println("✅ Test PASSED: Rendement retourne null avec paramètres invalides");
        } else {
            System.out.println("❌ Test FAILED: Rendement devrait être null");
        }
    }

    // =====================
    // Tests de calculs supplémentaires
    // =====================

    public static void testCalculerProductiviteTresPetite() {
        service = new RendementService();
        try {
            double productivite = service.calculerProductivite(1.0, 10000.0);
            if (Math.abs(productivite - 0.0001) < 0.00001) {
                System.out.println("✅ Test PASSED: Productivité très petite calculée correctement");
            } else {
                System.out.println("❌ Test FAILED: Productivité très petite incorrecte");
            }
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testCalculerProductiviteTresGrandeMaisAcceptable() {
        service = new RendementService();
        try {
            double productivite = service.calculerProductivite(999_999.0, 1.0);
            if (productivite < 1_000_000) {
                System.out.println("✅ Test PASSED: Productivité très grande mais acceptable");
            } else {
                System.out.println("❌ Test FAILED: Productivité dépasse les limites");
            }
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValidationRendementComplet() {
        service = new RendementService();
        rendement = new Rendement(1, 7.5, 375.0, 50.0, 5);
        try {
            service.valider(rendement);
            System.out.println("✅ Test PASSED: Rendement complet avec tous paramètres valides");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    // =====================
    // Exécution de tous les tests
    // =====================

    public static void runAllTests() {
        System.out.println("\n========================================");
        System.out.println("🧪 TESTS UNITAIRES - RENDEMENT SERVICE");
        System.out.println("========================================\n");

        System.out.println("--- Tests Surface Exploitée ---");
        testValiderSurfaceExploiteeValide();
        testValiderSurfaceExploiteeMinimum();
        testValiderSurfaceExploiteeMaximum();
        testValiderSurfaceExploiteeInferieurMinimum();
        testValiderSurfaceExploiteeDepasserMaximum();
        testValiderSurfaceExploiteeNegative();

        System.out.println("\n--- Tests Quantité Totale ---");
        testValiderQuantiteTotaleValide();
        testValiderQuantiteTotaleZero();
        testValiderQuantiteTotaleNegative();
        testValiderQuantiteTotaleTropGrande();

        System.out.println("\n--- Tests Productivité ---");
        testValiderProductiviteValide();
        testValiderProductiviteZero();
        testValiderProductiviteNegative();
        testValiderProductiviteTropGrande();

        System.out.println("\n--- Tests ID Récolte ---");
        testValiderIdRecolteValide();
        testValiderIdRecolteZero();
        testValiderIdRecolteNegatif();

        System.out.println("\n--- Tests Validation Globale ---");
        testValiderRendementValide();
        testValiderRendementSurfaceInvalide();
        testValiderRendementQuantiteNegative();
        testValiderRendementProductiviteNegative();
        testValiderRendementIdRecolteInvalide();

        System.out.println("\n--- Tests Calcul Productivité ---");
        testCalculerProductivite();
        testCalculerProductiviteDifferentes();
        testCalculerProductiviteQuantiteZero();
        testCalculerProductiviteSurfaceZero();
        testCalculerProductiviteQuantiteNegative();
        testCalculerProductiviteDepasseLimites();

        System.out.println("\n--- Tests Modèle Rendement ---");
        testCreationRendementSansID();
        testCreationRendementAvecID();
        testSettersGettersRendement();
        testConstructeurParDefautRendement();

        System.out.println("\n--- Tests Création Automatique ---");
        testCreerRendementAutomatique();
        testCreerRendementAutomatiqueInvalide();

        System.out.println("\n--- Tests Supplémentaires ---");
        testCalculerProductiviteTresPetite();
        testCalculerProductiviteTresGrandeMaisAcceptable();
        testValidationRendementComplet();

        System.out.println("\n========================================");
        System.out.println("✅ Tous les tests sont terminés");
        System.out.println("========================================\n");
    }

    public static void main(String[] args) {
        runAllTests();
    }
}
