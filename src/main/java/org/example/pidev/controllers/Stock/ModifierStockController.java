package org.example.pidev.controllers.Stock;

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

    private final ProduitService produitService = new ProduitService();
    private final StockService stockService = new StockService();

    private Stock stock;

    @FXML
    public void initialize() {
        cbProduit.setItems(
                FXCollections.observableArrayList(produitService.getAll())
        );
        // Custom cell factory for better product display
        cbProduit.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Format columns for alignment
                    String formatted = String.format("%-12s | %-10s | %-6s | %8.2f DT",
                            item.getNom(), item.getType(), item.getUnite(), item.getPrixUnitaire());
                    setText(formatted);
                    setStyle("-fx-font-family: 'Consolas', 'monospace';");
                }
            }
        });
        cbProduit.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String formatted = String.format("%-18s | %-10s | %-6s | %8.2f DT",
                            item.getNom(), item.getType(), item.getUnite(), item.getPrixUnitaire());
                    setText(formatted);
                    setStyle("-fx-font-family: 'Consolas', 'monospace';");
                }
            }
        });
    }

    // 🔴 Cette méthode remplit automatiquement les champs
    public void setStock(Stock stock) {

        this.stock = stock;

        tfQuantite.setText(String.valueOf(stock.getQuantite()));

        if (stock.getDateEntree() != null)
            tfDateEntree.setText(stock.getDateEntree().toString());

        if (stock.getDateExpiration() != null)
            tfDateExpiration.setText(stock.getDateExpiration().toString());

        // Sélectionner le bon produit
        for (Produit p : cbProduit.getItems()) {
            if (p.getIdProduit() == stock.getIdProduit()) {
                cbProduit.getSelectionModel().select(p);
                break;
            }
        }

        tfDateEntree.setEditable(false);
    }

    @FXML
    private void update() {
        try {
            stock.setQuantite(Double.parseDouble(tfQuantite.getText()));
            stock.setIdProduit(cbProduit.getValue().getIdProduit());

            if (!tfDateExpiration.getText().isEmpty()) {
                stock.setDateExpiration(
                        java.time.LocalDate.parse(tfDateExpiration.getText())
                );
            }

            stockService.update(stock);

            lblInfo.setText("Stock modifié avec succès !");
            closeWindow();

        } catch (Exception e) {
            lblInfo.setText("Erreur: " + e.getMessage());
        }
    }

    private void closeWindow() {
        javafx.stage.Stage stage =
                (javafx.stage.Stage) tfQuantite.getScene().getWindow();
        stage.close();
    }
}
