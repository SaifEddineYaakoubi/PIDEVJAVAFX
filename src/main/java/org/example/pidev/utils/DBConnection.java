package org.example.pidev.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("InstantiationOfUtilityClass")
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/smartfarm";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;
    private static DBConnection instance;

    // constructeur privé (Singleton)
    private DBConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database successfully");
        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
        }
    }

    // retourner l'instance unique
    @SuppressWarnings("unused")
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // retourner la connexion - reconnecte automatiquement si fermée
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Reconnexion à la base de données...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Reconnecté à la base de données");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reconnecting: " + e.getMessage());
            // Last attempt
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException ex) {
                System.out.println("❌ Critical: Cannot connect to database: " + ex.getMessage());
            }
        }
        return connection;
    }

    // Close connection gracefully (optional)
    @SuppressWarnings("unused")
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                instance = null;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
