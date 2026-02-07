package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Culture;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CultureService implements IService<Culture> {

    private Connection connection;

    // États de croissance valides
    private static final List<String> ETATS_CROISSANCE_VALIDES = Arrays.asList(
            "germination", "croissance", "floraison", "mature", "récolté"
    );

    // Constantes de validation
    private static final int TYPE_CULTURE_MIN_LENGTH = 2;
    private static final int TYPE_CULTURE_MAX_LENGTH = 100;

    public CultureService() {
        connection = DBConnection.getConnection();
    }

    // ==========================================
    // MÉTHODES DE VALIDATION
    // ==========================================

    /**
     * Valide les données d'une culture
     * @param culture La culture à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    public void valider(Culture culture) throws IllegalArgumentException {
        validerTypeCulture(culture.getTypeCulture());
        validerDates(culture.getDatePlantation(), culture.getDateRecoltePrevue());
        validerEtatCroissance(culture.getEtatCroissance());
        validerIdParcelle(culture.getIdParcelle());
    }

    /**
     * Valide le type de culture
     */
    public void validerTypeCulture(String typeCulture) throws IllegalArgumentException {
        if (typeCulture == null || typeCulture.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de culture ne peut pas être vide.");
        }
        if (typeCulture.trim().length() < TYPE_CULTURE_MIN_LENGTH) {
            throw new IllegalArgumentException("Le type de culture doit contenir au moins " + TYPE_CULTURE_MIN_LENGTH + " caractères.");
        }
        if (typeCulture.trim().length() > TYPE_CULTURE_MAX_LENGTH) {
            throw new IllegalArgumentException("Le type de culture ne peut pas dépasser " + TYPE_CULTURE_MAX_LENGTH + " caractères.");
        }
        // Vérification des caractères spéciaux dangereux
        if (typeCulture.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("Le type de culture contient des caractères non autorisés.");
        }
    }

    /**
     * Valide les dates de plantation et de récolte
     */
    public void validerDates(LocalDate datePlantation, LocalDate dateRecoltePrevue) throws IllegalArgumentException {
        if (datePlantation == null) {
            throw new IllegalArgumentException("La date de plantation ne peut pas être vide.");
        }
        if (dateRecoltePrevue == null) {
            throw new IllegalArgumentException("La date de récolte prévue ne peut pas être vide.");
        }
        if (dateRecoltePrevue.isBefore(datePlantation)) {
            throw new IllegalArgumentException("La date de récolte prévue doit être après la date de plantation.");
        }
        if (dateRecoltePrevue.isEqual(datePlantation)) {
            throw new IllegalArgumentException("La date de récolte prévue doit être différente de la date de plantation.");
        }
        // Vérification que la date de plantation n'est pas trop ancienne (max 5 ans)
        if (datePlantation.isBefore(LocalDate.now().minusYears(5))) {
            throw new IllegalArgumentException("La date de plantation ne peut pas être antérieure à 5 ans.");
        }
        // Vérification que la date de récolte n'est pas trop lointaine (max 5 ans)
        if (dateRecoltePrevue.isAfter(LocalDate.now().plusYears(5))) {
            throw new IllegalArgumentException("La date de récolte prévue ne peut pas être supérieure à 5 ans dans le futur.");
        }
    }

    /**
     * Valide l'état de croissance
     */
    public void validerEtatCroissance(String etatCroissance) throws IllegalArgumentException {
        if (etatCroissance == null || etatCroissance.trim().isEmpty()) {
            throw new IllegalArgumentException("L'état de croissance ne peut pas être vide.");
        }
        if (!ETATS_CROISSANCE_VALIDES.contains(etatCroissance.toLowerCase())) {
            throw new IllegalArgumentException("L'état de croissance doit être l'un des suivants: " + ETATS_CROISSANCE_VALIDES);
        }
    }

    /**
     * Valide l'ID de la parcelle
     */
    public void validerIdParcelle(int idParcelle) throws IllegalArgumentException {
        if (idParcelle <= 0) {
            throw new IllegalArgumentException("L'ID de la parcelle doit être un nombre positif.");
        }
    }

    // ==========================================
    // MÉTHODES CRUD
    // ==========================================

    @Override
    public boolean add(Culture culture) {
        // Validation des données avant insertion
        try {
            valider(culture);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO culture (type_culture, date_plantation, date_recolte_prevue, etat_croissance, id_parcelle) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, culture.getTypeCulture().trim());
            pst.setDate(2, Date.valueOf(culture.getDatePlantation()));
            pst.setDate(3, Date.valueOf(culture.getDateRecoltePrevue()));
            pst.setString(4, culture.getEtatCroissance().toLowerCase());
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
        // Validation des données avant mise à jour
        try {
            valider(culture);
            if (culture.getIdCulture() <= 0) {
                throw new IllegalArgumentException("L'ID de la culture doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE culture SET type_culture = ?, date_plantation = ?, date_recolte_prevue = ?, etat_croissance = ?, id_parcelle = ? WHERE id_culture = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, culture.getTypeCulture().trim());
            pst.setDate(2, Date.valueOf(culture.getDatePlantation()));
            pst.setDate(3, Date.valueOf(culture.getDateRecoltePrevue()));
            pst.setString(4, culture.getEtatCroissance().toLowerCase());
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
