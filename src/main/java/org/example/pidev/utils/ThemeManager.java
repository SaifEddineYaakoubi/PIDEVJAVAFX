package org.example.pidev.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestionnaire de thème (Singleton).
 * Permet de basculer entre le mode clair et le mode sombre.
 * Gère aussi le changement dynamique des inline styles.
 */
public class ThemeManager {

    private static ThemeManager instance;
    private final BooleanProperty darkMode = new SimpleBooleanProperty(false);

    // Couleurs du mode sombre
    public static final String DARK_BG = "#1a1a2e";
    public static final String DARK_CARD = "#16213e";
    public static final String DARK_SIDEBAR = "linear-gradient(to bottom, #0d1b2a, #1b2838)";
    public static final String DARK_TEXT = "#e0e0e0";
    public static final String DARK_TEXT_MUTED = "#9ca3af";
    public static final String DARK_BORDER = "#0f3460";
    public static final String DARK_INPUT_BG = "#1e293b";
    public static final String DARK_CARD_BG = "#16213e";
    public static final String DARK_HOVER = "#1e3a5f";

    // Couleurs du mode clair (pour restaurer)
    public static final String LIGHT_BG_PARCELLE = "#f0f2f5";
    public static final String LIGHT_BG_CULTURE = "#faf5f0";

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
     * Change aussi les inline styles des containers principaux.
     */
    public void applyTheme(Parent root) {
        if (root == null) return;
        if (darkMode.get()) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
            applyDarkInlineStyles(root);
        } else {
            root.getStyleClass().remove("dark-mode");
            applyLightInlineStyles(root);
        }
    }

    /**
     * Bascule le thème et l'applique immédiatement.
     */
    public void toggleAndApply(Parent root) {
        toggleDarkMode();
        applyTheme(root);
    }

    // Remember original root background for restore
    private String originalRootStyle = null;

    /**
     * Parcourt récursivement l'arbre des nœuds et applique les styles sombres.
     */
    private void applyDarkInlineStyles(Parent root) {
        // Sauvegarder et remplacer le style du root (AnchorPane)
        if (root.getStyle() != null) {
            String style = root.getStyle();
            if (originalRootStyle == null && !style.contains(DARK_BG)) {
                originalRootStyle = style;
            }
            if (style.contains("-fx-background-color: #f0f2f5") || style.contains("-fx-background-color: #faf5f0")) {
                root.setStyle(style.replace("#f0f2f5", DARK_BG).replace("#faf5f0", DARK_BG));
            }
        }
        applyDarkRecursive(root);
    }

    /**
     * Restaure les styles clairs.
     */
    private void applyLightInlineStyles(Parent root) {
        if (originalRootStyle != null) {
            root.setStyle(originalRootStyle);
            originalRootStyle = null;
        } else if (root.getStyle() != null) {
            String style = root.getStyle();
            if (style.contains("-fx-background-color: " + DARK_BG)) {
                root.setStyle(style.replace(DARK_BG, LIGHT_BG_PARCELLE));
            }
        }
        applyLightRecursive(root);
    }

    private void applyDarkRecursive(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            applyDarkToNode(node);
            if (node instanceof Parent) {
                applyDarkRecursive((Parent) node);
            }
        }
    }

    private void applyLightRecursive(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            applyLightToNode(node);
            if (node instanceof Parent) {
                applyLightRecursive((Parent) node);
            }
        }
    }

    private void applyDarkToNode(Node node) {
        if (node == null || node.getStyle() == null || node.getStyle().isEmpty()) return;
        String style = node.getStyle();

        // Skip sidebar elements (already dark-themed with gradients)
        if (style.contains("linear-gradient(to bottom, #1a472a") ||
                style.contains("linear-gradient(to bottom, #78350f") ||
                style.contains("linear-gradient(to bottom, #0d2818") ||
                style.contains("linear-gradient(to bottom, #451a03") ||
                style.contains("rgba(0,0,0,0.2)")) {
            return;
        }

        // Skip small sidebar elements with translucent white backgrounds
        if (style.contains("-fx-background-color: rgba(255,255,255,0.") &&
                !style.contains("-fx-background-color: white")) {
            return;
        }

        // Skip already-dark gradient buttons (nav buttons, action buttons)
        if (style.contains("linear-gradient(to right, #1a472a") ||
                style.contains("linear-gradient(to right, #78350f") ||
                style.contains("linear-gradient(to right, #667eea") ||
                style.contains("linear-gradient(to right, #f59e0b") ||
                style.contains("linear-gradient(to right, #ef4444") ||
                style.contains("linear-gradient(to right, #6366f1") ||
                style.contains("linear-gradient(to right, #8b5cf6") ||
                style.contains("linear-gradient(to right, #11998e") ||
                style.contains("linear-gradient(to right, #bdc3c7") ||
                style.contains("linear-gradient(to bottom right, #667eea")) {
            return;
        }

        // Main content white cards
        if (style.contains("-fx-background-color: white") && !style.contains("rgba")) {
            style = style.replace("-fx-background-color: white", "-fx-background-color: " + DARK_CARD);
            node.setStyle(style);
            node.getStyleClass().add("dark-card");
        }

        // Re-read style after potential changes
        style = node.getStyle();

        // Light gray backgrounds (search bars, filter areas)
        if (style.contains("-fx-background-color: #f3f4f6")) {
            style = style.replace("-fx-background-color: #f3f4f6", "-fx-background-color: " + DARK_INPUT_BG);
            style = style.replace("-fx-text-fill: #374151", "-fx-text-fill: " + DARK_TEXT);
            node.setStyle(style);
        }
        if (style.contains("-fx-background-color: #fef3c7")) {
            style = style.replace("-fx-background-color: #fef3c7", "-fx-background-color: " + DARK_INPUT_BG);
            style = style.replace("-fx-text-fill: #92400e", "-fx-text-fill: " + DARK_TEXT);
            node.setStyle(style);
        }
        if (style.contains("-fx-background-color: #f0fdf4")) {
            node.setStyle(style.replace("-fx-background-color: #f0fdf4", "-fx-background-color: " + DARK_INPUT_BG));
        }
        if (style.contains("-fx-background-color: #ebf8ff")) {
            node.setStyle(style.replace("-fx-background-color: #ebf8ff", "-fx-background-color: " + DARK_INPUT_BG));
        }
        if (style.contains("-fx-background-color: #f7fafc")) {
            node.setStyle(style.replace("-fx-background-color: #f7fafc", "-fx-background-color: " + DARK_INPUT_BG));
        }

        // Re-read style
        style = node.getStyle();

        // Title labels and text colors
        if (node instanceof Label) {
            if (style.contains("-fx-text-fill: #1a472a")) {
                node.setStyle(style.replace("-fx-text-fill: #1a472a", "-fx-text-fill: #4ade80"));
            } else if (style.contains("-fx-text-fill: #2E7D32")) {
                node.setStyle(style.replace("-fx-text-fill: #2E7D32", "-fx-text-fill: #4ade80"));
            } else if (style.contains("-fx-text-fill: #78350f")) {
                node.setStyle(style.replace("-fx-text-fill: #78350f", "-fx-text-fill: #fbbf24"));
            } else if (style.contains("-fx-text-fill: #6b7280")) {
                node.setStyle(style.replace("-fx-text-fill: #6b7280", "-fx-text-fill: " + DARK_TEXT_MUTED));
            } else if (style.contains("-fx-text-fill: #9ca3af")) {
                node.setStyle(style.replace("-fx-text-fill: #9ca3af", "-fx-text-fill: #6b7280"));
            } else if (style.contains("-fx-text-fill: #2c3e50")) {
                node.setStyle(style.replace("-fx-text-fill: #2c3e50", "-fx-text-fill: " + DARK_TEXT));
            } else if (style.contains("-fx-text-fill: #166534")) {
                node.setStyle(style.replace("-fx-text-fill: #166534", "-fx-text-fill: #4ade80"));
            } else if (style.contains("-fx-text-fill: #2b6cb0")) {
                node.setStyle(style.replace("-fx-text-fill: #2b6cb0", "-fx-text-fill: #60a5fa"));
            } else if (style.contains("-fx-text-fill: #495057")) {
                node.setStyle(style.replace("-fx-text-fill: #495057", "-fx-text-fill: " + DARK_TEXT));
            } else if (style.contains("-fx-text-fill: #374151")) {
                node.setStyle(style.replace("-fx-text-fill: #374151", "-fx-text-fill: " + DARK_TEXT));
            } else if (style.contains("-fx-text-fill: #4a5568")) {
                node.setStyle(style.replace("-fx-text-fill: #4a5568", "-fx-text-fill: " + DARK_TEXT_MUTED));
            } else if (style.contains("-fx-text-fill: #2d3748")) {
                node.setStyle(style.replace("-fx-text-fill: #2d3748", "-fx-text-fill: " + DARK_TEXT));
            } else if (style.contains("-fx-text-fill: #718096")) {
                node.setStyle(style.replace("-fx-text-fill: #718096", "-fx-text-fill: " + DARK_TEXT_MUTED));
            }
        }

        // TextField transparent background (inside search bars)
        if (node instanceof TextField) {
            style = node.getStyle();
            if (style.contains("-fx-background-color: transparent")) {
                node.setStyle(style + " -fx-text-fill: " + DARK_TEXT + "; -fx-prompt-text-fill: #6b7280;");
            }
        }

        // Re-read for border changes
        style = node.getStyle();

        // Border colors on cards / lists
        if (style.contains("-fx-border-color: #e5e7eb")) {
            node.setStyle(style.replace("-fx-border-color: #e5e7eb", "-fx-border-color: " + DARK_BORDER));
        } else if (style.contains("-fx-border-color: #fde68a")) {
            node.setStyle(style.replace("-fx-border-color: #fde68a", "-fx-border-color: " + DARK_BORDER));
        } else if (style.contains("-fx-border-color: #e0e0e0")) {
            node.setStyle(style.replace("-fx-border-color: #e0e0e0", "-fx-border-color: " + DARK_BORDER));
        } else if (style.contains("-fx-border-color: #e2e8f0")) {
            node.setStyle(style.replace("-fx-border-color: #e2e8f0", "-fx-border-color: " + DARK_BORDER));
        } else if (style.contains("-fx-border-color: #dee2e6")) {
            node.setStyle(style.replace("-fx-border-color: #dee2e6", "-fx-border-color: " + DARK_BORDER));
        }
    }

    private void applyLightToNode(Node node) {
        if (node == null || node.getStyle() == null) return;
        String style = node.getStyle();

        // Skip sidebar
        if (style.contains("linear-gradient(to bottom, #1a472a") ||
                style.contains("linear-gradient(to bottom, #78350f") ||
                style.contains("linear-gradient(to bottom, #0d2818") ||
                style.contains("linear-gradient(to bottom, #451a03") ||
                style.contains("rgba(255,255,255,0.") ||
                style.contains("rgba(0,0,0,0.2)")) {
            return;
        }

        // Restore white cards
        if (style.contains("-fx-background-color: " + DARK_CARD)) {
            node.setStyle(style.replace("-fx-background-color: " + DARK_CARD, "-fx-background-color: white"));
            node.getStyleClass().remove("dark-card");
        }

        // Restore input backgrounds
        if (style.contains("-fx-background-color: " + DARK_INPUT_BG)) {
            // Check what was the original - try to guess from context
            if (node instanceof HBox || node instanceof VBox) {
                node.setStyle(style.replace("-fx-background-color: " + DARK_INPUT_BG, "-fx-background-color: #f3f4f6")
                        .replace("-fx-text-fill: " + DARK_TEXT, "-fx-text-fill: #374151"));
            }
        }

        // Restore label colors
        if (node instanceof Label) {
            if (style.contains("-fx-text-fill: #4ade80") && !style.contains("rgba")) {
                String currentStyle = node.getStyle();
                if (currentStyle.contains("-fx-font-size: 26px") || currentStyle.contains("-fx-font-size: 28px")) {
                    // Restore to original green title — use #2E7D32 for recolte, #1a472a for parcelle
                    node.setStyle(currentStyle.replace("-fx-text-fill: #4ade80", "-fx-text-fill: #2E7D32"));
                }
            }
            if (style.contains("-fx-text-fill: #fbbf24") && (style.contains("-fx-font-size: 28px") || style.contains("-fx-font-size: 26px"))) {
                node.setStyle(style.replace("-fx-text-fill: #fbbf24", "-fx-text-fill: #78350f"));
            }
            if (style.contains("-fx-text-fill: " + DARK_TEXT_MUTED)) {
                node.setStyle(style.replace("-fx-text-fill: " + DARK_TEXT_MUTED, "-fx-text-fill: #6b7280"));
            }
            if (style.contains("-fx-text-fill: " + DARK_TEXT)) {
                node.setStyle(style.replace("-fx-text-fill: " + DARK_TEXT, "-fx-text-fill: #374151"));
            }
        }

        // Restore border colors
        if (style.contains("-fx-border-color: " + DARK_BORDER)) {
            node.setStyle(node.getStyle().replace("-fx-border-color: " + DARK_BORDER, "-fx-border-color: #e5e7eb"));
        }
    }

    /**
     * Returns the appropriate cell container style for list items based on dark mode.
     */
    public String getCellCardStyle() {
        if (darkMode.get()) {
            return "-fx-padding: 15; -fx-background-color: " + DARK_CARD + "; -fx-background-radius: 10;";
        }
        return "-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;";
    }

    /**
     * Returns appropriate text color for primary labels.
     */
    public String getPrimaryTextFill() {
        return darkMode.get() ? DARK_TEXT : "#374151";
    }

    /**
     * Returns appropriate text color for muted labels.
     */
    public String getMutedTextFill() {
        return darkMode.get() ? DARK_TEXT_MUTED : "#9ca3af";
    }

    /**
     * Returns appropriate text color for the parcelle title.
     */
    public String getParcelleTitleColor() {
        return darkMode.get() ? "#4ade80" : "#1a472a";
    }

    /**
     * Returns appropriate text color for the culture title.
     */
    public String getCultureTitleColor() {
        return darkMode.get() ? "#fbbf24" : "#78350f";
    }
}

