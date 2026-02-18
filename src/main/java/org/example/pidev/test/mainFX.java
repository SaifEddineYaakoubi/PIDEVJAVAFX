package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚀 Smart Farm Application Starting...");
        System.out.println("=".repeat(50) + "\n");

        // 🔹 Charger la page LOGIN au démarrage
        System.out.println("📂 Loading LoginView.fxml...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent root = loader.load();
        System.out.println("✅ LoginView.fxml loaded successfully");

        Scene scene = new Scene(root, 600, 500);

        // 🔹 Charger ton CSS global ici (recommandé)
        System.out.println("🎨 Loading CSS...");
        var cssResourceUrl = getClass().getResource("/styles/smartfarm.css");
        if (cssResourceUrl != null) {
            scene.getStylesheets().add(cssResourceUrl.toExternalForm());
            System.out.println("✅ CSS loaded successfully");
        } else {
            System.out.println("⚠️ CSS not found at /styles/smartfarm.css");
        }

        stage.setTitle("Smart Farm - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();

        System.out.println("✅ Login window displayed");
        System.out.println("\n" + "=".repeat(50) + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
