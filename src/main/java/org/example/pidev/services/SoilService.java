package org.example.pidev.services;

import org.example.pidev.models.SoilData;
import org.example.pidev.utils.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SoilService : récupère les données depuis SoilGrids API v2 (API réelle gratuite)
 * Documentation: https://soilgrids.org/
 * et persiste l'analyse dans la table `soil_analysis`.
 *
 * Endpoints utilisés:
 * - https://rest.soilgrids.org/soilgrids/v2.0/properties/query?lon=X&lat=Y&property=...
 *
 * Propriétés disponibles:
 * - phh2o: pH (eau) à 0-5cm
 * - sand, silt, clay: Texture du sol (%)
 * - organic_carbon: Carbone organique (%)
 * - nitrogen: Azote total (mg/kg)
 */
public class SoilService {

    private static final String SOILGRIDS_BASE_URL = "https://rest.soilgrids.org/soilgrids/v2.0/properties/query";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Connection connection;

    public SoilService() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Récupérer les données du sol via SoilGrids API v2 pour des coordonnées
     */
    public SoilData getSoilData(double latitude, double longitude) {
        try {
            System.out.println("🌍 Appel SoilGrids API pour lat=" + latitude + ", lon=" + longitude);

            // Récupérer tous les paramètres nécessaires
            String properties = "phh2o,sand,silt,clay,organic_carbon,nitrogen";
            String url = String.format("%s?lon=%.4f&lat=%.4f&property=%s&depth=0-5cm",
                    SOILGRIDS_BASE_URL, longitude, latitude, properties);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(20))
                    .header("User-Agent", "SmartFarm/1.0")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📊 Réponse SoilGrids: " + response.statusCode());

            if (response.statusCode() == 200) {
                SoilData data = parseSoilgridsResponse(response.body());
                if (data != null) {
                    data.setSource("SoilGrids v2.0");
                    System.out.println("✅ Données SoilGrids chargées avec succès");
                    // sauvegarder en base
                    saveSoilAnalysis(data);
                    return data;
                }
            } else {
                System.err.println("❌ SoilGrids API retourna le code: " + response.statusCode());
                System.err.println("Réponse: " + response.body().substring(0, Math.min(500, response.body().length())));
            }
        } catch (java.net.ConnectException e) {
            System.err.println("⚠️ Impossible de se connecter à SoilGrids API (pas de connexion internet ?)");
        } catch (Exception e) {
            System.err.println("❌ Erreur appel SoilGrids API: " + e.getMessage());
        }

        // fallback avec données par défaut
        System.out.println("⚠️ Utilisation des données de fallback local");
        SoilData fallback = createFallback();
        saveSoilAnalysis(fallback);
        return fallback;
    }

    /**
     * Parse la réponse JSON de SoilGrids API v2
     * Structure: { "properties": { "phh2o": {...}, "sand": {...}, ... } }
     */
    private SoilData parseSoilgridsResponse(String body) {
        try {
            JSONObject json = new JSONObject(body);
            SoilData d = new SoilData();

            if (json.has("properties")) {
                JSONObject props = json.getJSONObject("properties");

                // pH (phh2o) - SoilGrids retourne pH * 10
                if (props.has("phh2o")) {
                    double phValue = extractPropertyValue(props.getJSONObject("phh2o"));
                    d.setPh(phValue / 10.0); // Convertir de 68 à 6.8
                    System.out.println("  📊 pH: " + d.getPh());
                }

                // Texture du sol (%)
                if (props.has("sand")) {
                    double sandValue = extractPropertyValue(props.getJSONObject("sand"));
                    d.setSand(sandValue / 100.0); // Convertir en %
                    System.out.println("  🟨 Sable: " + d.getSand() + "%");
                }

                if (props.has("silt")) {
                    double siltValue = extractPropertyValue(props.getJSONObject("silt"));
                    d.setSilt(siltValue / 100.0);
                    System.out.println("  🟫 Limon: " + d.getSilt() + "%");
                }

                if (props.has("clay")) {
                    double clayValue = extractPropertyValue(props.getJSONObject("clay"));
                    d.setClay(clayValue / 100.0);
                    System.out.println("  🟪 Argile: " + d.getClay() + "%");
                }

                // Carbone organique (%)
                if (props.has("organic_carbon")) {
                    double ocValue = extractPropertyValue(props.getJSONObject("organic_carbon"));
                    d.setOrganicCarbon(ocValue / 100.0); // Convertir en %
                    System.out.println("  🌱 Matière organique: " + d.getOrganicCarbon() + "%");
                }

                // Azote (mg/kg)
                if (props.has("nitrogen")) {
                    double nValue = extractPropertyValue(props.getJSONObject("nitrogen"));
                    d.setNitrogen(nValue);
                    System.out.println("  🔬 Azote: " + d.getNitrogen() + " mg/kg");
                }

                // Phosphore et Potassium (estimation basée sur matière organique)
                d.setPhosphorus(estimatePhosphorus(d.getOrganicCarbon()));
                d.setPotassium(estimatePotassium(d.getOrganicCarbon()));

                return d;
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing réponse SoilGrids: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extrait la valeur numérique d'une propriété SoilGrids
     * Format: { "0-5cm": { "mean": 350 } } ou { "0-5cm": 350 }
     */
    private double extractPropertyValue(JSONObject property) {
        try {
            // Parcourir les clés (généralement "0-5cm", "5-15cm", etc.)
            for (String depthKey : property.keySet()) {
                Object depthValue = property.get(depthKey);

                if (depthValue instanceof JSONObject) {
                    JSONObject depthObj = (JSONObject) depthValue;

                    // Chercher "mean", "value", ou la première clé numérique
                    if (depthObj.has("mean")) {
                        return depthObj.getDouble("mean");
                    }
                    if (depthObj.has("value")) {
                        return depthObj.getDouble("value");
                    }

                    // Sinon, prendre la première valeur numérique trouvée
                    for (String key : depthObj.keySet()) {
                        Object val = depthObj.get(key);
                        if (val instanceof Number) {
                            return ((Number) val).doubleValue();
                        }
                    }
                } else if (depthValue instanceof Number) {
                    return ((Number) depthValue).doubleValue();
                }
            }
        } catch (Exception e) {
            // continue
        }
        return 0.0;
    }

    /**
     * Estime le phosphore basé sur la matière organique
     * Formule empirique: P = 20 + 15 * MO%
     */
    private double estimatePhosphorus(double organicCarbon) {
        // Conversion matière organique (%) à phosphore disponible (mg/kg)
        return 20 + (15 * organicCarbon);
    }

    /**
     * Estime le potassium basé sur la matière organique et la texture
     * Formule empirique: K = 150 + 50 * MO%
     */
    private double estimatePotassium(double organicCarbon) {
        return 150 + (50 * organicCarbon);
    }
    /**
     * Sauvegarder l'analyse en base (table soil_analysis)
     */
    public boolean saveSoilAnalysis(SoilData data) {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("⚠️ Connexion base de données non disponible");
                return false;
            }

            // Essayer d'abord avec date_analyse (nom de colonne courant dans la BDD)
            String sql = "INSERT INTO soil_analysis (ph, sand, silt, clay, nitrogen, phosphorus, potassium, organic_carbon, source) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setDouble(1, data.getPh());
            pst.setDouble(2, data.getSand());
            pst.setDouble(3, data.getSilt());
            pst.setDouble(4, data.getClay());
            pst.setDouble(5, data.getNitrogen());
            pst.setDouble(6, data.getPhosphorus());
            pst.setDouble(7, data.getPotassium());
            pst.setDouble(8, data.getOrganicCarbon());
            pst.setString(9, data.getSource());
            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                data.setId(keys.getInt(1));
                System.out.println("✅ Données sauvegardées en base (ID: " + data.getId() + ")");
            }
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur sauvegarde soil_analysis: " + e.getMessage());
            // Ne pas planter l'application si la sauvegarde échoue
            return false;
        }
    }

    /**
     * Crée un objet SoilData par défaut avec des valeurs réalistes
     * Utilisé en fallback si l'API SoilGrids n'est pas disponible
     */
    private SoilData createFallback() {
        System.out.println("📋 Création des données de fallback local");
        SoilData d = new SoilData();

        // Données réalistes pour une région agricole méditerranéenne
        d.setPh(6.8);           // Légèrement acide à neutre
        d.setSand(35);          // %
        d.setSilt(45);          // %
        d.setClay(20);          // %
        d.setNitrogen(120);     // mg/kg
        d.setPhosphorus(15);    // mg/kg (estimé)
        d.setPotassium(200);    // mg/kg (estimé)
        d.setOrganicCarbon(2.5); // %
        d.setSource("LOCAL_FALLBACK");

        return d;
    }
}

