

package org.example.pidev.controllers.Produits;


import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ConsulterProduitController {
    @FXML private javafx.scene.control.ListView<String> listProduits;
    @FXML private javafx.scene.image.ImageView imageProduit;
    @FXML private javafx.scene.control.Label lblTotalProduits;
    @FXML private javafx.scene.control.ComboBox<String> comboCritere;
    @FXML private javafx.scene.control.TextField txtValeurRecherche;
    @FXML private javafx.scene.control.Label lblNbEngrais;
    @FXML private javafx.scene.control.Label lblNbFertilisant;
    @FXML private javafx.scene.control.Label lblNbSemence;
    @FXML private javafx.scene.control.Label lblNbAutre;
    // StackPane pour barre dynamique
    @FXML private javafx.scene.layout.StackPane barEngrais;
    @FXML private javafx.scene.layout.StackPane barFertilisant;
    @FXML private javafx.scene.layout.StackPane barSemence;
    @FXML private javafx.scene.layout.StackPane barAutre;
    @FXML private javafx.scene.control.Label lblPrixIndicatif;
    private final ProduitService produitService = new ProduitService();
    private final org.example.pidev.services.ImageProduitService imageProduitService = new org.example.pidev.services.ImageProduitService();
    private final org.example.pidev.services.USDAFoodService usdaFoodService = new org.example.pidev.services.USDAFoodService("hDaCa3Tq2g75SW0EQAVaAulI7LvHWxmluCCOfAre");

    @FXML
    private void initialize() {
        rafraichirTable();
        statistiqueProduitParType();
        // Style modern pour ListView
        if (listProduits != null) {
            listProduits.getStyleClass().add("modern-listview");
        }
        // Style modern pour ComboBox
        if (comboCritere != null) {
            comboCritere.getStyleClass().add("combo-box-modern");
            comboCritere.getItems().clear();
            comboCritere.getItems().addAll("Nom", "Type", "Unité", "Prix unitaire");
            comboCritere.getSelectionModel().selectFirst();
        }
        // Style modern pour TextField
        if (txtValeurRecherche != null) {
            txtValeurRecherche.getStyleClass().add("modern-textfield");
        }
        // Style modern pour Label indicatif (soft card)
        if (lblPrixIndicatif != null) {
            lblPrixIndicatif.getStyleClass().add("soft-card");
        }
        // Style modern pour les compteurs
        if (lblNbEngrais != null) lblNbEngrais.getStyleClass().add("modern-label-card");
        if (lblNbFertilisant != null) lblNbFertilisant.getStyleClass().add("modern-label-card");
        if (lblNbSemence != null) lblNbSemence.getStyleClass().add("modern-label-card");
        if (lblNbAutre != null) lblNbAutre.getStyleClass().add("modern-label-card");
        // Ajout du listener pour afficher l'image à droite et les nutriments
        listProduits.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            if (idx >= 0) {
                ObservableList<Produit> produits2 = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
                Produit selected = produits2.get(idx);
                javafx.scene.image.Image img = imageProduitService.fetchImage(selected.getNom());
                if (img != null) {
                    imageProduit.setImage(img);
                } else {
                    imageProduit.setImage(null);
                }
                // USDA API call and display
                if (lblPrixIndicatif != null) {
                    new Thread(() -> {
                        org.json.JSONObject foodJson = usdaFoodService.fetchFoodByName(selected.getNom());
                        javafx.application.Platform.runLater(() -> {
                            if (foodJson.has("error")) {
                                lblPrixIndicatif.setText("Nutriments: " + foodJson.getString("error"));
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Nutriments pour ").append(foodJson.optString("description", selected.getNom())).append(":\n");
                                org.json.JSONArray nutrients = foodJson.optJSONArray("nutrients");
                                if (nutrients != null && nutrients.length() > 0) {
                                    for (int i = 0; i < nutrients.length(); i++) {
                                        org.json.JSONObject n = nutrients.getJSONObject(i);
                                        sb.append("- ").append(n.optString("name"))
                                                .append(": ")
                                                .append(n.optDouble("value", 0))
                                                .append(" ")
                                                .append(n.optString("unit"))
                                                .append("\n");
                                    }
                                } else {
                                    sb.append("Aucun nutriment trouvé.");
                                }
                                lblPrixIndicatif.setText(sb.toString());
                            }
                        });
                    }).start();
                }
            } else {
                imageProduit.setImage(null);
                if (lblPrixIndicatif != null) {
                    lblPrixIndicatif.setText("");
                }
            }
        });
    }
    // Navigation vers l'interface Consulter Stock
    @FXML
    private void navigateToConsulterStock() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/consulterstock.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) listProduits.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Smart Farm - Gestion des Stocks");
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Exporte la liste des produits en PDF dans Téléchargements
    // Statistiques par type de produit (Légume, Fruit, Alimentation, Autre)
    private void statistiqueProduitParType() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAll());
        int nbEngrais = 0, nbFertilisant = 0, nbSemence = 0, nbAutre = 0;
        for (Produit p : produits) {
            String type = p.getType() != null ? p.getType().toLowerCase() : "";
            if (type.contains("legume")) nbEngrais++;
            else if (type.contains("fruit")) nbFertilisant++;
            else if (type.contains("alimentation")) nbSemence++;
            else nbAutre++;
        }
        int max = Math.max(1, Math.max(Math.max(nbEngrais, nbFertilisant), Math.max(nbSemence, nbAutre)));
        double maxWidth = 100.0; // Largeur max relative (en px, à ajuster selon la largeur du panneau)
        double minWidth = 18.0; // Largeur minimale pour visibilité
        if (lblNbEngrais != null) lblNbEngrais.setText(String.valueOf(nbEngrais));
        if (lblNbFertilisant != null) lblNbFertilisant.setText(String.valueOf(nbFertilisant));
        if (lblNbSemence != null) lblNbSemence.setText(String.valueOf(nbSemence));
        if (lblNbAutre != null) lblNbAutre.setText(String.valueOf(nbAutre));
        // Largeur dynamique limitée (jamais 100% du panneau)
        if (barEngrais != null) barEngrais.setPrefWidth(nbEngrais == 0 ? minWidth : (minWidth + (maxWidth-minWidth) * nbEngrais / max));
        if (barFertilisant != null) barFertilisant.setPrefWidth(nbFertilisant == 0 ? minWidth : (minWidth + (maxWidth-minWidth) * nbFertilisant / max));
        if (barSemence != null) barSemence.setPrefWidth(nbSemence == 0 ? minWidth : (minWidth + (maxWidth-minWidth) * nbSemence / max));
        if (barAutre != null) barAutre.setPrefWidth(nbAutre == 0 ? minWidth : (minWidth + (maxWidth-minWidth) * nbAutre / max));
    }

    // Ajouter un produit : ouvre le formulaire d'ajout dans une nouvelle fenêtre
    @FXML
    private void ajouterProduit() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ajouterproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
            rafraichirTable(); // Rafraîchir la liste après ajout
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void exporterPDFProduit() {
        try {
            String userHome = System.getProperty("user.home");
            String downloads = userHome + "/Downloads/Produits.pdf";
            java.util.List<Produit> produits = produitService.getAll();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(downloads));
            document.open();

            // Titre principal
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(56, 161, 105));
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Liste des Produits", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table des produits
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            String[] headers = {"Nom", "Type", "Unité", "Prix Unitaire"};
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 13, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(110, 75, 42));
            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, headerFont));
                cell.setBackgroundColor(new com.itextpdf.text.BaseColor(247, 239, 222));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }
            com.itextpdf.text.Font rowFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
            for (Produit p : produits) {
                table.addCell(new com.itextpdf.text.Phrase(p.getNom(), rowFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getType(), rowFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getUnite(), rowFont));
                table.addCell(new com.itextpdf.text.Phrase(String.valueOf(p.getPrixUnitaire()), rowFont));
            }
            document.add(table);
            document.close();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF des produits a été enregistré dans Téléchargements.");
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


    // Modifier un produit : ouvre le formulaire de modification pré-rempli
    @FXML
    private void modifierProduit() {
        int selectedIndex = listProduits.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit à modifier.");
            alert.showAndWait();
            return;
        }
        try {
            ObservableList<Produit> produits = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
            Produit selected = produits.get(selectedIndex);
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/modifierproduit.fxml"));
            javafx.scene.Parent root = loader.load();
            // Passer le produit sélectionné au contrôleur de modification
            org.example.pidev.controllers.Produits.ModifierProduitController controller = loader.getController();
            controller.setProduit(selected);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
            rafraichirTable(); // Rafraîchir la liste après modification
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Supprime le produit sélectionné
    @FXML
    private void supprimerProduit() {
        int selectedIndex = listProduits.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit à supprimer.");
            alert.showAndWait();
            return;
        }
        ObservableList<Produit> produits = javafx.collections.FXCollections.observableArrayList(produitService.getAll());
        Produit selected = produits.get(selectedIndex);
        // Confirmation
        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le produit");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer le produit \"" + selected.getNom() + "\" ?\n\nCette action est irréversible.");
        java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                boolean success = produitService.delete(selected.getIdProduit());
                if (success) {
                    rafraichirTable();
                    javafx.scene.control.Alert info = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    info.setTitle("Suppression Produit");
                    info.setHeaderText(null);
                    info.setContentText("Produit supprimé avec succès.");
                    info.showAndWait();
                } else {
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    error.setTitle("Erreur de suppression");
                    error.setHeaderText(null);
                    error.setContentText("Impossible de supprimer le produit.");
                    error.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                error.setTitle("Erreur de suppression");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la suppression : " + e.getMessage());
                error.showAndWait();
            }
        }
    }

    // Removed all OpenFoodFactsBox and related label fields, as well as btnExportPDFProduit, since they are not used in the controller logic.

    private void rafraichirTable() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAll());
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Produit p : produits) {
            StringBuilder sb = new StringBuilder();
            sb.append(p.getType() == null ? "" : p.getType().replace("|", "/")).append("|");
            sb.append(p.getIdProduit()).append("|");
            sb.append(p.getNom() == null ? "" : p.getNom().replace("|", "/")).append("|");
            sb.append(p.getType() == null ? "" : p.getType().replace("|", "/")).append("|");
            sb.append(p.getUnite() == null ? "" : p.getUnite().replace("|", "/")).append("|");
            sb.append(p.getPrixUnitaire());
            displayList.add(sb.toString());
        }
        listProduits.setItems(displayList);
        listProduits.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] parts = item.split("\\|");
                    String type = parts.length > 0 ? parts[0] : "";
                    String id = parts.length > 1 ? parts[1] : "";
                    String nom = parts.length > 2 ? parts[2] : "";
                    String type2 = parts.length > 3 ? parts[3] : "";
                    String unite = parts.length > 4 ? parts[4] : "";
                    String prix = parts.length > 5 ? parts[5] : "";
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(18);
                    hbox.setStyle("-fx-alignment: center-left;");
                    javafx.scene.control.Label icon = new javafx.scene.control.Label();
                    String typeLower = (type + type2).toLowerCase();
                    icon.setText(typeLower.contains("engrais") || typeLower.contains("fertil") ? "🧪" : "🌱");
                    icon.setStyle("-fx-font-size: 28px; -fx-padding: 0 10 0 0; -fx-text-fill: #6e4b2a;");
                    hbox.getChildren().add(icon);
                    javafx.scene.layout.HBox attrBox = new javafx.scene.layout.HBox(30);
                    attrBox.setStyle("-fx-alignment: center-left;");
                    javafx.scene.control.Label lblNom = new javafx.scene.control.Label("Nom : " + nom);
                    javafx.scene.control.Label lblType = new javafx.scene.control.Label("Type : " + type2);
                    javafx.scene.control.Label lblUnite = new javafx.scene.control.Label("Unité : " + unite);
                    javafx.scene.control.Label lblPrix = new javafx.scene.control.Label("Prix Unitaire : " + prix);
                    for (javafx.scene.control.Label lbl : new javafx.scene.control.Label[]{lblNom, lblType, lblUnite, lblPrix}) {
                        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6e4b2a; -fx-font-family: 'Segoe UI', 'Arial';");
                        attrBox.getChildren().add(lbl);
                    }
                    hbox.getChildren().add(attrBox);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
        if (lblTotalProduits != null) {
            lblTotalProduits.setText(String.valueOf(produits.size()));
        }
    }

    // Méthode appelée lors du clic sur le bouton "Rechercher"
    @FXML
    private void rechercherProduit() {
        String critere = comboCritere != null ? comboCritere.getValue() : null;
        String valeur = txtValeurRecherche != null ? txtValeurRecherche.getText() : null;
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAll());
        ObservableList<Produit> filtered = FXCollections.observableArrayList();

        if (valeur == null || valeur.trim().isEmpty() || critere == null || critere.trim().isEmpty()) {
            filtered.addAll(produits);
        } else {
            String valeurLower = valeur.toLowerCase();
            for (Produit p : produits) {
                switch (critere) {
                    case "Nom":
                        if (p.getNom() != null && p.getNom().toLowerCase().contains(valeurLower)) filtered.add(p);
                        break;
                    case "Type":
                        if (p.getType() != null && p.getType().toLowerCase().contains(valeurLower)) filtered.add(p);
                        break;
                    case "Unité":
                        if (p.getUnite() != null && p.getUnite().toLowerCase().contains(valeurLower)) filtered.add(p);
                        break;
                    case "Prix unitaire":
                        try {
                            double prix = Double.parseDouble(valeur);
                            if (Double.compare(p.getPrixUnitaire(), prix) == 0) filtered.add(p);
                        } catch (NumberFormatException ignored) {}
                        break;
                    default:
                        // Recherche globale sur tous les champs
                        if ((p.getNom() != null && p.getNom().toLowerCase().contains(valeurLower)) ||
                                (p.getType() != null && p.getType().toLowerCase().contains(valeurLower)) ||
                                (p.getUnite() != null && p.getUnite().toLowerCase().contains(valeurLower)) ||
                                (String.valueOf(p.getPrixUnitaire()).equals(valeur))) {
                            filtered.add(p);
                        }
                        break;
                }
            }
        }
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Produit p : filtered) {
            // Encodage sous forme de String sérialisée (type|id|nom|type|unite|prix)
            StringBuilder sb = new StringBuilder();
            sb.append(p.getType() == null ? "" : p.getType().replace("|", "/")).append("|");
            sb.append(p.getIdProduit()).append("|");
            sb.append(p.getNom() == null ? "" : p.getNom().replace("|", "/")).append("|");
            sb.append(p.getType() == null ? "" : p.getType().replace("|", "/")).append("|");
            sb.append(p.getUnite() == null ? "" : p.getUnite().replace("|", "/")).append("|");
            sb.append(p.getPrixUnitaire());
            displayList.add(sb.toString());
        }
        listProduits.setItems(displayList);
        // (Removed duplicate and broken cell factory logic)
        if (lblTotalProduits != null) {
            lblTotalProduits.setText(String.valueOf(filtered.size()));
        }
    }

    // ==================== CHATBOT ====================

    @FXML
    private void handleChatbot() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/chatbot_view.fxml"));
            javafx.scene.Parent chatRoot = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) listProduits.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(chatRoot);

            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/smartfarmm.css");
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
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/profile_view.fxml"));
            javafx.scene.Parent profileRoot = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof org.example.pidev.controllers.utilisateur.ProfileController) {
                org.example.pidev.controllers.utilisateur.ProfileController profileCtrl =
                        (org.example.pidev.controllers.utilisateur.ProfileController) ctrl;
                if (org.example.pidev.utils.Session.getCurrentUser() != null) {
                    profileCtrl.setUser(org.example.pidev.utils.Session.getCurrentUser());
                }
            }

            javafx.stage.Stage profileStage = new javafx.stage.Stage();
            profileStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            profileStage.initOwner(listProduits.getScene().getWindow());
            profileStage.setTitle("Smart Farm - Mon Profil");
            javafx.scene.Scene scene = new javafx.scene.Scene(profileRoot);
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/smartfarmm.css");
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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");
        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                org.example.pidev.utils.Session.clear();

                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/LoginView.fxml"));
                javafx.scene.Parent loginRoot = loader.load();

                javafx.stage.Stage stage = (javafx.stage.Stage) listProduits.getScene().getWindow();
                stage.setMaximized(false);

                javafx.scene.Scene loginScene = new javafx.scene.Scene(loginRoot, 600, 500);
                var css1 = getClass().getResource("/styles/smartfarm.css");
                if (css1 != null) loginScene.getStylesheets().add(css1.toExternalForm());
                var css2 = getClass().getResource("/smartfarmm.css");
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
