package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pidev.models.ClientRelance;
import org.example.pidev.services.ClientService;
import org.example.pidev.services.EmailService;

import java.util.List;

public class RelanceClientController {

    @FXML
    private ListView<ClientRelance> inactiveClientsList;

    @FXML
    private Label totalInactiveLabel;

    @FXML
    private Label statsLabel;

    private ClientService clientService;
    private EmailService emailService;

    public void initialize() {
        clientService = new ClientService();
        emailService = new EmailService();
        loadInactiveClients();
    }

    /**
     * Charge et affiche les clients inactifs
     */
    private void loadInactiveClients() {
        try {
            List<ClientRelance> inactiveClients = clientService.getInactiveClients();

            // Mettre à jour le label total
            totalInactiveLabel.setText(String.valueOf(inactiveClients.size()));

            // Afficher les statistiques
            StringBuilder stats = new StringBuilder();
            int totalDays = 0;
            for (ClientRelance client : inactiveClients) {
                totalDays += client.getDaysInactive();
            }
            if (!inactiveClients.isEmpty()) {
                int avgDays = totalDays / inactiveClients.size();
                stats.append(String.format("Inactivité moyenne: %d jours", avgDays));
            }
            statsLabel.setText(stats.toString());

            // Configurer le cellFactory pour afficher les clients avec boutons
            inactiveClientsList.setCellFactory(param -> new ClientRelanceCell());
            inactiveClientsList.getItems().setAll(inactiveClients);

            if (inactiveClients.isEmpty()) {
                showAlert("Information", "Aucun client inactif détecté. Excellent travail! 🎉", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des clients inactifs: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * CellFactory personnalisée pour afficher les clients avec actions
     */
    private class ClientRelanceCell extends ListCell<ClientRelance> {
        @Override
        protected void updateItem(ClientRelance client, boolean empty) {
            super.updateItem(client, empty);

            if (empty || client == null) {
                setGraphic(null);
                setText(null);
            } else {
                // Container principal
                HBox container = new HBox(15);
                container.setPadding(new Insets(12));
                container.setStyle("-fx-border-radius: 8; -fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 1;");

                // Colonne gauche: Informations client
                VBox infoBox = new VBox(5);
                infoBox.setPrefWidth(300);

                Label nameLabel = new Label(client.getNom() + " " + (client.getPrenom() != null ? client.getPrenom() : ""));
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

                Label emailLabel = new Label("📧 " + client.getEmail());
                emailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                Label lastSaleLabel = new Label("Dernière vente: " + (client.getLastSaleDate() != null ? client.getLastSaleDate() : "Jamais"));
                lastSaleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");

                Label inactiveLabel = new Label("⏱️ " + client.getDaysInactive() + " jours d'inactivité");
                inactiveLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #d9534f;");

                // Badge de statut de fidélité
                Label fideleLabel = new Label(client.getStatutFidelite() != null ? client.getStatutFidelite() : "Standard");
                fideleLabel.setPadding(new Insets(3, 8, 3, 8));
                fideleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-background-radius: 5; " +
                    getBadgeStyle(client.getStatutFidelite()));

                infoBox.getChildren().addAll(nameLabel, emailLabel, lastSaleLabel, inactiveLabel, fideleLabel);

                // Colonne droite: Bouton d'action
                VBox actionBox = new VBox();
                actionBox.setStyle("-fx-alignment: CENTER;");

                Button couponButton = new Button("💎 Générer\nCoupon");
                couponButton.setPrefSize(120, 60);
                couponButton.setStyle(
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: linear-gradient(to bottom, #28a745, #1e7e34); " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(40, 167, 69, 0.3), 8, 0, 0, 2);"
                );

                couponButton.setOnAction(e -> handleGenerateCoupon(client, couponButton));

                actionBox.getChildren().add(couponButton);

                container.getChildren().addAll(infoBox, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, actionBox);

                setGraphic(container);
                setText(null);
            }
        }

        private String getBadgeStyle(String statut) {
            if (statut == null) statut = "Standard";
            return switch (statut) {
                case "VIP" -> "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-text-fill: #1a1a1a;";
                case "Fidèle" -> "-fx-background-color: linear-gradient(to bottom, #76b947, #5fa032); -fx-text-fill: white;";
                default -> "-fx-background-color: #95a5a6; -fx-text-fill: white;";
            };
        }
    }

    /**
     * Gère la génération du coupon pour un client
     */
    private void handleGenerateCoupon(ClientRelance client, Button button) {
        try {
            // 1. Générer le coupon
            Double coupon = client.generateCoupon();
            clientService.updateClientPromoStatus(client.getIdClient(), true);

            // 2. Envoyer un email au client avec le coupon
            String emailDestinaire = client.getEmail();
            String nomClient = client.getNom();
            String codeCoupon = String.format("%.2f DT", coupon);

            try {
                emailService.sendCouponEmail(emailDestinaire, nomClient, codeCoupon);
                System.out.println("✅ Email envoyé avec succès à " + emailDestinaire);
            } catch (Exception emailException) {
                System.err.println("⚠️ Le coupon a été généré mais l'email n'a pas pu être envoyé: " + emailException.getMessage());
                // Afficher une alerte mais ne pas bloquer le succès de la génération du coupon
                showAlert("⚠️ Avertissement",
                    String.format("Coupon généré (%.2f DT) mais l'email n'a pas pu être envoyé.\n" +
                        "Raison: %s\n\nVérifiez votre configuration EmailService.",
                        coupon, emailException.getMessage()),
                    Alert.AlertType.WARNING);
            }

            // 3. Désactiver le bouton après clic
            button.setDisable(true);
            button.setText("✅ Coupon\nGénéré!");
            button.setStyle(button.getStyle().replace("28a745", "28a745").replace("#1e7e34", "#1e7e34"));

            // 4. Afficher un message de succès
            showAlert("Succès",
                String.format("✅ Coupon de %.2f DT généré pour %s!\n" +
                    "📧 Email envoyé à: %s\n" +
                    "Statut: %s",
                    coupon, nomClient, emailDestinaire, client.getStatutFidelite()),
                Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du coupon: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadInactiveClients();
        showAlert("Succès", "Liste actualisée.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) inactiveClientsList.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

