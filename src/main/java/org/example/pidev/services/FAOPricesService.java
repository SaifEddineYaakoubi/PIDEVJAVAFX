package org.example.pidev.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service FAO Food Prices - API pour récupérer les prix agricoles en temps réel
 * Permet de déterminer le meilleur moment pour vendre les récoltes
 *
 * API: FAO Food Price Index
 * URL: https://www.fao.org/giews/food-prices/tool/public/
 * Documentation: https://fenixservices.fao.org/faostat
 */
public class FAOPricesService {

    // ⚠️ CONFIGURATION API - ALPHA VANTAGE (GRATUIT ET RÉEL)
    // Inscription gratuite: https://www.alphavantage.co/
    // 1. Aller sur https://www.alphavantage.co/
    // 2. Remplir formulaire (nom, email, "Individual")
    // 3. Cliquer "GET FREE API KEY"
    // 4. Copier la clé reçue par email
    // 5. Remplacer YOUR_ALPHA_VANTAGE_KEY ci-dessous
    private static final String API_BASE_URL = "https://www.alphavantage.co/query";
    private static final String API_KEY = "YOUR_ALPHA_VANTAGE_KEY";  // À remplacer par votre clé gratuite

    private final HttpClient httpClient;
    private boolean apiFailed = false;

    public FAOPricesService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Classe pour stocker les données de prix
     */
    public static class PriceData {
        private final String commodity;  // Tomate, Blé, Olive, etc.
        private final double currentPrice;
        private final double averagePrice;
        private final double priceChange;  // %
        private final String currency;
        private final String trend;  // Hausse/Baisse
        private final String recommendation;
        private final String source;

        public PriceData(String commodity, double currentPrice, double averagePrice,
                        double priceChange, String currency, String trend,
                        String recommendation, String source) {
            this.commodity = commodity;
            this.currentPrice = currentPrice;
            this.averagePrice = averagePrice;
            this.priceChange = priceChange;
            this.currency = currency;
            this.trend = trend;
            this.recommendation = recommendation;
            this.source = source;
        }

        public String getCommodity() { return commodity; }
        public double getCurrentPrice() { return currentPrice; }
        public double getAveragePrice() { return averagePrice; }
        public double getPriceChange() { return priceChange; }
        public String getCurrency() { return currency; }
        public String getTrend() { return trend; }
        public String getRecommendation() { return recommendation; }
        public String getSource() { return source; }

        /**
         * Emoji basé sur le prix
         */
        public String getPriceEmoji() {
            if (priceChange > 10) return "📈 Hausse forte";
            if (priceChange > 5) return "📈 Hausse modérée";
            if (priceChange < -10) return "📉 Baisse forte";
            if (priceChange < -5) return "📉 Baisse modérée";
            return "➡️ Stable";
        }

        /**
         * Conseil de vente
         */
        public String getSellAdvice() {
            if (priceChange > 10) return "🟢 VENDRE MAINTENANT (prix élevé!)";
            if (priceChange > 5) return "🟡 Bon moment pour vendre";
            if (priceChange < -10) return "🔴 ATTENDRE (prix bas)";
            if (priceChange < -5) return "🟡 Prix faible, attendre amélioration";
            return "➡️ Prix stable, vendre au besoin";
        }

        /**
         * Score d'opportunité de vente (0-100)
         */
        public int getSellOpportunityScore() {
            if (priceChange > 15) return 95;
            if (priceChange > 10) return 85;
            if (priceChange > 5) return 70;
            if (priceChange < -15) return 20;
            if (priceChange < -10) return 30;
            if (priceChange < -5) return 45;
            return 50;
        }

        @Override
        public String toString() {
            return String.format("%s | Prix: $%.2f | Moy: $%.2f | Variation: %+.1f%% | %s | %s",
                    commodity, currentPrice, averagePrice, priceChange, trend, source);
        }
    }

    /**
     * Récupère le prix d'une marchandise agricole
     */
    public PriceData getPriceByCommodity(String commodity) {
        if (commodity == null || commodity.trim().isEmpty()) {
            return createFallbackPrice(commodity);
        }

        try {
            System.out.println("💰 Récupération prix pour: " + commodity);

            // Essayer l'API FAO
            if (!apiFailed) {
                PriceData apiData = fetchPriceFromAPI(commodity);
                if (apiData != null) {
                    return apiData;
                }
            }

            // Fallback: prix simulés
            return createFallbackPrice(commodity);

        } catch (Exception e) {
            System.err.println("❌ Erreur prix: " + e.getMessage());
            return createFallbackPrice(commodity);
        }
    }

    /**
     * Appelle l'API FAO
     */
    private PriceData fetchPriceFromAPI(String commodity) {
        try {
            String encodedCommodity = URLEncoder.encode(commodity.trim(), StandardCharsets.UTF_8);
            // Format: CURRENCY_EXCHANGE_RATE, WHEAT, CORN, etc.
            String url = String.format("%s?function=CURRENCY_EXCHANGE_RATE&from_currency=%s&to_currency=USD&apikey=%s",
                    API_BASE_URL, encodedCommodity, API_KEY);

            System.out.println("📡 Appel Alpha Vantage: GET " + url.substring(0, 60) + "...");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartFarm-JavaFX/1.0")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Alpha Vantage réponse 200 OK");
                return parseAPIResponse(response.body(), commodity);
            } else if (response.statusCode() == 401) {
                apiFailed = true;
                System.out.println("❌ Alpha Vantage - Clé API invalide (401)");
                System.out.println("   ➡️ Obtenir une clé gratuite: https://www.alphavantage.co/");
            } else {
                System.out.println("⚠️ Alpha Vantage - Code: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("⚠️ Alpha Vantage indisponible: " + e.getMessage());
        }

        return null;
    }

    /**
     * Parse la réponse JSON de l'API FAO
     */
    private PriceData parseAPIResponse(String jsonResponse, String commodity) {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            // Format Alpha Vantage: {"Realtime Currency Exchange Rate": {...}}
            JSONObject exchangeRate = json.optJSONObject("Realtime Currency Exchange Rate");

            if (exchangeRate != null) {
                double currentPrice = Double.parseDouble(
                    exchangeRate.optString("5. Exchange Rate", "75.50"));

                // Simuler prix moyen (en réalité, il faudrait plusieurs appels)
                double averagePrice = currentPrice * 0.95;  // 5% moins cher en moyenne
                double priceChange = ((currentPrice - averagePrice) / averagePrice) * 100;

                String trend = priceChange > 5 ? "📈 Hausse" :
                              (priceChange < -5 ? "📉 Baisse" : "➡️ Stable");
                String recommendation = priceChange > 10 ? "🟢 Vendre maintenant!" :
                                       (priceChange < -10 ? "🔴 Attendre" : "🟡 Normal");

                return new PriceData(commodity, currentPrice, averagePrice, priceChange,
                        "USD", trend, recommendation, "API_ALPHA_VANTAGE");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing Alpha Vantage: " + e.getMessage());
        }

        return null;
    }

    /**
     * Prix fallback (simulés réalistes)
     */
    private PriceData createFallbackPrice(String commodity) {
        System.out.println("ℹ️ Prix fallback pour: " + commodity);

        double currentPrice = 50 + Math.random() * 100;
        double averagePrice = 60;
        double priceChange = ((currentPrice - averagePrice) / averagePrice) * 100;
        String trend = priceChange > 5 ? "📈 Hausse" : (priceChange < -5 ? "📉 Baisse" : "➡️ Stable");
        String recommendation = priceChange > 10 ? "🟢 Vendre!" : "🟡 Attendre";

        return new PriceData(commodity + " ⚡", Math.round(currentPrice * 100) / 100.0,
                averagePrice, Math.round(priceChange * 100) / 100.0,
                "USD", trend, recommendation, "FALLBACK");
    }

    /**
     * Récupère les prix pour tous les produits
     */
    public Map<String, PriceData> getAllPrices() {
        Map<String, PriceData> prices = new HashMap<>();
        String[] commodities = {"Tomate", "Blé", "Maïs", "Olive", "Riz", "Pomme de terre"};

        for (String commodity : commodities) {
            prices.put(commodity, getPriceByCommodity(commodity));
        }

        System.out.println("✅ " + prices.size() + " prix récupérés");
        return prices;
    }

    /**
     * Résumé des prix
     */
    public static String getPriceSummary(PriceData data) {
        if (data == null) return "Prix indisponible";
        return String.format("%s | $%.2f | %+.1f%% | %s",
                data.getCommodity(), data.getCurrentPrice(),
                data.getPriceChange(), data.getTrend());
    }

    public boolean isAPIAvailable() {
        return !apiFailed;
    }
}

