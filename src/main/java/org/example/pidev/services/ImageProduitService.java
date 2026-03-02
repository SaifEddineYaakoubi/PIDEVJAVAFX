package org.example.pidev.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageProduitService {

    // Récupère l'URL de l'image du produit depuis Unsplash
    public String fetchImageUrl(String productName) {
        try {
            String query = productName.replace(" ", "%20");
            String apiUrl = "https://api.unsplash.com/search/photos?query=" + query + "&client_id=ZJ_aKe69-iZcNgkzbGCiI0ooDxTAo8s16ajRb93wU9I";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                }
                org.json.JSONObject json = new org.json.JSONObject(sb.toString());
                if (json.has("results")) {
                    if (json.getJSONArray("results").length() > 0) {
                        org.json.JSONObject photo = json.getJSONArray("results").getJSONObject(0);
                        if (photo.has("urls")) {
                            org.json.JSONObject urls = photo.getJSONObject("urls");
                            if (urls.has("regular")) {
                                return urls.getString("regular");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retourne une Image JavaFX à partir de l'URL
    public javafx.scene.image.Image fetchImage(String productName) {
        String url = fetchImageUrl(productName);
        if (url != null && !url.isEmpty()) {
            try {
                return new javafx.scene.image.Image(url, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}