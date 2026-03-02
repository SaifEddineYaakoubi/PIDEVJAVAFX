package org.example.pidev.models;

import java.time.LocalDate;

/**
 * Modèle pour les clients à relancer (inactifs depuis > 30 jours)
 */
public class ClientRelance extends Client {
    private LocalDate lastSaleDate;
    private int daysInactive;
    private boolean promoted;
    private Double couponAmount;

    public ClientRelance() {
        super();
    }

    public ClientRelance(Client client, LocalDate lastSaleDate, int daysInactive) {
        super(client.getIdClient(), client.getNom(), client.getPrenom(),
              client.getEmail(), client.getTelephone(), client.getAdresse(), client.getVille());
        this.lastSaleDate = lastSaleDate;
        this.daysInactive = daysInactive;
        this.promoted = false;
        this.couponAmount = 0.0;
        this.setTotalAchats(client.getTotalAchats());
        this.setStatutFidelite(client.getStatutFidelite());
    }

    // Getters et Setters
    public LocalDate getLastSaleDate() {
        return lastSaleDate;
    }

    public void setLastSaleDate(LocalDate lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public int getDaysInactive() {
        return daysInactive;
    }

    public void setDaysInactive(int daysInactive) {
        this.daysInactive = daysInactive;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public Double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(Double couponAmount) {
        this.couponAmount = couponAmount;
    }

    /**
     * Génère un coupon de réduction basé sur le statut de fidélité
     * @return Montant du coupon
     */
    public Double generateCoupon() {
        double coupon = 0.0;
        String statut = this.getStatutFidelite();

        if ("VIP".equals(statut)) {
            coupon = 100.0;  // 100 DT pour VIP
        } else if ("Fidèle".equals(statut)) {
            coupon = 50.0;   // 50 DT pour Fidèles
        } else {
            coupon = 25.0;   // 25 DT pour Standard
        }

        this.couponAmount = coupon;
        this.promoted = true;
        return coupon;
    }

    @Override
    public String toString() {
        return String.format("%s (Inactif depuis %d jours - Dernière vente: %s)",
            this.getNom(), daysInactive, lastSaleDate);
    }
}

