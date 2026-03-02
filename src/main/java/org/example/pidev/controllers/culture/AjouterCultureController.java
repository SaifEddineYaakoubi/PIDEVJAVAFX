package org.example.pidev.controllers.culture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    private CultureService cultureService;
    private ParcelleService parcelleService;
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
                lblDureeEstimee.setStyle("-fx-font-size: 13px; -fx-text-fill: #1565C0;");
            } else {
                lblDureeEstimee.setText("Date invalide ⚠️");
                lblDureeEstimee.setStyle("-fx-font-size: 13px; -fx-text-fill: #C62828;");
            }
        } else {
            lblDureeEstimee.setText("-- jours");
        }
    }

    private String getSelectedEtatCroissance() {
        if (toggleGroupEtat == null || toggleGroupEtat.getSelectedToggle() == null) {
            return null;
        }
        RadioButton selected = (RadioButton) toggleGroupEtat.getSelectedToggle();
        String text = selected.getText();
        // Extraire le texte sans l'emoji - utiliser les valeurs attendues par CultureService
        if (text.contains("Germination")) return "germination";
        if (text.contains("Croissance")) return "croissance";
        if (text.contains("Floraison")) return "floraison";
        if (text.contains("Maturité")) return "mature";  // Le service attend "mature" et non "maturité"
        return text;
    }

    @FXML
    void ajouterCulture(ActionEvent event) {
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

            // Créer l'objet Culture
            Culture culture = new Culture(typeCulture.trim(), datePlantation, dateRecoltePrevue, etatCroissance, idParcelle);

            // Ajouter via le service (lance une exception si erreur de validation)
            cultureService.add(culture);

            // Succès
            showSuccess("✅ Culture ajoutée avec succès !");
            AnimationUtils.showSuccessAnimation(lblSuccess);
            ActionHistoryService.getInstance().logAdd("Culture", culture.getTypeCulture());

            // Fermer la fenêtre après un court délai
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(e -> {
                Stage stage = (Stage) tfTypeCulture.getScene().getWindow();
                stage.close();
            });
            pause.play();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) tfTypeCulture.getScene().getWindow();
        stage.close();
    }

    @FXML
    void resetForm(ActionEvent event) {
        clearFields();
        clearMessages();
        tfTypeCulture.requestFocus();
    }


    // ==================== HELPERS ====================

    private void clearFields() {
        tfTypeCulture.clear();
        dpDatePlantation.setValue(null);
        dpDateRecoltePrevue.setValue(null);
        if (toggleGroupEtat != null) {
            toggleGroupEtat.selectToggle(null);
        }
        cbParcelle.setValue(null);
        if (lblDureeEstimee != null) {
            lblDureeEstimee.setText("-- jours");
        }
    }

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
