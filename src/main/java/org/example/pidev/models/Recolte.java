package org.example.pidev.models;

import java.time.LocalDate;

public class Recolte {
    private int idRecolte;
    private double quantite;
    private LocalDate dateRecolte;
    private String qualite;
    private String typeCulture;
    private String localisation;

    public Recolte() {
    }

    public Recolte(int idRecolte, double quantite, LocalDate dateRecolte, String qualite, String typeCulture, String localisation) {
        this.idRecolte = idRecolte;
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.typeCulture = typeCulture;
        this.localisation = localisation;
    }

    public Recolte(double quantite, LocalDate dateRecolte, String qualite, String typeCulture, String localisation) {
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.typeCulture = typeCulture;
        this.localisation = localisation;
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

    public String getTypeCulture() {
        return typeCulture;
    }

    public void setTypeCulture(String typeCulture) {
        this.typeCulture = typeCulture;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    @Override
    public String toString() {
        return "Recolte{" +
                "idRecolte=" + idRecolte +
                ", quantite=" + quantite +
                ", dateRecolte=" + dateRecolte +
                ", qualite='" + qualite + '\'' +
                ", typeCulture='" + typeCulture + '\'' +
                ", localisation='" + localisation + '\'' +
                '}';
    }
}
