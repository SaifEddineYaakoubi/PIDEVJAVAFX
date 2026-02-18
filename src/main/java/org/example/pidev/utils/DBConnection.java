package org.example.pidev.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("InstantiationOfUtilityClass")
public class DBConnection {

    private static Connection connection;
    private static DBConnection instance;

    // constructeur privé (Singleton)
    private DBConnection() {
        final String URL = "jdbc:mysql://localhost:3307/smartfarm";
        final String USER = "root";
        final String PASSWORD = "";

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

    // retourner la connexion
    public static Connection getConnection() {
        if (instance == null) {
            instance = new DBConnection();
        }
        // Check if connection is still valid, reconnect if needed
        try {
            if (connection == null || connection.isClosed()) {
                instance = null; // Reset singleton
                getConnection(); // Recursive call to reconnect
            }
        } catch (SQLException e) {
            System.out.println("❌ Error checking connection: " + e.getMessage());
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
