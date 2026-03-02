package org.example.pidev.services.ahmed;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.pidev.models.Client;
import org.example.pidev.models.Vente;

import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * GroqService - Service d'intégration avec l'API Groq pour l'IA
 * 🧠 CHATBOT TEMPS RÉEL SIMPLIFIÉ
 *
 * Fonctionnalités :
 * ✅ getRealTimeData() : Récupère les données BD en temps réel
 * ✅ chat() : Appelle directement getRealTimeData() avant chaque requête
 * ✅ Support multilingue (Tunisien/Français)
 * ✅ Injection minimale des services
 */
public class GroqService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "llama-3.3-70b-versatile";
    private static final int TIMEOUT_SECONDS = 30;

    // 🔌 Services injectés
    private ClientService clientService;
    private VenteService venteService;
    private FideliteService fideliteService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Constructeur avec injection complète
     */
    public GroqService(ClientService clientService, VenteService venteService, FideliteService fideliteService) {
        this.clientService = clientService;
        this.venteService = venteService;
        this.fideliteService = fideliteService;
    }

    /**
     * Constructeur avec deux services
     */
    public GroqService(ClientService clientService, VenteService venteService) {
        this(clientService, venteService, new FideliteService());
    }

    /**
     * Constructeur par défaut
     */
    public GroqService() {
        this(new ClientService(), new VenteService(), new FideliteService());
    }

    // ============================================================================
    // 🔄 MÉTHODE UNIQUE TEMPS RÉEL
    // ============================================================================

    /**
     * 🚀 MÉTHODE UNIQUE : Récupère TOUTES les données actuelles de la BD
     *
     * Appelle directement:
     * - clientService.getAll()
     * - venteService.getAll()
     * - clientService.getAllWithFidelite()
     *
     * Format simple et direct:
     * "CONTEXTE ACTUEL SMART FARM:
     *  - Clients: Mohamed, Sarah, Ahmed...
     *  - Ventes: ID1: 50DT, ID2: 120DT...
     *  - Fidélité: Mohamed: 500pts, Sarah: 1200pts..."
     *
     * @return String : Snapshot temps réel formaté
     */
    public String getRealTimeData() {
        StringBuilder data = new StringBuilder();

        try {
            // 1️⃣ RÉCUPÉRER TOUS LES CLIENTS
            List<Client> allClients = clientService.getAll();
            StringBuilder clients = new StringBuilder();
            if (allClients != null && !allClients.isEmpty()) {
                for (int i = 0; i < allClients.size(); i++) {
                    clients.append(allClients.get(i).getNom());
                    if (i < allClients.size() - 1) {
                        clients.append(", ");
                    }
                }
            } else {
                clients.append("Aucun client");
            }

            // 2️⃣ RÉCUPÉRER TOUTES LES VENTES
            List<Vente> allVentes = venteService.getAll();
            StringBuilder ventes = new StringBuilder();
            if (allVentes != null && !allVentes.isEmpty()) {
                for (int i = 0; i < allVentes.size(); i++) {
                    Vente v = allVentes.get(i);
                    ventes.append("ID").append(v.getIdVente()).append(": ")
                          .append(String.format("%.2f", v.getMontantTotal())).append("DT");
                    if (i < allVentes.size() - 1) {
                        ventes.append(", ");
                    }
                }
            } else {
                ventes.append("Aucune vente");
            }

            // 3️⃣ RÉCUPÉRER LES POINTS DE FIDÉLITÉ
            List<Client> clientsWithFidelity = clientService.getAllWithFidelite();
            StringBuilder fidelite = new StringBuilder();
            if (clientsWithFidelity != null && !clientsWithFidelity.isEmpty()) {
                boolean first = true;
                for (Client c : clientsWithFidelity) {
                    if (c.getTotalAchats() != null && c.getTotalAchats() > 0) {
                        if (!first) {
                            fidelite.append(", ");
                        }
                        fidelite.append(c.getNom()).append(": ")
                                .append(String.format("%.0f", c.getTotalAchats())).append("pts");
                        first = false;
                    }
                }
                if (fidelite.length() == 0) {
                    fidelite.append("Aucun point de fidélité");
                }
            } else {
                fidelite.append("Aucune donnée de fidélité");
            }

            // 🔑 FORMAT FINAL SIMPLIFIÉ
            data.append("CONTEXTE ACTUEL SMART FARM:\n")
                .append("- Clients: ").append(clients).append("\n")
                .append("- Ventes: ").append(ventes).append("\n")
                .append("- Fidélité: ").append(fidelite).append("\n");

        } catch (Exception e) {
            System.err.println("⚠️  Erreur getRealTimeData(): " + e.getMessage());
            data.append("ERREUR: Impossible de récupérer les données. Veuillez réessayer.\n");
        }

        return data.toString();
    }

    // ============================================================================
    // 🚀 CHAT PRINCIPAL - Appelle getRealTimeData() directement
    // ============================================================================

    /**
     * 🔥 MÉTHODE PRINCIPALE : Chat avec données TEMPS RÉEL injectées
     *
     * ✅ APPELLE getRealTimeData() à chaque requête
     * ✅ Envoie les données ACTUELLES comme premier message (SYSTEM)
     * ✅ Ajoute consigne de langue (Language Lock)
     * ✅ Puis envoie la question de l'utilisateur
     *
     * @param userQuestion Question de l'utilisateur
     * @return Réponse de Groq (en tenant compte des données actuelles)
     */
    public String chat(String userQuestion) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("La question ne peut pas être vide");
        }

        // 🔑 APPELER getRealTimeData() DIRECTEMENT
        String realTimeData = getRealTimeData();

        // 🌍 CONSTRUIRE LE MESSAGE SYSTEM AVEC LES DONNÉES TEMPS RÉEL
        String systemMessage = realTimeData + "\n" +
                "RÉPONDS TOUJOURS DANS LA LANGUE DE L'UTILISATEUR.\n" +
                "S'il parle en Tunisien, réponds en Tunisien.\n" +
                "S'il parle en Français, réponds en Français.\n" +
                "Ne JAMAIS mélanger les langues.\n" +
                "Utilise UNIQUEMENT les données fournies ci-dessus.";

        // 📝 CONSTRUIRE LE PAYLOAD JSON
        String payload = buildPayloadJson(userQuestion, systemMessage);
        HttpRequest request = buildRequest(payload);

        // 🌐 ENVOYER À GROQ
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new Exception("Erreur Groq API (HTTP " + response.statusCode() + "): " + response.body());
            }

            return extractMessageFromResponse(response.body());
        } catch (Exception e) {
            System.err.println("❌ Erreur Groq Service: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Surcharge : Chat avec custom system prompt
     */
    public String chat(String userQuestion, String customSystemPrompt) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("La question ne peut pas être vide");
        }

        // Utiliser le prompt personnalisé ou le default avec données temps réel
        String systemMessage = (customSystemPrompt == null || customSystemPrompt.trim().isEmpty())
            ? (getRealTimeData() + "\nRÉPONDS TOUJOURS DANS LA LANGUE DE L'UTILISATEUR.")
            : customSystemPrompt;

        String payload = buildPayloadJson(userQuestion, systemMessage);
        HttpRequest request = buildRequest(payload);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new Exception("Erreur Groq API (HTTP " + response.statusCode() + "): " + response.body());
            }

            return extractMessageFromResponse(response.body());
        } catch (Exception e) {
            System.err.println("❌ Erreur Groq Service: " + e.getMessage());
            throw e;
        }
    }

    // ============================================================================
    // 🔧 HELPERS TECHNIQUES
    // ============================================================================

    /**
     * Construit la requête HTTP pour Groq
     */
    private HttpRequest buildRequest(String payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .timeout(java.time.Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    /**
     * Construit le payload JSON pour Groq
     *
     * Format:
     * {
     *   "model": "llama-3.3-70b-versatile",
     *   "messages": [
     *     {"role": "system", "content": "[DONNÉES TEMPS RÉEL + INSTRUCTIONS]"},
     *     {"role": "user", "content": "[QUESTION DE L'UTILISATEUR]"}
     *   ]
     * }
     */
    private String buildPayloadJson(String userQuestion, String systemMessage) throws Exception {
        StringWriter sw = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator gen = jsonFactory.createGenerator(sw);

        gen.writeStartObject();
        gen.writeStringField("model", MODEL);

        gen.writeArrayFieldStart("messages");

        // 🔑 MESSAGE SYSTEM (DONNÉES + INSTRUCTIONS)
        gen.writeStartObject();
        gen.writeStringField("role", "system");
        gen.writeStringField("content", systemMessage);
        gen.writeEndObject();

        // MESSAGE UTILISATEUR
        gen.writeStartObject();
        gen.writeStringField("role", "user");
        gen.writeStringField("content", userQuestion);
        gen.writeEndObject();

        gen.writeEndArray();

        gen.writeNumberField("temperature", 0.7);
        gen.writeNumberField("max_tokens", 1500);

        gen.writeEndObject();
        gen.close();

        return sw.toString();
    }

    /**
     * Extrait la réponse du JSON Groq
     */
    private String extractMessageFromResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        if (root.has("error")) {
            throw new Exception("Erreur Groq: " + root.get("error").get("message").asText());
        }

        JsonNode choices = root.path("choices");
        if (choices.isEmpty()) {
            throw new Exception("Réponse invalide de Groq: pas de choices");
        }

        return choices.get(0).path("message").path("content").asText();
    }

    /**
     * Teste la connexion à Groq
     */
    public boolean testConnection() {
        try {
            String response = chat("Dis 'OK' simplement");
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
            return false;
        }
    }
}

