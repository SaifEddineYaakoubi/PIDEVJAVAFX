package org.example.pidev.test;

import java.sql.*;

/**
 * Migration utilitaire: ajoute id_user aux tables pour l'isolation des données par utilisateur.
 * Exécuter une seule fois.
 */
public class MigrateUserIsolation {

    private static final String URL = "jdbc:mysql://localhost:3306/smartfarm";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🔄 Migration multi-tenant - Isolation des données par utilisateur");
        System.out.println("=".repeat(60));

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            // 1. Add id_user to tables that don't have it
            addColumnIfNotExists(conn, "recolte", "id_user", "INT DEFAULT NULL");
            addColumnIfNotExists(conn, "produit", "id_user", "INT DEFAULT NULL");
            addColumnIfNotExists(conn, "stock", "id_user", "INT DEFAULT NULL");
            addColumnIfNotExists(conn, "client", "id_user", "INT DEFAULT NULL");
            addColumnIfNotExists(conn, "recolte_archive", "id_user", "INT DEFAULT NULL");
            addColumnIfNotExists(conn, "utilisateur", "id_agriculteur", "INT DEFAULT NULL");

            // 2. Update existing recolte data
            System.out.println("\n📊 Mise à jour des récoltes...");
            executeUpdate(conn, "UPDATE recolte SET id_user = 2 WHERE id_user IS NULL AND (localisation LIKE '%Mornag%' OR localisation LIKE '%éja%' OR localisation LIKE '%Beja%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 3 WHERE id_user IS NULL AND (localisation LIKE '%Sfax%' OR localisation LIKE '%Hammamet%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 4 WHERE id_user IS NULL AND (localisation LIKE '%Tabarka%' OR localisation LIKE '%libia%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 6 WHERE id_user IS NULL AND (localisation LIKE '%Sidi Bouzid%' OR localisation LIKE '%Tozeur%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 8 WHERE id_user IS NULL AND (localisation LIKE '%Kairouan%' OR localisation LIKE '%Grombalia%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 9 WHERE id_user IS NULL AND (localisation LIKE '%Bizerte%' OR localisation LIKE '%Menzel%')");
            executeUpdate(conn, "UPDATE recolte SET id_user = 10 WHERE id_user IS NULL AND (localisation LIKE '%Kasserine%' OR localisation LIKE '%Siliana%')");
            // Fallback: assign any remaining to user 2
            executeUpdate(conn, "UPDATE recolte SET id_user = 2 WHERE id_user IS NULL");

            // 3. Update produit data
            System.out.println("📦 Mise à jour des produits...");
            executeUpdate(conn, "UPDATE produit SET id_user = 2 WHERE id_user IS NULL AND id_produit IN (1, 3, 4, 19, 20)");
            executeUpdate(conn, "UPDATE produit SET id_user = 3 WHERE id_user IS NULL AND id_produit IN (2, 6, 7)");
            executeUpdate(conn, "UPDATE produit SET id_user = 4 WHERE id_user IS NULL AND id_produit IN (8, 15)");
            executeUpdate(conn, "UPDATE produit SET id_user = 6 WHERE id_user IS NULL AND id_produit IN (5, 9, 10)");
            executeUpdate(conn, "UPDATE produit SET id_user = 8 WHERE id_user IS NULL AND id_produit IN (11, 14, 16, 18)");
            executeUpdate(conn, "UPDATE produit SET id_user = 9 WHERE id_user IS NULL AND id_produit IN (12, 13, 17)");
            // Fallback
            executeUpdate(conn, "UPDATE produit SET id_user = 2 WHERE id_user IS NULL");

            // 4. Update stock: match with produit's id_user
            System.out.println("📋 Mise à jour des stocks...");
            executeUpdate(conn, "UPDATE stock s JOIN produit p ON s.id_produit = p.id_produit SET s.id_user = p.id_user WHERE s.id_user IS NULL");

            // 5. Update client: match via vente
            System.out.println("👥 Mise à jour des clients...");
            executeUpdate(conn, "UPDATE client c JOIN (SELECT DISTINCT id_client, MIN(id_user) as id_user FROM vente GROUP BY id_client) v ON c.id_client = v.id_client SET c.id_user = v.id_user WHERE c.id_user IS NULL");
            executeUpdate(conn, "UPDATE client SET id_user = 2 WHERE id_user IS NULL");

            // 6. Update archives
            System.out.println("🗄️ Mise à jour des archives...");
            executeUpdate(conn, "UPDATE recolte_archive SET id_user = 2 WHERE id_user IS NULL AND (localisation LIKE '%Mornag%' OR localisation LIKE '%éja%')");
            executeUpdate(conn, "UPDATE recolte_archive SET id_user = 3 WHERE id_user IS NULL AND localisation LIKE '%Sfax%'");
            executeUpdate(conn, "UPDATE recolte_archive SET id_user = 6 WHERE id_user IS NULL AND localisation LIKE '%Sidi Bouzid%'");
            executeUpdate(conn, "UPDATE recolte_archive SET id_user = 9 WHERE id_user IS NULL AND localisation LIKE '%Tozeur%'");
            executeUpdate(conn, "UPDATE recolte_archive SET id_user = 2 WHERE id_user IS NULL");

            // 7. Link RESPONSABLE_STOCK to AGRICULTEUR
            System.out.println("🔗 Liaison responsables stock → agriculteurs...");
            executeUpdate(conn, "UPDATE utilisateur SET id_agriculteur = 2 WHERE id_user = 5 AND id_agriculteur IS NULL");
            executeUpdate(conn, "UPDATE utilisateur SET id_agriculteur = 4 WHERE id_user = 7 AND id_agriculteur IS NULL");

            conn.commit();

            // 8. Verify
            System.out.println("\n✅ Migration terminée ! Vérification:");
            verify(conn, "recolte");
            verify(conn, "produit");
            verify(conn, "stock");
            verify(conn, "client");
            verify(conn, "recolte_archive");

            System.out.println("\n🔗 Responsables Stock:");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_user, nom, prenom, role, id_agriculteur FROM utilisateur WHERE role = 'RESPONSABLE_STOCK'")) {
                while (rs.next()) {
                    System.out.printf("   User %d (%s %s) → Agriculteur ID: %s%n",
                            rs.getInt("id_user"), rs.getString("nom"), rs.getString("prenom"),
                            rs.getObject("id_agriculteur") != null ? rs.getInt("id_agriculteur") : "NON LIÉ");
                }
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ MIGRATION MULTI-TENANT TERMINÉE AVEC SUCCÈS");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.err.println("❌ Erreur migration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addColumnIfNotExists(Connection conn, String table, String column, String definition) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, table, column);
            if (rs.next()) {
                System.out.println("   ✓ " + table + "." + column + " existe déjà");
            } else {
                String sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition;
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                    System.out.println("   ✅ " + table + "." + column + " ajouté");
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("   ⚠️ " + table + "." + column + ": " + e.getMessage());
        }
    }

    private static void executeUpdate(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            int rows = st.executeUpdate(sql);
            if (rows > 0) {
                System.out.println("   → " + rows + " ligne(s) mise(s) à jour");
            }
        }
    }

    private static void verify(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) as total, COUNT(id_user) as with_user FROM " + table)) {
            if (rs.next()) {
                System.out.printf("   %s: %d total, %d avec id_user%n", table, rs.getInt("total"), rs.getInt("with_user"));
            }
        }
    }
}

