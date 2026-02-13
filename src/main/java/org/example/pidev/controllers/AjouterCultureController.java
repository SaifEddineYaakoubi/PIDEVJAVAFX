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
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterCultureController implements Initializable {

    @FXML
    private TextField tfTypeCulture;

    @FXML
    private DatePicker dpDatePlantation;

    @FXML
    private DatePicker dpDateRecoltePrevue;

    @FXML
    private ComboBox<String> cbEtatCroissance;

    @FXML
    private ComboBox<String> cbParcelle;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private List<Parcelle> parcelles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();

        // Remplir le ComboBox des états de croissance
        cbEtatCroissance.setItems(FXCollections.observableArrayList(
                "germination", "croissance", "floraison", "mature", "récolté"
        ));

        // Charger les parcelles disponibles
        loadParcelles();

        // Effacer les messages quand l'utilisateur interagit
        tfTypeCulture.textProperty().addListener((obs, old, newVal) -> clearMessages());
        dpDatePlantation.valueProperty().addListener((obs, old, newVal) -> clearMessages());
        dpDateRecoltePrevue.valueProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtatCroissance.valueProperty().addListener((obs, old, newVal) -> clearMessages());
        cbParcelle.valueProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    private void loadParcelles() {
        parcelles = parcelleService.getAll();
        cbParcelle.getItems().clear();
        for (Parcelle p : parcelles) {
            cbParcelle.getItems().add(p.getIdParcelle() + " - " + p.getNom());
        }
    }

    @FXML
    void ajouterCulture(ActionEvent event) {
        clearMessages();

        try {
            String typeCulture = tfTypeCulture.getText();
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecoltePrevue = dpDateRecoltePrevue.getValue();
            String etatCroissance = cbEtatCroissance.getValue();
            String parcelleSelection = cbParcelle.getValue();

            // Validation des champs
            if (typeCulture == null || typeCulture.trim().isEmpty()) {
                showError("Le type de culture est obligatoire.");
                return;
            }

            if (datePlantation == null) {
                showError("La date de plantation est obligatoire.");
                return;
            }

            if (dateRecoltePrevue == null) {
                showError("La date de récolte prévue est obligatoire.");
                return;
            }

            if (etatCroissance == null || etatCroissance.trim().isEmpty()) {
                showError("Veuillez sélectionner un état de croissance.");
                return;
            }

            if (parcelleSelection == null || parcelleSelection.trim().isEmpty()) {
                showError("Veuillez sélectionner une parcelle.");
                return;
            }

            // Extraire l'ID de la parcelle
            int idParcelle = Integer.parseInt(parcelleSelection.split(" - ")[0]);

            // Créer l'objet Culture
            Culture culture = new Culture(typeCulture.trim(), datePlantation, dateRecoltePrevue, etatCroissance, idParcelle);

            // Ajouter via le service
            boolean success = cultureService.add(culture);

            if (success) {
                showSuccess("✅ Culture ajoutée avec succès !");
                clearFields();
            } else {
                showError("❌ Erreur lors de l'ajout de la culture.");
            }

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        clearFields();
        clearMessages();
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

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tfTypeCulture.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - " + title);
        } catch (IOException e) {
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    // ==================== HELPERS ====================

    private void clearFields() {
        tfTypeCulture.clear();
        dpDatePlantation.setValue(null);
        dpDateRecoltePrevue.setValue(null);
        cbEtatCroissance.setValue(null);
        cbParcelle.setValue(null);
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
