package org.example.pidev.test;

import java.time.LocalDate;

import org.example.pidev.controllers.StockController;
import org.example.pidev.models.Stock;

public class StockControllerTest {
    public void runAllTests() {
        System.out.println("[StockControllerTest] Début des tests...");
        testAddStock();
        testUpdateStock();
        testDeleteStock();
        System.out.println("[StockControllerTest] Fin des tests.\n");
    }

    private void testAddStock() {
        StockController controller = new StockController();
        Stock s = new Stock(10.0, LocalDate.now(), LocalDate.now().plusDays(30), 1);
        boolean result = controller.ajouterStock(s);
        System.out.println("testAddStock: " + (result ? "OK" : "FAIL"));
    }

    private void testUpdateStock() {
        StockController controller = new StockController();
        Stock s = new Stock(10.0, LocalDate.now(), LocalDate.now().plusDays(30), 1);
        controller.ajouterStock(s);
        s.setQuantite(20.0);
        controller.modifierStock(s);
        System.out.println("testUpdateStock: OK");
    }

    private void testDeleteStock() {
        StockController controller = new StockController();
        Stock s = new Stock(10.0, LocalDate.now(), LocalDate.now().plusDays(30), 1);
        controller.ajouterStock(s);
        boolean result = controller.supprimerStock(s.getIdStock());
        System.out.println("testDeleteStock: " + (result ? "OK" : "FAIL"));
    }

    public static void main(String[] args) {
        new StockControllerTest().runAllTests();
    }
}
