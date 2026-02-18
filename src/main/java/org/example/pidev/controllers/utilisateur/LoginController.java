package org.example.pidev.controllers.utilisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.utils.Session;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private final UtilisateurService service = new UtilisateurService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        List<Utilisateur> users = service.getAll();

        for (Utilisateur u : users) {
            if (u.getEmail().equals(email) && u.getMotDePasse().equals(password) && u.isStatut()) {

                // Stocker l'utilisateur connecté dans la session
                Session.setCurrentUser(u);

                // 🔹 Déterminer quelle interface charger selon le rôle
                String fxmlPath;
                String windowTitle;

                if ("ADMIN".equalsIgnoreCase(u.getRole().name())) {
                    fxmlPath = "/Utilisateur.fxml";
                    windowTitle = "Smart Farm - Gestion Utilisateurs (Admin)";
                } else {
                    fxmlPath = "/consulterparcelle.fxml";
                    windowTitle = "Smart Farm - Consulter Parcelles";
                }

                try {
                    // 1. Charger le FXML sélectionné
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();

                    // 2. Récupérer le Stage actuel
                    Stage stage = (Stage) emailField.getScene().getWindow();

                    // 3. Configurer la scène avec ton CSS global
                    Scene scene = new Scene(root);
                    var cssResourceUrl = getClass().getResource("/styles/smartfarm.css");
                    if (cssResourceUrl != null) {
                        scene.getStylesheets().add(cssResourceUrl.toExternalForm());
                    }

                    // 4. Mise à jour du Stage
                    stage.setScene(scene);
                    stage.setTitle(windowTitle);
                    stage.centerOnScreen();
                    stage.show();

                } catch (IOException e) {
                    messageLabel.setText("Erreur lors de l'accès à l'interface : " + fxmlPath);
                    System.err.println("IOException during interface loading: " + e.getMessage());
                }
                return;
            }
        }
        messageLabel.setText("Email ou mot de passe incorrect ou compte inactif !");
    }
}