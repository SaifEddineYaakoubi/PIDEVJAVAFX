package org.example.pidev.test;

import org.example.pidev.models.Recolte;
import org.example.pidev.services.RecolteService;
import org.example.pidev.utils.DBConnection;

import java.sql.Connection;
import java.util.List;

public class TestRecoltesLoad {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("TEST: Chargement des récoltes");
        System.out.println("========================================\n");

        try {
            // Test 1: Vérifier la connexion
            System.out.println("1️⃣  Test de connexion à la base de données...");
            Connection cnx = DBConnection.getConnection();
            if (cnx != null && !cnx.isClosed()) {
                System.out.println("   ✅ Connexion réussie!\n");
            } else {
                System.out.println("   ❌ Connexion échouée!\n");
                return;
            }

            // Test 2: Charger les récoltes
            System.out.println("2️⃣  Chargement des récoltes depuis la base...");
            RecolteService service = new RecolteService();
            List<Recolte> recoltes = service.getAll();

            System.out.println("   ✅ " + recoltes.size() + " récoltes trouvées\n");

            if (recoltes.isEmpty()) {
                System.out.println("   ⚠️  ATTENTION: Aucune récolte en base de données!");
                System.out.println("   Vérifiez que la table 'recolte' contient des données.\n");
            } else {
                System.out.println("   Détail des récoltes:");
                for (int i = 0; i < recoltes.size(); i++) {
                    Recolte r = recoltes.get(i);
                    System.out.println("   " + (i + 1) + ". ID=" + r.getIdRecolte()
                            + ", Quantité=" + r.getQuantite()
                            + ", Date=" + r.getDateRecolte()
                            + ", Qualité=" + r.getQualite()
                            + ", Type=" + r.getTypeCulture()
                            + ", Localisation=" + r.getLocalisation());
                }
                System.out.println();
            }

            System.out.println("========================================");
            System.out.println("✅ TEST COMPLÉTÉ AVEC SUCCÈS");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

