package org.example.pidev.controllers.recoltes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.pidev.services.recoltes.RecolteService;
import org.example.pidev.services.recoltes.RendementService;

/**
 * Contrôleur pour la barre de totaux dynamiques
 * Affiche le nombre total de récoltes et rendements
 */
public class TotalsBarController {

    @FXML
    private VBox totalsContainer;

    @FXML
    private VBox recolteContainer;

    @FXML
    private VBox rendementContainer;

    @FXML
    private Label totalRecoltesValue;

    @FXML
    private Label totalRendementsValue;

    private RecolteService recolteService;
    private RendementService rendementService;

    public TotalsBarController() {
        this.recolteService = new RecolteService();
        this.rendementService = new RendementService();
    }

    @FXML
    public void initialize() {
        updateTotals();
    }

    /**
     * Met à jour les totaux affichés
     */
    public void updateTotals() {
        // Récupérer les totaux
        int totalRecoltes = getTotalRecoltes();
        int totalRendements = getTotalRendements();

        // Mettre à jour les labels
        totalRecoltesValue.setText(String.valueOf(totalRecoltes));
        totalRendementsValue.setText(String.valueOf(totalRendements));
    }

    /**
     * Obtient le nombre total de récoltes
     */
    private int getTotalRecoltes() {
        try {
            return recolteService.getAll().size();
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du calcul du total des récoltes: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Obtient le nombre total de rendements
     */
    private int getTotalRendements() {
        try {
            return rendementService.getAll().size();
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du calcul du total des rendements: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Change le style du sidebar pour Recolte (vert)
     */
    public void setSidebarRecolte(VBox sidebar) {
        sidebar.getStyleClass().clear();
        sidebar.getStyleClass().addAll("sidebar", "sidebar-recolte");
    }

    /**
     * Change le style du sidebar pour Rendement (marron)
     */
    public void setSidebarRendement(VBox sidebar) {
        sidebar.getStyleClass().clear();
        sidebar.getStyleClass().addAll("sidebar", "sidebar-rendement");
    }

    /**
     * Crée un conteneur de total pour Recolte
     */
    public VBox createRecolteContainer(int total) {
        VBox container = new VBox();
        container.getStyleClass().addAll("total-container", "total-container-recolte");
        container.setStyle("-fx-padding: 20; -fx-spacing: 10;");

        // Header avec icône
        HBox header = new HBox();
        header.getStyleClass().add("total-header");

        Label icon = new Label("🌾");
        icon.getStyleClass().add("total-icon");
        icon.setStyle("-fx-font-size: 24px;");

        Label label = new Label("Total Récoltes");
        label.getStyleClass().add("total-label");

        header.getChildren().addAll(icon, label);

        // Valeur
        Label value = new Label(String.valueOf(total));
        value.getStyleClass().add("total-value");

        // Subtitle
        Label subtitle = new Label("récoltes enregistrées");
        subtitle.getStyleClass().add("total-subtitle");

        container.getChildren().addAll(header, value, subtitle);
        return container;
    }

    /**
     * Crée un conteneur de total pour Rendement
     */
    public VBox createRendementContainer(int total) {
        VBox container = new VBox();
        container.getStyleClass().addAll("total-container", "total-container-rendement");
        container.setStyle("-fx-padding: 20; -fx-spacing: 10;");

        // Header avec icône
        HBox header = new HBox();
        header.getStyleClass().add("total-header");

        Label icon = new Label("📊");
        icon.getStyleClass().add("total-icon");
        icon.setStyle("-fx-font-size: 24px;");

        Label label = new Label("Total Rendements");
        label.getStyleClass().add("total-label");

        header.getChildren().addAll(icon, label);

        // Valeur
        Label value = new Label(String.valueOf(total));
        value.getStyleClass().add("total-value");

        // Subtitle
        Label subtitle = new Label("rendements enregistrés");
        subtitle.getStyleClass().add("total-subtitle");

        container.getChildren().addAll(header, value, subtitle);
        return container;
    }
}

