package org.example.pidev.controllers.ventes;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.models.Vente;
import org.example.pidev.services.ventes.ClientService;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.services.ventes.VenteService;

import java.time.LocalDate;
import java.util.List;

public class AjouterVenteController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField montantField;
    @FXML
    private ComboBox<Client> clientCombo;
    @FXML
    private ComboBox<Utilisateur> userCombo;
    @FXML
    private Label taxLabel;
    @FXML
    private Label clientIdLabel;
    @FXML
    private Label montantHTLabel;
    @FXML
    private Label totalTTCLabel;

    private VenteService venteService;
    private ClientService clientService;
    private UtilisateurService utilisateurService;
    private Vente vente; // null pour ajout, non null pour modification

    public void initialize() {
        venteService = new VenteService();
        clientService = new ClientService();
        utilisateurService = new UtilisateurService();

        List<Client> clients = clientService.getAll();
        clientCombo.getItems().setAll(clients);

        // Afficher le nom du client sélectionné
        clientCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clientIdLabel.setText("Client: " + newVal.getNom());
            } else {
                clientIdLabel.setText("Client: --");
            }
        });

        List<Utilisateur> users = utilisateurService.getAll();
        userCombo.getItems().setAll(users);

        // Pré-sélectionner l'utilisateur courant
        Utilisateur currentUser = org.example.pidev.utils.Session.getCurrentUser();
        if (currentUser != null) {
            for (Utilisateur u : users) {
                if (u.getIdUser() == currentUser.getIdUser()) {
                    userCombo.getSelectionModel().select(u);
                    break;
                }
            }
        }

        montantField.textProperty().addListener((obs, oldText, newText) -> computeTax());
    }

    private void computeTax() {
        try {
            double montantTTC = Double.parseDouble(montantField.getText());
            // Calculer le montant HT (montantTTC / 1.19)
            double montantHT = montantTTC / 1.19;
            // Calculer la taxe
            double tax = montantTTC - montantHT;

            montantHTLabel.setText(String.format("%.2f TND", montantHT));
            taxLabel.setText(String.format("%.2f TND", tax));
            totalTTCLabel.setText(String.format("%.2f TND", montantTTC));
        } catch (NumberFormatException ex) {
            montantHTLabel.setText("0.00 TND");
            taxLabel.setText("0.00 TND");
            totalTTCLabel.setText("0.00 TND");
        }
    }

    public void setVente(Vente v) {
        this.vente = v;
        if (v != null) {
            datePicker.setValue(v.getDateVente());
            montantField.setText(String.valueOf(v.getMontantTotal()));
            Client selectedClient = clientService.getById(v.getIdClient());
            if (selectedClient != null) {
                clientCombo.getSelectionModel().select(selectedClient);
                clientIdLabel.setText("Client: " + selectedClient.getNom());
            }
            Utilisateur selectedUser = utilisateurService.getById(v.getIdUser());
            if (selectedUser != null) {
                userCombo.getSelectionModel().select(selectedUser);
            }
            computeTax();
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!validateInputs()) {
            return;
        }
        LocalDate date = datePicker.getValue();
        double montant = Double.parseDouble(montantField.getText());
        Client c = clientCombo.getSelectionModel().getSelectedItem();
        Utilisateur u = userCombo.getSelectionModel().getSelectedItem();

        if (vente == null) {
            vente = new Vente(date, montant, c.getIdClient(), u.getIdUser());
            try {
                if (venteService.add(vente)) {
                    showSuccessAlert("Vente ajoutée avec succès !");
                    Stage stage = (Stage) montantField.getScene().getWindow();
                    stage.close();
                } else {
                    showErrorAlert("Erreur lors de l'ajout de la vente.");
                }
            } catch (Exception e) {
                showErrorAlert("Erreur: " + e.getMessage());
            }
        } else {
            vente.setDateVente(date);
            vente.setMontantTotal(montant);
            vente.setIdClient(c.getIdClient());
            vente.setIdUser(u.getIdUser());
            try {
                venteService.update(vente);
                showSuccessAlert("Vente modifiée avec succès !");
                Stage stage = (Stage) montantField.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                showErrorAlert("Erreur: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (datePicker.getValue() == null) {
            errors.append("La date est obligatoire.\n");
        } else {
            LocalDate selectedDate = datePicker.getValue();
            // La date ne peut pas être dans le futur
            if (selectedDate.isAfter(LocalDate.now())) {
                errors.append("La date de vente ne peut pas être dans le futur.\n");
            }
            // La date ne peut pas être trop ancienne (plus de 10 ans)
            if (selectedDate.isBefore(LocalDate.now().minusYears(10))) {
                errors.append("La date de vente ne peut pas être antérieure à 10 ans.\n");
            }
        }
        if (montantField.getText() == null || montantField.getText().trim().isEmpty()) {
            errors.append("Le montant est obligatoire.\n");
        } else {
            try {
                double m = Double.parseDouble(montantField.getText());
                if (m <= 0) {
                    errors.append("Le montant doit être supérieur à zéro.\n");
                }
            } catch (NumberFormatException ex) {
                errors.append("Le montant doit être un nombre valide.\n");
            }
        }
        if (clientCombo.getSelectionModel().getSelectedItem() == null) {
            errors.append("Veuillez sélectionner un client.\n");
        }
        if (userCombo.getSelectionModel().getSelectedItem() == null) {
            errors.append("Veuillez sélectionner un utilisateur.\n");
        }
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Erreur de saisie");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) montantField.getScene().getWindow();
        stage.close();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}