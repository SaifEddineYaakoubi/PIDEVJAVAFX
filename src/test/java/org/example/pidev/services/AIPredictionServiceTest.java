package org.example.pidev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour AIPredictionService
 */
@DisplayName("AI Prediction Service Tests")
class AIPredictionServiceTest {

    private AIPredictionService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AIPredictionService();
    }

    @Test
    @DisplayName("Récupère une prédiction IA")
    void testGetAIPrediction() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Tomate", 500, 20);

        assertNotNull(prediction, "La prédiction ne doit pas être null");
        assertTrue(prediction.getCropType().contains("Tomate"), "Doit contenir Tomate");
        assertTrue(prediction.getConfidence() >= 0 && prediction.getConfidence() <= 100,
                "Confiance entre 0 et 100");
    }

    @Test
    @DisplayName("Calcule correctement la confiance")
    void testConfidenceScore() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Blé", 600, 30);

        double confidence = prediction.getConfidence();
        assertTrue(confidence >= 50, "Confiance minimale réaliste");
        assertTrue(confidence <= 100, "Confiance maximale 100");
    }

    @Test
    @DisplayName("Évalue le risque de maladie")
    void testDiseaseRiskAssessment() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Maïs", 400, 25);

        String risk = prediction.getDiseaseRisk();
        assertTrue(risk.equals("LOW") || risk.equals("MEDIUM") || risk.equals("HIGH"),
                "Risque doit être LOW, MEDIUM ou HIGH");
        assertNotNull(prediction.getDiseaseRiskEmoji(), "Emoji ne doit pas être null");
    }

    @Test
    @DisplayName("Fournit conseil d'irrigation")
    void testIrrigationAdvice() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Tomate", 500, 20);

        assertNotNull(prediction.getIrrigationAdvice(), "Conseil n'est pas null");
        assertFalse(prediction.getIrrigationAdvice().isEmpty(), "Conseil n'est pas vide");
    }

    @Test
    @DisplayName("Prédit le prix dans 30 jours")
    void testPriceProjection() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Olive", 300, 15);

        double priceProjection = prediction.getPriceProjection();
        assertTrue(priceProjection > 0, "Prix prédit positif");
        assertTrue(priceProjection <= 200, "Prix prédit réaliste");
    }

    @Test
    @DisplayName("Fournit date optimale de récolte")
    void testOptimalHarvestDate() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Riz", 400, 20);

        String date = prediction.getOptimalHarvestDate();
        assertNotNull(date, "Date ne doit pas être null");
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"), "Format date valide YYYY-MM-DD");
    }

    @Test
    @DisplayName("Génère qualité d'indicateur basée sur confiance")
    void testQualityIndicator() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Tomate", 500, 20);

        String quality = prediction.getQualityIndicator();
        assertNotNull(quality, "Qualité ne doit pas être null");
        assertTrue(quality.contains("⭐"), "Doit contenir étoile");
    }

    @Test
    @DisplayName("Fournit conseil global")
    void testGlobalAdvice() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Maïs", 500, 25);

        String advice = prediction.getGlobalAdvice();
        assertNotNull(advice, "Conseil n'est pas null");
        assertFalse(advice.isEmpty(), "Conseil n'est pas vide");
    }

    @Test
    @DisplayName("Récupère prédictions IA pour toutes cultures")
    void testGetAllAIPredictions() {
        Map<String, AIPredictionService.AIPrediction> predictions =
            aiService.getAllAIPredictions(30);

        assertNotNull(predictions, "Map ne doit pas être null");
        assertTrue(predictions.size() > 0, "Doit contenir au moins une prédiction");
        assertTrue(predictions.containsKey("Tomate"), "Doit contenir Tomate");
    }

    @Test
    @DisplayName("Bascule entre Azure et Google")
    void testProviderSwitching() {
        aiService.switchProvider("GOOGLE");
        assertEquals("GOOGLE", aiService.getActiveProvider(), "Doit basculer vers Google");

        aiService.switchProvider("AZURE");
        assertEquals("AZURE", aiService.getActiveProvider(), "Doit basculer vers Azure");
    }

    @Test
    @DisplayName("Gère les prédictions invalides")
    void testInvalidCropPrediction() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction(null, 500, 20);

        assertNotNull(prediction, "Doit retourner fallback");
        assertEquals("FALLBACK", prediction.getSource());
    }

    @Test
    @DisplayName("Spécifie le modèle ML utilisé")
    void testModelIdentification() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Tomate", 500, 20);

        assertNotNull(prediction.getModelUsed(), "Modèle ne doit pas être null");
        assertFalse(prediction.getModelUsed().isEmpty(), "Modèle ne doit pas être vide");
    }

    @Test
    @DisplayName("Fournit prédictions réalistes")
    void testRealisticPredictions() {
        AIPredictionService.AIPrediction prediction =
            aiService.getAIPrediction("Blé", 600, 30);

        // Vérifier que les prédictions sont réalistes
        assertTrue(prediction.getYieldPrediction() > 200, "Rendement réaliste minimal");
        assertTrue(prediction.getYieldPrediction() < 2000, "Rendement réaliste maximal");
        assertTrue(prediction.getConfidence() >= 50, "Confiance minimale");
    }
}

