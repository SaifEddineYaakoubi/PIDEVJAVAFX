package org.example.pidev.controllers.culture;

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
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private ComboBox<String> cbParcelle;

    @FXML
    private RadioButton rbGermination;

    @FXML
    private RadioButton rbCroissance;

    @FXML
    private RadioButton rbFloraison;

    @FXML
    private RadioButton rbMaturite;

    @FXML
    private Label lblDureeEstimee;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private Label lblInfo;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnReset;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private Culture currentCulture;
    private Culture originalCulture;
    private List<Parcelle> parcelles;
    private ToggleGroup toggleGroupEtat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();

        // Créer le groupe de toggle pour les RadioButtons
        toggleGroupEtat = new ToggleGroup();
        if (rbGermination != null) rbGermination.setToggleGroup(toggleGroupEtat);
        if (rbCroissance != null) rbCroissance.setToggleGroup(toggleGroupEtat);
        if (rbFloraison != null) rbFloraison.setToggleGroup(toggleGroupEtat);
        if (rbMaturite != null) rbMaturite.setToggleGroup(toggleGroupEtat);

        // Charger les parcelles disponibles
        loadParcelles();

        // Effacer les messages quand l'utilisateur interagit
        tfTypeCulture.textProperty().addListener((obs, old, newVal) -> clearMessages());
        dpDatePlantation.valueProperty().addListener((obs, old, newVal) -> {
            clearMessages();
            updateDureeEstimee();
        });
        dpDateRecoltePrevue.valueProperty().addListener((obs, old, newVal) -> {
            clearMessages();
            updateDureeEstimee();
        });
        cbParcelle.valueProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    private void loadParcelles() {
        parcelles = parcelleService.getAll();
        cbParcelle.getItems().clear();
        for (Parcelle p : parcelles) {
            cbParcelle.getItems().add(p.getIdParcelle() + " - " + p.getNom());
        }
    }

    private void updateDureeEstimee() {
        if (lblDureeEstimee == null) return;

        LocalDate datePlantation = dpDatePlantation.getValue();
        LocalDate dateRecolte = dpDateRecoltePrevue.getValue();

        if (datePlantation != null && dateRecolte != null) {
            long jours = ChronoUnit.DAYS.between(datePlantation, dateRecolte);
            if (jours > 0) {
                lblDureeEstimee.setText(jours + " jours");
                lblDureeEstimee.setStyle("-fx-font-size: 12px; -fx-text-fill: #1565C0;");
            } else {
                lblDureeEstimee.setText("Date invalide ⚠️");
                lblDureeEstimee.setStyle("-fx-font-size: 12px; -fx-text-fill: #C62828;");
            }
        } else {
            lblDureeEstimee.setText("-- jours");
        }
    }

    private void selectEtatCroissance(String etat) {
        if (etat == null) return;
        switch (etat.toLowerCase()) {
            case "germination":
                if (rbGermination != null) rbGermination.setSelected(true);
                break;
            case "croissance":
                if (rbCroissance != null) rbCroissance.setSelected(true);
                break;
            case "floraison":
                if (rbFloraison != null) rbFloraison.setSelected(true);
                break;
            case "maturité":
            case "mature":
                if (rbMaturite != null) rbMaturite.setSelected(true);
                break;
        }
    }

    private String getSelectedEtatCroissance() {
        if (toggleGroupEtat == null || toggleGroupEtat.getSelectedToggle() == null) {
            return null;
        }
        RadioButton selected = (RadioButton) toggleGroupEtat.getSelectedToggle();
        String text = selected.getText();
        if (text.contains("Germination")) return "germination";
        if (text.contains("Croissance")) return "croissance";
        if (text.contains("Floraison")) return "floraison";
        if (text.contains("Maturité")) return "mature";  // Le service attend "mature" et non "maturité"
        return text;
    }

    /**
     * Méthode appelée pour pré-remplir le formulaire avec les données de la culture
     */
    public void setCulture(Culture culture) {
        this.currentCulture = culture;
        // Sauvegarder les valeurs originales pour le reset
        this.originalCulture = new Culture(
                culture.getIdCulture(),
                culture.getTypeCulture(),
                culture.getDatePlantation(),
                culture.getDateRecoltePrevue(),
                culture.getEtatCroissance(),
                culture.getIdParcelle()
        );

        // Remplir les champs
        tfTypeCulture.setText(culture.getTypeCulture());
        dpDatePlantation.setValue(culture.getDatePlantation());
        dpDateRecoltePrevue.setValue(culture.getDateRecoltePrevue());
        selectEtatCroissance(culture.getEtatCroissance());

        // Sélectionner la parcelle correspondante
        for (Parcelle p : parcelles) {
            if (p.getIdParcelle() == culture.getIdParcelle()) {
                cbParcelle.setValue(p.getIdParcelle() + " - " + p.getNom());
                break;
            }
        }

        // Mettre à jour la durée estimée
        updateDureeEstimee();

        // Afficher l'info
        if (lblInfo != null) {
            lblInfo.setText("Modification de: " + culture.getTypeCulture());
        }
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        clearMessages();

        try {
            String typeCulture = tfTypeCulture.getText();
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecoltePrevue = dpDateRecoltePrevue.getValue();
            String etatCroissance = getSelectedEtatCroissance();
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

            if (etatCroissance == null) {
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
            AnimationUtils.showSuccessAnimation(lblSuccess);
            ActionHistoryService.getInstance().logUpdate("Culture", currentCulture.getTypeCulture());

            // Fermer la fenêtre après un court délai
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(e -> fermerFenetre(null));
            pause.play();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    void resetForm(ActionEvent event) {
        if (originalCulture != null) {
            tfTypeCulture.setText(originalCulture.getTypeCulture());
            dpDatePlantation.setValue(originalCulture.getDatePlantation());
            dpDateRecoltePrevue.setValue(originalCulture.getDateRecoltePrevue());
            selectEtatCroissance(originalCulture.getEtatCroissance());

            for (Parcelle p : parcelles) {
                if (p.getIdParcelle() == originalCulture.getIdParcelle()) {
                    cbParcelle.setValue(p.getIdParcelle() + " - " + p.getNom());
                    break;
                }
            }
            updateDureeEstimee();
        }
        clearMessages();
    }

    @FXML
    void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) tfTypeCulture.getScene().getWindow();
        stage.close();
    }

    // ==================== HELPERS ====================

    private void clearMessages() {
        lblError.setText("");
        lblSuccess.setText("");
    }

    private void showError(String message) {
        lblError.setText(message);
        lblSuccess.setText("");
        AnimationUtils.showErrorAnimation(lblError);
    }

    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblError.setText("");
    }
}
