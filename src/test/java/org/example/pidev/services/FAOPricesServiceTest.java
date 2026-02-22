package org.example.pidev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour FAOPricesService
 */
@DisplayName("FAO Prices Service Tests")
class FAOPricesServiceTest {

    private FAOPricesService faoService;

    @BeforeEach
    void setUp() {
        faoService = new FAOPricesService();
    }

    @Test
    @DisplayName("Récupère le prix d'une marchandise")
    void testGetPriceByCommodity() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Tomate");

        assertNotNull(price, "Le prix ne doit pas être null");
        assertTrue(price.getCommodity().contains("Tomate"), "Doit contenir Tomate");
        assertTrue(price.getCurrentPrice() > 0, "Le prix doit être positif");
        assertNotNull(price.getTrend(), "La tendance ne doit pas être null");
    }

    @Test
    @DisplayName("Retourne fallback pour marchandise invalide")
    void testGetPriceInvalidCommodity() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity(null);

        assertNotNull(price, "Doit retourner un fallback");
        assertEquals("FALLBACK", price.getSource());
    }

    @Test
    @DisplayName("Calcule correctement la variation de prix")
    void testPriceChangeCalculation() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Blé");

        assertNotNull(price.getPriceChange(), "La variation ne doit pas être null");
        assertTrue(price.getPriceChange() >= -100, "Variation réaliste");
    }

    @Test
    @DisplayName("Fournit conseil de vente approprié")
    void testSellAdvice() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Maïs");

        assertNotNull(price.getSellAdvice(), "Conseil ne doit pas être null");
        assertTrue(price.getSellAdvice().contains("🟢") ||
                   price.getSellAdvice().contains("🟡") ||
                   price.getSellAdvice().contains("🔴"),
                "Doit contenir un indicateur emoji");
    }

    @Test
    @DisplayName("Calcule score d'opportunité de vente")
    void testSellOpportunityScore() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Olive");

        int score = price.getSellOpportunityScore();
        assertTrue(score >= 0 && score <= 100, "Score entre 0 et 100");
    }

    @Test
    @DisplayName("Récupère tous les prix")
    void testGetAllPrices() {
        Map<String, FAOPricesService.PriceData> prices = faoService.getAllPrices();

        assertNotNull(prices, "Map de prix ne doit pas être null");
        assertTrue(prices.size() > 0, "Doit contenir au moins un prix");
        assertTrue(prices.containsKey("Tomate"), "Doit contenir Tomate");
    }

    @Test
    @DisplayName("Résumé des prix formaté correctement")
    void testGetPriceSummary() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Tomate");
        String summary = FAOPricesService.getPriceSummary(price);

        assertNotNull(summary, "Résumé ne doit pas être null");
        assertTrue(summary.contains("Tomate") || summary.contains("$"), "Format valide");
    }

    @Test
    @DisplayName("Gère l'indisponibilité de l'API")
    void testAPIUnavailability() {
        FAOPricesService.PriceData price = faoService.getPriceByCommodity("Riz");

        // Doit utiliser fallback si API indisponible
        assertNotNull(price, "Doit avoir fallback");
    }
}

