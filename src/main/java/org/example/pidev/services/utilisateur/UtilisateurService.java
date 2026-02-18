package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Role;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    @Override
    public boolean add(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMotDePasse());
            ps.setString(5, utilisateur.getRole().name());
            ps.setBoolean(6, utilisateur.isStatut());
            ps.setDate(7, Date.valueOf(utilisateur.getDateCreation()));

            int result = ps.executeUpdate();
            System.out.println("ADD: " + (result > 0 ? "Succès" : "Échec"));
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur add: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=?, statut=?, date_creation=? " +
                "WHERE id_user=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMotDePasse());
            ps.setString(5, utilisateur.getRole().name());
            ps.setBoolean(6, utilisateur.isStatut());
            ps.setDate(7, Date.valueOf(utilisateur.getDateCreation()));
            ps.setInt(8, utilisateur.getIdUser());

            int rows = ps.executeUpdate();
            System.out.println("UPDATE: " + rows + " ligne(s) modifiée(s)");

        } catch (SQLException e) {
            System.err.println("Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id_user=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("DELETE: " + rows + " ligne(s) supprimée(s)");

        } catch (SQLException e) {
            System.err.println("Erreur delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Utilisateur getById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id_user=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilisateur> getAll() {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY id_user";

        System.out.println("EXÉCUTION DE LA REQUÊTE GET ALL...");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utilisateur user = extractUserFromResultSet(rs);
                list.add(user);
                System.out.println("  → Utilisateur trouvé: " + user.getEmail());
            }

            System.out.println("TOTAL: " + list.size() + " utilisateur(s)");

        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) as cnt FROM utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur existsByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Utilisateur getByEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Utilisateur extractUserFromResultSet(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setIdUser(rs.getInt("id_user"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(Role.valueOf(roleStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Rôle invalide: " + roleStr);
                user.setRole(Role.AGRICULTEUR); // Valeur par défaut
            }
        }

        user.setStatut(rs.getBoolean("statut"));

        Date date = rs.getDate("date_creation");
        if (date != null) {
            user.setDateCreation(date.toLocalDate());
        }

        return user;
    }
}