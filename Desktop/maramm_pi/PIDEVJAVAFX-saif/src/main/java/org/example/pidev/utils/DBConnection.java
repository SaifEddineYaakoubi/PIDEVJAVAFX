package org.example.pidev.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final String URL = "jdbc:mysql://localhost:3306/smartfarm";
    private final String USER = "root";
    private final String PASSWORD = "";

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
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // retourner la connexion
    public static Connection getConnection() {
        if (instance == null) {
            getInstance();
        }
        return connection;
    }
}
