package org.example.pidev.models;

import java.time.LocalDate;

/**
 * Symfony schema aligned Vente (Sales) model
 * Fields: id_vente, date_vente, montant_total, id_client, id_user, quantite, id_produit, ville, region, frais_livraison
 */
public class Vente {
    private int idVente;
    private LocalDate dateVente;
    private double montantTotal;
    private int idClient;
    private int idUser;

    // New Symfony fields
    private double quantite;          // Quantity sold
    private Integer idProduit;        // Product ID (FK)
    private String ville;             // City for delivery
    private String region;            // Region for delivery
    private Float fraisLivraison;     // Delivery fees

    public Vente() {
    }

    public Vente(int idVente, LocalDate dateVente, double montantTotal, int idClient, int idUser) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idUser = idUser;
    }

    public Vente(LocalDate dateVente, double montantTotal, int idClient, int idUser) {
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idUser = idUser;
    }

    // Constructor with new Symfony fields
    public Vente(int idVente, LocalDate dateVente, double montantTotal, int idClient, int idUser,
                 double quantite, Integer idProduit, String ville, String region, Float fraisLivraison) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idUser = idUser;
        this.quantite = quantite;
        this.idProduit = idProduit;
        this.ville = ville;
        this.region = region;
        this.fraisLivraison = fraisLivraison;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public Integer getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Float getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(Float fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    @Override
    public String toString() {
        return "Vente{" +
                "idVente=" + idVente +
                ", dateVente=" + dateVente +
                ", montantTotal=" + montantTotal +
                ", idClient=" + idClient +
                ", idUser=" + idUser +
                '}';
    }
}
