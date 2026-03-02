package org.example.pidev.utils;

import org.example.pidev.models.Utilisateur;
import org.example.pidev.models.Role;

/**
 * Petite classe utilitaire pour stocker l'utilisateur courant en mémoire pendant la session.
 */
public class Session {
    private static Utilisateur currentUser;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }

    /**
     * Retourne l'ID du propriétaire des données (l'agriculteur).
     * - AGRICULTEUR → son propre idUser
     * - RESPONSABLE_STOCK → l'idAgriculteur auquel il est rattaché
     * - ADMIN → 0 (voit tout)
     */
    public static int getOwnerUserId() {
        if (currentUser == null) return 0;
        return currentUser.getOwnerUserId();
    }

    /**
     * Retourne true si l'utilisateur courant est ADMIN (voit tout).
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}

