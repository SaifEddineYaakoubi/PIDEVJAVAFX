package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur pour la vue DashboardDefault.fxml (cartes de statistiques)
 */
public class DashboardDefaultController {

    @FXML
    private Label lblTotalRecoltes;

    @FXML
    private Label lblTotalRendements;

    @FXML
    private Label lblSurfaceTotale;

    public void updateStats(int totalRecoltes, int totalRendements, double surfaceTotale) {
        try {
            if (lblTotalRecoltes != null) lblTotalRecoltes.setText(String.valueOf(totalRecoltes));
            if (lblTotalRendements != null) lblTotalRendements.setText(String.valueOf(totalRendements));
            if (lblSurfaceTotale != null) lblSurfaceTotale.setText(String.format("%.2f ha", surfaceTotale));
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour des statistiques dans DashboardDefaultController: " + e.getMessage());
        }
    }
}

