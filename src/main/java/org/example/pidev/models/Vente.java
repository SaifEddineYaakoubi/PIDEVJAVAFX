package org.example.pidev.models;

import java.time.LocalDate;

public class Vente {
    private int idVente;
    private LocalDate dateVente;
    private double montantTotal;
    private int idClient;
    private int idUser;

    public Vente() {
    }

    public Vente(int idVente, LocalDate dateVente, double montantTotal, int idClient, int idUser) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idUser = idUser;
    }

    public Vente(LocalDate dateVente, double montantTotal, int idClient, int idUser) {
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idUser = idUser;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Vente{" +
                "idVente=" + idVente +
                ", dateVente=" + dateVente +
                ", montantTotal=" + montantTotal +
                ", idClient=" + idClient +
                ", idUser=" + idUser +
                '}';
    }
}
