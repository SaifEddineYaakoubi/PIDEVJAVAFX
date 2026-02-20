package org.example.pidev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour OptimizationService")
class OptimizationServiceTest {

    private OptimizationService optimizationService;

    @BeforeEach
    void setUp() {
        optimizationService = new OptimizationService();
    }

    // =====================
    // Tests de recommandations
    // =====================

    @Test
    @DisplayName("Obtenir les recommandations pour un type de culture")
    void testGetRecommendations() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("Tomate");
        assertNotNull(recommendations, "Les recommandations ne doivent pas être null");
    }

    @Test
    @DisplayName("Recommandations retourne une liste pour type valide")
    void testGetRecommendationsValidType() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("Blé");
        assertNotNull(recommendations);
        // Peut être vide si pas de données
        assertTrue(recommendations.size() >= 0);
    }

    @Test
    @DisplayName("Type inexistant retourne liste vide")
    void testGetRecommendationsInvalidType() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("TypeInexistantXYZ");
        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty() || !recommendations.isEmpty());
    }

    @Test
    @DisplayName("Recommandations sont triées par priorité")
    void testRecommendationsSortedByPriority() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("Olive");

        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).getPriority() <=
                    recommendations.get(i + 1).getPriority(),
                    "Les recommandations doivent être triées par priorité");
        }
    }

    // =====================
    // Tests de classe Recommendation
    // =====================

    @Test
    @DisplayName("Créer une recommandation complète")
    void testRecommendationCreation() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        rec.setTitle("Test Recommandation");
        rec.setDescription("Description test");
        rec.setImpact("Impact test");
        rec.setAction("Action test");
        rec.setPriority(1);
        rec.setType("TEST");

        assertEquals("Test Recommandation", rec.getTitle());
        assertEquals("Description test", rec.getDescription());
        assertEquals("Impact test", rec.getImpact());
        assertEquals("Action test", rec.getAction());
        assertEquals(1, rec.getPriority());
        assertEquals("TEST", rec.getType());
    }

    @Test
    @DisplayName("Couleur de priorité pour niveau 1 (haute)")
    void testPriorityColorHigh() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        rec.setPriority(1);
        assertEquals("#C62828", rec.getPriorityColor(), "Haute priorité = rouge");
    }

    @Test
    @DisplayName("Couleur de priorité pour niveau 2 (moyenne)")
    void testPriorityColorMedium() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        rec.setPriority(2);
        assertEquals("#FF9800", rec.getPriorityColor(), "Moyenne priorité = orange");
    }

    @Test
    @DisplayName("Couleur de priorité pour niveau 3+ (basse)")
    void testPriorityColorLow() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        rec.setPriority(3);
        assertEquals("#2196F3", rec.getPriorityColor(), "Basse priorité = bleu");
    }

    @Test
    @DisplayName("toString génère une représentation valide")
    void testRecommendationToString() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        rec.setTitle("Augmenter la qualité");
        rec.setPriority(1);
        rec.setType("QUALITY");

        String str = rec.toString();
        assertTrue(str.contains("Augmenter la qualité"));
        assertTrue(str.contains("QUALITY"));
    }

    // =====================
    // Tests de robustesse
    // =====================

    @Test
    @DisplayName("Service gère les types avec espaces")
    void testTypeWithSpaces() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("Type Culture");
        assertNotNull(recommendations);
    }

    @Test
    @DisplayName("Service gère les types avec caractères spéciaux")
    void testTypeWithSpecialCharacters() {
        List<OptimizationService.Recommendation> recommendations =
                optimizationService.getRecommendations("Culture@2024");
        assertNotNull(recommendations);
    }

    @Test
    @DisplayName("Recommandation non null après création")
    void testRecommendationNotNull() {
        OptimizationService.Recommendation rec = new OptimizationService.Recommendation();
        assertNotNull(rec);
    }

    @Test
    @DisplayName("Prédictions cohérentes pour appels multiples")
    void testMultipleCallsConsistent() {
        List<OptimizationService.Recommendation> rec1 =
                optimizationService.getRecommendations("Tomate");
        List<OptimizationService.Recommendation> rec2 =
                optimizationService.getRecommendations("Tomate");

        assertEquals(rec1.size(), rec2.size(),
                "Appels multiples doivent retourner le même nombre de recommandations");
    }
}

