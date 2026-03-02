package org.example.pidev.controllers.ahmed;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.pidev.services.ahmed.ClientService;

import java.util.Map;

public class ConversionStatsController {

    @FXML
    private BarChart<String, Number> conversionChart;

    @FXML
    private Label totalInactiveLabel;

    @FXML
    private Label convertedLabel;

    @FXML
    private Label rateLabel;

    private ClientService clientService;

    public void initialize() {
        clientService = new ClientService();
        loadConversionStats();
    }

    /**
     * Charge et affiche les statistiques de conversion
     */
    private void loadConversionStats() {
        try {
            Map<String, Object> stats = clientService.getConversionStats();

            int totalInactive = (int) stats.get("totalInactive");
            long convertedToFidele = (long) stats.get("convertedToFidele");
            long convertedToVIP = (long) stats.get("convertedToVIP");
            long totalConverted = (long) stats.get("totalConverted");
            double conversionRate = (double) stats.get("conversionRate");

            // Mettre à jour les labels
            totalInactiveLabel.setText(String.valueOf(totalInactive));
            convertedLabel.setText(String.format("%d / %d", totalConverted, totalInactive));
            rateLabel.setText(String.format("%.1f%%", conversionRate));

            // Afficher le graphique en barres
            Platform.runLater(() -> {
                conversionChart.getData().clear();

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Clients Convertis");

                series.getData().add(new XYChart.Data<>("Vers Fidèle", convertedToFidele));
                series.getData().add(new XYChart.Data<>("Vers VIP", convertedToVIP));
                series.getData().add(new XYChart.Data<>("Non Convertis", totalInactive - totalConverted));

                conversionChart.getData().add(series);

                // Styliser les barres
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String style = "";
                    if ("Vers Fidèle".equals(data.getXValue())) {
                        style = "-fx-bar-fill: #76b947;"; // Vert
                    } else if ("Vers VIP".equals(data.getXValue())) {
                        style = "-fx-bar-fill: #FFD700;"; // Or
                    } else {
                        style = "-fx-bar-fill: #95a5a6;"; // Gris
                    }

                    if (data.getNode() != null) {
                        data.getNode().setStyle(style);
                    }
                }
            });

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des statistiques: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadConversionStats();
        showAlert("Succès", "Statistiques actualisées.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) conversionChart.getScene().getWindow();
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

