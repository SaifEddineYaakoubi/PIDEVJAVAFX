package org.example.pidev.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service d'historique des actions (Singleton).
 * Enregistre toutes les actions CRUD effectuées par l'utilisateur.
 */
public class ActionHistoryService {

    private static ActionHistoryService instance;
    private final ObservableList<String> history = FXCollections.observableArrayList();
    private static final int MAX_HISTORY = 50;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ActionHistoryService() {}

    public static ActionHistoryService getInstance() {
        if (instance == null) {
            instance = new ActionHistoryService();
        }
        return instance;
    }

    /**
     * Enregistre une action dans l'historique.
     */
    public void log(String action) {
        String entry = "🕐 " + LocalDateTime.now().format(FORMATTER) + " — " + action;
        history.add(0, entry); // Ajouter en haut (plus récent en premier)
        if (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }
    }

    /**
     * Enregistre un ajout.
     */
    public void logAdd(String entityType, String entityName) {
        log("➕ Ajout " + entityType + ": " + entityName);
    }

    /**
     * Enregistre une modification.
     */
    public void logUpdate(String entityType, String entityName) {
        log("✏️ Modification " + entityType + ": " + entityName);
    }

    /**
     * Enregistre une suppression.
     */
    public void logDelete(String entityType, String entityName) {
        log("🗑️ Suppression " + entityType + ": " + entityName);
    }

    /**
     * Enregistre un export.
     */
    public void logExport(String format, String fileName) {
        log("📄 Export " + format + ": " + fileName);
    }

    /**
     * Retourne la liste observable de l'historique.
     */
    public ObservableList<String> getHistory() {
        return history;
    }

    /**
     * Efface l'historique.
     */
    public void clear() {
        history.clear();
    }
}

