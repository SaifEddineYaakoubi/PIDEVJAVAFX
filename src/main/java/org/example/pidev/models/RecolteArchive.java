package org.example.pidev.models;

import java.time.LocalDate;

/**
 * Modèle pour les récoltes archivées (supprimées)
 */
public class RecolteArchive {
    private int idArchive;
    private int idRecolteOriginal;
    private double quantite;
    private LocalDate dateRecolte;
    private String qualite;
    private String typeCulture;
    private String localisation;
    private String causeSupression;  // Raison de la suppression
    private LocalDate dateArchivage;   // Date d'archivage
    private int idUser;

    // =====================
    // Constructeurs
    // =====================

    public RecolteArchive() {}

    public RecolteArchive(int idRecolteOriginal, double quantite, LocalDate dateRecolte,
                          String qualite, String typeCulture, String localisation,
                          String causeSupression, LocalDate dateArchivage) {
        this.idRecolteOriginal = idRecolteOriginal;
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.typeCulture = typeCulture;
        this.localisation = localisation;
        this.causeSupression = causeSupression;
        this.dateArchivage = dateArchivage;
    }

    public RecolteArchive(int idArchive, int idRecolteOriginal, double quantite, LocalDate dateRecolte,
                          String qualite, String typeCulture, String localisation,
                          String causeSupression, LocalDate dateArchivage) {
        this.idArchive = idArchive;
        this.idRecolteOriginal = idRecolteOriginal;
        this.quantite = quantite;
        this.dateRecolte = dateRecolte;
        this.qualite = qualite;
        this.typeCulture = typeCulture;
        this.localisation = localisation;
        this.causeSupression = causeSupression;
        this.dateArchivage = dateArchivage;
    }

    // =====================
    // Getters/Setters
    // =====================

    public int getIdArchive() { return idArchive; }
    public void setIdArchive(int idArchive) { this.idArchive = idArchive; }

    public int getIdRecolteOriginal() { return idRecolteOriginal; }
    public void setIdRecolteOriginal(int idRecolteOriginal) { this.idRecolteOriginal = idRecolteOriginal; }

    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }

    public LocalDate getDateRecolte() { return dateRecolte; }
    public void setDateRecolte(LocalDate dateRecolte) { this.dateRecolte = dateRecolte; }

    public String getQualite() { return qualite; }
    public void setQualite(String qualite) { this.qualite = qualite; }

    public String getTypeCulture() { return typeCulture; }
    public void setTypeCulture(String typeCulture) { this.typeCulture = typeCulture; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getCauseSupression() { return causeSupression; }
    public void setCauseSupression(String causeSupression) { this.causeSupression = causeSupression; }

    public LocalDate getDateArchivage() { return dateArchivage; }
    public void setDateArchivage(LocalDate dateArchivage) { this.dateArchivage = dateArchivage; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    @Override
    public String toString() {
        return "RecolteArchive{" +
                "idArchive=" + idArchive +
                ", idRecolteOriginal=" + idRecolteOriginal +
                ", quantite=" + quantite +
                ", dateRecolte=" + dateRecolte +
                ", qualite='" + qualite + '\'' +
                ", typeCulture='" + typeCulture + '\'' +
                ", localisation='" + localisation + '\'' +
                ", causeSupression='" + causeSupression + '\'' +
                ", dateArchivage=" + dateArchivage +
                '}';
    }
}

