package org.example.pidev.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Service météo utilisant l'API OpenWeatherMap.
 * Permet de récupérer les données météo actuelles pour une localisation donnée.
 *
 * API: https://api.openweathermap.org
 * Clé API gratuite : inscription sur https://openweathermap.org/api
 * Plan gratuit : 1000 appels/jour
 */
public class WeatherService {

    // ⚠️ REMPLACEZ PAR VOTRE CLÉ API OpenWeatherMap gratuite
    // Inscription gratuite : https://home.openweathermap.org/users/sign_up
    // ⏳ Une nouvelle clé peut prendre jusqu'à 2 heures pour s'activer !
    private static final String API_KEY = "aebc05c29fd3fa4fc6f0a50cd99902b2";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final HttpClient httpClient;
    private boolean apiFailed = false;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Représente les données météo actuelles.
     */
    public static class WeatherData {
        private final String cityName;
        private final double temperature;
        private final double tempMin;
        private final double tempMax;
        private final double feelsLike;
        private final int humidity;
        private final double windSpeed;
        private final String description;
        private final String icon;
        private final String mainCondition;
        private final int pressure;
        private final int clouds;

        public WeatherData(String cityName, double temperature, double tempMin, double tempMax,
                           double feelsLike, int humidity, double windSpeed, String description,
                           String icon, String mainCondition, int pressure, int clouds) {
            this.cityName = cityName;
            this.temperature = temperature;
            this.tempMin = tempMin;
            this.tempMax = tempMax;
            this.feelsLike = feelsLike;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.description = description;
            this.icon = icon;
            this.mainCondition = mainCondition;
            this.pressure = pressure;
            this.clouds = clouds;
        }

        public String getCityName() { return cityName; }
        public double getTemperature() { return temperature; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public String getMainCondition() { return mainCondition; }
        public int getPressure() { return pressure; }
        public int getClouds() { return clouds; }

        /**
         * Retourne l'URL de l'icône météo.
         */
        public String getIconUrl() {
            return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        }

        /**
         * Retourne un emoji représentant la météo actuelle.
         */
        public String getWeatherEmoji() {
            if (mainCondition == null) return "🌡️";
            switch (mainCondition.toLowerCase()) {
                case "clear": return "☀️";
                case "clouds": return clouds < 50 ? "⛅" : "☁️";
                case "rain":
                case "drizzle": return "🌧️";
                case "thunderstorm": return "⛈️";
                case "snow": return "❄️";
                case "mist":
                case "fog":
                case "haze": return "🌫️";
                default: return "🌡️";
            }
        }

        /**
         * Retourne un conseil agricole basé sur la météo.
         */
        public String getAgricultureAdvice() {
            if (temperature > 35) return "🔥 Chaleur extrême - Arrosage intensif recommandé";
            if (temperature > 30) return "☀️ Temps chaud - Arrosez tôt le matin ou tard le soir";
            if (temperature < 0) return "❄️ Gel - Protégez vos cultures sensibles";
            if (temperature < 5) return "🥶 Froid - Risque de gel, couvrez les jeunes plants";
            if ("rain".equalsIgnoreCase(mainCondition) || "drizzle".equalsIgnoreCase(mainCondition))
                return "🌧️ Pluie - Pas besoin d'arroser, vérifiez le drainage";
            if ("thunderstorm".equalsIgnoreCase(mainCondition))
                return "⛈️ Orage - Protégez les cultures et équipements";
            if (humidity > 80) return "💧 Humidité élevée - Risque de maladies fongiques";
            if (humidity < 30) return "🏜️ Air sec - Augmentez l'arrosage";
            if (windSpeed > 40) return "💨 Vent fort - Protégez les cultures fragiles";
            return "✅ Conditions favorables pour l'agriculture";
        }

        /**
         * Retourne la température formatée en °C.
         */
        public String getFormattedTemp() {
            return String.format("%.0f°C", temperature);
        }

        @Override
        public String toString() {
            return String.format("%s %s | %s | Temp: %.1f°C (Ressenti: %.1f°C) | Humidité: %d%% | Vent: %.1f km/h",
                    getWeatherEmoji(), cityName, description, temperature, feelsLike, humidity, windSpeed);
        }
    }

    /**
     * Récupère les données météo actuelles par nom de ville.
     *
     * @param cityName Nom de la ville
     * @return Les données météo ou null si erreur
     */
    public WeatherData getWeatherByCity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) return null;

        // Si l'API a déjà échoué (401), utiliser le mode fallback directement
        if (apiFailed) {
            return createFallbackWeather(cityName);
        }

        try {
            String encodedCity = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=fr",
                    BASE_URL, encodedCity, API_KEY);

            WeatherData data = fetchWeatherData(url);
            if (data != null) return data;

            // Si l'API retourne null (erreur 401/404/etc.), retourner des données fallback
            return createFallbackWeather(cityName);
        } catch (Exception e) {
            System.out.println("❌ Erreur Weather API (city): " + e.getMessage());
            return createFallbackWeather(cityName);
        }
    }

    /**
     * Récupère les données météo actuelles par coordonnées GPS.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Les données météo ou null si erreur
     */
    public WeatherData getWeatherByCoordinates(double latitude, double longitude) {
        if (apiFailed) {
            return createFallbackWeather("Lat:" + String.format("%.2f", latitude));
        }

        try {
            String url = String.format("%s?lat=%.6f&lon=%.6f&appid=%s&units=metric&lang=fr",
                    BASE_URL, latitude, longitude, API_KEY);

            WeatherData data = fetchWeatherData(url);
            if (data != null) return data;
            return createFallbackWeather("Lat:" + String.format("%.2f", latitude));
        } catch (Exception e) {
            System.out.println("❌ Erreur Weather API (coords): " + e.getMessage());
            return createFallbackWeather("Lat:" + String.format("%.2f", latitude));
        }
    }

    /**
     * Récupère les données météo à partir de la localisation (essaie d'extraire la ville).
     * Fonctionne avec un nom de ville ou une adresse complète.
     *
     * @param location Le texte de localisation
     * @return Les données météo ou null si erreur
     */
    public WeatherData getWeatherByLocation(String location) {
        if (location == null || location.trim().isEmpty()) return null;

        // Essayer d'abord avec la localisation telle quelle
        WeatherData data = getWeatherByCity(location);
        if (data != null) return data;

        // Si ça ne marche pas, essayer avec juste la première partie (ville)
        String[] parts = location.split(",");
        if (parts.length > 0) {
            data = getWeatherByCity(parts[0].trim());
            if (data != null) return data;
        }

        return null;
    }

    /**
     * Méthode interne pour récupérer et parser les données météo.
     */
    private WeatherData fetchWeatherData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartFarm-JavaFX/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                return parseWeatherData(json);
            } else if (response.statusCode() == 401) {
                apiFailed = true;
                System.out.println("❌ API météo - Clé API invalide (401). Vérifiez votre clé sur https://home.openweathermap.org/api_keys");
                System.out.println("   ➡️ Si la clé est nouvelle, attendez 2 heures pour son activation.");
                System.out.println("   ➡️ Mode fallback activé (données simulées).");
            } else if (response.statusCode() == 404) {
                System.out.println("⚠️ API météo - Ville non trouvée (404): " + response.body());
            } else {
                System.out.println("⚠️ API météo - Code: " + response.statusCode() + " | " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur fetch météo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parse la réponse JSON de l'API OpenWeatherMap.
     */
    private WeatherData parseWeatherData(JSONObject json) {
        try {
            String cityName = json.getString("name");

            JSONObject main = json.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");
            int pressure = main.getInt("pressure");

            JSONObject wind = json.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed") * 3.6; // Convertir m/s en km/h

            JSONArray weatherArray = json.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");
            String icon = weather.getString("icon");
            String mainCondition = weather.getString("main");

            JSONObject cloudsObj = json.optJSONObject("clouds");
            int clouds = cloudsObj != null ? cloudsObj.optInt("all", 0) : 0;

            // Première lettre en majuscule pour la description
            description = description.substring(0, 1).toUpperCase() + description.substring(1);

            return new WeatherData(cityName, temperature, tempMin, tempMax, feelsLike,
                    humidity, windSpeed, description, icon, mainCondition, pressure, clouds);
        } catch (Exception e) {
            System.out.println("❌ Erreur parsing météo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Vérifie si la clé API est configurée et fonctionne.
     */
    public boolean isApiKeyConfigured() {
        return !apiFailed && API_KEY != null && !API_KEY.trim().isEmpty();
    }

    /**
     * Crée des données météo simulées quand l'API n'est pas disponible.
     * Permet à l'interface de fonctionner même sans clé API valide.
     */
    private WeatherData createFallbackWeather(String location) {
        // Simuler des données météo réalistes basées sur la date
        java.time.LocalDate today = java.time.LocalDate.now();
        int month = today.getMonthValue();

        // Température simulée selon la saison (hémisphère nord / Tunisie)
        double temp;
        String desc;
        String mainCond;
        String icon;
        int humidity;
        double windSpeed;

        if (month >= 6 && month <= 8) { // Été
            temp = 28 + (Math.random() * 8); // 28-36°C
            desc = "Ciel dégagé (données simulées)";
            mainCond = "Clear";
            icon = "01d";
            humidity = 35 + (int)(Math.random() * 20);
            windSpeed = 5 + Math.random() * 15;
        } else if (month >= 12 || month <= 2) { // Hiver
            temp = 8 + (Math.random() * 8); // 8-16°C
            desc = "Partiellement nuageux (données simulées)";
            mainCond = "Clouds";
            icon = "03d";
            humidity = 60 + (int)(Math.random() * 25);
            windSpeed = 10 + Math.random() * 20;
        } else if (month >= 3 && month <= 5) { // Printemps
            temp = 16 + (Math.random() * 10); // 16-26°C
            desc = "Ensoleillé (données simulées)";
            mainCond = "Clear";
            icon = "02d";
            humidity = 45 + (int)(Math.random() * 20);
            windSpeed = 8 + Math.random() * 15;
        } else { // Automne
            temp = 18 + (Math.random() * 8); // 18-26°C
            desc = "Peu nuageux (données simulées)";
            mainCond = "Clouds";
            icon = "02d";
            humidity = 50 + (int)(Math.random() * 20);
            windSpeed = 7 + Math.random() * 18;
        }

        // Extraire un nom de ville simple
        String cityName = location;
        if (location.contains(",")) {
            cityName = location.split(",")[0].trim();
        }

        System.out.println("ℹ️ Mode fallback: données météo simulées pour " + cityName);

        return new WeatherData(
                cityName + " ⚡", // Indicateur que c'est simulé
                Math.round(temp * 10.0) / 10.0,
                Math.round((temp - 3) * 10.0) / 10.0,
                Math.round((temp + 3) * 10.0) / 10.0,
                Math.round((temp - 1) * 10.0) / 10.0,
                humidity,
                Math.round(windSpeed * 10.0) / 10.0,
                desc,
                icon,
                mainCond,
                1013,
                mainCond.equals("Clouds") ? 40 : 10
        );
    }

    /**
     * Retourne un résumé météo pour l'affichage dans l'interface.
     */
    public static String getWeatherSummary(WeatherData data) {
        if (data == null) return "Météo indisponible";
        return String.format("%s %s | %.0f°C | %s",
                data.getWeatherEmoji(), data.getCityName(), data.getTemperature(), data.getDescription());
    }
}

