package org.example.pidev.test;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.RecolteService;
import org.example.pidev.services.RendementService;
import org.example.pidev.services.UtilisateurService;
import org.example.pidev.utils.DBConnection;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

// Ajout des imports JavaFX pour charger le FXML depuis le Launcher
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher {

    private static final Scanner scanner = new Scanner(System.in);
    private static RecolteService recolteService;

    // Flag pour s'assurer qu'on lance JavaFX une seule fois
    private static final AtomicBoolean javafxLaunched = new AtomicBoolean(false);

    public static void main(String[] args) {

        try {
            Connection cnx = DBConnection.getConnection();

            recolteService = new RecolteService();
            RendementService rendementService = new RendementService();
            UtilisateurService utilisateurService = new UtilisateurService();

            System.out.println("[DEBUG] Services initialisés: recolte=" + (recolteService != null)
                    + ", rendement=" + (rendementService != null)
                    + ", utilisateurService=" + (utilisateurService != null));

            if (cnx != null && !cnx.isClosed()) {
                System.out.println("✅ Connexion à la base de données réussie !");
            } else {
                System.out.println("❌ Connexion échouée !");
                return;
            }

            Utilisateur utilisateur = new Utilisateur(
                    "Yaakoubi", "Saif", "saif.yaakoubi" + System.currentTimeMillis() + "@email.com",
                    "password123", "Agriculteur", true, LocalDate.now()
            );
            utilisateurService.add(utilisateur);

            boolean running = true;
            while (running) {
                afficherMenuPrincipal();
                int choix = lireEntier("Votre choix: ");

                switch (choix) {
                    case 1:
                        System.out.println("-> Ouverture du menu Récoltes...");
                        menuRecolte();
                        break;
                    case 2:
                        System.out.println("-> Ouverture du menu Rendements...");
                        menuRendement(rendementService);
                        break;
                    case 3:
                        System.out.println("-> Ouverture de l'interface AddRecolte.fxml...");
                        launchAddRecolteUI();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\n👋 Au revoir !");
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'exécution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       🌾 SMART FARM - MENU PRINCIPAL    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Gestion des Récoltes                ║");
        System.out.println("║  2. Gestion des Rendements              ║");
        System.out.println("║  3. Ouvrir interface AddRecolte (GUI)   ║");
        System.out.println("║  0. Quitter                             ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void menuRecolte() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         🧺 GESTION DES RÉCOLTES         ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter une récolte                 ║");
            System.out.println("║  2. Modifier une récolte                ║");
            System.out.println("║  3. Supprimer une récolte               ║");
            System.out.println("║  4. Afficher toutes les récoltes        ║");
            System.out.println("║  5. Rechercher une récolte par ID       ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterRecolte();
                    break;
                case 2:
                    modifierRecolte();
                    break;
                case 3:
                    supprimerRecolte();
                    break;
                case 4:
                    afficherRecoltes();
                    break;
                case 5:
                    rechercherRecolte();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // Méthode pour lancer/afficher l'UI AddRecolte.fxml
    private static void launchAddRecolteUI() {
        // Si JavaFX n'a pas encore été lancé, on lance l'application JavaFX
        if (javafxLaunched.compareAndSet(false, true)) {
            new Thread(() -> {
                try {
                    Application.launch(AddRecolteApp.class);
                } catch (Throwable t) {
                    // Si Application.launch échoue, reset flag pour permettre une nouvelle tentative
                    t.printStackTrace();
                }
            }, "JavaFX-Launcher").start();
        } else {
            // Si JavaFX est déjà lancé, on crée une nouvelle fenêtre via Platform.runLater
            Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(AddRecolteApp.class.getResource("/AddRecolte.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Ajouter Récolte");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Petite Application JavaFX embarquée qui charge le FXML
    public static class AddRecolteApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(AddRecolteApp.class.getResource("/AddRecolte.fxml"));
            primaryStage.setTitle("Ajouter Récolte");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    private static void ajouterRecolte() {
        System.out.println("\n--- ➕ AJOUTER UNE RÉCOLTE ---");

        double quantite = lireDouble("Quantité (en kg ou unité): ");
        LocalDate dateRecolte = lireDate("Date de récolte (jj/mm/aaaa): ");
        String qualite = lireChaine("Qualité (ex: A, B, premium): ");
        String typeCulture = lireChaine("Type de culture: ");
        String localisation = lireChaine("Localisation: ");

        Recolte recolte = new Recolte(quantite, dateRecolte, qualite, typeCulture, localisation);
        if (recolteService.add(recolte)) {
            System.out.println("✅ Récolte ajoutée avec succès ! ID: " + recolte.getIdRecolte());
        }
    }

    private static void modifierRecolte() {
        System.out.println("\n--- ✏️ MODIFIER UNE RÉCOLTE ---");
        afficherRecoltes();

        int id = lireEntier("ID de la récolte à modifier: ");
        Recolte recolte = recolteService.getById(id);

        if (recolte == null) {
            System.out.println("❌ Récolte non trouvée !");
            return;
        }

        System.out.println("Récolte actuelle: " + recolte);
        System.out.println("\n(Laissez vide pour garder la valeur actuelle)");

        String quantiteStr = lireChaineOptionnelle("Nouvelle quantité [" + recolte.getQuantite() + "]: ");
        if (!quantiteStr.isEmpty()) {
            try {
                double q = Double.parseDouble(quantiteStr);
                recolte.setQuantite(q);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Valeur invalide, quantité non modifiée.");
            }
        }

        String dateStr = lireChaineOptionnelle("Nouvelle date de récolte (jj/mm/aaaa) [" + formatDate(recolte.getDateRecolte()) + "]: ");
        if (!dateStr.isEmpty()) {
            LocalDate d = parseDate(dateStr);
            if (d != null) recolte.setDateRecolte(d);
        }

        String qualite = lireChaineOptionnelle("Nouvelle qualité [" + recolte.getQualite() + "]: ");
        if (!qualite.isEmpty()) recolte.setQualite(qualite);

        String typeCulture = lireChaineOptionnelle("Nouveau type de culture [" + recolte.getTypeCulture() + "]: ");
        if (!typeCulture.isEmpty()) recolte.setTypeCulture(typeCulture);

        String localisation = lireChaineOptionnelle("Nouvelle localisation [" + recolte.getLocalisation() + "]: ");
        if (!localisation.isEmpty()) recolte.setLocalisation(localisation);

        recolteService.update(recolte);
    }

    private static void supprimerRecolte() {
        System.out.println("\n--- 🗑️ SUPPRIMER UNE RÉCOLTE ---");
        afficherRecoltes();

        int id = lireEntier("ID de la récolte à supprimer: ");
        Recolte recolte = recolteService.getById(id);

        if (recolte == null) {
            System.out.println("❌ Récolte non trouvée !");
            return;
        }

        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer la récolte ID: " + recolte.getIdRecolte() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");

        if (confirm.equalsIgnoreCase("OUI")) {
            recolteService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static void afficherRecoltes() {
        System.out.println("\n--- 📋 LISTE DES RÉCOLTES ---");
        List<Recolte> recoltes = recolteService.getAll();
        if (recoltes.isEmpty()) {
            System.out.println("Aucune récolte trouvée.");
        } else {
            System.out.println("┌───────┬──────────┬──────────────┬──────────────┬──────────────┬──────────────┐");
            System.out.println("│  ID   │ Quantité │ Date Récolte │  Qualité     │ Type Culture │ Localisation │");
            System.out.println("├───────┼──────────┼──────────────┼──────────────┼──────────────┼──────────────┤");
            for (Recolte r : recoltes) {
                System.out.printf("│ %5d │ %8.2f │ %-12s │ %-12s │ %-12s │ %-12s │%n",
                        r.getIdRecolte(), r.getQuantite(), formatDate(r.getDateRecolte()), truncate(r.getQualite(), 12),
                        truncate(r.getTypeCulture(), 12), truncate(r.getLocalisation(), 12));
            }
            System.out.println("└───────┴──────────┴──────────────┴──────────────┴──────────────┴──────────────┘");
        }
    }

    private static void rechercherRecolte() {
        System.out.println("\n--- 🔍 RECHERCHER UNE RÉCOLTE ---");
        int id = lireEntier("ID de la récolte: ");
        Recolte recolte = recolteService.getById(id);

        if (recolte != null) {
            System.out.println("\n✅ Récolte trouvée:");
            System.out.println("   ID: " + recolte.getIdRecolte());
            System.out.println("   Quantité: " + recolte.getQuantite());
            System.out.println("   Date récolte: " + formatDate(recolte.getDateRecolte()));
            System.out.println("   Qualité: " + recolte.getQualite());
            System.out.println("   Type culture: " + recolte.getTypeCulture());
            System.out.println("   Localisation: " + recolte.getLocalisation());
        } else {
            System.out.println("❌ Récolte non trouvée !");
        }
    }

    private static void menuRendement(RendementService rendementService) {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       📊 GESTION DES RENDEMENTS         ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter un rendement (auto)         ║");
            System.out.println("║  2. Afficher tous les rendements        ║");
            System.out.println("║  3. Rechercher un rendement par ID      ║");
            System.out.println("║  4. Recalculer la productivité          ║");
            System.out.println("║  5. Statistiques pour une récolte       ║");
            System.out.println("║  6. Supprimer un rendement              ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterRendementAuto(rendementService);
                    break;
                case 2:
                    afficherRendements(rendementService);
                    break;
                case 3:
                    rechercherRendement(rendementService);
                    break;
                case 4:
                    recalculerProductivite(rendementService);
                    break;
                case 5:
                    statistiquesRecolte(rendementService);
                    break;
                case 6:
                    supprimerRendement(rendementService);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void ajouterRendementAuto(RendementService rendementService) {
        System.out.println("\n--- ➕ AJOUTER UN RENDEMENT (CALCUL AUTO) ---");
        afficherRecoltes();
        int idRecolte = lireEntier("ID de la récolte: ");
        if (recolteService.getById(idRecolte) == null) {
            System.out.println("❌ Récolte non trouvée !");
            return;
        }
        double quantiteTotale = lireDouble("Quantité totale (en kg): ");
        double surfaceExploitee = lireDouble("Surface exploitée (en hectares): ");
        if (rendementService.ajouterRendementAutomatique(quantiteTotale, surfaceExploitee, idRecolte)) {
            System.out.println("✅ Rendement ajouté avec calcul automatique de la productivité!");
        }
    }

    private static void afficherRendements(RendementService rendementService) {
        System.out.println("\n--- 📋 LISTE DES RENDEMENTS ---");
        List<Rendement> rendements = rendementService.getAll();
        if (rendements.isEmpty()) {
            System.out.println("Aucun rendement trouvé.");
        } else {
            System.out.println("┌────────┬─────────────┬────────────┬──────────────┬──────────┐");
            System.out.println("│   ID   │ Surface(ha) │ Quantité(kg)│ Productivité │ Récolte  │");
            System.out.println("├────────┼─────────────┼────────────┼──────────────┼──────────┤");
            for (Rendement r : rendements) {
                System.out.printf("│ %6d │ %11.2f │ %10.2f │ %12.2f │ %8d │%n",
                        r.getIdRendement(), r.getSurfaceExploitee(), r.getQuantiteTotale(),
                        r.getProductivite(), r.getIdRecolte());
            }
            System.out.println("└────────┴─────────────┴────────────┴──────────────┴──────────┘");
        }
    }

    private static void rechercherRendement(RendementService rendementService) {
        System.out.println("\n--- 🔍 RECHERCHER UN RENDEMENT ---");
        int id = lireEntier("ID du rendement: ");
        Rendement rendement = rendementService.getById(id);
        if (rendement != null) {
            System.out.println("\n✅ Rendement trouvé:");
            System.out.println("   ID: " + rendement.getIdRendement());
            System.out.println("   Surface exploitée: " + rendement.getSurfaceExploitee() + " ha");
            System.out.println("   Quantité totale: " + rendement.getQuantiteTotale() + " kg");
            System.out.println("   Productivité: " + String.format("%.2f", rendement.getProductivite()) + " kg/ha");
            System.out.println("   ID Récolte: " + rendement.getIdRecolte());
        } else {
            System.out.println("❌ Rendement non trouvé !");
        }
    }

    private static void recalculerProductivite(RendementService rendementService) {
        System.out.println("\n--- 🔄 RECALCULER LA PRODUCTIVITÉ ---");
        afficherRendements(rendementService);
        int id = lireEntier("ID du rendement à recalculer: ");
        double nouvelleQuantite = lireDouble("Nouvelle quantité totale (en kg): ");
        double nouvelleSurface = lireDouble("Nouvelle surface exploitée (en ha): ");
        if (rendementService.recalculerProductivite(id, nouvelleQuantite, nouvelleSurface)) {
            System.out.println("✅ Productivité recalculée avec succès!");
        }
    }

    private static void statistiquesRecolte(RendementService rendementService) {
        System.out.println("\n--- 📈 STATISTIQUES POUR UNE RÉCOLTE ---");
        afficherRecoltes();
        int idRecolte = lireEntier("ID de la récolte: ");
        if (recolteService.getById(idRecolte) == null) {
            System.out.println("❌ Récolte non trouvée !");
            return;
        }
        double moyenne = rendementService.getProductiviteMoyenne(idRecolte);
        double total = rendementService.getQuantiteTotalePourRecolte(idRecolte);
        double surface = rendementService.getSurfaceTotalePourRecolte(idRecolte);
        Rendement max = rendementService.getRendementMaximum(idRecolte);
        Rendement min = rendementService.getRendementMinimum(idRecolte);
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    STATISTIQUES POUR RÉCOLTE " + idRecolte + "       ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  Productivité moyenne: " + String.format("%8.2f", moyenne) + " kg/ha   ║");
        System.out.println("║  Quantité totale: " + String.format("%14.2f", total) + " kg      ║");
        System.out.println("║  Surface totale: " + String.format("%14.2f", surface) + " ha      ║");
        if (max != null) {
            System.out.println("║  Meilleur rendement: " + String.format("%9.2f", max.getProductivite()) + " kg/ha   ║");
        }
        if (min != null) {
            System.out.println("║  Pire rendement: " + String.format("%12.2f", min.getProductivite()) + " kg/ha   ║");
        }
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void supprimerRendement(RendementService rendementService) {
        System.out.println("\n--- 🗑️ SUPPRIMER UN RENDEMENT ---");
        afficherRendements(rendementService);
        int id = lireEntier("ID du rendement à supprimer: ");
        Rendement rendement = rendementService.getById(id);
        if (rendement == null) {
            System.out.println("❌ Rendement non trouvé !");
            return;
        }
        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer le rendement ID: " + rendement.getIdRendement() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");
        if (confirm.equalsIgnoreCase("OUI")) {
            rendementService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static String lireChaine(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static String lireChaineOptionnelle(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide.");
            }
        }
    }

    private static double lireDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide.");
            }
        }
    }

    private static LocalDate lireDate(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print(message);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), formatter);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de date invalide. Utilisez le format jj/mm/aaaa.");
            }
        }
    }

    private static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("⚠️ Format de date invalide.");
            return null;
        }
    }

    private static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 2) + ".." : str;
    }
}

