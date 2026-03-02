package org.example.pidev.models;
import java.time.LocalDate;
public class Stock {
    private int idStock;
    private double quantite;
    private LocalDate dateEntree;
    private LocalDate dateExpiration;
    private int idProduit;
    private int idUser;
    public Stock() {
    }
    public Stock(int idStock, double quantite, LocalDate dateEntree, LocalDate dateExpiration, int idProduit) {
        this.idStock = idStock;
        this.quantite = quantite;
        this.dateEntree = dateEntree;
        this.dateExpiration = dateExpiration;
        this.idProduit = idProduit;
    }
    public Stock(double quantite, LocalDate dateEntree, LocalDate dateExpiration, int idProduit) {
        this.quantite = quantite;
        this.dateEntree = dateEntree;
        this.dateExpiration = dateExpiration;
        this.idProduit = idProduit;
    }
    public int getIdStock() {
        return idStock;
    }
    public void setIdStock(int idStock) {
        this.idStock = idStock;
    }
    public double getQuantite() {
        return quantite;
    }
    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }
    public LocalDate getDateEntree() {
        return dateEntree;
    }
    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    public int getIdProduit() {
        return idProduit;
    }
    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }
    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    @Override
    public String toString() {
        return "Stock{" +
                "idStock=" + idStock +
                ", quantite=" + quantite +
                ", dateEntree=" + dateEntree +
                ", dateExpiration=" + dateExpiration +
                ", idProduit=" + idProduit +
                '}';
    }
}