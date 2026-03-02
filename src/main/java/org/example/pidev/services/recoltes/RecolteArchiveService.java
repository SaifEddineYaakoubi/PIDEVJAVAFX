package org.example.pidev.services.recoltes;

import org.example.pidev.models.Recolte;
import org.example.pidev.models.RecolteArchive;
import org.example.pidev.utils.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer l'archive des récoltes supprimées
 */
public class RecolteArchiveService {

    private final Connection connection;

    public RecolteArchiveService() {
        connection = DBConnection.getConnection();
    }

    /**
     * Ajouter une récolte à l'archive
     */
    public boolean archiver(Recolte recolte, String causeSupression) {
        String query = "INSERT INTO recolte_archive (id_recolte_original, quantite, date_recolte, qualite, type_culture, localisation, cause_supression, date_archivage, id_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, recolte.getIdRecolte());
            pst.setDouble(2, recolte.getQuantite());
            pst.setDate(3, Date.valueOf(recolte.getDateRecolte()));
            pst.setString(4, recolte.getQualite());
            pst.setString(5, recolte.getTypeCulture());
            pst.setString(6, recolte.getLocalisation());
            pst.setString(7, causeSupression.trim());
            pst.setDate(8, Date.valueOf(LocalDate.now()));
            pst.setInt(9, recolte.getIdUser() > 0 ? recolte.getIdUser() : org.example.pidev.utils.Session.getOwnerUserId());
            pst.executeUpdate();

            System.out.println("✅ Récolte archivée avec succès (Cause: " + causeSupression + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'archivage de la récolte: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer toutes les récoltes archivées
     */
    public List<RecolteArchive> getAllArchives() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        if (ownerId > 0) {
            return getArchivesByUserId(ownerId);
        }
        List<RecolteArchive> archives = new ArrayList<>();
        String query = "SELECT * FROM recolte_archive ORDER BY date_archivage DESC";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                RecolteArchive archive = new RecolteArchive(
                        rs.getInt("id_archive"),
                        rs.getInt("id_recolte_original"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation"),
                        rs.getString("cause_supression"),
                        rs.getDate("date_archivage") != null ? rs.getDate("date_archivage").toLocalDate() : null
                );
                archives.add(archive);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des archives: " + e.getMessage());
        }
        return archives;
    }

    /**
     * Récupérer les archives d'un utilisateur spécifique
     */
    public List<RecolteArchive> getArchivesByUserId(int idUser) {
        List<RecolteArchive> archives = new ArrayList<>();
        String query = "SELECT * FROM recolte_archive WHERE id_user = ? ORDER BY date_archivage DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                RecolteArchive archive = new RecolteArchive(
                        rs.getInt("id_archive"),
                        rs.getInt("id_recolte_original"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation"),
                        rs.getString("cause_supression"),
                        rs.getDate("date_archivage") != null ? rs.getDate("date_archivage").toLocalDate() : null
                );
                archive.setIdUser(rs.getInt("id_user"));
                archives.add(archive);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des archives par user: " + e.getMessage());
        }
        return archives;
    }

    /**
     * Récupérer une archive par son ID
     */
    public RecolteArchive getArchiveById(int idArchive) {
        String query = "SELECT * FROM recolte_archive WHERE id_archive = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idArchive);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new RecolteArchive(
                        rs.getInt("id_archive"),
                        rs.getInt("id_recolte_original"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation"),
                        rs.getString("cause_supression"),
                        rs.getDate("date_archivage") != null ? rs.getDate("date_archivage").toLocalDate() : null
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de l'archive: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer les archives par type de culture
     */
    public List<RecolteArchive> getArchivesByTypeCulture(String typeCulture) {
        List<RecolteArchive> archives = new ArrayList<>();
        String query = "SELECT * FROM recolte_archive WHERE type_culture = ? ORDER BY date_archivage DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, typeCulture);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                RecolteArchive archive = new RecolteArchive(
                        rs.getInt("id_archive"),
                        rs.getInt("id_recolte_original"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation"),
                        rs.getString("cause_supression"),
                        rs.getDate("date_archivage") != null ? rs.getDate("date_archivage").toLocalDate() : null
                );
                archives.add(archive);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des archives: " + e.getMessage());
        }
        return archives;
    }

    /**
     * Récupérer les archives par cause de suppression
     */
    public List<RecolteArchive> getArchivesByCause(String cause) {
        List<RecolteArchive> archives = new ArrayList<>();
        String query = "SELECT * FROM recolte_archive WHERE cause_supression LIKE ? ORDER BY date_archivage DESC";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, "%" + cause + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                RecolteArchive archive = new RecolteArchive(
                        rs.getInt("id_archive"),
                        rs.getInt("id_recolte_original"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation"),
                        rs.getString("cause_supression"),
                        rs.getDate("date_archivage") != null ? rs.getDate("date_archivage").toLocalDate() : null
                );
                archives.add(archive);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des archives: " + e.getMessage());
        }
        return archives;
    }

    /**
     * Supprimer définitivement une archive
     */
    public boolean deleteArchive(int idArchive) {
        String query = "DELETE FROM recolte_archive WHERE id_archive = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idArchive);
            pst.executeUpdate();
            System.out.println("✅ Archive supprimée définitivement");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de l'archive: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vider complètement l'archive
     */
    public boolean viderArchive() {
        String query = "DELETE FROM recolte_archive";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(query);
            System.out.println("✅ Archive vidée complètement");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du vidage de l'archive: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtenir le nombre de récoltes archivées
     */
    public int countArchives() {
        String query = "SELECT COUNT(*) as total FROM recolte_archive";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du comptage des archives: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtenir les statistiques des suppressions
     */
    public void afficherStatistiquesArchives() {
        System.out.println("\n📊 STATISTIQUES DE L'ARCHIVE");
        System.out.println("================================");
        System.out.println("Total archivé: " + countArchives());

        // Compter par cause
        String queryCause = "SELECT cause_supression, COUNT(*) as count FROM recolte_archive GROUP BY cause_supression";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(queryCause);
            while (rs.next()) {
                System.out.println("  - " + rs.getString("cause_supression") + ": " + rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }

        // Quantité totale archivée
        String queryQuantite = "SELECT SUM(quantite) as total FROM recolte_archive";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(queryQuantite);
            if (rs.next()) {
                double totalQuantite = rs.getDouble("total");
                if (totalQuantite > 0) {
                    System.out.println("Quantité totale archivée: " + totalQuantite + " kg");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }
}

