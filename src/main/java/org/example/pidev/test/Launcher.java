package org.example.pidev.test;

import org.example.pidev.models.*;
import org.example.pidev.services.*;
import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Launcher {

    private static Scanner scanner = new Scanner(System.in);
    private static ParcelleService parcelleService;
    private static CultureService cultureService;
    private static UtilisateurService utilisateurService;
    private static ClientService clientService;
    private static VenteService venteService;

    public static void main(String[] args) {

        try {
            // D'abord initialiser la connexion
            Connection cnx = DBConnection.getInstance().getConnection();

            // Ensuite initialiser les services (après que la connexion soit établie)
            parcelleService = new ParcelleService();
            cultureService = new CultureService();
            utilisateurService = new UtilisateurService();
            clientService = new ClientService();
            venteService = new VenteService();

            if (cnx != null && !cnx.isClosed()) {
                System.out.println("✅ Connexion à la base de données réussie !");
            } else {
                System.out.println("❌ Connexion échouée !");
                return;
            }

            // Créer un utilisateur par défaut pour les tests
            Utilisateur utilisateur = new Utilisateur(
                    "Yaakoubi", "Saif", "saif.yaakoubi" + System.currentTimeMillis() + "@email.com",
                    "password123", "Agriculteur", true, LocalDate.now()
            );
            utilisateurService.add(utilisateur);
            int idUserDefault = utilisateur.getIdUser();

            boolean running = true;
            while (running) {
                afficherMenuPrincipal();
                int choix = lireEntier("Votre choix: ");

                switch (choix) {
                    case 1:
                        menuParcelle(idUserDefault);
                        break;
                    case 2:
                        menuCulture();
                        break;
                    case 3:
                        menuClient();
                        break;
                    case 4:
                        menuVente(idUserDefault);
                        break;
                    case 5:
                        afficherTout();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\n👋 Au revoir !");
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'exécution");
            e.printStackTrace();
        }
    }

    // ==========================================
    // MENUS
    // ==========================================

    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       🌾 SMART FARM - MENU PRINCIPAL    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Gestion des Parcelles               ║");
        System.out.println("║  2. Gestion des Cultures                ║");
        System.out.println("║  3. Gestion des Clients                 ║");
        System.out.println("║  4. Gestion des Ventes                  ║");
        System.out.println("║  5. Afficher toutes les données         ║");
        System.out.println("║  0. Quitter                             ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void menuParcelle(int idUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         📍 GESTION DES PARCELLES        ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter une parcelle                ║");
            System.out.println("║  2. Modifier une parcelle               ║");
            System.out.println("║  3. Supprimer une parcelle              ║");
            System.out.println("║  4. Afficher toutes les parcelles       ║");
            System.out.println("║  5. Rechercher une parcelle par ID      ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterParcelle(idUser);
                    break;
                case 2:
                    modifierParcelle();
                    break;
                case 3:
                    supprimerParcelle();
                    break;
                case 4:
                    afficherParcelles();
                    break;
                case 5:
                    rechercherParcelle();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void menuCulture() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         🌱 GESTION DES CULTURES         ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter une culture                 ║");
            System.out.println("║  2. Modifier une culture                ║");
            System.out.println("║  3. Supprimer une culture               ║");
            System.out.println("║  4. Afficher toutes les cultures        ║");
            System.out.println("║  5. Rechercher une culture par ID       ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterCulture();
                    break;
                case 2:
                    modifierCulture();
                    break;
                case 3:
                    supprimerCulture();
                    break;
                case 4:
                    afficherCultures();
                    break;
                case 5:
                    rechercherCulture();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // ==========================================
    // CRUD PARCELLE
    // ==========================================

    private static void ajouterParcelle(int idUser) {
        System.out.println("\n--- ➕ AJOUTER UNE PARCELLE ---");

        String nom = lireChaine("Nom de la parcelle: ");
        double superficie = lireDouble("Superficie (en m²): ");
        String localisation = lireChaine("Localisation: ");
        String etat = lireEtatParcelle();

        Parcelle parcelle = new Parcelle(nom, superficie, localisation, etat, idUser);
        if (parcelleService.add(parcelle)) {
            System.out.println("✅ Parcelle ajoutée avec succès ! ID: " + parcelle.getIdParcelle());
        }
    }

    private static void modifierParcelle() {
        System.out.println("\n--- ✏️ MODIFIER UNE PARCELLE ---");
        afficherParcelles();

        int id = lireEntier("ID de la parcelle à modifier: ");
        Parcelle parcelle = parcelleService.getById(id);

        if (parcelle == null) {
            System.out.println("❌ Parcelle non trouvée !");
            return;
        }

        System.out.println("Parcelle actuelle: " + parcelle);
        System.out.println("\n(Laissez vide pour garder la valeur actuelle)");

        String nom = lireChaineOptionnelle("Nouveau nom [" + parcelle.getNom() + "]: ");
        if (!nom.isEmpty()) parcelle.setNom(nom);

        String superficieStr = lireChaineOptionnelle("Nouvelle superficie [" + parcelle.getSuperficie() + "]: ");
        if (!superficieStr.isEmpty()) {
            try {
                double superficie = Double.parseDouble(superficieStr);
                if (superficie > 0) parcelle.setSuperficie(superficie);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Valeur invalide, superficie non modifiée.");
            }
        }

        String localisation = lireChaineOptionnelle("Nouvelle localisation [" + parcelle.getLocalisation() + "]: ");
        if (!localisation.isEmpty()) parcelle.setLocalisation(localisation);

        System.out.println("Nouvel état (1: active, 2: repos, 3: exploitée) [" + parcelle.getEtat() + "]: ");
        String etatStr = lireChaineOptionnelle("");
        if (!etatStr.isEmpty()) {
            String etat = convertirEtatParcelle(etatStr);
            if (etat != null) parcelle.setEtat(etat);
        }

        parcelleService.update(parcelle);
    }

    private static void supprimerParcelle() {
        System.out.println("\n--- 🗑️ SUPPRIMER UNE PARCELLE ---");
        afficherParcelles();

        int id = lireEntier("ID de la parcelle à supprimer: ");
        Parcelle parcelle = parcelleService.getById(id);

        if (parcelle == null) {
            System.out.println("❌ Parcelle non trouvée !");
            return;
        }

        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer: " + parcelle.getNom() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");

        if (confirm.equalsIgnoreCase("OUI")) {
            parcelleService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static void afficherParcelles() {
        System.out.println("\n--- 📋 LISTE DES PARCELLES ---");
        List<Parcelle> parcelles = parcelleService.getAll();
        if (parcelles.isEmpty()) {
            System.out.println("Aucune parcelle trouvée.");
        } else {
            System.out.println("┌────────┬────────────────────┬────────────┬────────────────┬────────────┐");
            System.out.println("│   ID   │        Nom         │ Superficie │  Localisation  │   État     │");
            System.out.println("├────────┼────────────────────┼────────────┼────────────────┼────────────┤");
            for (Parcelle p : parcelles) {
                System.out.printf("│ %6d │ %-18s │ %10.2f │ %-14s │ %-10s │%n",
                        p.getIdParcelle(), truncate(p.getNom(), 18), p.getSuperficie(),
                        truncate(p.getLocalisation(), 14), p.getEtat());
            }
            System.out.println("└────────┴────────────────────┴────────────┴────────────────┴────────────┘");
        }
    }

    private static void rechercherParcelle() {
        System.out.println("\n--- 🔍 RECHERCHER UNE PARCELLE ---");
        int id = lireEntier("ID de la parcelle: ");
        Parcelle parcelle = parcelleService.getById(id);

        if (parcelle != null) {
            System.out.println("\n✅ Parcelle trouvée:");
            System.out.println("   ID: " + parcelle.getIdParcelle());
            System.out.println("   Nom: " + parcelle.getNom());
            System.out.println("   Superficie: " + parcelle.getSuperficie() + " m²");
            System.out.println("   Localisation: " + parcelle.getLocalisation());
            System.out.println("   État: " + parcelle.getEtat());
        } else {
            System.out.println("❌ Parcelle non trouvée !");
        }
    }

    // ==========================================
    // CRUD CULTURE
    // ==========================================

    private static void ajouterCulture() {
        System.out.println("\n--- ➕ AJOUTER UNE CULTURE ---");

        // Vérifier qu'il y a des parcelles
        List<Parcelle> parcelles = parcelleService.getAll();
        if (parcelles.isEmpty()) {
            System.out.println("❌ Aucune parcelle disponible. Créez d'abord une parcelle !");
            return;
        }

        afficherParcelles();
        int idParcelle = lireEntier("ID de la parcelle pour cette culture: ");

        if (parcelleService.getById(idParcelle) == null) {
            System.out.println("❌ Parcelle non trouvée !");
            return;
        }

        String typeCulture = lireChaine("Type de culture (ex: Blé, Tomate, Olivier): ");
        LocalDate datePlantation = lireDate("Date de plantation (jj/mm/aaaa): ");
        LocalDate dateRecoltePrevue = lireDateApres("Date de récolte prévue (jj/mm/aaaa): ");
        String etatCroissance = lireEtatCroissance();

        Culture culture = new Culture(typeCulture, datePlantation, dateRecoltePrevue, etatCroissance, idParcelle);
        if (cultureService.add(culture)) {
            System.out.println("✅ Culture ajoutée avec succès ! ID: " + culture.getIdCulture());
        }
    }

    private static void modifierCulture() {
        System.out.println("\n--- ✏️ MODIFIER UNE CULTURE ---");
        afficherCultures();

        int id = lireEntier("ID de la culture à modifier: ");
        Culture culture = cultureService.getById(id);

        if (culture == null) {
            System.out.println("❌ Culture non trouvée !");
            return;
        }

        System.out.println("Culture actuelle: " + culture);
        System.out.println("\n(Laissez vide pour garder la valeur actuelle)");

        String typeCulture = lireChaineOptionnelle("Nouveau type [" + culture.getTypeCulture() + "]: ");
        if (!typeCulture.isEmpty()) culture.setTypeCulture(typeCulture);

        String datePlantStr = lireChaineOptionnelle("Nouvelle date plantation (jj/mm/aaaa) [" + formatDate(culture.getDatePlantation()) + "]: ");
        if (!datePlantStr.isEmpty()) {
            LocalDate date = parseDate(datePlantStr);
            if (date != null) culture.setDatePlantation(date);
        }

        String dateRecolteStr = lireChaineOptionnelle("Nouvelle date récolte (jj/mm/aaaa) [" + formatDate(culture.getDateRecoltePrevue()) + "]: ");
        if (!dateRecolteStr.isEmpty()) {
            LocalDate date = parseDate(dateRecolteStr);
            if (date != null && (culture.getDatePlantation() == null || date.isAfter(culture.getDatePlantation()))) {
                culture.setDateRecoltePrevue(date);
            }
        }

        System.out.println("Nouvel état (1: germination, 2: croissance, 3: floraison, 4: mature, 5: récolté): ");
        String etatStr = lireChaineOptionnelle("");
        if (!etatStr.isEmpty()) {
            String etat = convertirEtatCroissance(etatStr);
            if (etat != null) culture.setEtatCroissance(etat);
        }

        cultureService.update(culture);
    }

    private static void supprimerCulture() {
        System.out.println("\n--- 🗑️ SUPPRIMER UNE CULTURE ---");
        afficherCultures();

        int id = lireEntier("ID de la culture à supprimer: ");
        Culture culture = cultureService.getById(id);

        if (culture == null) {
            System.out.println("❌ Culture non trouvée !");
            return;
        }

        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer: " + culture.getTypeCulture() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");

        if (confirm.equalsIgnoreCase("OUI")) {
            cultureService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static void afficherCultures() {
        System.out.println("\n--- 📋 LISTE DES CULTURES ---");
        List<Culture> cultures = cultureService.getAll();
        if (cultures.isEmpty()) {
            System.out.println("Aucune culture trouvée.");
        } else {
            System.out.println("┌────────┬────────────────┬──────────────┬──────────────┬────────────┬──────────┐");
            System.out.println("│   ID   │     Type       │  Plantation  │   Récolte    │   État     │ Parcelle │");
            System.out.println("├────────┼────────────────┼──────────────┼──────────────┼────────────┼──────────┤");
            for (Culture c : cultures) {
                System.out.printf("│ %6d │ %-14s │ %-12s │ %-12s │ %-10s │ %8d │%n",
                        c.getIdCulture(), truncate(c.getTypeCulture(), 14),
                        formatDate(c.getDatePlantation()), formatDate(c.getDateRecoltePrevue()),
                        truncate(c.getEtatCroissance(), 10), c.getIdParcelle());
            }
            System.out.println("└────────┴────────────────┴──────────────┴──────────────┴────────────┴──────────┘");
        }
    }

    private static void rechercherCulture() {
        System.out.println("\n--- 🔍 RECHERCHER UNE CULTURE ---");
        int id = lireEntier("ID de la culture: ");
        Culture culture = cultureService.getById(id);

        if (culture != null) {
            System.out.println("\n✅ Culture trouvée:");
            System.out.println("   ID: " + culture.getIdCulture());
            System.out.println("   Type: " + culture.getTypeCulture());
            System.out.println("   Date plantation: " + formatDate(culture.getDatePlantation()));
            System.out.println("   Date récolte prévue: " + formatDate(culture.getDateRecoltePrevue()));
            System.out.println("   État: " + culture.getEtatCroissance());
            System.out.println("   ID Parcelle: " + culture.getIdParcelle());
        } else {
            System.out.println("❌ Culture non trouvée !");
        }
    }

    private static void afficherTout() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       📊 AFFICHAGE DE TOUTES DONNÉES    ║");
        System.out.println("╚════════════════════════════════════════╝");
        afficherParcelles();
        afficherCultures();
    }

    // ==========================================
    // MÉTHODES DE SAISIE (sans validation - la validation est dans les services)
    // ==========================================

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

    private static LocalDate lireDateApres(String message) {
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

    private static String lireEtatParcelle() {
        System.out.println("État de la parcelle:");
        System.out.println("  1. active");
        System.out.println("  2. repos");
        System.out.println("  3. exploitée");
        int choix = lireEntier("Votre choix: ");
        switch (choix) {
            case 1: return "active";
            case 2: return "repos";
            case 3: return "exploitée";
            default: return "active";
        }
    }

    private static String convertirEtatParcelle(String choix) {
        switch (choix) {
            case "1": return "active";
            case "2": return "repos";
            case "3": return "exploitée";
            default: return null;
        }
    }

    private static String lireEtatCroissance() {
        System.out.println("État de croissance:");
        System.out.println("  1. germination");
        System.out.println("  2. croissance");
        System.out.println("  3. floraison");
        System.out.println("  4. mature");
        System.out.println("  5. récolté");
        int choix = lireEntier("Votre choix: ");
        switch (choix) {
            case 1: return "germination";
            case 2: return "croissance";
            case 3: return "floraison";
            case 4: return "mature";
            case 5: return "récolté";
            default: return "croissance";
        }
    }

    private static String convertirEtatCroissance(String choix) {
        switch (choix) {
            case "1": return "germination";
            case "2": return "croissance";
            case "3": return "floraison";
            case "4": return "mature";
            case "5": return "récolté";
            default: return null;
        }
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 2) + ".." : str;
    }

    // ==========================================
    // CRUD CLIENT
    // ==========================================

    private static void menuClient() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       👥 GESTION DES CLIENTS            ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter un client                   ║");
            System.out.println("║  2. Modifier un client                  ║");
            System.out.println("║  3. Supprimer un client                 ║");
            System.out.println("║  4. Afficher tous les clients           ║");
            System.out.println("║  5. Rechercher un client par ID         ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterClient();
                    break;
                case 2:
                    modifierClient();
                    break;
                case 3:
                    supprimerClient();
                    break;
                case 4:
                    afficherClients();
                    break;
                case 5:
                    rechercherClient();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void ajouterClient() {
        System.out.println("\n--- ➕ AJOUTER UN CLIENT ---");

        String nom = lireChaine("Nom du client: ");
        String contact = lireChaine("Contact (email ou téléphone): ");
        String adresse = lireChaine("Adresse: ");

        Client client = new Client(nom, contact, adresse);
        if (clientService.add(client)) {
            System.out.println("✅ Client ajouté avec succès ! ID: " + client.getIdClient());
        }
    }

    private static void modifierClient() {
        System.out.println("\n--- ✏️ MODIFIER UN CLIENT ---");
        afficherClients();

        int id = lireEntier("ID du client à modifier: ");
        Client client = clientService.getById(id);

        if (client == null) {
            System.out.println("❌ Client non trouvé !");
            return;
        }

        System.out.println("Client actuel: " + client);
        System.out.println("\n(Laissez vide pour garder la valeur actuelle)");

        String nom = lireChaineOptionnelle("Nouveau nom [" + client.getNom() + "]: ");
        if (!nom.isEmpty()) client.setNom(nom);

        String contact = lireChaineOptionnelle("Nouveau contact [" + client.getContact() + "]: ");
        if (!contact.isEmpty()) client.setContact(contact);

        String adresse = lireChaineOptionnelle("Nouvelle adresse [" + client.getAdresse() + "]: ");
        if (!adresse.isEmpty()) client.setAdresse(adresse);

        clientService.update(client);
    }

    private static void supprimerClient() {
        System.out.println("\n--- 🗑️ SUPPRIMER UN CLIENT ---");
        afficherClients();

        int id = lireEntier("ID du client à supprimer: ");
        Client client = clientService.getById(id);

        if (client == null) {
            System.out.println("❌ Client non trouvé !");
            return;
        }

        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer: " + client.getNom() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");

        if (confirm.equalsIgnoreCase("OUI")) {
            clientService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static void afficherClients() {
        System.out.println("\n--- 📋 LISTE DES CLIENTS ---");
        List<Client> clients = clientService.getAll();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            System.out.println("┌────────┬────────────────────┬──────────────────────┬────────────────────┐");
            System.out.println("│   ID   │        Nom         │      Contact         │     Adresse        │");
            System.out.println("├────────┼────────────────────┼──────────────────────┼────────────────────┤");
            for (Client c : clients) {
                System.out.printf("│ %6d │ %-18s │ %-20s │ %-18s │%n",
                        c.getIdClient(), truncate(c.getNom(), 18),
                        truncate(c.getContact(), 20), truncate(c.getAdresse(), 18));
            }
            System.out.println("└────────┴────────────────────┴──────────────────────┴────────────────────┘");
        }
    }

    private static void rechercherClient() {
        System.out.println("\n--- 🔍 RECHERCHER UN CLIENT ---");
        int id = lireEntier("ID du client: ");
        Client client = clientService.getById(id);

        if (client != null) {
            System.out.println("\n✅ Client trouvé:");
            System.out.println("   ID: " + client.getIdClient());
            System.out.println("   Nom: " + client.getNom());
            System.out.println("   Contact: " + client.getContact());
            System.out.println("   Adresse: " + client.getAdresse());
        } else {
            System.out.println("❌ Client non trouvé !");
        }
    }

    // ==========================================
    // CRUD VENTE
    // ==========================================

    private static void menuVente(int idUserDefault) {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       💰 GESTION DES VENTES             ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter une vente                   ║");
            System.out.println("║  2. Modifier une vente                  ║");
            System.out.println("║  3. Supprimer une vente                 ║");
            System.out.println("║  4. Afficher toutes les ventes          ║");
            System.out.println("║  5. Rechercher une vente par ID         ║");
            System.out.println("║  6. Ventes d'un client                  ║");
            System.out.println("║  7. Montant total des ventes            ║");
            System.out.println("║  0. Retour                              ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choix = lireEntier("Votre choix: ");

            switch (choix) {
                case 1:
                    ajouterVente(idUserDefault);
                    break;
                case 2:
                    modifierVente();
                    break;
                case 3:
                    supprimerVente();
                    break;
                case 4:
                    afficherVentes();
                    break;
                case 5:
                    rechercherVente();
                    break;
                case 6:
                    ventesParClient();
                    break;
                case 7:
                    afficherMontantTotal();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void ajouterVente(int idUserDefault) {
        System.out.println("\n--- ➕ AJOUTER UNE VENTE ---");

        // Vérifier qu'il y a des clients
        List<Client> clients = clientService.getAll();
        if (clients.isEmpty()) {
            System.out.println("❌ Aucun client disponible. Créez d'abord un client !");
            return;
        }

        afficherClients();
        int idClient = lireEntier("ID du client: ");

        if (clientService.getById(idClient) == null) {
            System.out.println("❌ Client non trouvé !");
            return;
        }

        LocalDate dateVente = lireDate("Date de vente (jj/mm/aaaa): ");
        double montantTotal = lireDouble("Montant total (DT): ");

        Vente vente = new Vente(dateVente, montantTotal, idClient, idUserDefault);
        if (venteService.add(vente)) {
            System.out.println("✅ Vente ajoutée avec succès ! ID: " + vente.getIdVente());
        }
    }

    private static void modifierVente() {
        System.out.println("\n--- ✏️ MODIFIER UNE VENTE ---");
        afficherVentes();

        int id = lireEntier("ID de la vente à modifier: ");
        Vente vente = venteService.getById(id);

        if (vente == null) {
            System.out.println("❌ Vente non trouvée !");
            return;
        }

        System.out.println("Vente actuelle: " + vente);
        System.out.println("\n(Laissez vide pour garder la valeur actuelle)");

        afficherClients();
        String idClientStr = lireChaineOptionnelle("Nouvel ID client [" + vente.getIdClient() + "]: ");
        if (!idClientStr.isEmpty()) {
            try {
                int idClient = Integer.parseInt(idClientStr);
                if (clientService.getById(idClient) != null) {
                    vente.setIdClient(idClient);
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ ID client invalide.");
            }
        }

        String dateVenteStr = lireChaineOptionnelle("Nouvelle date (jj/mm/aaaa) [" + formatDate(vente.getDateVente()) + "]: ");
        if (!dateVenteStr.isEmpty()) {
            LocalDate date = parseDate(dateVenteStr);
            if (date != null) vente.setDateVente(date);
        }

        String montantStr = lireChaineOptionnelle("Nouveau montant [" + vente.getMontantTotal() + "]: ");
        if (!montantStr.isEmpty()) {
            try {
                double montant = Double.parseDouble(montantStr);
                if (montant > 0) vente.setMontantTotal(montant);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Montant invalide.");
            }
        }

        venteService.update(vente);
    }

    private static void supprimerVente() {
        System.out.println("\n--- 🗑️ SUPPRIMER UNE VENTE ---");
        afficherVentes();

        int id = lireEntier("ID de la vente à supprimer: ");
        Vente vente = venteService.getById(id);

        if (vente == null) {
            System.out.println("❌ Vente non trouvée !");
            return;
        }

        System.out.println("⚠️ Êtes-vous sûr de vouloir supprimer la vente ID " + vente.getIdVente() + " ?");
        String confirm = lireChaine("Tapez 'OUI' pour confirmer: ");

        if (confirm.equalsIgnoreCase("OUI")) {
            venteService.delete(id);
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private static void afficherVentes() {
        System.out.println("\n--- 📋 LISTE DES VENTES ---");
        List<Vente> ventes = venteService.getAll();
        if (ventes.isEmpty()) {
            System.out.println("Aucune vente trouvée.");
        } else {
            System.out.println("┌────────┬──────────────┬────────────┬──────────┬──────────┐");
            System.out.println("│   ID   │     Date     │  Montant   │ Client   │   User   │");
            System.out.println("├────────┼──────────────┼────────────┼──────────┼──────────┤");
            for (Vente v : ventes) {
                System.out.printf("│ %6d │ %-12s │ %10.2f │ %8d │ %8d │%n",
                        v.getIdVente(), formatDate(v.getDateVente()),
                        v.getMontantTotal(), v.getIdClient(), v.getIdUser());
            }
            System.out.println("└────────┴──────────────┴────────────┴──────────┴──────────┘");
        }
    }

    private static void rechercherVente() {
        System.out.println("\n--- 🔍 RECHERCHER UNE VENTE ---");
        int id = lireEntier("ID de la vente: ");
        Vente vente = venteService.getById(id);

        if (vente != null) {
            System.out.println("\n✅ Vente trouvée:");
            System.out.println("   ID: " + vente.getIdVente());
            System.out.println("   Date: " + formatDate(vente.getDateVente()));
            System.out.println("   Montant: " + vente.getMontantTotal() + " DT");
            System.out.println("   ID Client: " + vente.getIdClient());
            System.out.println("   ID Utilisateur: " + vente.getIdUser());
        } else {
            System.out.println("❌ Vente non trouvée !");
        }
    }

    private static void ventesParClient() {
        System.out.println("\n--- 📊 VENTES PAR CLIENT ---");
        afficherClients();
        int idClient = lireEntier("ID du client: ");

        if (clientService.getById(idClient) == null) {
            System.out.println("❌ Client non trouvé !");
            return;
        }

        List<Vente> ventes = venteService.getVentesByClient(idClient);
        if (ventes.isEmpty()) {
            System.out.println("Aucune vente pour ce client.");
        } else {
            System.out.println("Ventes du client " + idClient + ":");
            System.out.println("┌────────┬──────────────┬────────────┐");
            System.out.println("│   ID   │     Date     │  Montant   │");
            System.out.println("├────────┼──────────────┼────────────┤");
            for (Vente v : ventes) {
                System.out.printf("│ %6d │ %-12s │ %10.2f │%n",
                        v.getIdVente(), formatDate(v.getDateVente()), v.getMontantTotal());
            }
            System.out.println("└────────┴──────────────┴────────────┘");
        }
    }

    private static void afficherMontantTotal() {
        System.out.println("\n--- 💵 MONTANT TOTAL DES VENTES ---");
        double montantTotal = venteService.calculerMontantTotalVentes();
        System.out.println("Montant total: " + montantTotal + " DT");
    }
}

