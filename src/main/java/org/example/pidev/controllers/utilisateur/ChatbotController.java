package org.example.pidev.controllers.utilisateur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.ChatbotService;
import org.example.pidev.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatbotController implements Initializable {

    @FXML
    private VBox chatContainer;

    @FXML
    private TextArea messageInput;

    @FXML
    private ComboBox<Language> languageSelector;

    @FXML
    private Button sendButton;

    @FXML
    private Button backToDashboardButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label userGreetingLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox suggestionsContainer;

    private ChatbotService chatbotService;
    private Language currentLanguage = Language.ENGLISH;
    private Utilisateur currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize chatbot service
        chatbotService = new ChatbotService();

        // Get current user from session
        currentUser = Session.getCurrentUser();

        // Setup UI components
        setupLanguageSelector();
        setupButtons();
        setupEnterKeyHandler();

        // Load suggestions
        loadSuggestions();

        // Show welcome message
        showWelcomeMessage();

        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) chatContainer.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    event.consume();
                    handleClose();
                });
                System.out.println("✅ Gestionnaire de fermeture ajouté");
            } catch (Exception e) {
                System.err.println("⚠️ Impossible d'ajouter le gestionnaire: " + e.getMessage());
            }
        });
    }

    private void setupLanguageSelector() {
        languageSelector.getItems().addAll(Language.values());
        languageSelector.setValue(Language.ENGLISH);
        languageSelector.setOnAction(e -> {
            currentLanguage = languageSelector.getValue();
            loadSuggestions();
        });
    }

    private void setupButtons() {
        // Send button
        sendButton.setOnAction(e -> sendMessage());

        // Back to dashboard button - déjà relié via FXML
        if (backToDashboardButton != null) {
            backToDashboardButton.setOnAction(e -> navigateToDashboard());
        }

        // Logout button - déjà relié via FXML
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
        }
    }

    private void setupEnterKeyHandler() {
        messageInput.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER") && !e.isShiftDown()) {
                sendMessage();
                e.consume();
            }
        });
    }

    private void loadSuggestions() {
        Platform.runLater(() -> {
            suggestionsContainer.getChildren().clear();
            List<String> suggestions = chatbotService.getAgricultureSuggestions(currentLanguage.name());

            for (String suggestion : suggestions) {
                Button suggestionBtn = new Button(suggestion);
                suggestionBtn.getStyleClass().add("suggestion-button");
                suggestionBtn.setWrapText(true);
                suggestionBtn.setMaxWidth(200);
                suggestionBtn.setOnAction(e -> {
                    messageInput.setText(suggestion);
                    sendMessage();
                });
                suggestionsContainer.getChildren().add(suggestionBtn);
            }
        });
    }

    private void showWelcomeMessage() {
        String welcomeMsg;
        if (currentUser != null) {
            // Update user info
            if (userGreetingLabel != null) {
                userGreetingLabel.setText("Welcome, " + currentUser.getPrenom() + " " + currentUser.getNom() + "!");
            }
            if (userRoleLabel != null) {
                userRoleLabel.setText("Role: " + currentUser.getRole().name());
            }

            // Personalized welcome message
            welcomeMsg = getPersonalizedWelcomeMessage(currentLanguage, currentUser.getPrenom());
        } else {
            welcomeMsg = getWelcomeMessage(currentLanguage);
        }

        addMessage("Bot", welcomeMsg, false);
    }

    private String getPersonalizedWelcomeMessage(Language language, String firstName) {
        switch(language) {
            case FRENCH:
                return "Bonjour " + firstName + "! Je suis votre assistant agricole spécialisé dans l'agriculture marocaine. " +
                        "Comment puis-je vous aider aujourd'hui? Vous pouvez me poser des questions sur:\n" +
                        "• Les cultures et leurs saisons\n" +
                        "• L'irrigation et la gestion de l'eau\n" +
                        "• Les engrais et traitements\n" +
                        "• Les maladies des plantes\n" +
                        "• Et tout autre sujet agricole!";

            case ARABIC:
                return "مرحبًا " + firstName + "! أنا مساعدك الزراعي المتخصص في الزراعة المغربية. " +
                        "كيف يمكنني مساعدتك اليوم؟ يمكنك سؤالي عن:\n" +
                        "• المحاصيل ومواسمها\n" +
                        "• الري وإدارة المياه\n" +
                        "• الأسمدة والعلاجات\n" +
                        "• أمراض النباتات\n" +
                        "• وأي موضوع زراعي آخر!";

            case DARIJA:
                return "أهلا " + firstName + "! أنا لمساعد ديالك ف لفلاحة لمغريبية. " +
                        "كيفاش نقدر نعاونك ليوما؟ تقدر تسولني على:\n" +
                        "• لمحاصيل ومواسمها\n" +
                        "• سقي وتدبير لما\n" +
                        "• لسماد ولعالجات\n" +
                        "• لأمراض د نباتات\n" +
                        "• وأي حاجة خرى ف لفلاحة!";

            case SPANISH:
                return "¡Hola " + firstName + "! Soy tu asistente agrícola especializado en la agricultura marroquí. " +
                        "¿Cómo puedo ayudarte hoy? Puedes preguntarme sobre:\n" +
                        "• Cultivos y sus temporadas\n" +
                        "• Riego y gestión del agua\n" +
                        "• Fertilizantes y tratamientos\n" +
                        "• Enfermedades de las plantas\n" +
                        "• ¡Y cualquier otro tema agrícola!";

            default:
                return "Hello " + firstName + "! I'm your agricultural assistant specialized in Moroccan agriculture. " +
                        "How can I help you today? You can ask me about:\n" +
                        "• Crops and their seasons\n" +
                        "• Irrigation and water management\n" +
                        "• Fertilizers and treatments\n" +
                        "• Plant diseases\n" +
                        "• And any other agricultural topic!";
        }
    }

    private String getWelcomeMessage(Language language) {
        switch(language) {
            case FRENCH:
                return "Bonjour! Je suis votre assistant agricole spécialisé dans l'agriculture marocaine. " +
                        "Posez-moi toutes vos questions sur l'agriculture!";

            case ARABIC:
                return "مرحبًا! أنا مساعدك الزراعي المتخصص في الزراعة المغربية. " +
                        "اسألني أي شيء عن الزراعة!";

            case DARIJA:
                return "أهلا! أنا لمساعد ديالك ف لفلاحة لمغريبية. " +
                        "سولني على أي حاجة ف لفلاحة!";

            case SPANISH:
                return "¡Hola! Soy tu asistente agrícola especializado en la agricultura marroquí. " +
                        "¡Pregúntame cualquier cosa sobre agricultura!";

            default:
                return "Hello! I'm your agricultural assistant specialized in Moroccan agriculture. " +
                        "Ask me anything about farming!";
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) return;

        // Add user message
        String userName = (currentUser != null) ? currentUser.getPrenom() : "You";
        addMessage(userName, message, true);

        // Clear input
        messageInput.clear();

        // Disable send button while processing
        sendButton.setDisable(true);
        messageInput.setDisable(true);

        // Process in background thread
        new Thread(() -> {
            try {
                // Get bot response
                String response = chatbotService.processUserQuery(message, currentLanguage.name());

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    addMessage("Bot", response, false);
                    sendButton.setDisable(false);
                    messageInput.setDisable(false);
                    messageInput.requestFocus();

                    // Scroll to bottom
                    scrollPane.setVvalue(1.0);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    addMessage("Bot", "Désolé, une erreur s'est produite. Veuillez réessayer.", false);
                    sendButton.setDisable(false);
                    messageInput.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void addMessage(String sender, String content, boolean isUser) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(10));
        messageBox.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox messageContent = new VBox(5);
        messageContent.setMaxWidth(500);

        // Sender label with timestamp
        Label senderLabel = new Label(sender + " • " + java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        senderLabel.getStyleClass().add("message-sender");
        senderLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        // Message text
        Text text = new Text(content);
        text.setWrappingWidth(450);
        text.getStyleClass().add("message-text");

        TextFlow textFlow = new TextFlow(text);
        textFlow.getStyleClass().add(isUser ? "user-message" : "bot-message");
        textFlow.setMaxWidth(450);
        textFlow.setPadding(new Insets(12, 15, 12, 15));

        // Add styles based on message type
        if (isUser) {
            textFlow.setStyle("-fx-background-color: #007AFF; -fx-background-radius: 18px 18px 5px 18px;");
            text.setStyle("-fx-fill: white;");
        } else {
            textFlow.setStyle("-fx-background-color: #E5E5EA; -fx-background-radius: 18px 18px 18px 5px;");
            text.setStyle("-fx-fill: black;");
        }

        messageContent.getChildren().addAll(senderLabel, textFlow);
        messageBox.getChildren().add(messageContent);

        // Add to chat container with animation
        Platform.runLater(() -> {
            chatContainer.getChildren().add(messageBox);
        });
    }

    @FXML
    private void handleClose() {
        // Cette méthode sera appelée quand on ferme la fenêtre
        navigateToDashboard();
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear session
            Session.clear();

            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/LoginView.fxml"));
            Parent root = loader.load();

            // Obtenir le stage de manière sûre
            Stage stage = null;
            if (logoutButton != null && logoutButton.getScene() != null) {
                stage = (Stage) logoutButton.getScene().getWindow();
            } else if (chatContainer != null && chatContainer.getScene() != null) {
                stage = (Stage) chatContainer.getScene().getWindow();
            }
            if (stage == null) {
                stage = (Stage) javafx.stage.Window.getWindows().stream()
                        .filter(javafx.stage.Window::isShowing)
                        .findFirst().orElse(null);
            }
            if (stage == null) {
                stage = new Stage();
            }

            stage.setMaximized(false);
            Scene scene = new Scene(root, 600, 500);

            // Charger les CSS
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Farm - Connexion");
            stage.setResizable(false);
            stage.setWidth(600);
            stage.setHeight(500);
            stage.centerOnScreen();
            stage.setOnCloseRequest(evt -> {
                javafx.application.Platform.exit();
                System.exit(0);
            });
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur déconnexion chatbot: " + e.getMessage());
            showError("Erreur lors de la déconnexion");
        }
    }

    @FXML
    private void navigateToDashboard() {
        try {
            String fxmlPath;
            String windowTitle;

            // Vérifier le rôle de l'utilisateur pour rediriger vers la bonne interface
            if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
                fxmlPath = "/utilisateur/Utilisateur.fxml";
                windowTitle = "Smart Farm - Gestion Utilisateurs (Admin)";
            } else if (currentUser != null && "RESPONSABLE_STOCK".equalsIgnoreCase(currentUser.getRole().name())) {
                fxmlPath = "/produits/consulterproduit.fxml";
                windowTitle = "Smart Farm - Gestion des Stocks et Produits";
            } else {
                fxmlPath = "/recoltes/Dashboard.fxml";
                windowTitle = "Smart Farm - Espace Agriculteur";
            }

            System.out.println("🔄 Redirection vers: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                System.err.println("❌ Fichier FXML introuvable: " + fxmlPath);
                loader = new FXMLLoader(getClass().getResource("/utilisateur/Utilisateur.fxml"));
            }

            Parent root = loader.load();

            // Obtenir le stage — essayer plusieurs sources
            Stage stage = null;
            if (backToDashboardButton != null && backToDashboardButton.getScene() != null) {
                stage = (Stage) backToDashboardButton.getScene().getWindow();
            } else if (logoutButton != null && logoutButton.getScene() != null) {
                stage = (Stage) logoutButton.getScene().getWindow();
            } else if (chatContainer != null && chatContainer.getScene() != null) {
                stage = (Stage) chatContainer.getScene().getWindow();
            }

            // Fallback : chercher dans les fenêtres ouvertes
            if (stage == null) {
                stage = (Stage) javafx.stage.Window.getWindows().stream()
                        .filter(javafx.stage.Window::isShowing)
                        .findFirst()
                        .orElse(null);
            }

            if (stage == null) {
                System.err.println("⚠️ Aucun stage trouvé, création d'un nouveau stage");
                stage = new Stage();
            }

            Scene scene = new Scene(root);

            // Charger les CSS
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.show();

            System.out.println("✅ Retour à l'interface principale réussi");

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'interface: " + e.getMessage());
            showError("Erreur lors du chargement du tableau de bord");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearChat() {
        chatContainer.getChildren().clear();
        showWelcomeMessage();
    }

    @FXML
    private void exportChat() {
        // TODO: Implement chat export functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Chat");
        alert.setHeaderText(null);
        alert.setContentText("Cette fonctionnalité sera bientôt disponible!");
        alert.showAndWait();
    }
}