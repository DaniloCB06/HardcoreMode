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
        public static final String KEY_PLAYER_DEATH_SETTINGS_ENABLED = "PlayerDeathSettingsEnabled";
        public static final String KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT = "PlayerItemDurabilityLossPercent";
        public static final String KEY_PLAYER_ITEM_DROP_PERCENT = "PlayerItemDropPercent";

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
        public boolean playerDeathSettingsEnabled = false;
        public int playerItemDurabilityLossPercent = 20;
        public int playerItemDropPercent = 20;

        public HardcoreModeConfig() {
        }
}
