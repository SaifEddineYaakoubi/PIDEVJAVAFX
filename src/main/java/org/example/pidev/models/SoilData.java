package org.example.pidev.models;

import java.time.LocalDateTime;

/**
 * Simple data model to hold soil analysis values.
 */
public class SoilData {
    private Integer id;
    private LocalDateTime dateCollected;

    private double ph;
    private double sand;
    private double silt;
    private double clay;
    private double nitrogen; // mg/kg
    private double phosphorus; // mg/kg
    private double potassium; // mg/kg
    private double organicCarbon; // %
    private String source = "LOCAL";

    public SoilData() {
        this.dateCollected = LocalDateTime.now();
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateCollected() { return dateCollected; }
    public void setDateCollected(LocalDateTime dateCollected) { this.dateCollected = dateCollected; }

    public double getPh() { return ph; }
    public void setPh(double ph) { this.ph = ph; }

    public double getSand() { return sand; }
    public void setSand(double sand) { this.sand = sand; }

    public double getSilt() { return silt; }
    public void setSilt(double silt) { this.silt = silt; }

    public double getClay() { return clay; }
    public void setClay(double clay) { this.clay = clay; }

    public double getNitrogen() { return nitrogen; }
    public void setNitrogen(double nitrogen) { this.nitrogen = nitrogen; }

    public double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }

    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }

    public double getOrganicCarbon() { return organicCarbon; }
    public void setOrganicCarbon(double organicCarbon) { this.organicCarbon = organicCarbon; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    // Convenience methods used by UI
    public String getRecommendedCrop() {
        // simple heuristic
        if (ph >= 6 && ph <= 7.5 && nitrogen >= 100) return "Tomate, Maïs, Blé";
        return "Tomate, Blé, Maïs";
    }

    public String getTextureQuality() {
        return String.format("Sable %.0f%%, Limon %.0f%%, Argile %.0f%%", sand, silt, clay);
    }

    public String getFertilizationAdvice() {
        return "Ajouter NPK si l'azote est faible. Ajouter compost si matière organique < 2.5%";
    }

    public String getDiagnostic() {
        return String.format("pH: %.1f\nN: %.0f mg/kg\nP: %.0f mg/kg\nK: %.0f mg/kg\nMO: %.1f%%\nSource: %s\nDate: %s",
                ph, nitrogen, phosphorus, potassium, organicCarbon, source, dateCollected == null ? "-" : dateCollected.toString());
    }

    @Override
    public String toString() {
        return "SoilData{" +
                "id=" + id +
                ", dateCollected=" + dateCollected +
                ", ph=" + ph +
                ", sand=" + sand +
                ", silt=" + silt +
                ", clay=" + clay +
                ", nitrogen=" + nitrogen +
                ", phosphorus=" + phosphorus +
                ", potassium=" + potassium +
                ", organicCarbon=" + organicCarbon +
                ", source='" + source + '\'' +
                '}';
    }
}
