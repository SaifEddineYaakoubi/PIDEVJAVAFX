package org.example.pidev.services.ventes;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Client;
import org.example.pidev.models.ClientRelance;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ClientService implements IService<Client> {

    private static final int NOM_MIN_LENGTH = 2;
    private static final int NOM_MAX_LENGTH = 100;
    private static final int ADRESSE_MAX_LENGTH = 255;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+0-9][0-9 -]{5,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public ClientService() {}

    private Connection getConn() {
        return DBConnection.getConnection();
    }

    // =========================
    // VALIDATION
    // =========================

    public void valider(Client client) throws IllegalArgumentException {
        if (client == null) throw new IllegalArgumentException("Le client ne peut pas etre null.");
        validerNom(client.getNom());
        validerContact(client.getContact());
        validerAdresse(client.getAdresse());
    }

    public void validerNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
        String t = nom.trim();
        if (t.length() < NOM_MIN_LENGTH) throw new IllegalArgumentException("Le nom doit contenir au moins " + NOM_MIN_LENGTH + " caracteres.");
        if (t.length() > NOM_MAX_LENGTH) throw new IllegalArgumentException("Le nom ne peut pas depasser " + NOM_MAX_LENGTH + " caracteres.");
        if (t.matches(".*[<>\"'%;()&+].*")) throw new IllegalArgumentException("Le nom contient des caracteres non autorises.");
    }

    public void validerContact(String contact) throws IllegalArgumentException {
        if (contact == null || contact.trim().isEmpty()) throw new IllegalArgumentException("Le contact ne peut pas etre vide.");
        String t = contact.trim();
        if (EMAIL_PATTERN.matcher(t).matches()) return;
        if (PHONE_PATTERN.matcher(t).matches()) return;
        throw new IllegalArgumentException("Le contact doit etre un email valide ou un numero de telephone.");
    }

    public void validerAdresse(String adresse) throws IllegalArgumentException {
        if (adresse == null || adresse.trim().isEmpty()) throw new IllegalArgumentException("L'adresse ne peut pas etre vide.");
        String t = adresse.trim();
        if (t.length() > ADRESSE_MAX_LENGTH) throw new IllegalArgumentException("L'adresse ne peut pas depasser " + ADRESSE_MAX_LENGTH + " caracteres.");
        if (t.matches(".*[<>].*")) throw new IllegalArgumentException("L'adresse contient des caracteres non autorises.");
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public boolean add(Client client) {
        try {
            valider(client);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation: " + e.getMessage());
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
            if (rs.next()) client.setIdClient(rs.getInt(1));
            System.out.println("Client ajoute avec succes (ID: " + client.getIdClient() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du client: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Client client) {
        try {
            valider(client);
            if (client.getIdClient() <= 0) throw new IllegalArgumentException("L'ID du client doit etre un nombre positif.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation: " + e.getMessage());
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
            System.out.println("Client mis a jour avec succes");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise a jour du client: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM client WHERE id_client = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Client supprime avec succes");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du client: " + e.getMessage());
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
                Client c = new Client(rs.getInt("id_client"), rs.getString("nom"), rs.getString("contact"), rs.getString("adresse"));
                try { c.setIdUser(rs.getInt("id_user")); } catch (SQLException ignored) {}
                return c;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation du client: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Client> getAll() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        System.out.println("[ClientService.getAll] ownerId=" + ownerId);
        if (ownerId > 0) {
            return getByUserId(ownerId);
        }
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Client c = new Client(rs.getInt("id_client"), rs.getString("nom"), rs.getString("contact"), rs.getString("adresse"));
                try { c.setIdUser(rs.getInt("id_user")); } catch (SQLException ignored) {}
                clients.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des clients: " + e.getMessage());
        }
        return clients;
    }

    public List<Client> getByUserId(int idUser) {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client WHERE id_user = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Client c = new Client(rs.getInt("id_client"), rs.getString("nom"), rs.getString("contact"), rs.getString("adresse"));
                c.setIdUser(rs.getInt("id_user"));
                clients.add(c);
            }
            System.out.println("[ClientService] getByUserId(" + idUser + ") -> " + clients.size() + " client(s)");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des clients par user: " + e.getMessage());
        }
        return clients;
    }

    // =========================
    // FIDELITE
    // =========================

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
            System.out.println("Erreur lors du calcul des achats totaux: " + e.getMessage());
        }
        return 0.0;
    }

    public void enrichClients(List<Client> clients) {
        for (Client client : clients) {
            if (client != null) {
                client.setTotalAchats(calculateTotalAchats(client.getIdClient()));
                client.updateStatutFidelite();
            }
        }
    }

    public List<Client> getAllWithFidelite() {
        List<Client> clients = getAll();
        enrichClients(clients);
        return clients;
    }

    public Double calculateRemiseVIP(Double montant) {
        return montant * 0.10;
    }

    public Map<String, Integer> getClientCountByStatut() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("VIP", 0);
        stats.put("Fidele", 0);
        stats.put("Standard", 0);
        for (Client client : getAllWithFidelite()) {
            String statut = client.getStatutFidelite();
            stats.put(statut, stats.getOrDefault(statut, 0) + 1);
        }
        return stats;
    }

    // =========================
    // RELANCE
    // =========================

    public LocalDate getLastSaleDate(int idClient) {
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
            System.out.println("Erreur lors de la recuperation de la derniere date de vente: " + e.getMessage());
        }
        return null;
    }

    public List<ClientRelance> getInactiveClients() {
        List<ClientRelance> inactiveClients = new ArrayList<>();
        List<Client> allClients = getAllWithFidelite();
        LocalDate today = LocalDate.now();

        for (Client client : allClients) {
            LocalDate lastSaleDate = getLastSaleDate(client.getIdClient());
            if (lastSaleDate == null) {
                inactiveClients.add(new ClientRelance(client, null, 999));
            } else {
                long daysInactive = ChronoUnit.DAYS.between(lastSaleDate, today);
                if (daysInactive > 30) {
                    inactiveClients.add(new ClientRelance(client, lastSaleDate, (int) daysInactive));
                }
            }
        }
        return inactiveClients;
    }

    public int getInactiveClientsCount() {
        return getInactiveClients().size();
    }

    public void updateClientPromoStatus(int idClient, boolean promoted) {
        System.out.println(promoted ? "Client " + idClient + " promu avec coupon" : "Client " + idClient + " non promu");
    }

    public Map<String, Object> getConversionStats() {
        Map<String, Object> stats = new HashMap<>();
        List<ClientRelance> inactiveClients = getInactiveClients();
        int totalInactive = inactiveClients.size();
        long convertedToFidele = inactiveClients.stream().filter(c -> "Fidele".equals(c.getStatutFidelite())).count();
        long convertedToVIP = inactiveClients.stream().filter(c -> "VIP".equals(c.getStatutFidelite())).count();
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
