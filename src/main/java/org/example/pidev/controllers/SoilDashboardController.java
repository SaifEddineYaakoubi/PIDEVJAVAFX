package org.example.pidev.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.pidev.models.SoilData;
import org.example.pidev.services.SoilService;

import java.util.logging.Logger;

/**
 * Contrôleur pour l'affichage des données du sol dans le Dashboard
 */
public class SoilDashboardController {

    private static final Logger logger = Logger.getLogger(SoilDashboardController.class.getName());

    // ===== FXML Components =====
    @FXML private Circle soilStatusCircle;
    @FXML private Label soilStatusLabel;
    @FXML private Button refreshSoilButton;
    @FXML private GridPane soilDataGrid;

    // pH
    @FXML private Label phValue;
    @FXML private Label phCategory;

    // NPK
    @FXML private Label nitrogenValue;
    @FXML private Label nitrogenStatus;
    @FXML private Label phosphorusValue;
    @FXML private Label phosphorusStatus;
    @FXML private Label potassiumValue;
    @FXML private Label potassiumStatus;

    // Texture
    @FXML private Label sandValue;
    @FXML private ProgressBar sandProgress;
    @FXML private Label siltValue;
    @FXML private ProgressBar siltProgress;
    @FXML private Label clayValue;
    @FXML private ProgressBar clayProgress;

    // Matière organique
    @FXML private Label organicCarbonValue;
    @FXML private Label organicStatus;

    // Recommandations
    @FXML private Label recommendationCrop;
    @FXML private Label textureRecommendation;
    @FXML private TextArea fertilizationAdviceArea;

    // Boutons (optionnel dans FXML)
    @FXML private Button fullDiagnosticButton;
    @FXML private Button exportPdfButton;

    private final SoilService soilService;
    private SoilData currentSoilData;

    public SoilDashboardController() {
        this.soilService = new SoilService();
    }

    @FXML
    public void initialize() {
        logger.info("📊 Initialisation SoilDashboardController");
        // Raccrocher actions
        if (refreshSoilButton != null) refreshSoilButton.setOnAction(e -> loadSoilData());
        if (fullDiagnosticButton != null) fullDiagnosticButton.setOnAction(e -> showFullDiagnostic());
        if (exportPdfButton != null) exportPdfButton.setOnAction(e -> exportToPdf());

        // Charger les données au démarrage
        loadSoilData();
    }

    private void loadSoilData() {
        logger.info("🪴 Chargement des données du sol...");
        updateStatus("Chargement...", "#cccccc");

        new Thread(() -> {
            try {
                double latitude = 36.737;
                double longitude = 3.087;

                SoilData soilData = soilService.getSoilData(latitude, longitude);
                if (soilData != null) {
                    currentSoilData = soilData;
                    Platform.runLater(() -> {
                        displaySoilData(soilData);
                        updateStatus("Données chargées", "#5cb85c");
                    });
                } else {
                    Platform.runLater(() -> {
                        showError("Impossible de charger les données du sol");
                        updateStatus("Erreur", "#d9534f");
                    });
                }
            } catch (Exception e) {
                logger.severe("❌ Erreur: " + e.getMessage());
                Platform.runLater(() -> {
                    showError("Erreur lors du chargement: " + e.getMessage());
                    updateStatus("Erreur", "#d9534f");
                });
            }
        }, "SoilLoader").start();
    }

    private void displaySoilData(SoilData soil) {
        logger.info("📊 Affichage des données du sol");

        if (phValue != null) phValue.setText(String.format("%.1f", soil.getPh()));
        if (phCategory != null) phCategory.setText(getPhCategory(soil.getPh()));

        if (nitrogenValue != null) displayNitrogen(soil);
        if (phosphorusValue != null) displayPhosphorus(soil);
        if (potassiumValue != null) displayPotassium(soil);

        if (sandValue != null) sandValue.setText(String.format("%.0f%%", soil.getSand()));
        if (sandProgress != null) sandProgress.setProgress(soil.getSand() / 100.0);

        if (siltValue != null) siltValue.setText(String.format("%.0f%%", soil.getSilt()));
        if (siltProgress != null) siltProgress.setProgress(soil.getSilt() / 100.0);

        if (clayValue != null) clayValue.setText(String.format("%.0f%%", soil.getClay()));
        if (clayProgress != null) clayProgress.setProgress(soil.getClay() / 100.0);

        if (organicCarbonValue != null) organicCarbonValue.setText(String.format("%.1f%%", soil.getOrganicCarbon()));
        if (organicStatus != null) {
            organicStatus.setText(getOrganicStatus(soil.getOrganicCarbon()));
            updateLabelColor(organicStatus, soil.getOrganicCarbon() >= 2.5);
        }

        if (recommendationCrop != null) recommendationCrop.setText(soil.getRecommendedCrop());
        if (textureRecommendation != null) textureRecommendation.setText(soil.getTextureQuality());
        if (fertilizationAdviceArea != null) fertilizationAdviceArea.setText(soil.getFertilizationAdvice());

        animateDisplay();
    }

    private void displayNitrogen(SoilData soil) {
        nitrogenValue.setText(String.format("%.0f mg/kg", soil.getNitrogen()));
        if (soil.getNitrogen() < 100) {
            nitrogenStatus.setText("⚠️ Faible"); updateLabelColor(nitrogenStatus, false);
        } else if (soil.getNitrogen() < 150) {
            nitrogenStatus.setText("✓ Normal"); updateLabelColor(nitrogenStatus, true);
        } else {
            nitrogenStatus.setText("✅ Bon"); updateLabelColor(nitrogenStatus, true);
        }
    }

    private void displayPhosphorus(SoilData soil) {
        phosphorusValue.setText(String.format("%.0f mg/kg", soil.getPhosphorus()));
        if (soil.getPhosphorus() < 10) {
            phosphorusStatus.setText("⚠️ Très faible"); updateLabelColor(phosphorusStatus, false);
        } else if (soil.getPhosphorus() < 15) {
            phosphorusStatus.setText("✓ Acceptable"); updateLabelColor(phosphorusStatus, true);
        } else {
            phosphorusStatus.setText("✅ Bon"); updateLabelColor(phosphorusStatus, true);
        }
    }

    private void displayPotassium(SoilData soil) {
        potassiumValue.setText(String.format("%.0f mg/kg", soil.getPotassium()));
        if (soil.getPotassium() < 150) {
            potassiumStatus.setText("⚠️ Faible"); updateLabelColor(potassiumStatus, false);
        } else {
            potassiumStatus.setText("✅ Bon"); updateLabelColor(potassiumStatus, true);
        }
    }

    private String getPhCategory(double ph) {
        if (ph < 6) return "Acide";
        if (ph <= 7) return "Neutre";
        if (ph <= 8) return "Légèrement alcalin";
        return "Alcalin";
    }

    private String getOrganicStatus(double carbon) {
        if (carbon < 2) return "⚠️ Faible";
        if (carbon < 3) return "✓ Acceptable";
        return "✅ Bon";
    }

    private void updateLabelColor(Label label, boolean isGood) {
        if (label == null) return;
        label.setStyle(isGood ? "-fx-text-fill: #5cb85c; -fx-font-weight: bold;" : "-fx-text-fill: #d9534f; -fx-font-weight: bold;");
    }

    private void animateDisplay() {
        if (soilDataGrid == null) return;
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), soilDataGrid);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void updateStatus(String message, String color) {
        if (soilStatusLabel != null) soilStatusLabel.setText(message);
        if (soilStatusCircle != null) soilStatusCircle.setFill(Color.web(color));
    }

    private void showFullDiagnostic() {
        if (currentSoilData != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🪴 Diagnostic Complet du Sol");
            alert.setHeaderText("Analyse Détaillée");
            alert.setContentText(currentSoilData.getDiagnostic());
            alert.getDialogPane().setPrefWidth(600);
            alert.showAndWait();
        } else {
            showError("Aucune donnée disponible");
        }
    }

    private void exportToPdf() {
        showError("Export PDF non implémenté");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ Erreur");
        alert.setHeaderText("Erreur Soil API");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

