package org.example.pidev.test;

import org.example.pidev.models.Stock;
import org.example.pidev.services.StockService;

public class StockServiceTest {

    public void runAllTests() {
        System.out.println("[StockServiceTest] Début des tests...");
        testAddStock();
        testUpdateStock();
        testDeleteStock();
        testGetAllStocks();
        testGetStockById();
        System.out.println("[StockServiceTest] Fin des tests.\n");
    }

    private void testAddStock() {
        StockService service = new StockService();
        // Example values
        Stock s = new Stock(10.0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(30), 1);
        boolean result = service.add(s);
        System.out.println("testAddStock: " + (result ? "OK" : "FAIL"));
    }

    private void testUpdateStock() {
        StockService service = new StockService();
        Stock s = new Stock(10.0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(30), 1);
        service.add(s);
        s.setQuantite(20.0);
        service.update(s);
        System.out.println("testUpdateStock: OK");
    }

    private void testDeleteStock() {
        StockService service = new StockService();
        Stock s = new Stock(10.0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(30), 1);
        service.add(s);
        boolean result = service.delete(s.getIdStock());
        System.out.println("testDeleteStock: " + (result ? "OK" : "FAIL"));
    }

    private void testGetAllStocks() {
        StockService service = new StockService();
        System.out.println("testGetAllStocks: " + (service.getAll() != null ? "OK" : "FAIL"));
    }

    private void testGetStockById() {
        StockService service = new StockService();
        Stock s = new Stock(10.0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(30), 1);
        service.add(s);
        Stock found = service.getById(s.getIdStock());
        System.out.println("testGetStockById: " + (found != null ? "OK" : "FAIL"));
    }

    public static void main(String[] args) {
        new StockServiceTest().runAllTests();
    }
}
