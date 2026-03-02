package org.example.pidev.controllers.ahmed;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.pidev.services.ahmed.ClientService;
import org.example.pidev.services.ahmed.GroqService;
import org.example.pidev.services.ahmed.VenteService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.concurrent.Executors;

/**
 * DashboardController - Contrôle la navigation dynamique et la gestion des vues
 * Gère le chargement des sous-pages dans le contentArea et la fenêtre de chat IA
 */
public class DashboardController {

    @FXML private AnchorPane contentArea;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox sidebar;
    @FXML private VBox statsBox;
    @FXML private Label dateLabel;

    // Chat Components
    @FXML private AnchorPane chatContainerPane;
    @FXML private VBox chatWindow;
    @FXML private Button btnFloatingChat;
    @FXML private Button btnCloseChat;
    @FXML private TextField chatInput;
    @FXML private Button btnSendMessage;
    @FXML private VBox chatMessagesBox;
    @FXML private ScrollPane chatMessagesPane;


    @FXML private Button btnDashboard;
    @FXML private Button btnClients;
    @FXML private Button btnVentes;
    @FXML private Button btnRapports;
    @FXML private Button btnFidelite;
    @FXML private Button btnRelance;
    @FXML private Button btnConversion;

    private ClientService clientService;
    private VenteService venteService;
    private GroqService groqService;
    private String currentView = "dashboard";


    @FXML
    public void initialize() {
        clientService = new ClientService();
        venteService = new VenteService();
        groqService = new GroqService(clientService, venteService);

        // Définir la date
        dateLabel.setText("📅 " + LocalDate.now());

        // Charger les statistiques de la sidebar
        loadSidebarStatistics();

        // Charger la page d'accueil (DashboardHome.fxml)
        try {
            loadDashboardHome();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement du dashboard home: " + e.getMessage());
            e.printStackTrace();
        }

        // ========== CONFIGURATION CHATBOT SIMPLE ==========
        // Assurer que le bouton est au premier plan
        btnFloatingChat.toFront();

        // Setup chat window handlers (Enter pour envoyer message)
        setupChatHandlers();
    }


    /**
     * Configure les handlers pour la fenêtre de chat
     */
    private void setupChatHandlers() {
        chatInput.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                sendChatMessage();
                event.consume();
            }
        });
    }


    // ...existing code...

    /**
     * Charge une vue FXML dans le contentArea
     * Supporte deux formats:
     * - "ListeVente.fxml" (chemin relatif simplifié)
     * - "/org/example/pidev/ListeVente.fxml" (chemin absolu)
     *
     * @param fxmlFileName Nom du fichier ou chemin complet
     * @param viewName Nom de la vue pour le tracking
     */
    public void loadView(String fxmlFileName, String viewName) throws IOException {
        try {
            // Construire le chemin complet si seulement le nom de fichier est fourni
            String fullPath = fxmlFileName.startsWith("/") ?
                fxmlFileName :
                "/ahmed/" + fxmlFileName;

            System.out.println("📂 Chargement: " + fullPath);

            // Charger la ressource FXML
            URL url = getClass().getResource(fullPath);
            if (url == null) {
                throw new IOException("FXML non trouvé: " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();

            // Vérifier que la vue est bien un AnchorPane ou wrapper dans un AnchorPane
            if (!(view instanceof AnchorPane)) {
                // Si ce n'est pas un AnchorPane, l'envelopper
                AnchorPane wrapper = new AnchorPane(view);
                AnchorPane.setTopAnchor(view, 0.0);
                AnchorPane.setBottomAnchor(view, 0.0);
                AnchorPane.setLeftAnchor(view, 0.0);
                AnchorPane.setRightAnchor(view, 0.0);
                view = wrapper;
            } else {
                // Anchorer les 4 côtés pour le responsive
                AnchorPane anchorPane = (AnchorPane) view;
                for (Node child : anchorPane.getChildren()) {
                    AnchorPane.setTopAnchor(child, 0.0);
                    AnchorPane.setBottomAnchor(child, 0.0);
                    AnchorPane.setLeftAnchor(child, 0.0);
                    AnchorPane.setRightAnchor(child, 0.0);
                }
            }

            // Remplacer le contenu
            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            currentView = viewName;
            updateSidebarButtons(viewName);
            scrollPane.setVvalue(0); // Scroll to top

            System.out.println("✅ Vue chargée: " + viewName);

        } catch (IOException e) {
            System.err.println("❌ Erreur chargement: " + fxmlFileName);
            e.printStackTrace();
            showError("Erreur de chargement", "Impossible de charger " + fxmlFileName + "\n\n" + e.getMessage());
            throw e;
        }
    }

    /**
     * Met en évidence le bouton actif dans la sidebar
     */
    private void updateSidebarButtons(String viewName) {
        btnDashboard.getStyleClass().remove("sidebar-button-active");
        btnClients.getStyleClass().remove("sidebar-button-active");
        btnVentes.getStyleClass().remove("sidebar-button-active");
        btnRapports.getStyleClass().remove("sidebar-button-active");
        btnFidelite.getStyleClass().remove("sidebar-button-active");
        btnRelance.getStyleClass().remove("sidebar-button-active");
        btnConversion.getStyleClass().remove("sidebar-button-active");

        switch (viewName) {
            case "dashboard" -> btnDashboard.getStyleClass().add("sidebar-button-active");
            case "clients" -> btnClients.getStyleClass().add("sidebar-button-active");
            case "ventes" -> btnVentes.getStyleClass().add("sidebar-button-active");
            case "rapports" -> btnRapports.getStyleClass().add("sidebar-button-active");
            case "fidelite" -> btnFidelite.getStyleClass().add("sidebar-button-active");
            case "relance" -> btnRelance.getStyleClass().add("sidebar-button-active");
            case "conversion" -> btnConversion.getStyleClass().add("sidebar-button-active");
        }
    }

    /**
     * Charge la page d'accueil (DashboardHome.fxml) dans le contentArea
     */
    public void loadDashboardHome() throws IOException {
        loadView("DashboardHome.fxml", "dashboard");
    }

    /**
     * Charge les statistiques dans la sidebar
     */
    private void loadSidebarStatistics() {
        statsBox.getChildren().clear();

        int clientCount = clientService.getAll().size();
        int venteCount = venteService.getAll().size();

        VBox statBox1 = createStatBox("👥 Clients", String.valueOf(clientCount));
        VBox statBox2 = createStatBox("📈 Ventes", String.valueOf(venteCount));

        statsBox.getChildren().addAll(statBox1, statBox2);
    }


    private VBox createStatBox(String label, String value) {
        VBox box = new VBox(3);
        box.getStyleClass().add("minimal-stat-box");

        Label labelL = new Label(label);
        labelL.getStyleClass().add("minimal-stat-label");

        Label valueL = new Label(value);
        valueL.getStyleClass().add("minimal-stat-value");

        box.getChildren().addAll(labelL, valueL);
        return box;
    }

    // ========== EVENT HANDLERS ==========

    @FXML private void onDashboardClick() {
        try {
            chatWindow.setVisible(false);
            loadDashboardHome();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le dashboard home");
        }
    }

    @FXML private void onClientsClick() {
        try {
            chatWindow.setVisible(false);
            loadView("ListeClient.fxml", "clients");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page des clients");
        }
    }

    @FXML private void onVentesClick() {
        try {
            chatWindow.setVisible(false);
            loadView("ListeVente.fxml", "ventes");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page des ventes");
        }
    }

    @FXML private void onRapportsClick() {
        try {
            chatWindow.setVisible(false);
            loadView("StatistiquesVente.fxml", "rapports");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page des rapports");
        }
    }

    @FXML private void onFideliteClick() {
        try {
            chatWindow.setVisible(false);
            loadView("FideliteStats.fxml", "fidelite");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page fidélité");
        }
    }

    @FXML private void onRelanceClick() {
        try {
            chatWindow.setVisible(false);
            loadView("RelanceClient.fxml", "relance");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page relance");
        }
    }

    @FXML private void onConversionClick() {
        try {
            chatWindow.setVisible(false);
            loadView("ConversionStats.fxml", "conversion");
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page conversion");
        }
    }

    @FXML private void onQuitClick() {
        System.exit(0);
    }

    /**
     * Affiche/Cache la fenêtre de chat
     * Simple toggle - la fenêtre apparaît/disparaît
     */
    @FXML
    public void toggleChat() {
        chatWindow.setVisible(!chatWindow.isVisible());
        if (chatWindow.isVisible()) {
            chatInput.requestFocus();
            System.out.println("✅ Chat ouvert");
        } else {
            System.out.println("✅ Chat fermé");
        }
    }

    /**
     * Envoie un message via le chat IA
     */
    @FXML
    public void sendChatMessage() {
        String userMessage = chatInput.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // Ajouter le message utilisateur à l'affichage
        addChatMessage("user", userMessage);
        chatInput.clear();

        // Désactiver le bouton d'envoi pendant le traitement
        btnSendMessage.setDisable(true);

        // Traiter le message en arrière-plan
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String dbContext = venteService.getAIDatabaseContext();
                String response = groqService.chat(userMessage, dbContext);

                // Ajouter la réponse de l'IA
                javafx.application.Platform.runLater(() -> {
                    addChatMessage("ai", response);
                    btnSendMessage.setDisable(false);
                    scrollChatToBottom();
                });
            } catch (Exception e) {
                System.err.println("❌ Erreur chat: " + e.getMessage());
                javafx.application.Platform.runLater(() -> {
                    addChatMessage("error", "Erreur: " + e.getMessage());
                    btnSendMessage.setDisable(false);
                });
            }
        });
    }

    /**
     * Ajoute un message au chat
     */
    private void addChatMessage(String sender, String message) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(8));
        messageBox.setSpacing(8);

        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(250);

        if ("user".equals(sender)) {
            messageBox.getStyleClass().add("chat-message-user");
            msgLabel.getStyleClass().add("chat-text-user");
            messageBox.setStyle("-fx-alignment: CENTER_RIGHT;");
        } else if ("ai".equals(sender)) {
            messageBox.getStyleClass().add("chat-message-ai");
            msgLabel.getStyleClass().add("chat-text-ai");
            messageBox.setStyle("-fx-alignment: CENTER_LEFT;");
        } else {
            messageBox.getStyleClass().add("chat-message-error");
            msgLabel.getStyleClass().add("chat-text-error");
        }

        messageBox.getChildren().add(msgLabel);
        chatMessagesBox.getChildren().add(messageBox);
    }

    /**
     * Scroll la fenêtre de chat vers le bas
     */
    private void scrollChatToBottom() {
        chatMessagesPane.setVvalue(1.0);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

