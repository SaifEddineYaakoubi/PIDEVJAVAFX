package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pidev.models.Rendement;
import org.example.pidev.services.RendementService;

import java.io.IOException;
import java.util.List;

/**
 * EXEMPLE DE CONTRÔLEUR POUR L'INTÉGRATION D'AddRendement.fxml
 * Ce fichier montre comment intégrer le formulaire d'ajout de rendement
 * dans votre contrôleur principal
 */
public class RendementControllerExample {

    @FXML
    private TableView<Rendement> tableRendement;

    @FXML
    private TableColumn<Rendement, Integer> colIdRendement;

    @FXML
    private TableColumn<Rendement, Double> colSurface;

    @FXML
    private TableColumn<Rendement, Double> colQuantite;

    @FXML
    private TableColumn<Rendement, Double> colProductivite;

    @FXML
    private TableColumn<Rendement, Integer> colIdRecolte;

    @FXML
    private Button btnAjouterRendement;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRafraichir;

    @FXML
    private Label lblTotal;

    private RendementService rendementService;

    @FXML
    public void initialize() {
        rendementService = new RendementService();

        // Initialiser les colonnes de la table
        setupTableColumns();

        // Charger les rendements
        loadRendements();

        // Configurer les boutons
        setupButtonActions();

        // Mettre à jour le total
        updateTotal();
    }

    /**
     * Configure les colonnes de la table
     */
    private void setupTableColumns() {
        colIdRendement.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdRendement()).asObject());
        colSurface.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSurfaceExploitee()).asObject());
        colQuantite.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getQuantiteTotale()).asObject());
        colProductivite.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getProductivite()).asObject());
        colIdRecolte.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdRecolte()).asObject());
    }

    /**
     * Charge les rendements depuis la base de données
     */
    private void loadRendements() {
        try {
            List<Rendement> rendements = rendementService.getAll();
            ObservableList<Rendement> observableRendements = FXCollections.observableArrayList(rendements);
            tableRendement.setItems(observableRendements);
            System.out.println("✓ " + rendements.size() + " rendement(s) chargé(s)");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du chargement des rendements: " + e.getMessage());
            showError("Erreur", "Impossible de charger les rendements");
        }
    }

    /**
     * Configure les actions des boutons
     */
    private void setupButtonActions() {
        btnAjouterRendement.setOnAction(e -> onAjouterRendement());
        btnModifier.setOnAction(e -> onModifierRendement());
        btnSupprimer.setOnAction(e -> onSupprimerRendement());
        btnRafraichir.setOnAction(e -> refreshRendementTable());
    }

    /**
     * MÉTHODE PRINCIPALE: Ouvre le formulaire d'ajout de rendement
     */
    @FXML
    private void onAjouterRendement() {
        try {
            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendement.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AddRendementController controller = loader.getController();

            // Créer et configurer la stage
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Rendement");
            stage.setScene(new Scene(root, 850, 950));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);

            // Passer la stage au contrôleur
            controller.setStage(stage);

            // Afficher et attendre la fermeture
            stage.showAndWait();

            // Rafraîchir la table après la fermeture
            refreshRendementTable();

            System.out.println("✓ Formulaire d'ajout fermé");

        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout de rendement\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Modifie un rendement sélectionné
     */
    @FXML
    private void onModifierRendement() {
        Rendement selectedRendement = tableRendement.getSelectionModel().getSelectedItem();

        if (selectedRendement == null) {
            showWarning("Attention", "Veuillez sélectionner un rendement à modifier");
            return;
        }

        // À implémenter: ouvrir un formulaire de modification
        showInfo("Info", "La modification n'est pas encore impl��mentée");
    }

    /**
     * Supprime un rendement sélectionné
     */
    @FXML
    private void onSupprimerRendement() {
        Rendement selectedRendement = tableRendement.getSelectionModel().getSelectedItem();

        if (selectedRendement == null) {
            showWarning("Attention", "Veuillez sélectionner un rendement à supprimer");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le rendement?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce rendement?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                rendementService.delete(selectedRendement.getIdRendement());
                showSuccess("Succès", "Rendement supprimé avec succès");
                refreshRendementTable();
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    /**
     * Rafraîchit la table des rendements
     */
    public void refreshRendementTable() {
        loadRendements();
        updateTotal();
        System.out.println("✓ Table rafraîchie");
    }

    /**
     * Met à jour le nombre total de rendements
     */
    private void updateTotal() {
        int total = tableRendement.getItems().size();
        lblTotal.setText("Total: " + total + " rendement(s)");
    }

    // =====================
    // MÉTHODES UTILITAIRES
    // =====================

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'avertissement
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de succès
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'information
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

