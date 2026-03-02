package org.example.pidev.services.utilisateur;

import com.google.gson.*;
import okhttp3.*;
import org.example.pidev.controllers.utilisateur.Language;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ChatbotService {

    private static final String API_KEY = loadApiKey();

    private OkHttpClient client;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean useMockMode = false;

    /**
     * Charge la clé API depuis config.properties ou depuis la variable d'environnement.
     */
    private static String loadApiKey() {
        // 1) Essayer de lire depuis config.properties dans les ressources
        try (InputStream input = ChatbotService.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                String key = props.getProperty("OPENROUTER_API_KEY", "").trim();
                if (!key.isEmpty()) {
                    System.out.println("✅ Clé API chargée depuis config.properties");
                    return key;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de lire config.properties: " + e.getMessage());
        }

        // 2) Fallback: variable d'environnement
        String envKey = System.getenv("OPENROUTER_API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            System.out.println("✅ Clé API chargée depuis variable d'environnement");
            return envKey;
        }

        return null;
    }

    public ChatbotService() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.out.println("⚠️ OPENROUTER_API_KEY non configurée — Chatbot en mode démo");
            System.out.println("   📝 Pour activer l'IA, ajoutez votre clé dans: src/main/resources/config.properties");
            System.out.println("   🔗 Obtenez une clé gratuite sur: https://openrouter.ai/keys");
            this.useMockMode = true;
        } else {
            try {
                this.client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                // ✅ TEST DE CONNEXION pour vérifier que la clé fonctionne
                testConnection();

                System.out.println("✅ Client HTTP initialisé avec succès !");
                System.out.println("🔑 Clé API: " + API_KEY.substring(0, 15) + "...");
                this.useMockMode = false;

            } catch (Exception e) {
                System.err.println("❌ Erreur d'initialisation: " + e.getMessage());
                this.useMockMode = true;
            }
        }
    }

    private void testConnection() {
        try {
            // Utiliser un modèle fiable pour le test
            JsonObject testBody = new JsonObject();
            testBody.addProperty("model", "meta-llama/llama-3.3-70b-instruct:free");

            List<JsonObject> testMessages = new ArrayList<>();
            JsonObject testMsg = new JsonObject();
            testMsg.addProperty("role", "user");
            testMsg.addProperty("content", "test");
            testMessages.add(testMsg);
            testBody.add("messages", gson.toJsonTree(testMessages));
            testBody.addProperty("max_tokens", 5);

            Request testRequest = new Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "Smart Farm App")
                    .post(RequestBody.create(
                            MediaType.parse("application/json"),
                            gson.toJson(testBody)
                    ))
                    .build();

            try (Response testResponse = client.newCall(testRequest).execute()) {
                if (testResponse.isSuccessful()) {
                    System.out.println("✅ Test de connexion réussi !");
                } else {
                    System.err.println("⚠️ Test de connexion échoué: " + testResponse.code());
                    String errorBody = testResponse.body() != null ? testResponse.body().string() : "";
                    System.err.println("Réponse: " + errorBody);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erreur de test: " + e.getMessage());
        }
    }

    public String processUserQuery(String userQuery, String language) {
        if (useMockMode) {
            return getMockResponse(userQuery, language);
        }

        // ✅ Modèles gratuits CONFIRMÉS (février 2026)
        String[] modelsToTry = {
                "meta-llama/llama-3.3-70b-instruct:free",     // Priorité 1 - Très stable
                "google/gemma-3-27b-it:free",                  // Priorité 2 - Google
                "mistralai/mistral-small-3.1-24b-instruct:free", // Priorité 3 - Mistral
                "qwen/qwen3-32b:free",                         // Priorité 4 - Qwen
                "deepseek/deepseek-r1-0528:free",              // Priorité 5 - DeepSeek
                "arcee-ai/trinity-large-preview:free",         // Priorité 6 - Arcee
                "stepfun/step-3.5-flash:free",                 // Priorité 7 - StepFun
                "openrouter/pony-alpha:free"                    // Priorité 8 - Pony Alpha [citation:4]
        };

        Utilisateur currentUser = Session.getCurrentUser();
        String userName = (currentUser != null && currentUser.getPrenom() != null)
                ? currentUser.getPrenom()
                : (currentUser != null && currentUser.getNom() != null)
                ? currentUser.getNom()
                : "cher utilisateur";

        String systemMessage = getSystemMessage(language, userName);

        // Essayer chaque modèle jusqu'à ce qu'un fonctionne
        for (String model : modelsToTry) {
            try {
                System.out.println("🔄 Essai du modèle: " + model);

                List<JsonObject> messages = new ArrayList<>();

                JsonObject systemMsg = new JsonObject();
                systemMsg.addProperty("role", "system");
                systemMsg.addProperty("content", systemMessage);
                messages.add(systemMsg);

                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", userQuery);
                messages.add(userMsg);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);
                requestBody.add("messages", gson.toJsonTree(messages));
                requestBody.addProperty("temperature", 0.7);
                requestBody.addProperty("max_tokens", 800);

                Request request = new Request.Builder()
                        .url("https://openrouter.ai/api/v1/chat/completions")
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .header("HTTP-Referer", "http://localhost:8080")
                        .header("X-Title", "Smart Farm App")
                        .post(RequestBody.create(
                                MediaType.parse("application/json"),
                                gson.toJson(requestBody)
                        ))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                        if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                            JsonObject firstChoice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                            if (firstChoice.has("message")) {
                                JsonObject message = firstChoice.getAsJsonObject("message");
                                if (message.has("content")) {
                                    String content = message.get("content").getAsString();
                                    System.out.println("✅ Succès avec le modèle: " + model);
                                    saveConversation(userQuery, content, language);
                                    return content;
                                }
                            }
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        System.out.println("⚠️ " + model + " non disponible: " + response.code());

                        // Log l'erreur pour debug
                        if (response.code() == 429) {
                            System.out.println("   ⏳ Rate limit - passage au modèle suivant");
                        } else if (response.code() == 404) {
                            System.out.println("   ❌ Modèle introuvable - passage au suivant");
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Erreur avec " + model + ": " + e.getMessage());
            }

            // Petit délai entre les essais pour éviter de surcharger
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        // Si aucun modèle n'a fonctionné, on utilise le mode mock
        System.err.println("⚠️ Aucun modèle gratuit disponible, utilisation du mode mock");
        return getMockResponse(userQuery, language);
    }
    private String getSystemMessage(String language, String userName) {
        switch(Language.fromString(language)) {
            case FRENCH:
                return "Vous êtes un expert agricole spécialisé dans l'agriculture marocaine. " +
                        "Vous vous appelez 'SmartFarm Assistant'. " +
                        "L'utilisateur actuel s'appelle " + userName + ". " +
                        "Répondez aux questions sur les cultures, l'irrigation, " +
                        "les engrais, les maladies des plantes, et les saisons de plantation. " +
                        "Utilisez un langage simple, amical et pratique. " +
                        "Donnez des conseils précis adaptés au climat marocain.";

            case ARABIC:
                return "أنت خبير زراعي متخصص في الزراعة المغربية. " +
                        "اسمك 'مساعد SmartFarm'. " +
                        "اسم المستخدم الحالي هو " + userName + ". " +
                        "أجب عن الأسئلة المتعلقة بالمحاصيل، الري، " +
                        "الأسمدة، أمراض النباتات، ومواسم الزراعة. " +
                        "استخدم لغة بسيطة وودية وعملية. " +
                        "قدم نصائح دقيقة مناسبة للمناخ المغربي.";

            case DARIJA:
                return "نتا خبير ف لفلاحة فالمغرب. " +
                        "سميتك 'مساعد SmartFarm'. " +
                        "لمستخدم دابا سميتو " + userName + ". " +
                        "جاوب على لأسئلة ديال لمحاصيل، سقي، " +
                        "لسماد، لأمراض د نباتات، ومواسم لزراعة. " +
                        "هضر بلغة بسيطة و ودية و عملية. " +
                        "عطي نصائح دقيقة مناسبة للمناخ لمغريبي.";

            case SPANISH:
                return "Eres un experto agrícola especializado en la agricultura marroquí. " +
                        "Te llamas 'Asistente SmartFarm'. " +
                        "El usuario actual se llama " + userName + ". " +
                        "Responde preguntas sobre cultivos, riego, " +
                        "fertilizantes, enfermedades de plantas y temporadas de siembra. " +
                        "Usa lenguaje simple, amigable y práctico. " +
                        "Da consejos precisos adaptados al clima marroquí.";

            default: // ENGLISH
                return "You are an agricultural expert specialized in Moroccan agriculture. " +
                        "Your name is 'SmartFarm Assistant'. " +
                        "The current user is called " + userName + ". " +
                        "Answer questions about crops, irrigation, " +
                        "fertilizers, plant diseases, and planting seasons. " +
                        "Use simple, friendly, and practical language. " +
                        "Give precise advice adapted to the Moroccan climate.";
        }
    }

    private void saveConversation(String userQuery, String response, String language) {
        try {
            org.example.pidev.models.ChatMessage message = new org.example.pidev.models.ChatMessage(userQuery, language, 1L);
            message.setResponse(response);
            System.out.println("💾 Conversation sauvegardée (simulation)");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private String getErrorMessage(String language) {
        switch(Language.fromString(language)) {
            case FRENCH:
                return "Désolé, je rencontre des difficultés techniques. Veuillez réessayer plus tard.";
            case ARABIC:
                return "عذرًا، أواجه مشكلة تقنية. الرجاء المحاولة مرة أخرى لاحقًا.";
            case DARIJA:
                return "سمح لي، كاينة مشكلة تقنية. عاود جرب بعد شوية.";
            case SPANISH:
                return "Lo siento, estoy teniendo problemas técnicos. Por favor, inténtalo de nuevo más tarde.";
            default:
                return "Sorry, I'm experiencing technical difficulties. Please try again later.";
        }
    }

    private String getMockResponse(String userQuery, String language) {
        String query = userQuery.toLowerCase();
        Language lang = Language.fromString(language);

        Utilisateur currentUser = Session.getCurrentUser();
        String userName = (currentUser != null && currentUser.getPrenom() != null)
                ? currentUser.getPrenom()
                : (currentUser != null && currentUser.getNom() != null)
                ? currentUser.getNom()
                : "cher utilisateur";

        if (query.contains("tomate") || query.contains("tomato") || query.contains("طماطم") || query.contains("ماطيشة")) {
            switch(lang) {
                case FRENCH:
                    return "🍅 Bonjour " + userName + "! Au Maroc, plantez les tomates de mars à mai. Utilisez un engrais riche en potassium.";
                case ARABIC:
                    return "🍅 مرحبًا " + userName + "! في المغرب، ازرع الطماطم من مارس إلى مايو. استخدم سمادًا غنيًا بالبوتاسيوم.";
                case DARIJA:
                    return "🍅 أهلا " + userName + "! فالمغرب، زرع لماطيشة من مارس حتى مايو. استعمل سماد فيه بوتاسيوم بزاف.";
                default:
                    return "🍅 Hello " + userName + "! In Morocco, plant tomatoes from March to May. Use potassium-rich fertilizer.";
            }
        } else if (query.contains("blé") || query.contains("wheat") || query.contains("قمح")) {
            switch(lang) {
                case FRENCH:
                    return "🌾 " + userName + ", le blé au Maroc se plante de novembre à décembre. Irrigation toutes les 2-3 semaines.";
                case ARABIC:
                    return "🌾 " + userName + "، يزرع القمح في المغرب من نوفمبر إلى ديسمبر. الري كل 2-3 أسابيع.";
                case DARIJA:
                    return "🌾 " + userName + "، ل قمح فالمغرب كايتزرع من نونبر حتى دجنبر. سقي كل 15 إلى 20 يوم.";
                default:
                    return "🌾 " + userName + ", wheat in Morocco is planted from November to December. Water every 2-3 weeks.";
            }
        } else if (query.contains("agrume") || query.contains("citrus") || query.contains("حمضيات")) {
            switch(lang) {
                case FRENCH:
                    return "🍊 " + userName + ", pour les agrumes, utilisez un engrais NPK 15-15-15 au printemps.";
                case ARABIC:
                    return "🍊 " + userName + "، للحمضيات، استخدم سماد NPK 15-15-15 في الربيع.";
                case DARIJA:
                    return "🍊 " + userName + "، للحمضيات، استعمل سماد NPK 15-15-15 ف لربيع.";
                default:
                    return "🍊 " + userName + ", for citrus trees, use NPK 15-15-15 fertilizer in spring.";
            }
        } else {
            switch(lang) {
                case FRENCH:
                    return "🌱 Bonjour " + userName + "! Je suis votre assistant agricole. Posez-moi des questions sur les tomates, le blé, les agrumes, ou les maladies des plantes.";
                case ARABIC:
                    return "🌱 مرحبًا " + userName + "! أنا مساعدك الزراعي. اسألني عن الطماطم، القمح، الحمضيات، أو أمراض النباتات.";
                case DARIJA:
                    return "🌱 أهلا " + userName + "! أنا لمساعد ديالك ف لفلاحة. سولني على لماطيشة، لقمح، لحمضيات، أو لأمراض د نباتات.";
                default:
                    return "🌱 Hello " + userName + "! I'm your agricultural assistant. Ask me about tomatoes, wheat, citrus, or plant diseases.";
            }
        }
    }

    public List<String> getAgricultureSuggestions(String language) {
        List<String> suggestions = new ArrayList<>();
        Language lang = Language.fromString(language);

        switch(lang) {
            case FRENCH:
                suggestions.add("🍅 Quand planter les tomates au Maroc?");
                suggestions.add("🌾 Calendrier d'irrigation pour le blé");
                suggestions.add("🍊 Quel engrais pour les agrumes?");
                suggestions.add("🌿 Comment traiter l'oïdium?");
                suggestions.add("💧 Fréquence d'arrosage pour les olives");
                break;

            case ARABIC:
                suggestions.add("🍅 متى نزرع الطماطم في المغرب؟");
                suggestions.add("🌾 جدول الري للقمح");
                suggestions.add("🍊 أي سماد مناسب للحمضيات؟");
                suggestions.add("🌿 كيف نعالج البياض الدقيقي؟");
                suggestions.add("💧 وتيرة الري للزيتون");
                break;

            case DARIJA:
                suggestions.add("🍅 فاش نزرع لماطيشة فالمغرب؟");
                suggestions.add("🌾 جدوال د سقي لقمح");
                suggestions.add("🍊 أشنو هو لسماد لمزيان للحمضيات؟");
                suggestions.add("🌿 كيف نعالج لبياض?");
                suggestions.add("💧 شحال نسقي الزيتون?");
                break;

            case SPANISH:
                suggestions.add("🍅 ¿Cuándo plantar tomates en Marruecos?");
                suggestions.add("🌾 Calendario de riego para trigo");
                suggestions.add("🍊 ¿Qué fertilizante para cítricos?");
                suggestions.add("🌿 ¿Cómo tratar el mildiú?");
                suggestions.add("💧 Frecuencia de riego para olivos");
                break;

            default:
                suggestions.add("🍅 When to plant tomatoes in Morocco?");
                suggestions.add("🌾 Wheat irrigation schedule");
                suggestions.add("🍊 What fertilizer for citrus trees?");
                suggestions.add("🌿 How to treat powdery mildew?");
                suggestions.add("💧 Olive tree watering frequency");
        }

        return suggestions;
    }
}