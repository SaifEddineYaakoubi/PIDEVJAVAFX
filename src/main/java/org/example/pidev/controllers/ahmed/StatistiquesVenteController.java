package org.example.pidev.controllers.ahmed;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.services.ahmed.ClientService;
import org.example.pidev.services.ahmed.PDFExportService;
import org.example.pidev.services.ahmed.VenteService;
import org.example.pidev.models.Vente;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatistiquesVenteController {
    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Label totalClientsLabel;

    @FXML
    private Label totalVentesLabel;

    @FXML
    private Label montantTotalLabel;

    @FXML
    private Button btnCloud;

    private VenteService venteService;
    private ClientService clientService;

    public void initialize() {
        venteService = new VenteService();
        clientService = new ClientService();
        chargerDonnees();
    }

    private void chargerDonnees() {
        // Récupérer les statistiques
        Map<Integer, Double> stats = venteService.getTotalVentesByClient();
        List<Client> clients = clientService.getAll();

        // Calculer les totaux
        int totalClients = clients.size();
        int totalVentes = stats.size();
        double montantTotal = stats.values().stream().mapToDouble(Double::doubleValue).sum();

        // Mettre à jour les labels
        totalClientsLabel.setText(String.valueOf(totalClients));
        totalVentesLabel.setText(String.valueOf(totalVentes));
        montantTotalLabel.setText(String.format("%.2f TND", montantTotal));

        // Préparer les données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montant Total");

        for (Map.Entry<Integer, Double> entry : stats.entrySet()) {
            Client c = clientService.getById(entry.getKey());
            String label = c != null ? c.getNom() : "Client#" + entry.getKey();
            series.getData().add(new XYChart.Data<>(label, entry.getValue()));
        }

        barChart.getData().add(series);
    }

    @FXML
    private void handleRetour() {
        Stage stage = (Stage) barChart.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRefresh() {
        barChart.getData().clear();
        chargerDonnees();
    }

    @FXML
    private void handleCloudAction() {
        // Disable button during export
        btnCloud.setDisable(true);
        btnCloud.setText("⏳ Génération PDF en cours...");

        // Run export in background thread to avoid blocking UI
        Thread exportThread = new Thread(() -> {
            try {
                // Get all ventes and clients data
                List<Vente> ventes = venteService.getAll();
                List<Client> clients = clientService.getAll();

                // Create temporary PDF file
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                File tempPdfFile = new File(System.getProperty("java.io.tmpdir"),
                        "Rapport_Ventes_" + timestamp + ".pdf");

                // Generate PDF report
                PDFExportService.exportVentesReportToPDF(ventes, clients, tempPdfFile);

                // Update UI on FX Application Thread
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("📄 Exporter PDF");

                    // Propose to save file
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Enregistrer le rapport PDF");
                    fileChooser.setInitialFileName("Rapport_Ventes_" + timestamp + ".pdf");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

                    Stage stage = (Stage) barChart.getScene().getWindow();
                    File destFile = fileChooser.showSaveDialog(stage);

                    if (destFile != null) {
                        try {
                            java.nio.file.Files.copy(tempPdfFile.toPath(), destFile.toPath(),
                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("✅ Succès");
                            success.setHeaderText("Rapport PDF exporté");
                            success.setContentText("Fichier: " + destFile.getAbsolutePath());
                            success.showAndWait();
                        } catch (Exception ex) {
                            showError("Erreur", "Impossible d'enregistrer le fichier: " + ex.getMessage());
                        }
                    }
                    // Clean up temp
                    if (tempPdfFile.exists()) tempPdfFile.delete();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("📄 Exporter PDF");
                    showError("Erreur", "Impossible de générer le rapport PDF: " + e.getMessage());
                });
                e.printStackTrace();
            }
        });
        exportThread.setDaemon(true);
        exportThread.start();
    }

    private void showError(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("❌ " + title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}

