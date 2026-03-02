package org.example.pidev.controllers.parcelles;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.GeoLocationService;
import org.example.pidev.services.GeoLocationService.LocationResult;
import org.example.pidev.services.ParcelleService;
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
    private Label lblInfo;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnReset;

    // OpenStreetMap elements
    @FXML
    private ListView<String> lvSuggestions;

    @FXML
    private WebView webViewMap;

    @FXML
    private Label lblCoordinates;

    private ParcelleService parcelleService;
    private GeoLocationService geoLocationService;
    private Parcelle currentParcelle;
    private Parcelle originalParcelle;
    private List<LocationResult> currentResults;
    private Timer searchTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parcelleService = new ParcelleService();
        geoLocationService = new GeoLocationService();

        // Remplir le ComboBox avec les états possibles
        cbEtat.setItems(FXCollections.observableArrayList("active", "repos", "exploitée"));

        // Effacer les messages d'erreur quand l'utilisateur tape
        tfNom.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfSuperficie.textProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtat.valueProperty().addListener((obs, old, newVal) -> clearMessages());

        // Configurer la recherche de localisation avec délai (debounce)
        tfLocalisation.textProperty().addListener((obs, old, newVal) -> {
            clearMessages();
            if (searchTimer != null) searchTimer.cancel();
            if (newVal != null && newVal.trim().length() >= 3) {
                searchTimer = new Timer(true);
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> searchLocations(newVal.trim()));
                    }
                }, 500);
            } else {
                if (lvSuggestions != null) {
                    lvSuggestions.setVisible(false);
                    lvSuggestions.setManaged(false);
                }
            }
        });

        // Configurer la sélection d'une suggestion
        if (lvSuggestions != null) {
            lvSuggestions.setVisible(false);
            lvSuggestions.setManaged(false);
            lvSuggestions.setOnMouseClicked(event -> {
                int selectedIndex = lvSuggestions.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && currentResults != null && selectedIndex < currentResults.size()) {
                    selectLocation(currentResults.get(selectedIndex));
                }
            });
        }

        // Initialiser la carte
        if (webViewMap != null) {
            String defaultHtml = GeoLocationService.getMapHtml(36.8065, 10.1815, "Tunisie");
            webViewMap.getEngine().loadContent(defaultHtml);
        }

        if (lblCoordinates != null) lblCoordinates.setText("");
    }

    private void searchLocations(String query) {
        try {
            currentResults = geoLocationService.searchLocation(query, 5);
            if (currentResults != null && !currentResults.isEmpty()) {
                lvSuggestions.getItems().clear();
                for (LocationResult result : currentResults) {
                    lvSuggestions.getItems().add("📍 " + result.getDisplayName());
                }
                lvSuggestions.setVisible(true);
                lvSuggestions.setManaged(true);
                lvSuggestions.setPrefHeight(Math.min(currentResults.size() * 35, 175));
            } else {
                lvSuggestions.setVisible(false);
                lvSuggestions.setManaged(false);
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur recherche localisation: " + e.getMessage());
        }
    }

    private void selectLocation(LocationResult location) {
        tfLocalisation.setText(location.getShortName());
        if (lblCoordinates != null) lblCoordinates.setText("📌 GPS: " + location.getCoordinates());
        lvSuggestions.setVisible(false);
        lvSuggestions.setManaged(false);
        if (webViewMap != null) {
            String mapHtml = GeoLocationService.getMapHtml(location.getLatitude(), location.getLongitude(), location.getShortName());
            webViewMap.getEngine().loadContent(mapHtml);
        }
        showSuccess("📍 Localisation sélectionnée : " + location.getShortName());
    }

    @FXML
    void rechercherSurCarte(ActionEvent event) {
        String localisation = tfLocalisation.getText();
        if (localisation != null && !localisation.trim().isEmpty()) {
            searchLocations(localisation.trim());
        } else {
            showError("Veuillez saisir une localisation à rechercher.");
        }
    }

    /**
     * Méthode appelée pour pré-remplir le formulaire avec les données de la parcelle
     */
    public void setParcelle(Parcelle parcelle) {
        this.currentParcelle = parcelle;
        this.originalParcelle = new Parcelle(
                parcelle.getIdParcelle(),
                parcelle.getNom(),
                parcelle.getSuperficie(),
                parcelle.getLocalisation(),
                parcelle.getEtat(),
                parcelle.getIdUser()
        );

        tfNom.setText(parcelle.getNom());
        tfSuperficie.setText(String.valueOf(parcelle.getSuperficie()));
        tfLocalisation.setText(parcelle.getLocalisation());
        cbEtat.setValue(parcelle.getEtat());

        if (lblInfo != null) lblInfo.setText("Modification de: " + parcelle.getNom());

        // Charger la carte pour la localisation actuelle
        if (webViewMap != null && parcelle.getLocalisation() != null) {
            new Thread(() -> {
                var results = geoLocationService.searchLocation(parcelle.getLocalisation(), 1);
                Platform.runLater(() -> {
                    if (results != null && !results.isEmpty()) {
                        var loc = results.get(0);
                        String mapHtml = GeoLocationService.getMapHtml(loc.getLatitude(), loc.getLongitude(), parcelle.getNom());
                        webViewMap.getEngine().loadContent(mapHtml);
                        if (lblCoordinates != null) lblCoordinates.setText("📌 GPS: " + loc.getCoordinates());
                    }
                });
            }).start();
        }
    }

    @FXML
    void modifierParcelle(ActionEvent event) {
        clearMessages();

        try {
            String nom = tfNom.getText();
            String superficieStr = tfSuperficie.getText();
            String localisation = tfLocalisation.getText();
            String etat = cbEtat.getValue();

            if (nom == null || nom.trim().isEmpty()) { showError("Le nom de la parcelle est obligatoire."); return; }
            if (superficieStr == null || superficieStr.trim().isEmpty()) { showError("La superficie est obligatoire."); return; }
            if (localisation == null || localisation.trim().isEmpty()) { showError("La localisation est obligatoire."); return; }
            if (etat == null || etat.trim().isEmpty()) { showError("Veuillez sélectionner un état."); return; }

            double superficie;
            try {
                superficie = Double.parseDouble(superficieStr.trim());
            } catch (NumberFormatException e) {
                showError("La superficie doit être un nombre valide.");
                return;
            }

            currentParcelle.setNom(nom.trim());
            currentParcelle.setSuperficie(superficie);
            currentParcelle.setLocalisation(localisation.trim());
            currentParcelle.setEtat(etat);
            currentParcelle.setIdUser(CURRENT_USER_ID);

            parcelleService.update(currentParcelle);

            showSuccess("✅ Parcelle modifiée avec succès !");
            AnimationUtils.showSuccessAnimation(lblSuccess);
            ActionHistoryService.getInstance().logUpdate("Parcelle", currentParcelle.getNom());

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
        if (originalParcelle != null) {
            tfNom.setText(originalParcelle.getNom());
            tfSuperficie.setText(String.valueOf(originalParcelle.getSuperficie()));
            tfLocalisation.setText(originalParcelle.getLocalisation());
            cbEtat.setValue(originalParcelle.getEtat());
        }
        clearMessages();
    }

    @FXML
    void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) tfNom.getScene().getWindow();
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
