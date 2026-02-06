package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class UtilisateurService implements IService<Utilisateur> {
    @Override
    public boolean add(Utilisateur utilisateur) {

        String sql = "INSERT INTO utilisateur " +
                "(nom, prenom, email, mot_de_passe, role, statut, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMotDePasse());
            ps.setString(5, utilisateur.getRole().name()); // enum → String
            ps.setBoolean(6, utilisateur.isStatut());
            ps.setDate(7, java.sql.Date.valueOf(utilisateur.getDateCreation()));

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error adding user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET " +
                "nom = ?, " +
                "prenom = ?, " +
                "email = ?, " +
                "mot_de_passe = ?, " +
                "role = ?, " +
                "statut = ?, " +
                "date_creation = ? " +
                "WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMotDePasse());
            ps.setString(5, utilisateur.getRole().name());
            ps.setBoolean(6, utilisateur.isStatut());
            ps.setDate(7, java.sql.Date.valueOf(utilisateur.getDateCreation()));
            ps.setInt(8, utilisateur.getIdUser());

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID !");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Utilisateur supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID !");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }


    @Override
    public Utilisateur getById(int id) {
        return null;
    }

    @Override
    public List<Utilisateur> getAll() {
        return List.of();
    }
}
