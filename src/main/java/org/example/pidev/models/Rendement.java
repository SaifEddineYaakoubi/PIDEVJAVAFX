package org.example.pidev.models;

public class Rendement {
    private int idRendement;
    private double surfaceExploitee;
    private double quantiteTotale;
    private double productivite;
    private int idRecolte;

    public Rendement() {
    }

    public Rendement(int idRendement, double surfaceExploitee, double quantiteTotale, double productivite, int idRecolte) {
        this.idRendement = idRendement;
        this.surfaceExploitee = surfaceExploitee;
        this.quantiteTotale = quantiteTotale;
        this.productivite = productivite;
        this.idRecolte = idRecolte;
    }

    public Rendement(double surfaceExploitee, double quantiteTotale, double productivite, int idRecolte) {
        this.surfaceExploitee = surfaceExploitee;
        this.quantiteTotale = quantiteTotale;
        this.productivite = productivite;
        this.idRecolte = idRecolte;
    }

    public int getIdRendement() {
        return idRendement;
    }

    public void setIdRendement(int idRendement) {
        this.idRendement = idRendement;
    }

    public double getSurfaceExploitee() {
        return surfaceExploitee;
    }

    public void setSurfaceExploitee(double surfaceExploitee) {
        this.surfaceExploitee = surfaceExploitee;
    }

    public double getQuantiteTotale() {
        return quantiteTotale;
    }

    public void setQuantiteTotale(double quantiteTotale) {
        this.quantiteTotale = quantiteTotale;
    }

    public double getProductivite() {
        return productivite;
    }

    public void setProductivite(double productivite) {
        this.productivite = productivite;
    }

    public int getIdRecolte() {
        return idRecolte;
    }

    public void setIdRecolte(int idRecolte) {
        this.idRecolte = idRecolte;
    }

    @Override
    public String toString() {
        return "Rendement{" +
                "idRendement=" + idRendement +
                ", surfaceExploitee=" + surfaceExploitee +
                ", quantiteTotale=" + quantiteTotale +
                ", productivite=" + productivite +
                ", idRecolte=" + idRecolte +
                '}';
    }
}
