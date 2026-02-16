package org.example.pidev.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Stock;
import org.example.pidev.utils.DBConnection;

public class StockService implements IService<Stock> {
        /**
         * Contrôle de saisie pour un stock.
         * Retourne null si tout est OK, sinon le message d'erreur.
         */
    public String controleSaisie(Stock stock, org.example.pidev.models.Produit produit) {
        if (stock.getQuantite() <= 0) {
            return "La quantité doit être positive.";
        }
        if (stock.getDateEntree() == null) {
            return "La date d'entrée est obligatoire.";
        }
        if (stock.getDateExpiration() == null) {
            return "La date de sortie est obligatoire.";
        }
        if (!stock.getDateExpiration().isAfter(stock.getDateEntree())) {
            return "La date de sortie doit être postérieure à la date d'entrée.";
        }
        if (produit == null || produit.getNom() == null || produit.getType() == null || produit.getUnite() == null) {
            return "Le produit associé est obligatoire.";
        }
        return null;
    }

    /**
     * Retourne la somme des quantités de stock pour un produit donné (nom, type, unité).
     */
    public double getTotalQuantiteByProduitAttributs(String nom, String type, String unite) {
        String query = "SELECT SUM(s.quantite) FROM stock s JOIN produit p ON s.id_produit = p.id_produit WHERE p.nom = ? AND p.type = ? AND p.unite = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, nom);
            pst.setString(2, type);
            pst.setString(3, unite);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul de la quantité totale: " + e.getMessage());
        }
        return 0;
    }
    private Connection connection;

    public StockService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public boolean add(Stock stock) {
        String query = "INSERT INTO stock (quantite, date_entree, date_expiration, id_produit) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setDouble(1, stock.getQuantite());
            pst.setDate(2, stock.getDateEntree() != null ? java.sql.Date.valueOf(stock.getDateEntree()) : null);
            pst.setDate(3, stock.getDateExpiration() != null ? java.sql.Date.valueOf(stock.getDateExpiration()) : null);
            pst.setInt(4, stock.getIdProduit());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                stock.setIdStock(rs.getInt(1));
            }
            System.out.println("✅ Stock ajouté avec succès (ID: " + stock.getIdStock() + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du stock: " + e.getMessage());
            throw new RuntimeException("Erreur base de données: " + e.getMessage());
        }
    }

    @Override
    public void update(Stock stock) {
        if (stock.getIdStock() <= 0) {
            throw new IllegalArgumentException("L'ID du stock doit être un nombre positif.");
        }
        String query = "UPDATE stock SET quantite = ?, date_entree = ?, date_expiration = ?, id_produit = ? WHERE id_stock = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setDouble(1, stock.getQuantite());
            pst.setDate(2, stock.getDateEntree() != null ? java.sql.Date.valueOf(stock.getDateEntree()) : null);
            pst.setDate(3, stock.getDateExpiration() != null ? java.sql.Date.valueOf(stock.getDateExpiration()) : null);
            pst.setInt(4, stock.getIdProduit());
            pst.setInt(5, stock.getIdStock());
            pst.executeUpdate();
            System.out.println("✅ Stock mis à jour avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du stock: " + e.getMessage());
            throw new RuntimeException("Erreur base de données: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM stock WHERE id_stock = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Stock supprimé avec succès");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression du stock: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Stock getById(int id) {
        String query = "SELECT * FROM stock WHERE id_stock = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Stock(
                        rs.getInt("id_stock"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_entree") != null ? rs.getDate("date_entree").toLocalDate() : null,
                        rs.getDate("date_expiration") != null ? rs.getDate("date_expiration").toLocalDate() : null,
                        rs.getInt("id_produit")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du stock: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Stock> getAll() {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM stock";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Stock stock = new Stock(
                        rs.getInt("id_stock"),
                        rs.getDouble("quantite"),
                        rs.getDate("date_entree") != null ? rs.getDate("date_entree").toLocalDate() : null,
                        rs.getDate("date_expiration") != null ? rs.getDate("date_expiration").toLocalDate() : null,
                        rs.getInt("id_produit")
                );
                stocks.add(stock);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des stocks: " + e.getMessage());
        }
        return stocks;
    }
}
