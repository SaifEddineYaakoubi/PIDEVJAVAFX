package org.example.pidev.controllers.utilisateur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Role;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserFormController {

    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label formTitle;

    private final UtilisateurService service = new UtilisateurService();
    private Utilisateur currentUser;
    private boolean isUpdate = false;

    // Référence au contrôleur parent pour rafraîchir la liste après save
    private UtilisateurController parentController;

    // Email validation pattern (simple but practical)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Fill fields if we are editing
    public void setUserData(Utilisateur user) {
        this.currentUser = user;
        this.isUpdate = true;
        formTitle.setText("Modifier l'utilisateur");
        nomField.setText(user.getNom());
        emailField.setText(user.getEmail());
        roleCombo.setValue(user.getRole().name());
        // Password stays empty for security or fills hidden
    }

    @FXML
    private void initialize() {
        // Ensure combo has a prompt and items (FXML already sets items but keep safe)
        if (roleCombo != null) {
            if (roleCombo.getItems().isEmpty()) {
                roleCombo.getItems().addAll("ADMIN", "AGRICULTEUR", "RESPONSABLE_STOCK");
            }
            roleCombo.setPromptText("Choisir un rôle");
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation before saving
            if (!validateInput()) return;

            if (isUpdate) {
                currentUser.setNom(nomField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setRole(Role.valueOf(roleCombo.getValue()));
                if(!passwordField.getText().isEmpty()) currentUser.setMotDePasse(passwordField.getText());
                service.update(currentUser);

                // Rafraîchir la liste dans le parent si présent (sûr thread UI)
                if (parentController != null) {
                    Platform.runLater(() -> {
                        System.out.println("[UserForm] Appel du rafraîchissement du parent après update...");
                        try { parentController.loadUsers(); System.out.println("[UserForm] Rafraîchissement terminé."); } catch (Exception ex) { ex.printStackTrace(); }
                    });
                }
                closeWindow();
            } else {
                Utilisateur newUser = new Utilisateur(
                        nomField.getText().trim(), "", emailField.getText().trim(),
                        passwordField.getText(), Role.valueOf(roleCombo.getValue()),
                        true, LocalDate.now()
                );
                boolean added = service.add(newUser);
                if (added) {
                    if (parentController != null) {
                        Platform.runLater(() -> {
                            System.out.println("[UserForm] Appel du rafraîchissement du parent après add...");
                            try { parentController.loadUsers(); System.out.println("[UserForm] Rafraîchissement terminé."); } catch (Exception ex) { ex.printStackTrace(); }
                        });
                    }
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Enregistrement échoué", "Impossible d'ajouter l'utilisateur. Vérifiez la console pour plus de détails.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue", e.getMessage());
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    // Validation helper methods
    private boolean validateInput() {
        String name = nomField.getText() == null ? "" : nomField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String role = roleCombo.getValue();

        StringBuilder errors = new StringBuilder();
        boolean valid = true;

        // Name
        if (name.isEmpty()) {
            errors.append("- Le nom est requis.\n");
            markInvalid(nomField);
            valid = false;
        } else {
            markValid(nomField);
        }

        // Email
        boolean emailOk = true;
        if (email.isEmpty()) {
            errors.append("- L'email est requis.\n");
            markInvalid(emailField);
            emailOk = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("- L'email n'est pas valide.\n");
            markInvalid(emailField);
            emailOk = false;
        } else {
            // Check for duplicates in DB
            try {
                Utilisateur found = service.getByEmail(email);
                if (!isUpdate) {
                    if (found != null) {
                        errors.append("- Un utilisateur avec cet email existe déjà.\n");
                        markInvalid(emailField);
                        emailOk = false;
                    }
                } else {
                    // If updating, allow same email if it belongs to the current user
                    if (found != null && currentUser != null && found.getIdUser() != currentUser.getIdUser()) {
                        errors.append("- Cet email est utilisé par un autre utilisateur.\n");
                        markInvalid(emailField);
                        emailOk = false;
                    }
                }
            } catch (Exception e) {
                // If DB check fails, log and continue with a generic message
                System.err.println("Erreur lors de la vérification de l'email: " + e.getMessage());
            }
        }
        if (emailOk) markValid(emailField); else valid = false;

        // Password
        boolean pwOk = true;
        if (!isUpdate) {
            // On création, mot de passe requis et min 6 caractères
            if (password.length() < 6) {
                errors.append("- Le mot de passe doit contenir au moins 6 caractères.\n");
                markInvalid(passwordField);
                pwOk = false;
            }
        } else {
            // En modification, mot de passe facultatif mais si fourni, on vérifie la longueur
            if (!password.isEmpty() && password.length() < 6) {
                errors.append("- Le mot de passe doit contenir au moins 6 caractères.\n");
                markInvalid(passwordField);
                pwOk = false;
            }
        }
        if (pwOk) markValid(passwordField); else valid = false;

        // Role
        if (role == null || role.isEmpty()) {
            errors.append("- Le rôle est requis.\n");
            markInvalid(roleCombo);
            valid = false;
        } else {
            markValid(roleCombo);
        }

        if (!valid) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez corriger les erreurs suivantes:", errors.toString());
            return false;
        }
        return true;
    }

    private void markInvalid(Control c) {
        if (c != null) c.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
    }

    private void markValid(Control c) {
        if (c != null) c.setStyle("");
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(nomField.getScene() != null ? nomField.getScene().getWindow() : null);
        alert.showAndWait();
    }

    // Setter du parent pour rafraîchir la table
    public void setParentController(UtilisateurController parent) { this.parentController = parent; }
}