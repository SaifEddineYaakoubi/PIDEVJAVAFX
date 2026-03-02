package org.example.pidev.controllers.ventes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.models.Vente;
import org.example.pidev.services.ventes.ClientService;
import org.example.pidev.services.ventes.PDFExportService;
import org.example.pidev.services.ventes.VenteService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTelephone;
    @FXML private TableColumn<Client, String> colStatut;
    @FXML private TextField searchIdField;

    private ClientService clientService;
    private List<Client> allClients;

    @FXML
    public void initialize() {
        clientService = new ClientService();
        allClients = new ArrayList<>();

        // Configuration des colonnes
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIdClient()));

        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()
        ));

        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : "N/A"
        ));

        colTelephone.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getTelephone() != null ? cellData.getValue().getTelephone() : "N/A"
        ));

        colStatut.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getStatutFidelite() != null ? cellData.getValue().getStatutFidelite() : "Standard"
        ));

        refreshTable();
    }

    public void refreshTable() {
        allClients = clientService.getAll();
        clientService.enrichClients(allClients);
        ObservableList<Client> observableClients = FXCollections.observableArrayList(allClients);
        clientTable.setItems(observableClients);
    }

    @FXML
    private void handleSearchById() {
        String searchText = searchIdField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un terme de recherche.", Alert.AlertType.WARNING);
            return;
        }

        List<Client> filteredList = new ArrayList<>();

        for (Client client : allClients) {
            // Recherche par ID
            // Recherche par Nom
            if ((client.getNom() + " " + client.getPrenom()).toLowerCase().contains(searchText)) {
                filteredList.add(client);
                continue;
            }

            // Recherche par Email
            if (client.getEmail() != null && client.getEmail().toLowerCase().contains(searchText)) {
                if (!filteredList.contains(client)) {
                    filteredList.add(client);
                }
            }
        }

        if (filteredList.isEmpty()) {
            showAlert("Résultat", "Aucun client trouvé pour: " + searchText, Alert.AlertType.INFORMATION);
            clientTable.setItems(FXCollections.observableArrayList(allClients));
        } else {
            clientTable.setItems(FXCollections.observableArrayList(filteredList));
            showAlert("Résultat", "Clients trouvés! (" + filteredList.size() + " résultat(s))", Alert.AlertType.INFORMATION);
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
        if (clientTable.getItems().isEmpty()) {
            showAlert("Erreur", "La liste des clients est vide.", Alert.AlertType.WARNING);
            return;
        }

        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("clients_export_" + System.currentTimeMillis() + ".pdf");

        Stage stage = (Stage) clientTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                if (selectedClient != null) {
                    PDFExportService.exportClientToPDF(selectedClient, file);
                    showAlert("Succès", "Client exporté en PDF avec succès!\nFichier: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                } else {
                    PDFExportService.exportClientsListToPDF(clientTable.getItems(), file);
                    showAlert("Succès", "Liste des clients exportée en PDF avec succès!\nFichier: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventes/AjouterClient.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter un client");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModifier() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un client à modifier.", Alert.AlertType.INFORMATION);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventes/AjouterClient.fxml"));
            Scene scene = new Scene(loader.load());
            AjouterClientController ctrl = loader.getController();
            ctrl.setClient(selected);
            Stage stage = new Stage();
            stage.setTitle("Modifier un client");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSupprimer() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un client à supprimer.", Alert.AlertType.INFORMATION);
            return;
        }

        VenteService venteService = new VenteService();
        List<Vente> ventesClient = venteService.getVentesByClient(selected.getIdClient());

        if (!ventesClient.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impossible de supprimer");
            alert.setHeaderText("Le client a des ventes associées");
            alert.setContentText("Ce client a " + ventesClient.size() + " vente(s) associée(s).\n" +
                    "Vous devez d'abord supprimer toutes les ventes avant de pouvoir supprimer ce client.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le client");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer le client \"" + selected.getNom() + "\" ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                clientService.delete(selected.getIdClient());
                showAlert("Succès", "Client supprimé avec succès.", Alert.AlertType.INFORMATION);
                refreshTable();
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression du client:\n" + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleGenerateRemise() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un client.", Alert.AlertType.INFORMATION);
            return;
        }

        String statut = selected.getStatutFidelite();
        if ("VIP".equals(statut)) {
            Double totalAchats = selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0;
            Double remise = clientService.calculateRemiseVIP(totalAchats);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✨ Remise VIP");
            alert.setHeaderText("Client VIP - Remise Générale");
            alert.setContentText(String.format(
                "🎉 Client: %s\n" +
                "Total Achats: %.2f DT\n" +
                "Remise VIP (10%%): %.2f DT",
                selected.getNom(), totalAchats, remise
            ));
            alert.showAndWait();
        } else if ("Fidèle".equals(statut)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🌟 Client Fidèle");
            alert.setHeaderText("Promotion Fidèle");
            alert.setContentText(String.format(
                "Client: %s\nTotal Achats: %.2f DT\n" +
                "Montant pour VIP: %.2f DT",
                selected.getNom(),
                selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0,
                5000.0 - (selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0)
            ));
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("📈 Progression Client");
            alert.setHeaderText("Client Standard");
            alert.setContentText(String.format(
                "Client: %s\nTotal Achats: %.2f DT\n" +
                "Pour Fidèle: %.2f DT | Pour VIP: %.2f DT",
                selected.getNom(),
                selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0,
                1000.0 - (selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0),
                5000.0 - (selected.getTotalAchats() != null ? selected.getTotalAchats() : 0.0)
            ));
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

