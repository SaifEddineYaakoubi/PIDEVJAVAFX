package org.example.pidev.controllers.stock;


import java.util.List;
import java.util.Properties;

import org.example.pidev.models.Produit;
import org.example.pidev.models.Stock;
import org.example.pidev.services.produits.ProduitService;
import org.example.pidev.services.stock.StockService;
import org.example.pidev.services.recoltes.VoiceRSSService;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConsulterStockController {
    @FXML private ListView<String> listStock;
    @FXML private ComboBox<String> cbCritere;
    @FXML private TextField tfRecherche;
    @FXML private Label lblTotalStock;
    @FXML private Button ajouters;
    @FXML private Button ups;
    @FXML private Button deletes;
    @FXML private Button btnExportPDFStock;
    @FXML private Button btnModifierStock;
    @FXML private Button btnSupprimerStock;
    // Ajout du service utilisateur
    private final org.example.pidev.services.utilisateur.UtilisateurService utilisateurService = new org.example.pidev.services.utilisateur.UtilisateurService();

    private final ProduitService produitService = new ProduitService();
    private final StockService stockService = new StockService();

    @FXML
    private void initialize() {
        // Envoi d'une alerte mail si un stock expire dans 5 jours ou moins
        try {
            ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
            java.time.LocalDate now = java.time.LocalDate.now();
            for (Stock stock : stocks) {
                if (stock.getDateExpiration() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(now, stock.getDateExpiration());
                    if (days >= 0 && days <= 5) {
                        Produit produit = produitService.getById(stock.getIdProduit());
                        envoyerAlerteExpirationStock(stock, produit, days);
                        break; // Envoyer un seul mail par ouverture
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Ajout du listener pour détecter le clic sur un stock expiré
        listStock.setOnMouseClicked(event -> {
            int selectedIndex = listStock.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
                Stock selected = stocks.get(selectedIndex);
                if (selected.getDateExpiration() != null) {
                    java.time.LocalDate now = java.time.LocalDate.now();
                    long days = java.time.temporal.ChronoUnit.DAYS.between(now, selected.getDateExpiration());
                    Produit produit = produitService.getById(selected.getIdProduit());
                    String nomProduit = (produit != null && produit.getNom() != null) ? produit.getNom() : "";
                    if (selected.getDateExpiration().isBefore(now)) {
                        String texte = "attention! Le produit " + nomProduit + " dans votre stock est expiré";
                        VoiceRSSService.speak(texte);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Produit Expiré");
                        alert.setHeaderText(null);
                        alert.setContentText("Ce produit est expiré. Il sera supprimé automatiquement dans 30 jours.");
                        alert.showAndWait();
                    } else if (days >= 0 && days <= 5) {
                        String texte = "attention! Le produit " + nomProduit + " dans votre stock va expirer dans " + days + " jours";
                        VoiceRSSService.speak(texte);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Produit bientôt expiré");
                        alert.setHeaderText(null);
                        alert.setContentText("Ce produit va expirer dans " + days + " jours.");
                        alert.showAndWait();
                    }
                }
            }
        });
        // Préparer les données
        ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Stock item : stocks) {
            Produit p = produitService.getById(item.getIdProduit());
            StringBuilder sb = new StringBuilder();
            sb.append(item.getIdStock()).append("|");
            sb.append(p != null ? p.getNom() : "").append("|");
            sb.append(p != null ? p.getType() : "").append("|");
            sb.append(item.getQuantite()).append("|");
            sb.append(p != null ? p.getPrixUnitaire() : "").append("|");
            sb.append(item.getDateEntree() != null ? item.getDateEntree() : "").append("|");
            sb.append(item.getDateExpiration() != null ? item.getDateExpiration() : "");
            displayList.add(sb.toString());
        }
        listStock.setItems(displayList);
        listStock.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    setDisable(false);
                } else {
                    // Décodage de la String sérialisée
                    String[] parts = item.split("\\|");
                    String id = parts.length > 0 ? parts[0] : "";
                    String nom = parts.length > 1 ? parts[1] : "";
                    String type = parts.length > 2 ? parts[2] : "";
                    String quantite = parts.length > 3 ? parts[3] : "";
                    String prix = parts.length > 4 ? parts[4] : "";
                    String entree = parts.length > 5 ? parts[5] : "";
                    String expiration = parts.length > 6 ? parts[6] : "";
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(18);
                    hbox.setStyle("-fx-alignment: center-left;");
                    // Icône marron à gauche selon le type
                    javafx.scene.control.Label icon = new javafx.scene.control.Label();
                    String typeLower = type.toLowerCase();
                    if (typeLower.contains("engrais") || typeLower.contains("fertil")) {
                        icon.setText("🧪"); // sac d'engrais
                    } else {
                        icon.setText("🌱"); // plante par défaut
                    }
                    icon.setStyle("-fx-font-size: 28px; -fx-padding: 0 10 0 0; -fx-text-fill: #6e4b2a;");
                    hbox.getChildren().add(icon);
                    // Attributs alignés sur une seule ligne
                    javafx.scene.layout.HBox attrBox = new javafx.scene.layout.HBox(30);
                    attrBox.setStyle("-fx-alignment: center-left;");
                    javafx.scene.control.Label lblNom = new javafx.scene.control.Label("Nom : " + nom);
                    javafx.scene.control.Label lblType = new javafx.scene.control.Label("Type : " + type);
                    javafx.scene.control.Label lblQuantite = new javafx.scene.control.Label("Quantité : " + quantite);
                    javafx.scene.control.Label lblPrix = new javafx.scene.control.Label("Prix : " + prix);
                    javafx.scene.control.Label lblEntree = new javafx.scene.control.Label("Entrée : " + entree);
                    javafx.scene.control.Label lblExpiration = new javafx.scene.control.Label("Sortie : " + expiration);
                    for (javafx.scene.control.Label lbl : new javafx.scene.control.Label[]{lblNom, lblType, lblQuantite, lblPrix, lblEntree, lblExpiration}) {
                        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6e4b2a; -fx-font-family: 'Segoe UI', 'Arial';");
                        attrBox.getChildren().add(lbl);
                    }
                    hbox.getChildren().add(attrBox);
                    setGraphic(hbox);
                    setText(null);
                    // Gestion de la couleur et de l'état disabled pour expiration proche ou stock faible
                    final boolean isExpired;
                    if (expiration != null && !expiration.isEmpty()) {
                        boolean expired = false;
                        try {
                            java.time.LocalDate expDate = java.time.LocalDate.parse(expiration);
                            if (expDate.isBefore(java.time.LocalDate.now())) {
                                expired = true;
                            }
                        } catch (Exception ignored) {}
                        isExpired = expired;
                    } else {
                        isExpired = false;
                    }
                    final boolean isLowStock;
                    if (quantite != null && !quantite.isEmpty()) {
                        boolean low = false;
                        try {
                            if (Integer.parseInt(quantite) < 5) {
                                low = true;
                            }
                        } catch (Exception ignored) {}
                        isLowStock = low;
                    } else {
                        isLowStock = false;
                    }
                    if (isExpired) {
                        setStyle("-fx-background-color: #cccccc; -fx-background-radius: 15px; -fx-padding: 14 0 14 0; margin: 10px 0; -fx-effect: dropshadow(gaussian, #e4d1b0, 8, 0, 0, 2);");
                        setDisable(false);
                    } else if (isLowStock) {
                        setStyle("-fx-background-color: #ffcccc; -fx-background-radius: 15px; -fx-padding: 14 0 14 0; margin: 10px 0; -fx-effect: dropshadow(gaussian, #e4d1b0, 8, 0, 0, 2);");
                        setDisable(false);
                    } else {
                        setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 14 0 14 0; margin: 10px 0; -fx-effect: dropshadow(gaussian, #e4d1b0, 8, 0, 0, 2);");
                        setDisable(false);
                    }
                    setOnMouseEntered(e -> {
                        if (!isExpired && !isLowStock) setStyle("-fx-background-color: #f7efde; -fx-background-radius: 15px; -fx-padding: 14 0 14 0; margin: 10px 0; -fx-effect: dropshadow(gaussian, #e4d1b0, 8, 0, 0, 2);");
                    });
                    setOnMouseExited(e -> {
                        if (!isExpired && !isLowStock) setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 14 0 14 0; margin: 10px 0; -fx-effect: dropshadow(gaussian, #e4d1b0, 8, 0, 0, 2);");
                    });
                }
            }
        });
        // Statistiques
        if (lblTotalStock != null) {
            lblTotalStock.setText(String.valueOf(stocks.size()));
        }
    }

    private void rafraichirTable() {
        ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
        ObservableList<String> displayList = FXCollections.observableArrayList();
        String header = String.format("%-20s | %-10s | %-8s | %-12s | %-15s | %-15s",
                "Produit", "Type", "Quantité", "Prix", "Entrée", "Sortie");
        displayList.add(header);
        for (Stock item : stocks) {
            Produit p = produitService.getById(item.getIdProduit());
            String row = String.format("%-20s | %-10s | %-8s | %-12s | %-15s | %-15s",
                    (p != null ? p.getNom() : ""),
                    (p != null ? p.getType() : ""),
                    item.getQuantite(),
                    (p != null ? p.getPrixUnitaire() : ""),
                    (item.getDateEntree() != null ? item.getDateEntree() : ""),
                    (item.getDateExpiration() != null ? item.getDateExpiration() : ""));
            displayList.add(row);
        }
        listStock.setItems(displayList);
        if (lblTotalStock != null) {
            lblTotalStock.setText(String.valueOf(stocks.size()));
        }
    }

    // Envoi mail d'alerte stock faible
    private void envoyerAlerteStockFaible(Stock stock, Produit produit) {
        final String username = "maramabdeladhim2022@gmail.com";
        final String password = "beqx iwlz mhnc bjqs"; // Remplacez par le mot de passe ou app password
        String to = getMailUtilisateur22();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Alerte : Stock Faible");
            String nomProduit = (produit != null ? produit.getNom() : "Produit inconnu");
            String body = "Bonjour,\n\n" +
                    "Nous vous informons que le stock du produit : " + nomProduit +
                    " (ID : " + stock.getIdStock() + ") est actuellement faible.\n" +
                    "Quantité restante : " + stock.getQuantite() + " unité(s).\n" +
                    "Nous vous recommandons de réapprovisionner ce produit afin d'éviter toute rupture de stock.\n\n" +
                    "Cordialement,\nL'équipe Smart Farm.";
            message.setText(body);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'envoi mail", "Impossible d'envoyer l'alerte stock faible : " + e.getMessage());
        }
    }

    // Envoi mail d'alerte expiration proche
    private void envoyerAlerteExpirationStock(Stock stock, Produit produit, long days) {
        final String username = "maramabdeladhim2022@gmail.com";
        final String password = "beqx iwlz mhnc bjqs"; // Remplacez par le mot de passe ou app password
        String to = getMailUtilisateur22();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Alerte : Stock proche expiration");
            String nomProduit = (produit != null ? produit.getNom() : "Produit inconnu");
            String body = "Bonjour,\n\n" +
                    "Nous vous informons que le stock du produit : " + nomProduit +
                    " (ID : " + stock.getIdStock() + ") arrive à expiration dans " + days + " jour(s).\n" +
                    "Date d'expiration prévue : " + stock.getDateExpiration() + ".\n" +
                    "Merci de prendre les mesures nécessaires pour éviter toute perte ou rupture de stock.\n\n" +
                    "Cordialement,\nL'équipe Smart Farm.";
            message.setText(body);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'envoi mail", "Impossible d'envoyer l'alerte expiration stock : " + e.getMessage());
        }
    }

    // Récupère le mail de l'utilisateur d'id 22
    private String getMailUtilisateur22() {
        org.example.pidev.models.Utilisateur u = utilisateurService.getById(29);
        if (u != null && u.getEmail() != null) {
            return u.getEmail();
        } else {
            return "";
        }
    }



    // Navigation vers la vue Produit
    @FXML
    private void getAll() {
        navigateToConsulterProduit();
    }

    // Navigation vers la vue Produit depuis Stock
    @FXML
    private void navigateToConsulterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/produits/consulterproduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listStock.getScene().getWindow();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - Liste des Produits");
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur de navigation", "Impossible de charger la vue des produits.");
        }
    }

    // Exporter la ListView en PDF
    @FXML
    private void exporterPDFStock() {
        try {
            String userHome = System.getProperty("user.home");
            String downloads = userHome + "/Downloads/StockTable.pdf";
            String bureau = userHome + "/Desktop/StockTable.pdf";
            List<Stock> stocks = stockService.getAll();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(downloads));
            document.open();
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(6);
            table.setWidthPercentage(100);
            String[] headers = {"Nom", "Type", "Quantité", "Prix", "Entrée", "Sortie"};
            for (String h : headers) {
                table.addCell(h);
            }
            for (Stock s : stocks) {
                Produit p = produitService.getById(s.getIdProduit());
                table.addCell(p != null ? p.getNom() : "");
                table.addCell(p != null ? p.getType() : "");
                table.addCell(String.valueOf(s.getQuantite()));
                table.addCell(p != null ? String.valueOf(p.getPrixUnitaire()) : "");
                table.addCell(s.getDateEntree() != null ? s.getDateEntree().toString() : "");
                table.addCell(s.getDateExpiration() != null ? s.getDateExpiration().toString() : "");
            }
            table.setSpacingBefore(20);
            document.add(table);
            document.close();
            java.nio.file.Files.copy(java.nio.file.Paths.get(downloads), java.nio.file.Paths.get(bureau), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF du stock a été exporté avec succès.\nVous pouvez le retrouver dans Téléchargements et sur le Bureau.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'export", "Impossible d'exporter le PDF : " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void ajouterStock() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock/ajouterstock.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listStock.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter un Stock");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            rafraichirTable();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ajout", "Impossible d'ouvrir la fenêtre d'ajout de stock : " + e.getMessage());
        }
    }

    @FXML
    private void rechercherStock() {
        String critere = cbCritere != null ? cbCritere.getValue() : null;
        String valeur = tfRecherche != null ? tfRecherche.getText().trim() : null;
        ObservableList<Stock> stocks = FXCollections.observableArrayList();
        List<Stock> allStocks = stockService.getAll();
        boolean globalSearch = (critere == null || critere.isEmpty());
        boolean emptyValue = (valeur == null || valeur.isEmpty());
        if (globalSearch || emptyValue) {
            stocks.addAll(allStocks);
        } else {
            for (Stock s : allStocks) {
                Produit p = produitService.getById(s.getIdProduit());
                String searchVal = valeur.toLowerCase();
                switch (critere) {
                    case "Nom du produit":
                        if (p != null && p.getNom() != null && p.getNom().toLowerCase().contains(searchVal)) stocks.add(s);
                        break;
                    case "Type":
                        if (p != null && p.getType() != null && p.getType().toLowerCase().contains(searchVal)) stocks.add(s);
                        break;
                    case "Quantité":
                        if (String.valueOf(s.getQuantite()).contains(valeur)) stocks.add(s);
                        break;
                    case "Prix":
                        if (p != null && String.valueOf(p.getPrixUnitaire()).contains(valeur)) stocks.add(s);
                        break;
                    case "Date d'entrée":
                        if (s.getDateEntree() != null && s.getDateEntree().toString().contains(valeur)) stocks.add(s);
                        break;
                    case "Date de sortie":
                        if (s.getDateExpiration() != null && s.getDateExpiration().toString().contains(valeur)) stocks.add(s);
                        break;
                    default:
                        if ((p != null && p.getNom() != null && p.getNom().toLowerCase().contains(searchVal))
                                || (p != null && p.getType() != null && p.getType().toLowerCase().contains(searchVal))
                                || String.valueOf(s.getQuantite()).contains(valeur)
                                || (p != null && String.valueOf(p.getPrixUnitaire()).contains(valeur))
                                || (s.getDateEntree() != null && s.getDateEntree().toString().contains(valeur))
                                || (s.getDateExpiration() != null && s.getDateExpiration().toString().contains(valeur))) {
                            stocks.add(s);
                        }
                        break;
                }
            }
        }
        // Build display list for filtered stocks
        ObservableList<String> displayList = FXCollections.observableArrayList();
        String header = String.format("%-20s | %-10s | %-8s | %-12s | %-15s | %-15s",
                "Produit", "Type", "Quantité", "Prix", "Entrée", "Sortie");
        displayList.add(header);
        for (Stock item : stocks) {
            Produit p = produitService.getById(item.getIdProduit());
            String row = String.format("%-20s | %-10s | %-8s | %-12s | %-15s | %-15s",
                    (p != null ? p.getNom() : ""),
                    (p != null ? p.getType() : ""),
                    item.getQuantite(),
                    (p != null ? p.getPrixUnitaire() : ""),
                    (item.getDateEntree() != null ? item.getDateEntree() : ""),
                    (item.getDateExpiration() != null ? item.getDateExpiration() : ""));
            displayList.add(row);
        }
        listStock.setItems(displayList);
    }

    @FXML
    private void modifierStock() {
        int selectedIndex = listStock.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showErrorAlert("Modification", "Veuillez sélectionner un stock à modifier.");
            return;
        }
        ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
        Stock selected = stocks.get(selectedIndex);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock/modifierstock.fxml"));
            Parent root = loader.load();
            // Pass the selected stock to the modifier controller
            ModifierStockController controller = loader.getController();
            controller.setStock(selected);
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listStock.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier un Stock");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
            rafraichirTable();
            // Send alert if stock is low after modification
            Stock updatedStock = stockService.getById(selected.getIdStock());
            Produit updatedProduit = produitService.getById(updatedStock.getIdProduit());
            if (updatedStock.getQuantite() < 5) {
                envoyerAlerteStockFaible(updatedStock, updatedProduit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur de modification", "Impossible d'ouvrir la fenêtre de modification de stock : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerStock() {
        int selectedIndex = listStock.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showErrorAlert("Suppression", "Veuillez sélectionner un stock à supprimer.");
            return;
        }
        ObservableList<Stock> stocks = FXCollections.observableArrayList(stockService.getAll());
        Stock selected = stocks.get(selectedIndex);
        try {
            stockService.delete(selected.getIdStock());
            rafraichirTable();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Suppression Stock");
            alert.setHeaderText(null);
            alert.setContentText("Stock supprimé avec succès.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur de suppression", "Impossible de supprimer le stock : " + e.getMessage());
        }
    }

    // ==================== CHATBOT ====================

    @FXML
    private void handleChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/chatbot_view.fxml"));
            Parent chatRoot = loader.load();

            Stage stage = (Stage) listStock.getScene().getWindow();
            Scene scene = new Scene(chatRoot);

            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Farm - Assistant Agricole");
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture chatbot: " + e.getMessage());
        }
    }

    // ==================== PROFIL & DÉCONNEXION ====================

    @FXML
    private void handleProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/profile_view.fxml"));
            Parent profileRoot = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof org.example.pidev.controllers.utilisateur.ProfileController) {
                org.example.pidev.controllers.utilisateur.ProfileController profileCtrl =
                        (org.example.pidev.controllers.utilisateur.ProfileController) ctrl;
                if (org.example.pidev.utils.Session.getCurrentUser() != null) {
                    profileCtrl.setUser(org.example.pidev.utils.Session.getCurrentUser());
                }
            }

            Stage profileStage = new Stage();
            profileStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            profileStage.initOwner(listStock.getScene().getWindow());
            profileStage.setTitle("Smart Farm - Mon Profil");
            Scene scene = new Scene(profileRoot);
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());
            profileStage.setScene(scene);
            profileStage.setResizable(false);
            profileStage.centerOnScreen();
            profileStage.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture profil: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeconnexion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");
        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                org.example.pidev.utils.Session.clear();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/LoginView.fxml"));
                Parent loginRoot = loader.load();

                Stage stage = (Stage) listStock.getScene().getWindow();
                stage.setMaximized(false);

                Scene loginScene = new Scene(loginRoot, 600, 500);
                var css1 = getClass().getResource("/styles/smartfarm.css");
                if (css1 != null) loginScene.getStylesheets().add(css1.toExternalForm());
                var css2 = getClass().getResource("/recoltes/smartfarmm.css");
                if (css2 != null) loginScene.getStylesheets().add(css2.toExternalForm());

                stage.setScene(loginScene);
                stage.setTitle("Smart Farm - Connexion");
                stage.setResizable(false);
                stage.setWidth(600);
                stage.setHeight(500);
                stage.centerOnScreen();
                stage.setOnCloseRequest(evt -> {
                    javafx.application.Platform.exit();
                    System.exit(0);
                });
            } catch (Exception e) {
                System.err.println("❌ Erreur déconnexion: " + e.getMessage());
            }
        }
    }

}