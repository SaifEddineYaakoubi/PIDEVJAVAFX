package org.example.pidev.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Produit;
import org.example.pidev.utils.DBConnection;

public class ProduitService implements IService<Produit> {
                /**
                 * Compte le nombre de produits distincts selon nom, type, unité.
                 */
                public int countProduitsByAttributs(String nom, String type, String unite) {
                    String query = "SELECT COUNT(*) FROM produit WHERE nom = ? AND type = ? AND unite = ?";
                    try (PreparedStatement pst = connection.prepareStatement(query)) {
                        pst.setString(1, nom);
                        pst.setString(2, type);
                        pst.setString(3, unite);
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur lors du comptage des produits: " + e.getMessage());
                    }
                    return 0;
                }
            /**
             * Recherche un produit existant par nom, type, unité et prix.
             * Retourne le produit trouvé ou null si aucun.
             */
            public Produit findProduitIdentique(Produit produit) {
                String query = "SELECT * FROM produit WHERE nom = ? AND type = ? AND unite = ? AND prix_unitaire = ?";
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, produit.getNom());
                    pst.setString(2, produit.getType());
                    pst.setString(3, produit.getUnite());
                    pst.setDouble(4, produit.getPrixUnitaire());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        Produit p = new Produit(
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getString("unite"),
                            rs.getDouble("prix_unitaire")
                        );
                        p.setIdProduit(rs.getInt("id_produit"));
                        return p;
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la recherche d'un produit identique: " + e.getMessage());
                }
                return null;
            }
        /**
         * Contrôle de saisie pour un produit.
         * Retourne null si tout est OK, sinon le message d'erreur.
         */
        public String controleSaisie(Produit produit) {
            if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
                return "Le nom du produit ne doit pas être vide.";
            }
            if (produit.getType() == null || produit.getType().trim().isEmpty()) {
                return "Le type du produit ne doit pas être vide.";
            }
            if (produit.getUnite() == null || produit.getUnite().trim().isEmpty()) {
                return "L'unité du produit ne doit pas être vide.";
            }
            String unite = produit.getUnite().trim().toUpperCase();
            if (!unite.equals("L") && !unite.equals("KG")) {
                return "L'unité doit être 'L' ou 'KG'.";
            }
            // Prix unitaire
            if (produit.getPrixUnitaire() <= 0) {
                return "Le prix unitaire doit être positif.";
            }
            return null;
        }
    private Connection connection;

    public ProduitService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public boolean add(Produit produit) {
        String query = "INSERT INTO produit (nom, type, unite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getType());
            pst.setString(3, produit.getUnite());
            pst.setDouble(4, produit.getPrixUnitaire());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                produit.setIdProduit(rs.getInt(1));
            }
            System.out.println("✅ Produit ajouté avec succès (ID: " + produit.getIdProduit() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du produit: " + e.getMessage());
            throw new RuntimeException("Erreur base de données: " + e.getMessage());
        }
    }

    @Override
    public void update(Produit produit) {
        if (produit.getIdProduit() <= 0) {
            throw new IllegalArgumentException("L'ID du produit doit être un nombre positif.");
        }
        String query = "UPDATE produit SET nom = ?, type = ?, unite = ?, prix_unitaire = ? WHERE id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getType());
            pst.setString(3, produit.getUnite());
            pst.setDouble(4, produit.getPrixUnitaire());
            pst.setInt(5, produit.getIdProduit());
            pst.executeUpdate();
            System.out.println("✅ Produit mis à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du produit: " + e.getMessage());
            throw new RuntimeException("Erreur base de données: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM produit WHERE id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Produit supprimé avec succès");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression du produit: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Produit getById(int id) {
        String query = "SELECT * FROM produit WHERE id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("unite"),
                        rs.getDouble("prix_unitaire")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du produit: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("unite"),
                        rs.getDouble("prix_unitaire")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des produits: " + e.getMessage());
        }
        return produits;
    }
}
