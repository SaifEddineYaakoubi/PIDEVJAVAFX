package org.example.pidev.controllers.ventes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import org.example.pidev.services.ventes.ClientService;
import org.example.pidev.services.ventes.VenteService;

/**
 * DashboardHomeController - Gère l'affichage de la page d'accueil du dashboard
 */
public class DashboardHomeController {

    @FXML private HBox statsCardsBox;
    @FXML private VBox activitiesBox;
    @FXML private Label notificationLabel;

    private ClientService clientService;
    private VenteService venteService;

    @FXML
    public void initialize() {
        clientService = new ClientService();
        venteService = new VenteService();

        loadStatistics();
        loadActivities();
    }

    private void loadStatistics() {
        statsCardsBox.getChildren().clear();

        int clientCount = clientService.getAll().size();
        int venteCount = venteService.getAll().size();
        double totalAmount = venteService.getAll().stream().mapToDouble(v -> v.getMontantTotal()).sum();
        int inactiveCount = clientService.getInactiveClientsCount();

        VBox card1 = createStatCard("👥", "Clients", String.valueOf(clientCount), "Utilisateurs actifs", "stat-card stat-card-blue");
        VBox card2 = createStatCard("📈", "Ventes", String.valueOf(venteCount), "Transactions", "stat-card stat-card-purple");
        VBox card3 = createStatCard("💰", "Montant", String.format("%.2f TND", totalAmount), "Revenus", "stat-card stat-card-yellow");

        statsCardsBox.getChildren().addAll(card1, card2, card3);
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);

        // Notification
        if (inactiveCount > 0) {
            notificationLabel.setText("🔔 " + inactiveCount + " clients inactifs");
            notificationLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold;");
        } else {
            notificationLabel.setText("✅ Tous les clients sont actifs");
            notificationLabel.setStyle("-fx-text-fill: #27ae60;");
        }
    }

    private void loadActivities() {
        activitiesBox.getChildren().clear();

        Label title = new Label("📋 Activités Récentes");
        title.getStyleClass().add("activities-title");

        VBox itemsList = new VBox(10);
        itemsList.setStyle("-fx-padding: 15 0;");

        int clientCount = clientService.getAll().size();
        int venteCount = venteService.getAll().size();

        itemsList.getChildren().addAll(
            createActivityItem("✅ Système prêt", "Connexion réussie", "activity-item-green", "activity-dot-green"),
            createActivityItem("👥 " + clientCount + " clients", "Enregistrés", "activity-item-blue", "activity-dot-blue"),
            createActivityItem("📈 " + venteCount + " ventes", "Transactées", "activity-item-purple", "activity-dot-purple")
        );

        activitiesBox.getChildren().addAll(title, itemsList);
    }

    private VBox createStatCard(String emoji, String title, String value, String subtitle, String classes) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll(classes.split(" "));

        Label emojiLabel = new Label(emoji);
        emojiLabel.getStyleClass().add("stat-emoji");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("stat-subtitle");

        card.getChildren().addAll(emojiLabel, titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    private HBox createActivityItem(String title, String description, String itemClasses, String dotClasses) {
        HBox item = new HBox(15);
        item.getStyleClass().add(itemClasses);
        item.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label("●");
        dot.getStyleClass().add(dotClasses);

        VBox textBox = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("activity-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("activity-desc");

        textBox.getChildren().addAll(titleLabel, descLabel);
        item.getChildren().addAll(dot, textBox);

        return item;
    }
}

