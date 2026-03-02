package org.example.pidev.controllers.recoltes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.Session;

import java.util.function.Consumer;

/**
 * Contrôleur pour la vue DashboardDefault.fxml (page d'accueil)
 */
public class DashboardDefaultController {

    @FXML private Label lblTotalRecoltes;
    @FXML private Label lblTotalRendements;
    @FXML private Label lblSurfaceTotale;
    @FXML private Label lblTotalParcelles;
    @FXML private Label lblWelcome;
    @FXML private Label lblWelcomeSub;

    // Accès rapide cards
    @FXML private VBox cardRecoltes;
    @FXML private VBox cardRendements;
    @FXML private VBox cardParcelles;
    @FXML private VBox cardCultures;
    @FXML private VBox cardSol;
    @FXML private VBox cardPredictions;
    @FXML private VBox cardArchive;
    @FXML private VBox cardClients;
    @FXML private VBox cardVentes;

    /** Callback pour naviguer vers un module depuis l'accès rapide */
    private Consumer<String> onNavigate;

    public void setOnNavigate(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    @FXML
    public void initialize() {
        // Afficher le nom de l'utilisateur connecté
        Utilisateur user = Session.getCurrentUser();
        if (user != null && lblWelcome != null) {
            String nom = user.getPrenom() != null && !user.getPrenom().isEmpty()
                    ? user.getPrenom() : user.getNom();
            lblWelcome.setText("👋 Bienvenue, " + nom + " !");
            if (lblWelcomeSub != null) {
                lblWelcomeSub.setText("Connecté en tant que " + (user.getRole() != null ? user.getRole().name() : "Utilisateur"));
            }
        }

        // Setup quick access card click + hover
        setupCard(cardRecoltes, "recoltes", "#f0fdf4", "#dcfce7");
        setupCard(cardRendements, "rendements", "#fef3c7", "#fde68a");
        setupCard(cardParcelles, "parcelles", "#ede9fe", "#ddd6fe");
        setupCard(cardCultures, "cultures", "#ecfdf5", "#d1fae5");
        setupCard(cardSol, "sol", "#fff1f2", "#ffe4e6");
        setupCard(cardPredictions, "predictions", "#f0f9ff", "#dbeafe");
        setupCard(cardArchive, "archive", "#faf5ff", "#ede9fe");
        setupCard(cardClients, "clients", "#eff6ff", "#dbeafe");
        setupCard(cardVentes, "ventes", "#fef9c3", "#fef08a");
    }

    private void setupCard(VBox card, String target, String normalBg, String hoverBg) {
        if (card == null) return;

        card.setOnMouseClicked(e -> {
            if (onNavigate != null) {
                onNavigate.accept(target);
            }
        });

        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle()
                    .replace("-fx-background-color: " + normalBg, "-fx-background-color: " + hoverBg)
                    + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4); -fx-scale-x: 1.03; -fx-scale-y: 1.03;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle(card.getStyle()
                    .replace("-fx-background-color: " + hoverBg, "-fx-background-color: " + normalBg)
                    .replace(" -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4); -fx-scale-x: 1.03; -fx-scale-y: 1.03;", ""));
        });
    }

    public void updateStats(int totalRecoltes, int totalRendements, double surfaceTotale) {
        try {
            if (lblTotalRecoltes != null) lblTotalRecoltes.setText(String.valueOf(totalRecoltes));
            if (lblTotalRendements != null) lblTotalRendements.setText(String.valueOf(totalRendements));
            if (lblSurfaceTotale != null) lblSurfaceTotale.setText(String.format("%.2f ha", surfaceTotale));
        } catch (Exception e) {
            System.err.println("❌ Erreur mise à jour stats: " + e.getMessage());
        }
    }

    public void updateParcelles(int totalParcelles) {
        if (lblTotalParcelles != null) lblTotalParcelles.setText(String.valueOf(totalParcelles));
    }
}
