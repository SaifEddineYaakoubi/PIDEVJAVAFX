package org.example.pidev.models;

public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String ville;
    private Double totalAchats;
    private String statutFidelite;

    public Client() {
    }

    public Client(int idClient, String nom, String prenom, String email, String telephone, String adresse, String ville) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.ville = ville;
    }

    public Client(String nom, String prenom, String email, String telephone, String adresse, String ville) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.ville = ville;
    }

    // Constructeur legacy pour compatibilité
    public Client(int idClient, String nom, String contact, String adresse) {
        this.idClient = idClient;
        this.nom = nom;
        this.email = contact;
        this.adresse = adresse;
    }

    public Client(String nom, String contact, String adresse) {
        this.nom = nom;
        this.email = contact;
        this.adresse = adresse;
    }

    // Getters et Setters
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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

    // Getter legacy
    public String getContact() {
        return email;
    }

    public void setContact(String contact) {
        this.email = contact;
    }

    @Override
    public String toString() {
        return nom;
    }
}
