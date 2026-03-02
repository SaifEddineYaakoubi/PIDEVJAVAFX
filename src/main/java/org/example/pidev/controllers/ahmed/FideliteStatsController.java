package org.example.pidev.controllers.ahmed;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.pidev.services.ahmed.ClientService;
import org.example.pidev.services.ahmed.PDFExportService;

import java.io.File;
import java.util.Map;

public class FideliteStatsController {

    @FXML
    private PieChart fideliteChart;

    @FXML
    private Label vipCountLabel;

    @FXML
    private Label fideleCountLabel;

    @FXML
    private Label standardCountLabel;

    private ClientService clientService;

    public void initialize() {
        clientService = new ClientService();
        loadStatistics();
    }

    /**
     * Charge et affiche les statistiques de fidélité
     */
    private void loadStatistics() {
        try {
            // Récupérer les comptages par statut
            Map<String, Integer> stats = clientService.getClientCountByStatut();

            // Mettre à jour les labels
            int vipCount = stats.getOrDefault("VIP", 0);
            int fideleCount = stats.getOrDefault("Fidèle", 0);
            int standardCount = stats.getOrDefault("Standard", 0);

            vipCountLabel.setText(String.valueOf(vipCount));
            fideleCountLabel.setText(String.valueOf(fideleCount));
            standardCountLabel.setText(String.valueOf(standardCount));

            // Remplir le graphique en camembert
            fideliteChart.getData().clear();

            if (vipCount > 0) {
                fideliteChart.getData().add(new PieChart.Data("VIP (👑) - " + vipCount, vipCount));
            }
            if (fideleCount > 0) {
                fideliteChart.getData().add(new PieChart.Data("Fidèles (⭐) - " + fideleCount, fideleCount));
            }
            if (standardCount > 0) {
                fideliteChart.getData().add(new PieChart.Data("Standard (🔄) - " + standardCount, standardCount));
            }

            fideliteChart.setTitle("Répartition des Clients par Statut de Fidélité");

            // Appliquer les styles APRÈS le rendu (Platform.runLater)
            Platform.runLater(() -> {
                for (PieChart.Data data : fideliteChart.getData()) {
                    String label = data.getName();
                    if (label.contains("VIP")) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-pie-color: #FFD700;");
                        }
                    } else if (label.contains("Fidèles")) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-pie-color: #76b947;");
                        }
                    } else if (label.contains("Standard")) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-pie-color: #95a5a6;");
                        }
                    }
                }
            });

            if (vipCount + fideleCount + standardCount == 0) {
                showAlert("Information", "Aucun client trouvé dans la base de données.", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des statistiques: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadStatistics();
        showAlert("Succès", "Statistiques actualisées.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportPDF() {
        try {
            // Pour simplifier, nous affichons un message car l'export de graphiques est complexe
            showAlert("Export", "Fonction d'export en PDF à implémenter. " +
                    "Les statistiques actuelles:\n" +
                    "VIP: " + vipCountLabel.getText() + "\n" +
                    "Fidèles: " + fideleCountLabel.getText() + "\n" +
                    "Standard: " + standardCountLabel.getText(), Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'export: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) fideliteChart.getScene().getWindow();
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

