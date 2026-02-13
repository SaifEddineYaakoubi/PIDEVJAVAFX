package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.example.pidev.services.CultureService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterCultureController implements Initializable {

    @FXML
    private TableView<Culture> tableViewCultures;

    @FXML
    private TableColumn<Culture, Integer> colId;

    @FXML
    private TableColumn<Culture, String> colType;

    @FXML
    private TableColumn<Culture, LocalDate> colDatePlantation;

    @FXML
    private TableColumn<Culture, LocalDate> colDateRecolte;

    @FXML
    private TableColumn<Culture, String> colEtat;

    @FXML
    private TableColumn<Culture, Integer> colParcelle;

    @FXML
    private TableColumn<Culture, Void> colActions;

    @FXML
    private Label lblMessage;

    private CultureService cultureService;
    private ObservableList<Culture> culturesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();

        // Configurer les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idCulture"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeCulture"));
        colDatePlantation.setCellValueFactory(new PropertyValueFactory<>("datePlantation"));
        colDateRecolte.setCellValueFactory(new PropertyValueFactory<>("dateRecoltePrevue"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatCroissance"));
        colParcelle.setCellValueFactory(new PropertyValueFactory<>("idParcelle"));

        // Charger les données
        rafraichirListe(null);
    }

    @FXML
    void rafraichirListe(ActionEvent event) {
        culturesList = FXCollections.observableArrayList(cultureService.getAll());
        tableViewCultures.setItems(culturesList);
        showMessage("Liste rafraîchie - " + culturesList.size() + " culture(s)", "#2E7D32");
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
