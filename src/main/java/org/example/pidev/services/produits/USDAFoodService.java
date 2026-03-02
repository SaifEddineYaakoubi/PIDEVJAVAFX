package org.example.pidev.services.produits;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class USDAFoodService {
    private final String apiKey;

    public USDAFoodService(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONObject fetchFoodByName(String name) {
        try {
            String encodedName = java.net.URLEncoder.encode(name, StandardCharsets.UTF_8);
            String urlStr = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + encodedName + "&api_key=" + apiKey;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8);
            String json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();
            conn.disconnect();

            if (responseCode != 200) {
                return new JSONObject().put("error", "Erreur de connexion: " + responseCode);
            }

            JSONObject root = new JSONObject(json);
            JSONArray foods = root.optJSONArray("foods");
            if (foods == null || foods.length() == 0) {
                return new JSONObject().put("error", "Produit non trouvé");
            }
            JSONObject food = foods.getJSONObject(0);
            JSONArray nutrients = food.optJSONArray("foodNutrients");
            JSONObject result = new JSONObject();
            result.put("description", food.optString("description", name));
            JSONArray nutriments = new JSONArray();
            if (nutrients != null) {
                for (int i = 0; i < nutrients.length(); i++) {
                    JSONObject n = nutrients.getJSONObject(i);
                    JSONObject nutri = new JSONObject();
                    nutri.put("name", n.optString("nutrientName"));
                    nutri.put("value", n.optDouble("value", 0));
                    nutri.put("unit", n.optString("unitName"));
                    nutriments.put(nutri);
                }
            }
            result.put("nutrients", nutriments);
            return result;
        } catch (Exception e) {
            return new JSONObject().put("error", "Erreur: " + e.getMessage());
        }
    }
}
