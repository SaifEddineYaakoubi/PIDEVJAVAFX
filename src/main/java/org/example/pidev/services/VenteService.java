package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Vente;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenteService implements IService<Vente> {

    private Connection connection;

    // Constantes de validation
    private static final double MONTANT_MIN = 0.01;
    private static final double MONTANT_MAX = 999999.99;

    public VenteService() {
        connection = DBConnection.getConnection();
    }

    // =========================
    // MÉTHODES DE VALIDATION
    // =========================

    /**
     * Valide les données d'une vente
     * @param vente La vente à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    public void valider(Vente vente) throws IllegalArgumentException {
        if (vente == null) {
            throw new IllegalArgumentException("La vente ne peut pas être null.");
        }
        validerDateVente(vente.getDateVente());
        validerMontantTotal(vente.getMontantTotal());
        validerIdClient(vente.getIdClient());
        validerIdUser(vente.getIdUser());
    }

    /**
     * Valide la date de vente
     */
    public void validerDateVente(LocalDate dateVente) throws IllegalArgumentException {
        if (dateVente == null) {
            throw new IllegalArgumentException("La date de vente ne peut pas être vide.");
        }
        // La date de vente ne peut pas être dans le futur (après aujourd'hui)
        if (dateVente.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de vente ne peut pas être dans le futur.");
        }
        // La date de vente ne peut pas être trop ancienne (plus de 10 ans)
        if (dateVente.isBefore(LocalDate.now().minusYears(10))) {
            throw new IllegalArgumentException("La date de vente ne peut pas être antérieure à 10 ans.");
        }
    }

    /**
     * Valide le montant total de la vente
     */
    public void validerMontantTotal(double montantTotal) throws IllegalArgumentException {
        if (montantTotal < MONTANT_MIN) {
            throw new IllegalArgumentException("Le montant total doit être au minimum " + MONTANT_MIN + " DT.");
        }
        if (montantTotal > MONTANT_MAX) {
            throw new IllegalArgumentException("Le montant total ne peut pas dépasser " + MONTANT_MAX + " DT.");
        }
        // Vérifier qu'il n'y a pas de valeurs NaN ou Infinity
        if (Double.isNaN(montantTotal) || Double.isInfinite(montantTotal)) {
            throw new IllegalArgumentException("Le montant total doit être un nombre valide.");
        }
    }

    /**
     * Valide l'ID du client
     */
    public void validerIdClient(int idClient) throws IllegalArgumentException {
        if (idClient <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être un nombre positif.");
        }
    }

    /**
     * Valide l'ID de l'utilisateur
     */
    public void validerIdUser(int idUser) throws IllegalArgumentException {
        if (idUser <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur doit être un nombre positif.");
        }
    }

    // =========================
    // MÉTHODES CRUD
    // =========================

    @Override
    public boolean add(Vente vente) {
        // Validation avant insertion
        try {
            valider(vente);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO vente (date_vente, montant_total, id_client, id_user) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setDate(1, Date.valueOf(vente.getDateVente()));
            pst.setDouble(2, vente.getMontantTotal());
            pst.setInt(3, vente.getIdClient());
            pst.setInt(4, vente.getIdUser());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                vente.setIdVente(rs.getInt(1));
            }
            System.out.println("✅ Vente ajoutée avec succès (ID: " + vente.getIdVente() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la vente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Vente vente) {
        // Validation avant mise à jour
        try {
            valider(vente);
            if (vente.getIdVente() <= 0) {
                throw new IllegalArgumentException("L'ID de la vente doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE vente SET date_vente = ?, montant_total = ?, id_client = ?, id_user = ? WHERE id_vente = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setDate(1, Date.valueOf(vente.getDateVente()));
            pst.setDouble(2, vente.getMontantTotal());
            pst.setInt(3, vente.getIdClient());
            pst.setInt(4, vente.getIdUser());
            pst.setInt(5, vente.getIdVente());
            pst.executeUpdate();
            System.out.println("✅ Vente mise à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la vente: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM vente WHERE id_vente = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Vente supprimée avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la vente: " + e.getMessage());
        }
    }

    @Override
    public Vente getById(int id) {
        String query = "SELECT * FROM vente WHERE id_vente = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Vente(
                        rs.getInt("id_vente"),
                        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
                        rs.getDouble("montant_total"),
                        rs.getInt("id_client"),
                        rs.getInt("id_user")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de la vente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Vente> getAll() {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Vente vente = new Vente(
                        rs.getInt("id_vente"),
                        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
                        rs.getDouble("montant_total"),
                        rs.getInt("id_client"),
                        rs.getInt("id_user")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des ventes: " + e.getMessage());
        }
        return ventes;
    }

    // =========================
    // MÉTHODES UTILITAIRES
    // =========================

    /**
     * Récupère toutes les ventes d'un client
     */
    public List<Vente> getVentesByClient(int idClient) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE id_client = ? ORDER BY date_vente DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vente vente = new Vente(
                        rs.getInt("id_vente"),
                        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
                        rs.getDouble("montant_total"),
                        rs.getInt("id_client"),
                        rs.getInt("id_user")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des ventes du client: " + e.getMessage());
        }
        return ventes;
    }

    /**
     * Récupère toutes les ventes d'un utilisateur
     */
    public List<Vente> getVentesByUser(int idUser) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE id_user = ? ORDER BY date_vente DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vente vente = new Vente(
                        rs.getInt("id_vente"),
                        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
                        rs.getDouble("montant_total"),
                        rs.getInt("id_client"),
                        rs.getInt("id_user")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des ventes de l'utilisateur: " + e.getMessage());
        }
        return ventes;
    }

    /**
     * Calcule le montant total des ventes
     */
    public double calculerMontantTotalVentes() {
        String query = "SELECT SUM(montant_total) as total FROM vente";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul du montant total: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Récupère les ventes entre deux dates
     */
    public List<Vente> getVentesByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE date_vente BETWEEN ? AND ? ORDER BY date_vente DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setDate(1, Date.valueOf(dateDebut));
            pst.setDate(2, Date.valueOf(dateFin));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vente vente = new Vente(
                        rs.getInt("id_vente"),
                        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
                        rs.getDouble("montant_total"),
                        rs.getInt("id_client"),
                        rs.getInt("id_user")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des ventes: " + e.getMessage());
        }
        return ventes;
    }

    /**
     * Calcule la taxe (19%) pour un montant donné.
     */
    public double calculerTaxe(double montant) {
        return montant * 0.19;
    }

    /**
     * Récupère le total des ventes par client (id_client -> somme des montants).
     */
    public Map<Integer, Double> getTotalVentesByClient() {
        Map<Integer, Double> stats = new HashMap<>();
        String query = "SELECT id_client, SUM(montant_total) as total FROM vente GROUP BY id_client";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                stats.put(rs.getInt("id_client"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul des statistiques par client: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Compte le nombre total de ventes
     */
    public int countVentes() {
        String query = "SELECT COUNT(*) as total FROM vente";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du comptage des ventes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Récupère le contexte de la base de données pour l'IA
     * Retourne un résumé des statistiques principales
     */
    public String getAIDatabaseContext() {
        try {
            List<Vente> allVentes = getAll();
            int totalVentes = allVentes.size();
            double totalMontant = allVentes.stream().mapToDouble(Vente::getMontantTotal).sum();
            double moyenneMontant = totalVentes > 0 ? totalMontant / totalVentes : 0;

            return String.format(
                "Contexte Base de Données SMART FARM:\n" +
                "- Nombre total de ventes: %d\n" +
                "- Montant total des ventes: %.2f DT\n" +
                "- Montant moyen par vente: %.2f DT\n" +
                "Tu es un assistant IA pour SMART FARM. Utilise ces données pour répondre aux questions sur les ventes et les statistiques.",
                totalVentes, totalMontant, moyenneMontant
            );
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération du contexte IA: " + e.getMessage());
            return "Contexte: Application SMART FARM - Gestion de ferme intelligente";
        }
    }
}
