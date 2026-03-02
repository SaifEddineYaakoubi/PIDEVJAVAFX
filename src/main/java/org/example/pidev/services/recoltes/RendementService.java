package org.example.pidev.services.recoltes;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Rendement;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendementService implements IService<Rendement> {

    private final Connection connection;

    // Constantes de validation
    private static final double SURFACE_MIN = 0.01;
    private static final double SURFACE_MAX = 10000.0;

    public RendementService() {
        connection = DBConnection.getConnection();
    }

    // =====================
    // Validation
    // =====================
    public void valider(Rendement rendement) throws IllegalArgumentException {
        validerSurfaceExploitee(rendement.getSurfaceExploitee());
        validerQuantiteTotale(rendement.getQuantiteTotale());
        validerProductivite(rendement.getProductivite());
        validerIdRecolte(rendement.getIdRecolte());
    }

    public void validerSurfaceExploitee(double surface) throws IllegalArgumentException {
        if (surface < SURFACE_MIN) {
            throw new IllegalArgumentException("La surface exploitée doit être au minimum " + SURFACE_MIN + " hectares.");
        }
        if (surface > SURFACE_MAX) {
            throw new IllegalArgumentException("La surface exploitée ne peut pas dépasser " + SURFACE_MAX + " hectares.");
        }
    }

    public void validerQuantiteTotale(double quantite) throws IllegalArgumentException {
        if (quantite < 0) {
            throw new IllegalArgumentException("La quantité totale ne peut pas être négative.");
        }
        if (quantite > 1_000_000) {
            throw new IllegalArgumentException("La quantité totale est trop grande.");
        }
    }

    public void validerProductivite(double productivite) throws IllegalArgumentException {
        if (productivite < 0) {
            throw new IllegalArgumentException("La productivité ne peut pas être négative.");
        }
        if (productivite > 1_000_000) {
            throw new IllegalArgumentException("La productivité est trop grande.");
        }
    }

    public void validerIdRecolte(int idRecolte) throws IllegalArgumentException {
        if (idRecolte <= 0) {
            throw new IllegalArgumentException("L'ID de la récolte doit être un nombre positif.");
        }
    }

    // =====================
    // CRUD
    // =====================

    @Override
    public boolean add(Rendement rendement) {
        try {
            valider(rendement);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        String query = "INSERT INTO rendement (surface_exploitee, quantite_totale, productivite, id_recolte) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setDouble(1, rendement.getSurfaceExploitee());
            pst.setDouble(2, rendement.getQuantiteTotale());
            pst.setDouble(3, rendement.getProductivite());
            pst.setInt(4, rendement.getIdRecolte());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                rendement.setIdRendement(rs.getInt(1));
            }

            System.out.println("✅ Rendement ajouté avec succès (ID: " + rendement.getIdRendement() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du rendement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Rendement rendement) {
        try {
            valider(rendement);
            if (rendement.getIdRendement() <= 0) {
                throw new IllegalArgumentException("L'ID du rendement doit être un nombre positif.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return;
        }

        String query = "UPDATE rendement SET surface_exploitee = ?, quantite_totale = ?, productivite = ?, id_recolte = ? WHERE id_rendement = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setDouble(1, rendement.getSurfaceExploitee());
            pst.setDouble(2, rendement.getQuantiteTotale());
            pst.setDouble(3, rendement.getProductivite());
            pst.setInt(4, rendement.getIdRecolte());
            pst.setInt(5, rendement.getIdRendement());
            pst.executeUpdate();
            System.out.println("✅ Rendement mis à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du rendement: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String query = "DELETE FROM rendement WHERE id_rendement = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Rendement supprimé avec succès");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression du rendement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Rendement getById(int id) {
        String query = "SELECT * FROM rendement WHERE id_rendement = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du rendement: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Rendement> getAll() {
        int ownerId = org.example.pidev.utils.Session.getOwnerUserId();
        if (ownerId > 0) {
            return getByUserId(ownerId);
        }
        List<Rendement> rendements = new ArrayList<>();
        String query = "SELECT * FROM rendement";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Rendement r = new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
                rendements.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des rendements: " + e.getMessage());
        }
        return rendements;
    }

    /**
     * Récupère les rendements d'un utilisateur via ses récoltes
     */
    public List<Rendement> getByUserId(int idUser) {
        List<Rendement> rendements = new ArrayList<>();
        String query = "SELECT rd.* FROM rendement rd JOIN recolte r ON rd.id_recolte = r.id_recolte WHERE r.id_user = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Rendement r = new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
                rendements.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des rendements par user: " + e.getMessage());
        }
        return rendements;
    }

    // =====================
    // Méthodes utilitaires
    // =====================

    /**
     * Récupère tous les rendements pour une récolte spécifique
     */
    public List<Rendement> getByIdRecolte(int idRecolte) {
        List<Rendement> rendements = new ArrayList<>();
        String query = "SELECT * FROM rendement WHERE id_recolte = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Rendement r = new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
                rendements.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des rendements: " + e.getMessage());
        }
        return rendements;
    }

    /**
     * Calcule la productivité moyenne pour une récolte
     */
    public double getProductiviteMoyenne(int idRecolte) {
        String query = "SELECT AVG(productivite) as moyenne FROM rendement WHERE id_recolte = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul de la productivité moyenne: " + e.getMessage());
        }
        return 0.0;
    }

    // =====================
    // Calcul automatique du rendement
    // =====================

    /**
     * Calcule automatiquement la productivité à partir de la quantité totale et de la surface exploitée
     * Productivité = Quantité totale / Surface exploitée (en kg/hectare ou unité/hectare)
     *
     * @param quantiteTotale La quantité totale récoltée
     * @param surfaceExploitee La surface exploitée en hectares
     * @return La productivité calculée
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public double calculerProductivite(double quantiteTotale, double surfaceExploitee) throws IllegalArgumentException {
        if (surfaceExploitee <= 0) {
            throw new IllegalArgumentException("La surface exploitée doit être positive.");
        }
        if (quantiteTotale < 0) {
            throw new IllegalArgumentException("La quantité totale ne peut pas être négative.");
        }

        double productivite = quantiteTotale / surfaceExploitee;

        if (productivite > 1_000_000) {
            throw new IllegalArgumentException("La productivité calculée dépasse les limites acceptables.");
        }

        return productivite;
    }

    /**
     * Crée automatiquement un rendement en calculant la productivité
     * Utilise les données de récolte (quantité) et les paramètres fournis
     *
     * @param quantiteTotale La quantité totale récoltée
     * @param surfaceExploitee La surface exploitée en hectares
     * @param idRecolte L'ID de la récolte associée
     * @return Un objet Rendement avec productivité calculée automatiquement
     */
    public Rendement creerRendementAutomatique(double quantiteTotale, double surfaceExploitee, int idRecolte) {
        try {
            double productivite = calculerProductivite(quantiteTotale, surfaceExploitee);
            return new Rendement(surfaceExploitee, quantiteTotale, productivite, idRecolte);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur lors de la création du rendement: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ajoute automatiquement un rendement en calculant la productivité
     *
     * @param quantiteTotale La quantité totale récoltée
     * @param surfaceExploitee La surface exploitée en hectares
     * @param idRecolte L'ID de la récolte associée
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterRendementAutomatique(double quantiteTotale, double surfaceExploitee, int idRecolte) {
        Rendement rendement = creerRendementAutomatique(quantiteTotale, surfaceExploitee, idRecolte);
        if (rendement != null) {
            return add(rendement);
        }
        return false;
    }

    /**
     * Recalcule la productivité d'un rendement existant en fonction de nouvelles données
     * et met à jour le rendement en base de données
     *
     * @param idRendement L'ID du rendement à mettre à jour
     * @param quantiteTotale La nouvelle quantité totale
     * @param surfaceExploitee La nouvelle surface exploitée
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean recalculerProductivite(int idRendement, double quantiteTotale, double surfaceExploitee) {
        Rendement rendement = getById(idRendement);
        if (rendement == null) {
            System.out.println("❌ Rendement non trouvé avec l'ID: " + idRendement);
            return false;
        }

        try {
            double nouvelleProductivite = calculerProductivite(quantiteTotale, surfaceExploitee);
            rendement.setQuantiteTotale(quantiteTotale);
            rendement.setSurfaceExploitee(surfaceExploitee);
            rendement.setProductivite(nouvelleProductivite);
            update(rendement);
            System.out.println("✅ Productivité recalculée et mise à jour: " + String.format("%.2f", nouvelleProductivite));
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur lors du recalcul: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère la productivité totale (somme) pour une récolte
     *
     * @param idRecolte L'ID de la récolte
     * @return La somme des productivités
     */
    public double getProductiviteTotale(int idRecolte) {
        String query = "SELECT SUM(productivite) as total FROM rendement WHERE id_recolte = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return Double.isNaN(total) ? 0.0 : total;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul de la productivité totale: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Récupère la quantité totale récoltée pour une récolte
     *
     * @param idRecolte L'ID de la récolte
     * @return La somme des quantités totales
     */
    public double getQuantiteTotalePourRecolte(int idRecolte) {
        String query = "SELECT SUM(quantite_totale) as total FROM rendement WHERE id_recolte = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return Double.isNaN(total) ? 0.0 : total;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul de la quantité totale: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Récupère la surface totale exploitée pour une récolte
     *
     * @param idRecolte L'ID de la récolte
     * @return La somme des surfaces exploitées
     */
    public double getSurfaceTotalePourRecolte(int idRecolte) {
        String query = "SELECT SUM(surface_exploitee) as total FROM rendement WHERE id_recolte = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return Double.isNaN(total) ? 0.0 : total;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du calcul de la surface totale: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Récupère le rendement maximum (meilleure productivité) pour une récolte
     *
     * @param idRecolte L'ID de la récolte
     * @return Le rendement avec la plus haute productivité
     */
    public Rendement getRendementMaximum(int idRecolte) {
        String query = "SELECT * FROM rendement WHERE id_recolte = ? ORDER BY productivite DESC LIMIT 1";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du rendement maximum: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère le rendement minimum (pire productivité) pour une récolte
     *
     * @param idRecolte L'ID de la récolte
     * @return Le rendement avec la plus basse productivité
     */
    public Rendement getRendementMinimum(int idRecolte) {
        String query = "SELECT * FROM rendement WHERE id_recolte = ? ORDER BY productivite ASC LIMIT 1";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idRecolte);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Rendement(
                        rs.getInt("id_rendement"),
                        rs.getDouble("surface_exploitee"),
                        rs.getDouble("quantite_totale"),
                        rs.getDouble("productivite"),
                        rs.getInt("id_recolte")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du rendement minimum: " + e.getMessage());
        }
        return null;
    }
}

