package org.example.pidev.test;

import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

public class ProduitServiceTest {

    public void runAllTests() {
        System.out.println("[ProduitServiceTest] Début des tests...");
        testAddProduit();
        testUpdateProduit();
        testDeleteProduit();
        testGetAllProduits();
        testGetProduitById();
        System.out.println("[ProduitServiceTest] Fin des tests.\n");
    }

    private void testAddProduit() {
        ProduitService service = new ProduitService();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        boolean result = service.add(p);
        System.out.println("testAddProduit: " + (result ? "OK" : "FAIL"));
    }

    private void testUpdateProduit() {
        ProduitService service = new ProduitService();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        service.add(p);
        p.setNom("ProduitModifié");
        service.update(p);
        System.out.println("testUpdateProduit: OK");
    }

    private void testDeleteProduit() {
        ProduitService service = new ProduitService();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        service.add(p);
        boolean result = service.delete(p.getIdProduit());
        System.out.println("testDeleteProduit: " + (result ? "OK" : "FAIL"));
    }

    private void testGetAllProduits() {
        ProduitService service = new ProduitService();
        System.out.println("testGetAllProduits: " + (service.getAll() != null ? "OK" : "FAIL"));
    }

    private void testGetProduitById() {
        ProduitService service = new ProduitService();
        Produit p = new Produit("TestProduit", "TypeA", "KG", 10.0);
        service.add(p);
        Produit found = service.getById(p.getIdProduit());
        System.out.println("testGetProduitById: " + (found != null ? "OK" : "FAIL"));
    }

    public static void main(String[] args) {
        new ProduitServiceTest().runAllTests();
    }
}
