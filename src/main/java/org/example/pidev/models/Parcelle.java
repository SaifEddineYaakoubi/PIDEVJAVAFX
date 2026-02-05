package org.example.pidev.models;

public class Parcelle {
    private int idParcelle;
    private String nom;
    private double superficie;
    private String localisation;
    private String etat;
    private int idUser;

    public Parcelle() {
    }

    public Parcelle(int idParcelle, String nom, double superficie, String localisation, String etat, int idUser) {
        this.idParcelle = idParcelle;
        this.nom = nom;
        this.superficie = superficie;
        this.localisation = localisation;
        this.etat = etat;
        this.idUser = idUser;
    }

    public Parcelle(String nom, double superficie, String localisation, String etat, int idUser) {
        this.nom = nom;
        this.superficie = superficie;
        this.localisation = localisation;
        this.etat = etat;
        this.idUser = idUser;
    }

    public int getIdParcelle() {
        return idParcelle;
    }

    public void setIdParcelle(int idParcelle) {
        this.idParcelle = idParcelle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Parcelle{" +
                "idParcelle=" + idParcelle +
                ", nom='" + nom + '\'' +
                ", superficie=" + superficie +
                ", localisation='" + localisation + '\'' +
                ", etat='" + etat + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
