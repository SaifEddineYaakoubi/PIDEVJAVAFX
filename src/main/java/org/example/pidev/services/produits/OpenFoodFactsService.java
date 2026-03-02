package org.example.pidev.services.produits;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class OpenFoodFactsService {
    private static final String API_URL = "https://world.openfoodfacts.org/api/v0/product/";

    public JSONObject fetchProductByName(String productName) {
        // Recherche par nom : OpenFoodFacts ne supporte pas la recherche directe par nom via l'API v0,
        // mais on peut utiliser l'API search (https://world.openfoodfacts.org/cgi/search.pl)
        String searchUrl = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + productName.replace(" ", "+") + "&search_simple=1&action=process&json=1&page_size=1";
        try {
            URL url = new URL(searchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) return null;
            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject data = new JSONObject(inline.toString());
            if (data.has("products") && data.getJSONArray("products").length() > 0) {
                return data.getJSONArray("products").getJSONObject(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
