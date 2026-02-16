package org.example.pidev.controllers;

import java.util.List;

import org.example.pidev.models.Produit;
import org.example.pidev.services.ProduitService;

public class ProduitController {
    private final ProduitService produitService = new ProduitService();

    public boolean ajouterProduit(Produit produit) {
        return produitService.add(produit);
    }

    public void modifierProduit(Produit produit) {
        produitService.update(produit);
    }

    public boolean supprimerProduit(int id) {
        return produitService.delete(id);
    }

    public Produit getProduitById(int id) {
        return produitService.getById(id);
    }

    public List<Produit> getAllProduits() {
        return produitService.getAll();
    }
}
