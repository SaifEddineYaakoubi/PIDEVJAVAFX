package org.example.pidev.services.recoltes;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Recolte;
import org.example.pidev.utils.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class RecolteService implements IService<Recolte> {

    private Connection connection;

    // Constantes de validation
    private static final int QUALITE_MIN_LENGTH = 2;
    private static final int QUALITE_MAX_LENGTH = 100;

    public RecolteService() {
        connection = DBConnection.getConnection();
    }

    private Connection getConn() {
        connection = DBConnection.getConnection();
        return connection;
    }

    // =====================
    // Validation
    // =====================
    public void valider(Recolte recolte) throws IllegalArgumentException {
        validerQuantite(recolte.getQuantite());
        validerDateRecolte(recolte.getDateRecolte());
        validerQualite(recolte.getQualite());
        validerTypeCulture(recolte.getTypeCulture());
        validerLocalisation(recolte.getLocalisation());
    }

    public void validerQuantite(double quantite) throws IllegalArgumentException {
        if (quantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative.");
        }
        // Quantité raisonnable (par exemple max 1 million)
        if (quantite > 1_000_000) {
            throw new IllegalArgumentException("La quantité est trop grande.");
        }
    }

    public void validerDateRecolte(LocalDate dateRecolte) throws IllegalArgumentException {
        if (dateRecolte == null) {
            throw new IllegalArgumentException("La date de récolte ne peut pas être vide.");
        }
        // On autorise la date d'aujourd'hui ou dans le passé, mais pas trop ancienne (5 ans)
        if (dateRecolte.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de récolte ne peut pas être dans le futur.");
        }
        if (dateRecolte.isBefore(LocalDate.now().minusYears(5))) {
            throw new IllegalArgumentException("La date de récolte ne peut pas être antérieure à 5 ans.");
        }
    }

    public void validerQualite(String qualite) throws IllegalArgumentException {
        if (qualite == null || qualite.trim().isEmpty()) {
            throw new IllegalArgumentException("La qualité ne peut pas être vide.");
        }
        String q = qualite.trim();
        if (q.length() < QUALITE_MIN_LENGTH) {
            throw new IllegalArgumentException("La qualité doit contenir au moins " + QUALITE_MIN_LENGTH + " caractères.");
        }
        if (q.length() > QUALITE_MAX_LENGTH) {
            throw new IllegalArgumentException("La qualité ne peut pas dépasser " + QUALITE_MAX_LENGTH + " caractères.");
        }
        if (q.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("La qualité contient des caractères non autorisés.");
        }
    }

    public void validerTypeCulture(String typeCulture) throws IllegalArgumentException {
        if (typeCulture == null || typeCulture.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de culture ne peut pas être vide.");
        }
        if (typeCulture.trim().length() > 100) {
            throw new IllegalArgumentException("Le type de culture est trop long.");
        }
    }

    public void validerLocalisation(String localisation) throws IllegalArgumentException {
        if (localisation == null || localisation.trim().isEmpty()) {
            throw new IllegalArgumentException("La localisation ne peut pas être vide.");
        }
        if (localisation.trim().length() > 100) {
            throw new IllegalArgumentException("La localisation est trop longue.");
        }
    }

    // ...existing code...

    // =====================
    // CRUD
    // =====================

    @Override
    public boolean add(Recolte recolte) {
        try {
            valider(recolte);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO recolte (quantite, date_recolte, qualite, type_culture, localisation, id_user) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setDouble(1, recolte.getQuantite());
            pst.setDate(2, Date.valueOf(recolte.getDateRecolte()));
            pst.setString(3, recolte.getQualite().trim());
            pst.setString(4, recolte.getTypeCulture().trim());
            pst.setString(5, recolte.getLocalisation().trim());
            pst.setInt(6, recolte.getIdUser() > 0 ? recolte.getIdUser() : org.example.pidev.utils.Session.getOwnerUserId());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                recolte.setIdRecolte(rs.getInt(1));
            }

            System.out.println("✅ Récolte ajoutée avec succès (ID: " + recolte.getIdRecolte() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la récolte: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Recolte recolte) {
        try {
            valider(recolte);
            if (recolte.getIdRecolte() <= 0) {
                throw new IllegalArgumentException("L'ID de la récolte doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE recolte SET quantite = ?, date_recolte = ?, qualite = ?, type_culture = ?, localisation = ? WHERE id_recolte = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setDouble(1, recolte.getQuantite());
            pst.setDate(2, Date.valueOf(recolte.getDateRecolte()));
            pst.setString(3, recolte.getQualite().trim());
            pst.setString(4, recolte.getTypeCulture().trim());
            pst.setString(5, recolte.getLocalisation().trim());
            pst.setInt(6, recolte.getIdRecolte());
            pst.executeUpdate();
            System.out.println("✅ Récolte mise à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la récolte: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        deleteWithReason(id, "Suppression manuelle");
        return true;
    }

    public void deleteWithReason(int id, String causeSupression) {
        if (causeSupression == null || causeSupression.trim().isEmpty()) {
            System.out.println("❌ Erreur: La cause de suppression ne peut pas être vide");
            return;
        }
        if (causeSupression.trim().length() < 3) {
            System.out.println("❌ Erreur: La cause doit contenir au moins 3 caractères");
            return;
        }
        if (causeSupression.trim().length() > 255) {
            System.out.println("❌ Erreur: La cause est trop longue (max 255 caractères)");
            return;
        }

        try {
            Recolte recolte = getById(id);
            if (recolte == null) {
                System.out.println("❌ Erreur: Récolte introuvable (ID: " + id + ")");
                return;
            }

            RecolteArchiveService archiveService = new RecolteArchiveService();
            boolean archiveSuccess = archiveService.archiver(recolte, causeSupression);

            if (!archiveSuccess) {
                System.out.println("❌ Erreur: Impossible d'archiver la récolte. Suppression annulée");
                return;
            }

            String query = "DELETE FROM recolte WHERE id_recolte = ?";
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();

            System.out.println("✅ Récolte supprimée et archivée avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la récolte: " + e.getMessage());
        }
    }

    @Override
    public Recolte getById(int id) {
        String query = "SELECT * FROM recolte WHERE id_recolte = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Recolte r = new Recolte(
                        rs.getInt("id_recolte"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation")
                );
                try { r.setIdUser(rs.getInt("id_user")); } catch (SQLException ignored) {}
                return r;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de la récolte: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Recolte> getAll() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        System.out.println("[RecolteService.getAll] ownerId=" + ownerId);
        if (ownerId > 0) {
            return getByUserId(ownerId);
        }
        List<Recolte> recoltes = new ArrayList<>();
        String query = "SELECT * FROM recolte";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Recolte r = new Recolte(
                        rs.getInt("id_recolte"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation")
                );
                try { r.setIdUser(rs.getInt("id_user")); } catch (SQLException ignored) {}
                recoltes.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des récoltes: " + e.getMessage());
        }
        return recoltes;
    }

    public List<Recolte> getByUserId(int idUser) {
        List<Recolte> recoltes = new ArrayList<>();
        String query = "SELECT * FROM recolte WHERE id_user = ?";
        try {
            PreparedStatement pst = getConn().prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Recolte r = new Recolte(
                        rs.getInt("id_recolte"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_recolte") != null ? rs.getDate("date_recolte").toLocalDate() : null,
                        rs.getString("qualite"),
                        rs.getString("type_culture"),
                        rs.getString("localisation")
                );
                r.setIdUser(rs.getInt("id_user"));
                recoltes.add(r);
            }
            System.out.println("[RecolteService] getByUserId(" + idUser + ") → " + recoltes.size() + " récolte(s)");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des récoltes par user: " + e.getMessage());
        }
        return recoltes;
    }
}
