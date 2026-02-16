package org.example.pidev.models;

import java.time.LocalDate;

public class Recolte {
    private int idRecolte;
    private double quantite;
    private LocalDate dateRecolte;
    private String qualite;
    private int idCulture;

    public Recolte() {
    }

    public Recolte(int idRecolte, double quantite, LocalDate dateRecolte, String qualite, int idCulture) {
        this.idRecolte = idRecolte;
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.idCulture = idCulture;
    }

    public Recolte(double quantite, LocalDate dateRecolte, String qualite, int idCulture) {
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.idCulture = idCulture;
    }

    public int getIdRecolte() {
        return idRecolte;
    }

    public void setIdRecolte(int idRecolte) {
        this.idRecolte = idRecolte;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateRecolte() {
        return dateRecolte;
    }

    public void setDateRecolte(LocalDate dateRecolte) {
        this.dateRecolte = dateRecolte;
    }

    public String getQualite() {
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    public int getIdCulture() {
        return idCulture;
    }

    public void setIdCulture(int idCulture) {
        this.idCulture = idCulture;
    }

    @Override
    public String toString() {
        return "Recolte{" +
                "idRecolte=" + idRecolte +
                ", quantite=" + quantite +
                ", dateRecolte=" + dateRecolte +
                ", qualite='" + qualite + '\'' +
                ", idCulture=" + idCulture +
                '}';
    }
}
