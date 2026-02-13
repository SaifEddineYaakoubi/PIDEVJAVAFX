package org.example.pidev.test;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * EXÉCUTION DE TOUS LES TESTS UNITAIRES
 * ═══════════════════════════════════════════════════════════════════════════
 * Cette classe exécute tous les tests unitaires du projet Smart Farm :
 *
 * 1. DBConnectionTest    - Tests de connexion à la base de données
 * 2. ModelsTest          - Tests des modèles (Parcelle, Culture)
 * 3. ParcelleServiceTest - Tests CRUD du service Parcelle
 * 4. CultureServiceTest  - Tests CRUD du service Culture
 *
 * Utilisation :
 *   - Exécuter cette classe pour lancer TOUS les tests
 *   - Ou exécuter chaque classe de test individuellement
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class AllTests {

    public static void main(String[] args) {

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                           ║");
        System.out.println("║              🌱 SMART FARM - SUITE DE TESTS UNITAIRES 🌱                  ║");
        System.out.println("║                                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        long startTime = System.currentTimeMillis();

        // ═══════════════════════════════════════════════════════════════════════
        // 1. TESTS DE CONNEXION À LA BASE DE DONNÉES
        // ═══════════════════════════════════════════════════════════════════════

        System.out.println("▶▶▶ EXÉCUTION : Tests DBConnection ◀◀◀");
        DBConnectionTest dbTest = new DBConnectionTest();
        dbTest.runAllTests();

        // ═══════════════════════════════════════════════════════════════════════
        // 2. TESTS DES MODÈLES
        // ═══════════════════════════════════════════════════════════════════════

        System.out.println("▶▶▶ EXÉCUTION : Tests Modèles ◀◀◀");
        ModelsTest modelsTest = new ModelsTest();
        modelsTest.runAllTests();

        // ═══════════════════════════════════════════════════════════════════════
        // 3. TESTS DU SERVICE PARCELLE
        // ═══════════════════════════════════════════════════════════════════════

        System.out.println("▶▶▶ EXÉCUTION : Tests ParcelleService ◀◀◀");
        ParcelleServiceTest parcelleTest = new ParcelleServiceTest();
        parcelleTest.runAllTests();

        // ═══════════════════════════════════════════════════════════════════════
        // 4. TESTS DU SERVICE CULTURE
        // ═══════════════════════════════════════════════════════════════════════

        System.out.println("▶▶▶ EXÉCUTION : Tests CultureService ◀◀◀");
        CultureServiceTest cultureTest = new CultureServiceTest();
        cultureTest.runAllTests();

        // ═══════════════════════════════════════════════════════════════════════
        // RÉSUMÉ FINAL
        // ═══════════════════════════════════════════════════════════════════════

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                           ║");
        System.out.println("║              ✅ TOUS LES TESTS ONT ÉTÉ EXÉCUTÉS ✅                        ║");
        System.out.println("║                                                                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                           ║");
        System.out.println("║   Tests exécutés :                                                        ║");
        System.out.println("║     • DBConnectionTest    - Connexion BD, Singleton, Tables               ║");
        System.out.println("║     • ModelsTest          - Constructeurs, Getters, Setters, toString     ║");
        System.out.println("║     • ParcelleServiceTest - Add, Update, Delete, GetAll, GetById          ║");
        System.out.println("║     • CultureServiceTest  - Add, Update, Delete, GetAll, GetById          ║");
        System.out.println("║                                                                           ║");
        System.out.printf("║   Temps d'exécution : %d ms                                              ║%n", duration);
        System.out.println("║                                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}

