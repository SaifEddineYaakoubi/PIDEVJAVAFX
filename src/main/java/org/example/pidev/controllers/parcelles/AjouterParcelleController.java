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
import org.example.pidev.services.recoltes.GeoLocationService;
import org.example.pidev.services.recoltes.GeoLocationService.LocationResult;
import org.example.pidev.services.parcelles.ParcelleService;
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class AjouterParcelleController implements Initializable {

    // ID de l'utilisateur connecté (par défaut = 1)
    // ID utilisateur dynamique via Session
    private int getCurrentUserId() {
        return org.example.pidev.utils.Session.getOwnerUserId();
    }

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

    // Nouveaux éléments pour OpenStreetMap
    @FXML
    private ListView<String> lvSuggestions;

    @FXML
    private WebView webViewMap;

    @FXML
    private Label lblCoordinates;

    @FXML
    private Button btnSearchMap;

    private ParcelleService parcelleService;
    private GeoLocationService geoLocationService;
    private List<LocationResult> currentResults;
    private Timer searchTimer;
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les services
        parcelleService = new ParcelleService();
        geoLocationService = new GeoLocationService();

        // Remplir le ComboBox avec les états possibles
        cbEtat.setItems(FXCollections.observableArrayList("active", "repos", "exploitée"));

        // Effacer les messages quand l'utilisateur tape
        tfNom.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfSuperficie.textProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtat.valueProperty().addListener((obs, old, newVal) -> clearMessages());

        // Configurer la recherche de localisation avec délai (debounce)
        tfLocalisation.textProperty().addListener((obs, old, newVal) -> {
            clearMessages();
            if (searchTimer != null) {
                searchTimer.cancel();
            }
            if (newVal != null && newVal.trim().length() >= 3) {
                searchTimer = new Timer(true);
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> searchLocations(newVal.trim()));
                    }
                }, 500); // Attendre 500ms après la dernière frappe
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

        // Initialiser la carte avec une vue par défaut (Tunisie)
        if (webViewMap != null) {
            loadDefaultMap();
        }

        // Masquer les coordonnées par défaut
        if (lblCoordinates != null) {
            lblCoordinates.setText("");
        }
    }

    /**
     * Recherche des localisations via l'API Nominatim (OpenStreetMap).
     */
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

    /**
     * Sélectionner une localisation depuis les suggestions.
     */
    private void selectLocation(LocationResult location) {
        // Mettre à jour le champ de localisation avec le nom court
        tfLocalisation.setText(location.getShortName());

        // Sauvegarder les coordonnées
        selectedLatitude = location.getLatitude();
        selectedLongitude = location.getLongitude();

        // Afficher les coordonnées
        if (lblCoordinates != null) {
            lblCoordinates.setText("📌 GPS: " + location.getCoordinates());
        }

        // Masquer les suggestions
        lvSuggestions.setVisible(false);
        lvSuggestions.setManaged(false);

        // Afficher la carte avec le marqueur
        if (webViewMap != null) {
            String mapHtml = GeoLocationService.getMapHtml(
                    location.getLatitude(), location.getLongitude(), location.getShortName());
            webViewMap.getEngine().loadContent(mapHtml);
        }

        showSuccess("📍 Localisation sélectionnée : " + location.getShortName());
    }

    /**
     * Charge la carte par défaut (vue Tunisie).
     */
    private void loadDefaultMap() {
        String defaultHtml = GeoLocationService.getMapHtml(36.8065, 10.1815, "Tunisie");
        webViewMap.getEngine().loadContent(defaultHtml);
    }

    /**
     * Rechercher sur la carte (bouton).
     */
    @FXML
    void rechercherSurCarte(ActionEvent event) {
        String localisation = tfLocalisation.getText();
        if (localisation != null && !localisation.trim().isEmpty()) {
            searchLocations(localisation.trim());
        } else {
            showError("Veuillez saisir une localisation à rechercher.");
        }
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
            Parcelle parcelle = new Parcelle(nom.trim(), superficie, localisation.trim(), etat, getCurrentUserId());

            // Ajouter via le service (qui fait aussi la validation)
            parcelleService.add(parcelle);

            // Succès
            showSuccess("✅ Parcelle ajoutée avec succès !");
            AnimationUtils.showSuccessAnimation(lblSuccess);
            ActionHistoryService.getInstance().logAdd("Parcelle", parcelle.getNom());

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
        if (webViewMap != null) loadDefaultMap();
        if (lblCoordinates != null) lblCoordinates.setText("");
        selectedLatitude = 0;
        selectedLongitude = 0;
        tfNom.requestFocus();
    }


    // ==================== HELPERS ====================

    private void clearFields() {
        tfNom.clear();
        tfSuperficie.clear();
        tfLocalisation.clear();
        cbEtat.setValue(null);
        if (lvSuggestions != null) {
            lvSuggestions.setVisible(false);
            lvSuggestions.setManaged(false);
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
