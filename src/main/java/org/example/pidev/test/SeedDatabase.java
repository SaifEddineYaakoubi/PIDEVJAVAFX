package org.example.pidev.test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utilitaire pour remplir la base de données smartfarm avec des données réalistes.
 * Exécute le fichier sql/seed_data.sql via JDBC.
 *
 * Pour exécuter, lancez depuis l'IDE (clic droit → Run)
 * ou importez sql/seed_data.sql dans phpMyAdmin.
 */
public class SeedDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("\n============================================================");
        System.out.println("  SMART FARM — SEED DATABASE");
        System.out.println("============================================================\n");

        try {
            // 1. Trouver et lire le fichier SQL
            Path sqlFile = findSqlFile();
            String sql = Files.readString(sqlFile, StandardCharsets.UTF_8);
            System.out.println("Fichier SQL charge: " + sqlFile.toAbsolutePath() + " (" + sql.length() + " chars)");

            // 2. Connexion
            System.out.println("Connexion a MySQL...");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("Connecte a MySQL " + conn.getMetaData().getDatabaseProductVersion());

                // 3. Exécuter
                executeStatements(conn, sql);
            }

            System.out.println("\n============================================================");
            System.out.println("  BASE DE DONNEES REMPLIE AVEC SUCCES !");
            System.out.println("============================================================");
            System.out.println("\nComptes de test :");
            System.out.println("  Admin      : admin@smartfarm.tn / admin123");
            System.out.println("  Agriculteur: yassine.trabelsi@mail.tn / yassine123");
            System.out.println("  Agriculteur: fatma.bouzid@mail.tn / fatma123");
            System.out.println("  Stock      : sana.maatoug@mail.tn / sana123");
            System.out.println("============================================================\n");

        } catch (Exception e) {
            System.err.println("\nERREUR : " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n--> Alternative: Importez sql/seed_data.sql dans phpMyAdmin");
        }
    }

    private static Path findSqlFile() throws Exception {
        // Essayer plusieurs chemins relatifs
        String[] paths = {
            "sql/seed_data.sql",
            "../sql/seed_data.sql",
            "src/main/resources/sql/seed_data.sql",
        };
        for (String p : paths) {
            Path path = Paths.get(p);
            if (Files.exists(path)) return path;
        }
        // Essayer depuis le répertoire du projet
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        Path sqlPath = projectDir.resolve("sql/seed_data.sql");
        if (Files.exists(sqlPath)) return sqlPath;

        throw new Exception("Fichier sql/seed_data.sql introuvable ! Répertoire courant: " + projectDir);
    }

    private static void executeStatements(Connection conn, String sql) throws Exception {
        // Nettoyer les commentaires
        StringBuilder cleaned = new StringBuilder();
        for (String line : sql.split("\n")) {
            String t = line.trim();
            if (t.startsWith("--") || t.isEmpty()) continue;
            cleaned.append(line).append("\n");
        }

        String[] statements = cleaned.toString().split(";\\s*\n");
        int ok = 0, errors = 0;

        try (Statement stmt = conn.createStatement()) {
            for (String raw : statements) {
                String s = raw.trim();
                if (s.isEmpty() || s.equals(";")) continue;

                String display = s.replaceAll("\\s+", " ");
                if (display.length() > 90) display = display.substring(0, 90) + "...";

                try {
                    stmt.execute(s);
                    ok++;

                    if (s.toUpperCase().startsWith("SELECT")) {
                        var rs = stmt.getResultSet();
                        if (rs != null) {
                            while (rs.next()) {
                                try { System.out.println("   " + rs.getString(1) + " : " + rs.getString(2)); }
                                catch (Exception ignored) {}
                            }
                        }
                    } else {
                        System.out.println("  OK  " + display);
                    }
                } catch (Exception e) {
                    errors++;
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("already exists") || msg.contains("Duplicate"))) {
                        System.out.println("  SKIP " + display);
                    } else {
                        System.err.println("  FAIL " + display);
                        System.err.println("       " + (msg != null ? msg.substring(0, Math.min(80, msg.length())) : "?"));
                    }
                }
            }
        }

        System.out.println("\nResultat : " + ok + " OK, " + errors + " erreurs/ignores");
    }
}
