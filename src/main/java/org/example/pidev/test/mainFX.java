package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class mainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger le fichier FXML - Page principale : Liste des Parcelles
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/consulterparcelle.fxml"));
        Parent root = loader.load();

        // Obtenir les dimensions de l'écran
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Configurer la scène avec les dimensions de l'écran
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        // Charger le CSS
        String css = getClass().getResource("/styles/smartfarm.css").toExternalForm();
        if (css != null) {
            scene.getStylesheets().add(css);
        }

        // Configurer le stage
        stage.setTitle("Smart Farm - Liste des Parcelles");
        stage.setScene(scene);
        stage.setResizable(true);

        // Positionner la fenêtre en haut à gauche
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());

        // Maximiser la fenêtre
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
