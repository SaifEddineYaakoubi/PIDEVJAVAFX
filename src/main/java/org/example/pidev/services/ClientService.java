package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Client;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClientService implements IService<Client> {

    private Connection connection;

    // Validation patterns
    private static final int NOM_MIN_LENGTH = 2;
    private static final int NOM_MAX_LENGTH = 100;
    private static final int ADRESSE_MAX_LENGTH = 255;

    // Autorise numéros (+, chiffres, espaces, tirets) ou emails simples
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+0-9][0-9 -]{5,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public ClientService() {
        connection = DBConnection.getConnection();
    }

    // =========================
    // MÉTHODES DE VALIDATION
    // =========================

    public void valider(Client client) throws IllegalArgumentException {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null.");
        }
        validerNom(client.getNom());
        validerContact(client.getContact());
        validerAdresse(client.getAdresse());
    }

    public void validerNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        String trimmed = nom.trim();
        if (trimmed.length() < NOM_MIN_LENGTH) {
            throw new IllegalArgumentException("Le nom doit contenir au moins " + NOM_MIN_LENGTH + " caractères.");
        }
        if (trimmed.length() > NOM_MAX_LENGTH) {
            throw new IllegalArgumentException("Le nom ne peut pas dépasser " + NOM_MAX_LENGTH + " caractères.");
        }
        if (trimmed.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("Le nom contient des caractères non autorisés.");
        }
    }

    public void validerContact(String contact) throws IllegalArgumentException {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contact ne peut pas être vide.");
        }
        String trimmed = contact.trim();
        // accepter soit un email valide soit un numéro de téléphone raisonnable
        if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            return; // ok
        }
        if (PHONE_PATTERN.matcher(trimmed).matches()) {
            return; // ok
        }
        throw new IllegalArgumentException("Le contact doit être un email valide ou un numéro de téléphone (6-20 chiffres, peut contenir +, espaces ou -).");
    }

    public void validerAdresse(String adresse) throws IllegalArgumentException {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide.");
        }
        String trimmed = adresse.trim();
        if (trimmed.length() > ADRESSE_MAX_LENGTH) {
            throw new IllegalArgumentException("L'adresse ne peut pas dépasser " + ADRESSE_MAX_LENGTH + " caractères.");
        }
        if (trimmed.matches(".*[<>].*")) {
            throw new IllegalArgumentException("L'adresse contient des caractères non autorisés.");
        }
    }

    // =========================
    // MÉTHODES CRUD
    // =========================

    @Override
    public boolean add(Client client) {
        // validation
        try {
            valider(client);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO client (nom, contact, adresse) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, client.getNom().trim());
            pst.setString(2, client.getContact().trim());
            pst.setString(3, client.getAdresse().trim());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                client.setIdClient(rs.getInt(1));
            }
            System.out.println("✅ Client ajouté avec succès (ID: " + client.getIdClient() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du client: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Client client) {
        // validation
        try {
            valider(client);
            if (client.getIdClient() <= 0) {
                throw new IllegalArgumentException("L'ID du client doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE client SET nom = ?, contact = ?, adresse = ? WHERE id_client = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, client.getNom().trim());
            pst.setString(2, client.getContact().trim());
            pst.setString(3, client.getAdresse().trim());
            pst.setInt(4, client.getIdClient());
            pst.executeUpdate();
            System.out.println("✅ Client mis à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du client: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM client WHERE id_client = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Client supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression du client: " + e.getMessage());
        }
    }

    @Override
    public Client getById(int id) {
        String query = "SELECT * FROM client WHERE id_client = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
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
            System.out.println("❌ Erreur lors de la récupération du client: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("contact"),
                        rs.getString("adresse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des clients: " + e.getMessage());
        }
        return clients;
    }

    // =========================
    // MÉTHODES DE FIDÉLITÉ
    // =========================

    /**
     * Calcule le montant total des achats pour un client spécifique
     * @param idClient L'ID du client
     * @return Le montant total des ventes pour ce client
     */
    public Double calculateTotalAchats(int idClient) {
        String query = "SELECT SUM(montant_total) AS total FROM vente WHERE id_client = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Double total = rs.getDouble("total");
                return rs.wasNull() ? 0.0 : total;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul des achats totaux: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Met à jour tous les clients avec leurs informations de fidélité
     * @param clients La liste des clients à enrichir
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
     * Récupère tous les clients enrichis avec leurs informations de fidélité
     * @return Liste de tous les clients avec totalAchats et statutFidelite
     */
    public List<Client> getAllWithFidelite() {
        List<Client> clients = getAll();
        enrichClients(clients);
        return clients;
    }

    /**
     * Calcule la remise VIP (10% de réduction)
     * @param montant Le montant sur lequel appliquer la remise
     * @return Le montant de la remise
     */
    public Double calculateRemiseVIP(Double montant) {
        return montant * 0.10;
    }

    /**
     * Compte les clients par statut de fidélité
     * @return Une map avec les comptages par statut
     */
    public java.util.Map<String, Integer> getClientCountByStatut() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("VIP", 0);
        stats.put("Fidèle", 0);
        stats.put("Standard", 0);

        List<Client> clients = getAllWithFidelite();
        for (Client client : clients) {
            String statut = client.getStatutFidelite();
            stats.put(statut, stats.getOrDefault(statut, 0) + 1);
        }
        return stats;
    }

    // =========================
    // MÉTHODES DE RELANCE
    // =========================

    /**
     * Récupère la date de la dernière vente pour un client
     * @param idClient L'ID du client
     * @return La date de la dernière vente
     */
    public java.time.LocalDate getLastSaleDate(int idClient) {
        String query = "SELECT MAX(date_vente) AS last_date FROM vente WHERE id_client = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("last_date");
                return sqlDate != null ? sqlDate.toLocalDate() : null;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de la dernière date de vente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Détecte les clients inactifs (sans vente depuis > 30 jours)
     * @return Liste des clients à relancer
     */
    public List<org.example.pidev.models.ClientRelance> getInactiveClients() {
        List<org.example.pidev.models.ClientRelance> inactiveClients = new ArrayList<>();
        List<Client> allClients = getAllWithFidelite();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Client client : allClients) {
            java.time.LocalDate lastSaleDate = getLastSaleDate(client.getIdClient());

            // Si pas de vente du tout, considérer comme inactif
            if (lastSaleDate == null) {
                int daysInactive = 999; // Jamais eu de vente
                org.example.pidev.models.ClientRelance relance = new org.example.pidev.models.ClientRelance(client, null, daysInactive);
                inactiveClients.add(relance);
            } else {
                // Calculer les jours d'inactivité
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
     * Compte le nombre de clients à relancer
     * @return Nombre de clients inactifs
     */
    public int getInactiveClientsCount() {
        return getInactiveClients().size();
    }

    /**
     * Marque un client comme promu (coupon généré)
     * @param idClient L'ID du client
     * @param promoted Le statut de promotion
     */
    public void updateClientPromoStatus(int idClient, boolean promoted) {
        // Cette méthode peut être étendue pour persister l'information en BD
        // Pour l'instant, elle est surtout utile pour le suivi en mémoire
        System.out.println(promoted ? "✅ Client " + idClient + " promu avec coupon" : "Client " + idClient + " non promu");
    }

    /**
     * Calcule le taux de conversion (clients relancés devenus fidèles/VIP)
     * @return Map avec les statistiques de conversion
     */
    public java.util.Map<String, Object> getConversionStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        List<org.example.pidev.models.ClientRelance> inactiveClients = getInactiveClients();
        int totalInactive = inactiveClients.size();

        // Compter combien sont devenus fidèles/VIP
        long convertedToFidele = inactiveClients.stream()
            .filter(c -> "Fidèle".equals(c.getStatutFidelite()))
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

