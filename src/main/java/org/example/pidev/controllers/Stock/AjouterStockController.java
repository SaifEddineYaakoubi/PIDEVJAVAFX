package org.example.pidev.controllers.stock;

import org.example.pidev.models.Produit;
import org.example.pidev.models.Stock;
import org.example.pidev.services.produits.ProduitService;
import org.example.pidev.services.stock.StockService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AjouterStockController {
    // Méthodes d'accès pour les tests ou appels externes
    public Produit getProduit() {
        return cbProduit.getValue();
    }

    public double getQuantite() {
        try {
            return Double.parseDouble(tfQuantite.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    public java.time.LocalDate getDateEntree() {
        try {
            return java.time.LocalDate.parse(tfDateEntree.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public java.time.LocalDate getDateExpiration() {
        try {
            return java.time.LocalDate.parse(tfDateExpiration.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public void setProduit(Produit produit) {
        cbProduit.setValue(produit);
    }

    public void setQuantite(double quantite) {
        tfQuantite.setText(String.valueOf(quantite));
    }

    public void setDateExpiration(java.time.LocalDate date) {
        tfDateExpiration.setText(date != null ? date.toString() : "");
    }

    public String getMessage() {
        return lblMessage.getText();
    }

    public void resetForm() {
        cbProduit.getSelectionModel().clearSelection();
        tfQuantite.clear();
        tfDateEntree.setText(java.time.LocalDate.now().toString());
        tfDateExpiration.clear();
        lblMessage.setText("");
    }
    @FXML private ComboBox<Produit> cbProduit;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfDateEntree;
    @FXML private TextField tfDateExpiration;
    @FXML private Label lblMessage;

    private final StockService stockService = new StockService();
    private final ProduitService produitService = new ProduitService();

    @FXML
    private void initialize() {
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
        tfDateEntree.setText(java.time.LocalDate.now().toString());
        tfDateEntree.setEditable(false);
    }

    @FXML
    private void add() {
        try {
            Produit produit = cbProduit.getValue();
            double quantite = Double.parseDouble(tfQuantite.getText());
            java.time.LocalDate dateEntree = java.time.LocalDate.now();
            java.time.LocalDate dateExpiration = tfDateExpiration.getText().isEmpty() ? null : java.time.LocalDate.parse(tfDateExpiration.getText());
            Stock stock = new Stock(quantite, dateEntree, dateExpiration, produit != null ? produit.getIdProduit() : -1);
            String erreur = stockService.controleSaisie(stock, produit);
            if (erreur != null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText(erreur);
                alert.showAndWait();
                return;
            }
            // Vérifier si un stock existe déjà pour ce produit
            Stock stockExistant = null;
            for (Stock s : stockService.getAll()) {
                if (s.getIdProduit() == stock.getIdProduit()) {
                    stockExistant = s;
                    break;
                }
            }
            if (stockExistant != null) {
                // Augmenter la quantité du stock existant
                stockExistant.setQuantite(stockExistant.getQuantite() + quantite);
                stockService.update(stockExistant);
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Stock mis à jour");
                alert.setHeaderText(null);
                alert.setContentText("Le produit existe déjà dans le stock. La quantité a été augmentée de " + quantite + ".");
                alert.showAndWait();
                closeWindow();
                return;
            }
            boolean result = stockService.add(stock);
            if (result) {
                lblMessage.setText("Stock ajouté avec succès !");
                closeWindow();
            } else {
                lblMessage.setText("Erreur lors de l'ajout du stock.");
            }
        } catch (Exception e) {
            lblMessage.setText("Erreur: " + e.getMessage());
        }
    }

    private void closeWindow() {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblMessage.getScene().getWindow();
        stage.close();
    }
}
