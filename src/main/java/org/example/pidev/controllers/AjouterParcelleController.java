package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterParcelleController implements Initializable {

    // ID de l'utilisateur connecté (par défaut = 1)
    private static final int CURRENT_USER_ID = 1;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfSuperficie;

    @FXML
    private TextField tfLocalisation;

    @FXML
    private ComboBox<String> cbEtat;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    private ParcelleService parcelleService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le service
        parcelleService = new ParcelleService();

        // Remplir le ComboBox avec les états possibles
        cbEtat.setItems(FXCollections.observableArrayList("active", "repos", "exploitée"));

        // Effacer les messages d'erreur quand l'utilisateur tape
        tfNom.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfSuperficie.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfLocalisation.textProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtat.valueProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    @FXML
    void ajouterParcelle(ActionEvent event) {
        clearMessages();

        try {
            // Récupérer les valeurs
            String nom = tfNom.getText();
            String superficieStr = tfSuperficie.getText();
            String localisation = tfLocalisation.getText();
            String etat = cbEtat.getValue();

            // Validation des champs vides
            if (nom == null || nom.trim().isEmpty()) {
                showError("Le nom de la parcelle est obligatoire.");
                return;
            }

            if (superficieStr == null || superficieStr.trim().isEmpty()) {
                showError("La superficie est obligatoire.");
                return;
            }

            if (localisation == null || localisation.trim().isEmpty()) {
                showError("La localisation est obligatoire.");
                return;
            }

            if (etat == null || etat.trim().isEmpty()) {
                showError("Veuillez sélectionner un état.");
                return;
            }

            // Conversion et validation de la superficie
            double superficie;
            try {
                superficie = Double.parseDouble(superficieStr.trim());
            } catch (NumberFormatException e) {
                showError("La superficie doit être un nombre valide.");
                return;
            }

            // Créer l'objet Parcelle avec l'ID de l'utilisateur connecté
            Parcelle parcelle = new Parcelle(nom.trim(), superficie, localisation.trim(), etat, CURRENT_USER_ID);

            // Ajouter via le service (qui fait aussi la validation)
            parcelleService.add(parcelle);

            // Succès
            showSuccess("✅ Parcelle ajoutée avec succès !");

            // Fermer la fenêtre après un court délai
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(e -> {
                Stage stage = (Stage) tfNom.getScene().getWindow();
                stage.close();
            });
            pause.play();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }

    @FXML
    void resetForm(ActionEvent event) {
        clearFields();
        clearMessages();
        tfNom.requestFocus();
    }


    // ==================== HELPERS ====================

    private void clearFields() {
        tfNom.clear();
        tfSuperficie.clear();
        tfLocalisation.clear();
        cbEtat.setValue(null);
    }

    private void clearMessages() {
        lblError.setText("");
        lblSuccess.setText("");
    }

    private void showError(String message) {
        lblError.setText(message);
        lblSuccess.setText("");
    }

    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblError.setText("");
    }
}
