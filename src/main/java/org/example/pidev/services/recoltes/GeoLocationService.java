package org.example.pidev.services.recoltes;

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
        String safeName = locationName.replace("'", "\\'").replace("\"", "&quot;");
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='utf-8'/>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'/>\n" +
                "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>\n" +
                "    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; }\n" +
                "        #map { width: 100%; height: 100vh; }\n" +
                "        .info-panel {\n" +
                "            position: absolute; top: 10px; left: 50px; z-index: 1000;\n" +
                "            background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);\n" +
                "            border-radius: 12px; padding: 10px 16px;\n" +
                "            box-shadow: 0 4px 20px rgba(0,0,0,0.15);\n" +
                "            font-size: 13px; font-weight: 600; color: #1a472a;\n" +
                "            display: flex; align-items: center; gap: 8px;\n" +
                "            border-left: 4px solid #2E7D32;\n" +
                "        }\n" +
                "        .info-panel .emoji { font-size: 18px; }\n" +
                "        .coords { font-size: 10px; color: #6b7280; font-weight: 400; }\n" +
                "        .layer-toggle {\n" +
                "            position: absolute; bottom: 25px; right: 10px; z-index: 1000;\n" +
                "            display: flex; flex-direction: column; gap: 6px;\n" +
                "        }\n" +
                "        .layer-btn {\n" +
                "            background: rgba(255,255,255,0.95); border: 2px solid transparent;\n" +
                "            border-radius: 10px; padding: 8px 12px; cursor: pointer;\n" +
                "            font-size: 12px; font-weight: 600; color: #374151;\n" +
                "            box-shadow: 0 2px 10px rgba(0,0,0,0.12); transition: all 0.2s;\n" +
                "            display: flex; align-items: center; gap: 6px;\n" +
                "        }\n" +
                "        .layer-btn:hover { transform: scale(1.05); box-shadow: 0 4px 15px rgba(0,0,0,0.2); }\n" +
                "        .layer-btn.active { border-color: #2E7D32; background: #f0fdf4; color: #166534; }\n" +
                "        .leaflet-popup-content-wrapper {\n" +
                "            border-radius: 14px !important;\n" +
                "            box-shadow: 0 8px 30px rgba(0,0,0,0.2) !important;\n" +
                "        }\n" +
                "        .leaflet-popup-content { margin: 0 !important; }\n" +
                "        .popup-content {\n" +
                "            padding: 14px 18px; text-align: center;\n" +
                "        }\n" +
                "        .popup-content .name {\n" +
                "            font-size: 15px; font-weight: 700; color: #1a472a;\n" +
                "            margin-bottom: 4px;\n" +
                "        }\n" +
                "        .popup-content .loc {\n" +
                "            font-size: 11px; color: #6b7280;\n" +
                "        }\n" +
                "        .popup-content .badge {\n" +
                "            display: inline-block; margin-top: 8px;\n" +
                "            background: linear-gradient(135deg, #2E7D32, #4CAF50);\n" +
                "            color: white; padding: 4px 12px; border-radius: 20px;\n" +
                "            font-size: 10px; font-weight: 600;\n" +
                "        }\n" +
                "        @keyframes pulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.15); } }\n" +
                "        .marker-pin {\n" +
                "            width: 36px; height: 36px; border-radius: 50% 50% 50% 0;\n" +
                "            background: linear-gradient(135deg, #2E7D32, #4CAF50);\n" +
                "            transform: rotate(-45deg);\n" +
                "            border: 3px solid white;\n" +
                "            box-shadow: 0 4px 15px rgba(46,125,50,0.5);\n" +
                "            display: flex; align-items: center; justify-content: center;\n" +
                "        }\n" +
                "        .marker-pin::after {\n" +
                "            content: '🌱'; font-size: 16px; transform: rotate(45deg);\n" +
                "        }\n" +
                "        .marker-pulse {\n" +
                "            width: 50px; height: 50px; border-radius: 50%;\n" +
                "            background: rgba(46,125,50,0.25);\n" +
                "            position: absolute; top: -7px; left: -7px;\n" +
                "            animation: pulse 2s ease-in-out infinite;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id='map'></div>\n" +
                "    <div class='info-panel'>\n" +
                "        <span class='emoji'>📍</span>\n" +
                "        <div>\n" +
                "            <div>" + safeName + "</div>\n" +
                "            <div class='coords'>" + String.format("%.4f", latitude) + "° N, " + String.format("%.4f", longitude) + "° E</div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class='layer-toggle'>\n" +
                "        <button class='layer-btn active' id='btnStreet' onclick='switchLayer(\"street\")'>🗺️ Carte</button>\n" +
                "        <button class='layer-btn' id='btnSatellite' onclick='switchLayer(\"satellite\")'>🛰️ Satellite</button>\n" +
                "        <button class='layer-btn' id='btnTerrain' onclick='switchLayer(\"terrain\")'>⛰️ Terrain</button>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        var lat = " + latitude + ", lng = " + longitude + ";\n" +
                "        var map = L.map('map', {\n" +
                "            zoomControl: false\n" +
                "        }).setView([lat, lng], 15);\n" +
                "\n" +
                "        L.control.zoom({ position: 'topright' }).addTo(map);\n" +
                "        L.control.scale({ position: 'bottomleft', imperial: false }).addTo(map);\n" +
                "\n" +
                "        var streetLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '© OpenStreetMap', maxZoom: 19\n" +
                "        });\n" +
                "        var satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {\n" +
                "            attribution: '© Esri', maxZoom: 19\n" +
                "        });\n" +
                "        var terrainLayer = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '© OpenTopoMap', maxZoom: 17\n" +
                "        });\n" +
                "\n" +
                "        var currentLayer = streetLayer;\n" +
                "        streetLayer.addTo(map);\n" +
                "\n" +
                "        function switchLayer(type) {\n" +
                "            map.removeLayer(currentLayer);\n" +
                "            document.querySelectorAll('.layer-btn').forEach(b => b.classList.remove('active'));\n" +
                "            if (type === 'satellite') { currentLayer = satelliteLayer; document.getElementById('btnSatellite').classList.add('active'); }\n" +
                "            else if (type === 'terrain') { currentLayer = terrainLayer; document.getElementById('btnTerrain').classList.add('active'); }\n" +
                "            else { currentLayer = streetLayer; document.getElementById('btnStreet').classList.add('active'); }\n" +
                "            currentLayer.addTo(map);\n" +
                "        }\n" +
                "\n" +
                "        // Marqueur personnalisé animé\n" +
                "        var markerIcon = L.divIcon({\n" +
                "            className: '',\n" +
                "            html: '<div class=\"marker-pulse\"></div><div class=\"marker-pin\"></div>',\n" +
                "            iconSize: [36, 36],\n" +
                "            iconAnchor: [18, 36],\n" +
                "            popupAnchor: [0, -40]\n" +
                "        });\n" +
                "\n" +
                "        var marker = L.marker([lat, lng], { icon: markerIcon }).addTo(map);\n" +
                "        marker.bindPopup(\n" +
                "            '<div class=\"popup-content\">' +\n" +
                "            '<div class=\"name\">🌱 " + safeName + "</div>' +\n" +
                "            '<div class=\"loc\">" + String.format("%.4f", latitude) + "° N, " + String.format("%.4f", longitude) + "° E</div>' +\n" +
                "            '<div class=\"badge\">📍 Parcelle Agricole</div>' +\n" +
                "            '</div>',\n" +
                "            { closeButton: false, maxWidth: 250 }\n" +
                "        ).openPopup();\n" +
                "\n" +
                "        // Cercle de zone autour de la parcelle\n" +
                "        L.circle([lat, lng], {\n" +
                "            radius: 200, color: '#2E7D32', fillColor: '#4CAF50',\n" +
                "            fillOpacity: 0.1, weight: 2, dashArray: '8 4'\n" +
                "        }).addTo(map);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}

