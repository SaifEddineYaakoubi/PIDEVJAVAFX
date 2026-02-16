package org.example.pidev.controllers;

import org.example.pidev.models.Produit;
import org.example.pidev.models.Stock;
import org.example.pidev.services.ProduitService;
import org.example.pidev.services.StockService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
// ...existing code...

public class ConsulterStockController {
                @FXML private javafx.scene.control.ComboBox<String> cbCritere;
                @FXML private javafx.scene.control.TextField tfRecherche;
            @FXML private javafx.scene.control.Label lblTotalStock;
        @FXML private TableColumn<Stock, String> colDateEntree;
        @FXML private TableColumn<Stock, String> colDateExpiration;
    @FXML private TableView<Stock> tableStock;
    // ...existing code...
    @FXML private TableColumn<Stock, Integer> colId;
    @FXML private TableColumn<Stock, String> colNomProduit;
    @FXML private TableColumn<Stock, String> colTypeProduit;
    @FXML private TableColumn<Stock, Double> colQuantite;
    @FXML private TableColumn<Stock, String> colPrixUnitaire;

    private final StockService stockService = new StockService();
    private final ProduitService produitService = new ProduitService();

    // Navigation vers la vue Produit
    @FXML
    private void getAll() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/consulterproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) tableStock.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            javafx.scene.Scene newScene = new javafx.scene.Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - Liste des Produits");
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation vers la vue Produit depuis Stock
    @FXML
    private void navigateToConsulterProduit() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/consulterproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) tableStock.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            javafx.scene.Scene newScene = new javafx.scene.Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - Liste des Produits");
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
                        // Initialisation des critères de recherche
                        if (cbCritere != null) {
                            cbCritere.setItems(FXCollections.observableArrayList(
                                "ID", "Nom du produit", "Type", "Date d'entrée", "Date de sortie"
                            ));
                            cbCritere.getSelectionModel().selectFirst();
                        }


                colDateEntree.setCellValueFactory(cellData -> {
                    java.time.LocalDate date = cellData.getValue().getDateEntree();
                    return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
                });

    // ...existing code...
                colDateExpiration.setCellValueFactory(cellData -> {
                    java.time.LocalDate date = cellData.getValue().getDateExpiration();
                    return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
                });
        colId.setCellValueFactory(new PropertyValueFactory<>("idStock"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colNomProduit.setCellValueFactory(cellData -> {
            int idProduit = cellData.getValue().getIdProduit();
            Produit produit = produitService.getById(idProduit);
            return new javafx.beans.property.SimpleStringProperty(produit != null ? produit.getNom() : "");
        });
        colTypeProduit.setCellValueFactory(cellData -> {
            int idProduit = cellData.getValue().getIdProduit();
            Produit produit = produitService.getById(idProduit);
            return new javafx.beans.property.SimpleStringProperty(produit != null ? produit.getType() : "");
        });
        colPrixUnitaire.setCellValueFactory(cellData -> {
            int idProduit = cellData.getValue().getIdProduit();
            Produit produit = produitService.getById(idProduit);
            if (produit != null) {
                String prix = String.format("%.2f/%s", produit.getPrixUnitaire(), produit.getUnite());
                return new javafx.beans.property.SimpleStringProperty(prix);
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        rafraichirTable();

                // Statistiques
                if (lblTotalStock != null) {
                    lblTotalStock.setText(String.valueOf(stockService.getAll().size()));
                }
    }

    private void rafraichirTable() {
        ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
        tableStock.setItems(stocks);
        // Mettre à jour le total stock à chaque rafraîchissement
        if (lblTotalStock != null) {
            lblTotalStock.setText(String.valueOf(stocks.size()));
        }
    }

    @FXML
    private void rechercherStock() {
        String critere = cbCritere != null ? cbCritere.getValue() : null;
        String valeur = tfRecherche != null ? tfRecherche.getText().trim() : null;
        ObservableList<Stock> stocks = FXCollections.observableArrayList();
        if (critere == null || valeur == null || valeur.isEmpty()) {
            // Si rien saisi, afficher tout
            stocks.addAll(stockService.getAll());
        } else {
            for (Stock s : stockService.getAll()) {
                Produit p = produitService.getById(s.getIdProduit());
                switch (critere) {
                    case "ID":
                        if (String.valueOf(s.getIdStock()).equals(valeur)) stocks.add(s);
                        break;
                    case "Nom du produit":
                        if (p != null && p.getNom() != null && p.getNom().toLowerCase().contains(valeur.toLowerCase())) stocks.add(s);
                        break;
                    case "Type":
                        if (p != null && p.getType() != null && p.getType().toLowerCase().contains(valeur.toLowerCase())) stocks.add(s);
                        break;
                    case "Date d'entrée":
                        if (s.getDateEntree() != null && s.getDateEntree().toString().contains(valeur)) stocks.add(s);
                        break;
                    case "Date de sortie":
                        if (s.getDateExpiration() != null && s.getDateExpiration().toString().contains(valeur)) stocks.add(s);
                        break;
                }
            }
        }
        tableStock.setItems(stocks);
    }

    
    @FXML
    public void ajouterStock() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ajouterstock.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableStock.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter un Stock");
            popupStage.setScene(new javafx.scene.Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            rafraichirTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierStock() {
        Stock selected = tableStock.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Aucun stock sélectionné pour modification.");
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/modifierstock.fxml"));
            javafx.scene.Parent root = loader.load();
            ModifierStockController controller = loader.getController();
            controller.setStock(selected);
            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableStock.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier le Stock");
            popupStage.setScene(new javafx.scene.Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            rafraichirTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerStock() {
        Stock selected = tableStock.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Aucun stock sélectionné pour suppression.");
            return;
        }
        stockService.delete(selected.getIdStock());
        rafraichirTable();
    }
    // Exporter la TableView en PDF
    @FXML
    private void exporterPDFStock() {
        try {
            String userHome = System.getProperty("user.home");
            String downloads = userHome + "/Downloads/StockTable.pdf";
            String bureau = userHome + "/Desktop/StockTable.pdf";
            java.util.List<Stock> stocks = stockService.getAll();
            // Utilisation de iText pour générer le PDF
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(downloads));
            document.open();
            document.add(new com.itextpdf.text.Paragraph("Liste du Stock"));
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(7);
            table.addCell("ID");
            table.addCell("Nom du produit");
            table.addCell("Type");
            table.addCell("Quantité");
            table.addCell("Prix unitaire");
            table.addCell("Date d'entrée");
            table.addCell("Date de sortie");
            for (Stock s : stocks) {
                Produit p = produitService.getById(s.getIdProduit());
                table.addCell(String.valueOf(s.getIdStock()));
                table.addCell(p != null ? p.getNom() : "");
                table.addCell(p != null ? p.getType() : "");
                table.addCell(String.valueOf(s.getQuantite()));
                table.addCell(p != null ? String.valueOf(p.getPrixUnitaire()) : "");
                table.addCell(s.getDateEntree() != null ? s.getDateEntree().toString() : "");
                table.addCell(s.getDateExpiration() != null ? s.getDateExpiration().toString() : "");
            }
            document.add(table);
            document.close();
            // Copier aussi sur le bureau
            java.nio.file.Files.copy(java.nio.file.Paths.get(downloads), java.nio.file.Paths.get(bureau), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF du stock a été exporté avec succès Vous pouvez le retrouver dans Téléchargements.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'export PDF : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
