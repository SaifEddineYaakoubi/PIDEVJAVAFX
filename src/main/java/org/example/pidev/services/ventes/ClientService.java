package org.example.pidev.services.ventes;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Client;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClientService implements IService<Client> {

    // Validation patterns
    private static final int NOM_MIN_LENGTH = 2;
    private static final int NOM_MAX_LENGTH = 100;
    private static final int ADRESSE_MAX_LENGTH = 255;

    // Autorise numÃ©ros (+, chiffres, espaces, tirets) ou emails simples
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+0-9][0-9 -]{5,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public ClientService() {
        // Connection is fetched fresh via DBConnection.getConnection() on each call
    }

    /** Helper to always get a valid connection */
    private Connection getConn() {
        return DBConnection.getConnection();
    }

    // =========================
    // MÃ‰THODES DE VALIDATION
    // =========================

    public void valider(Client client) throws IllegalArgumentException {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas Ãªtre null.");
        }
        validerNom(client.getNom());
        validerContact(client.getContact());
        validerAdresse(client.getAdresse());
    }

    public void validerNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas Ãªtre vide.");
        }
        String trimmed = nom.trim();
        if (trimmed.length() < NOM_MIN_LENGTH) {
            throw new IllegalArgumentException("Le nom doit contenir au moins " + NOM_MIN_LENGTH + " caractÃ¨res.");
        }
        if (trimmed.length() > NOM_MAX_LENGTH) {
            throw new IllegalArgumentException("Le nom ne peut pas dÃ©passer " + NOM_MAX_LENGTH + " caractÃ¨res.");
        }
        if (trimmed.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("Le nom contient des caractÃ¨res non autorisÃ©s.");
        }
    }

    public void validerContact(String contact) throws IllegalArgumentException {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contact ne peut pas Ãªtre vide.");
        }
        String trimmed = contact.trim();
        // accepter soit un email valide soit un numÃ©ro de tÃ©lÃ©phone raisonnable
        if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            return; // ok
        }
        if (PHONE_PATTERN.matcher(trimmed).matches()) {
            return; // ok
        }
        throw new IllegalArgumentException("Le contact doit Ãªtre un email valide ou un numÃ©ro de tÃ©lÃ©phone (6-20 chiffres, peut contenir +, espaces ou -).");
    }

    public void validerAdresse(String adresse) throws IllegalArgumentException {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas Ãªtre vide.");
        }
        String trimmed = adresse.trim();
        if (trimmed.length() > ADRESSE_MAX_LENGTH) {
            throw new IllegalArgumentException("L'adresse ne peut pas dÃ©passer " + ADRESSE_MAX_LENGTH + " caractÃ¨res.");
        }
        if (trimmed.matches(".*[<>].*")) {
            throw new IllegalArgumentException("L'adresse contient des caractÃ¨res non autorisÃ©s.");
        }
    }

    // =========================
    // MÃ‰THODES CRUD
    // =========================

    @Override
    public boolean add(Client client) {
        // validation
        try {
            valider(client);
        } catch (IllegalArgumentException e) {
            System.out.println("âŒ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO client (nom, contact, adresse, id_user) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, client.getNom().trim());
            pst.setString(2, client.getContact().trim());
            pst.setString(3, client.getAdresse().trim());
            pst.setInt(4, client.getIdUser() > 0 ? client.getIdUser() : org.example.pidev.utils.Session.getOwnerUserId());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                client.setIdClient(rs.getInt(1));
            }
            System.out.println("âœ… Client ajoutÃ© avec succÃ¨s (ID: " + client.getIdClient() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors de l'ajout du client: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Client client) {
        // validation
        try {
            valider(client);
            if (client.getIdClient() <= 0) {
                throw new IllegalArgumentException("L'ID du client doit Ãªtre un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("âŒ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE client SET nom = ?, contact = ?, adresse = ? WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setString(1, client.getNom().trim());
            pst.setString(2, client.getContact().trim());
            pst.setString(3, client.getAdresse().trim());
            pst.setInt(4, client.getIdClient());
            pst.executeUpdate();
            System.out.println("âœ… Client mis Ã  jour avec succÃ¨s");
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors de la mise Ã  jour du client: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM client WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("âœ… Client supprimÃ© avec succÃ¨s");
            return true;
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors de la suppression du client: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Client getById(int id) {
        String query = "SELECT * FROM client WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("contact"),
                        rs.getString("adresse")
                );
            }
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors de la rÃ©cupÃ©ration du client: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Client> getAll() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        if (ownerId > 0) {
            return getByUserId(ownerId);
        }
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("contact"),
                        rs.getString("adresse")
                );
                try { client.setIdUser(rs.getInt("id_user")); } catch (SQLException ignored) {}
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des clients: " + e.getMessage());
        }
        return clients;
    }

    /**
     * Récupère les clients d'un utilisateur spécifique
     */
    public List<Client> getByUserId(int idUser) {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client WHERE id_user = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("contact"),
                        rs.getString("adresse")
                );
                client.setIdUser(rs.getInt("id_user"));
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des clients par user: " + e.getMessage());
        }
        return clients;
    }


    // =========================
    // MÃ‰THODES DE FIDÃ‰LITÃ‰
    // =========================

    /**
     * Calcule le montant total des achats pour un client spÃ©cifique
     * @param idClient L'ID du client
     * @return Le montant total des ventes pour ce client
     */
    public Double calculateTotalAchats(int idClient) {
        String query = "SELECT SUM(montant_total) AS total FROM vente WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Double total = rs.getDouble("total");
                return rs.wasNull() ? 0.0 : total;
            }
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors du calcul des achats totaux: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Met Ã  jour tous les clients avec leurs informations de fidÃ©litÃ©
     * @param clients La liste des clients Ã  enrichir
     */
    public void enrichClients(List<Client> clients) {
        for (Client client : clients) {
            if (client != null) {
                Double totalAchats = calculateTotalAchats(client.getIdClient());
                client.setTotalAchats(totalAchats);
                client.updateStatutFidelite();
            }
        }
    }

    /**
     * RÃ©cupÃ¨re tous les clients enrichis avec leurs informations de fidÃ©litÃ©
     * @return Liste de tous les clients avec totalAchats et statutFidelite
     */
    public List<Client> getAllWithFidelite() {
        List<Client> clients = getAll();
        enrichClients(clients);
        return clients;
    }

    /**
     * Calcule la remise VIP (10% de rÃ©duction)
     * @param montant Le montant sur lequel appliquer la remise
     * @return Le montant de la remise
     */
    public Double calculateRemiseVIP(Double montant) {
        return montant * 0.10;
    }

    /**
     * Compte les clients par statut de fidÃ©litÃ©
     * @return Une map avec les comptages par statut
     */
    public java.util.Map<String, Integer> getClientCountByStatut() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("VIP", 0);
        stats.put("FidÃ¨le", 0);
        stats.put("Standard", 0);

        List<Client> clients = getAllWithFidelite();
        for (Client client : clients) {
            String statut = client.getStatutFidelite();
            stats.put(statut, stats.getOrDefault(statut, 0) + 1);
        }
        return stats;
    }

    // =========================
    // MÃ‰THODES DE RELANCE
    // =========================

    /**
     * RÃ©cupÃ¨re la date de la derniÃ¨re vente pour un client
     * @param idClient L'ID du client
     * @return La date de la derniÃ¨re vente
     */
    public java.time.LocalDate getLastSaleDate(int idClient) {
        String query = "SELECT MAX(date_vente) AS last_date FROM vente WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("last_date");
                return sqlDate != null ? sqlDate.toLocalDate() : null;
            }
        } catch (SQLException e) {
            System.out.println("âŒ Erreur lors de la rÃ©cupÃ©ration de la derniÃ¨re date de vente: " + e.getMessage());
        }
        return null;
    }

    /**
     * DÃ©tecte les clients inactifs (sans vente depuis > 30 jours)
     * @return Liste des clients Ã  relancer
     */
    public List<org.example.pidev.models.ClientRelance> getInactiveClients() {
        List<org.example.pidev.models.ClientRelance> inactiveClients = new ArrayList<>();
        List<Client> allClients = getAllWithFidelite();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Client client : allClients) {
            java.time.LocalDate lastSaleDate = getLastSaleDate(client.getIdClient());

            // Si pas de vente du tout, considÃ©rer comme inactif
            if (lastSaleDate == null) {
                int daysInactive = 999; // Jamais eu de vente
                org.example.pidev.models.ClientRelance relance = new org.example.pidev.models.ClientRelance(client, null, daysInactive);
                inactiveClients.add(relance);
            } else {
                // Calculer les jours d'inactivitÃ©
                long daysInactive = java.time.temporal.ChronoUnit.DAYS.between(lastSaleDate, today);

                // Si inactif depuis plus de 30 jours
                if (daysInactive > 30) {
                    org.example.pidev.models.ClientRelance relance = new org.example.pidev.models.ClientRelance(client, lastSaleDate, (int)daysInactive);
                    inactiveClients.add(relance);
                }
            }
        }

        return inactiveClients;
    }

    /**
     * Compte le nombre de clients Ã  relancer
     * @return Nombre de clients inactifs
     */
    public int getInactiveClientsCount() {
        return getInactiveClients().size();
    }

    /**
     * Marque un client comme promu (coupon gÃ©nÃ©rÃ©)
     * @param idClient L'ID du client
     * @param promoted Le statut de promotion
     */
    public void updateClientPromoStatus(int idClient, boolean promoted) {
        // Cette mÃ©thode peut Ãªtre Ã©tendue pour persister l'information en BD
        // Pour l'instant, elle est surtout utile pour le suivi en mÃ©moire
        System.out.println(promoted ? "âœ… Client " + idClient + " promu avec coupon" : "Client " + idClient + " non promu");
    }

    /**
     * Calcule le taux de conversion (clients relancÃ©s devenus fidÃ¨les/VIP)
     * @return Map avec les statistiques de conversion
     */
    public java.util.Map<String, Object> getConversionStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        List<org.example.pidev.models.ClientRelance> inactiveClients = getInactiveClients();
        int totalInactive = inactiveClients.size();

        // Compter combien sont devenus fidÃ¨les/VIP
        long convertedToFidele = inactiveClients.stream()
            .filter(c -> "FidÃ¨le".equals(c.getStatutFidelite()))
            .count();

        long convertedToVIP = inactiveClients.stream()
            .filter(c -> "VIP".equals(c.getStatutFidelite()))
            .count();

        long totalConverted = convertedToFidele + convertedToVIP;
        double conversionRate = totalInactive > 0 ? (double) totalConverted / totalInactive * 100 : 0.0;

        stats.put("totalInactive", totalInactive);
        stats.put("convertedToFidele", convertedToFidele);
        stats.put("convertedToVIP", convertedToVIP);
        stats.put("totalConverted", totalConverted);
        stats.put("conversionRate", conversionRate);

        return stats;
    }
}

