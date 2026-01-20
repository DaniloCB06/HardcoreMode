package com.example.plugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreModeConfig {
    public static final String KEY_ENABLED = "Enabled";
    public static final String KEY_HEALTH_MULTIPLIER = "HealthMultiplier";
    public static final String KEY_DAMAGE_MULTIPLIER = "DamageMultiplier";
    public static final String KEY_PEACEFUL_ENABLED = "PeacefulEnabled";
    public static final String KEY_PEACEFUL_HEALTH_MULTIPLIER = "PeacefulHealthMultiplier";
    public static final String KEY_PEACEFUL_DAMAGE_MULTIPLIER = "PeacefulDamageMultiplier";
    public static final String KEY_NEUTRAL_ENABLED = "NeutralEnabled";
    public static final String KEY_NEUTRAL_HEALTH_MULTIPLIER = "NeutralHealthMultiplier";
    public static final String KEY_NEUTRAL_DAMAGE_MULTIPLIER = "NeutralDamageMultiplier";
    public static final String KEY_HOSTILE_ENABLED = "HostileEnabled";
    public static final String KEY_HOSTILE_HEALTH_MULTIPLIER = "HostileHealthMultiplier";
    public static final String KEY_HOSTILE_DAMAGE_MULTIPLIER = "HostileDamageMultiplier";
    public static final String KEY_BLOOD_MOON_ENABLED = "BloodMoonEnabled";
    public static final String KEY_BLOOD_MOON_INTERVAL_DAYS = "BloodMoonIntervalDays";
    public static final String KEY_BLOOD_MOON_START_HOUR = "BloodMoonStartHour";
    public static final String KEY_BLOOD_MOON_DURATION_HOURS = "BloodMoonDurationHours";
    public static final String KEY_BLOOD_MOON_HOSTILE_HEALTH_MULTIPLIER = "BloodMoonHostileHealthMultiplier";
    public static final String KEY_BLOOD_MOON_HOSTILE_DAMAGE_MULTIPLIER = "BloodMoonHostileDamageMultiplier";

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
            .append(new KeyedCodec<>(KEY_PEACEFUL_ENABLED, Codec.BOOLEAN),
                    (config, value) -> config.peacefulEnabled = value,
                    config -> config.peacefulEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_PEACEFUL_HEALTH_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.peacefulHealthMultiplier = value,
                    config -> config.peacefulHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_PEACEFUL_DAMAGE_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.peacefulDamageMultiplier = value,
                    config -> config.peacefulDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_ENABLED, Codec.BOOLEAN),
                    (config, value) -> config.neutralEnabled = value,
                    config -> config.neutralEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_HEALTH_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.neutralHealthMultiplier = value,
                    config -> config.neutralHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_DAMAGE_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.neutralDamageMultiplier = value,
                    config -> config.neutralDamageMultiplier)
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
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_HEALTH_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.bloodMoonHostileHealthMultiplier = value,
                    config -> config.bloodMoonHostileHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DAMAGE_MULTIPLIER, Codec.FLOAT),
                    (config, value) -> config.bloodMoonHostileDamageMultiplier = value,
                    config -> config.bloodMoonHostileDamageMultiplier)
            .add()
            .build();

    public boolean enabled = true;
    public float healthMultiplier = 2.0f;
    public float damageMultiplier = 2.0f;
    public boolean peacefulEnabled = true;
    public float peacefulHealthMultiplier = -1.0f;
    public float peacefulDamageMultiplier = -1.0f;
    public boolean neutralEnabled = true;
    public float neutralHealthMultiplier = -1.0f;
    public float neutralDamageMultiplier = -1.0f;
    public boolean hostileEnabled = true;
    public float hostileHealthMultiplier = -1.0f;
    public float hostileDamageMultiplier = -1.0f;
    public boolean bloodMoonEnabled = false;
    public int bloodMoonIntervalDays = 7;
    public int bloodMoonStartHour = 20;
    public int bloodMoonDurationHours = 3;
    public float bloodMoonHostileHealthMultiplier = 4.0f;
    public float bloodMoonHostileDamageMultiplier = 4.0f;

    public HardcoreModeConfig() {
    }
}
