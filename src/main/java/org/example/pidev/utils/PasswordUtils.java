package org.example.pidev.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire de hachage de mots de passe avec BCrypt.
 * Utilisé pour stocker et vérifier les mots de passe de manière sécurisée.
 */
public class PasswordUtils {

    // Coût BCrypt (12 = bon compromis sécurité/performance)
    private static final int BCRYPT_COST = 12;

    /**
     * Hache un mot de passe en clair avec BCrypt.
     * @param plainPassword le mot de passe en clair
     * @return le hash BCrypt
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash BCrypt.
     * @param plainPassword le mot de passe en clair saisi par l'utilisateur
     * @param hashedPassword le hash stocké en base de données
     * @return true si le mot de passe correspond
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        // Si le hash ne commence pas par "$2" c'est un ancien mot de passe en clair
        // → comparaison directe pour rétro-compatibilité
        if (!hashedPassword.startsWith("$2")) {
            return plainPassword.equals(hashedPassword);
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("❌ Erreur vérification BCrypt: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un mot de passe est déjà haché (format BCrypt).
     * @param password le mot de passe à vérifier
     * @return true si c'est déjà un hash BCrypt
     */
    public static boolean isHashed(String password) {
        return password != null && password.startsWith("$2");
    }
}
