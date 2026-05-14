package org.example.pidev.services.ventes;

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

    private static final double MONTANT_MIN = 0.01;
    private static final double MONTANT_MAX = 999999.99;

    public VenteService() {}

    private Connection getConn() {
        return DBConnection.getConnection();
    }

    // =========================
    // VALIDATION
    // =========================

    public void valider(Vente vente) throws IllegalArgumentException {
        if (vente == null) throw new IllegalArgumentException("La vente ne peut pas etre null.");
        validerDateVente(vente.getDateVente());
        validerMontantTotal(vente.getMontantTotal());
        validerIdClient(vente.getIdClient());
        validerIdUser(vente.getIdUser());
    }

    public void validerDateVente(LocalDate dateVente) throws IllegalArgumentException {
        if (dateVente == null) throw new IllegalArgumentException("La date de vente ne peut pas etre vide.");
        if (dateVente.isAfter(LocalDate.now())) throw new IllegalArgumentException("La date de vente ne peut pas etre dans le futur.");
        if (dateVente.isBefore(LocalDate.now().minusYears(10))) throw new IllegalArgumentException("La date de vente ne peut pas etre anterieure a 10 ans.");
    }

    public void validerMontantTotal(double montantTotal) throws IllegalArgumentException {
        if (montantTotal < MONTANT_MIN) throw new IllegalArgumentException("Le montant total doit etre au minimum " + MONTANT_MIN + " DT.");
        if (montantTotal > MONTANT_MAX) throw new IllegalArgumentException("Le montant total ne peut pas depasser " + MONTANT_MAX + " DT.");
        if (Double.isNaN(montantTotal) || Double.isInfinite(montantTotal)) throw new IllegalArgumentException("Le montant total doit etre un nombre valide.");
    }

    public void validerIdClient(int idClient) throws IllegalArgumentException {
        if (idClient <= 0) throw new IllegalArgumentException("L'ID du client doit etre un nombre positif.");
    }

    public void validerIdUser(int idUser) throws IllegalArgumentException {
        if (idUser <= 0) throw new IllegalArgumentException("L'ID de l'utilisateur doit etre un nombre positif.");
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public boolean add(Vente vente) {
        try {
            valider(vente);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO vente (date_vente, montant_total, id_client, id_user, quantite, id_produit, ville, region, frais_livraison) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setDate(1, Date.valueOf(vente.getDateVente()));
            pst.setDouble(2, vente.getMontantTotal());
            pst.setInt(3, vente.getIdClient());
            pst.setInt(4, vente.getIdUser());
            pst.setDouble(5, vente.getQuantite() > 0 ? vente.getQuantite() : 0.0);
            if (vente.getIdProduit() != null && vente.getIdProduit() > 0) {
                pst.setInt(6, vente.getIdProduit());
            } else {
                pst.setNull(6, Types.INTEGER);
            }
            pst.setString(7, vente.getVille());
            pst.setString(8, vente.getRegion());
            if (vente.getFraisLivraison() != null) {
                pst.setFloat(9, vente.getFraisLivraison());
            } else {
                pst.setNull(9, Types.FLOAT);
            }
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) vente.setIdVente(rs.getInt(1));
            System.out.println("Vente ajoutee avec succes (ID: " + vente.getIdVente() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la vente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Vente vente) {
        try {
            valider(vente);
            if (vente.getIdVente() <= 0) throw new IllegalArgumentException("L'ID de la vente doit etre un nombre positif.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE vente SET date_vente = ?, montant_total = ?, id_client = ?, id_user = ?, quantite = ?, id_produit = ?, ville = ?, region = ?, frais_livraison = ? WHERE id_vente = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setDate(1, Date.valueOf(vente.getDateVente()));
            pst.setDouble(2, vente.getMontantTotal());
            pst.setInt(3, vente.getIdClient());
            pst.setInt(4, vente.getIdUser());
            pst.setDouble(5, vente.getQuantite() > 0 ? vente.getQuantite() : 0.0);
            if (vente.getIdProduit() != null && vente.getIdProduit() > 0) {
                pst.setInt(6, vente.getIdProduit());
            } else {
                pst.setNull(6, Types.INTEGER);
            }
            pst.setString(7, vente.getVille());
            pst.setString(8, vente.getRegion());
            if (vente.getFraisLivraison() != null) {
                pst.setFloat(9, vente.getFraisLivraison());
            } else {
                pst.setNull(9, Types.FLOAT);
            }
            pst.setInt(10, vente.getIdVente());
            pst.executeUpdate();
            System.out.println("Vente mise a jour avec succes");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise a jour de la vente: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM vente WHERE id_vente = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Vente supprimee avec succes");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la vente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Vente getById(int id) {
        String query = "SELECT * FROM vente WHERE id_vente = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return extractVenteFromResultSet(rs);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation de la vente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Vente> getAll() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        System.out.println("[VenteService.getAll] ownerId=" + ownerId);
        if (ownerId > 0) {
            return getByUserId(ownerId);
        }
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) ventes.add(extractVenteFromResultSet(rs));
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des ventes: " + e.getMessage());
        }
        return ventes;
    }

    public List<Vente> getByUserId(int idUser) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE id_user = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ventes.add(extractVenteFromResultSet(rs));
            System.out.println("[VenteService] getByUserId(" + idUser + ") -> " + ventes.size() + " vente(s)");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des ventes par user: " + e.getMessage());
        }
        return ventes;
    }

    private Vente extractVenteFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_vente");
        java.sql.Date sqlDate = rs.getDate("date_vente");
        LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
        double montant = rs.getDouble("montant_total");
        int idClient = rs.getInt("id_client");
        int idUser = rs.getInt("id_user");

        double quantite = 0.0;
        try { quantite = rs.getDouble("quantite"); if (rs.wasNull()) quantite = 0.0; } catch (SQLException ignored) {}

        Integer idProduit = null;
        try { int tmp = rs.getInt("id_produit"); idProduit = rs.wasNull() ? null : tmp; } catch (SQLException ignored) {}

        String ville = null;
        try { ville = rs.getString("ville"); } catch (SQLException ignored) {}

        String region = null;
        try { region = rs.getString("region"); } catch (SQLException ignored) {}

        Float fraisLivraison = null;
        try { float tmpf = rs.getFloat("frais_livraison"); fraisLivraison = rs.wasNull() ? null : tmpf; } catch (SQLException ignored) {}

        return new Vente(id, date, montant, idClient, idUser, quantite, idProduit, ville, region, fraisLivraison);
    }

    // =========================
    // METHODES UTILITAIRES
    // =========================

    public List<Vente> getVentesByClient(int idClient) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE id_client = ? ORDER BY date_vente DESC";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ventes.add(extractVenteFromResultSet(rs));
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des ventes du client: " + e.getMessage());
        }
        return ventes;
    }

    public List<Vente> getVentesByUser(int idUser) {
        return getByUserId(idUser);
    }

    public double calculerMontantTotalVentes() {
        String query = "SELECT SUM(montant_total) as total FROM vente";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul du montant total: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Vente> getVentesByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE date_vente BETWEEN ? AND ? ORDER BY date_vente DESC";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setDate(1, Date.valueOf(dateDebut));
            pst.setDate(2, Date.valueOf(dateFin));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ventes.add(extractVenteFromResultSet(rs));
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recuperation des ventes: " + e.getMessage());
        }
        return ventes;
    }

    public double calculerTaxe(double montant) {
        return montant * 0.19;
    }

    public Map<Integer, Double> getTotalVentesByClient() {
        Map<Integer, Double> stats = new HashMap<>();
        String query = "SELECT id_client, SUM(montant_total) as total FROM vente GROUP BY id_client";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) stats.put(rs.getInt("id_client"), rs.getDouble("total"));
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul des statistiques par client: " + e.getMessage());
        }
        return stats;
    }

    public int countVentes() {
        String query = "SELECT COUNT(*) as total FROM vente";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage des ventes: " + e.getMessage());
        }
        return 0;
    }

    public String getAIDatabaseContext() {
        try {
            List<Vente> allVentes = getAll();
            int totalVentes = allVentes.size();
            double totalMontant = allVentes.stream().mapToDouble(Vente::getMontantTotal).sum();
            double moyenneMontant = totalVentes > 0 ? totalMontant / totalVentes : 0;
            return String.format(
                "Contexte SMART FARM:\n- Ventes: %d\n- Total: %.2f DT\n- Moyenne: %.2f DT",
                totalVentes, totalMontant, moyenneMontant
            );
        } catch (Exception e) {
            return "Contexte: Application SMART FARM - Gestion de ferme intelligente";
        }
    }
}
