package org.example.pidev.services;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour PredictionService")
class PredictionServiceTest {

    private PredictionService predictionService;

    @BeforeEach
    void setUp() {
        predictionService = new PredictionService();
    }

    // =====================
    // Tests de prédiction de quantité
    // =====================

    @Test
    @DisplayName("Prédiction de quantité avec données valides")
    void testPredictNextHarvestQuantity() {
        double prediction = predictionService.predictNextHarvestQuantity("Tomate");
        assertTrue(prediction >= 0, "La prédiction doit être positive ou zéro");
    }

    @Test
    @DisplayName("Prédiction de quantité pour type inexistant retourne 0")
    void testPredictNextHarvestQuantityTypeInexistant() {
        double prediction = predictionService.predictNextHarvestQuantity("TypeInexistant123");
        assertEquals(0, prediction, "Doit retourner 0 pour un type inexistant");
    }

    @Test
    @DisplayName("Prédiction de quantité retourne un nombre arrondi")
    void testPredictNextHarvestQuantityRounded() {
        double prediction = predictionService.predictNextHarvestQuantity("Tomate");
        // Vérifier que le nombre a maximum 2 décimales
        double rounded = Math.round(prediction * 100.0) / 100.0;
        assertEquals(rounded, prediction, "La prédiction doit être arrondie à 2 décimales");
    }

    // =====================
    // Tests de prédiction de rendement
    // =====================

    @Test
    @DisplayName("Prédiction de rendement par hectare")
    void testPredictNextYieldPerHectare() {
        double yieldPrediction = predictionService.predictNextYieldPerHectare("Blé");
        assertTrue(yieldPrediction >= 0, "Le rendement prédit doit être positif ou zéro");
    }

    @Test
    @DisplayName("Prédiction de rendement pour type inexistant retourne 0")
    void testPredictNextYieldPerHectareTypeInexistant() {
        double yieldPrediction = predictionService.predictNextYieldPerHectare("TypeInexistant456");
        assertEquals(0, yieldPrediction, "Doit retourner 0 pour un type inexistant");
    }

    // =====================
    // Tests de tendance
    // =====================

    @Test
    @DisplayName("Tendance retourne une chaîne non vide")
    void testGetTrendNotEmpty() {
        String trend = predictionService.getTrend("Olive");
        assertNotNull(trend);
        assertFalse(trend.isEmpty(), "La tendance ne doit pas être vide");
    }

    @Test
    @DisplayName("Tendance pour type inexistant retourne message d'insuffisance")
    void testGetTrendTypeInexistant() {
        String trend = predictionService.getTrend("TypeInexistant789");
        assertTrue(trend.contains("insuffisantes") || trend.contains("Erreur"),
                "Doit mentionner données insuffisantes ou erreur");
    }

    @Test
    @DisplayName("Tendance retourne un format valide")
    void testGetTrendFormat() {
        String trend = predictionService.getTrend("Tomate");
        // Doit contenir un symbole ou un texte reconnaissable
        assertTrue(
                trend.contains("📈") || trend.contains("📉") || trend.contains("→") ||
                trend.contains("➖") || trend.contains("Erreur") || trend.contains("insuffisantes"),
                "La tendance doit avoir un format valide avec emoji ou texte"
        );
    }

    // =====================
    // Tests de prédictions complètes
    // =====================

    @Test
    @DisplayName("getAllPredictions retourne une Map")
    void testGetAllPredictionsReturnMap() {
        Map<String, PredictionService.PredictionData> predictions =
                predictionService.getAllPredictions();
        assertNotNull(predictions, "La Map ne doit pas être null");
    }

    @Test
    @DisplayName("getAllPredictions retourne une Map non null")
    void testGetAllPredictionsNotNull() {
        Map<String, PredictionService.PredictionData> predictions =
                predictionService.getAllPredictions();
        assertNotNull(predictions);
    }

    @Test
    @DisplayName("Chaque PredictionData contient des informations valides")
    void testPredictionDataValidity() {
        Map<String, PredictionService.PredictionData> predictions =
                predictionService.getAllPredictions();

        for (PredictionService.PredictionData prediction : predictions.values()) {
            assertNotNull(prediction.getTypeCulture(), "Type de culture ne doit pas être null");
            assertNotNull(prediction.getTrend(), "Tendance ne doit pas être null");
            assertTrue(prediction.getPredictedQuantity() >= 0, "Quantité doit être positive");
            assertTrue(prediction.getPredictedYield() >= 0, "Rendement doit être positif");
            assertTrue(prediction.getHistoryCount() >= 0, "Historique doit être positif");
        }
    }

    // =====================
    // Tests de classe interne PredictionData
    // =====================

    @Test
    @DisplayName("PredictionData peut être créée et modifiée")
    void testPredictionDataCreation() {
        PredictionService.PredictionData data = new PredictionService.PredictionData();
        data.setTypeCulture("Tomate");
        data.setPredictedQuantity(500.0);
        data.setPredictedYield(25.5);
        data.setTrend("📈 Augmentation");
        data.setHistoryCount(10);

        assertEquals("Tomate", data.getTypeCulture());
        assertEquals(500.0, data.getPredictedQuantity());
        assertEquals(25.5, data.getPredictedYield());
        assertEquals("📈 Augmentation", data.getTrend());
        assertEquals(10, data.getHistoryCount());
    }

    @Test
    @DisplayName("PredictionData toString génère une représentation valide")
    void testPredictionDataToString() {
        PredictionService.PredictionData data = new PredictionService.PredictionData();
        data.setTypeCulture("Blé");
        data.setPredictedQuantity(1000.0);

        String str = data.toString();
        assertTrue(str.contains("Blé"), "toString doit contenir le type de culture");
        assertTrue(str.contains("1000"), "toString doit contenir la quantité");
    }

    // =====================
    // Tests d'intégration
    // =====================

    @Test
    @DisplayName("Service fonctionne correctement avec plusieurs appels")
    void testMultiplePredictionCalls() {
        double pred1 = predictionService.predictNextHarvestQuantity("Tomate");
        double pred2 = predictionService.predictNextHarvestQuantity("Tomate");

        // Les deux prédictions doivent être identiques
        assertEquals(pred1, pred2, "Appels multiples doivent retourner les mêmes résultats");
    }

    @Test
    @DisplayName("Service gère correctement les cas limites")
    void testEdgeCases() {
        // Type null ou vide
        double predNull = predictionService.predictNextHarvestQuantity("");
        assertEquals(0, predNull, "Doit retourner 0 pour string vide");

        // Symboles spéciaux
        double predSpecial = predictionService.predictNextHarvestQuantity("Type@#$%");
        assertEquals(0, predSpecial, "Doit retourner 0 pour symboles spéciaux");
    }
}

