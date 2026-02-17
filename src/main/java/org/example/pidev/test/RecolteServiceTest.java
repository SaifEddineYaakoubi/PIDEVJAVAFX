package org.example.pidev.test;

import org.example.pidev.models.Recolte;
import org.example.pidev.services.RecolteService;
import java.time.LocalDate;

/**
 * Tests unitaires pour le service RecolteService
 * Teste les validations et la logique métier du service
 */
public class RecolteServiceTest {

    private static RecolteService service;
    private static Recolte recolte;

    // =====================
    // Tests de validation - Quantité
    // =====================

    public static void testValiderQuantitePositive() {
        service = new RecolteService();
        try {
            service.validerQuantite(50.0);
            System.out.println("✅ Test PASSED: Quantité positive acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQuantiteZero() {
        service = new RecolteService();
        try {
            service.validerQuantite(0.0);
            System.out.println("✅ Test PASSED: Quantité zéro acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQuantiteNegative() {
        service = new RecolteService();
        try {
            service.validerQuantite(-10.0);
            System.out.println("❌ Test FAILED: Quantité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQuantiteTropGrande() {
        service = new RecolteService();
        try {
            service.validerQuantite(2_000_000.0);
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
    // Tests de validation - Date de récolte
    // =====================

    public static void testValiderDateRecolteValide() {
        service = new RecolteService();
        try {
            service.validerDateRecolte(LocalDate.now().minusDays(5));
            System.out.println("✅ Test PASSED: Date de récolte valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderDateRecolteAujourdhui() {
        service = new RecolteService();
        try {
            service.validerDateRecolte(LocalDate.now());
            System.out.println("✅ Test PASSED: Date d'aujourd'hui acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderDateRecolteNull() {
        service = new RecolteService();
        try {
            service.validerDateRecolte(null);
            System.out.println("❌ Test FAILED: Date null devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour date null");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderDateRecolteFuture() {
        service = new RecolteService();
        try {
            service.validerDateRecolte(LocalDate.now().plusDays(5));
            System.out.println("❌ Test FAILED: Date future devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("futur")) {
                System.out.println("✅ Test PASSED: Exception levée pour date future");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderDateRecolteTropAncienne() {
        service = new RecolteService();
        try {
            service.validerDateRecolte(LocalDate.now().minusYears(6));
            System.out.println("❌ Test FAILED: Date trop ancienne devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("antérieure à 5 ans")) {
                System.out.println("✅ Test PASSED: Exception levée pour date trop ancienne");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - Qualité
    // =====================

    public static void testValiderQualiteValide() {
        service = new RecolteService();
        try {
            service.validerQualite("Excellente");
            System.out.println("✅ Test PASSED: Qualité valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQualiteAvecEspaces() {
        service = new RecolteService();
        try {
            service.validerQualite("  Bonne qualité  ");
            System.out.println("✅ Test PASSED: Qualité avec espaces acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderQualiteNull() {
        service = new RecolteService();
        try {
            service.validerQualite(null);
            System.out.println("❌ Test FAILED: Qualité null devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour qualité null");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQualiteVide() {
        service = new RecolteService();
        try {
            service.validerQualite("");
            System.out.println("❌ Test FAILED: Qualité vide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour qualité vide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQualiteTropCourte() {
        service = new RecolteService();
        try {
            service.validerQualite("A");
            System.out.println("❌ Test FAILED: Qualité trop courte devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("au moins")) {
                System.out.println("✅ Test PASSED: Exception levée pour qualité trop courte");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQualiteTropLongue() {
        service = new RecolteService();
        try {
            String qualiteLongue = "A".repeat(101);
            service.validerQualite(qualiteLongue);
            System.out.println("❌ Test FAILED: Qualité trop longue devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("dépasser")) {
                System.out.println("✅ Test PASSED: Exception levée pour qualité trop longue");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderQualiteAvecCaracteresInterdits() {
        service = new RecolteService();
        try {
            service.validerQualite("Qualité<>\"'%;()&+");
            System.out.println("❌ Test FAILED: Qualité avec caractères interdits devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("caractères non autorisés")) {
                System.out.println("✅ Test PASSED: Exception levée pour caractères interdits");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - Type de culture
    // =====================

    public static void testValiderTypeCultureValide() {
        service = new RecolteService();
        try {
            service.validerTypeCulture("Tomate");
            System.out.println("✅ Test PASSED: Type de culture valide accepté");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderTypeCultureNull() {
        service = new RecolteService();
        try {
            service.validerTypeCulture(null);
            System.out.println("❌ Test FAILED: Type de culture null devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour type null");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderTypeCultureVide() {
        service = new RecolteService();
        try {
            service.validerTypeCulture("");
            System.out.println("❌ Test FAILED: Type de culture vide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour type vide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderTypeCultureTropLong() {
        service = new RecolteService();
        try {
            String typeLong = "A".repeat(101);
            service.validerTypeCulture(typeLong);
            System.out.println("❌ Test FAILED: Type de culture trop long devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("trop long")) {
                System.out.println("✅ Test PASSED: Exception levée pour type trop long");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation - Localisation
    // =====================

    public static void testValiderLocalisationValide() {
        service = new RecolteService();
        try {
            service.validerLocalisation("Champ A");
            System.out.println("✅ Test PASSED: Localisation valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderLocalisationNull() {
        service = new RecolteService();
        try {
            service.validerLocalisation(null);
            System.out.println("❌ Test FAILED: Localisation null devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour localisation null");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderLocalisationVide() {
        service = new RecolteService();
        try {
            service.validerLocalisation("");
            System.out.println("❌ Test FAILED: Localisation vide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour localisation vide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderLocalisationTropLongue() {
        service = new RecolteService();
        try {
            String localisationLongue = "A".repeat(101);
            service.validerLocalisation(localisationLongue);
            System.out.println("❌ Test FAILED: Localisation trop longue devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("trop longue")) {
                System.out.println("✅ Test PASSED: Exception levée pour localisation trop longue");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests de validation globale
    // =====================

    public static void testValiderRecolteValide() {
        service = new RecolteService();
        recolte = new Recolte(
                100.0,
                LocalDate.now().minusDays(10),
                "Excellente",
                "Tomate",
                "Champ A"
        );
        try {
            service.valider(recolte);
            System.out.println("✅ Test PASSED: Récolte valide acceptée");
        } catch (Exception e) {
            System.out.println("❌ Test FAILED: " + e.getMessage());
        }
    }

    public static void testValiderRecolteQuantiteNegative() {
        service = new RecolteService();
        recolte = new Recolte(
                -50.0,
                LocalDate.now().minusDays(10),
                "Excellente",
                "Tomate",
                "Champ A"
        );
        try {
            service.valider(recolte);
            System.out.println("❌ Test FAILED: Récolte avec quantité négative devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("négative")) {
                System.out.println("✅ Test PASSED: Exception levée pour quantité négative");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderRecolteDateFuture() {
        service = new RecolteService();
        recolte = new Recolte(
                100.0,
                LocalDate.now().plusDays(5),
                "Excellente",
                "Tomate",
                "Champ A"
        );
        try {
            service.valider(recolte);
            System.out.println("❌ Test FAILED: Récolte avec date future devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("futur")) {
                System.out.println("✅ Test PASSED: Exception levée pour date future");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    public static void testValiderRecolteQualiteVide() {
        service = new RecolteService();
        recolte = new Recolte(
                100.0,
                LocalDate.now().minusDays(10),
                "",
                "Tomate",
                "Champ A"
        );
        try {
            service.valider(recolte);
            System.out.println("❌ Test FAILED: Récolte avec qualité vide devrait lever une exception");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("vide")) {
                System.out.println("✅ Test PASSED: Exception levée pour qualité vide");
            } else {
                System.out.println("❌ Test FAILED: Message d'erreur incorrect");
            }
        }
    }

    // =====================
    // Tests du modèle Recolte
    // =====================

    public static void testCreationRecoltesSansID() {
        recolte = new Recolte(
                50.0,
                LocalDate.now(),
                "Bonne",
                "Patate",
                "Champ B"
        );
        if (recolte.getQuantite() == 50.0 && recolte.getQualite().equals("Bonne") && recolte.getTypeCulture().equals("Patate")) {
            System.out.println("✅ Test PASSED: Création de récolte sans ID");
        } else {
            System.out.println("❌ Test FAILED: Création de récolte sans ID");
        }
    }

    public static void testCreationRecolteAvecID() {
        recolte = new Recolte(
                1,
                75.0,
                LocalDate.now(),
                "Très bonne",
                "Oignon",
                "Champ C"
        );
        if (recolte.getIdRecolte() == 1 && recolte.getQuantite() == 75.0) {
            System.out.println("✅ Test PASSED: Création de récolte avec ID");
        } else {
            System.out.println("❌ Test FAILED: Création de récolte avec ID");
        }
    }

    public static void testSettersGettersRecolte() {
        recolte = new Recolte();
        recolte.setIdRecolte(5);
        recolte.setQuantite(200.0);
        recolte.setDateRecolte(LocalDate.now());
        recolte.setQualite("Acceptable");
        recolte.setTypeCulture("Carotte");
        recolte.setLocalisation("Champ D");

        if (recolte.getIdRecolte() == 5 &&
                recolte.getQuantite() == 200.0 &&
                recolte.getQualite().equals("Acceptable") &&
                recolte.getTypeCulture().equals("Carotte") &&
                recolte.getLocalisation().equals("Champ D")) {
            System.out.println("✅ Test PASSED: Setters et getters de récolte");
        } else {
            System.out.println("❌ Test FAILED: Setters et getters de récolte");
        }
    }

    // =====================
    // Exécution de tous les tests
    // =====================

    public static void runAllTests() {
        System.out.println("\n========================================");
        System.out.println("🧪 TESTS UNITAIRES - RECOLTE SERVICE");
        System.out.println("========================================\n");

        System.out.println("--- Tests Quantité ---");
        testValiderQuantitePositive();
        testValiderQuantiteZero();
        testValiderQuantiteNegative();
        testValiderQuantiteTropGrande();

        System.out.println("\n--- Tests Date de Récolte ---");
        testValiderDateRecolteValide();
        testValiderDateRecolteAujourdhui();
        testValiderDateRecolteNull();
        testValiderDateRecolteFuture();
        testValiderDateRecolteTropAncienne();

        System.out.println("\n--- Tests Qualité ---");
        testValiderQualiteValide();
        testValiderQualiteAvecEspaces();
        testValiderQualiteNull();
        testValiderQualiteVide();
        testValiderQualiteTropCourte();
        testValiderQualiteTropLongue();
        testValiderQualiteAvecCaracteresInterdits();

        System.out.println("\n--- Tests Type de Culture ---");
        testValiderTypeCultureValide();
        testValiderTypeCultureNull();
        testValiderTypeCultureVide();
        testValiderTypeCultureTropLong();

        System.out.println("\n--- Tests Localisation ---");
        testValiderLocalisationValide();
        testValiderLocalisationNull();
        testValiderLocalisationVide();
        testValiderLocalisationTropLongue();

        System.out.println("\n--- Tests Validation Globale ---");
        testValiderRecolteValide();
        testValiderRecolteQuantiteNegative();
        testValiderRecolteDateFuture();
        testValiderRecolteQualiteVide();

        System.out.println("\n--- Tests Modèle Recolte ---");
        testCreationRecoltesSansID();
        testCreationRecolteAvecID();
        testSettersGettersRecolte();

        System.out.println("\n========================================");
        System.out.println("✅ Tous les tests sont terminés");
        System.out.println("========================================\n");
    }

    public static void main(String[] args) {
        runAllTests();
    }
}
