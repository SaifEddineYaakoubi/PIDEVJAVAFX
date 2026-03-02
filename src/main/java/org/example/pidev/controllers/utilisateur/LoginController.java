package org.example.pidev.controllers.utilisateur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.utils.FaceRecognitionUtil;
import org.example.pidev.utils.PasswordUtils;
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
    @FXML
    private Button loginButton;
    @FXML
    private Button faceLoginButton;
    @FXML
    private ProgressIndicator progressIndicator;

    private final UtilisateurService service = new UtilisateurService();

    @FXML
    private void initialize() {
        // Ensure face directory exists
        FaceRecognitionUtil.ensureFaceDirectory();

        // Hide progress indicator initially
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez entrer email et mot de passe", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Utilisateur> users = service.getAll();

            for (Utilisateur u : users) {
                if (u.getEmail().equals(email) && u.isStatut()
                        && PasswordUtils.verify(password, u.getMotDePasse())) {

                    // Auto-migration : si l'ancien mdp était en clair, le hacher maintenant
                    if (!PasswordUtils.isHashed(u.getMotDePasse())) {
                        u.setMotDePasse(PasswordUtils.hash(password));
                        service.update(u);
                        System.out.println("🔒 Mot de passe migré vers BCrypt pour: " + u.getEmail());
                    }

                    // Stocker l'utilisateur connecté
                    Session.setCurrentUser(u);
                    handleLoginSuccess(u);
                    return;
                }
            }
            showMessage("Email ou mot de passe incorrect ou compte inactif!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            showMessage("Erreur lors de la connexion: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleFaceLogin() {
        try {
            // Check if OpenCV is available
            if (!FaceRecognitionUtil.isOpenCvAvailable()) {
                showMessage("❌ OpenCV non disponible - Face ID désactivé", Alert.AlertType.ERROR);
                System.err.println("OpenCV Error: " + FaceRecognitionUtil.getOpenCvLoadError());
                return;
            }

            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                showMessage("Veuillez entrer votre email", Alert.AlertType.WARNING);
                return;
            }

            // Show progress
            setProgressVisible(true);
            disableButtons(true);

            // Run face login in background thread
            new Thread(() -> {
                try {
                    System.out.println("\n[FaceLogin] ======== STARTING FACE AUTHENTICATION ========");
                    System.out.println("[FaceLogin] Email: " + email);

                    // Get user by email
                    Utilisateur user = service.getByEmail(email);

                    if (user == null) {
                        System.err.println("[FaceLogin] User not found");
                        Platform.runLater(() -> {
                            showMessage("Utilisateur introuvable", Alert.AlertType.ERROR);
                            setProgressVisible(false);
                            disableButtons(false);
                        });
                        return;
                    }

                    System.out.println("[FaceLogin] User found: " + user.getNom() + " (ID: " + user.getIdUser() + ")");

                    String facePath = user.getFaceImagePath();
                    System.out.println("[FaceLogin] Saved face path from user object: " + facePath);


                    if (facePath == null || facePath.isEmpty()) {
                        System.err.println("[FaceLogin] No registered face found");
                        Platform.runLater(() -> {
                            showMessage("Aucun visage enregistré pour cet utilisateur", Alert.AlertType.WARNING);
                            setProgressVisible(false);
                            disableButtons(false);
                        });
                        return;
                    }

                    // Make final for lambda
                    final String savedFacePath = facePath;

                    // Capture new image
                    String tempPath = "faces/temp_" + System.currentTimeMillis() + ".jpg";
                    System.out.println("[FaceLogin] Capturing image to: " + tempPath);

                    boolean captured = FaceRecognitionUtil.captureImage(tempPath, 5000);

                    if (!captured) {
                        System.err.println("[FaceLogin] Image capture failed");
                        Platform.runLater(() -> {
                            showMessage("Erreur lors de la capture caméra", Alert.AlertType.ERROR);
                            setProgressVisible(false);
                            disableButtons(false);
                        });
                        return;
                    }

                    System.out.println("[FaceLogin] Image captured successfully");

                    // Validate captured image
                    if (!FaceRecognitionUtil.isFaceDetected(tempPath)) {
                        System.err.println("[FaceLogin] Captured image validation failed");
                        Platform.runLater(() -> {
                            showMessage("Image capturée invalide", Alert.AlertType.ERROR);
                            setProgressVisible(false);
                            disableButtons(false);
                        });
                        return;
                    }

                    System.out.println("[FaceLogin] Captured image is valid");
                    System.out.println("[FaceLogin] Comparing: " + savedFacePath + " <-> " + tempPath);

                    // Compare faces
                    double similarity = FaceRecognitionUtil.compareImages(savedFacePath, tempPath);
                    boolean matches = similarity >= 0.75; // Threshold de similarité

                    Platform.runLater(() -> {
                        if (matches) {
                            System.out.println("[FaceLogin] ✅ AUTHENTICATION SUCCESSFUL");
                            showMessage("✅ Connexion Face ID réussie!", Alert.AlertType.INFORMATION);
                            Session.setCurrentUser(user);
                            handleLoginSuccess(user);
                        } else {
                            System.err.println("[FaceLogin] ❌ AUTHENTICATION FAILED");
                            showMessage(String.format("❌ Visage non reconnu (similarité: %.1f%%)", similarity * 100), Alert.AlertType.ERROR);
                            setProgressVisible(false);
                            disableButtons(false);
                        }
                    });

                    System.out.println("[FaceLogin] ======== FACE AUTHENTICATION COMPLETED ========\n");

                } catch (Exception e) {
                    System.err.println("Erreur Face Login: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        showMessage("Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
                        setProgressVisible(false);
                        disableButtons(false);
                    });
                }
            }).start();

        } catch (Exception e) {
            System.err.println("Exception in handleFaceLogin: " + e.getMessage());
            e.printStackTrace();
            showMessage("Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
            setProgressVisible(false);
            disableButtons(false);
        }
    }

    private void handleLoginSuccess(Utilisateur u) {
        String fxmlPath;
        String windowTitle;

        // Récupérer le rôle de l'utilisateur
        String role = u.getRole().name();

        switch (role) {
            case "ADMIN":
                fxmlPath = "/Utilisateur.fxml";
                windowTitle = "Smart Farm - Gestion Utilisateurs (Admin)";
                break;

            case "RESPONSABLE_STOCK":
                fxmlPath = "/consulterproduit.fxml";
                windowTitle = "Smart Farm - Gestion des Stocks et Produits (Responsable Stock)";
                break;

            case "AGRICULTEUR":
                // Rediriger vers le Dashboard unifié (parcelles, cultures, récoltes, rendements)
                fxmlPath = "/Dashboard.fxml";
                windowTitle = "Smart Farm - Espace Agriculteur";
                break;

            default:
                // Par défaut, rediriger vers le Dashboard unifié
                fxmlPath = "/Dashboard.fxml";
                windowTitle = "Smart Farm - Dashboard";
                break;
        }

        try {
            System.out.println("🔀 Redirection vers: " + fxmlPath + " pour le rôle: " + role);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();

            Scene scene = new Scene(root);
            var cssResourceUrl = getClass().getResource("/styles/smartfarm.css");
            if (cssResourceUrl != null) {
                scene.getStylesheets().add(cssResourceUrl.toExternalForm());
            }
            // Aussi charger le CSS principal smartfarmm.css
            var css2 = getClass().getResource("/smartfarmm.css");
            if (css2 != null) {
                scene.getStylesheets().add(css2.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);

            // Maximiser la fenêtre pour tous les utilisateurs sauf ADMIN
            if (!role.equals("ADMIN")) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.setWidth(1200);
                stage.setHeight(800);
                stage.centerOnScreen();
            }

            stage.show();

            setProgressVisible(false);
            disableButtons(false);

            System.out.println("✅ Redirection réussie vers: " + windowTitle);

        } catch (IOException e) {
            System.err.println("IOException during interface loading: " + e.getMessage());
            e.printStackTrace();
            showMessage("Erreur lors de l'accès à l'interface: " + e.getMessage(), Alert.AlertType.ERROR);
            setProgressVisible(false);
            disableButtons(false);
        }
    }
    private void showMessage(String message, Alert.AlertType type) {
        messageLabel.setText(message);
        messageLabel.setStyle(type == Alert.AlertType.ERROR ? "-fx-text-fill: #e74c3c;" :
                            type == Alert.AlertType.WARNING ? "-fx-text-fill: #f39c12;" :
                            "-fx-text-fill: #27ae60;");
    }

    private void setProgressVisible(boolean visible) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(visible);
        }
    }

    private void disableButtons(boolean disabled) {
        if (loginButton != null) loginButton.setDisable(disabled);
        if (faceLoginButton != null) faceLoginButton.setDisable(disabled);
    }


    private void navigateToChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatbot_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);

            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Farm - Assistant Agricole");
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement du chatbot", Alert.AlertType.ERROR);
        }
    }
}