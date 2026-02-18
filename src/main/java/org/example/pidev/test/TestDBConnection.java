package org.example.pidev.test;

import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TestDBConnection {

    public static void main(String[] args) {
        System.out.println("\n=== TEST DATABASE CONNECTION ===\n");

        // Test 1: Connection
        System.out.println("TEST 1: Testing database connection...");
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Connection successful!");
            try {
                System.out.println("   - Connection closed? " + conn.isClosed());
                System.out.println("   - Connection valid? " + conn.isValid(2));
            } catch (Exception e) {
                System.out.println("❌ Error checking connection: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Connection failed!");
            return;
        }

        // Test 2: Count users
        System.out.println("\nTEST 2: Checking utilisateur table...");
        try {
            String sql = "SELECT COUNT(*) as cnt FROM utilisateur";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("cnt");
                System.out.println("✅ Total users in database: " + count);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        // Test 3: Service
        System.out.println("\nTEST 3: Testing UtilisateurService.getAll()...");
        UtilisateurService service = new UtilisateurService();
        List<Utilisateur> users = service.getAll();
        System.out.println("✅ Service returned " + users.size() + " users");

        for (Utilisateur u : users) {
            System.out.println("   - " + u.getNom() + " | " + u.getEmail() + " | Role: " + u.getRole());
        }

        System.out.println("\n=== TEST COMPLETE ===\n");
    }
}

