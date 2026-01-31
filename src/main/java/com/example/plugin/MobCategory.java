package com.example.plugin;

/**
 * Specialized creature/mob category in HardcoreMode.
 *
 * The list was extracted from {@code Criaturas_classificadas.txt} and is used by
 * {@link MobCategoryResolver} to map IDs (e.g., {@code Dragon_Fire}).
 */
public enum MobCategory {
    PASSIVE,
    CRITTER,
    HOSTILE,
    ELITE,
    MINIBOSS,
    WORLDBOSS,
    NONE;

    public static MobCategory fromFileKey(String key) {
        if (key == null) {
            return NONE;
        }
        switch (key.toLowerCase()) {
            case "passive":
                return PASSIVE;
            case "critter":
                return CRITTER;
            case "hostile":
                return HOSTILE;
            case "elite":
                return ELITE;
            case "miniboss":
                return MINIBOSS;
            case "worldboss":
                return WORLDBOSS;
            default:
                return NONE;
        }
    }
}
