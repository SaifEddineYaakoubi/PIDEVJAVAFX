package org.example.pidev.models;

public class Client {
    private int idClient;
    private String nom;
    private String contact;
    private String adresse;

    public Client() {
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
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", contact='" + contact + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
