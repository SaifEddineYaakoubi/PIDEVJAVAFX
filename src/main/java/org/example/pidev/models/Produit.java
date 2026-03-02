package org.example.pidev.models;

public class Produit {
    private int idProduit;
    private String nom;
    private String type;
    private String unite;
    private double prixUnitaire;
    private int idUser;

    public Produit() {
    }

    public Produit(int idProduit, String nom, String type, String unite, double prixUnitaire) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.type = type;
        this.unite = unite;
        this.prixUnitaire = prixUnitaire;
    }

    public Produit(String nom, String type, String unite, double prixUnitaire) {
        this.nom = nom;
        this.type = type;
        this.unite = unite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    @Override
    public String toString() {
        return "Produit{" +
                "idProduit=" + idProduit +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", unite='" + unite + '\'' +
                ", prixUnitaire=" + prixUnitaire +
                '}';
    }
}
