package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreSettingsPageEventData {
    public static final String KEY_ENABLED = "@Enabled";
    public static final String KEY_GLOBAL_HEALTH = "@GlobalHealthMultiplier";
    public static final String KEY_GLOBAL_DAMAGE = "@GlobalDamageMultiplier";
    public static final String KEY_PASSIVE_ENABLED = "@PassiveEnabled";
    public static final String KEY_PASSIVE_HEALTH = "@PassiveHealthMultiplier";
    public static final String KEY_PASSIVE_DAMAGE = "@PassiveDamageMultiplier";
    public static final String KEY_CRITTER_ENABLED = "@CritterEnabled";
    public static final String KEY_CRITTER_HEALTH = "@CritterHealthMultiplier";
    public static final String KEY_CRITTER_DAMAGE = "@CritterDamageMultiplier";
    public static final String KEY_HOSTILE_ENABLED = "@HostileEnabled";
    public static final String KEY_HOSTILE_HEALTH = "@HostileHealthMultiplier";
    public static final String KEY_HOSTILE_DAMAGE = "@HostileDamageMultiplier";
    public static final String KEY_ELITE_ENABLED = "@EliteEnabled";
    public static final String KEY_ELITE_HEALTH = "@EliteHealthMultiplier";
    public static final String KEY_ELITE_DAMAGE = "@EliteDamageMultiplier";
    public static final String KEY_MINIBOSS_ENABLED = "@MinibossEnabled";
    public static final String KEY_MINIBOSS_HEALTH = "@MinibossHealthMultiplier";
    public static final String KEY_MINIBOSS_DAMAGE = "@MinibossDamageMultiplier";
    public static final String KEY_WORLDBOSS_ENABLED = "@WorldbossEnabled";
    public static final String KEY_WORLDBOSS_HEALTH = "@WorldbossHealthMultiplier";
    public static final String KEY_WORLDBOSS_DAMAGE = "@WorldbossDamageMultiplier";
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
    public static final String KEY_BLOOD_MOON_XP_MULTIPLIER = "@BloodMoonXpMultiplier";
    public static final String KEY_BLOOD_MOON_HOSTILE_ENABLED = "@BloodMoonHostileEnabled";
    public static final String KEY_BLOOD_MOON_ELITE_ENABLED = "@BloodMoonEliteEnabled";
    public static final String KEY_BLOOD_MOON_ELITE_HEALTH = "@BloodMoonEliteHealthMultiplier";
    public static final String KEY_BLOOD_MOON_ELITE_DAMAGE = "@BloodMoonEliteDamageMultiplier";
    public static final String KEY_BLOOD_MOON_MINIBOSS_ENABLED = "@BloodMoonMinibossEnabled";
    public static final String KEY_BLOOD_MOON_MINIBOSS_HEALTH = "@BloodMoonMinibossHealthMultiplier";
    public static final String KEY_BLOOD_MOON_MINIBOSS_DAMAGE = "@BloodMoonMinibossDamageMultiplier";
    public static final String KEY_BLOOD_MOON_WORLDBOSS_ENABLED = "@BloodMoonWorldbossEnabled";
    public static final String KEY_BLOOD_MOON_WORLDBOSS_HEALTH = "@BloodMoonWorldbossHealthMultiplier";
    public static final String KEY_BLOOD_MOON_WORLDBOSS_DAMAGE = "@BloodMoonWorldbossDamageMultiplier";
    public static final String KEY_BLOOD_MOON_FORCE = "@BloodMoonForce";
    public static final String KEY_BLOOD_MOON_HUD_ENABLED = "@BloodMoonHudEnabled";
    public static final String KEY_PLAYER_DEATH_SETTINGS_ENABLED = "@PlayerDeathSettingsEnabled";
    public static final String KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT = "@PlayerItemDurabilityLossPercent";
    public static final String KEY_PLAYER_ITEM_DROP_PERCENT = "@PlayerItemDropPercent";
    public static final String KEY_GO_BACK = "@GoBack";

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
            .append(new KeyedCodec<>(KEY_PASSIVE_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.passiveEnabled = value,
                    data -> data.passiveEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_PASSIVE_HEALTH, Codec.FLOAT),
                    (data, value) -> data.passiveHealthMultiplier = value,
                    data -> data.passiveHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_PASSIVE_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.passiveDamageMultiplier = value,
                    data -> data.passiveDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_CRITTER_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.critterEnabled = value,
                    data -> data.critterEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_CRITTER_HEALTH, Codec.FLOAT),
                    (data, value) -> data.critterHealthMultiplier = value,
                    data -> data.critterHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_CRITTER_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.critterDamageMultiplier = value,
                    data -> data.critterDamageMultiplier)
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
            .append(new KeyedCodec<>(KEY_ELITE_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.eliteEnabled = value,
                    data -> data.eliteEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_ELITE_HEALTH, Codec.FLOAT),
                    (data, value) -> data.eliteHealthMultiplier = value,
                    data -> data.eliteHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_ELITE_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.eliteDamageMultiplier = value,
                    data -> data.eliteDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_MINIBOSS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.minibossEnabled = value,
                    data -> data.minibossEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_MINIBOSS_HEALTH, Codec.FLOAT),
                    (data, value) -> data.minibossHealthMultiplier = value,
                    data -> data.minibossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_MINIBOSS_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.minibossDamageMultiplier = value,
                    data -> data.minibossDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_WORLDBOSS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.worldbossEnabled = value,
                    data -> data.worldbossEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_WORLDBOSS_HEALTH, Codec.FLOAT),
                    (data, value) -> data.worldbossHealthMultiplier = value,
                    data -> data.worldbossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_WORLDBOSS_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.worldbossDamageMultiplier = value,
                    data -> data.worldbossDamageMultiplier)
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
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HOSTILE_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonHostileEnabled = value,
                    data -> data.bloodMoonHostileEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonEliteEnabled = value,
                    data -> data.bloodMoonEliteEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_HEALTH, Codec.FLOAT),
                    (data, value) -> data.bloodMoonEliteHealthMultiplier = value,
                    data -> data.bloodMoonEliteHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_ELITE_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.bloodMoonEliteDamageMultiplier = value,
                    data -> data.bloodMoonEliteDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonMinibossEnabled = value,
                    data -> data.bloodMoonMinibossEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_HEALTH, Codec.FLOAT),
                    (data, value) -> data.bloodMoonMinibossHealthMultiplier = value,
                    data -> data.bloodMoonMinibossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_MINIBOSS_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.bloodMoonMinibossDamageMultiplier = value,
                    data -> data.bloodMoonMinibossDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonWorldbossEnabled = value,
                    data -> data.bloodMoonWorldbossEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_HEALTH, Codec.FLOAT),
                    (data, value) -> data.bloodMoonWorldbossHealthMultiplier = value,
                    data -> data.bloodMoonWorldbossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_WORLDBOSS_DAMAGE, Codec.FLOAT),
                    (data, value) -> data.bloodMoonWorldbossDamageMultiplier = value,
                    data -> data.bloodMoonWorldbossDamageMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_XP_MULTIPLIER, Codec.FLOAT),
                    (data, value) -> data.bloodMoonXpMultiplier = value,
                    data -> data.bloodMoonXpMultiplier)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_FORCE, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonForce = value,
                    data -> data.bloodMoonForce)
            .add()
            .append(new KeyedCodec<>(KEY_BLOOD_MOON_HUD_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.bloodMoonHudEnabled = value,
                    data -> data.bloodMoonHudEnabled)
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
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                    (data, value) -> data.goBack = value,
                    data -> data.goBack)
            .add()
            .build();

    private Boolean enabled;
    private Float globalHealthMultiplier;
    private Float globalDamageMultiplier;
    private Boolean passiveEnabled;
    private Float passiveHealthMultiplier;
    private Float passiveDamageMultiplier;
    private Boolean critterEnabled;
    private Float critterHealthMultiplier;
    private Float critterDamageMultiplier;
    private Boolean hostileEnabled;
    private Float hostileHealthMultiplier;
    private Float hostileDamageMultiplier;
    private Boolean eliteEnabled;
    private Float eliteHealthMultiplier;
    private Float eliteDamageMultiplier;
    private Boolean minibossEnabled;
    private Float minibossHealthMultiplier;
    private Float minibossDamageMultiplier;
    private Boolean worldbossEnabled;
    private Float worldbossHealthMultiplier;
    private Float worldbossDamageMultiplier;
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
    private Float bloodMoonXpMultiplier;
    private Boolean bloodMoonHostileEnabled;
    private Boolean bloodMoonEliteEnabled;
    private Float bloodMoonEliteHealthMultiplier;
    private Float bloodMoonEliteDamageMultiplier;
    private Boolean bloodMoonMinibossEnabled;
    private Float bloodMoonMinibossHealthMultiplier;
    private Float bloodMoonMinibossDamageMultiplier;
    private Boolean bloodMoonWorldbossEnabled;
    private Float bloodMoonWorldbossHealthMultiplier;
    private Float bloodMoonWorldbossDamageMultiplier;
    private Boolean bloodMoonForce;
    private Boolean bloodMoonHudEnabled;
    private Boolean playerDeathSettingsEnabled;
    private Float playerItemDurabilityLossPercent;
    private Float playerItemDropPercent;
    private Boolean goBack;

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

    public Boolean getPassiveEnabled() {
        return passiveEnabled;
    }

    public Float getPassiveHealthMultiplier() {
        return passiveHealthMultiplier;
    }

    public Float getPassiveDamageMultiplier() {
        return passiveDamageMultiplier;
    }

    public Boolean getCritterEnabled() {
        return critterEnabled;
    }

    public Float getCritterHealthMultiplier() {
        return critterHealthMultiplier;
    }

    public Float getCritterDamageMultiplier() {
        return critterDamageMultiplier;
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

    public Boolean getEliteEnabled() {
        return eliteEnabled;
    }

    public Float getEliteHealthMultiplier() {
        return eliteHealthMultiplier;
    }

    public Float getEliteDamageMultiplier() {
        return eliteDamageMultiplier;
    }

    public Boolean getMinibossEnabled() {
        return minibossEnabled;
    }

    public Float getMinibossHealthMultiplier() {
        return minibossHealthMultiplier;
    }

    public Float getMinibossDamageMultiplier() {
        return minibossDamageMultiplier;
    }

    public Boolean getWorldbossEnabled() {
        return worldbossEnabled;
    }

    public Float getWorldbossHealthMultiplier() {
        return worldbossHealthMultiplier;
    }

    public Float getWorldbossDamageMultiplier() {
        return worldbossDamageMultiplier;
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

    public Float getBloodMoonXpMultiplier() {
        return bloodMoonXpMultiplier;
    }

    public Boolean getBloodMoonHostileEnabled() {
        return bloodMoonHostileEnabled;
    }

    public Boolean getBloodMoonEliteEnabled() {
        return bloodMoonEliteEnabled;
    }

    public Float getBloodMoonEliteHealthMultiplier() {
        return bloodMoonEliteHealthMultiplier;
    }

    public Float getBloodMoonEliteDamageMultiplier() {
        return bloodMoonEliteDamageMultiplier;
    }

    public Boolean getBloodMoonMinibossEnabled() {
        return bloodMoonMinibossEnabled;
    }

    public Float getBloodMoonMinibossHealthMultiplier() {
        return bloodMoonMinibossHealthMultiplier;
    }

    public Float getBloodMoonMinibossDamageMultiplier() {
        return bloodMoonMinibossDamageMultiplier;
    }

    public Boolean getBloodMoonWorldbossEnabled() {
        return bloodMoonWorldbossEnabled;
    }

    public Float getBloodMoonWorldbossHealthMultiplier() {
        return bloodMoonWorldbossHealthMultiplier;
    }

    public Float getBloodMoonWorldbossDamageMultiplier() {
        return bloodMoonWorldbossDamageMultiplier;
    }

    public Boolean getBloodMoonForce() {
        return bloodMoonForce;
    }

    public Boolean getBloodMoonHudEnabled() {
        return bloodMoonHudEnabled;
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

    public Boolean getGoBack() {
        return goBack;
    }
}
