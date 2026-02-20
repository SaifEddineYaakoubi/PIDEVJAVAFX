package org.example.pidev.services;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de recommandations d'optimisation basé sur les données historiques
 * Fournit des recommandations intelligentes pour améliorer les rendements
 */
public class OptimizationService {

    private final RecolteService recolteService;
    private final RendementService rendementService;
    private final PredictionService predictionService;

    public OptimizationService() {
        this.recolteService = new RecolteService();
        this.rendementService = new RendementService();
        this.predictionService = new PredictionService();
    }

    /**
     * Obtenir les recommandations pour améliorer la production
     *
     * @param typeCulture Le type de culture
     * @return Liste des recommandations
     */
    public List<Recommendation> getRecommendations(String typeCulture) {
        List<Recommendation> recommendations = new ArrayList<>();

        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) {
                return recommendations;
            }

            // Analyse de la qualité
            recommendations.addAll(analyzeQuality(recoltes, typeCulture));

            // Analyse de la productivité
            recommendations.addAll(analyzeProductivity(recoltes, typeCulture));

            // Analyse de la saisonnalité
            recommendations.addAll(analyzeSeasonality(recoltes, typeCulture));

            // Trier par priorité
            recommendations.sort(Comparator.comparingInt(Recommendation::getPriority));

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération des recommandations: " + e.getMessage());
        }

        return recommendations;
    }

    /**
     * Analyser la qualité des récoltes
     */
    private List<Recommendation> analyzeQuality(List<Recolte> recoltes, String typeCulture) {
        List<Recommendation> recommendations = new ArrayList<>();

        try {
            // Récupérer les récoltes par qualité
            Map<String, Long> qualityCount = recoltes.stream()
                    .collect(Collectors.groupingBy(Recolte::getQualite, Collectors.counting()));

            // Chercher la qualité la plus fréquente
            String mostFrequentQuality = qualityCount.entrySet().stream()
                    .max(Comparator.comparingLong(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse("");

            if (!mostFrequentQuality.toLowerCase().contains("excellente")) {
                Recommendation rec = new Recommendation();
                rec.setTitle("🎯 Améliorer la qualité des récoltes");
                rec.setDescription("Votre qualité moyenne est: " + mostFrequentQuality +
                        ". Cherchez à atteindre l'excellence.");
                rec.setImpact("Augmenter la valeur marchande");
                rec.setAction("Optimiser les pratiques agricoles et les conditions de stockage");
                rec.setPriority(1);
                rec.setType("QUALITY");
                recommendations.add(rec);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur analyse qualité: " + e.getMessage());
        }

        return recommendations;
    }

    /**
     * Analyser la productivité
     */
    private List<Recommendation> analyzeProductivity(List<Recolte> recoltes, String typeCulture) {
        List<Recommendation> recommendations = new ArrayList<>();

        try {
            double avgQuantity = recoltes.stream()
                    .mapToDouble(Recolte::getQuantite)
                    .average()
                    .orElse(0);

            // Comparer avec la prédiction
            double predicted = predictionService.predictNextHarvestQuantity(typeCulture);

            if (avgQuantity < predicted * 0.8) {
                Recommendation rec = new Recommendation();
                rec.setTitle("📦 Augmenter les rendements");
                rec.setDescription("Votre productivité est " +
                        String.format("%.0f", (1 - avgQuantity/predicted) * 100) +
                        "% sous les prédictions.");
                rec.setImpact("Augmenter de " +
                        String.format("%.0f", (predicted - avgQuantity)) + " kg en moyenne");
                rec.setAction("Investir en irrigation, engrais ou main-d'œuvre");
                rec.setPriority(1);
                rec.setType("PRODUCTIVITY");
                recommendations.add(rec);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur analyse productivité: " + e.getMessage());
        }

        return recommendations;
    }

    /**
     * Analyser la saisonnalité
     */
    private List<Recommendation> analyzeSeasonality(List<Recolte> recoltes, String typeCulture) {
        List<Recommendation> recommendations = new ArrayList<>();

        try {
            // Grouper par mois
            Map<Integer, Double> monthlyAvg = new HashMap<>();
            for (Recolte r : recoltes) {
                int month = r.getDateRecolte().getMonthValue();
                double qty = r.getQuantite();
                monthlyAvg.put(month, monthlyAvg.getOrDefault(month, 0.0) + qty);
            }

            // Normaliser
            monthlyAvg.replaceAll((k, v) -> v / recoltes.stream()
                    .filter(r -> r.getDateRecolte().getMonthValue() == k)
                    .count());

            // Trouver le meilleur et pire mois
            if (!monthlyAvg.isEmpty()) {
                int bestMonth = monthlyAvg.entrySet().stream()
                        .max(Comparator.comparingDouble(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .orElse(0);

                int worstMonth = monthlyAvg.entrySet().stream()
                        .min(Comparator.comparingDouble(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .orElse(0);

                String[] months = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

                Recommendation rec = new Recommendation();
                rec.setTitle("📅 Optimiser selon la saison");
                rec.setDescription("Le meilleur mois: " + months[bestMonth] +
                        ", le pire: " + months[worstMonth]);
                rec.setImpact("Réduire la variabilité saisonnière");
                rec.setAction("Planifier les cultures pour les périodes optimales");
                rec.setPriority(2);
                rec.setType("SEASONALITY");
                recommendations.add(rec);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur analyse saisonnalité: " + e.getMessage());
        }

        return recommendations;
    }

    /**
     * Classe interne pour les recommandations
     */
    public static class Recommendation {
        private String title;
        private String description;
        private String impact;
        private String action;
        private int priority; // 1 = haute, 2 = moyenne, 3 = basse
        private String type; // QUALITY, PRODUCTIVITY, SEASONALITY

        // Getters et Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getPriorityColor() {
            return switch(priority) {
                case 1 -> "#C62828"; // Rouge
                case 2 -> "#FF9800"; // Orange
                default -> "#2196F3"; // Bleu
            };
        }

        @Override
        public String toString() {
            return "Recommendation{" +
                    "title='" + title + '\'' +
                    ", priority=" + priority +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}

