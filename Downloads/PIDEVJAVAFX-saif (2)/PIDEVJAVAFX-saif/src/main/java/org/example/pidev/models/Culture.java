package org.example.pidev.models;

import java.time.LocalDate;

public class Culture {
    private int idCulture;
    private String typeCulture;
    private LocalDate datePlantation;
    private LocalDate dateRecoltePrevue;
    private String etatCroissance;
    private int idParcelle;
    private String nomParcelle; // Champ transient pour affichage

    public Culture() {
    }

    public Culture(int idCulture, String typeCulture, LocalDate datePlantation, LocalDate dateRecoltePrevue, String etatCroissance, int idParcelle) {
        this.idCulture = idCulture;
        this.typeCulture = typeCulture;
        this.datePlantation = datePlantation;
        this.dateRecoltePrevue = dateRecoltePrevue;
        this.etatCroissance = etatCroissance;
        this.idParcelle = idParcelle;
    }

    public Culture(String typeCulture, LocalDate datePlantation, LocalDate dateRecoltePrevue, String etatCroissance, int idParcelle) {
        this.typeCulture = typeCulture;
        this.datePlantation = datePlantation;
        this.dateRecoltePrevue = dateRecoltePrevue;
        this.etatCroissance = etatCroissance;
        this.idParcelle = idParcelle;
    }

    public int getIdCulture() {
        return idCulture;
    }

    public void setIdCulture(int idCulture) {
        this.idCulture = idCulture;
    }

    public String getTypeCulture() {
        return typeCulture;
    }

    public void setTypeCulture(String typeCulture) {
        this.typeCulture = typeCulture;
    }

    public LocalDate getDatePlantation() {
        return datePlantation;
    }

    public void setDatePlantation(LocalDate datePlantation) {
        this.datePlantation = datePlantation;
    }

    public LocalDate getDateRecoltePrevue() {
        return dateRecoltePrevue;
    }

    public void setDateRecoltePrevue(LocalDate dateRecoltePrevue) {
        this.dateRecoltePrevue = dateRecoltePrevue;
    }

    public String getEtatCroissance() {
        return etatCroissance;
    }

    public void setEtatCroissance(String etatCroissance) {
        this.etatCroissance = etatCroissance;
    }

    public int getIdParcelle() {
        return idParcelle;
    }

    public void setIdParcelle(int idParcelle) {
        this.idParcelle = idParcelle;
    }

    public String getNomParcelle() {
        return nomParcelle;
    }

    public void setNomParcelle(String nomParcelle) {
        this.nomParcelle = nomParcelle;
    }

    @Override
    public String toString() {
        return "Culture{" +
                "idCulture=" + idCulture +
                ", typeCulture='" + typeCulture + '\'' +
                ", datePlantation=" + datePlantation +
                ", dateRecoltePrevue=" + dateRecoltePrevue +
                ", etatCroissance='" + etatCroissance + '\'' +
                ", idParcelle=" + idParcelle +
                '}';
    }


}
