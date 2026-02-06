package org.example.pidev.controllers;

import org.example.pidev.models.Role;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.UtilisateurService;

import java.time.LocalDate;

public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController() {
        this.service = new UtilisateurService();
    }

    // Ajouter un utilisateur
    public boolean add(String nom, String prenom, String email,
                                      String motDePasse, Role role, boolean statut) {
        // Créer un objet Utilisateur
        Utilisateur utilisateur = new Utilisateur(
                nom,
                prenom,
                email,
                motDePasse,
                role,
                statut,
                LocalDate.now() // date de création maintenant
        );

        // Vérifier si l'email est valide
        if (!utilisateur.isValidEmail()) {
            System.out.println("❌ Email invalide !");
            return false;
        }

        // Appeler le service pour ajouter l'utilisateur
        boolean success = service.add(utilisateur);
        if (success) {
            System.out.println("✅ Utilisateur ajouté avec succès !");
        } else {
            System.out.println("❌ Échec de l'ajout de l'utilisateur !");
        }
        return success;
    }


    public void update(int idUser, String nom, String prenom, String email,
                                  String motDePasse, Role role, boolean statut) {
        Utilisateur utilisateur = new Utilisateur(
                idUser,
                nom,
                prenom,
                email,
                motDePasse,
                role,
                statut,
                LocalDate.now() // you can keep original date if you want
        );

        service.update(utilisateur);
    }

    public void deleteUtilisateur(int idUser) {
        service.delete(idUser);
    }



}
