package org.example.pidev.services;

import org.example.pidev.models.Client;

import java.util.List;

/**
 * FideliteService - Service de gestion de la fidélité des clients
 * ⭐ Gère les statistiques de fidélité, les points, et les statuts VIP/Fidèle/Standard
 */
public class FideliteService {

    private ClientService clientService;

    /**
     * Constructeur par défaut
     */
    public FideliteService() {
        this.clientService = new ClientService();
    }

    /**
     * Constructeur avec injection de ClientService
     */
    public FideliteService(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Calcule le statut de fidélité d'un client basé sur ses achats
     * - VIP: > 5000 DT d'achats
     * - Fidèle: Entre 2000 et 5000 DT
     * - Standard: < 2000 DT
     */
    public String calculateStatut(Double totalAchats) {
        if (totalAchats == null || totalAchats <= 0) {
            return "Standard";
        }

        if (totalAchats >= 5000) {
            return "VIP";
        } else if (totalAchats >= 2000) {
            return "Fidèle";
        } else {
            return "Standard";
        }
    }

    /**
     * Calcule la remise applicable selon le statut de fidélité
     * - VIP: 15% de réduction
     * - Fidèle: 10% de réduction
     * - Standard: 0% de réduction
     */
    public Double calculateRemise(String statut, Double montant) {
        if (montant == null || montant <= 0) {
            return 0.0;
        }

        switch (statut) {
            case "VIP":
                return montant * 0.15;
            case "Fidèle":
                return montant * 0.10;
            default:
                return 0.0;
        }
    }

    /**
     * Enrichit un client avec ses informations de fidélité
     */
    public void enrichClient(Client client) {
        if (client != null) {
            Double totalAchats = clientService.calculateTotalAchats(client.getIdClient());
            client.setTotalAchats(totalAchats);
            client.setStatutFidelite(calculateStatut(totalAchats));
        }
    }

    /**
     * Enrichit une liste de clients avec leurs informations de fidélité
     */
    public void enrichClients(List<Client> clients) {
        for (Client client : clients) {
            enrichClient(client);
        }
    }

    /**
     * Récupère les statistiques de fidélité
     */
    public String getStatistiques() {
        try {
            List<Client> allClients = clientService.getAllWithFidelite();

            if (allClients == null || allClients.isEmpty()) {
                return "Aucun client disponible.";
            }

            int vipCount = 0;
            int fideleCount = 0;
            int standardCount = 0;

            for (Client client : allClients) {
                String statut = client.getStatutFidelite();
                if ("VIP".equals(statut)) {
                    vipCount++;
                } else if ("Fidèle".equals(statut)) {
                    fideleCount++;
                } else {
                    standardCount++;
                }
            }

            return String.format(
                "Statistiques Fidélité:\n" +
                "- VIP: %d clients\n" +
                "- Fidèle: %d clients\n" +
                "- Standard: %d clients\n" +
                "- Total: %d clients",
                vipCount, fideleCount, standardCount, allClients.size()
            );
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques: " + e.getMessage());
            return "Erreur lors de la récupération des statistiques.";
        }
    }
}

