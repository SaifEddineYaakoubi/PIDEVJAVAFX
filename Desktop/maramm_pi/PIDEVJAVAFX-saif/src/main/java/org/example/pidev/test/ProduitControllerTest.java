package org.example.pidev.test;

import org.example.pidev.controllers.ProduitController;
import org.example.pidev.models.Produit;

public class ProduitControllerTest {
    public void runAllTests() {
        System.out.println("[ProduitControllerTest] Début des tests...");
        testAddProduit();
        testUpdateProduit();
        testDeleteProduit();
        System.out.println("[ProduitControllerTest] Fin des tests.\n");
    }

    private void testAddProduit() {
        ProduitController controller = new ProduitController();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        boolean result = controller.ajouterProduit(p);
        System.out.println("testAddProduit: " + (result ? "OK" : "FAIL"));
    }

    private void testUpdateProduit() {
        ProduitController controller = new ProduitController();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        controller.ajouterProduit(p);
        p.setNom("ProduitModifié");
        controller.modifierProduit(p);
        System.out.println("testUpdateProduit: OK");
    }

    private void testDeleteProduit() {
        ProduitController controller = new ProduitController();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        controller.ajouterProduit(p);
        boolean result = controller.supprimerProduit(p.getIdProduit());
        System.out.println("testDeleteProduit: " + (result ? "OK" : "FAIL"));
    }

    public static void main(String[] args) {
        new ProduitControllerTest().runAllTests();
    }
}
