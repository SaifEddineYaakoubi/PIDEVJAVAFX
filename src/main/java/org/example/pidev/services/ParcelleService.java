package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Parcelle;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelleService implements IService<Parcelle> {

    private Connection connection;

    public ParcelleService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public boolean add(Parcelle parcelle) {
        String query = "INSERT INTO parcelle (nom, superficie, localisation, etat, id_user) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, parcelle.getNom());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation());
            pst.setString(4, parcelle.getEtat());
            pst.setInt(5, parcelle.getIdUser());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                parcelle.setIdParcelle(rs.getInt(1));
            }

            System.out.println("✅ Parcelle ajoutée avec succès (ID: " + parcelle.getIdParcelle() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la parcelle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Parcelle parcelle) {
        String query = "UPDATE parcelle SET nom = ?, superficie = ?, localisation = ?, etat = ?, id_user = ? WHERE id_parcelle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, parcelle.getNom());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation());
            pst.setString(4, parcelle.getEtat());
            pst.setInt(5, parcelle.getIdUser());
            pst.setInt(6, parcelle.getIdParcelle());
            pst.executeUpdate();
            System.out.println("✅ Parcelle mise à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la parcelle: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM parcelle WHERE id_parcelle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Parcelle supprimée avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la parcelle: " + e.getMessage());
        }
    }

    @Override
    public Parcelle getById(int id) {
        String query = "SELECT * FROM parcelle WHERE id_parcelle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Parcelle(
                        rs.getInt("id_parcelle"),
                        rs.getString("nom"),
                        rs.getDouble("superficie"),
                        rs.getString("localisation"),
                        rs.getString("etat"),
                        rs.getInt("id_user")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de la parcelle: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Parcelle> getAll() {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT * FROM parcelle";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Parcelle parcelle = new Parcelle(
                        rs.getInt("id_parcelle"),
                        rs.getString("nom"),
                        rs.getDouble("superficie"),
                        rs.getString("localisation"),
                        rs.getString("etat"),
                        rs.getInt("id_user")
                );
                parcelles.add(parcelle);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des parcelles: " + e.getMessage());
        }
        return parcelles;
    }
}
