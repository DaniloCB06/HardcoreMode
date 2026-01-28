package com.example.plugin;

/**
 * Categoria especializada de criatura/mob no HardcoreMode.
 *
 * A lista foi extraída de {@code Criaturas_classificadas.txt} e usada pelo
 * {@link MobCategoryResolver} para mapear IDs (ex.: {@code Dragon_Fire}).
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
