package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreSettingsPageEventData {
    public static final String KEY_ENABLED = "@Enabled";
    public static final String KEY_GLOBAL_HEALTH = "@GlobalHealthMultiplier";
    public static final String KEY_GLOBAL_DAMAGE = "@GlobalDamageMultiplier";
    public static final String KEY_PEACEFUL_ENABLED = "@PeacefulEnabled";
    public static final String KEY_PEACEFUL_HEALTH = "@PeacefulHealthMultiplier";
    public static final String KEY_PEACEFUL_DAMAGE = "@PeacefulDamageMultiplier";
    public static final String KEY_NEUTRAL_ENABLED = "@NeutralEnabled";
    public static final String KEY_NEUTRAL_HEALTH = "@NeutralHealthMultiplier";
    public static final String KEY_NEUTRAL_DAMAGE = "@NeutralDamageMultiplier";
    public static final String KEY_HOSTILE_ENABLED = "@HostileEnabled";
    public static final String KEY_HOSTILE_HEALTH = "@HostileHealthMultiplier";
    public static final String KEY_HOSTILE_DAMAGE = "@HostileDamageMultiplier";
    public static final String KEY_BLOOD_MOON_ENABLED = "@BloodMoonEnabled";
    public static final String KEY_BLOOD_MOON_INTERVAL_DAYS = "@BloodMoonIntervalDays";
    public static final String KEY_BLOOD_MOON_START_HOUR = "@BloodMoonStartHour";
    public static final String KEY_BLOOD_MOON_DURATION_1H = "@BloodMoonDuration1h";
    public static final String KEY_BLOOD_MOON_DURATION_3H = "@BloodMoonDuration3h";
    public static final String KEY_BLOOD_MOON_DURATION_6H = "@BloodMoonDuration6h";
    public static final String KEY_BLOOD_MOON_DURATION_9H = "@BloodMoonDuration9h";
    public static final String KEY_BLOOD_MOON_DURATION_12H = "@BloodMoonDuration12h";
    public static final String KEY_BLOOD_MOON_HOSTILE_HEALTH = "@BloodMoonHostileHealthMultiplier";
    public static final String KEY_BLOOD_MOON_HOSTILE_DAMAGE = "@BloodMoonHostileDamageMultiplier";
    public static final String KEY_BLOOD_MOON_FORCE = "@BloodMoonForce";
    public static final String KEY_PLAYER_DEATH_SETTINGS_ENABLED = "@PlayerDeathSettingsEnabled";
    public static final String KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT = "@PlayerItemDurabilityLossPercent";
    public static final String KEY_PLAYER_ITEM_DROP_PERCENT = "@PlayerItemDropPercent";

    public static final BuilderCodec<HardcoreSettingsPageEventData> CODEC = BuilderCodec
            .builder(HardcoreSettingsPageEventData.class, HardcoreSettingsPageEventData::new)
            .append(new KeyedCodec<>(KEY_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.enabled = value,
                    data -> data.enabled)
            .add()
            .append(new KeyedCodec<>(KEY_GLOBAL_HEALTH, Codec.FLOAT),
                    (data, value) -> data.globalHealthMultiplier = value,
                    data -> data.globalHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_GLOBAL_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.globalDamageMultiplier = value,
                    data -> data.globalDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_PEACEFUL_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.peacefulEnabled = value,
                    data -> data.peacefulEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_PEACEFUL_HEALTH, Codec.FLOAT),
                    (data, value) -> data.peacefulHealthMultiplier = value,
                    data -> data.peacefulHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_PEACEFUL_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.peacefulDamageMultiplier = value,
                    data -> data.peacefulDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.neutralEnabled = value,
                    data -> data.neutralEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_HEALTH, Codec.FLOAT),
                    (data, value) -> data.neutralHealthMultiplier = value,
                    data -> data.neutralHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_NEUTRAL_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.neutralDamageMultiplier = value,
                    data -> data.neutralDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_HOSTILE_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.hostileEnabled = value,
                    data -> data.hostileEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_HOSTILE_HEALTH, Codec.FLOAT),
                    (data, value) -> data.hostileHealthMultiplier = value,
                    data -> data.hostileHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_HOSTILE_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.hostileDamageMultiplier = value,
                    data -> data.hostileDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonEnabled = value,
                    data -> data.bloodMoonEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_INTERVAL_DAYS, Codec.FLOAT),
                    (data, value) -> data.bloodMoonIntervalDays = value,
                    data -> data.bloodMoonIntervalDays)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_START_HOUR, Codec.FLOAT),
                    (data, value) -> data.bloodMoonStartHour = value,
                    data -> data.bloodMoonStartHour)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_1H, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonDuration1h = value,
                    data -> data.bloodMoonDuration1h)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_3H, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonDuration3h = value,
                    data -> data.bloodMoonDuration3h)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_6H, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonDuration6h = value,
                    data -> data.bloodMoonDuration6h)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_9H, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonDuration9h = value,
                    data -> data.bloodMoonDuration9h)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_DURATION_12H, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonDuration12h = value,
                    data -> data.bloodMoonDuration12h)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_HEALTH, Codec.FLOAT),
                    (data, value) -> data.bloodMoonHostileHealthMultiplier = value,
                    data -> data.bloodMoonHostileHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.bloodMoonHostileDamageMultiplier = value,
                    data -> data.bloodMoonHostileDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_FORCE, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonForce = value,
                    data -> data.bloodMoonForce)
            .add()
            .append(new KeyedCodec<>(KEY_PLAYER_DEATH_SETTINGS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.playerDeathSettingsEnabled = value,
                    data -> data.playerDeathSettingsEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT, Codec.FLOAT),
                    (data, value) -> data.playerItemDurabilityLossPercent = value,
                    data -> data.playerItemDurabilityLossPercent)
            .add()
            .append(new KeyedCodec<>(KEY_PLAYER_ITEM_DROP_PERCENT, Codec.FLOAT),
                    (data, value) -> data.playerItemDropPercent = value,
                    data -> data.playerItemDropPercent)
            .add()
            .build();

    private Boolean enabled;
    private Float globalHealthMultiplier;
    private Float globalDamageMultiplier;
    private Boolean peacefulEnabled;
    private Float peacefulHealthMultiplier;
    private Float peacefulDamageMultiplier;
    private Boolean neutralEnabled;
    private Float neutralHealthMultiplier;
    private Float neutralDamageMultiplier;
    private Boolean hostileEnabled;
    private Float hostileHealthMultiplier;
    private Float hostileDamageMultiplier;
    private Boolean bloodMoonEnabled;
    private Float bloodMoonIntervalDays;
    private Float bloodMoonStartHour;
    private Boolean bloodMoonDuration1h;
    private Boolean bloodMoonDuration3h;
    private Boolean bloodMoonDuration6h;
    private Boolean bloodMoonDuration9h;
    private Boolean bloodMoonDuration12h;
    private Float bloodMoonHostileHealthMultiplier;
    private Float bloodMoonHostileDamageMultiplier;
    private Boolean bloodMoonForce;
    private Boolean playerDeathSettingsEnabled;
    private Float playerItemDurabilityLossPercent;
    private Float playerItemDropPercent;

    public HardcoreSettingsPageEventData() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Float getGlobalHealthMultiplier() {
        return globalHealthMultiplier;
    }

    public Float getGlobalDamageMultiplier() {
        return globalDamageMultiplier;
    }

    public Boolean getPeacefulEnabled() {
        return peacefulEnabled;
    }

    public Float getPeacefulHealthMultiplier() {
        return peacefulHealthMultiplier;
    }

    public Float getPeacefulDamageMultiplier() {
        return peacefulDamageMultiplier;
    }

    public Boolean getNeutralEnabled() {
        return neutralEnabled;
    }

    public Float getNeutralHealthMultiplier() {
        return neutralHealthMultiplier;
    }

    public Float getNeutralDamageMultiplier() {
        return neutralDamageMultiplier;
    }

    public Boolean getHostileEnabled() {
        return hostileEnabled;
    }

    public Float getHostileHealthMultiplier() {
        return hostileHealthMultiplier;
    }

    public Float getHostileDamageMultiplier() {
        return hostileDamageMultiplier;
    }

    public Boolean getBloodMoonEnabled() {
        return bloodMoonEnabled;
    }

    public Float getBloodMoonIntervalDays() {
        return bloodMoonIntervalDays;
    }

    public Float getBloodMoonStartHour() {
        return bloodMoonStartHour;
    }

    public Boolean getBloodMoonDuration1h() {
        return bloodMoonDuration1h;
    }

    public Boolean getBloodMoonDuration3h() {
        return bloodMoonDuration3h;
    }

    public Boolean getBloodMoonDuration6h() {
        return bloodMoonDuration6h;
    }

    public Boolean getBloodMoonDuration9h() {
        return bloodMoonDuration9h;
    }

    public Boolean getBloodMoonDuration12h() {
        return bloodMoonDuration12h;
    }

    public Float getBloodMoonHostileHealthMultiplier() {
        return bloodMoonHostileHealthMultiplier;
    }

    public Float getBloodMoonHostileDamageMultiplier() {
        return bloodMoonHostileDamageMultiplier;
    }

    public Boolean getBloodMoonForce() {
        return bloodMoonForce;
    }

    public Boolean getPlayerDeathSettingsEnabled() {
        return playerDeathSettingsEnabled;
    }

    public Float getPlayerItemDurabilityLossPercent() {
        return playerItemDurabilityLossPercent;
    }

    public Float getPlayerItemDropPercent() {
        return playerItemDropPercent;
    }
}
