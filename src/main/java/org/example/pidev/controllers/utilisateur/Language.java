package org.example.pidev.controllers.utilisateur;

public enum Language {
    ENGLISH("en"),
    FRENCH("fr"),
    ARABIC("ar"),
    DARIJA("ary"),  // Moroccan Arabic
    SPANISH("es");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromString(String text) {
        for (Language lang : Language.values()) {
            if (lang.code.equalsIgnoreCase(text) || lang.name().equalsIgnoreCase(text)) {
                return lang;
            }
        }
        return ENGLISH;
    }
}