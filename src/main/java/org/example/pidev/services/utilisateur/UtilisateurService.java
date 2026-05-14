package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Role;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.DBConnection;
import org.example.pidev.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    @Override
    public boolean add(Utilisateur utilisateur) {
        // Symfony schema: face_descriptor, face_enabled, profile_picture, date_naissance, sexe
        // Note: role is stored in lowercase in Symfony (admin, agriculteur, responsable_stock)
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut, date_creation, " +
                "face_descriptor, face_enabled, profile_picture, date_naissance, sexe) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            // Hacher le mot de passe avant stockage en BDD
            String mdp = utilisateur.getMotDePasse();
            ps.setString(4, PasswordUtils.isHashed(mdp) ? mdp : PasswordUtils.hash(mdp));
            // Symfony stores roles in lowercase
            ps.setString(5, utilisateur.getRole().name().toLowerCase());
            ps.setBoolean(6, utilisateur.isStatut());
            // Handle null or zero date (Symfony may store '0000-00-00' for admin)
            if (utilisateur.getDateCreation() != null) {
                ps.setDate(7, Date.valueOf(utilisateur.getDateCreation()));
            } else {
                ps.setDate(7, Date.valueOf(java.time.LocalDate.now()));
            }

            // New Symfony fields
            ps.setString(8, utilisateur.getFaceDescriptor());
            ps.setBoolean(9, utilisateur.isFaceEnabled());
            ps.setString(10, utilisateur.getProfilePicture());
            ps.setDate(11, utilisateur.getDateNaissance() != null ? Date.valueOf(utilisateur.getDateNaissance()) : null);
            ps.setString(12, utilisateur.getSexe());

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
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=?, statut=?, date_creation=?, " +
                "face_descriptor=?, face_enabled=?, profile_picture=?, date_naissance=?, sexe=? " +
                "WHERE id_user=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            // Hacher le mot de passe si ce n'est pas déjà un hash
            String mdp = utilisateur.getMotDePasse();
            ps.setString(4, PasswordUtils.isHashed(mdp) ? mdp : PasswordUtils.hash(mdp));
            // Symfony stores roles in lowercase
            ps.setString(5, utilisateur.getRole().name().toLowerCase());
            ps.setBoolean(6, utilisateur.isStatut());
            // Handle null or zero date (Symfony may store '0000-00-00' for admin)
            if (utilisateur.getDateCreation() != null) {
                ps.setDate(7, Date.valueOf(utilisateur.getDateCreation()));
            } else {
                ps.setDate(7, Date.valueOf(java.time.LocalDate.now()));
            }

            // New Symfony fields
            ps.setString(8, utilisateur.getFaceDescriptor());
            ps.setBoolean(9, utilisateur.isFaceEnabled());
            ps.setString(10, utilisateur.getProfilePicture());
            ps.setDate(11, utilisateur.getDateNaissance() != null ? Date.valueOf(utilisateur.getDateNaissance()) : null);
            ps.setString(12, utilisateur.getSexe());

            ps.setInt(13, utilisateur.getIdUser());

            int rows = ps.executeUpdate();
            System.out.println("UPDATE: " + rows + " ligne(s) modifiée(s)");

        } catch (SQLException e) {
            System.err.println("Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id_user=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("DELETE: " + rows + " ligne(s) supprimée(s)");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur delete: " + e.getMessage());
            e.printStackTrace();
            return false;
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
        System.out.println("[UtilisateurService.getByEmail] Recherche: " + email);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = extractUserFromResultSet(rs);
                    System.out.println("[UtilisateurService.getByEmail] Trouvé: id=" + u.getIdUser() + " role=" + u.getRole());
                    return u;
                } else {
                    System.out.println("[UtilisateurService.getByEmail] Aucun utilisateur avec email: " + email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Enable face recognition for a user
     */
    public boolean enableFaceRecognition(int userId, String faceDescriptor) {
        String sql = "UPDATE utilisateur SET face_descriptor=?, face_enabled=TRUE WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, faceDescriptor);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            System.out.println("Face recognition enabled for user " + userId);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur enabling face recognition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Disable face recognition for a user
     */
    public boolean disableFaceRecognition(int userId) {
        String sql = "UPDATE utilisateur SET face_enabled=FALSE WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            System.out.println("Face recognition disabled for user " + userId);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur disabling face recognition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get face descriptor for a user
     */
    public String getFaceDescriptor(int userId) {
        String sql = "SELECT face_descriptor FROM utilisateur WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("face_descriptor");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getting face descriptor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update profile picture path
     */
    public boolean updateProfilePicture(int userId, String picturePath) {
        String sql = "UPDATE utilisateur SET profile_picture=? WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, picturePath);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            System.out.println("Profile picture updated for user " + userId);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur updating profile picture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if user has face recognition enabled
     */
    public boolean isFaceRecognitionEnabled(int userId) {
        String sql = "SELECT face_enabled FROM utilisateur WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("face_enabled");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur checking face recognition status: " + e.getMessage());
        }
        return false;
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
                // Symfony stores roles in lowercase (admin, agriculteur, responsable_stock)
                // JavaFX enum uses uppercase — normalize before parsing
                String normalizedRole = roleStr.trim().toUpperCase()
                        .replace("RESPONSABLE_STOCK", "RESPONSABLE_STOCK"); // already handled by toUpperCase
                user.setRole(Role.valueOf(normalizedRole));
            } catch (IllegalArgumentException e) {
                System.err.println("Rôle invalide: " + roleStr + " — utilisation du rôle par défaut AGRICULTEUR");
                user.setRole(Role.AGRICULTEUR); // Valeur par défaut
            }
        }

        user.setStatut(rs.getBoolean("statut"));

        Date date = rs.getDate("date_creation");
        if (date != null) {
            user.setDateCreation(date.toLocalDate());
        } else {
            // Symfony may store '0000-00-00' which is converted to null by zeroDateTimeBehavior=CONVERT_TO_NULL
            user.setDateCreation(java.time.LocalDate.now());
        }

        // Récupérer les nouveaux champs Symfony
        try {
            String faceDescriptor = rs.getString("face_descriptor");
            if (faceDescriptor != null) {
                user.setFaceDescriptor(faceDescriptor);
            }
        } catch (SQLException e) {
            // Colonne n'existe pas, ignorer
        }

        try {
            user.setFaceEnabled(rs.getBoolean("face_enabled"));
        } catch (SQLException e) {
            // Colonne n'existe pas, ignorer
        }

        try {
            String profilePicture = rs.getString("profile_picture");
            if (profilePicture != null) {
                user.setProfilePicture(profilePicture);
            }
        } catch (SQLException e) {
            // Colonne n'existe pas, ignorer
        }

        try {
            Date dateNaissance = rs.getDate("date_naissance");
            if (dateNaissance != null && !rs.wasNull()) {
                user.setDateNaissance(dateNaissance.toLocalDate());
            }
        } catch (SQLException e) {
            // Colonne n'existe pas, ignorer
        }

        try {
            String sexe = rs.getString("sexe");
            if (sexe != null) {
                user.setSexe(sexe);
            }
        } catch (SQLException e) {
            // Colonne n'existe pas, ignorer
        }

        // Backward compatibility: Récupérer id_agriculteur (pour RESPONSABLE_STOCK)
        try {
            int idAgriculteur = rs.getInt("id_agriculteur");
            if (!rs.wasNull()) {
                user.setIdAgriculteur(idAgriculteur);
            }
        } catch (SQLException e) {
            // Colonne n'existe pas encore, ignorer
        }

        return user;
    }
}