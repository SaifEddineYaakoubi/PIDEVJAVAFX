package org.example.pidev.controllers;

import org.example.pidev.models.Produit;
import org.example.pidev.models.Stock;
import org.example.pidev.services.ProduitService;
import org.example.pidev.services.StockService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ModifierStockController {
    @FXML private ComboBox<Produit> cbProduit;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfDateEntree;
    @FXML private TextField tfDateExpiration;
    @FXML private Label lblInfo;

    private final StockService stockService = new StockService();
    private final ProduitService produitService = new ProduitService();
    private Stock stock;

    public void setStock(Stock stock) {
        this.stock = stock;
        cbProduit.setItems(FXCollections.observableArrayList(produitService.getAll()));
        // Affichage personnalisé dans la ComboBox
        cbProduit.setCellFactory(listView -> new javafx.scene.control.ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " | " + item.getType() + " | " + item.getUnite() + " | " + item.getPrixUnitaire() + " DT");
                }
            }
        });
        cbProduit.setButtonCell(new javafx.scene.control.ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " | " + item.getType() + " | " + item.getUnite() + " | " + item.getPrixUnitaire() + " DT");
                }
            }
        });
        cbProduit.getSelectionModel().select(
            produitService.getAll().stream().filter(p -> p.getIdProduit() == stock.getIdProduit()).findFirst().orElse(null)
        );
        tfQuantite.setText(String.valueOf(stock.getQuantite()));
        tfDateEntree.setText(stock.getDateEntree() != null ? stock.getDateEntree().toString() : "");
        tfDateEntree.setEditable(false);
        tfDateExpiration.setText(stock.getDateExpiration() != null ? stock.getDateExpiration().toString() : "");
    }

    @FXML
    private void update() {
        try {
            Produit produit = cbProduit.getValue();
            double quantite = Double.parseDouble(tfQuantite.getText());
            java.time.LocalDate dateEntree = stock.getDateEntree();
            java.time.LocalDate dateExpiration = tfDateExpiration.getText().isEmpty() ? null : java.time.LocalDate.parse(tfDateExpiration.getText());
            stock.setIdProduit(produit != null ? produit.getIdProduit() : -1);
            stock.setQuantite(quantite);
            stock.setDateExpiration(dateExpiration);
            String erreur = stockService.controleSaisie(stock, produit);
            if (erreur != null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText(erreur);
                alert.showAndWait();
                return;
            }
            stockService.update(stock);
            lblInfo.setText("Stock modifié avec succès !");
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
        javafx.stage.Stage stage = (javafx.stage.Stage) tfQuantite.getScene().getWindow();
        stage.close();
    }
}
