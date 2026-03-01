package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.services.ClientService;

public class AjouterClientController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField villeField;

    private ClientService clientService;
    private Client client; // null pour ajout, non null pour modification

    public void initialize() {
        clientService = new ClientService();
    }

    public void setClient(Client c) {
        this.client = c;
        if (c != null) {
            nomField.setText(c.getNom());
            prenomField.setText(c.getPrenom());
            emailField.setText(c.getEmail());
            telephoneField.setText(c.getTelephone());
            adresseField.setText(c.getAdresse());
            villeField.setText(c.getVille());
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!validateInputs()) {
            return;
        }

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String ville = villeField.getText();

        if (client == null) {
            // CREATE - Ajouter un nouveau client
            client = new Client(nom, prenom, email, telephone, adresse, ville);
            if (clientService.add(client)) {
                showSuccessAlert("Client ajouté avec succès !");
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.close();
            } else {
                showErrorAlert("Erreur lors de l'ajout du client.");
            }
        } else {
            // UPDATE - Modifier le client existant
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setTelephone(telephone);
            client.setAdresse(adresse);
            client.setVille(ville);
            clientService.update(client);
            showSuccessAlert("Client modifié avec succès !");
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            errors.append("Le nom est obligatoire.\n");
        }
        if (prenomField.getText() == null || prenomField.getText().trim().isEmpty()) {
            errors.append("Le prénom est obligatoire.\n");
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            errors.append("L'email est obligatoire.\n");
        } else if (!emailField.getText().contains("@gmail.com")) {
            errors.append("L'email doit contenir @gmail.com\n");
        }
        if (telephoneField.getText() == null || telephoneField.getText().trim().isEmpty()) {
            errors.append("Le téléphone est obligatoire.\n");
        }
        if (adresseField.getText() == null || adresseField.getText().trim().isEmpty()) {
            errors.append("L'adresse est obligatoire.\n");
        }
        if (villeField.getText() == null || villeField.getText().trim().isEmpty()) {
            errors.append("La ville est obligatoire.\n");
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
        Stage stage = (Stage) nomField.getScene().getWindow();
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

