package org.example.pidev.controllers.utilisateur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.pidev.models.Role;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.services.utilisateur.FileStorageService;
import org.example.pidev.utils.FaceRecognitionUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserFormController {

    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label formTitle;

    // Nouveaux composants pour la photo
    @FXML private ImageView profileImageView;
    @FXML private Button uploadPhotoButton;
    @FXML private Button removePhotoButton;

    private final UtilisateurService service = new UtilisateurService();
    private final FileStorageService fileStorageService = new FileStorageService();
    private Utilisateur currentUser;
    private boolean isUpdate = false;
    private File selectedImageFile;
    private boolean imageChanged = false;

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

        // Charger la photo existante
        loadProfileImage(user.getFaceImagePath());
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

        // Configurer les boutons photo
        if (uploadPhotoButton != null) {
            uploadPhotoButton.setOnAction(event -> uploadPhoto());
        }

        if (removePhotoButton != null) {
            removePhotoButton.setOnAction(event -> removePhoto());
        }

        // Image par défaut
        setDefaultImage();
    }

    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadPhotoButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Vérifier la taille (max 5MB)
                if (selectedFile.length() > 5 * 1024 * 1024) {
                    showAlert(Alert.AlertType.WARNING, "Erreur", "Fichier trop volumineux",
                            "L'image ne doit pas dépasser 5MB");
                    return;
                }

                // Vérifier le type via FileStorageService (optionnel)
                String fileName = selectedFile.getName().toLowerCase();
                if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") &&
                        !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
                    showAlert(Alert.AlertType.WARNING, "Erreur", "Format non supporté",
                            "Utilisez JPG, PNG ou GIF");
                    return;
                }

                // Afficher l'aperçu
                Image image = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(image);

                // Sauvegarder le fichier sélectionné
                selectedImageFile = selectedFile;
                imageChanged = true;

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image",
                        e.getMessage());
            }
        }
    }

    private void removePhoto() {
        // Supprimer la photo actuelle
        profileImageView.setImage(null);
        setDefaultImage();
        selectedImageFile = null;
        imageChanged = true;
    }

    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File photoFile = fileStorageService.getProfileImage(imagePath);
                if (photoFile.exists()) {
                    Image image = new Image(photoFile.toURI().toString());
                    profileImageView.setImage(image);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setDefaultImage();
    }

    private void setDefaultImage() {
        try {
            // Essayer de charger l'image par défaut depuis les ressources
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            profileImageView.setImage(defaultImage);
        } catch (Exception e) {
            // Si pas d'image par défaut, laisser vide
            profileImageView.setImage(null);
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation before saving
            if (!validateInput()) return;

            if (isUpdate) {
                // Mode modification
                currentUser.setNom(nomField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setRole(Role.valueOf(roleCombo.getValue()));
                if(!passwordField.getText().isEmpty()) currentUser.setMotDePasse(passwordField.getText());

                // Gérer la photo si elle a changé
                handlePhotoChange();

                service.update(currentUser);

                // Rafraîchir la liste dans le parent
                if (parentController != null) {
                    Platform.runLater(() -> {
                        System.out.println("[UserForm] Appel du rafraîchissement du parent après update...");
                        try {
                            parentController.loadUsers();
                            System.out.println("[UserForm] Rafraîchissement terminé.");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                closeWindow();

            } else {
                // Mode création
                Utilisateur newUser = new Utilisateur(
                        nomField.getText().trim(),
                        "", // Prénom vide par défaut
                        emailField.getText().trim(),
                        passwordField.getText(),
                        Role.valueOf(roleCombo.getValue()),
                        true,
                        LocalDate.now()
                );

                boolean added = service.add(newUser);
                if (added) {
                    // Récupérer l'utilisateur avec son ID (par email)
                    Utilisateur savedUser = service.getByEmail(newUser.getEmail());

                    // Si une photo a été sélectionnée, la sauvegarder
                    if (savedUser != null && imageChanged && selectedImageFile != null) {
                        try {
                            String fileName = fileStorageService.saveProfileImage(selectedImageFile);
                            savedUser.setFaceImagePath(fileName);
                            service.update(savedUser);
                        } catch (IOException e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur",
                                    "Erreur lors de la sauvegarde de la photo", e.getMessage());
                        }
                    }

                    if (parentController != null) {
                        Platform.runLater(() -> {
                            System.out.println("[UserForm] Appel du rafraîchissement du parent après add...");
                            try {
                                parentController.loadUsers();
                                System.out.println("[UserForm] Rafraîchissement terminé.");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Enregistrement échoué",
                            "Impossible d'ajouter l'utilisateur. Vérifiez la console pour plus de détails.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue", e.getMessage());
        }
    }

    private void handlePhotoChange() throws IOException {
        if (imageChanged && currentUser != null) {
            // Supprimer l'ancienne photo si elle existe
            if (currentUser.getFaceImagePath() != null) {
                fileStorageService.deleteProfileImage(currentUser.getFaceImagePath());
            }

            // Sauvegarder la nouvelle photo si un fichier est sélectionné
            if (selectedImageFile != null) {
                String fileName = fileStorageService.saveProfileImage(selectedImageFile);
                currentUser.setFaceImagePath(fileName);
            } else {
                // Si imageChanged est true mais pas de fichier, c'est qu'on a supprimé la photo
                currentUser.setFaceImagePath(null);
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

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
    public void setParentController(UtilisateurController parent) {
        this.parentController = parent;
    }

    @FXML
    public void handleFaceCapture() {
        try {
            if (currentUser == null && !isUpdate) {
                showAlert(Alert.AlertType.WARNING,
                        "Attention",
                        "Utilisateur non encore enregistré",
                        "Veuillez d'abord enregistrer l'utilisateur avant d'ajouter le visage.");
                return;
            }

            // Si modification → currentUser existe
            Utilisateur user = isUpdate ? currentUser : null;

            if (user == null) {
                showAlert(Alert.AlertType.WARNING,
                        "Erreur",
                        "Utilisateur introuvable",
                        "Impossible d'associer le visage.");
                return;
            }

            String path = "faces/user_" + user.getIdUser() + ".jpg";

            // Show confirmation before capturing
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Capture du Visage");
            alert.setHeaderText("Préparation à la capture");
            alert.setContentText("Veuillez positionner votre visage devant la caméra. La capture débutera dans 3 secondes.");
            alert.showAndWait();

            // Capture with 5 second timeout
            System.out.println("[FaceCapture] Capturing face image for user: " + user.getIdUser());
            boolean captured = FaceRecognitionUtil.captureImage(path, 5000);

            if (captured) {
                // Validate captured image
                if (FaceRecognitionUtil.isFaceDetected(path)) {
                    user.setFaceImagePath(path);
                    service.update(user);

                    // Mettre à jour l'affichage de la photo
                    loadProfileImage(path);

                    showAlert(Alert.AlertType.INFORMATION,
                            "Succès",
                            "Visage enregistré",
                            "Le visage a été enregistré avec succès pour cet utilisateur.");
                    System.out.println("✅ Face image saved successfully: " + path);
                } else {
                    showAlert(Alert.AlertType.WARNING,
                            "Avertissement",
                            "Image invalide",
                            "L'image capturée ne semble pas valide. Veuillez réessayer.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Erreur",
                        "Erreur de capture",
                        "Impossible de capturer l'image. Vérifiez que votre caméra fonctionne correctement.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error during face capture: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Exception lors de la capture",
                    e.getMessage());
        }
    }
}