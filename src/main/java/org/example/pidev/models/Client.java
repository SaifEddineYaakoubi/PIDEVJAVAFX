package org.example.pidev.models;

/**
 * Symfony schema aligned Client model
 * Fields: id_client, nom, contact, adresse, id_user, badge
 */
public class Client {
    private int idClient;
    private String nom;
    private String contact;           // Email or phone (Symfony schema)
    private String adresse;
    private int idUser;               // FK to utilisateur
    private String badge;             // Badge level: gold, silver, bronze, etc.

    // Backward compatibility fields (legacy)
    private String prenom;            // Not in Symfony schema
    private String email;             // Mapped to contact
    private String telephone;         // Mapped to contact
    private String ville;             // Not in Symfony schema
    private Double totalAchats;       // Not in Symfony schema
    private String statutFidelite;    // Not in Symfony schema

    public Client() {
    }

    public Client(int idClient, String nom, String contact, String adresse, int idUser) {
        this.idClient = idClient;
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
        this.idUser = idUser;
    }

    public Client(String nom, String contact, String adresse, int idUser) {
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
        this.idUser = idUser;
    }

    // Constructors for backward compatibility
    public Client(int idClient, String nom, String prenom, String email, String telephone, String adresse, String ville) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.contact = email;  // Map to contact
        this.telephone = telephone;
        this.adresse = adresse;
        this.ville = ville;
    }

    public Client(String nom, String prenom, String email, String telephone, String adresse, String ville) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.contact = email;  // Map to contact
        this.telephone = telephone;
        this.adresse = adresse;
        this.ville = ville;
    }

    public Client(int idClient, String nom, String contact, String adresse) {
        this.idClient = idClient;
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
    }

    public Client(String nom, String contact, String adresse) {
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
    }

    // Getters and Setters
    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
        this.email = contact;  // Keep email in sync
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    // Legacy getter methods for backward compatibility
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return contact;  // Return contact field
    }

    public void setEmail(String email) {
        this.email = email;
        this.contact = email;  // Keep contact in sync
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Double getTotalAchats() {
        return totalAchats;
    }

    public void setTotalAchats(Double totalAchats) {
        this.totalAchats = totalAchats;
        updateStatutFidelite();
    }

    public String getStatutFidelite() {
        return statutFidelite;
    }

    public void setStatutFidelite(String statutFidelite) {
        this.statutFidelite = statutFidelite;
    }

    /**
     * Calcule le statut de fidélité basé sur le montant total des achats
     * VIP: totalAchats > 5000 DT
     * Fidèle: 1000 <= totalAchats <= 5000 DT
     * Standard: totalAchats < 1000 DT
     */
    public void updateStatutFidelite() {
        if (totalAchats == null || totalAchats == 0) {
            this.statutFidelite = "Standard";
        } else if (totalAchats > 5000) {
            this.statutFidelite = "VIP";
        } else if (totalAchats >= 1000) {
            this.statutFidelite = "Fidèle";
        } else {
            this.statutFidelite = "Standard";
        }
    }


    @Override
    public String toString() {
        return nom;
    }
}
