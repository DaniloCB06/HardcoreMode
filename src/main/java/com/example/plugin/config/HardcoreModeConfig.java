package com.example.plugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreModeConfig {
        public static final String KEY_ENABLED = "Enabled";
        public static final String KEY_HEALTH_MULTIPLIER = "HealthMultiplier";
        public static final String KEY_DAMAGE_MULTIPLIER = "DamageMultiplier";
        public static final String KEY_PASSIVE_ENABLED = "PassiveEnabled";
        public static final String KEY_PASSIVE_HEALTH_MULTIPLIER = "PassiveHealthMultiplier";
        public static final String KEY_PASSIVE_DAMAGE_MULTIPLIER = "PassiveDamageMultiplier";
        public static final String KEY_CRITTER_ENABLED = "CritterEnabled";
        public static final String KEY_CRITTER_HEALTH_MULTIPLIER = "CritterHealthMultiplier";
        public static final String KEY_CRITTER_DAMAGE_MULTIPLIER = "CritterDamageMultiplier";
        public static final String KEY_HOSTILE_ENABLED = "HostileEnabled";
        public static final String KEY_HOSTILE_HEALTH_MULTIPLIER = "HostileHealthMultiplier";
        public static final String KEY_HOSTILE_DAMAGE_MULTIPLIER = "HostileDamageMultiplier";
        public static final String KEY_ELITE_ENABLED = "EliteEnabled";
        public static final String KEY_ELITE_HEALTH_MULTIPLIER = "EliteHealthMultiplier";
        public static final String KEY_ELITE_DAMAGE_MULTIPLIER = "EliteDamageMultiplier";
        public static final String KEY_MINIBOSS_ENABLED = "MinibossEnabled";
        public static final String KEY_MINIBOSS_HEALTH_MULTIPLIER = "MinibossHealthMultiplier";
        public static final String KEY_MINIBOSS_DAMAGE_MULTIPLIER = "MinibossDamageMultiplier";
        public static final String KEY_WORLDBOSS_ENABLED = "WorldbossEnabled";
        public static final String KEY_WORLDBOSS_HEALTH_MULTIPLIER = "WorldbossHealthMultiplier";
        public static final String KEY_WORLDBOSS_DAMAGE_MULTIPLIER = "WorldbossDamageMultiplier";
        public static final String KEY_BLOOD_MOON_ENABLED = "BloodMoonEnabled";
        public static final String KEY_BLOOD_MOON_INTERVAL_DAYS = "BloodMoonIntervalDays";
        public static final String KEY_BLOOD_MOON_START_HOUR = "BloodMoonStartHour";
        public static final String KEY_BLOOD_MOON_DURATION_HOURS = "BloodMoonDurationHours";
        public static final String KEY_BLOOD_MOON_HOSTILE_HEALTH_MULTIPLIER = "BloodMoonHostileHealthMultiplier";
        public static final String KEY_BLOOD_MOON_HOSTILE_DAMAGE_MULTIPLIER = "BloodMoonHostileDamageMultiplier";
        public static final String KEY_BLOOD_MOON_XP_MULTIPLIER = "BloodMoonXpMultiplier";
        public static final String KEY_BLOOD_MOON_XP_MULTIPLIER_ENABLED = "BloodMoonXpMultiplierEnabled";
        public static final String KEY_BLOOD_MOON_HOSTILE_ENABLED = "BloodMoonHostileEnabled";
        public static final String KEY_BLOOD_MOON_ELITE_ENABLED = "BloodMoonEliteEnabled";
        public static final String KEY_BLOOD_MOON_ELITE_HEALTH_MULTIPLIER = "BloodMoonEliteHealthMultiplier";
        public static final String KEY_BLOOD_MOON_ELITE_DAMAGE_MULTIPLIER = "BloodMoonEliteDamageMultiplier";
        public static final String KEY_BLOOD_MOON_MINIBOSS_ENABLED = "BloodMoonMinibossEnabled";
        public static final String KEY_BLOOD_MOON_MINIBOSS_HEALTH_MULTIPLIER = "BloodMoonMinibossHealthMultiplier";
        public static final String KEY_BLOOD_MOON_MINIBOSS_DAMAGE_MULTIPLIER = "BloodMoonMinibossDamageMultiplier";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_ENABLED = "BloodMoonWorldbossEnabled";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_HEALTH_MULTIPLIER = "BloodMoonWorldbossHealthMultiplier";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_DAMAGE_MULTIPLIER = "BloodMoonWorldbossDamageMultiplier";
        // Blood Moon Drop System Keys
        public static final String KEY_BLOOD_MOON_DROPS_ENABLED = "BloodMoonDropsEnabled";
        public static final String KEY_BLOOD_MOON_HOSTILE_DROP_ENABLED = "BloodMoonHostileDropEnabled";
        public static final String KEY_BLOOD_MOON_HOSTILE_DROP_ITEM = "BloodMoonHostileDropItem";
        public static final String KEY_BLOOD_MOON_HOSTILE_DROP_QUANTITY = "BloodMoonHostileDropQuantity";
        public static final String KEY_BLOOD_MOON_HOSTILE_DROP_CHANCE = "BloodMoonHostileDropChance";
        public static final String KEY_BLOOD_MOON_ELITE_DROP_ENABLED = "BloodMoonEliteDropEnabled";
        public static final String KEY_BLOOD_MOON_ELITE_DROP_ITEM = "BloodMoonEliteDropItem";
        public static final String KEY_BLOOD_MOON_ELITE_DROP_QUANTITY = "BloodMoonEliteDropQuantity";
        public static final String KEY_BLOOD_MOON_ELITE_DROP_CHANCE = "BloodMoonEliteDropChance";
        public static final String KEY_BLOOD_MOON_MINIBOSS_DROP_ENABLED = "BloodMoonMinibossDropEnabled";
        public static final String KEY_BLOOD_MOON_MINIBOSS_DROP_ITEM = "BloodMoonMinibossDropItem";
        public static final String KEY_BLOOD_MOON_MINIBOSS_DROP_QUANTITY = "BloodMoonMinibossDropQuantity";
        public static final String KEY_BLOOD_MOON_MINIBOSS_DROP_CHANCE = "BloodMoonMinibossDropChance";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_DROP_ENABLED = "BloodMoonWorldbossDropEnabled";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_DROP_ITEM = "BloodMoonWorldbossDropItem";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_DROP_QUANTITY = "BloodMoonWorldbossDropQuantity";
        public static final String KEY_BLOOD_MOON_WORLDBOSS_DROP_CHANCE = "BloodMoonWorldbossDropChance";
        public static final String KEY_PLAYER_DEATH_SETTINGS_ENABLED = "PlayerDeathSettingsEnabled";
        public static final String KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT = "PlayerItemDurabilityLossPercent";
        public static final String KEY_PLAYER_ITEM_DROP_PERCENT = "PlayerItemDropPercent";
        // Blood Moon Death Player Settings Keys
        public static final String KEY_BLOOD_MOON_DEATH_SETTINGS_ENABLED = "BloodMoonDeathSettingsEnabled";
        public static final String KEY_BLOOD_MOON_ITEM_DURABILITY_LOSS_PERCENT = "BloodMoonItemDurabilityLossPercent";
        public static final String KEY_BLOOD_MOON_ITEM_DROP_PERCENT = "BloodMoonItemDropPercent";

        public static final BuilderCodec<HardcoreModeConfig> CODEC = BuilderCodec
                        .builder(HardcoreModeConfig.class, HardcoreModeConfig::new)
                        .append(new KeyedCodec<>(KEY_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.enabled = value,
                                        config -> config.enabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.healthMultiplier = value,
                                        config -> config.healthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.damageMultiplier = value,
                                        config -> config.damageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_PASSIVE_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.passiveEnabled = value,
                                        config -> config.passiveEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_PASSIVE_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.passiveHealthMultiplier = value,
                                        config -> config.passiveHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_PASSIVE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.passiveDamageMultiplier = value,
                                        config -> config.passiveDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_CRITTER_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.critterEnabled = value,
                                        config -> config.critterEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_CRITTER_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.critterHealthMultiplier = value,
                                        config -> config.critterHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_CRITTER_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.critterDamageMultiplier = value,
                                        config -> config.critterDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_HOSTILE_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.hostileEnabled = value,
                                        config -> config.hostileEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_HOSTILE_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.hostileHealthMultiplier = value,
                                        config -> config.hostileHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_HOSTILE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.hostileDamageMultiplier = value,
                                        config -> config.hostileDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_ELITE_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.eliteEnabled = value,
                                        config -> config.eliteEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_ELITE_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.eliteHealthMultiplier = value,
                                        config -> config.eliteHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_ELITE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.eliteDamageMultiplier = value,
                                        config -> config.eliteDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_MINIBOSS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.minibossEnabled = value,
                                        config -> config.minibossEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_MINIBOSS_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.minibossHealthMultiplier = value,
                                        config -> config.minibossHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_MINIBOSS_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.minibossDamageMultiplier = value,
                                        config -> config.minibossDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_WORLDBOSS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.worldbossEnabled = value,
                                        config -> config.worldbossEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_WORLDBOSS_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.worldbossHealthMultiplier = value,
                                        config -> config.worldbossHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_WORLDBOSS_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.worldbossDamageMultiplier = value,
                                        config -> config.worldbossDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonEnabled = value,
                                        config -> config.bloodMoonEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_INTERVAL_DAYS, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonIntervalDays = value,
                                        config -> config.bloodMoonIntervalDays)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_START_HOUR, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonStartHour = value,
                                        config -> config.bloodMoonStartHour)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_HOURS, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonDurationHours = value,
                                        config -> config.bloodMoonDurationHours)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonHostileEnabled = value,
                                        config -> config.bloodMoonHostileEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonHostileHealthMultiplier = value,
                                        config -> config.bloodMoonHostileHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonHostileDamageMultiplier = value,
                                        config -> config.bloodMoonHostileDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonEliteEnabled = value,
                                        config -> config.bloodMoonEliteEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonEliteHealthMultiplier = value,
                                        config -> config.bloodMoonEliteHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonEliteDamageMultiplier = value,
                                        config -> config.bloodMoonEliteDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonMinibossEnabled = value,
                                        config -> config.bloodMoonMinibossEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonMinibossHealthMultiplier = value,
                                        config -> config.bloodMoonMinibossHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonMinibossDamageMultiplier = value,
                                        config -> config.bloodMoonMinibossDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonWorldbossEnabled = value,
                                        config -> config.bloodMoonWorldbossEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_HEALTH_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonWorldbossHealthMultiplier = value,
                                        config -> config.bloodMoonWorldbossHealthMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DAMAGE_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonWorldbossDamageMultiplier = value,
                                        config -> config.bloodMoonWorldbossDamageMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_XP_MULTIPLIER, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonXpMultiplier = value,
                                        config -> config.bloodMoonXpMultiplier)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_XP_MULTIPLIER_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonXpMultiplierEnabled = value,
                                        config -> config.bloodMoonXpMultiplierEnabled)
                        .add()
                        // Blood Moon Drop System Codecs
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_DROPS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonDropsEnabled = value,
                                        config -> config.bloodMoonDropsEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DROP_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonHostileDropEnabled = value,
                                        config -> config.bloodMoonHostileDropEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DROP_ITEM, Codec.STRING),
                                        (config, value) -> config.bloodMoonHostileDropItem = value,
                                        config -> config.bloodMoonHostileDropItem)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DROP_QUANTITY, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonHostileDropQuantity = value,
                                        config -> config.bloodMoonHostileDropQuantity)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DROP_CHANCE, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonHostileDropChance = value,
                                        config -> config.bloodMoonHostileDropChance)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DROP_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonEliteDropEnabled = value,
                                        config -> config.bloodMoonEliteDropEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DROP_ITEM, Codec.STRING),
                                        (config, value) -> config.bloodMoonEliteDropItem = value,
                                        config -> config.bloodMoonEliteDropItem)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DROP_QUANTITY, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonEliteDropQuantity = value,
                                        config -> config.bloodMoonEliteDropQuantity)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DROP_CHANCE, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonEliteDropChance = value,
                                        config -> config.bloodMoonEliteDropChance)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DROP_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonMinibossDropEnabled = value,
                                        config -> config.bloodMoonMinibossDropEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DROP_ITEM, Codec.STRING),
                                        (config, value) -> config.bloodMoonMinibossDropItem = value,
                                        config -> config.bloodMoonMinibossDropItem)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DROP_QUANTITY, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonMinibossDropQuantity = value,
                                        config -> config.bloodMoonMinibossDropQuantity)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DROP_CHANCE, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonMinibossDropChance = value,
                                        config -> config.bloodMoonMinibossDropChance)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DROP_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonWorldbossDropEnabled = value,
                                        config -> config.bloodMoonWorldbossDropEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DROP_ITEM, Codec.STRING),
                                        (config, value) -> config.bloodMoonWorldbossDropItem = value,
                                        config -> config.bloodMoonWorldbossDropItem)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DROP_QUANTITY, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonWorldbossDropQuantity = value,
                                        config -> config.bloodMoonWorldbossDropQuantity)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DROP_CHANCE, Codec.FLOAT),
                                        (config, value) -> config.bloodMoonWorldbossDropChance = value,
                                        config -> config.bloodMoonWorldbossDropChance)
                        .add()
                        .append(new KeyedCodec<>(KEY_PLAYER_DEATH_SETTINGS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.playerDeathSettingsEnabled = value,
                                        config -> config.playerDeathSettingsEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT, Codec.INTEGER),
                                        (config, value) -> config.playerItemDurabilityLossPercent = value,
                                        config -> config.playerItemDurabilityLossPercent)
                        .add()
                        .append(new KeyedCodec<>(KEY_PLAYER_ITEM_DROP_PERCENT, Codec.INTEGER),
                                        (config, value) -> config.playerItemDropPercent = value,
                                        config -> config.playerItemDropPercent)
                        .add()
                        // Blood Moon Death Player Settings Codecs
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_DEATH_SETTINGS_ENABLED, Codec.BOOLEAN),
                                        (config, value) -> config.bloodMoonDeathSettingsEnabled = value,
                                        config -> config.bloodMoonDeathSettingsEnabled)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ITEM_DURABILITY_LOSS_PERCENT, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonItemDurabilityLossPercent = value,
                                        config -> config.bloodMoonItemDurabilityLossPercent)
                        .add()
                        .append(new KeyedCodec<>(KEY_BLOOD_MOON_ITEM_DROP_PERCENT, Codec.INTEGER),
                                        (config, value) -> config.bloodMoonItemDropPercent = value,
                                        config -> config.bloodMoonItemDropPercent)
                        .add()
                        .build();

        public boolean enabled = false;
        public float healthMultiplier = 2.0f;
        public float damageMultiplier = 2.0f;
        public boolean passiveEnabled = false;
        public float passiveHealthMultiplier = 2.0f;
        public float passiveDamageMultiplier = 2.0f;
        public boolean critterEnabled = false;
        public float critterHealthMultiplier = 2.0f;
        public float critterDamageMultiplier = 2.0f;
        public boolean hostileEnabled = false;
        public float hostileHealthMultiplier = 2.0f;
        public float hostileDamageMultiplier = 2.0f;
        public boolean eliteEnabled = false;
        public float eliteHealthMultiplier = 2.0f;
        public float eliteDamageMultiplier = 2.0f;
        public boolean minibossEnabled = false;
        public float minibossHealthMultiplier = 2.0f;
        public float minibossDamageMultiplier = 2.0f;
        public boolean worldbossEnabled = false;
        public float worldbossHealthMultiplier = 2.0f;
        public float worldbossDamageMultiplier = 2.0f;
        public boolean bloodMoonEnabled = false;
        public int bloodMoonIntervalDays = 7;
        public int bloodMoonStartHour = 20;
        public int bloodMoonDurationHours = 3;
        public float bloodMoonHostileHealthMultiplier = 2.0f;
        public float bloodMoonHostileDamageMultiplier = 2.0f;
        public float bloodMoonXpMultiplier = 2.0f;
        public boolean bloodMoonXpMultiplierEnabled = false;
        public boolean bloodMoonHostileEnabled = false;
        public boolean bloodMoonEliteEnabled = false;
        public float bloodMoonEliteHealthMultiplier = 2.0f;
        public float bloodMoonEliteDamageMultiplier = 2.0f;
        public boolean bloodMoonMinibossEnabled = false;
        public float bloodMoonMinibossHealthMultiplier = 2.0f;
        public float bloodMoonMinibossDamageMultiplier = 2.0f;
        public boolean bloodMoonWorldbossEnabled = false;
        public float bloodMoonWorldbossHealthMultiplier = 2.0f;
        public float bloodMoonWorldbossDamageMultiplier = 2.0f;
        // Blood Moon Drop System Fields
        public boolean bloodMoonDropsEnabled = false;
        public boolean bloodMoonHostileDropEnabled = false;
        public String bloodMoonHostileDropItem = "Ingredient_Bar_Iron";
        public int bloodMoonHostileDropQuantity = 1;
        public float bloodMoonHostileDropChance = 100.0f;
        public boolean bloodMoonEliteDropEnabled = false;
        public String bloodMoonEliteDropItem = "Ingredient_Bar_Thorium";
        public int bloodMoonEliteDropQuantity = 1;
        public float bloodMoonEliteDropChance = 100.0f;
        public boolean bloodMoonMinibossDropEnabled = false;
        public String bloodMoonMinibossDropItem = "Ingredient_Bar_Adamantite";
        public int bloodMoonMinibossDropQuantity = 1;
        public float bloodMoonMinibossDropChance = 100.0f;
        public boolean bloodMoonWorldbossDropEnabled = false;
        public String bloodMoonWorldbossDropItem = "Ingredient_Bar_Mithril";
        public int bloodMoonWorldbossDropQuantity = 1;
        public float bloodMoonWorldbossDropChance = 100.0f;
        public boolean playerDeathSettingsEnabled = false;
        public int playerItemDurabilityLossPercent = 20;
        public int playerItemDropPercent = 20;
        // Blood Moon Death Player Settings Fields
        public boolean bloodMoonDeathSettingsEnabled = false;
        public int bloodMoonItemDurabilityLossPercent = 50;
        public int bloodMoonItemDropPercent = 50;

        // World Settings
        // Por padrão, TODOS os mundos iniciam DESABILITADOS.
        // Quando defaultWorldsEnabled == false, usamos a lista de mundos habilitados.
        // Quando defaultWorldsEnabled == true (modo legado), usamos a lista de mundos desabilitados.
        private static final String DISABLED_WORLDS_FILE = "HardcoreModeDisabledWorlds.txt";
        private static final String ENABLED_WORLDS_FILE = "HardcoreModeEnabledWorlds.txt";
        private transient java.util.Set<String> disabledWorldsCache = null;
        private transient java.util.Set<String> enabledWorldsCache = null;
        private transient boolean defaultWorldsEnabled = false;

        public HardcoreModeConfig() {
        }

        /**
         * Verifica se um mundo está habilitado para os efeitos do HardcoreMode.
         * Por padrão, mundos novos ficam desabilitados até serem ativados.
         */
        public boolean isWorldEnabled(String worldName) {
            if (worldName == null || worldName.isEmpty()) {
                return false;
            }
            loadWorldSettingsIfNeeded();
            String key = worldName.toLowerCase();
            if (defaultWorldsEnabled) {
                // Mundo está habilitado se NÃO está na lista de desabilitados
                return !disabledWorldsCache.contains(key);
            }
            // Modo default (novos mundos): habilita somente se estiver na lista
            return enabledWorldsCache.contains(key);
        }

        /**
         * Define se um mundo está habilitado ou desabilitado.
         */
        public void setWorldEnabled(String worldName, boolean enabled) {
            if (worldName == null || worldName.isEmpty()) {
                return;
            }
            loadWorldSettingsIfNeeded();

            String lowerName = worldName.toLowerCase();

            if (defaultWorldsEnabled) {
                if (enabled) {
                    // Remover da lista de desabilitados para habilitar
                    disabledWorldsCache.remove(lowerName);
                } else {
                    // Adicionar à lista de desabilitados para desabilitar
                    disabledWorldsCache.add(lowerName);
                }
            } else {
                if (enabled) {
                    enabledWorldsCache.add(lowerName);
                } else {
                    enabledWorldsCache.remove(lowerName);
                }
            }

            saveWorldSettings();
        }

        /**
         * Obtém a lista de mundos desabilitados (somente no modo legado).
         */
        public java.util.Set<String> getDisabledWorlds() {
            loadWorldSettingsIfNeeded();
            if (defaultWorldsEnabled) {
                return new java.util.HashSet<>(disabledWorldsCache);
            }
            return new java.util.HashSet<>();
        }

        /**
         * Define todos os mundos como habilitados.
         */
        public void enableAllWorlds() {
            loadWorldSettingsIfNeeded();
            defaultWorldsEnabled = true;
            disabledWorldsCache.clear();
            enabledWorldsCache.clear();
            saveWorldSettings();
        }

        private void loadWorldSettingsIfNeeded() {
            if (disabledWorldsCache == null || enabledWorldsCache == null) {
                disabledWorldsCache = new java.util.HashSet<>();
                enabledWorldsCache = new java.util.HashSet<>();
                loadWorldSettings();
            }
        }

        private void loadWorldSettings() {
            try {
                java.nio.file.Path configDir = java.nio.file.Paths.get("config");
                java.nio.file.Path enabledFile = configDir.resolve(ENABLED_WORLDS_FILE);
                java.nio.file.Path disabledFile = configDir.resolve(DISABLED_WORLDS_FILE);

                if (java.nio.file.Files.exists(enabledFile)) {
                    defaultWorldsEnabled = false;
                    String content = java.nio.file.Files.readString(enabledFile).trim();
                    parseWorldList(content, enabledWorldsCache);
                    return;
                }

                if (java.nio.file.Files.exists(disabledFile)) {
                    // Modo legado: mundos habilitados por padrão
                    defaultWorldsEnabled = true;
                    String content = java.nio.file.Files.readString(disabledFile).trim();
                    parseWorldList(content, disabledWorldsCache);
                    return;
                }

                // Sem arquivos: padrão desabilitado para novos mundos
                defaultWorldsEnabled = false;
            } catch (Exception e) {
                // Em caso de erro, manter padrão desabilitado
                defaultWorldsEnabled = false;
            }
        }

        private void parseWorldList(String content, java.util.Set<String> target) {
            target.clear();
            if (content == null || content.isEmpty()) {
                return;
            }

            String[] parts = content.split(",");
            for (String part : parts) {
                String trimmed = part.trim().toLowerCase();
                if (!trimmed.isEmpty()) {
                    target.add(trimmed);
                }
            }
        }

        private void saveWorldSettings() {
            try {
                java.nio.file.Path configDir = java.nio.file.Paths.get("config");
                java.nio.file.Files.createDirectories(configDir);
                java.nio.file.Path enabledFile = configDir.resolve(ENABLED_WORLDS_FILE);
                java.nio.file.Path disabledFile = configDir.resolve(DISABLED_WORLDS_FILE);

                if (defaultWorldsEnabled) {
                    if (disabledWorldsCache.isEmpty()) {
                        java.nio.file.Files.writeString(disabledFile, "");
                    } else {
                        java.nio.file.Files.writeString(disabledFile, String.join(",", disabledWorldsCache));
                    }
                    try {
                        java.nio.file.Files.deleteIfExists(enabledFile);
                    } catch (Exception ignored) {
                    }
                } else {
                    if (enabledWorldsCache.isEmpty()) {
                        java.nio.file.Files.writeString(enabledFile, "");
                    } else {
                        java.nio.file.Files.writeString(enabledFile, String.join(",", enabledWorldsCache));
                    }
                    try {
                        java.nio.file.Files.deleteIfExists(disabledFile);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }

        /**
         * Migra a lista legada de mundos desabilitados para o novo modo default desabilitado.
         * Preserva mundos existentes como habilitados (exceto os que estavam explicitamente desabilitados).
         * @return true se houve migração
         */
        public boolean migrateLegacyWorldSettings(java.util.Set<String> knownWorlds) {
            loadWorldSettingsIfNeeded();
            if (!defaultWorldsEnabled) {
                return false;
            }

            enabledWorldsCache.clear();
            if (knownWorlds != null) {
                for (String worldName : knownWorlds) {
                    if (worldName == null || worldName.isEmpty()) {
                        continue;
                    }
                    String key = worldName.toLowerCase();
                    if (!disabledWorldsCache.contains(key)) {
                        enabledWorldsCache.add(key);
                    }
                }
            }

            defaultWorldsEnabled = false;
            saveWorldSettings();
            return true;
        }
}

