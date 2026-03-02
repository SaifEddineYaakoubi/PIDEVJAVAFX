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
import org.example.pidev.services.AIPredictionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Contrôleur pour l'affichage des prédictions IA
 * Intègre Hugging Face avec design moderne
 */
public class AIPredictionsController {

    @FXML
    private GridPane predictionsGridPane;

    @FXML
    private Circle aiStatusCircle;

    @FXML
    private Label aiStatusLabel;

    @FXML
    private Label lastAIUpdateLabel;

    private AIPredictionService aiService;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        System.out.println("🤖 Initialisation du contrôleur des prédictions IA");

        aiService = new AIPredictionService();

        // Charger les prédictions au démarrage
        loadPredictions();
    }

    /**
     * Charge et affiche toutes les prédictions IA
     */
    @FXML
    private void loadPredictions() {
        System.out.println("🤖 Chargement des prédictions IA...");

        // Exécuter en arrière-plan pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                Map<String, AIPredictionService.AIPrediction> allPredictions = aiService.getAllAIPredictions(20);

                Platform.runLater(() -> {
                    displayPredictions(allPredictions);
                    updateAIStatus();
                });

            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement des prédictions: " + e.getMessage());
                Platform.runLater(() -> showError("Erreur lors du chargement des prédictions IA"));
            }
        }).start();
    }

    /**
     * Affiche les cartes de prédictions dans la grille
     */
    private void displayPredictions(Map<String, AIPredictionService.AIPrediction> predictions) {
        predictionsGridPane.getChildren().clear();

        int row = 0;
        int col = 0;

        for (AIPredictionService.AIPrediction prediction : predictions.values()) {
            VBox card = createPredictionCard(prediction);

            predictionsGridPane.add(card, col, row);
            GridPane.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);

            col++;
            if (col >= 3) {  // 3 colonnes
                col = 0;
                row++;
            }

            // Animation fade in
            FadeTransition fade = new FadeTransition(Duration.millis(600), card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        System.out.println("✅ " + predictions.size() + " cartes de prédictions affichées");
    }

    /**
     * Crée une carte de prédiction IA unique simplifiée et lisible
     */
    private VBox createPredictionCard(AIPredictionService.AIPrediction prediction) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 14;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);"
        );

        // En-tête : Emoji + Titre culture
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label emoji = new Label(getEmojiForCrop(prediction.getCropType()));
        emoji.setStyle("-fx-font-size: 28;");
        Label title = new Label(prediction.getCropType());
        title.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        header.getChildren().addAll(emoji, title);

        // Rendement
        Label yieldLabel = new Label(String.format("%.0f kg", prediction.getYieldPrediction()));
        yieldLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #7B1FA2;");

        // Confiance + barre de progression
        double conf = prediction.getConfidence();
        String confColor = conf > 85 ? "#27AE60" : conf > 70 ? "#3498DB" : "#F39C12";

        HBox confidenceBox = new HBox(8);
        confidenceBox.setAlignment(Pos.CENTER_LEFT);
        Label confLabel = new Label(String.format("%.0f%%", conf));
        confLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: " + confColor + ";");

        ProgressBar confBar = new ProgressBar(conf / 100.0);
        confBar.setPrefHeight(6);
        confBar.setStyle("-fx-accent: " + confColor + ";");
        HBox.setHgrow(confBar, javafx.scene.layout.Priority.ALWAYS);

        confidenceBox.getChildren().addAll(confLabel, confBar);

        // Risque maladie
        String riskEmoji = "LOW".equals(prediction.getDiseaseRisk()) ? "✅" :
                "MEDIUM".equals(prediction.getDiseaseRisk()) ? "⚠️" : "🔴";
        Label riskLabel = new Label(riskEmoji + " " + prediction.getDiseaseRisk());
        String riskColor = "LOW".equals(prediction.getDiseaseRisk()) ? "#27AE60" :
                "MEDIUM".equals(prediction.getDiseaseRisk()) ? "#F39C12" : "#E74C3C";
        riskLabel.setStyle("-fx-font-size: 11; -fx-text-fill: " + riskColor + "; -fx-font-weight: bold;");

        // Conseil global (court)
        Label adviceLabel = new Label(prediction.getGlobalAdvice());
        adviceLabel.setWrapText(true);
        adviceLabel.setStyle(
                "-fx-font-size: 11; -fx-padding: 8; -fx-background-color: #D5F4E6; " +
                        "-fx-text-fill: #27AE60; -fx-background-radius: 4; -fx-border-radius: 4;"
        );

        card.getChildren().addAll(
                header,
                yieldLabel,
                confidenceBox,
                riskLabel,
                new Separator(),
                adviceLabel
        );

        return card;
    }

    /**
     * Récupère l'emoji approprié pour une culture
     */
    private String getEmojiForCrop(String crop) {
        return switch(crop.toLowerCase()) {
            case "tomate" -> "🍅";
            case "blé" -> "🌾";
            case "maïs" -> "🌽";
            case "olive" -> "🫒";
            case "riz" -> "🍚";
            case "pomme de terre" -> "🥔";
            default -> "🌱";
        };
    }

    /**
     * Met à jour le statut de l'API IA
     */
    private void updateAIStatus() {
        boolean isAvailable = aiService.isAPIAvailable();

        if (isAvailable) {
            aiStatusCircle.setFill(javafx.scene.paint.Color.web("#9C27B0"));
            aiStatusLabel.setText("✅ API Hugging Face: CONNECTÉE");
        } else {
            aiStatusCircle.setFill(javafx.scene.paint.Color.web("#F39C12"));
            aiStatusLabel.setText("⚠️ API Hugging Face: MODE FALLBACK (Prédictions simulées)");
        }

        lastAIUpdateLabel.setText("Dernière prédiction: " + LocalDateTime.now().format(TIME_FORMAT));
    }

    /**
     * Rafraîchit les prédictions (bouton)
     */
    @FXML
    private void handleRefreshPredictions() {
        System.out.println("🔄 Rafraîchissement des prédictions IA...");
        loadPredictions();
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

