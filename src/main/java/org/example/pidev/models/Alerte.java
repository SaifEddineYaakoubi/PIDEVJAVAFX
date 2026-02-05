package org.example.pidev.models;

import java.time.LocalDate;

public class Alerte {
    private int idAlerte;
    private String type;
    private String message;
    private LocalDate dateAlerte;
    private String statut;
    private int idUser;

    public Alerte() {
    }

    public Alerte(int idAlerte, String type, String message, LocalDate dateAlerte, String statut, int idUser) {
        this.idAlerte = idAlerte;
        this.type = type;
        this.message = message;
        this.dateAlerte = dateAlerte;
        this.statut = statut;
        this.idUser = idUser;
    }

    public Alerte(String type, String message, LocalDate dateAlerte, String statut, int idUser) {
        this.type = type;
        this.message = message;
        this.dateAlerte = dateAlerte;
        this.statut = statut;
        this.idUser = idUser;
    }

    public int getIdAlerte() {
        return idAlerte;
    }

    public void setIdAlerte(int idAlerte) {
        this.idAlerte = idAlerte;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDateAlerte() {
        return dateAlerte;
    }

    public void setDateAlerte(LocalDate dateAlerte) {
        this.dateAlerte = dateAlerte;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Alerte{" +
                "idAlerte=" + idAlerte +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", dateAlerte=" + dateAlerte +
                ", statut='" + statut + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
