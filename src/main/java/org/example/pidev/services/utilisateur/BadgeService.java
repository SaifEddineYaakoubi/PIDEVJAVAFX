package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Badge;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BadgeService implements IService<Badge> {

    private Connection cnx;

    public BadgeService() {
        getFreshConnection();
        ensureUserBadgeTableExists();
        System.out.println("[BadgeService] BadgeService initialisé");
    }

    // Nouvelle méthode pour obtenir une connexion fraîche
    private void getFreshConnection() {
        try {
            cnx = DBConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.out.println("[BadgeService] Connexion fermée, tentative de reconnexion...");
                cnx = DBConnection.getInstance().getConnection();
            }
        } catch (SQLException e) {
            System.out.println("[BadgeService] Erreur de connexion: " + e.getMessage());
        }
    }

    // Vérifie et réinitialise la connexion si nécessaire
    private boolean checkConnection() {
        try {
            if (cnx == null || cnx.isClosed()) {
                System.out.println("[BadgeService] Connexion perdue, reconnexion...");
                getFreshConnection();
                return cnx != null && !cnx.isClosed();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("[BadgeService] Erreur de vérification connexion: " + e.getMessage());
            return false;
        }
    }

    private void ensureUserBadgeTableExists() {
        if (!checkConnection()) return;

        String sql = "CREATE TABLE IF NOT EXISTS user_badge (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  user_id INT NOT NULL," +
                "  badge_id INT NOT NULL," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  UNIQUE KEY uk_user_badge (user_id, badge_id)" +
                ")";
        try {
            Statement st = cnx.createStatement();
            st.execute(sql);
            System.out.println("[BadgeService] ✅ Table user_badge vérifiée/créée");
        } catch (SQLException e) {
            System.out.println("[BadgeService] ℹ️ Info table: " + e.getMessage());
        }
    }

    @Override
    public boolean add(Badge b) {
        if (!checkConnection()) return false;

        String sql = "INSERT INTO badge (nom, description, niveau) VALUES (?, ?, ?)";
        try {
            System.out.println("[BadgeService] Ajout: " + b.getNom());
            PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getDescription());
            ps.setString(3, b.getNiveau());
            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    b.setId(keys.getInt(1));
                }
                System.out.println("[BadgeService] ✅ Badge ajouté avec ID: " + b.getId());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR add: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(Badge b) {
        if (!checkConnection()) return;

        String sql = "UPDATE badge SET nom=?, description=?, niveau=? WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getDescription());
            ps.setString(3, b.getNiveau());
            ps.setInt(4, b.getId());
            ps.executeUpdate();
            System.out.println("[BadgeService] ✅ Badge mis à jour: " + b.getId());
        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR update: " + e.getMessage());
        }
    }

    @Override
    public Badge getById(int id) {
        if (!checkConnection()) return null;

        String sql = "SELECT * FROM badge WHERE id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Badge(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("niveau")
                );
            }
        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR getById: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Badge> getAll() {
        List<Badge> list = new ArrayList<>();

        if (!checkConnection()) {
            System.out.println("[BadgeService] ❌ Pas de connexion pour getAll()");
            return list;
        }

        String sql = "SELECT * FROM badge";
        try {
            System.out.println("[BadgeService] Exécution de getAll()...");
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Badge b = new Badge(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("niveau")
                );
                list.add(b);
            }
            System.out.println("[BadgeService] ✅ " + list.size() + " badges trouvés");

            // Affiche les badges dans la console
            for (Badge b : list) {
                System.out.println("  - " + b.getId() + ": " + b.getNom() + " (" + b.getNiveau() + ")");
            }

        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR getAll: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean delete(int id) {
        if (!checkConnection()) return false;

        String sql = "DELETE FROM badge WHERE id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            System.out.println("[BadgeService] ✅ Badge supprimé: " + id);
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR delete: " + e.getMessage());
        }
        return false;
    }

    public boolean assignBadgeToUser(int badgeId, int userId) {
        if (!checkConnection()) return false;

        String sql = "INSERT INTO user_badge (user_id, badge_id) VALUES (?, ?)";
        try {
            System.out.println("[BadgeService] Assignation badge " + badgeId + " à user " + userId);
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, badgeId);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                System.out.println("[BadgeService] ✅ Assignation réussie!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[BadgeService] ❌ ERREUR assignation: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("[BadgeService] L'utilisateur a déjà ce badge");
            }
        }
        return false;
    }
}