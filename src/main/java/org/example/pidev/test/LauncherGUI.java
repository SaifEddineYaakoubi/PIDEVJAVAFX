package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LauncherGUI extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Vérifier que la ressource FXML existe
            URL fxmlUrl = getClass().getResource("/Dashboard.fxml");
            if (fxmlUrl == null) {
                System.err.println("❌ Ressource Dashboard.fxml introuvable dans le classpath. Vérifiez src/main/resources/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setTitle("Smart Farm - Dashboard");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.show();

            System.out.println("✅ Application démarrée : Dashboard affiché");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement du FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
