package org.example.pidev.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.pidev.models.Client;
import org.example.pidev.services.ClientService;
import org.example.pidev.services.GoogleDriveService;
import org.example.pidev.services.PDFExportService;
import org.example.pidev.services.VenteService;
import org.example.pidev.models.Vente;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
        // Disable button during upload
        btnCloud.setDisable(true);
        btnCloud.setText("⏳ Génération PDF en cours...");

        // Run cloud upload in background thread to avoid blocking UI
        Thread uploadThread = new Thread(() -> {
            try {
                // Get all ventes and clients data
                List<Vente> ventes = venteService.getAll();
                List<Client> clients = clientService.getAll();

                // Create temporary PDF file
                String timestamp = String.valueOf(System.currentTimeMillis());
                java.io.File tempPdfFile = new java.io.File(System.getProperty("java.io.tmpdir"),
                        "Rapport_Ventes_" + timestamp + ".pdf");

                // Generate PDF report using the new professional service
                PDFExportService.exportVentesReportToPDF(ventes, clients, tempPdfFile);

                // Get Google Drive service
                com.google.api.services.drive.Drive drive = GoogleDriveService.getDriveService();

                // Create file metadata for Google Drive
                com.google.api.services.drive.model.File fileMetadata =
                    new com.google.api.services.drive.model.File()
                        .setName("Rapport_Ventes_" + timestamp + ".pdf");

                // Create file content from PDF file
                java.io.FileInputStream fileInputStream = new java.io.FileInputStream(tempPdfFile);
                com.google.api.client.http.InputStreamContent mediaContent =
                    new com.google.api.client.http.InputStreamContent("application/pdf", fileInputStream);

                // Upload to Google Drive
                com.google.api.services.drive.model.File uploadedFile =
                    drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, webViewLink")
                        .execute();

                // Clean up temporary file
                if (tempPdfFile.exists()) {
                    tempPdfFile.delete();
                }

                // Show success message on FX Application Thread
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("☁️ Envoyer vers Cloud");

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("✅ Succès");
                    successAlert.setHeaderText("Rapport PDF uploadé vers Google Drive");
                    successAlert.setContentText("Fichier: Rapport_Ventes_" + timestamp + ".pdf\n" +
                            "ID: " + uploadedFile.getId() + "\n\n" +
                            "Lien: " + uploadedFile.getWebViewLink());
                    successAlert.showAndWait();
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("☁️ Envoyer vers Cloud");

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("❌ Erreur d'Upload");
                    errorAlert.setHeaderText("Impossible d'uploader vers Google Drive");
                    errorAlert.setContentText("Erreur: " + e.getMessage() + "\n\n" +
                            "Assurez-vous que:\n" +
                            "1. Vous êtes connecté à Internet\n" +
                            "2. Vous avez autorisé l'accès à Google Drive\n" +
                            "3. credentials.json est correctement configuré");
                    errorAlert.showAndWait();
                });
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("☁️ Envoyer vers Cloud");

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("❌ Erreur d'Authentification");
                    errorAlert.setHeaderText("Erreur de sécurité OAuth2");
                    errorAlert.setContentText("Erreur: " + e.getMessage() + "\n\n" +
                            "Veuillez vérifier votre configuration OAuth2");
                    errorAlert.showAndWait();
                });
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnCloud.setDisable(false);
                    btnCloud.setText("☁️ Envoyer vers Cloud");

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("❌ Erreur de Génération PDF");
                    errorAlert.setHeaderText("Impossible de générer le rapport PDF");
                    errorAlert.setContentText("Erreur: " + e.getMessage());
                    errorAlert.showAndWait();
                });
                e.printStackTrace();
            }
        });
        uploadThread.setDaemon(true);
        uploadThread.start();
    }

}