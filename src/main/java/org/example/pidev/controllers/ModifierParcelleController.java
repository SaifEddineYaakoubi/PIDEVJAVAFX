package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifierParcelleController implements Initializable {

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
    private Button btnModifier;

    @FXML
    private Button btnAnnuler;

    private ParcelleService parcelleService;
    private Parcelle currentParcelle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parcelleService = new ParcelleService();

        // Remplir le ComboBox avec les états possibles
        cbEtat.setItems(FXCollections.observableArrayList("active", "repos", "exploitée"));

        // Effacer les messages d'erreur quand l'utilisateur tape
        tfNom.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfSuperficie.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfLocalisation.textProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtat.valueProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    /**
     * Méthode appelée pour pré-remplir le formulaire avec les données de la parcelle
     */
    public void setParcelle(Parcelle parcelle) {
        this.currentParcelle = parcelle;
        tfNom.setText(parcelle.getNom());
        tfSuperficie.setText(String.valueOf(parcelle.getSuperficie()));
        tfLocalisation.setText(parcelle.getLocalisation());
        cbEtat.setValue(parcelle.getEtat());
    }

    @FXML
    void modifierParcelle(ActionEvent event) {
        clearMessages();

        try {
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

            // Mettre à jour l'objet Parcelle
            currentParcelle.setNom(nom.trim());
            currentParcelle.setSuperficie(superficie);
            currentParcelle.setLocalisation(localisation.trim());
            currentParcelle.setEtat(etat);
            currentParcelle.setIdUser(CURRENT_USER_ID);

            // Modifier via le service
            parcelleService.update(currentParcelle);

            showSuccess("✅ Parcelle modifiée avec succès !");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    void retourListe(ActionEvent event) {
        navigateToConsulterParcelle(event);
    }

    // ==================== NAVIGATION ====================

    @FXML
    void navigateToConsulterParcelle(ActionEvent event) {
        navigateTo("/consulterparcelle.fxml", "Liste des Parcelles");
    }

    @FXML
    void navigateToAjouterParcelle(ActionEvent event) {
        navigateTo("/ajouterparcelle.fxml", "Ajouter une Parcelle");
    }

    @FXML
    void navigateToConsulterCulture(ActionEvent event) {
        navigateTo("/consulterculture.fxml", "Liste des Cultures");
    }

    @FXML
    void navigateToAjouterCulture(ActionEvent event) {
        navigateTo("/ajouterculture.fxml", "Ajouter une Culture");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - " + title);
        } catch (IOException e) {
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    // ==================== HELPERS ====================

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
