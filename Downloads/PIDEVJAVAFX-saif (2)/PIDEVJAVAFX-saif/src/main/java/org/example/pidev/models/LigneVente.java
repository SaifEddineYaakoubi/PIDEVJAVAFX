package org.example.pidev.models;

public class LigneVente {
    private int idLigne;
    private double quantite;
    private double prix;
    private int idVente;
    private int idProduit;

    public LigneVente() {
    }

    public LigneVente(int idLigne, double quantite, double prix, int idVente, int idProduit) {
        this.idLigne = idLigne;
        this.quantite = quantite;
        this.prix = prix;
        this.idVente = idVente;
        this.idProduit = idProduit;
    }

    public LigneVente(double quantite, double prix, int idVente, int idProduit) {
        this.quantite = quantite;
        this.prix = prix;
        this.idVente = idVente;
        this.idProduit = idProduit;
    }

    public int getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(int idLigne) {
        this.idLigne = idLigne;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    @Override
    public String toString() {
        return "LigneVente{" +
                "idLigne=" + idLigne +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", idVente=" + idVente +
                ", idProduit=" + idProduit +
                '}';
    }
}
