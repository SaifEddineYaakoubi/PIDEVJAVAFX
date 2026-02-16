package org.example.pidev.test;

import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;

import java.time.LocalDate;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTS UNITAIRES - Modèles (Parcelle et Culture)
 * ═══════════════════════════════════════════════════════════════════════════
 * Cette classe teste les classes modèles :
 * - Constructeurs
 * - Getters et Setters
 * - Méthode toString()
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class ModelsTest {

    // ═══════════════════════════════════════════════════════════════════════
    // TESTS PARCELLE
    // ═══════════════════════════════════════════════════════════════════════

    public void testParcelleConstructeurVide() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : PARCELLE - Constructeur Vide");
        System.out.println("═══════════════════════════════════════════════════");

        Parcelle p = new Parcelle();

        if (p != null) {
            System.out.println("✅ TEST RÉUSSI : Constructeur vide fonctionne");
            System.out.println("   → ID par défaut : " + p.getIdParcelle());
            System.out.println("   → Nom par défaut : " + p.getNom());
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    public void testParcelleConstructeurComplet() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : PARCELLE - Constructeur Complet");
        System.out.println("═══════════════════════════════════════════════════");

        Parcelle p = new Parcelle(1, "Ma Parcelle", 500.0, "Tunis", "active", 1);

        boolean success = true;

        if (p.getIdParcelle() != 1) {
            System.out.println("❌ ID incorrect");
            success = false;
        }
        if (!"Ma Parcelle".equals(p.getNom())) {
            System.out.println("❌ Nom incorrect");
            success = false;
        }
        if (p.getSuperficie() != 500.0) {
            System.out.println("❌ Superficie incorrecte");
            success = false;
        }
        if (!"Tunis".equals(p.getLocalisation())) {
            System.out.println("❌ Localisation incorrecte");
            success = false;
        }
        if (!"active".equals(p.getEtat())) {
            System.out.println("❌ État incorrect");
            success = false;
        }
        if (p.getIdUser() != 1) {
            System.out.println("❌ ID User incorrect");
            success = false;
        }

        if (success) {
            System.out.println("✅ TEST RÉUSSI : Tous les attributs sont corrects");
            System.out.println("   → " + p.toString());
        }
    }

    public void testParcelleConstructeurSansId() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : PARCELLE - Constructeur Sans ID");
        System.out.println("═══════════════════════════════════════════════════");

        Parcelle p = new Parcelle("Nouvelle Parcelle", 300.0, "Sfax", "repos", 2);

        if (p.getIdParcelle() == 0 && "Nouvelle Parcelle".equals(p.getNom())) {
            System.out.println("✅ TEST RÉUSSI : Constructeur sans ID fonctionne");
            System.out.println("   → ID = 0 (sera généré par la BD)");
            System.out.println("   → Nom = " + p.getNom());
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    public void testParcelleSetters() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : PARCELLE - Setters");
        System.out.println("═══════════════════════════════════════════════════");

        Parcelle p = new Parcelle();

        p.setIdParcelle(10);
        p.setNom("Parcelle Modifiée");
        p.setSuperficie(1000.5);
        p.setLocalisation("Sousse");
        p.setEtat("exploitée");
        p.setIdUser(5);

        boolean success =
            p.getIdParcelle() == 10 &&
            "Parcelle Modifiée".equals(p.getNom()) &&
            p.getSuperficie() == 1000.5 &&
            "Sousse".equals(p.getLocalisation()) &&
            "exploitée".equals(p.getEtat()) &&
            p.getIdUser() == 5;

        if (success) {
            System.out.println("✅ TEST RÉUSSI : Tous les setters fonctionnent");
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Un ou plusieurs setters ne fonctionnent pas");
        }
    }

    public void testParcelleToString() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : PARCELLE - toString()");
        System.out.println("═══════════════════════════════════════════════════");

        Parcelle p = new Parcelle(1, "Test", 100.0, "Loc", "active", 1);
        String str = p.toString();

        if (str != null && str.contains("Parcelle") && str.contains("Test")) {
            System.out.println("✅ TEST RÉUSSI : toString() fonctionne");
            System.out.println("   → " + str);
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTS CULTURE
    // ═══════════════════════════════════════════════════════════════════════

    public void testCultureConstructeurVide() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : CULTURE - Constructeur Vide");
        System.out.println("═══════════════════════════════════════════════════");

        Culture c = new Culture();

        if (c != null) {
            System.out.println("✅ TEST RÉUSSI : Constructeur vide fonctionne");
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    public void testCultureConstructeurComplet() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : CULTURE - Constructeur Complet");
        System.out.println("═══════════════════════════════════════════════════");

        LocalDate datePlantation = LocalDate.of(2026, 1, 15);
        LocalDate dateRecolte = LocalDate.of(2026, 6, 15);

        Culture c = new Culture(1, "Blé", datePlantation, dateRecolte, "croissance", 5);

        boolean success = true;

        if (c.getIdCulture() != 1) {
            System.out.println("❌ ID incorrect");
            success = false;
        }
        if (!"Blé".equals(c.getTypeCulture())) {
            System.out.println("❌ Type incorrect");
            success = false;
        }
        if (!datePlantation.equals(c.getDatePlantation())) {
            System.out.println("❌ Date plantation incorrecte");
            success = false;
        }
        if (!dateRecolte.equals(c.getDateRecoltePrevue())) {
            System.out.println("❌ Date récolte incorrecte");
            success = false;
        }
        if (!"croissance".equals(c.getEtatCroissance())) {
            System.out.println("❌ État incorrect");
            success = false;
        }
        if (c.getIdParcelle() != 5) {
            System.out.println("❌ ID Parcelle incorrect");
            success = false;
        }

        if (success) {
            System.out.println("✅ TEST RÉUSSI : Tous les attributs sont corrects");
            System.out.println("   → " + c.toString());
        }
    }

    public void testCultureConstructeurSansId() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : CULTURE - Constructeur Sans ID");
        System.out.println("═══════════════════════════════════════════════════");

        Culture c = new Culture("Maïs", LocalDate.now(), LocalDate.now().plusMonths(4), "germination", 3);

        if (c.getIdCulture() == 0 && "Maïs".equals(c.getTypeCulture())) {
            System.out.println("✅ TEST RÉUSSI : Constructeur sans ID fonctionne");
            System.out.println("   → ID = 0 (sera généré par la BD)");
            System.out.println("   → Type = " + c.getTypeCulture());
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    public void testCultureSetters() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : CULTURE - Setters");
        System.out.println("═══════════════════════════════════════════════════");

        Culture c = new Culture();
        LocalDate date1 = LocalDate.of(2026, 3, 1);
        LocalDate date2 = LocalDate.of(2026, 8, 1);

        c.setIdCulture(20);
        c.setTypeCulture("Tomates");
        c.setDatePlantation(date1);
        c.setDateRecoltePrevue(date2);
        c.setEtatCroissance("floraison");
        c.setIdParcelle(10);
        c.setNomParcelle("Parcelle A");

        boolean success =
            c.getIdCulture() == 20 &&
            "Tomates".equals(c.getTypeCulture()) &&
            date1.equals(c.getDatePlantation()) &&
            date2.equals(c.getDateRecoltePrevue()) &&
            "floraison".equals(c.getEtatCroissance()) &&
            c.getIdParcelle() == 10 &&
            "Parcelle A".equals(c.getNomParcelle());

        if (success) {
            System.out.println("✅ TEST RÉUSSI : Tous les setters fonctionnent");
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Un ou plusieurs setters ne fonctionnent pas");
        }
    }

    public void testCultureToString() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST : CULTURE - toString()");
        System.out.println("═══════════════════════════════════════════════════");

        Culture c = new Culture(1, "Test", LocalDate.now(), LocalDate.now().plusMonths(2), "mature", 1);
        String str = c.toString();

        if (str != null && str.contains("Culture") && str.contains("Test")) {
            System.out.println("✅ TEST RÉUSSI : toString() fonctionne");
            System.out.println("   → " + str);
        } else {
            System.out.println("❌ TEST ÉCHOUÉ");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXÉCUTER TOUS LES TESTS
    // ═══════════════════════════════════════════════════════════════════════

    public void runAllTests() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        TESTS UNITAIRES - Modèles (Parcelle & Culture)         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        // Tests Parcelle
        System.out.println("\n▓▓▓ TESTS PARCELLE ▓▓▓");
        testParcelleConstructeurVide();
        testParcelleConstructeurComplet();
        testParcelleConstructeurSansId();
        testParcelleSetters();
        testParcelleToString();

        // Tests Culture
        System.out.println("\n▓▓▓ TESTS CULTURE ▓▓▓");
        testCultureConstructeurVide();
        testCultureConstructeurComplet();
        testCultureConstructeurSansId();
        testCultureSetters();
        testCultureToString();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FIN DES TESTS - Modèles                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN - Point d'entrée pour exécuter les tests
    // ═══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        ModelsTest test = new ModelsTest();
        test.runAllTests();
    }
}

