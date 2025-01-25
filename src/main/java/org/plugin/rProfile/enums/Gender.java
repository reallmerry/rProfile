package org.plugin.rProfile.enums;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String genderName;

    Gender(String genderName) {
        this.genderName = genderName;
    }

    public String getGenderName() {
        return genderName;
    }

    public static boolean isValidGender(String gender) {
        for (Gender g : values()) {
            if (g.getGenderName().equalsIgnoreCase(gender)) {
                return true;
            }
        }
        return false;
    }

    public static Gender fromString(String gender) {
        for (Gender g : values()) {
            if (g.getGenderName().equalsIgnoreCase(gender)) {
                return g;
            }
        }
        return null;
    }
}