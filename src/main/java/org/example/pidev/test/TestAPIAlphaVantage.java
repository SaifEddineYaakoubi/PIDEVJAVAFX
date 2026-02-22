package org.example.pidev.test;

import org.example.pidev.services.FAOPricesService;

/**
 * Test simple pour vérifier si l'API Alpha Vantage fonctionne
 */
public class TestAPIAlphaVantage {

    public static void main(String[] args) {
        System.out.println("🧪 TEST API ALPHA VANTAGE");
        System.out.println("══════════════════════════════════════");

        FAOPricesService service = new FAOPricesService();

        // Test 1: Récupérer prix Tomate
        System.out.println("\n📊 TEST 1: Récupérer prix Tomate");
        FAOPricesService.PriceData tomatoPrice = service.getPriceByCommodity("Tomate");

        if (tomatoPrice != null) {
            System.out.println("✅ Données reçues:");
            System.out.println("   Commodity: " + tomatoPrice.getCommodity());
            System.out.println("   Prix: $" + tomatoPrice.getCurrentPrice());
            System.out.println("   Variation: " + String.format("%+.1f%%", tomatoPrice.getPriceChange()));
            System.out.println("   Tendance: " + tomatoPrice.getTrend());
            System.out.println("   Conseil: " + tomatoPrice.getSellAdvice());
            System.out.println("   Source: " + tomatoPrice.getSource());

            // Vérifier si c'est de l'API ou fallback
            if ("API_ALPHA_VANTAGE".equals(tomatoPrice.getSource())) {
                System.out.println("\n✅✅ API FONCTIONNE! (Données réelles d'Alpha Vantage)");
            } else if ("FALLBACK".equals(tomatoPrice.getSource())) {
                System.out.println("\n⚠️ Fallback activé (API indisponible, données simulées)");
            }
        } else {
            System.out.println("❌ Erreur: Aucune donnée reçue");
        }

        // Test 2: Récupérer tous les prix
        System.out.println("\n📊 TEST 2: Récupérer tous les prix");
        java.util.Map<String, FAOPricesService.PriceData> allPrices = service.getAllPrices();

        System.out.println("✅ " + allPrices.size() + " prix récupérés");

        int apiCount = 0;
        int fallbackCount = 0;

        for (FAOPricesService.PriceData price : allPrices.values()) {
            if ("API_ALPHA_VANTAGE".equals(price.getSource())) {
                apiCount++;
            } else if ("FALLBACK".equals(price.getSource())) {
                fallbackCount++;
            }
        }

        System.out.println("   - Données API: " + apiCount);
        System.out.println("   - Données Fallback: " + fallbackCount);

        // Test 3: Vérifier si API est disponible
        System.out.println("\n📊 TEST 3: Statut API");
        if (service.isAPIAvailable()) {
            System.out.println("✅ API Alpha Vantage: DISPONIBLE");
        } else {
            System.out.println("⚠️ API Alpha Vantage: INDISPONIBLE (fallback activé)");
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("✅ TESTS TERMINÉS");
    }
}

