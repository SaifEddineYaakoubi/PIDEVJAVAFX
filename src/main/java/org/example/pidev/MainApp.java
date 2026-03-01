package org.example.pidev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.pidev.utils.DatabaseConnection;

public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // Vérification de la connexion à la base de données
        if (!checkDatabaseConnection()) {
            showDatabaseError();
            return;
        }

        showDashboard();
    }

    /**
     * Vérifie la connexion à la base de données
     * @return true si connexion OK, false sinon
     */
    private boolean checkDatabaseConnection() {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            if (db.getConnection() == null) {
                return false;
            }
            // Test une simple requête pour vérifier la connexion
            db.getConnection().createStatement().executeQuery("SELECT 1").close();
            return true;
        } catch (Exception e) {
            System.err.println("Erreur de connexion base de données: " + e.getMessage());
            return false;
        }
    }

    /**
     * Affiche une alerte d'erreur de base de données
     */
    private void showDatabaseError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ Erreur de Connexion Base de Données");
        alert.setHeaderText("Impossible de se connecter à la base de données");
        alert.setContentText(
            "Veuillez vérifier:\n" +
            "1. Que le serveur MySQL est démarré\n" +
            "2. Les paramètres de connexion dans DatabaseConnection.java\n" +
            "3. Que la base de données 'pidev' existe\n\n" +
            "L'application va se fermer."
        );
        alert.showAndWait();
        System.exit(1);
    }

    /**
     * Charge le Dashboard principal avec navigation dynamique
     */
    private void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pidev/Dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1600, 900);

            primaryStage.setTitle("🌱 SMART FARM - Dashboard Professionnel");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1400);
            primaryStage.setMinHeight(700);
            primaryStage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Chargement");
            alert.setHeaderText("Impossible de charger le dashboard");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

