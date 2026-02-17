package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.models.Rendement;
import org.example.pidev.services.RendementService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RendementController {

    @FXML
    private TableView<Rendement> tableRendements;
    @FXML
    private TableColumn<Rendement, Integer> colId;
    @FXML
    private TableColumn<Rendement, Double> colSurface;
    @FXML
    private TableColumn<Rendement, Double> colQuantite;
    @FXML
    private TableColumn<Rendement, Double> colProductivite;
    @FXML
    private TableColumn<Rendement, Integer> colRecolte;
    @FXML
    private TableColumn<Rendement, String> colAction;

    @FXML
    private TextField searchField;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnActualiser;
    @FXML
    private Button btnChercher;

    private RendementService rendementService;
    private ObservableList<Rendement> rendementList;

    @FXML
    public void initialize() {
        try {
            rendementService = new RendementService();
            setupTableColumns();
            loadRendements();
            setupButtonListeners();
            updateStatus("✅ Données chargées");
        } catch (Exception e) {
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idRendement"));
        colSurface.setCellValueFactory(new PropertyValueFactory<>("surfaceExploitee"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteTotale"));
        colProductivite.setCellValueFactory(new PropertyValueFactory<>("productivite"));
        colRecolte.setCellValueFactory(new PropertyValueFactory<>("idRecolte"));

        colAction.setCellFactory(col -> new TableCell<Rendement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Rendement rendement = getTableRow().getItem();
                    HBox hbox = new HBox(5);

                    Button btnEdit = new Button("✏️ Modifier");
                    btnEdit.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px;");
                    btnEdit.setOnAction(e -> handleModifier(rendement));

                    Button btnDelete = new Button("🗑️ Supprimer");
                    btnDelete.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px;");
                    btnDelete.setOnAction(e -> handleSupprimer(rendement));

                    hbox.getChildren().addAll(btnEdit, btnDelete);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void loadRendements() {
        try {
            List<Rendement> rendements = rendementService.getAll();
            rendementList = FXCollections.observableArrayList(rendements);
            tableRendements.setItems(rendementList);
            lblTotal.setText("Total: " + rendements.size() + " rendements");
            updateStatus("✅ " + rendements.size() + " rendements chargés");
        } catch (Exception e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void setupButtonListeners() {
        btnAjouter.setOnAction(e -> handleAjouter());
        btnActualiser.setOnAction(e -> handleActualiser());
        btnChercher.setOnAction(e -> handleChercher());
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendement.fxml"));
            Parent root = loader.load();
            AddRendementController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Rendement");
            stage.setScene(new Scene(root, 850, 950));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            controller.setStage(stage);

            stage.showAndWait();
            loadRendements(); // rafraîchit la table
        } catch (IOException e) {
            showError("Impossible d'ouvrir le formulaire AddRendement: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualiser() { loadRendements(); }

    @FXML
    private void handleChercher() {
        if (searchField.getText() == null || searchField.getText().isEmpty()) {
            loadRendements();
            return;
        }
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Rendement> filtered = FXCollections.observableArrayList();
        for (Rendement r : rendementList) {
            if (String.valueOf(r.getIdRendement()).contains(searchText) ||
                    String.valueOf(r.getIdRecolte()).contains(searchText) ||
                    String.valueOf(r.getProductivite()).contains(searchText)) {
                filtered.add(r);
            }
        }
        tableRendements.setItems(filtered);
        updateStatus("🔍 " + filtered.size() + " résultats trouvés");
    }

    private void handleModifier(Rendement rendement) {
        try {
            System.out.println("📝 Ouverture détail rendement ID: " + rendement.getIdRendement());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendementDetail.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et charger les données
            RendementDetailController controller = loader.getController();
            controller.loadRendement(rendement.getIdRendement());

            // Définir le callback pour retour à la liste
            controller.setCallbackRetour(() -> {
                System.out.println("🔄 Retour à la liste des rendements");
                loadRendements();
            });

            // Créer et afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Détail du Rendement");
            stage.setScene(new Scene(root, 800, 900));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);

            System.out.println("✅ Fenêtre détail ouverte");
            stage.showAndWait();

            // Rafraîchir la table après fermeture
            loadRendements();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de RendementDetail.fxml: " + e.getMessage());
            e.printStackTrace();
            showError("Impossible d'ouvrir les détails du rendement: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur: " + e.getMessage());
        }
    }

    private void handleSupprimer(Rendement rendement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer un rendement");
        alert.setContentText("Voulez-vous supprimer le rendement ID: " + rendement.getIdRendement() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rendementService.delete(rendement.getIdRendement());
                showSuccess("Rendement supprimé");
                loadRendements();
            } catch (Exception e) { showError("Erreur: " + e.getMessage()); }
        }
    }

    private void updateStatus(String message) { lblStatus.setText(message); }
    private void showError(String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
    private void showSuccess(String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    private void showInfo(String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
}
