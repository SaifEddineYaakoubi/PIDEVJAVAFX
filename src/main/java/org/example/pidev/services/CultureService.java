package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Culture;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CultureService implements IService<Culture> {

    private Connection connection;

    public CultureService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public boolean add(Culture culture) {
        String query = "INSERT INTO culture (type_culture, date_plantation, date_recolte_prevue, etat_croissance, id_parcelle) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, culture.getTypeCulture());
            pst.setDate(2, culture.getDatePlantation() != null ? Date.valueOf(culture.getDatePlantation()) : null);
            pst.setDate(3, culture.getDateRecoltePrevue() != null ? Date.valueOf(culture.getDateRecoltePrevue()) : null);
            pst.setString(4, culture.getEtatCroissance());
            pst.setInt(5, culture.getIdParcelle());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                culture.setIdCulture(rs.getInt(1));
            }

            System.out.println("✅ Culture ajoutée avec succès (ID: " + culture.getIdCulture() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la culture: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Culture culture) {
        String query = "UPDATE culture SET type_culture = ?, date_plantation = ?, date_recolte_prevue = ?, etat_croissance = ?, id_parcelle = ? WHERE id_culture = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, culture.getTypeCulture());
            pst.setDate(2, culture.getDatePlantation() != null ? Date.valueOf(culture.getDatePlantation()) : null);
            pst.setDate(3, culture.getDateRecoltePrevue() != null ? Date.valueOf(culture.getDateRecoltePrevue()) : null);
            pst.setString(4, culture.getEtatCroissance());
            pst.setInt(5, culture.getIdParcelle());
            pst.setInt(6, culture.getIdCulture());
            pst.executeUpdate();
            System.out.println("✅ Culture mise à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la culture: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM culture WHERE id_culture = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Culture supprimée avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la culture: " + e.getMessage());
        }
    }

    @Override
    public Culture getById(int id) {
        String query = "SELECT * FROM culture WHERE id_culture = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Culture(
                        rs.getInt("id_culture"),
                        rs.getString("type_culture"),
                        rs.getDate("date_plantation") != null ? rs.getDate("date_plantation").toLocalDate() : null,
                        rs.getDate("date_recolte_prevue") != null ? rs.getDate("date_recolte_prevue").toLocalDate() : null,
                        rs.getString("etat_croissance"),
                        rs.getInt("id_parcelle")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération de la culture: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Culture> getAll() {
        List<Culture> cultures = new ArrayList<>();
        String query = "SELECT * FROM culture";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Culture culture = new Culture(
                        rs.getInt("id_culture"),
                        rs.getString("type_culture"),
                        rs.getDate("date_plantation") != null ? rs.getDate("date_plantation").toLocalDate() : null,
                        rs.getDate("date_recolte_prevue") != null ? rs.getDate("date_recolte_prevue").toLocalDate() : null,
                        rs.getString("etat_croissance"),
                        rs.getInt("id_parcelle")
                );
                cultures.add(culture);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des cultures: " + e.getMessage());
        }
        return cultures;
    }
}
