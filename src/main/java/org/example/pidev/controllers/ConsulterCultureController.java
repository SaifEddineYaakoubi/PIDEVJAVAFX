package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterCultureController implements Initializable {

    @FXML
    private TableView<Culture> tableViewCultures;

    @FXML
    private TableColumn<Culture, String> colType;

    @FXML
    private TableColumn<Culture, LocalDate> colDatePlantation;

    @FXML
    private TableColumn<Culture, LocalDate> colDateRecolte;

    @FXML
    private TableColumn<Culture, String> colEtat;

    @FXML
    private TableColumn<Culture, String> colParcelle;

    @FXML
    private TextField tfRecherche;

    @FXML
    private Label lblMessage;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private ObservableList<Culture> culturesList;
    private FilteredList<Culture> filteredCultures;
    private Map<Integer, String> parcellesMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();

        // Charger les parcelles dans une map pour récupérer les noms
        loadParcellesMap();

        // Configurer les colonnes
        colType.setCellValueFactory(new PropertyValueFactory<>("typeCulture"));
        colDatePlantation.setCellValueFactory(new PropertyValueFactory<>("datePlantation"));
        colDateRecolte.setCellValueFactory(new PropertyValueFactory<>("dateRecoltePrevue"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatCroissance"));
        colParcelle.setCellValueFactory(new PropertyValueFactory<>("nomParcelle"));

        // Charger les données
        loadData();

        // Configurer la recherche en temps réel
        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredCultures.setPredicate(culture -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase().trim();

                // Recherche dans type et état de croissance
                if (culture.getTypeCulture().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (culture.getEtatCroissance().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (culture.getNomParcelle() != null && culture.getNomParcelle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            showMessage("Résultats: " + filteredCultures.size() + " culture(s)", "#2E7D32");
        });
    }

    private void loadParcellesMap() {
        parcellesMap = new HashMap<>();
        for (Parcelle p : parcelleService.getAll()) {
            parcellesMap.put(p.getIdParcelle(), p.getNom());
        }
    }

    private void loadData() {
        culturesList = FXCollections.observableArrayList(cultureService.getAll());

        // Associer le nom de parcelle à chaque culture
        for (Culture c : culturesList) {
            String nomParcelle = parcellesMap.get(c.getIdParcelle());
            c.setNomParcelle(nomParcelle != null ? nomParcelle : "Parcelle #" + c.getIdParcelle());
        }

        filteredCultures = new FilteredList<>(culturesList, c -> true);
        tableViewCultures.setItems(filteredCultures);
        showMessage("Liste chargée - " + culturesList.size() + " culture(s)", "#2E7D32");
    }

    @FXML
    void rafraichirListe(ActionEvent event) {
        tfRecherche.clear();
        loadParcellesMap();
        loadData();
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        Culture selected = tableViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une culture à modifier.", "#FF9800");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierculture.fxml"));
            Parent root = loader.load();

            // Passer la culture sélectionnée au contrôleur
            ModifierCultureController controller = loader.getController();
            controller.setCulture(selected);

            Stage stage = (Stage) tableViewCultures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - Modifier la Culture");
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void supprimerCulture(ActionEvent event) {
        Culture selected = tableViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une culture à supprimer.", "#FF9800");
            return;
        }

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la culture");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la culture \"" + selected.getTypeCulture() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cultureService.delete(selected.getIdCulture());
            culturesList.remove(selected);
            showMessage("✅ Culture supprimée avec succès !", "#2E7D32");
        }
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
    void navigateToAjouterCulture(ActionEvent event) {
        navigateTo("/ajouterculture.fxml", "Ajouter une Culture");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewCultures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - " + title);
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    // ==================== HELPERS ====================

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
    }
}
