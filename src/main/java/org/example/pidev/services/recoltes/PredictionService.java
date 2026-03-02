package org.example.pidev.services.recoltes;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de prédiction de rendement avec API externe
 * Architecture identique au WeatherService avec HttpClient et API KEY
 */
public class PredictionService {

    // ⚠️ API CONFIGURATION - Similaire au WeatherService
    private static final String API_BASE_URL = "https://serpapi.com/manage-api-key";
    private static final String API_KEY = "113a795152dd2519d16bb1e18ba520ec28b87138f89859a1793d57626d7b771a";  // À remplacer

    private final RecolteService recolteService;
    private final RendementService rendementService;
    private final HttpClient httpClient;
    private boolean apiFailed = false;

    public PredictionService() {
        this.recolteService = new RecolteService();
        this.rendementService = new RendementService();
        this.httpClient = HttpClient.newHttpClient();
    }

    public static class PredictionData {
        private final String typeCulture;
        private final double predictedQuantity;
        private final double predictedYield;
        private final String trend;
        private final int historyCount;
        private final String emoji;
        private final String recommendation;
        private final String source;

        public PredictionData(String typeCulture, double predictedQuantity, double predictedYield,
                              String trend, int historyCount, String emoji, String recommendation, String source) {
            this.typeCulture = typeCulture;
            this.predictedQuantity = predictedQuantity;
            this.predictedYield = predictedYield;
            this.trend = trend;
            this.historyCount = historyCount;
            this.emoji = emoji;
            this.recommendation = recommendation;
            this.source = source;
        }

        public String getTypeCulture() { return typeCulture; }
        public double getPredictedQuantity() { return predictedQuantity; }
        public double getPredictedYield() { return predictedYield; }
        public String getTrend() { return trend; }
        public int getHistoryCount() { return historyCount; }
        public String getEmoji() { return emoji; }
        public String getRecommendation() { return recommendation; }
        public String getSource() { return source; }

        public String getTrendEmoji() {
            if (trend.contains("Augmentation")) return "📈";
            if (trend.contains("Diminution")) return "📉";
            return "→";
        }

        public int getReliabilityScore() {
            if (historyCount < 2) return 10;
            if (historyCount < 4) return 40;
            if (historyCount < 7) return 70;
            return 95;
        }

        public String getReliabilityMessage() {
            int score = getReliabilityScore();
            if (score < 30) return "⚠️ Données insuffisantes";
            if (score < 70) return "✓ Modérément fiable";
            return "✓✓ Très fiable";
        }

        @Override
        public String toString() {
            return String.format("%s %s | %.0f kg | %.1f kg/ha | %s | %d%% | %s",
                    emoji, typeCulture, predictedQuantity, predictedYield, trend, getReliabilityScore(), source);
        }
    }

    public PredictionData getPredictionByCulture(String typeCulture) {
        if (typeCulture == null || typeCulture.trim().isEmpty()) {
            return createFallbackPrediction(typeCulture);
        }

        try {
            System.out.println("🔍 Prédiction pour: " + typeCulture);

            if (!apiFailed) {
                PredictionData apiData = fetchPredictionFromAPI(typeCulture);
                if (apiData != null) return apiData;
            }

            return predictFromLocalData(typeCulture);

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return createFallbackPrediction(typeCulture);
        }
    }

    private PredictionData fetchPredictionFromAPI(String typeCulture) {
        try {
            String encodedCulture = URLEncoder.encode(typeCulture.trim(), StandardCharsets.UTF_8);
            String url = String.format("%s/%s?apikey=%s", API_BASE_URL, encodedCulture, API_KEY);

            System.out.println("📡 Appel API: GET " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartFarm-JavaFX/1.0")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ API réponse 200 OK");
                return parseAPIResponse(response.body(), typeCulture);
            } else if (response.statusCode() == 401) {
                apiFailed = true;
                System.out.println("❌ API - Clé API invalide (401)");
                System.out.println("   Mode fallback: utilisation données locales");
            } else {
                System.out.println("⚠️ API - Code: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("⚠️ API indisponible: " + e.getMessage());
        }

        return null;
    }

    private PredictionData parseAPIResponse(String jsonResponse, String typeCulture) {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            double qty = json.optDouble("quantite_predite", 0);
            double yield = json.optDouble("rendement_predit", 0);
            String trend = json.optString("tendance", "→ Stable");
            int count = json.optInt("nombre_recoltes", 0);
            String rec = json.optString("recommandation", "✓ OK");

            return new PredictionData(typeCulture, qty, yield, trend, count,
                    getCultureEmoji(typeCulture), rec, "API");

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing: " + e.getMessage());
            return null;
        }
    }

    private PredictionData predictFromLocalData(String typeCulture) {
        System.out.println("📊 Données locales");

        double quantity = predictNextHarvestQuantity(typeCulture);
        double yield = predictNextYieldPerHectare(typeCulture);
        String trend = getTrend(typeCulture);
        int count = getHistoryCount(typeCulture);
        String emoji = getCultureEmoji(typeCulture);
        String recommendation = getRecommendation(typeCulture, quantity, trend);

        return new PredictionData(typeCulture, quantity, yield, trend, count, emoji, recommendation, "LOCAL");
    }

    public double predictNextHarvestQuantity(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) return 0;

            double avg = recoltes.stream().mapToDouble(Recolte::getQuantite).average().orElse(0);
            return Math.round(avg * 100.0) / 100.0;
        } catch (Exception e) {
            return 0;
        }
    }

    public double predictNextYieldPerHectare(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture() != null && r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            if (recoltes.isEmpty()) return 0;

            double avg = recoltes.stream().mapToDouble(Recolte::getQuantite).average().orElse(0);
            double surface = rendementService.getAll().stream().mapToDouble(Rendement::getSurfaceExploitee)
                    .average().orElse(1);

            if (surface > 0) avg = avg / surface;
            return Math.round(avg * 100.0) / 100.0;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getTrend(String typeCulture) {
        try {
            List<Recolte> recoltes = recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture))
                    .sorted(Comparator.comparing(Recolte::getDateRecolte))
                    .collect(Collectors.toList());

            if (recoltes.size() < 2) return "➖ Insuffisant";

            double last = recoltes.get(recoltes.size() - 1).getQuantite();
            double prev = recoltes.get(recoltes.size() - 2).getQuantite();
            double pct = ((last - prev) / prev) * 100;

            if (pct > 5) return "📈 Augmentation (" + String.format("%.1f", pct) + "%)";
            if (pct < -5) return "📉 Diminution (" + String.format("%.1f", pct) + "%)";
            return "→ Stable";
        } catch (Exception e) {
            return "❌ Erreur";
        }
    }

    private int getHistoryCount(String typeCulture) {
        try {
            return (int) recolteService.getAll().stream()
                    .filter(r -> r.getTypeCulture().equalsIgnoreCase(typeCulture)).count();
        } catch (Exception e) {
            return 0;
        }
    }

    private String getCultureEmoji(String type) {
        if (type == null) return "🌾";
        String t = type.toLowerCase();
        if (t.contains("tomate")) return "🍅";
        if (t.contains("blé")) return "🌾";
        if (t.contains("maïs")) return "🌽";
        if (t.contains("olive")) return "🫒";
        return "🌾";
    }

    private String getRecommendation(String type, double qty, String trend) {
        if (qty < 200) return "📉 Faible - Augmentez engrais";
        if (trend.contains("Diminution")) return "⚠️ Baisse - Vérifiez irrigation";
        if (qty > 500) return "✅ Bon - Maintenez pratiques";
        return "✓ Normal";
    }

    public Map<String, PredictionData> getAllPredictions() {
        Map<String, PredictionData> predictions = new HashMap<>();
        try {
            Set<String> types = recolteService.getAll().stream().map(Recolte::getTypeCulture).collect(Collectors.toSet());
            for (String type : types) {
                predictions.put(type, getPredictionByCulture(type));
            }
            System.out.println("✅ " + predictions.size() + " prédictions");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
        return predictions;
    }

    private PredictionData createFallbackPrediction(String typeCulture) {
        System.out.println("ℹ️ Fallback");
        double qty = 300 + Math.random() * 400;
        double yield = 15 + Math.random() * 20;
        return new PredictionData(typeCulture + " ⚡", Math.round(qty * 100) / 100.0,
                Math.round(yield * 100) / 100.0, "→ Stable (sim)", 0, getCultureEmoji(typeCulture),
                "⚠️ Ajoutez récoltes", "FALLBACK");
    }

    public static String getPredictionSummary(PredictionData data) {
        if (data == null) return "Indisponible";
        return String.format("%s %s | %.0f kg | %.1f kg/ha | %s",
                data.getEmoji(), data.getTypeCulture(), data.getPredictedQuantity(),
                data.getPredictedYield(), data.getTrend());
    }

    public boolean isAPIAvailable() {
        return !apiFailed;
    }

    public boolean isDataAvailable() {
        return recolteService.getAll().size() > 0;
    }
}

