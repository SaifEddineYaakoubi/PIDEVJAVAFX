package org.example.pidev.test;

import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTS UNITAIRES - DBConnection (Connexion Base de Données)
 * ═══════════════════════════════════════════════════════════════════════════
 * Cette classe teste la connexion à la base de données :
 * - testConnection()       : Test de connexion
 * - testSingleton()        : Test du pattern Singleton
 * - testDatabaseMetadata() : Test des métadonnées de la BD
 * - testTableExists()      : Test de l'existence des tables
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class DBConnectionTest {

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1 : CONNEXION À LA BASE DE DONNÉES
    // ═══════════════════════════════════════════════════════════════════════

    public void testConnection() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 1 : CONNEXION À LA BASE DE DONNÉES");
        System.out.println("═══════════════════════════════════════════════════");

        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    System.out.println("✅ TEST RÉUSSI : Connexion établie");
                    System.out.println("   → URL : " + connection.getMetaData().getURL());
                    System.out.println("   → Utilisateur : " + connection.getMetaData().getUserName());
                    System.out.println("   → Driver : " + connection.getMetaData().getDriverName());
                } else {
                    System.out.println("❌ TEST ÉCHOUÉ : Connexion fermée");
                }
            } catch (Exception e) {
                System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
            }
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Connexion null");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 2 : PATTERN SINGLETON
    // ═══════════════════════════════════════════════════════════════════════

    public void testSingleton() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 2 : PATTERN SINGLETON");
        System.out.println("═══════════════════════════════════════════════════");

        // Récupérer deux références
        Connection conn1 = DBConnection.getConnection();
        Connection conn2 = DBConnection.getConnection();

        // Vérifier qu'il s'agit de la même instance
        if (conn1 == conn2) {
            System.out.println("✅ TEST RÉUSSI : Les deux références pointent vers la même connexion");
            System.out.println("   → conn1.hashCode() = " + System.identityHashCode(conn1));
            System.out.println("   → conn2.hashCode() = " + System.identityHashCode(conn2));
        } else {
            System.out.println("❌ TEST ÉCHOUÉ : Connexions différentes (Singleton non respecté)");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 3 : MÉTADONNÉES DE LA BASE DE DONNÉES
    // ═══════════════════════════════════════════════════════════════════════

    public void testDatabaseMetadata() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 3 : MÉTADONNÉES DE LA BASE DE DONNÉES");
        System.out.println("═══════════════════════════════════════════════════");

        Connection connection = DBConnection.getConnection();

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            System.out.println("✅ TEST RÉUSSI : Métadonnées récupérées");
            System.out.println("   → Nom BD : " + metaData.getDatabaseProductName());
            System.out.println("   → Version BD : " + metaData.getDatabaseProductVersion());
            System.out.println("   → Driver JDBC : " + metaData.getDriverName());
            System.out.println("   → Version Driver : " + metaData.getDriverVersion());
            System.out.println("   → URL : " + metaData.getURL());

        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4 : EXISTENCE DES TABLES
    // ═══════════════════════════════════════════════════════════════════════

    public void testTableExists() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 4 : EXISTENCE DES TABLES");
        System.out.println("═══════════════════════════════════════════════════");

        Connection connection = DBConnection.getConnection();
        String[] tables = {"utilisateur", "parcelle", "culture", "recolte", "produit", "stock", "client", "vente", "ligne_vente", "alerte"};

        int tablesExistantes = 0;

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            for (String table : tables) {
                ResultSet rs = metaData.getTables(null, null, table, null);
                if (rs.next()) {
                    System.out.println("   ✅ Table '" + table + "' existe");
                    tablesExistantes++;
                } else {
                    System.out.println("   ❌ Table '" + table + "' n'existe pas");
                }
                rs.close();
            }

            System.out.println("\n--- RÉSUMÉ : " + tablesExistantes + "/" + tables.length + " tables trouvées ---");

        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5 : EXÉCUTION D'UNE REQUÊTE SIMPLE
    // ═══════════════════════════════════════════════════════════════════════

    public void testSimpleQuery() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("TEST 5 : EXÉCUTION D'UNE REQUÊTE SIMPLE");
        System.out.println("═══════════════════════════════════════════════════");

        Connection connection = DBConnection.getConnection();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 + 1 AS result");

            if (rs.next()) {
                int result = rs.getInt("result");
                if (result == 2) {
                    System.out.println("✅ TEST RÉUSSI : Requête exécutée (1 + 1 = " + result + ")");
                } else {
                    System.out.println("❌ TEST ÉCHOUÉ : Résultat incorrect");
                }
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.out.println("❌ TEST ÉCHOUÉ : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXÉCUTER TOUS LES TESTS
    // ═══════════════════════════════════════════════════════════════════════

    public void runAllTests() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        TESTS UNITAIRES - DBConnection                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        testConnection();        // Test 1 : Connexion
        testSingleton();         // Test 2 : Singleton
        testDatabaseMetadata();  // Test 3 : Métadonnées
        testTableExists();       // Test 4 : Tables
        testSimpleQuery();       // Test 5 : Requête simple

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FIN DES TESTS - DBConnection                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN - Point d'entrée pour exécuter les tests
    // ═══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        DBConnectionTest test = new DBConnectionTest();
        test.runAllTests();
    }
}

