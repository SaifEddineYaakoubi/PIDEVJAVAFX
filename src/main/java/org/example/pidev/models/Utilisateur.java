package org.example.pidev.models;

import java.time.LocalDate;

public class Utilisateur {
    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;
    private boolean statut;
    private LocalDate dateCreation;
    private String faceImagePath;
    private int idAgriculteur; // For RESPONSABLE_STOCK: the agriculteur they work for

    public Utilisateur() {
    }

    public Utilisateur(int idUser, String nom, String prenom, String email, String motDePasse, Role role, boolean statut, LocalDate dateCreation) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, Role role, boolean statut, LocalDate dateCreation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public String getFaceImagePath() {
        return faceImagePath;
    }

    public void setFaceImagePath(String faceImagePath) {
        this.faceImagePath = faceImagePath;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRoleString() {
        return role != null ? role.name() : "";
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getIdAgriculteur() {
        return idAgriculteur;
    }

    public void setIdAgriculteur(int idAgriculteur) {
        this.idAgriculteur = idAgriculteur;
    }

    /**
     * Retourne l'ID utilisateur propriétaire des données:
     * - Pour un AGRICULTEUR: son propre idUser
     * - Pour un RESPONSABLE_STOCK: l'idAgriculteur auquel il est rattaché
     * - Pour un ADMIN: 0 (voit tout)
     */
    public int getOwnerUserId() {
        if (role == Role.RESPONSABLE_STOCK && idAgriculteur > 0) {
            return idAgriculteur;
        }
        return idUser;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", statut=" + statut +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
