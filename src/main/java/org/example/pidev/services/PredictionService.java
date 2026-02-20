package org.example.pidev.services;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;
import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de prédiction de rendement basé sur les données historiques
 * Utilise des méthodes statistiques simples pour prédire les rendements futurs
 */
public class PredictionService {

    private final RecolteService recolteService;
    private final RendementService rendementService;
    private final Connection connection;

    public PredictionService() {
        this.recolteService = new RecolteService();
        this.rendementService = new RendementService();
        this.connection = DBConnection.getConnection();
    }

    /**
     * Prédiction de la quantité de la prochaine récolte pour un type de culture
     * Utilise la moyenne des 3 dernières récoltes
     *
     * @param typeCulture Le type de culture (ex: "Olive", "Blé", etc.)
     * @return La quantité prédite, ou 0 si pas assez de données
     */
    public double predictNextHarvestQuantity(String typeCulture) {
        try {
            // Récupérer toutes les récoltes du type spécifié
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) {
                return 0;
            }

            // Prendre les 3 dernières récoltes maximum
            List<Recolte> lastThree = recoltes.stream()
                    .limit(3)
                    .collect(Collectors.toList());

            // Calculer la moyenne
            double average = lastThree.stream()
                    .mapToDouble(Recolte::getQuantite)
                    .average()
                    .orElse(0);

            return Math.round(average * 100.0) / 100.0; // Arrondir à 2 décimales
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la prédiction: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Prédiction du rendement (quantité par hectare) pour un type de culture
     *
     * @param typeCulture Le type de culture
     * @return La quantité prédite par hectare, ou 0 si pas assez de données
     */
    public double predictNextYieldPerHectare(String typeCulture) {
        try {
            // Récupérer les rendements et les récoltes correspondantes
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture() != null &&
                                 r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) {
                return 0;
            }

            // Calculer le rendement moyen
            List<Rendement> rendements = rendementService.getAll();
            double averageYield = recoltes.stream()
                    .mapToDouble(Recolte::getQuantite)
                    .average()
                    .orElse(0);

            // Diviser par la surface moyenne si disponible
            if (!rendements.isEmpty()) {
                double avgSurface = rendements.stream()
                        .mapToDouble(Rendement::getSurfaceExploitee)
                        .average()
                        .orElse(1);

                if (avgSurface > 0) {
                    averageYield = averageYield / avgSurface;
                }
            }

            return Math.round(averageYield * 100.0) / 100.0;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la prédiction de rendement: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Obtenir la tendance (augmentation, diminution, stable) pour un type de culture
     *
     * @param typeCulture Le type de culture
     * @return "📈 Augmentation", "📉 Diminution", ou "→ Stable"
     */
    public String getTrend(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte))
                    .collect(Collectors.toList());

            if (recoltes.size() < 2) {
                return "➖ Données insuffisantes";
            }

            // Prendre les 2 dernières récoltes
            double lastQuantity = recoltes.get(recoltes.size() - 1).getQuantite();
            double previousQuantity = recoltes.get(recoltes.size() - 2).getQuantite();

            double diff = lastQuantity - previousQuantity;
            double percentChange = (diff / previousQuantity) * 100;

            if (percentChange > 5) {
                return "📈 Augmentation (" + String.format("%.1f", percentChange) + "%)";
            } else if (percentChange < -5) {
                return "📉 Diminution (" + String.format("%.1f", percentChange) + "%)";
            } else {
                return "→ Stable";
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du calcul de la tendance: " + e.getMessage());
            return "❌ Erreur";
        }
    }

    /**
     * Obtenir les statistiques complètes pour tous les types de culture
     *
     * @return Map avec les prédictions pour chaque type de culture
     */
    public Map<String, PredictionData> getAllPredictions() {
        Map<String, PredictionData> predictions = new HashMap<>();

        try {
            // Récupérer tous les types de culture uniques
            Set<String> typeCultures = recolteService.getAll().stream()
                    .map(Recolte::getTypeCulture)
                    .collect(Collectors.toSet());

            for (String type : typeCultures) {
                PredictionData data = new PredictionData();
                data.setTypeCulture(type);
                data.setPredictedQuantity(predictNextHarvestQuantity(type));
                data.setPredictedYield(predictNextYieldPerHectare(type));
                data.setTrend(getTrend(type));

                // Nombre de récoltes enregistrées
                long recolteCount = recolteService.getAll().stream()
                        .filter(r -> r.getTypeCulture().equalsIgnoreCase(type))
                        .count();
                data.setHistoryCount((int) recolteCount);

                predictions.put(type, data);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des prédictions: " + e.getMessage());
        }

        return predictions;
    }

    /**
     * Classe interne pour stocker les données de prédiction
     */
    public static class PredictionData {
        private String typeCulture;
        private double predictedQuantity;
        private double predictedYield;
        private String trend;
        private int historyCount;

        // Getters et Setters
        public String getTypeCulture() { return typeCulture; }
        public void setTypeCulture(String typeCulture) { this.typeCulture = typeCulture; }

        public double getPredictedQuantity() { return predictedQuantity; }
        public void setPredictedQuantity(double predictedQuantity) { this.predictedQuantity = predictedQuantity; }

        public double getPredictedYield() { return predictedYield; }
        public void setPredictedYield(double predictedYield) { this.predictedYield = predictedYield; }

        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }

        public int getHistoryCount() { return historyCount; }
        public void setHistoryCount(int historyCount) { this.historyCount = historyCount; }

        @Override
        public String toString() {
            return "PredictionData{" +
                    "typeCulture='" + typeCulture + '\'' +
                    ", predictedQuantity=" + predictedQuantity +
                    ", predictedYield=" + predictedYield +
                    ", trend='" + trend + '\'' +
                    ", historyCount=" + historyCount +
                    '}';
        }
    }
}

