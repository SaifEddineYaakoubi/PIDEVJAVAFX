package org.example.pidev.controllers.utilisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.Session;

import java.io.IOException;

public class ProfileController {

    @FXML private Label lblName;
    @FXML private Label lblEmail;
    @FXML private Label lblRole;
    @FXML private Label lblActive;

    private Utilisateur user;

    public void setUser(Utilisateur user) {
        this.user = user;
        populate();
    }

    private void populate() {
        if (user == null) return;
        lblName.setText((user.getNom() != null ? user.getNom() : "") + (user.getPrenom() != null && !user.getPrenom().isEmpty() ? " " + user.getPrenom() : ""));
        lblEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        lblRole.setText(user.getRole() != null ? user.getRole().name() : "");
        lblActive.setText(user.isStatut() ? "Oui" : "Non");
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear session
            Session.clear();

            // Charger la vue de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/smartfarm.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            // Obtenir le Stage du modal et son owner (fenêtre principale)
            Stage modalStage = (Stage) lblName.getScene().getWindow();
            Window ownerWindow = modalStage.getOwner();

            if (ownerWindow instanceof Stage) {
                Stage ownerStage = (Stage) ownerWindow;
                ownerStage.setScene(scene);
                ownerStage.centerOnScreen();
            } else {
                // Fallback : remplacer la scène du modal si pas d'owner
                modalStage.setScene(scene);
                modalStage.centerOnScreen();
            }

            // Fermer le modal si il est toujours ouvert
            if (modalStage.isShowing()) modalStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblName.getScene().getWindow();
        stage.close();
    }
}
