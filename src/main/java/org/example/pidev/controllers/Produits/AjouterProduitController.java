package org.example.pidev.controllers.Produits;

import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterProduitController {
    @FXML private TextField tfNom;
    @FXML private TextField tfType;
    @FXML private TextField tfUnite;
    @FXML private TextField tfPrixUnitaire;
    @FXML private Label lblMessage;

    private final ProduitService produitService = new ProduitService();


    @FXML
    private void add() {
        try {
            String nom = tfNom.getText();
            String type = tfType.getText();
            String unite = tfUnite.getText();
            double prixUnitaire = Double.parseDouble(tfPrixUnitaire.getText());
            Produit produit = new Produit(nom, type, unite, prixUnitaire);
            String erreur = produitService.controleSaisie(produit);
            if (erreur != null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText(erreur);
                alert.showAndWait();
                return;
            }
            boolean result = produitService.add(produit);
            if (result) {
                lblMessage.setText("Produit ajouté avec succès !");
                closeWindow();
            } else {
                lblMessage.setText("Erreur lors de l'ajout du produit.");
            }
        } catch (Exception e) {
            lblMessage.setText("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }
}
