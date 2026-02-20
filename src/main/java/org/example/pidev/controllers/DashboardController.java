package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.pidev.services.RecolteService;
import org.example.pidev.services.RendementService;

/**
 * Contrôleur principal du Dashboard
 * Gère la navigation entre les différentes sections et affiche les statistiques
 */
public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox sidebarBox;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnRecoltes;

    @FXML
    private Button btnRendements;

    @FXML
    private Button btnPredictions;

    @FXML
    private Label lblTotalRecoltes;

    @FXML
    private Label lblTotalRendements;

    @FXML
    private Label lblSurfaceTotale;

    private RecolteService recolteService;
    private RendementService rendementService;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("✅ DashboardController initialisé");

        try {
            // Initialiser les services
            recolteService = new RecolteService();
            rendementService = new RendementService();

            // Charger le dashboard par défaut (qui chargera aussi les statistiques)
            loadDashboardView();

            // Configurer les boutons de navigation
            setupNavigationButtons();

            // Safety: apply a default sidebar mode if sidebarBox is present
            applySidebarMode("default");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            showErrorMessage("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    /**
     * Charge les statistiques depuis la base de données
     */
    private void loadStatistics() {
        try {
            System.out.println("📊 Chargement des statistiques...");

            // Total récoltes
            int totalRecoltes = recolteService.getAll().size();
            lblTotalRecoltes.setText(String.valueOf(totalRecoltes));
            System.out.println("  ✓ Total récoltes: " + totalRecoltes);

            // Total rendements
            int totalRendements = rendementService.getAll().size();
            lblTotalRendements.setText(String.valueOf(totalRendements));
            System.out.println("  ✓ Total rendements: " + totalRendements);

            // Surface totale (somme des surfaces de tous les rendements)
            double surfaceTotale = rendementService.getAll().stream()
                    .mapToDouble(r -> r.getSurfaceExploitee())
                    .sum();
            lblSurfaceTotale.setText(String.format("%.2f ha", surfaceTotale));
            System.out.println("  ✓ Surface totale: " + String.format("%.2f", surfaceTotale) + " ha");

            System.out.println("✅ Statistiques chargées avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des statistiques: " + e.getMessage());
            lblTotalRecoltes.setText("--");
            lblTotalRendements.setText("--");
            lblSurfaceTotale.setText("--");
        }
    }

    /**
     * Configure les boutons de navigation du sidebar
     */
    private void setupNavigationButtons() {
        if (btnDashboard != null) {
            btnDashboard.setOnAction(event -> {
                loadDashboardView();
                applySidebarMode("default");
                setActiveButton(btnDashboard);
            });
        } else {
            System.err.println("⚠️ btnDashboard n'est pas injecté (fx:id manquant?)");
        }

        if (btnRecoltes != null) {
            btnRecoltes.setOnAction(event -> {
                loadRecolteView();
                applySidebarMode("recolte");
                setActiveButton(btnRecoltes);
            });
        } else {
            System.err.println("⚠️ btnRecoltes n'est pas injecté (fx:id manquant?)");
        }

        if (btnRendements != null) {
            btnRendements.setOnAction(event -> {
                loadRendementView();
                applySidebarMode("rendement");
                setActiveButton(btnRendements);
            });
        } else {
            System.err.println("⚠️ btnRendements n'est pas injecté (fx:id manquant?)");
        }

        if (btnPredictions != null) {
            btnPredictions.setOnAction(event -> {
                loadPredictionsView();
                applySidebarMode("default");
                setActiveButton(btnPredictions);
            });
        } else {
            System.err.println("⚠️ btnPredictions n'est pas injecté (fx:id manquant?)");
        }
    }

    /**
     * Charge la vue Dashboard
     */
    private void loadDashboardView() {
        try {
            System.out.println("📊 Chargement du Dashboard");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardDefault.fxml"));
            Parent view = loader.load();
            if (rootPane != null) {
                rootPane.setCenter(view);
            } else {
                System.err.println("⚠️ rootPane est null - vérifiez le FXML principal");
            }

            // Récupérer le controller de DashboardDefault.fxml et mettre à jour les stats via son API
            try {
                Object maybeController = loader.getController();
                if (maybeController instanceof DashboardDefaultController) {
                    DashboardDefaultController dController = (DashboardDefaultController) maybeController;

                    if (recolteService == null) recolteService = new RecolteService();
                    if (rendementService == null) rendementService = new RendementService();

                    int totalRecoltes = recolteService.getAll().size();
                    int totalRendements = rendementService.getAll().size();
                    double surfaceTotale = rendementService.getAll().stream().mapToDouble(r -> r.getSurfaceExploitee()).sum();

                    dController.updateStats(totalRecoltes, totalRendements, surfaceTotale);
                } else {
                    System.err.println("⚠️ Controller de DashboardDefault.fxml non disponible ou de type inattendu");
                }
            } catch (Exception e) {
                System.err.println("❌ Impossible de mettre à jour les statistiques via le controller inclus: " + e.getMessage());
            }

            setActiveButton(btnDashboard);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du Dashboard: " + e.getMessage());
            showErrorMessage("Erreur lors du chargement du Dashboard: " + e.getMessage());
            loadDefaultDashboard();
        }
    }

    /**
     * Charge la vue par défaut du dashboard
     */
    private void loadDefaultDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardDefault.fxml"));
            Parent view = loader.load();
            if (rootPane != null) rootPane.setCenter(view);
        } catch (Exception e) {
            System.err.println("⚠️ Dashboard par défaut non disponible: " + e.getMessage());
            showErrorMessage("Dashboard par défaut non disponible: " + e.getMessage());
        }
    }

    /**
     * Charge la vue Récoltes
     */
    private void loadRecolteView() {
        try {
            System.out.println("🌾 Chargement de la liste des Récoltes");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Recolte.fxml"));
            Parent view = loader.load();
            if (rootPane != null) rootPane.setCenter(view);

            setActiveButton(btnRecoltes);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de Recolte.fxml: " + e.getMessage());
            showErrorMessage("Erreur lors du chargement des récoltes: " + e.getMessage());
        }
    }

    /**
     * Charge la vue Rendements
     */
    private void loadRendementView() {
        try {
            System.out.println("📈 Chargement de la liste des Rendements");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Rendement.fxml"));
            Parent view = loader.load();
            if (rootPane != null) rootPane.setCenter(view);

            setActiveButton(btnRendements);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de Rendement.fxml: " + e.getMessage());
            showErrorMessage("Erreur lors du chargement des rendements: " + e.getMessage());
        }
    }

    /**
     * Charge la vue Prédictions
     */
    private void loadPredictionsView() {
        try {
            System.out.println("🤖 Chargement des Prédictions");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Predictions.fxml"));
            Parent view = loader.load();
            if (rootPane != null) rootPane.setCenter(view);

            setActiveButton(btnPredictions);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de Predictions.fxml: " + e.getMessage());
            showErrorMessage("Erreur lors du chargement des prédictions: " + e.getMessage());
        }
    }

    /**
     * Applique le mode du sidebar: 'recolte' => vert, 'rendement' => marron, 'default' => neutre
     */
    private void applySidebarMode(String mode) {
        if (sidebarBox == null) {
            System.err.println("⚠️ sidebarBox est null — la VBox du sidebar n'a pas été injectée (fx:id='sidebarBox')");
            return;
        }

        sidebarBox.getStyleClass().removeAll("sidebar-recolte", "sidebar-rendement");

        switch (mode) {
            case "recolte":
                sidebarBox.getStyleClass().add("sidebar-recolte");
                break;
            case "rendement":
                sidebarBox.getStyleClass().add("sidebar-rendement");
                break;
            default:
                break;
        }
    }

    /**
     * Définit le bouton actif (visuellement)
     */
    private void setActiveButton(Button activeButton) {
        if (btnDashboard != null) btnDashboard.getStyleClass().removeAll("sidebar-button-active");
        if (btnRecoltes != null) btnRecoltes.getStyleClass().removeAll("sidebar-button-active");
        if (btnRendements != null) btnRendements.getStyleClass().removeAll("sidebar-button-active");

        if (activeButton == null) return;

        if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showErrorMessage(String message) {
        System.err.println("⚠️ " + message);
    }
}
