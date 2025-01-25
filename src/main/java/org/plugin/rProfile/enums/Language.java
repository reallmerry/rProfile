package org.plugin.rProfile.enums;

public enum Language {
    RUSSIAN("Русский"),
    ENGLISH("English"),
    UKRAINIAN("Українська");

    private final String languageName;

    Language(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }

    public static boolean isValidLanguage(String language) {
        for (Language lang : values()) {
            if (lang.getLanguageName().equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }

    public static Language fromString(String language) {
        for (Language lang : values()) {
            if (lang.getLanguageName().equalsIgnoreCase(language)) {
                return lang;
            }
        }
        return null;
    }
}