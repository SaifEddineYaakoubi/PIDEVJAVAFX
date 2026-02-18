package org.example.pidev.utils;

import org.example.pidev.models.Utilisateur;

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
}

