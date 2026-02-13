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

public class ModifierCultureController implements Initializable {

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
    private Button btnModifier;

    @FXML
    private Button btnAnnuler;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private Culture currentCulture;
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

    /**
     * Méthode appelée pour pré-remplir le formulaire avec les données de la culture
     */
    public void setCulture(Culture culture) {
        this.currentCulture = culture;
        tfTypeCulture.setText(culture.getTypeCulture());
        dpDatePlantation.setValue(culture.getDatePlantation());
        dpDateRecoltePrevue.setValue(culture.getDateRecoltePrevue());
        cbEtatCroissance.setValue(culture.getEtatCroissance());

        // Sélectionner la parcelle correspondante
        for (Parcelle p : parcelles) {
            if (p.getIdParcelle() == culture.getIdParcelle()) {
                cbParcelle.setValue(p.getIdParcelle() + " - " + p.getNom());
                break;
            }
        }
    }

    @FXML
    void modifierCulture(ActionEvent event) {
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

            // Mettre à jour l'objet Culture
            currentCulture.setTypeCulture(typeCulture.trim());
            currentCulture.setDatePlantation(datePlantation);
            currentCulture.setDateRecoltePrevue(dateRecoltePrevue);
            currentCulture.setEtatCroissance(etatCroissance);
            currentCulture.setIdParcelle(idParcelle);

            // Modifier via le service
            cultureService.update(currentCulture);

            showSuccess("✅ Culture modifiée avec succès !");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    void retourListe(ActionEvent event) {
        navigateToConsulterCulture(event);
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
            Stage stage = (Stage) tfTypeCulture.getScene().getWindow();
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
