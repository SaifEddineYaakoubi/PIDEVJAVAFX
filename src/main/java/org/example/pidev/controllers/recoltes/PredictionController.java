package org.example.pidev.controllers.recoltes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.pidev.services.recoltes.PredictionService;

import java.util.Map;

/**
 * Contrôleur pour l'affichage des prédictions de rendement
 * Affiche les prédictions pour chaque type de culture sous forme de cartes
 */
public class PredictionController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane predictionsGrid;

    private PredictionService predictionService;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("✅ PredictionController initialisé");

        try {
            predictionService = new PredictionService();

            // Configurer le GridPane
            setupGrid();

            // Charger les prédictions
            loadPredictions();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure le GridPane pour afficher les cartes
     */
    private void setupGrid() {
        predictionsGrid.setHgap(20);
        predictionsGrid.setVgap(20);
        predictionsGrid.setPadding(new Insets(20));
        predictionsGrid.setPrefWidth(Double.MAX_VALUE);

        // Style CSS
        predictionsGrid.setStyle("-fx-fill: #f5f5f5;");
    }

    /**
     * Charge et affiche toutes les prédictions
     */
    private void loadPredictions() {
        try {
            predictionsGrid.getChildren().clear();

            Map<String, PredictionService.PredictionData> predictions =
                    predictionService.getAllPredictions();

            if (predictions.isEmpty()) {
                Label noData = new Label("📊 Aucune donnée disponible pour les prédictions");
                noData.setStyle("-fx-font-size: 16; -fx-text-fill: #999;");
                predictionsGrid.add(noData, 0, 0);
                return;
            }

            // Afficher chaque prédiction sous forme de carte
            int col = 0, row = 0;
            for (PredictionService.PredictionData prediction : predictions.values()) {
                VBox card = createPredictionCard(prediction);
                predictionsGrid.add(card, col, row);

                col++;
                if (col > 2) { // 3 colonnes par défaut
                    col = 0;
                    row++;
                }
            }

            System.out.println("✅ " + predictions.size() + " prédictions chargées");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des prédictions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée une carte (VBox) pour afficher une prédiction
     *
     * @param prediction Les données de prédiction
     * @return VBox contenant la carte
     */
    private VBox createPredictionCard(PredictionService.PredictionData prediction) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-border-color: #ddd; -fx-border-radius: 10; " +
                "-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPrefWidth(300);

        // En-tête avec type de culture
        Label typeLabel = new Label("🌾 " + prediction.getTypeCulture());
        typeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2d5016;");

        // Prédiction de quantité
        HBox quantityBox = createInfoBox(
                "📦 Quantité prédite",
                String.format("%.2f kg", prediction.getPredictedQuantity()),
                "#4CAF50"
        );

        // Rendement prédit (quantité/ha)
        HBox yieldBox = createInfoBox(
                "📊 Rendement prédit",
                String.format("%.2f kg/ha", prediction.getPredictedYield()),
                "#2196F3"
        );

        // Tendance
        HBox trendBox = createInfoBox(
                "📈 Tendance",
                prediction.getTrend(),
                "#FF9800"
        );

        // Historique
        HBox historyBox = createInfoBox(
                "📅 Récoltes enregistrées",
                String.valueOf(prediction.getHistoryCount()),
                "#9C27B0"
        );

        card.getChildren().addAll(
                typeLabel,
                quantityBox,
                yieldBox,
                trendBox,
                historyBox
        );

        return card;
    }

    /**
     * Crée une boîte d'information avec un label et une valeur
     *
     * @param label Le label
     * @param value La valeur
     * @param color La couleur de la boîte
     * @return HBox contenant l'information
     */
    private HBox createInfoBox(String label, String value, String color) {
        HBox box = new HBox(10);
        box.setStyle("-fx-background-color: " + color + "22; -fx-border-radius: 5; " +
                "-fx-padding: 10; -fx-border-color: " + color + ";");

        Label labelControl = new Label(label);
        labelControl.setStyle("-fx-font-size: 12; -fx-text-fill: #666; -fx-font-weight: bold;");

        Label valueControl = new Label(value);
        valueControl.setStyle("-fx-font-size: 16; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        HBox.setHgrow(valueControl, javafx.scene.layout.Priority.ALWAYS);
        valueControl.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        box.getChildren().addAll(labelControl, valueControl);
        return box;
    }

    /**
     * Rafraîchir les prédictions (peut être appelé de l'extérieur)
     */
    public void refreshPredictions() {
        System.out.println("🔄 Rafraîchissement des prédictions...");
        loadPredictions();
    }
}

