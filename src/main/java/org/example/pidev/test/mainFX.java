package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.example.pidev.utils.FaceRecognitionUtil;
import org.example.pidev.utils.Session;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Point d'entrée unique de l'application Smart Farm.
 * Fusionne l'ancien LauncherGUI et mainFX en un seul launcher unifié.
 */
public class mainFX extends Application {

    private static final Logger logger = Logger.getLogger(mainFX.class.getName());
    private static final String LOG_FILE = "launcher_debug.log";

    // ==================== OpenCV Init ====================
    static {
        try {
            OpenCV.loadLocally();
            System.out.println("✅ OpenCV loaded successfully via OpenCV.loadLocally()");
            System.out.println("OpenCV version: " + org.opencv.core.Core.VERSION);
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load OpenCV");
            System.err.println("   Error: " + e.getMessage());
            try {
                String dllPath = System.getProperty("opencv.dll.path", "");
                if (!dllPath.isEmpty()) {
                    System.load(dllPath);
                    System.out.println("✅ OpenCV chargé manuellement (fallback)");
                } else {
                    System.err.println("❌ Pas de chemin OpenCV configuré. Définir -Dopencv.dll.path=<chemin>");
                }
            } catch (Exception ex) {
                System.err.println("❌ Échec du fallback: " + ex.getMessage());
            }
        }
    }

    // ==================== Debug Logging ====================
    private void log(String msg) {
        System.out.println(msg);
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(msg);
        } catch (IOException ignored) {}
    }

    private void logException(String prefix, Exception e) {
        log(prefix + ": " + e.getMessage());
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            e.printStackTrace(out);
        } catch (IOException ignored) {}
    }

    // ==================== Application Start ====================
    @Override
    public void start(Stage stage) {
        // Nettoyer le log précédent
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE))) { out.print(""); } catch (IOException ignored) {}

        // Quand la fenêtre principale est fermée → quitter l'application
        javafx.application.Platform.setImplicitExit(true);
        stage.setOnCloseRequest(event -> {
            System.out.println("👋 Fermeture de l'application...");
            javafx.application.Platform.exit();
            System.exit(0);
        });

        afficherBanniere();
        initialiserFaceID();

        String vueDemarrage = obtenirVueDemarrage();
        log("🚀 Vue de démarrage: " + vueDemarrage);

        switch (vueDemarrage) {
            case "dashboard":
                afficherDashboard(stage);
                break;
            case "parcelles":
                afficherParcelles(stage);
                break;
            case "produits":
                afficherProduits(stage);
                break;
            case "login":
            default:
                afficherLogin(stage);
                break;
        }
    }

    /**
     * Détermine quelle vue afficher au démarrage
     */
    private String obtenirVueDemarrage() {
        if (Session.getCurrentUser() != null) {
            return "dashboard";
        }
        return System.getProperty("app.start.view", "login");
    }

    private void afficherBanniere() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 SMART FARM APPLICATION - VERSION INTÉGRÉE");
        System.out.println("=".repeat(60) + "\n");
        System.out.println("📋 Modules disponibles:");
        System.out.println("   - Login (authentification)");
        System.out.println("   - Dashboard (récoltes & rendements)");
        System.out.println("   - Parcelles & Cultures");
        System.out.println("   - Produits & Stocks");
        System.out.println("=".repeat(60));
    }

    private void initialiserFaceID() {
        log("📂 Initializing Face ID System...");
        try {
            FaceRecognitionUtil.ensureFaceDirectory();
            log("✅ Face ID System initialized successfully");
        } catch (Exception e) {
            log("⚠️ Warning: Face ID initialization failed: " + e.getMessage());
        }
    }

    // ==================== Afficher Dashboard Unifié ====================
    /**
     * Charge le Dashboard principal unifié (récoltes, rendements, parcelles, cultures)
     */
    public void afficherDashboard(Stage stage) {
        try {
            log("📊 Loading Dashboard...");

            URL fxmlUrl = getClass().getResource("/recoltes/Dashboard.fxml");
            if (fxmlUrl == null) {
                log("⚠️ Dashboard.fxml not found, trying DashboardDefault.fxml...");
                fxmlUrl = getClass().getResource("/recoltes/DashboardDefault.fxml");
            }
            if (fxmlUrl == null) {
                log("❌ No Dashboard FXML found.");
                afficherFallback(stage);
                return;
            }

            log("ℹ️ Chargement FXML depuis: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            chargerCSS(scene);

            stage.setTitle("Smart Farm - Dashboard");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.show();

            log("✅ Dashboard affiché avec succès");

        } catch (Exception e) {
            logException("❌ Error loading Dashboard", e);
            logger.log(Level.SEVERE, "Error loading Dashboard", e);
            afficherFallback(stage);
        }
    }

    // ==================== Afficher Login ====================
    private void afficherLogin(Stage stage) {
        try {
            log("📱 Loading Login View...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 650);
            chargerCSS(scene);

            stage.setTitle("Smart Farm - Connexion");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.setWidth(600);
            stage.setHeight(650);
            stage.centerOnScreen();
            stage.show();

            log("✅ Login window displayed successfully");
        } catch (Exception e) {
            logException("❌ Error loading Login View", e);
            logger.log(Level.SEVERE, "Error loading Login View", e);
            afficherDashboard(stage);
        }
    }

    // ==================== Afficher Parcelles ====================
    private void afficherParcelles(Stage stage) {
        try {
            log("📱 Loading Parcelles View...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parcelles/consulterparcelle.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            chargerCSS(scene);

            stage.setTitle("Smart Farm - Gestion des Parcelles");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.show();

            log("✅ Parcelles window displayed successfully");
        } catch (Exception e) {
            logException("❌ Error loading Parcelles View", e);
            logger.log(Level.SEVERE, "Error loading Parcelles View", e);
            afficherFallback(stage);
        }
    }

    // ==================== Afficher Produits ====================
    private void afficherProduits(Stage stage) {
        try {
            log("📱 Loading Produits View...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/produits/consulterproduit.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            chargerCSS(scene);

            stage.setTitle("Smart Farm - Gestion des Produits");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.show();

            log("✅ Produits window displayed successfully");
        } catch (Exception e) {
            logException("❌ Error loading Produits View", e);
            logger.log(Level.SEVERE, "Error loading Produits View", e);
            afficherParcelles(stage);
        }
    }

    // ==================== Fallback ====================
    private void afficherFallback(Stage stage) {
        try {
            log("⚠️ Loading fallback view...");
            javafx.scene.control.Label label = new javafx.scene.control.Label("Application Smart Farm - Mode Fallback");
            label.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");
            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(label);
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Smart Farm - Fallback");
            stage.setScene(scene);
            stage.show();
            log("⚠️ Fallback view displayed");
        } catch (Exception ex) {
            System.err.println("❌ Critical error: " + ex.getMessage());
            System.exit(1);
        }
    }

    // ==================== CSS ====================
    private void chargerCSS(Scene scene) {
        try {
            // Charger les deux CSS pour une couverture complète
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());

            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            if (css1 == null && css2 == null) {
                System.err.println("⚠️ Warning: No CSS file found");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load CSS: " + e.getMessage());
        }
    }

    // ==================== Main ====================
    public static void main(String[] args) {
        System.out.println("\n" + "⭐".repeat(50));
        System.out.println("🌟 SMART FARM - DÉMARRAGE DE L'APPLICATION");
        System.out.println("⭐".repeat(50) + "\n");

        launch(args);

        System.out.println("\n" + "⭐".repeat(50));
        System.out.println("👋 SMART FARM - APPLICATION TERMINÉE");
        System.out.println("⭐".repeat(50) + "\n");
    }
}

