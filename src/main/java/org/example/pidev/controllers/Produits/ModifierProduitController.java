package org.example.pidev.controllers.Produits;

import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ModifierProduitController {
    @FXML private TextField tfNom;
    @FXML private TextField tfType;
    @FXML private TextField tfUnite;
    @FXML private TextField tfPrixUnitaire;
    @FXML private Label lblInfo;

    private final ProduitService produitService = new ProduitService();
    private Produit produit;

    public void setProduit(Produit produit) {
        this.produit = produit;
        tfNom.setText(produit.getNom());
        tfType.setText(produit.getType());
        tfUnite.setText(produit.getUnite());
        tfPrixUnitaire.setText(String.valueOf(produit.getPrixUnitaire()));
    }

    @FXML
    private void update() {
        try {
            Produit produit = new Produit(tfNom.getText(), tfType.getText(), tfUnite.getText(), Double.parseDouble(tfPrixUnitaire.getText()));
            produit.setIdProduit(this.produit.getIdProduit());
            String erreur = produitService.controleSaisie(produit);
            if (erreur != null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText(erreur);
                alert.showAndWait();
                return;
            }
            produitService.update(produit);
            lblInfo.setText("Produit modifié avec succès !");
            closeWindow();
        } catch (Exception e) {
            lblInfo.setText("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        javafx.stage.Stage stage = (javafx.stage.Stage) tfNom.getScene().getWindow();
        stage.close();
    }
}
