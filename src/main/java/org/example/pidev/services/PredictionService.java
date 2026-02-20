package org.example.pidev.services;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de prédiction de rendement basé sur les données historiques
 * Utilise des méthodes statistiques simples pour prédire les rendements futurs
 *
 * Architecture similaire au WeatherService :
 * - Récupère les données depuis la DB (RecolteService/RendementService)
 * - Analyse et traite les données
 * - Retourne des objets structurés (PredictionData)
 * - Gère les erreurs avec fallback
 */
public class PredictionService {

    private final RecolteService recolteService;
    private final RendementService rendementService;
    private boolean dataFailed = false; // Fallback si données insuffisantes

    public PredictionService() {
        this.recolteService = new RecolteService();
        this.rendementService = new RendementService();
    }

    /**
     * Classe interne pour stocker les données de prédiction
     * Similaire à WeatherData du WeatherService
     */
    public static class PredictionData {
        private final String typeCulture;
        private final double predictedQuantity;
        private final double predictedYield;
        private final String trend;
        private final int historyCount;
        private final String emoji;
        private final String recommendation;

        public PredictionData(String typeCulture, double predictedQuantity, double predictedYield,
                            String trend, int historyCount, String emoji, String recommendation) {
            this.typeCulture = typeCulture;
            this.predictedQuantity = predictedQuantity;
            this.predictedYield = predictedYield;
            this.trend = trend;
            this.historyCount = historyCount;
            this.emoji = emoji;
            this.recommendation = recommendation;
        }

        // Getters
        public String getTypeCulture() { return typeCulture; }
        public double getPredictedQuantity() { return predictedQuantity; }
        public double getPredictedYield() { return predictedYield; }
        public String getTrend() { return trend; }
        public int getHistoryCount() { return historyCount; }
        public String getEmoji() { return emoji; }
        public String getRecommendation() { return recommendation; }

        /**
         * Retourne un emoji basé sur la tendance
         */
        public String getTrendEmoji() {
            if (trend.contains("Augmentation")) return "📈";
            if (trend.contains("Diminution")) return "📉";
            if (trend.contains("Stable")) return "→";
            return "❓";
        }

        /**
         * Indice de fiabilité basé sur l'historique
         */
        public int getReliabilityScore() {
            if (historyCount < 2) return 10;      // Très peu fiable
            if (historyCount < 4) return 40;      // Peu fiable
            if (historyCount < 7) return 70;      // Modérément fiable
            return 95;                             // Très fiable
        }

        /**
         * Message de fiabilité à afficher
         */
        public String getReliabilityMessage() {
            int score = getReliabilityScore();
            if (score < 30) return "⚠️ Données insuffisantes";
            if (score < 70) return "✓ Modérément fiable";
            return "✓✓ Très fiable";
        }

        @Override
        public String toString() {
            return String.format("%s %s | Quantité: %.0f kg | Rendement: %.1f kg/ha | %s | Fiabilité: %d%%",
                    emoji, typeCulture, predictedQuantity, predictedYield, trend, getReliabilityScore());
        }
    }

    /**
     * Récupère la prédiction complète pour un type de culture
     * Méthode principale du service
     *
     * @param typeCulture Le type de culture (ex: "Tomate")
     * @return Objet PredictionData avec toutes les informations
     */
    public PredictionData getPredictionByCulture(String typeCulture) {
        if (typeCulture == null || typeCulture.trim().isEmpty()) {
            return createFallbackPrediction(typeCulture);
        }

        try {
            System.out.println("🔍 Prédiction pour: " + typeCulture);

            // Prédiction quantité
            double quantity = predictNextHarvestQuantity(typeCulture);

            // Prédiction rendement
            double yield = predictNextYieldPerHectare(typeCulture);

            // Tendance
            String trend = getTrend(typeCulture);

            // Historique
            int count = getHistoryCount(typeCulture);

            // Emoji basé sur le type
            String emoji = getCultureEmoji(typeCulture);

            // Recommandation
            String recommendation = getRecommendation(typeCulture, quantity, trend);

            return new PredictionData(typeCulture, quantity, yield, trend, count, emoji, recommendation);

        } catch (Exception e) {
            System.err.println("❌ Erreur prédiction pour " + typeCulture + ": " + e.getMessage());
            return createFallbackPrediction(typeCulture);
        }
    }

    /**
     * Prédiction de la quantité de la prochaine récolte
     * Algorithme: Moyenne des 3 dernières récoltes
     */
    public double predictNextHarvestQuantity(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) {
                return 0;
            }

            double average = recoltes.stream()
                    .mapToDouble(Recolte::getQuantite)
                    .average()
                    .orElse(0);

            return Math.round(average * 100.0) / 100.0;
        } catch (Exception e) {
            System.err.println("❌ Erreur quantité: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Prédiction du rendement (quantité par hectare)
     */
    public double predictNextYieldPerHectare(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture() != null &&
                                 r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) {
                return 0;
            }

            List<Rendement> rendements = rendementService.getAll();
            double averageYield = recoltes.stream()
                    .mapToDouble(Recolte::getQuantite)
                    .average()
                    .orElse(0);

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
            System.err.println("❌ Erreur rendement: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Analyse de tendance (augmentation, diminution, stable)
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
            System.err.println("❌ Erreur tendance: " + e.getMessage());
            return "❌ Erreur";
        }
    }

    /**
     * Compte le nombre de récoltes enregistrées pour un type
     */
    private int getHistoryCount(String typeCulture) {
        try {
            return (int) recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retourne l'emoji basé sur le type de culture
     */
    private String getCultureEmoji(String typeCulture) {
        if (typeCulture == null) return "🌾";

        String type = typeCulture.toLowerCase();
        if (type.contains("tomate")) return "🍅";
        if (type.contains("blé")) return "🌾";
        if (type.contains("maïs")) return "🌽";
        if (type.contains("olive")) return "🫒";
        if (type.contains("riz")) return "🍚";
        if (type.contains("carotte")) return "🥕";
        if (type.contains("laitue")) return "🥬";
        if (type.contains("pomme")) return "🍎";

        return "🌾";
    }

    /**
     * Génère une recommandation basée sur les prédictions
     */
    private String getRecommendation(String typeCulture, double quantity, String trend) {
        StringBuilder recommendation = new StringBuilder();

        if (quantity < 200) {
            recommendation.append("📉 Productivité faible - Augmentez les engrais. ");
        }
        if (trend.contains("Diminution")) {
            recommendation.append("⚠️ Tendance baissière - Vérifiez l'irrigation. ");
        }
        if (quantity > 500) {
            recommendation.append("✅ Bonne productivité - Maintenir les pratiques actuelles. ");
        }

        return recommendation.toString().isEmpty() ? "✓ Conditions normales" : recommendation.toString();
    }

    /**
     * Obtient toutes les prédictions pour tous les types de culture
     * Similaire à getAllPredictions() précédent, mais retourne des PredictionData
     */
    public Map<String, PredictionData> getAllPredictions() {
        Map<String, PredictionData> predictions = new HashMap<>();

        try {
            Set<String> typeCultures = recolteService.getAll().stream()
                    .map(Recolte::getTypeCulture)
                    .collect(Collectors.toSet());

            for (String type : typeCultures) {
                predictions.put(type, getPredictionByCulture(type));
            }

            System.out.println("✅ " + predictions.size() + " prédictions générées");

        } catch (Exception e) {
            System.err.println("❌ Erreur prédictions globales: " + e.getMessage());
        }

        return predictions;
    }

    /**
     * Crée une prédiction fallback quand les données sont insuffisantes
     * Similaire au createFallbackWeather() du WeatherService
     */
    private PredictionData createFallbackPrediction(String typeCulture) {
        System.out.println("ℹ️ Prédiction fallback pour: " + typeCulture);

        // Données simulées réalistes
        double quantity = 300 + Math.random() * 400;  // 300-700 kg
        double yield = 15 + Math.random() * 20;       // 15-35 kg/ha
        String trend = "→ Stable (données simulées)";
        String emoji = getCultureEmoji(typeCulture);
        String recommendation = "⚠️ Ajoutez plus de récoltes pour des prédictions précises";

        return new PredictionData(
                typeCulture + " ⚡",  // Indicateur fallback
                Math.round(quantity * 100.0) / 100.0,
                Math.round(yield * 100.0) / 100.0,
                trend,
                0,  // Pas d'historique
                emoji,
                recommendation
        );
    }

    /**
     * Résumé des prédictions pour l'affichage
     */
    public static String getPredictionSummary(PredictionData data) {
        if (data == null) return "Prédiction indisponible";
        return String.format("%s %s | %.0f kg | %.1f kg/ha | %s",
                data.getEmoji(), data.getTypeCulture(),
                data.getPredictedQuantity(), data.getPredictedYield(),
                data.getTrend());
    }

    /**
     * Vérifie si les données sont disponibles
     */
    public boolean isDataAvailable() {
        return !dataFailed && recolteService.getAll().size() > 0;
    }
}

