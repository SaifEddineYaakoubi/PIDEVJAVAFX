package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger le fichier FXML - Page principale : Liste des Parcelles
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/consulterparcelle.fxml"));
        Parent root = loader.load();

        // Configurer la scène
        Scene scene = new Scene(root);

        // Configurer le stage
        stage.setTitle("Smart Farm - Liste des Parcelles");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
