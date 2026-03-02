package org.example.pidev.controllers.ahmed;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.models.Vente;
import org.example.pidev.services.ahmed.ClientService;
import org.example.pidev.services.ahmed.PDFExportService;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.services.ahmed.VenteService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListeVenteController {

    @FXML private TableView<Vente> venteTable;
    @FXML private TableColumn<Vente, Integer> colId;
    @FXML private TableColumn<Vente, String> colDate;
    @FXML private TableColumn<Vente, String> colClient;
    @FXML private TableColumn<Vente, String> colMontant;
    @FXML private TextField searchIdField;

    private VenteService venteService;
    private ClientService clientService;
    private UtilisateurService utilisateurService;
    private List<Vente> allVentes;

    @FXML
    public void initialize() {
        venteService = new VenteService();
        clientService = new ClientService();
        utilisateurService = new UtilisateurService();
        allVentes = new ArrayList<>();

        // Configuration des colonnes
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIdVente()));

        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDateVente() != null
                ? cellData.getValue().getDateVente().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : "N/A"
        ));

        colClient.setCellValueFactory(cellData -> {
            Client client = clientService.getById(cellData.getValue().getIdClient());
            return new SimpleStringProperty(client != null ? client.getNom() : "Inconnu");
        });

        colMontant.setCellValueFactory(cellData -> new SimpleStringProperty(
            String.format("%.2f TND", cellData.getValue().getMontantTotal())
        ));


        refreshTable();
    }


    public void refreshTable() {
        allVentes = venteService.getAll();
        ObservableList<Vente> observableVentes = FXCollections.observableArrayList(allVentes);
        venteTable.setItems(observableVentes);
    }

    @FXML
    private void handleSearchById() {
        String searchText = searchIdField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un terme de recherche.", Alert.AlertType.WARNING);
            return;
        }

        List<Vente> filteredList = new ArrayList<>();

        for (Vente vente : allVentes) {
            // Recherche par client
            Client client = clientService.getById(vente.getIdClient());
            if (client != null && client.getNom().toLowerCase().contains(searchText)) {
                filteredList.add(vente);
                continue;
            }

            // Recherche par montant
            if (String.valueOf(vente.getMontantTotal()).contains(searchText)) {
                if (!filteredList.contains(vente)) {
                    filteredList.add(vente);
                }
            }
        }

        if (filteredList.isEmpty()) {
            showAlert("Résultat", "Aucune vente trouvée pour: " + searchText, Alert.AlertType.INFORMATION);
            venteTable.setItems(FXCollections.observableArrayList(allVentes));
        } else {
            venteTable.setItems(FXCollections.observableArrayList(filteredList));
            showAlert("Résultat", "Ventes trouvées! (" + filteredList.size() + " résultat(s))", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleResetSearch() {
        searchIdField.clear();
        refreshTable();
        showAlert("Succès", "Recherche réinitialisée.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportPDF() {
        if (venteTable.getItems().isEmpty()) {
            showAlert("Erreur", "La liste des ventes est vide.", Alert.AlertType.WARNING);
            return;
        }

        Vente selectedVente = venteTable.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("ventes_export_" + System.currentTimeMillis() + ".pdf");

        Stage stage = (Stage) venteTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                if (selectedVente != null) {
                    // Exporter seulement la vente sélectionnée
                    PDFExportService.exportVenteToPDF(selectedVente, file);
                    showAlert("Succès", "Vente exportée en PDF avec succès!\nFichier: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                } else {
                    // Exporter toute la liste
                    PDFExportService.exportVentesListToPDF(venteTable.getItems(), file);
                    showAlert("Succès", "Liste des ventes exportée en PDF avec succès!\nFichier: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ahmed/AjouterVente.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter une vente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            refreshTable();
        } catch (Exception e) {
            showAddVenteDialog();
        }
    }

    private void showAddVenteDialog() {
        Stage stage = new Stage();
        stage.setTitle("Ajouter une vente");
        stage.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<Client> clientCombo = new ComboBox<>();
        clientCombo.setItems(javafx.collections.FXCollections.observableArrayList(clientService.getAll()));
        clientCombo.setPromptText("Sélectionner un client");

        TextField montantField = new TextField();
        montantField.setPromptText("Montant");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        ComboBox<Utilisateur> userCombo = new ComboBox<>();
        userCombo.setItems(javafx.collections.FXCollections.observableArrayList(utilisateurService.getAll()));
        userCombo.setPromptText("Sélectionner un utilisateur");

        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientCombo, 1, 0);
        grid.add(new Label("Montant:"), 0, 1);
        grid.add(montantField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Utilisateur:"), 0, 3);
        grid.add(userCombo, 1, 3);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button addBtn = new Button("Ajouter");
        Button cancelBtn = new Button("Annuler");

        addBtn.setOnAction(e -> {
            if (clientCombo.getValue() != null && !montantField.getText().isEmpty() && userCombo.getValue() != null) {
                try {
                    Vente vente = new Vente();
                    vente.setIdClient(clientCombo.getValue().getIdClient());
                    vente.setMontantTotal(Double.parseDouble(montantField.getText()));
                    vente.setDateVente(datePicker.getValue());
                    vente.setIdUser(userCombo.getValue().getIdUser());
                    venteService.add(vente);
                    refreshTable();
                    stage.close();
                    showAlert("Succès", "Vente ajoutée avec succès", Alert.AlertType.INFORMATION);
                } catch (NumberFormatException ex) {
                    showAlert("Erreur", "Le montant doit être un nombre valide", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(addBtn, cancelBtn);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void handleModifier() {
        Vente selected = venteTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une vente à modifier", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ahmed/AjouterVente.fxml"));
            Scene scene = new Scene(loader.load());
            AjouterVenteController controller = loader.getController();
            controller.setVente(selected);
            Stage stage = new Stage();
            stage.setTitle("Modifier une vente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSupprimer() {
        Vente selected = venteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une vente à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette vente ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            venteService.delete(selected.getIdVente());
            refreshTable();
            showAlert("Succès", "Vente supprimée avec succès", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleStatistiques() {
        List<Vente> ventes = venteService.getAll();

        if (ventes.isEmpty()) {
            showAlert("Statistiques", "Aucune vente enregistrée pour le moment.", Alert.AlertType.INFORMATION);
            return;
        }

        double totalMontant = 0;
        double montantMoyen = 0;
        double montantMax = 0;
        double montantMin = Double.MAX_VALUE;

        for (Vente vente : ventes) {
            double montant = vente.getMontantTotal();
            totalMontant += montant;
            if (montant > montantMax) montantMax = montant;
            if (montant < montantMin) montantMin = montant;
        }

        montantMoyen = totalMontant / ventes.size();

        Alert statsAlert = new Alert(Alert.AlertType.INFORMATION);
        statsAlert.setTitle("📊 Statistiques des Ventes");
        statsAlert.setHeaderText("Résumé des statistiques");
        statsAlert.setContentText(
            "Total des ventes: " + String.format("%.2f", totalMontant) + " TND\n" +
            "Nombre de ventes: " + ventes.size() + "\n" +
            "Montant moyen: " + String.format("%.2f", montantMoyen) + " TND\n" +
            "Montant maximum: " + String.format("%.2f", montantMax) + " TND\n" +
            "Montant minimum: " + String.format("%.2f", montantMin) + " TND"
        );
        statsAlert.showAndWait();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showAlert(String msg) {
        showAlert("Information", msg, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRetour() {
        javafx.scene.Scene scene = venteTable.getScene();
        if (scene != null) {
            javafx.scene.input.KeyEvent escapeEvent = new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "",
                javafx.scene.input.KeyCode.ESCAPE, false, false, false, false
            );
            scene.getRoot().fireEvent(escapeEvent);
        }
    }
}