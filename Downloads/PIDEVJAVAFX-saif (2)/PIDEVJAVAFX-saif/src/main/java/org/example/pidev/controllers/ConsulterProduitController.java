// ...existing code...
    // ...existing code...
package org.example.pidev.controllers;

import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ConsulterProduitController {
    @FXML private javafx.scene.control.Label lblTotalProduits;
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colType;
    @FXML private TableColumn<Produit, String> colUnite;
    @FXML private TableColumn<Produit, Double> colPrixUnitaire;

    // Ajout pour la recherche
    @FXML private javafx.scene.control.ComboBox<String> comboCritere;
    @FXML private javafx.scene.control.TextField txtValeurRecherche;

    private final ProduitService produitService = new ProduitService();

    // Navigation vers la vue Stock depuis Produit
    @FXML
    private void navigateToConsulterStock() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/consulterstock.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) tableProduits.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            javafx.scene.Scene newScene = new javafx.scene.Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - Liste du Stock");
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation directe vers l'interface d'ajout de produit
    @FXML
    private void navigateToAjouterProduit() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ajouterproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) tableProduits.getScene().getWindow();
            javafx.scene.Scene newScene = new javafx.scene.Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - Ajouter un Produit");
            stage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ouvre la fenêtre/modale d'ajout de produit
    @FXML
    private void add() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ajouterproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableProduits.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter un Produit");
            popupStage.setScene(new javafx.scene.Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            // Rafraîchir la table et la stat après ajout
            ObservableList<Produit> produits = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
            tableProduits.setItems(produits);
            if (lblTotalProduits != null) {
                lblTotalProduits.setText(String.valueOf(produits.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ouvre la fenêtre/modale de modification de produit
    @FXML
    private void update() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Aucun produit sélectionné pour modification.");
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/modifierproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            ModifierProduitController controller = loader.getController();
            controller.setProduit(selected);
            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableProduits.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier le Produit");
            popupStage.setScene(new javafx.scene.Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            // Rafraîchir la table et la stat après modification
            ObservableList<Produit> produits = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
            tableProduits.setItems(produits);
            if (lblTotalProduits != null) {
                lblTotalProduits.setText(String.valueOf(produits.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Supprime le produit sélectionné
    @FXML
    private void delete() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Aucun produit sélectionné pour suppression.");
            return;
        }
        produitService.delete(selected.getIdProduit());
        ObservableList<Produit> produits = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
        tableProduits.setItems(produits);
        if (lblTotalProduits != null) {
            lblTotalProduits.setText(String.valueOf(produits.size()));
        }
    }

    @FXML
    private void initialize() {
        // ...existing code...
            // ...existing code...
        colId.setCellValueFactory(new PropertyValueFactory<>("idProduit"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAll());
        tableProduits.setItems(produits);
        if (lblTotalProduits != null) {
            lblTotalProduits.setText(String.valueOf(produits.size()));
        }
        // Initialisation des critères de recherche
        if (comboCritere != null) {
            comboCritere.getItems().addAll("ID", "Nom", "Type", "Unité", "Prix unitaire");
            comboCritere.getSelectionModel().selectFirst();
        }
    }

    // Méthode appelée lors du clic sur le bouton "Rechercher"
    @FXML
    private void rechercherProduit() {
        String critere = comboCritere.getValue();
        String valeur = txtValeurRecherche.getText();
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAll());
        if (valeur == null || valeur.isEmpty()) {
            tableProduits.setItems(produits);
            if (lblTotalProduits != null) {
                lblTotalProduits.setText(String.valueOf(produits.size()));
            }
            return;
        }
        ObservableList<Produit> filtered = FXCollections.observableArrayList();
        for (Produit p : produits) {
            switch (critere) {
                case "ID":
                    if (String.valueOf(p.getIdProduit()).equals(valeur)) filtered.add(p);
                    break;
                case "Nom":
                    if (p.getNom() != null && p.getNom().toLowerCase().contains(valeur.toLowerCase())) filtered.add(p);
                    break;
                case "Type":
                    if (p.getType() != null && p.getType().toLowerCase().contains(valeur.toLowerCase())) filtered.add(p);
                    break;
                case "Unité":
                    if (p.getUnite() != null && p.getUnite().toLowerCase().contains(valeur.toLowerCase())) filtered.add(p);
                    break;
                case "Prix unitaire":
                    try {
                        double prix = Double.parseDouble(valeur);
                        if (Double.compare(p.getPrixUnitaire(), prix) == 0) filtered.add(p);
                    } catch (NumberFormatException ignored) {}
                    break;
            }
        }
        tableProduits.setItems(filtered);
        if (lblTotalProduits != null) {
            lblTotalProduits.setText(String.valueOf(filtered.size()));
        }
    }
    // Exporter la TableView en PDF
    @FXML
    private void exporterPDFProduit() {
        try {
            String userHome = System.getProperty("user.home");
            String downloads = userHome + "/Downloads/ProduitTable.pdf";
            String bureau = userHome + "/Desktop/ProduitTable.pdf";
            java.util.List<Produit> produits = produitService.getAll();
            // Utilisation de iText pour générer le PDF
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(downloads));
            document.open();
            document.add(new com.itextpdf.text.Paragraph("Liste des Produits"));
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
            table.addCell("ID");
            table.addCell("Nom");
            table.addCell("Type");
            table.addCell("Unité");
            table.addCell("Prix unitaire");
            for (Produit p : produits) {
                table.addCell(String.valueOf(p.getIdProduit()));
                table.addCell(p.getNom());
                table.addCell(p.getType());
                table.addCell(p.getUnite());
                table.addCell(String.valueOf(p.getPrixUnitaire()));
            }
            document.add(table);
            document.close();
            // Copier aussi sur le bureau
            java.nio.file.Files.copy(java.nio.file.Paths.get(downloads), java.nio.file.Paths.get(bureau), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF des produits a été exporté avec succès vous pouvez le retrouver dans Téléchargements.");
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
