package org.example.pidev.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;

/**
 * Gestionnaire de thème (Singleton).
 * Permet de basculer entre le mode clair et le mode sombre.
 */
public class ThemeManager {

    private static ThemeManager instance;
    private final BooleanProperty darkMode = new SimpleBooleanProperty(false);

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public boolean isDarkMode() {
        return darkMode.get();
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public void setDarkMode(boolean dark) {
        darkMode.set(dark);
    }

    public void toggleDarkMode() {
        darkMode.set(!darkMode.get());
    }

    /**
     * Applique le thème actuel à un nœud racine.
     */
    public void applyTheme(Parent root) {
        if (root == null) return;
        if (darkMode.get()) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
        } else {
            root.getStyleClass().remove("dark-mode");
        }
    }

    /**
     * Bascule le thème et l'applique immédiatement.
     */
    public void toggleAndApply(Parent root) {
        toggleDarkMode();
        applyTheme(root);
    }
}

