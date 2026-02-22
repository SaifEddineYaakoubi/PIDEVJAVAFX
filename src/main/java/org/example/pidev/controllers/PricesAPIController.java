package org.example.pidev.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.pidev.services.FAOPricesService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Contrôleur pour l'affichage des prix API
 * Intègre Alpha Vantage avec design moderne
 */
public class PricesAPIController {

    @FXML
    private GridPane pricesGridPane;

    @FXML
    private Label avgPriceLabel;

    @FXML
    private Label highestPriceLabel;

    @FXML
    private Label lowestPriceLabel;

    @FXML
    private Label upCountLabel;

    @FXML
    private Circle apiStatusCircle;

    @FXML
    private Label apiStatusLabel;

    @FXML
    private Label lastUpdateLabel;

    private FAOPricesService faoService;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        System.out.println("🟢 Initialisation du contrôleur des prix API");

        faoService = new FAOPricesService();

        // Charger les prix au démarrage
        loadPrices();
    }

    /**
     * Charge et affiche tous les prix
     */
    @FXML
    private void loadPrices() {
        System.out.println("📊 Chargement des prix...");

        // Exécuter en arrière-plan pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                Map<String, FAOPricesService.PriceData> allPrices = faoService.getAllPrices();

                Platform.runLater(() -> {
                    displayPrices(allPrices);
                    updateStatistics(allPrices);
                    updateApiStatus();
                });

            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement des prix: " + e.getMessage());
                Platform.runLater(() -> showError("Erreur lors du chargement des prix"));
            }
        }).start();
    }

    /**
     * Affiche les cartes de prix dans la grille
     */
    private void displayPrices(Map<String, FAOPricesService.PriceData> prices) {
        pricesGridPane.getChildren().clear();

        int row = 0;
        int col = 0;

        for (FAOPricesService.PriceData price : prices.values()) {
            VBox card = createPriceCard(price);

            pricesGridPane.add(card, col, row);
            GridPane.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);

            col++;
            if (col >= 3) {  // 3 colonnes
                col = 0;
                row++;
            }

            // Animation fade in
            FadeTransition fade = new FadeTransition(Duration.millis(500), card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        System.out.println("✅ " + prices.size() + " cartes de prix affichées");
    }

    /**
     * Crée une carte de prix unique
     */
    private VBox createPriceCard(FAOPricesService.PriceData price) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: linear-gradient(135deg, #FFFFFF 0%, #F8F9FA 100%);" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 16;" +
            "-fx-border-width: 1;" +
            "-fx-border-color: #E0E6ED;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);"
        );

        // Emoji + Titre
        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label emoji = new Label(price.getCommodity().substring(0, 1).equals("T") ? "🍅" :
                                price.getCommodity().substring(0, 1).equals("B") ? "🌾" :
                                price.getCommodity().substring(0, 1).equals("M") ? "🌽" : "🌾");
        emoji.setStyle("-fx-font-size: 36;");

        Label title = new Label(price.getCommodity());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        titleBox.getChildren().addAll(emoji, title);

        // Prix actuel
        HBox priceBox = new HBox(4);
        priceBox.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label(String.format("$%.4f", price.getCurrentPrice()));
        priceLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2D5016;");

        Label currencyLabel = new Label("USD");
        currencyLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7F8C8D;");

        priceBox.getChildren().addAll(priceLabel, currencyLabel);

        // Variation %
        String variationText = String.format("%+.2f%%", price.getPriceChange());
        String variationColor = price.getPriceChange() > 0 ? "#27AE60" :
                               price.getPriceChange() < 0 ? "#E74C3C" : "#95A5A6";
        String variationIcon = price.getPriceChange() > 0 ? "📈 " :
                              price.getPriceChange() < 0 ? "📉 " : "➡️ ";

        Label variation = new Label(variationIcon + variationText);
        variation.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + variationColor + ";");

        // Tendance
        Label trend = new Label(price.getTrend());
        trend.setStyle("-fx-font-size: 12; -fx-text-fill: #7F8C8D;");

        // Conseil de vente
        Label advice = new Label(price.getSellAdvice());
        advice.setWrapText(true);

        String adviceStyle;
        if (price.getSellAdvice().contains("VENDRE")) {
            adviceStyle = "-fx-background-color: #D5F4E6; -fx-text-fill: #27AE60; " +
                         "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 8 12; " +
                         "-fx-background-radius: 8; -fx-border-color: #27AE60; -fx-border-width: 1;";
        } else if (price.getSellAdvice().contains("Attendre")) {
            adviceStyle = "-fx-background-color: #FCF3DE; -fx-text-fill: #F39C12; " +
                         "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 8 12; " +
                         "-fx-background-radius: 8; -fx-border-color: #F39C12; -fx-border-width: 1;";
        } else {
            adviceStyle = "-fx-background-color: #FADBD8; -fx-text-fill: #E74C3C; " +
                         "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 8 12; " +
                         "-fx-background-radius: 8; -fx-border-color: #E74C3C; -fx-border-width: 1;";
        }
        advice.setStyle(adviceStyle);

        // Score d'opportunité
        int score = price.getSellOpportunityScore();
        ProgressBar scoreBar = new ProgressBar(score / 100.0);
        scoreBar.setPrefWidth(Double.MAX_VALUE);
        scoreBar.setStyle("-fx-accent: " +
                         (score > 70 ? "#27AE60" : score > 50 ? "#F39C12" : "#E74C3C") + ";");

        Label scoreLabel = new Label(String.format("Opportunité: %d/100", score));
        scoreLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7F8C8D;");

        // Source API
        Label source = new Label("🌐 Source: " + price.getSource());
        source.setStyle("-fx-font-size: 10; -fx-text-fill: #BDC3C7; -fx-font-style: italic;");

        card.getChildren().addAll(
            titleBox,
            priceBox,
            variation,
            trend,
            new Separator(),
            advice,
            scoreLabel,
            scoreBar,
            source
        );

        return card;
    }

    /**
     * Met à jour les statistiques du marché
     */
    private void updateStatistics(Map<String, FAOPricesService.PriceData> prices) {
        if (prices.isEmpty()) {
            return;
        }

        // Calculer les statistiques
        double avgPrice = prices.values().stream()
            .mapToDouble(FAOPricesService.PriceData::getCurrentPrice)
            .average()
            .orElse(0);

        double highestPrice = prices.values().stream()
            .mapToDouble(FAOPricesService.PriceData::getCurrentPrice)
            .max()
            .orElse(0);

        double lowestPrice = prices.values().stream()
            .mapToDouble(FAOPricesService.PriceData::getCurrentPrice)
            .min()
            .orElse(0);

        long upCount = prices.values().stream()
            .filter(p -> p.getPriceChange() > 0)
            .count();

        // Mettre à jour les labels
        avgPriceLabel.setText(String.format("$%.4f", avgPrice));
        highestPriceLabel.setText(String.format("$%.4f", highestPrice));
        lowestPriceLabel.setText(String.format("$%.4f", lowestPrice));
        upCountLabel.setText(upCount + "/" + prices.size());

        System.out.println("📊 Statistiques mises à jour");
    }

    /**
     * Met à jour le statut de l'API
     */
    private void updateApiStatus() {
        boolean isAvailable = faoService.isAPIAvailable();

        if (isAvailable) {
            apiStatusCircle.setFill(javafx.scene.paint.Color.web("#27AE60"));
            apiStatusLabel.setText("✅ API Alpha Vantage: CONNECTÉE");
        } else {
            apiStatusCircle.setFill(javafx.scene.paint.Color.web("#F39C12"));
            apiStatusLabel.setText("⚠️ API Alpha Vantage: MODE FALLBACK (Données simulées)");
        }

        lastUpdateLabel.setText("Dernière mise à jour: " + LocalDateTime.now().format(TIME_FORMAT));
    }

    /**
     * Rafraîchit les prix (bouton)
     */
    @FXML
    private void handleRefreshPrices() {
        System.out.println("🔄 Rafraîchissement des prix...");
        loadPrices();
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur lors du chargement");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

