package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Parcelle;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParcelleService implements IService<Parcelle> {

    private Connection connection;

    // États valides pour une parcelle
    private static final List<String> ETATS_VALIDES = Arrays.asList("active", "repos", "exploitée");

    // Constantes de validation
    private static final int NOM_MIN_LENGTH = 2;
    private static final int NOM_MAX_LENGTH = 100;
    private static final int LOCALISATION_MIN_LENGTH = 2;
    private static final int LOCALISATION_MAX_LENGTH = 150;
    private static final double SUPERFICIE_MIN = 0.1;
    private static final double SUPERFICIE_MAX = 100000;

    public ParcelleService() {
        connection = DBConnection.getConnection();
    }

    // ==========================================
    // MÉTHODES DE VALIDATION
    // ==========================================

    /**
     * Valide les données d'une parcelle
     * @param parcelle La parcelle à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    public void valider(Parcelle parcelle) throws IllegalArgumentException {
        validerNom(parcelle.getNom());
        validerSuperficie(parcelle.getSuperficie());
        validerLocalisation(parcelle.getLocalisation());
        validerEtat(parcelle.getEtat());
        validerIdUser(parcelle.getIdUser());
    }

    /**
     * Valide le nom de la parcelle
     */
    public void validerNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la parcelle ne peut pas être vide.");
        }
        if (nom.trim().length() < NOM_MIN_LENGTH) {
            throw new IllegalArgumentException("Le nom de la parcelle doit contenir au moins " + NOM_MIN_LENGTH + " caractères.");
        }
        if (nom.trim().length() > NOM_MAX_LENGTH) {
            throw new IllegalArgumentException("Le nom de la parcelle ne peut pas dépasser " + NOM_MAX_LENGTH + " caractères.");
        }
        // Vérification des caractères spéciaux dangereux
        if (nom.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("Le nom de la parcelle contient des caractères non autorisés.");
        }
    }

    /**
     * Valide la superficie de la parcelle
     */
    public void validerSuperficie(double superficie) throws IllegalArgumentException {
        if (superficie < SUPERFICIE_MIN) {
            throw new IllegalArgumentException("La superficie doit être supérieure à " + SUPERFICIE_MIN + " m².");
        }
        if (superficie > SUPERFICIE_MAX) {
            throw new IllegalArgumentException("La superficie ne peut pas dépasser " + SUPERFICIE_MAX + " m².");
        }
    }

    /**
     * Valide la localisation de la parcelle
     */
    public void validerLocalisation(String localisation) throws IllegalArgumentException {
        if (localisation == null || localisation.trim().isEmpty()) {
            throw new IllegalArgumentException("La localisation ne peut pas être vide.");
        }
        if (localisation.trim().length() < LOCALISATION_MIN_LENGTH) {
            throw new IllegalArgumentException("La localisation doit contenir au moins " + LOCALISATION_MIN_LENGTH + " caractères.");
        }
        if (localisation.trim().length() > LOCALISATION_MAX_LENGTH) {
            throw new IllegalArgumentException("La localisation ne peut pas dépasser " + LOCALISATION_MAX_LENGTH + " caractères.");
        }
    }

    /**
     * Valide l'état de la parcelle
     */
    public void validerEtat(String etat) throws IllegalArgumentException {
        if (etat == null || etat.trim().isEmpty()) {
            throw new IllegalArgumentException("L'état de la parcelle ne peut pas être vide.");
        }
        if (!ETATS_VALIDES.contains(etat.toLowerCase())) {
            throw new IllegalArgumentException("L'état de la parcelle doit être l'un des suivants: " + ETATS_VALIDES);
        }
    }

    /**
     * Valide l'ID de l'utilisateur
     */
    public void validerIdUser(int idUser) throws IllegalArgumentException {
        if (idUser <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur doit être un nombre positif.");
        }
    }

    // ==========================================
    // MÉTHODES CRUD
    // ==========================================

    @Override
    public boolean add(Parcelle parcelle) {
        // Validation des données avant insertion
        try {
            valider(parcelle);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO parcelle (nom, superficie, localisation, etat, id_user) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, parcelle.getNom().trim());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation().trim());
            pst.setString(4, parcelle.getEtat().toLowerCase());
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
        // Validation des données avant mise à jour
        try {
            valider(parcelle);
            if (parcelle.getIdParcelle() <= 0) {
                throw new IllegalArgumentException("L'ID de la parcelle doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE parcelle SET nom = ?, superficie = ?, localisation = ?, etat = ?, id_user = ? WHERE id_parcelle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, parcelle.getNom().trim());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation().trim());
            pst.setString(4, parcelle.getEtat().toLowerCase());
            pst.setInt(5, parcelle.getIdUser());
            pst.setInt(6, parcelle.getIdParcelle());
            pst.executeUpdate();
            System.out.println("✅ Parcelle mise à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la parcelle: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM parcelle WHERE id_parcelle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Parcelle supprimée avec succès");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la parcelle: " + e.getMessage());
            // Lancer une exception pour permettre au contrôleur de gérer l'erreur
            throw new RuntimeException(e.getMessage());
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
