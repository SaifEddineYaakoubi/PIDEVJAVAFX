package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.pidev.services.PredictionService;

import java.util.Map;

/**
 * Contrôleur pour le widget de prédictions sur le Dashboard
 * Affiche un aperçu compact des prédictions principales
 */
public class PredictionWidgetController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane predictionsGrid;

    @FXML
    private Button btnRefresh;

    private PredictionService predictionService;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("✅ PredictionWidgetController initialisé");

        try {
            predictionService = new PredictionService();

            // Configurer le GridPane
            setupGrid();

            // Charger les prédictions
            loadPredictions();

            // Bouton de rafraîchissement
            btnRefresh.setOnAction(event -> refreshPredictions());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure le GridPane
     */
    private void setupGrid() {
        predictionsGrid.setHgap(15);
        predictionsGrid.setVgap(15);
        predictionsGrid.setPrefWidth(Double.MAX_VALUE);
    }

    /**
     * Charge et affiche les prédictions
     */
    private void loadPredictions() {
        try {
            predictionsGrid.getChildren().clear();

            Map<String, PredictionService.PredictionData> predictions =
                    predictionService.getAllPredictions();

            if (predictions.isEmpty()) {
                Label noData = new Label("📊 Aucune donnée disponible pour les prédictions");
                noData.setStyle("-fx-font-size: 14; -fx-text-fill: #999;");
                predictionsGrid.add(noData, 0, 0);
                return;
            }

            // Afficher chaque prédiction
            int col = 0, row = 0;
            for (PredictionService.PredictionData prediction : predictions.values()) {
                VBox card = createCompactCard(prediction);
                predictionsGrid.add(card, col, row);
                GridPane.setHgrow(card, Priority.ALWAYS);

                col++;
                if (col > 1) { // 2 colonnes pour le widget compact
                    col = 0;
                    row++;
                }
            }

            System.out.println("✅ " + predictions.size() + " prédictions chargées");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement: " + e.getMessage());
        }
    }

    /**
     * Crée une carte compacte pour le widget
     *
     * @param prediction Les données de prédiction
     * @return VBox contenant la carte
     */
    private VBox createCompactCard(PredictionService.PredictionData prediction) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 8; " +
                "-fx-background-color: #FAFAFA; -fx-border-width: 1;");

        // Type de culture
        Label typeLabel = new Label("🌾 " + prediction.getTypeCulture());
        typeLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2d5016;");

        // Quantité prédite
        Label quantityLabel = new Label("📦 " + String.format("%.1f kg", prediction.getPredictedQuantity()));
        quantityLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4CAF50;");

        // Rendement
        Label yieldLabel = new Label("📊 " + String.format("%.1f kg/ha", prediction.getPredictedYield()));
        yieldLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2196F3;");

        // Tendance
        Label trendLabel = new Label(prediction.getTrend());
        trendLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #FF9800; -fx-font-weight: bold;");

        card.getChildren().addAll(
                typeLabel,
                quantityLabel,
                yieldLabel,
                trendLabel
        );

        return card;
    }

    /**
     * Rafraîchir les prédictions
     */
    private void refreshPredictions() {
        System.out.println("🔄 Rafraîchissement des prédictions...");
        loadPredictions();
    }
}

