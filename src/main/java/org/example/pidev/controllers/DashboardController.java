package org.example.pidev.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.pidev.controllers.culture.ConsulterCultureController;
import org.example.pidev.controllers.parcelles.ConsulterParcelleController;
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.*;
import org.example.pidev.services.ahmed.ClientService;
import org.example.pidev.services.ahmed.VenteService;
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;
import org.example.pidev.utils.Session;
import org.example.pidev.utils.ThemeManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Contrôleur principal du Dashboard unifié.
 * UNE seule sidebar dont le contenu (stats, météo, alertes, historique, mode sombre)
 * change dynamiquement selon le module actif.
 */
public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarBox;
    @FXML private ScrollPane centerScroll;

    // Navigation buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnRecoltes;
    @FXML private Button btnRendements;
    @FXML private Button btnSoil;
    @FXML private Button btnPredictions;
    @FXML private Button btnArchive;
    @FXML private Button btnParcelles;
    @FXML private Button btnCultures;
    @FXML private Button btnChatbot;

    // Ventes & Clients buttons
    @FXML private Button btnClients;
    @FXML private Button btnVentes;
    @FXML private Button btnRapports;
    @FXML private Button btnFidelite;
    @FXML private Button btnRelance;
    @FXML private Button btnConversion;

    // Zone dynamique
    @FXML private VBox dynamicStatsContainer;

    // Météo
    @FXML private VBox weatherSection;
    @FXML private Label lblWeatherEmoji;
    @FXML private Label lblWeatherTemp;
    @FXML private Label lblWeatherDesc;
    @FXML private Label lblWeatherCity;
    @FXML private Label lblWeatherHumidity;
    @FXML private Label lblWeatherWind;
    @FXML private Label lblWeatherAdvice;

    // Alertes, Historique, Dark Mode, Horloge
    @FXML private VBox vboxAlerts;
    @FXML private ListView<String> lvHistorique;
    @FXML private ToggleButton btnDarkMode;
    @FXML private Label lblDateTime;

    // Profil et Déconnexion
    @FXML private Button btnProfil;
    @FXML private Button btnDeconnexion;

    // Services
    private RecolteService recolteService;
    private RendementService rendementService;
    private ParcelleService parcelleService;
    private CultureService cultureService;
    private WeatherService weatherService;
    private org.example.pidev.services.ahmed.ClientService ahmedClientService;
    private org.example.pidev.services.ahmed.VenteService ahmedVenteService;

    @FXML
    public void initialize() {
        System.out.println("✅ DashboardController initialisé");
        try {
            recolteService = new RecolteService();
            rendementService = new RendementService();
            parcelleService = new ParcelleService();
            cultureService = new CultureService();
            weatherService = new WeatherService();
            ahmedClientService = new org.example.pidev.services.ahmed.ClientService();
            ahmedVenteService = new org.example.pidev.services.ahmed.VenteService();

            // Horloge en temps réel
            startClock();

            // Historique des actions
            if (lvHistorique != null) {
                lvHistorique.setItems(ActionHistoryService.getInstance().getHistory());
            }

            // Alertes
            loadAlerts();

            // Dark mode
            setupDarkMode();

            // Profil et Déconnexion
            setupProfilDeconnexion();

            // Charger la vue par défaut
            loadDashboardView();
            setupNavigationButtons();
            applySidebarMode("default");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    // ==================== HORLOGE ====================

    private void startClock() {
        if (lblDateTime == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            lblDateTime.setText(LocalDateTime.now().format(fmt))
        ));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // ==================== DARK MODE ====================

    private void setupDarkMode() {
        if (btnDarkMode == null) return;
        ThemeManager tm = ThemeManager.getInstance();
        btnDarkMode.setSelected(tm.isDarkMode());
        btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
        btnDarkMode.setOnAction(e -> {
            tm.toggleAndApply(rootPane.getScene().getRoot());
            btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
        });

        // Appliquer le thème courant au démarrage
        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null) {
                tm.applyTheme(rootPane.getScene().getRoot());
            }
        });
    }

    // ==================== PROFIL & DÉCONNEXION ====================

    private void setupProfilDeconnexion() {
        // Bouton Profil → ouvre profile_view.fxml en popup
        if (btnProfil != null) {
            btnProfil.setOnAction(e -> openProfile());
        }
        // Bouton Déconnexion → retour à LoginView.fxml
        if (btnDeconnexion != null) {
            btnDeconnexion.setOnAction(e -> logout());
        }
    }

    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent profileRoot = loader.load();

            // Passer l'utilisateur courant au ProfileController
            Object ctrl = loader.getController();
            if (ctrl instanceof org.example.pidev.controllers.utilisateur.ProfileController) {
                org.example.pidev.controllers.utilisateur.ProfileController profileCtrl =
                        (org.example.pidev.controllers.utilisateur.ProfileController) ctrl;
                if (Session.getCurrentUser() != null) {
                    profileCtrl.setUser(Session.getCurrentUser());
                }
            }

            Stage profileStage = new Stage();
            profileStage.initModality(Modality.APPLICATION_MODAL);
            profileStage.initOwner(rootPane.getScene().getWindow());
            profileStage.setTitle("Smart Farm - Mon Profil");
            profileStage.setScene(new Scene(profileRoot));
            profileStage.setResizable(false);
            profileStage.centerOnScreen();
            profileStage.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void logout() {
        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");
        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Vider la session
                Session.clear();
                System.out.println("🚪 Déconnexion réussie");

                // Charger la vue Login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
                Parent loginRoot = loader.load();

                Stage stage = (Stage) rootPane.getScene().getWindow();

                // D'abord dé-maximiser, sinon setWidth/setHeight ne fonctionnent pas
                stage.setMaximized(false);

                Scene loginScene = new Scene(loginRoot, 600, 500);

                // Charger le CSS
                var cssUrl = getClass().getResource("/smartfarmm.css");
                if (cssUrl != null) {
                    loginScene.getStylesheets().add(cssUrl.toExternalForm());
                }
                var css2 = getClass().getResource("/styles/smartfarm.css");
                if (css2 != null) {
                    loginScene.getStylesheets().add(css2.toExternalForm());
                }

                stage.setScene(loginScene);
                stage.setTitle("Smart Farm - Connexion");
                stage.setResizable(false);
                stage.setWidth(600);
                stage.setHeight(500);
                stage.centerOnScreen();

                // S'assurer que le bouton X ferme bien l'application
                stage.setOnCloseRequest(evt -> {
                    javafx.application.Platform.exit();
                    System.exit(0);
                });
            } catch (Exception e) {
                System.err.println("❌ Erreur déconnexion: " + e.getMessage());
            }
        }
    }

    // ==================== CHATBOT ====================

    private void openChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatbot_view.fxml"));
            Parent chatRoot = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(chatRoot);

            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Farm - Assistant Agricole");
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture chatbot: " + e.getMessage());
        }
    }

    // ==================== ALERTES ====================

    private void loadAlerts() {
        if (vboxAlerts == null) return;
        vboxAlerts.getChildren().clear();
        try {
            var cultures = cultureService.getAll();
            int alertCount = 0;
            for (Culture c : cultures) {
                if (c.getDateRecoltePrevue() == null) continue;
                long jours = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue());

                if (jours < 0) {
                    VBox card = createAlertCard("🚨 RÉCOLTE DÉPASSÉE",
                            c.getTypeCulture() + " — dépassée de " + Math.abs(jours) + " jours",
                            "rgba(239,68,68,0.15)", "#ef4444");
                    vboxAlerts.getChildren().add(card);
                    try { AnimationUtils.slideInFromLeft(card, alertCount * 150); } catch (Exception ignored) {}
                    alertCount++;
                } else if (jours <= 7) {
                    VBox card = createAlertCard("⚠️ Récolte proche",
                            c.getTypeCulture() + " — dans " + jours + " jour(s)",
                            "rgba(245,158,11,0.15)", "#f59e0b");
                    vboxAlerts.getChildren().add(card);
                    try { AnimationUtils.slideInFromLeft(card, alertCount * 150); } catch (Exception ignored) {}
                    alertCount++;
                }
                if (alertCount >= 5) break;
            }
            if (alertCount == 0) {
                Label noAlert = new Label("✅ Aucune alerte");
                noAlert.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-padding: 8;");
                vboxAlerts.getChildren().add(noAlert);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erreur alertes: " + e.getMessage());
        }
    }

    private VBox createAlertCard(String title, String message, String bgColor, String borderColor) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: " + borderColor + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + borderColor + ";");
        Label lblMsg = new Label(message);
        lblMsg.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.8);");
        lblMsg.setWrapText(true);
        card.getChildren().addAll(lblTitle, lblMsg);
        return card;
    }

    // ==================== MÉTÉO ====================

    private void showWeatherSection(boolean show) {
        if (weatherSection != null) {
            weatherSection.setVisible(show);
            weatherSection.setManaged(show);
        }
    }

    private void loadDefaultWeather() {
        showWeatherSection(true);
        setWeatherLoading();
        new Thread(() -> {
            WeatherService.WeatherData data = weatherService.getWeatherByCity("Tunis");
            Platform.runLater(() -> updateWeatherUI(data));
        }).start();
    }

    /**
     * Charge la météo pour une parcelle spécifique (appelé quand on sélectionne une parcelle).
     */
    private void loadWeatherForParcelle(Parcelle parcelle) {
        if (parcelle == null || parcelle.getLocalisation() == null || parcelle.getLocalisation().isBlank()) {
            return;
        }
        showWeatherSection(true);
        setWeatherLoading();
        new Thread(() -> {
            WeatherService.WeatherData data = weatherService.getWeatherByLocation(parcelle.getLocalisation());
            Platform.runLater(() -> updateWeatherUI(data));
        }).start();
    }

    private void setWeatherLoading() {
        if (lblWeatherEmoji != null) lblWeatherEmoji.setText("⏳");
        if (lblWeatherTemp != null) lblWeatherTemp.setText("...");
        if (lblWeatherDesc != null) lblWeatherDesc.setText("Chargement...");
        if (lblWeatherCity != null) lblWeatherCity.setText("");
        if (lblWeatherHumidity != null) lblWeatherHumidity.setText("...");
        if (lblWeatherWind != null) lblWeatherWind.setText("...");
        if (lblWeatherAdvice != null) lblWeatherAdvice.setText("");
    }

    private void updateWeatherUI(WeatherService.WeatherData data) {
        if (data != null) {
            if (lblWeatherEmoji != null) lblWeatherEmoji.setText(data.getWeatherEmoji());
            if (lblWeatherTemp != null) lblWeatherTemp.setText(data.getFormattedTemp());
            if (lblWeatherDesc != null) lblWeatherDesc.setText(data.getDescription());
            if (lblWeatherCity != null) lblWeatherCity.setText(data.getCityName());
            if (lblWeatherHumidity != null) lblWeatherHumidity.setText(data.getHumidity() + "%");
            if (lblWeatherWind != null) lblWeatherWind.setText(String.format("%.0f km/h", data.getWindSpeed()));
            if (lblWeatherAdvice != null) lblWeatherAdvice.setText(data.getAgricultureAdvice());
        } else {
            if (lblWeatherEmoji != null) lblWeatherEmoji.setText("⚠️");
            if (lblWeatherTemp != null) lblWeatherTemp.setText("N/A");
            if (lblWeatherDesc != null) lblWeatherDesc.setText("Météo indisponible");
            if (lblWeatherCity != null) lblWeatherCity.setText("");
            if (lblWeatherHumidity != null) lblWeatherHumidity.setText("N/A");
            if (lblWeatherWind != null) lblWeatherWind.setText("N/A");
            if (lblWeatherAdvice != null) lblWeatherAdvice.setText("⚠️ Vérifiez votre connexion");
        }
    }

    // ==================== NAVIGATION SETUP ====================

    private void setupNavigationButtons() {
        if (btnDashboard != null) btnDashboard.setOnAction(e -> { loadDashboardView(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnDashboard); });
        if (btnRecoltes != null) btnRecoltes.setOnAction(e -> { loadView("/Recolte.fxml", "Récoltes"); buildRecolteStats(); showWeatherSection(false); loadAlerts(); applySidebarMode("recolte"); setActiveButton(btnRecoltes); });
        if (btnRendements != null) btnRendements.setOnAction(e -> { loadView("/Rendement.fxml", "Rendements"); buildRendementStats(); showWeatherSection(false); loadAlerts(); applySidebarMode("rendement"); setActiveButton(btnRendements); });
        if (btnSoil != null) btnSoil.setOnAction(e -> { loadView("/SoilDashboard.fxml", "Analyse du Sol"); buildDefaultStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnSoil); });
        if (btnPredictions != null) btnPredictions.setOnAction(e -> { loadView("/AIPredictionsWidget.fxml", "Prédictions IA"); buildDefaultStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnPredictions); });
        if (btnArchive != null) btnArchive.setOnAction(e -> { loadView("/Archive.fxml", "Archive"); buildDefaultStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnArchive); });
        if (btnParcelles != null) btnParcelles.setOnAction(e -> { loadParcellesView(); applySidebarMode("default"); setActiveButton(btnParcelles); });
        if (btnCultures != null) btnCultures.setOnAction(e -> { loadCulturesView(); applySidebarMode("default"); setActiveButton(btnCultures); });
        if (btnChatbot != null) btnChatbot.setOnAction(e -> openChatbot());

        // Ventes & Clients navigation
        if (btnClients != null) btnClients.setOnAction(e -> { loadAhmedView("/ahmed/ListeClient.fxml", "Clients"); buildClientStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnClients); });
        if (btnVentes != null) btnVentes.setOnAction(e -> { loadAhmedView("/ahmed/ListeVente.fxml", "Ventes"); buildVenteStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnVentes); });
        if (btnRapports != null) btnRapports.setOnAction(e -> { loadAhmedView("/ahmed/StatistiquesVente.fxml", "Rapports Ventes"); buildVenteStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnRapports); });
        if (btnFidelite != null) btnFidelite.setOnAction(e -> { loadAhmedView("/ahmed/FideliteStats.fxml", "Fidélité"); buildClientStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnFidelite); });
        if (btnRelance != null) btnRelance.setOnAction(e -> { loadAhmedView("/ahmed/RelanceClient.fxml", "Relance Clients"); buildClientStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnRelance); });
        if (btnConversion != null) btnConversion.setOnAction(e -> { loadAhmedView("/ahmed/ConversionStats.fxml", "Conversion"); buildClientStats(); showWeatherSection(false); applySidebarMode("default"); setActiveButton(btnConversion); });
    }

    // ==================== VIEW LOADERS ====================

    /** Charge une vue dans le ScrollPane (pour les vues scrollables) */
    private void loadIntoScroll(Parent view) {
        if (rootPane == null) return;
        // Remettre le ScrollPane au center si nécessaire
        if (rootPane.getCenter() != centerScroll && centerScroll != null) {
            rootPane.setCenter(centerScroll);
        }
        if (centerScroll != null) centerScroll.setContent(view);
    }

    /** Charge une vue directement au center (pour AnchorPane comme Parcelles/Cultures qui doivent remplir l'espace) */
    private void loadIntoCenter(Parent view) {
        if (rootPane != null) rootPane.setCenter(view);
    }

    private void loadDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardDefault.fxml"));
            Parent view = loader.load();
            loadIntoScroll(view);

            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardDefaultController) {
                DashboardDefaultController dCtrl = (DashboardDefaultController) ctrl;
                int tr = recolteService.getAll().size();
                int trd = rendementService.getAll().size();
                double surf = rendementService.getAll().stream().mapToDouble(r -> r.getSurfaceExploitee()).sum();
                int tp = parcelleService.getAll().size();
                dCtrl.updateStats(tr, trd, surf);
                dCtrl.updateParcelles(tp);

                // Wire quick access cards to sidebar navigation
                dCtrl.setOnNavigate(target -> {
                    switch (target) {
                        case "recoltes":
                            if (btnRecoltes != null) btnRecoltes.fire();
                            break;
                        case "rendements":
                            if (btnRendements != null) btnRendements.fire();
                            break;
                        case "parcelles":
                            if (btnParcelles != null) btnParcelles.fire();
                            break;
                        case "cultures":
                            if (btnCultures != null) btnCultures.fire();
                            break;
                        case "sol":
                            if (btnSoil != null) btnSoil.fire();
                            break;
                        case "predictions":
                            if (btnPredictions != null) btnPredictions.fire();
                            break;
                        case "archive":
                            if (btnArchive != null) btnArchive.fire();
                            break;
                        case "clients":
                            if (btnClients != null) btnClients.fire();
                            break;
                        case "ventes":
                            if (btnVentes != null) btnVentes.fire();
                            break;
                    }
                });
            }
            buildDefaultStats();
            showWeatherSection(false);
            loadAlerts();
            setActiveButton(btnDashboard);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement Dashboard: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath, String name) {
        try {
            System.out.println("📂 Chargement: " + name);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            loadIntoScroll(view);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void loadParcellesView() {
        try {
            System.out.println("🌱 Chargement Parcelles");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consulterparcelle.fxml"));
            Parent view = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof ConsulterParcelleController) {
                ConsulterParcelleController parcelleCtrl = (ConsulterParcelleController) ctrl;
                parcelleCtrl.hideSidebar();
                parcelleCtrl.setOnParcelleSelected(parcelle -> loadWeatherForParcelle(parcelle));
            }
            loadIntoCenter(view);

            buildParcelleStats();
            loadDefaultWeather();
            loadAlerts();
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement Parcelles: " + e.getMessage());
        }
    }

    private void loadCulturesView() {
        try {
            System.out.println("🌿 Chargement Cultures");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consulterculture.fxml"));
            Parent view = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof ConsulterCultureController) {
                ((ConsulterCultureController) ctrl).hideSidebar();
            }
            loadIntoCenter(view);

            buildCultureStats();
            showWeatherSection(false); // Pas de météo pour Cultures
            loadAlerts();
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement Cultures: " + e.getMessage());
        }
    }

    /** Charge une vue ahmed (Ventes/Clients) dans le ScrollPane */
    private void loadAhmedView(String fxmlPath, String name) {
        try {
            System.out.println("📂 Chargement ahmed: " + name);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            loadIntoScroll(view);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== DYNAMIC SIDEBAR STATS BUILDERS ====================

    /** Stats par défaut : Récoltes + Rendements + Surface */
    private void buildDefaultStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();

        try {
            int totalRecoltes = recolteService.getAll().size();
            int totalRendements = rendementService.getAll().size();
            double surfaceTotale = rendementService.getAll().stream().mapToDouble(r -> r.getSurfaceExploitee()).sum();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES"),
                statCard("🌾", "#f59e0b", "#d97706", "Total Récoltes", String.valueOf(totalRecoltes), "white"),
                statCard("⏰", "#ef4444", "#dc2626", "Total Rendements", String.valueOf(totalRendements), "#fca5a5"),
                statCard("📊", "#8b5cf6", "#7c3aed", "Surface Totale", String.format("%.2f ha", surfaceTotale), "#c4b5fd")
            );
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur chargement stats"));
        }
    }

    /** Stats pour Récoltes */
    private void buildRecolteStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            int total = recolteService.getAll().size();
            double surfTot = rendementService.getAll().stream().mapToDouble(r -> r.getSurfaceExploitee()).sum();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES RÉCOLTES"),
                statCard("🌾", "#f59e0b", "#d97706", "Total Récoltes", String.valueOf(total), "white"),
                statCard("📊", "#8b5cf6", "#7c3aed", "Surface Totale", String.format("%.2f ha", surfTot), "#c4b5fd")
            );
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats récoltes"));
        }
    }

    /** Stats pour Rendements */
    private void buildRendementStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            int total = rendementService.getAll().size();
            double surfTot = rendementService.getAll().stream().mapToDouble(r -> r.getSurfaceExploitee()).sum();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES RENDEMENTS"),
                statCard("📈", "#ef4444", "#dc2626", "Total Rendements", String.valueOf(total), "#fca5a5"),
                statCard("🌍", "#8b5cf6", "#7c3aed", "Surface Exploitée", String.format("%.2f ha", surfTot), "#c4b5fd")
            );
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats rendements"));
        }
    }

    /** Stats pour Parcelles : total, superficie, répartition par état */
    private void buildParcelleStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            List<Parcelle> parcelles = parcelleService.getAll();
            int total = parcelles.size();
            double superficie = parcelles.stream().mapToDouble(Parcelle::getSuperficie).sum();
            long actives = parcelles.stream().filter(p -> "Active".equalsIgnoreCase(p.getEtat())).count();
            long repos = parcelles.stream().filter(p -> "En repos".equalsIgnoreCase(p.getEtat())).count();
            long exploitees = parcelles.stream().filter(p -> "Exploitée".equalsIgnoreCase(p.getEtat())).count();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES PARCELLES"),
                statCard("📊", "#667eea", "#764ba2", "Total Parcelles", String.valueOf(total), "white"),
                statCard("📐", "#11998e", "#38ef7d", "Superficie Totale", String.format("%.0f m²", superficie), "white")
            );

            // Répartition par état
            VBox etatBox = new VBox(8);
            etatBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 12; -fx-padding: 12;");
            Label etatTitle = new Label("Répartition par état");
            etatTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold;");
            etatBox.getChildren().add(etatTitle);
            etatBox.getChildren().add(etatRow("●", "#4ade80", "Actives", String.valueOf(actives), "#4ade80"));
            etatBox.getChildren().add(etatRow("●", "#fbbf24", "En Repos", String.valueOf(repos), "#fbbf24"));
            etatBox.getChildren().add(etatRow("●", "#60a5fa", "Exploitées", String.valueOf(exploitees), "#60a5fa"));
            dynamicStatsContainer.getChildren().add(etatBox);
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats parcelles"));
        }
    }

    /** Stats pour Cultures : total, répartition par état croissance */
    private void buildCultureStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            List<Culture> cultures = cultureService.getAll();
            int total = cultures.size();
            long germination = cultures.stream().filter(c -> "Germination".equalsIgnoreCase(c.getEtatCroissance())).count();
            long croissance = cultures.stream().filter(c -> "Croissance".equalsIgnoreCase(c.getEtatCroissance())).count();
            long floraison = cultures.stream().filter(c -> "Floraison".equalsIgnoreCase(c.getEtatCroissance())).count();
            long maturite = cultures.stream().filter(c -> "Maturité".equalsIgnoreCase(c.getEtatCroissance())).count();
            long recolteProcheCount = cultures.stream().filter(c -> {
                if (c.getDateRecoltePrevue() == null) return false;
                long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), c.getDateRecoltePrevue());
                return days >= 0 && days <= 7;
            }).count();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES CULTURES"),
                statCard("🌾", "#f59e0b", "#d97706", "Total Cultures", String.valueOf(total), "white"),
                statCard("⏰", "#ef4444", "#dc2626", "Récolte Proche (≤7j)", String.valueOf(recolteProcheCount), "#fca5a5")
            );

            // État de croissance
            VBox etatBox = new VBox(10);
            etatBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 15; -fx-padding: 14;");
            Label etatTitle = new Label("État de croissance");
            etatTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold;");
            etatBox.getChildren().add(etatTitle);
            etatBox.getChildren().add(etatRow("🌱", null, "Germination", String.valueOf(germination), "#4ade80"));
            etatBox.getChildren().add(etatRow("🌿", null, "Croissance", String.valueOf(croissance), "#60a5fa"));
            etatBox.getChildren().add(etatRow("🌸", null, "Floraison", String.valueOf(floraison), "#f472b6"));
            etatBox.getChildren().add(etatRow("🌻", null, "Maturité", String.valueOf(maturite), "#fbbf24"));
            dynamicStatsContainer.getChildren().add(etatBox);
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats cultures"));
        }
    }

    /** Stats pour Clients */
    private void buildClientStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            int totalClients = ahmedClientService.getAll().size();
            int inactifs = ahmedClientService.getInactiveClientsCount();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES CLIENTS"),
                statCard("👥", "#3b82f6", "#2563eb", "Total Clients", String.valueOf(totalClients), "white"),
                statCard("⚠️", "#ef4444", "#dc2626", "Clients Inactifs", String.valueOf(inactifs), "#fca5a5")
            );
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats clients"));
        }
    }

    /** Stats pour Ventes */
    private void buildVenteStats() {
        if (dynamicStatsContainer == null) return;
        dynamicStatsContainer.getChildren().clear();
        try {
            var ventes = ahmedVenteService.getAll();
            int totalVentes = ventes.size();
            double montantTotal = ventes.stream().mapToDouble(v -> v.getMontantTotal()).sum();

            dynamicStatsContainer.getChildren().addAll(
                sectionTitle("📊 STATISTIQUES VENTES"),
                statCard("💰", "#f59e0b", "#d97706", "Total Ventes", String.valueOf(totalVentes), "white"),
                statCard("💵", "#10b981", "#059669", "Montant Total", String.format("%.2f TND", montantTotal), "#6ee7b7")
            );
        } catch (Exception e) {
            dynamicStatsContainer.getChildren().add(errorLabel("Erreur stats ventes"));
        }
    }

    // ==================== UI BUILDER HELPERS ====================

    private Label sectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.4); -fx-font-weight: bold;");
        return lbl;
    }

    private VBox statCard(String emoji, String color1, String color2, String title, String value, String valueColor) {
        StackPane icon = new StackPane();
        icon.setPrefSize(45, 45);
        icon.setStyle("-fx-background-color: linear-gradient(to bottom right, " + color1 + ", " + color2 + "); -fx-background-radius: 12;");
        Label iconLabel = new Label(emoji);
        iconLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        icon.getChildren().add(iconLabel);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + valueColor + ";");

        VBox textBox = new VBox(2, titleLbl, valueLbl);
        HBox row = new HBox(12, icon, textBox);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, row);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 15; -fx-padding: 14;");
        return card;
    }

    private HBox etatRow(String icon, String dotColor, String label, String value, String valueColor) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        if (dotColor != null) {
            StackPane dot = new StackPane();
            dot.setPrefSize(10, 10);
            dot.setStyle("-fx-background-color: " + dotColor + "; -fx-background-radius: 5;");
            row.getChildren().add(dot);
        } else {
            Label emojiLbl = new Label(icon);
            emojiLbl.setStyle("-fx-font-size: 14px;");
            row.getChildren().add(emojiLbl);
        }

        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.7);");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + valueColor + ";");

        row.getChildren().addAll(nameLbl, spacer, valLbl);
        return row;
    }

    private Label errorLabel(String msg) {
        Label lbl = new Label("⚠️ " + msg);
        lbl.setStyle("-fx-text-fill: #fca5a5; -fx-font-size: 11px;");
        return lbl;
    }

    // ==================== SIDEBAR MODE ====================

    private void applySidebarMode(String mode) {
        if (sidebarBox == null) return;
        if (!sidebarBox.getStyleClass().contains("sidebar")) sidebarBox.getStyleClass().add("sidebar");
        sidebarBox.getStyleClass().removeAll("sidebar-recolte", "sidebar-rendement", "sidebar-soil", "sidebar-predictions");
        switch (mode) {
            case "recolte": sidebarBox.getStyleClass().add("sidebar-recolte"); break;
            case "rendement": sidebarBox.getStyleClass().add("sidebar-rendement"); break;
            case "soil": sidebarBox.getStyleClass().add("sidebar-soil"); break;
            case "predictions": sidebarBox.getStyleClass().add("sidebar-predictions"); break;
        }
    }

    private void setActiveButton(Button active) {
        Button[] all = {btnDashboard, btnRecoltes, btnRendements, btnSoil, btnPredictions, btnArchive, btnParcelles, btnCultures, btnClients, btnVentes, btnRapports, btnFidelite, btnRelance, btnConversion};
        for (Button b : all) {
            if (b != null) b.getStyleClass().removeAll("sidebar-button-active");
        }
        if (active != null && !active.getStyleClass().contains("sidebar-button-active")) {
            active.getStyleClass().add("sidebar-button-active");
        }
    }
}
