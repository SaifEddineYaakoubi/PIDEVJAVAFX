package org.example.pidev.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire de hachage de mots de passe avec BCrypt.
 * Utilisé pour stocker et vérifier les mots de passe de manière sécurisée.
 */
public class PasswordUtils {

    // Coût BCrypt (12 = bon compromis sécurité/performance)
    private static final int BCRYPT_COST = 12;
    private static final String BCRYPT_REGEX = "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$";

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
        // Hash BCrypt valide → vérification sécurisée
        if (isValidBcryptHash(hashedPassword)) {
            try {
                return BCrypt.checkpw(plainPassword, normalizeBcryptHash(hashedPassword));
            } catch (Exception e) {
                System.err.println("❌ Erreur vérification BCrypt: " + e.getMessage());
                return false;
            }
        }

        // Chaîne qui ressemble à BCrypt mais est mal formée → échec fermé
        if (looksLikeBcryptHash(hashedPassword)) {
            System.err.println("⚠️ Mot de passe stocké au format BCrypt invalide ou corrompu");
            return false;
        }

        // Ancien mot de passe en clair → comparaison directe pour rétro-compatibilité
        return plainPassword.equals(hashedPassword);
    }

    /**
     * Vérifie si le mot de passe est un hash BCrypt syntaxiquement valide.
     */
    public static boolean isHashed(String password) {
        return isValidBcryptHash(password);
    }

    private static boolean isValidBcryptHash(String password) {
        return password != null && password.matches(BCRYPT_REGEX);
    }

    private static boolean looksLikeBcryptHash(String password) {
        return password != null && password.startsWith("$2");
    }

    private static String normalizeBcryptHash(String hash) {
        if (hash.startsWith("$2y$") || hash.startsWith("$2b$")) {
            return "$2a$" + hash.substring(4);
        }
        return hash;
    }
}
