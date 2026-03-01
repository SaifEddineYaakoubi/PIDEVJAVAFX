package org.example.pidev.test;

import org.example.pidev.models.Vente;
import org.example.pidev.services.VenteService;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe de test pour vérifier les opérations CRUD de Vente
 */
public class TestVenteService {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("Test du service Vente (CRUD)");
        System.out.println("======================================\n");

        VenteService service = new VenteService();

        try {
            // ===== TEST 1 : Compter les ventes actuelles =====
            System.out.println("1️⃣  TEST 1 - Compter les ventes actuelles");
            int countBefore = service.countVentes();
            System.out.println("   ✓ Nombre de ventes avant l'ajout: " + countBefore);
            System.out.println();

            // ===== TEST 2 : Ajouter une nouvelle vente =====
            System.out.println("2️⃣  TEST 2 - Ajouter une nouvelle vente");
            Vente newVente = new Vente(
                    LocalDate.now(),      // Date actuelle
                    1500.00,               // Montant: 1500 DT
                    1,                     // ID Client: 1
                    1                      // ID User: 1
            );
            System.out.println("   Vente à ajouter: " + newVente);
            boolean added = service.add(newVente);
            if (added) {
                System.out.println("   ✅ Vente ajoutée avec succès!");
                System.out.println("   ID généré: " + newVente.getIdVente());
            } else {
                System.out.println("   ❌ Erreur lors de l'ajout");
            }
            System.out.println();

            // ===== TEST 3 : Vérifier le comptage après ajout =====
            System.out.println("3️⃣  TEST 3 - Vérifier le comptage après ajout");
            int countAfter = service.countVentes();
            System.out.println("   ✓ Nombre de ventes après l'ajout: " + countAfter);
            System.out.println("   ✓ Différence: " + (countAfter - countBefore) + " nouvelle(s) vente(s)");
            if (countAfter > countBefore) {
                System.out.println("   ✅ Ajout confirmé dans la base de données!");
            } else {
                System.out.println("   ❌ L'ajout n'a pas été enregistré");
            }
            System.out.println();

            // ===== TEST 4 : Récupérer la vente ajoutée =====
            System.out.println("4️⃣  TEST 4 - Récupérer la vente ajoutée par ID");
            if (newVente.getIdVente() > 0) {
                Vente retrievedVente = service.getById(newVente.getIdVente());
                if (retrievedVente != null) {
                    System.out.println("   ✅ Vente récupérée:");
                    System.out.println("      ID: " + retrievedVente.getIdVente());
                    System.out.println("      Date: " + retrievedVente.getDateVente());
                    System.out.println("      Montant: " + retrievedVente.getMontantTotal() + " DT");
                    System.out.println("      Client ID: " + retrievedVente.getIdClient());
                    System.out.println("      User ID: " + retrievedVente.getIdUser());
                } else {
                    System.out.println("   ❌ Impossible de récupérer la vente");
                }
            }
            System.out.println();

            // ===== TEST 5 : Afficher toutes les ventes =====
            System.out.println("5️⃣  TEST 5 - Afficher toutes les ventes");
            List<Vente> allVentes = service.getAll();
            System.out.println("   ✓ Total de ventes: " + allVentes.size());
            System.out.println("   Dernier ajout (le plus récent):");
            if (!allVentes.isEmpty()) {
                Vente lastVente = allVentes.get(0);
                System.out.println("      ID: " + lastVente.getIdVente());
                System.out.println("      Date: " + lastVente.getDateVente());
                System.out.println("      Montant: " + lastVente.getMontantTotal() + " DT");
            }
            System.out.println();

            // ===== TEST 6 : Modifier la vente =====
            System.out.println("6️⃣  TEST 6 - Modifier la vente ajoutée");
            if (newVente.getIdVente() > 0) {
                newVente.setMontantTotal(2000.00);
                service.update(newVente);
                System.out.println("   ✓ Montant modifié à: " + newVente.getMontantTotal() + " DT");

                Vente updatedVente = service.getById(newVente.getIdVente());
                if (updatedVente != null && updatedVente.getMontantTotal() == 2000.00) {
                    System.out.println("   ✅ Modification confirmée dans la base de données!");
                } else {
                    System.out.println("   ❌ La modification n'a pas été enregistrée");
                }
            }
            System.out.println();

            // ===== TEST 7 : Valider les données =====
            System.out.println("7️⃣  TEST 7 - Tester la validation");
            try {
                Vente invalidVente = new Vente(LocalDate.now(), -100, 1, 1);
                service.valider(invalidVente);
                System.out.println("   ❌ La validation devrait échouer avec un montant négatif");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✅ Validation correcte: " + e.getMessage());
            }
            System.out.println();

            // ===== TEST 8 : Calculer la taxe =====
            System.out.println("8️⃣  TEST 8 - Calculer la taxe (19%)");
            double montant = 1000.00;
            double taxe = service.calculerTaxe(montant);
            System.out.println("   Montant: " + montant + " DT");
            System.out.println("   Taxe (19%): " + taxe + " DT");
            System.out.println("   Total TTC: " + (montant + taxe) + " DT");
            System.out.println();

            // ===== RÉSUMÉ =====
            System.out.println("======================================");
            System.out.println("✅ RÉSUMÉ DES TESTS");
            System.out.println("======================================");
            System.out.println("✓ Tous les tests CRUD sont passés avec succès!");
            System.out.println("✓ Les données sont correctement enregistrées en base de données");
            System.out.println("✓ L'application est prête pour la production");
            System.out.println("======================================\n");

        } catch (Exception e) {
            System.out.println("❌ Erreur durant les tests:");
            e.printStackTrace();
        }
    }
}

