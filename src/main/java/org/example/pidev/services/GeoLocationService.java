package org.example.pidev.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de géolocalisation utilisant l'API Nominatim (OpenStreetMap).
 * Permet la recherche d'adresses et la récupération des coordonnées GPS.
 *
 * API: https://nominatim.openstreetmap.org
 * Gratuit et open-source, pas de clé API nécessaire.
 */
public class GeoLocationService {

    private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search";
    private static final String NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse";
    private final HttpClient httpClient;

    public GeoLocationService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Représente un résultat de recherche de localisation.
     */
    public static class LocationResult {
        private final String displayName;
        private final double latitude;
        private final double longitude;
        private final String type;

        public LocationResult(String displayName, double latitude, double longitude, String type) {
            this.displayName = displayName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.type = type;
        }

        public String getDisplayName() { return displayName; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getType() { return type; }

        /**
         * Retourne un nom court (ville, pays) au lieu de l'adresse complète.
         */
        public String getShortName() {
            String[] parts = displayName.split(",");
            if (parts.length >= 2) {
                return parts[0].trim() + ", " + parts[parts.length - 1].trim();
            }
            return displayName;
        }

        /**
         * Retourne les coordonnées formatées.
         */
        public String getCoordinates() {
            return String.format("%.6f, %.6f", latitude, longitude);
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Recherche des localisations par nom (adresse, ville, etc.)
     *
     * @param query Le texte de recherche
     * @param limit Nombre maximum de résultats (max 10)
     * @return Liste de résultats de localisation
     */
    public List<LocationResult> searchLocation(String query, int limit) {
        List<LocationResult> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        try {
            String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&format=json&limit=%d&addressdetails=1",
                    NOMINATIM_SEARCH_URL, encodedQuery, Math.min(limit, 10));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartFarm-JavaFX/1.0")
                    .header("Accept-Language", "fr")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String displayName = obj.getString("display_name");
                    double lat = obj.getDouble("lat");
                    double lon = obj.getDouble("lon");
                    String type = obj.optString("type", "unknown");

                    results.add(new LocationResult(displayName, lat, lon, type));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur GeoLocation API: " + e.getMessage());
        }

        return results;
    }

    /**
     * Recherche des localisations (avec limite par défaut de 5).
     */
    public List<LocationResult> searchLocation(String query) {
        return searchLocation(query, 5);
    }

    /**
     * Géocodage inverse : obtenir l'adresse à partir de coordonnées GPS.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Le résultat de localisation ou null si non trouvé
     */
    public LocationResult reverseGeocode(double latitude, double longitude) {
        try {
            String url = String.format("%s?lat=%.6f&lon=%.6f&format=json&addressdetails=1",
                    NOMINATIM_REVERSE_URL, latitude, longitude);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartFarm-JavaFX/1.0")
                    .header("Accept-Language", "fr")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject obj = new JSONObject(response.body());
                if (obj.has("display_name")) {
                    String displayName = obj.getString("display_name");
                    double lat = obj.getDouble("lat");
                    double lon = obj.getDouble("lon");
                    String type = obj.optString("type", "unknown");
                    return new LocationResult(displayName, lat, lon, type);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur Reverse Geocoding: " + e.getMessage());
        }
        return null;
    }

    /**
     * Génère l'URL d'une carte OpenStreetMap pour des coordonnées données.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @param zoom Niveau de zoom (1-18)
     * @return URL de la carte
     */
    public static String getMapUrl(double latitude, double longitude, int zoom) {
        return String.format("https://www.openstreetmap.org/#map=%d/%.6f/%.6f", zoom, latitude, longitude);
    }

    /**
     * Génère l'URL d'une image de carte statique via des tuiles OSM.
     */
    public static String getStaticMapUrl(double latitude, double longitude) {
        return String.format("https://staticmap.openstreetmap.de/staticmap.php?center=%.6f,%.6f&zoom=14&size=600x300&markers=%.6f,%.6f,red-pushpin",
                latitude, longitude, latitude, longitude);
    }

    /**
     * Génère le contenu HTML pour afficher une carte OpenStreetMap dans un WebView.
     */
    public static String getMapHtml(double latitude, double longitude, String locationName) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='utf-8'/>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'/>\n" +
                "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>\n" +
                "    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { width: 100%; height: 100vh; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id='map'></div>\n" +
                "    <script>\n" +
                "        var map = L.map('map').setView([" + latitude + ", " + longitude + "], 14);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; OpenStreetMap contributors'\n" +
                "        }).addTo(map);\n" +
                "        L.marker([" + latitude + ", " + longitude + "]).addTo(map)\n" +
                "            .bindPopup('<b>🌱 " + locationName.replace("'", "\\'") + "</b>')\n" +
                "            .openPopup();\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}

