package org.example.pidev.controllers.utilisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.FileStorageService;
import org.example.pidev.utils.Session;

import java.io.File;
import java.io.IOException;

public class ProfileController {

    @FXML private Label lblName;
    @FXML private Label lblEmail;
    @FXML private Label lblRole;
    @FXML private Label lblActive;
    @FXML private ImageView profileImageView; // Ajout du ImageView pour la photo

    private Utilisateur user;
    private FileStorageService fileStorageService;

    @FXML
    public void initialize() {
        // Initialiser le service de stockage
        fileStorageService = new FileStorageService();

        // Arrondir l'image (optionnel - pour un effet avatar)
        makeImageCircular();

        // Auto-charger l'utilisateur depuis la session si disponible
        if (user == null && Session.getCurrentUser() != null) {
            setUser(Session.getCurrentUser());
        }
    }

    public void setUser(Utilisateur user) {
        this.user = user;
        populate();
        loadProfileImage();
    }

    private void populate() {
        if (user == null) return;
        lblName.setText((user.getNom() != null ? user.getNom() : "") +
                (user.getPrenom() != null && !user.getPrenom().isEmpty() ? " " + user.getPrenom() : ""));
        lblEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        lblRole.setText(user.getRole() != null ? user.getRole().name() : "");
        lblActive.setText(user.isStatut() ? "Oui" : "Non");
    }

    /**
     * Charge la photo de profil de l'utilisateur
     */
    private void loadProfileImage() {
        if (user != null && user.getFaceImagePath() != null && !user.getFaceImagePath().isEmpty()) {
            try {
                // Récupérer le fichier de la photo
                File photoFile = fileStorageService.getProfileImage(user.getFaceImagePath());

                if (photoFile.exists()) {
                    // Charger l'image
                    Image image = new Image(photoFile.toURI().toString());
                    profileImageView.setImage(image);
                    System.out.println("✅ Photo chargée avec succès: " + user.getFaceImagePath());
                } else {
                    System.out.println("⚠️ Fichier photo non trouvé: " + user.getFaceImagePath());
                    setDefaultImage();
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement de la photo: " + e.getMessage());
                setDefaultImage();
            }
        } else {
            System.out.println("ℹ️ Aucune photo associée à l'utilisateur");
            setDefaultImage();
        }
    }

    /**
     * Définit l'image par défaut
     */
    private void setDefaultImage() {
        try {
            // Essayer de charger l'image par défaut depuis les ressources
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                profileImageView.setImage(defaultImage);
            } else {
                // Si l'image par défaut n'existe pas, créer une image vide ou utiliser un placeholder
                profileImageView.setImage(null);
            }
        } catch (Exception e) {
            // En cas d'erreur, laisser l'ImageView vide
            profileImageView.setImage(null);
            System.err.println("⚠️ Impossible de charger l'image par défaut");
        }
    }

    /**
     * Optionnel : Rend l'image circulaire (effet avatar)
     */
    private void makeImageCircular() {
        if (profileImageView != null) {
            // Cette méthode sera appelée après que l'image soit chargée
            // Vous pouvez aussi le faire directement dans le FXML avec CSS
            profileImageView.setStyle(
                    "-fx-background-radius: 75;" +
                            "-fx-border-radius: 75;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-color: white;"
            );
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear session
            Session.clear();

            // Charger la vue de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 500);

            // Charger les CSS
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            // Obtenir le Stage du modal et son owner (fenêtre principale)
            Stage modalStage = (Stage) lblName.getScene().getWindow();
            Window ownerWindow = modalStage.getOwner();

            if (ownerWindow instanceof Stage) {
                Stage ownerStage = (Stage) ownerWindow;
                ownerStage.setMaximized(false);
                ownerStage.setScene(scene);
                ownerStage.setTitle("Smart Farm - Connexion");
                ownerStage.setResizable(false);
                ownerStage.setWidth(600);
                ownerStage.setHeight(500);
                ownerStage.centerOnScreen();
                ownerStage.setOnCloseRequest(evt -> {
                    javafx.application.Platform.exit();
                    System.exit(0);
                });
            } else {
                modalStage.setScene(scene);
                modalStage.centerOnScreen();
                modalStage.setOnCloseRequest(evt -> {
                    javafx.application.Platform.exit();
                    System.exit(0);
                });
            }

            // Fermer le modal
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