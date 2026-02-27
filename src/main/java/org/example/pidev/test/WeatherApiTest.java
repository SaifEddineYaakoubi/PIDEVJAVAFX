package org.example.pidev.test;

import org.example.pidev.services.WeatherService;
import org.example.pidev.services.WeatherService.WeatherData;

/**
 * Test simple pour vérifier que la clé API OpenWeatherMap fonctionne.
 * Exécutez cette classe pour tester la connexion à l'API météo.
 */
public class WeatherApiTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST API OpenWeatherMap");
        System.out.println("========================================\n");

        WeatherService weatherService = new WeatherService();

        // --- Test 1 : Vérifier si la clé API est configurée ---
        System.out.println("--- Test 1 : Clé API configurée ---");
        boolean configured = weatherService.isApiKeyConfigured();
        System.out.println("Clé API configurée : " + (configured ? "✅ Oui" : "❌ Non"));
        System.out.println();

        // --- Test 2 : Météo par nom de ville (Tunis) ---
        System.out.println("--- Test 2 : Météo par ville (Tunis) ---");
        WeatherData tunis = weatherService.getWeatherByCity("Tunis");
        if (tunis != null) {
            if (tunis.getCityName().contains("⚡")) {
                System.out.println("⚠️ FALLBACK MODE - L'API n'a pas répondu, données simulées !");
                System.out.println("   ➡️ La clé API est probablement invalide ou pas encore activée.");
                System.out.println("   ➡️ Si la clé est nouvelle, attendez jusqu'à 2 heures.");
            } else {
                System.out.println("✅ API FONCTIONNE ! Données réelles reçues.");
            }
            System.out.println("   Ville      : " + tunis.getCityName());
            System.out.println("   Température : " + tunis.getFormattedTemp());
            System.out.println("   Ressenti    : " + String.format("%.1f°C", tunis.getFeelsLike()));
            System.out.println("   Min/Max     : " + String.format("%.1f°C / %.1f°C", tunis.getTempMin(), tunis.getTempMax()));
            System.out.println("   Humidité    : " + tunis.getHumidity() + "%");
            System.out.println("   Vent        : " + String.format("%.1f km/h", tunis.getWindSpeed()));
            System.out.println("   Description : " + tunis.getDescription());
            System.out.println("   Condition   : " + tunis.getMainCondition());
            System.out.println("   Pression    : " + tunis.getPressure() + " hPa");
            System.out.println("   Nuages      : " + tunis.getClouds() + "%");
            System.out.println("   Icône URL   : " + tunis.getIconUrl());
            System.out.println("   Emoji       : " + tunis.getWeatherEmoji());
            System.out.println("   Conseil     : " + tunis.getAgricultureAdvice());
            System.out.println("   Résumé      : " + tunis);
        } else {
            System.out.println("❌ Aucune donnée reçue pour Tunis !");
        }
        System.out.println();

        // --- Test 3 : Météo par ville (Paris) ---
        System.out.println("--- Test 3 : Météo par ville (Paris) ---");
        WeatherData paris = weatherService.getWeatherByCity("Paris");
        if (paris != null) {
            boolean isReal = !paris.getCityName().contains("⚡");
            System.out.println((isReal ? "✅" : "⚠️") + " " + paris);
        } else {
            System.out.println("❌ Aucune donnée pour Paris");
        }
        System.out.println();

        // --- Test 4 : Météo par coordonnées GPS (Tunis) ---
        System.out.println("--- Test 4 : Météo par coordonnées GPS (Tunis: 36.8065, 10.1815) ---");
        WeatherData coords = weatherService.getWeatherByCoordinates(36.8065, 10.1815);
        if (coords != null) {
            boolean isReal = !coords.getCityName().contains("⚡");
            System.out.println((isReal ? "✅" : "⚠️") + " " + coords);
        } else {
            System.out.println("❌ Aucune donnée pour les coordonnées");
        }
        System.out.println();

        // --- Test 5 : Météo par localisation texte ---
        System.out.println("--- Test 5 : Météo par localisation (Sousse, Tunisie) ---");
        WeatherData loc = weatherService.getWeatherByLocation("Sousse, Tunisie");
        if (loc != null) {
            boolean isReal = !loc.getCityName().contains("⚡");
            System.out.println((isReal ? "✅" : "⚠️") + " " + loc);
        } else {
            System.out.println("❌ Aucune donnée pour Sousse");
        }
        System.out.println();

        // --- Test 6 : Ville invalide ---
        System.out.println("--- Test 6 : Ville invalide (XYZVilleInconnue) ---");
        WeatherData invalid = weatherService.getWeatherByCity("XYZVilleInconnue12345");
        if (invalid != null) {
            System.out.println("⚠️ Données fallback retournées : " + invalid.getCityName());
        } else {
            System.out.println("✅ Null retourné pour ville invalide");
        }
        System.out.println();

        // --- Résultat final ---
        System.out.println("========================================");
        boolean apiWorks = tunis != null && !tunis.getCityName().contains("⚡");
        if (apiWorks) {
            System.out.println("✅✅ RÉSULTAT : L'API Weather fonctionne correctement !");
            System.out.println("   La clé API est valide et active.");
        } else {
            System.out.println("❌❌ RÉSULTAT : L'API Weather ne fonctionne PAS !");
            System.out.println("   Vérifiez votre clé API dans WeatherService.java");
            System.out.println("   Clé actuelle : vérifiez la constante API_KEY");
            System.out.println("   ➡️ https://home.openweathermap.org/api_keys");
        }
        System.out.println("========================================");
    }
}

