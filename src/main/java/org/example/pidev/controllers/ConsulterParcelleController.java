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
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterParcelleController implements Initializable {

    @FXML
    private TableView<Parcelle> tableViewParcelles;

    @FXML
    private TableColumn<Parcelle, Integer> colId;

    @FXML
    private TableColumn<Parcelle, String> colNom;

    @FXML
    private TableColumn<Parcelle, Double> colSuperficie;

    @FXML
    private TableColumn<Parcelle, String> colLocalisation;

    @FXML
    private TableColumn<Parcelle, String> colEtat;

    @FXML
    private TableColumn<Parcelle, Void> colActions;

    @FXML
    private Label lblMessage;

    private ParcelleService parcelleService;
    private ObservableList<Parcelle> parcellesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parcelleService = new ParcelleService();

        // Configurer les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idParcelle"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Charger les données
        rafraichirListe(null);
    }

    @FXML
    void rafraichirListe(ActionEvent event) {
        parcellesList = FXCollections.observableArrayList(parcelleService.getAll());
        tableViewParcelles.setItems(parcellesList);
        showMessage("Liste rafraîchie - " + parcellesList.size() + " parcelle(s)", "#2E7D32");
    }

    @FXML
    void modifierParcelle(ActionEvent event) {
        Parcelle selected = tableViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une parcelle à modifier.", "#FF9800");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierparcelle.fxml"));
            Parent root = loader.load();

            // Passer la parcelle sélectionnée au contrôleur
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(selected);

            Stage stage = (Stage) tableViewParcelles.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - Modifier la Parcelle");
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void supprimerParcelle(ActionEvent event) {
        Parcelle selected = tableViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une parcelle à supprimer.", "#FF9800");
            return;
        }

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la parcelle");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la parcelle \"" + selected.getNom() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            parcelleService.delete(selected.getIdParcelle());
            parcellesList.remove(selected);
            showMessage("✅ Parcelle supprimée avec succès !", "#2E7D32");
        }
    }

    // ==================== NAVIGATION ====================

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
            Stage stage = (Stage) tableViewParcelles.getScene().getWindow();
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
