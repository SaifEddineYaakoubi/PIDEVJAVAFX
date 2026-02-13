package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    private Connection connection;

    public UtilisateurService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public boolean add(Utilisateur utilisateur) {
        String query = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getEmail());
            pst.setString(4, utilisateur.getMotDePasse());
            pst.setString(5, utilisateur.getRole());
            pst.setBoolean(6, utilisateur.isStatut());
            pst.setDate(7, utilisateur.getDateCreation() != null ? Date.valueOf(utilisateur.getDateCreation()) : null);
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                utilisateur.setIdUser(rs.getInt(1));
            }

            System.out.println("✅ Utilisateur ajouté avec succès (ID: " + utilisateur.getIdUser() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, role = ?, statut = ?, date_creation = ? WHERE id_user = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getEmail());
            pst.setString(4, utilisateur.getMotDePasse());
            pst.setString(5, utilisateur.getRole());
            pst.setBoolean(6, utilisateur.isStatut());
            pst.setDate(7, utilisateur.getDateCreation() != null ? Date.valueOf(utilisateur.getDateCreation()) : null);
            pst.setInt(8, utilisateur.getIdUser());
            pst.executeUpdate();
            System.out.println("✅ Utilisateur mis à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM utilisateur WHERE id_user = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Utilisateur supprimé avec succès");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Utilisateur getById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id_user = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id_user"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getBoolean("statut"),
                        rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Utilisateur> getAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                        rs.getInt("id_user"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getBoolean("statut"),
                        rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null
                );
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }
}
