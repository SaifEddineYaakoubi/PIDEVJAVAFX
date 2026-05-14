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

    // Symfony schema fields
    private String faceDescriptor;     // longtext - JSON array of face detection values
    private boolean faceEnabled;       // tinyint - whether face recognition is enabled
    private String profilePicture;     // varchar - path to profile picture
    private LocalDate dateNaissance;   // date - birth date
    private String sexe;               // varchar - 'homme'/'femme'

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

    public String getFaceDescriptor() {
        return faceDescriptor;
    }

    public void setFaceDescriptor(String faceDescriptor) {
        this.faceDescriptor = faceDescriptor;
    }

    public boolean isFaceEnabled() {
        return faceEnabled;
    }

    public void setFaceEnabled(boolean faceEnabled) {
        this.faceEnabled = faceEnabled;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Backward-compatible alias used by controllers adapted to Symfony schema.
     * Some controllers refer to "faceImagePath" while the model stores it in profilePicture.
     */
    public String getFaceImagePath() {
        return this.profilePicture;
    }

    public void setFaceImagePath(String path) {
        this.profilePicture = path;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
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
        return this.idUser; // For backward compatibility - returns self
    }

    public void setIdAgriculteur(int idAgriculteur) {
        // For backward compatibility - do nothing
    }

    /**
     * Retourne l'ID utilisateur propriétaire des données
     */
    public int getOwnerUserId() {
        // Conserver la logique suivante:
        // - ADMIN voit tout -> retourner 0 pour indiquer "pas d'isolation"
        // - AGRICULTEUR voit ses propres données -> retourner son id
        // - RESPONSABLE_STOCK pour l'instant retourne son propre id (peut être
        //   lié à un agriculteur spécifique si une relation existait)
        if (this.role == null) return this.idUser;
        switch (this.role) {
            case ADMIN:
                return 0;
            case RESPONSABLE_STOCK:
            case AGRICULTEUR:
            default:
                return this.idUser;
        }
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
