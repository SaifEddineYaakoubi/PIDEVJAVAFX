package org.example.pidev.controllers;

import java.util.List;

import org.example.pidev.models.Stock;
import org.example.pidev.services.StockService;

public class StockController {
    private final StockService stockService = new StockService();

    public boolean ajouterStock(Stock stock) {
        return stockService.add(stock);
    }

    public void modifierStock(Stock stock) {
        stockService.update(stock);
    }

    public boolean supprimerStock(int id) {
        return stockService.delete(id);
    }

    public Stock getStockById(int id) {
        return stockService.getById(id);
    }

    public List<Stock> getAllStocks() {
        return stockService.getAll();
    }
}
