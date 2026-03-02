package org.example.pidev.models;

public class Badge {

    private int id;
    private String nom;
    private String description;
    private String niveau;

    public Badge() {}

    public Badge(int id, String nom, String description, String niveau) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.niveau = niveau;
    }

    // getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

}
