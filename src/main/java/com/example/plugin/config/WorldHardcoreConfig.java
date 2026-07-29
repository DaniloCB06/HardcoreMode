package com.example.plugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Configuração do HardcoreMode específica para um mundo.
 * Cada mundo pode ter suas próprias configurações independentes.
 */
public class WorldHardcoreConfig {
    public static final BuilderCodec<WorldHardcoreConfig> CODEC = BuilderCodec
            .builder(WorldHardcoreConfig.class, WorldHardcoreConfig::new)
            // Enabled
            .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                    (config, value) -> config.enabled = value,
                    config -> config.enabled)
            .add()
            // Base multipliers
            .append(new KeyedCodec<>("HealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.healthMultiplier = value,
                    config -> config.healthMultiplier)
            .add()
            .append(new KeyedCodec<>("DamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.damageMultiplier = value,
                    config -> config.damageMultiplier)
            .add()
            // Category enabled flags
            .append(new KeyedCodec<>("PassiveEnabled", Codec.BOOLEAN),
                    (config, value) -> config.passiveEnabled = value,
                    config -> config.passiveEnabled)
            .add()
            .append(new KeyedCodec<>("CritterEnabled", Codec.BOOLEAN),
                    (config, value) -> config.critterEnabled = value,
                    config -> config.critterEnabled)
            .add()
            .append(new KeyedCodec<>("HostileEnabled", Codec.BOOLEAN),
                    (config, value) -> config.hostileEnabled = value,
                    config -> config.hostileEnabled)
            .add()
            .append(new KeyedCodec<>("EliteEnabled", Codec.BOOLEAN),
                    (config, value) -> config.eliteEnabled = value,
                    config -> config.eliteEnabled)
            .add()
            .append(new KeyedCodec<>("MinibossEnabled", Codec.BOOLEAN),
                    (config, value) -> config.minibossEnabled = value,
                    config -> config.minibossEnabled)
            .add()
            .append(new KeyedCodec<>("WorldbossEnabled", Codec.BOOLEAN),
                    (config, value) -> config.worldbossEnabled = value,
                    config -> config.worldbossEnabled)
            .add()
            // Category health multipliers
            .append(new KeyedCodec<>("PassiveHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.passiveHealthMultiplier = value,
                    config -> config.passiveHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("CritterHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.critterHealthMultiplier = value,
                    config -> config.critterHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("HostileHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.hostileHealthMultiplier = value,
                    config -> config.hostileHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("EliteHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.eliteHealthMultiplier = value,
                    config -> config.eliteHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("MinibossHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.minibossHealthMultiplier = value,
                    config -> config.minibossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("WorldbossHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.worldbossHealthMultiplier = value,
                    config -> config.worldbossHealthMultiplier)
            .add()
            // Category damage multipliers
            .append(new KeyedCodec<>("PassiveDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.passiveDamageMultiplier = value,
                    config -> config.passiveDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("CritterDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.critterDamageMultiplier = value,
                    config -> config.critterDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("HostileDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.hostileDamageMultiplier = value,
                    config -> config.hostileDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("EliteDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.eliteDamageMultiplier = value,
                    config -> config.eliteDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("MinibossDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.minibossDamageMultiplier = value,
                    config -> config.minibossDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("WorldbossDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.worldbossDamageMultiplier = value,
                    config -> config.worldbossDamageMultiplier)
            .add()
            // Blood Moon settings
            .append(new KeyedCodec<>("BloodMoonEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonEnabled = value,
                    config -> config.bloodMoonEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonIntervalDays", Codec.INTEGER),
                    (config, value) -> config.bloodMoonIntervalDays = value,
                    config -> config.bloodMoonIntervalDays)
            .add()
            .append(new KeyedCodec<>("BloodMoonDurationHours", Codec.INTEGER),
                    (config, value) -> config.bloodMoonDurationHours = value,
                    config -> config.bloodMoonDurationHours)
            .add()
            .append(new KeyedCodec<>("BloodMoonStartHour", Codec.INTEGER),
                    (config, value) -> config.bloodMoonStartHour = value,
                    config -> config.bloodMoonStartHour)
            .add()
            // Blood Moon category enabled
            .append(new KeyedCodec<>("BloodMoonHostileEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonHostileEnabled = value,
                    config -> config.bloodMoonHostileEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonEliteEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonEliteEnabled = value,
                    config -> config.bloodMoonEliteEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonMinibossEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonMinibossEnabled = value,
                    config -> config.bloodMoonMinibossEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonWorldbossEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonWorldbossEnabled = value,
                    config -> config.bloodMoonWorldbossEnabled)
            .add()
            // Blood Moon health multipliers
            .append(new KeyedCodec<>("BloodMoonHostileHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonHostileHealthMultiplier = value,
                    config -> config.bloodMoonHostileHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonEliteHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonEliteHealthMultiplier = value,
                    config -> config.bloodMoonEliteHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonMinibossHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonMinibossHealthMultiplier = value,
                    config -> config.bloodMoonMinibossHealthMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonWorldbossHealthMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonWorldbossHealthMultiplier = value,
                    config -> config.bloodMoonWorldbossHealthMultiplier)
            .add()
            // Blood Moon damage multipliers
            .append(new KeyedCodec<>("BloodMoonHostileDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonHostileDamageMultiplier = value,
                    config -> config.bloodMoonHostileDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonEliteDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonEliteDamageMultiplier = value,
                    config -> config.bloodMoonEliteDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonMinibossDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonMinibossDamageMultiplier = value,
                    config -> config.bloodMoonMinibossDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonWorldbossDamageMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonWorldbossDamageMultiplier = value,
                    config -> config.bloodMoonWorldbossDamageMultiplier)
            .add()
            // Blood Moon XP
            .append(new KeyedCodec<>("BloodMoonXpMultiplierEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonXpMultiplierEnabled = value,
                    config -> config.bloodMoonXpMultiplierEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonXpMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonXpMultiplier = value,
                    config -> config.bloodMoonXpMultiplier)
            .add()
            .append(new KeyedCodec<>("BloodMoonMoneyMultiplierEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonMoneyMultiplierEnabled = value,
                    config -> config.bloodMoonMoneyMultiplierEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonMoneyMultiplier", Codec.FLOAT),
                    (config, value) -> config.bloodMoonMoneyMultiplier = value,
                    config -> config.bloodMoonMoneyMultiplier)
            .add()
            // Blood Moon Drops
            .append(new KeyedCodec<>("BloodMoonDropsEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonDropsEnabled = value,
                    config -> config.bloodMoonDropsEnabled)
            .add()
            // Player Death Settings
            .append(new KeyedCodec<>("PlayerDeathSettingsEnabled", Codec.BOOLEAN),
                    (config, value) -> config.playerDeathSettingsEnabled = value,
                    config -> config.playerDeathSettingsEnabled)
            .add()
            .append(new KeyedCodec<>("PlayerItemDurabilityLossPercent", Codec.INTEGER),
                    (config, value) -> config.playerItemDurabilityLossPercent = value,
                    config -> config.playerItemDurabilityLossPercent)
            .add()
            .append(new KeyedCodec<>("PlayerItemDropPercent", Codec.INTEGER),
                    (config, value) -> config.playerItemDropPercent = value,
                    config -> config.playerItemDropPercent)
            .add()
            // Blood Moon Death Settings
            .append(new KeyedCodec<>("BloodMoonDeathSettingsEnabled", Codec.BOOLEAN),
                    (config, value) -> config.bloodMoonDeathSettingsEnabled = value,
                    config -> config.bloodMoonDeathSettingsEnabled)
            .add()
            .append(new KeyedCodec<>("BloodMoonItemDurabilityLossPercent", Codec.INTEGER),
                    (config, value) -> config.bloodMoonItemDurabilityLossPercent = value,
                    config -> config.bloodMoonItemDurabilityLossPercent)
            .add()
            .append(new KeyedCodec<>("BloodMoonItemDropPercent", Codec.INTEGER),
                    (config, value) -> config.bloodMoonItemDropPercent = value,
                    config -> config.bloodMoonItemDropPercent)
            .add()
            .build();

    // Whether HardcoreMode is enabled for this world
    public boolean enabled = false;

    // Base multipliers
    public float healthMultiplier = 1.0f;
    public float damageMultiplier = 1.0f;

    // Category enabled flags
    public boolean passiveEnabled = false;
    public boolean critterEnabled = false;
    public boolean hostileEnabled = false;
    public boolean eliteEnabled = false;
    public boolean minibossEnabled = false;
    public boolean worldbossEnabled = false;

    // Category health multipliers
    public float passiveHealthMultiplier = 1.0f;
    public float critterHealthMultiplier = 1.0f;
    public float hostileHealthMultiplier = 2.0f;
    public float eliteHealthMultiplier = 2.5f;
    public float minibossHealthMultiplier = 3.0f;
    public float worldbossHealthMultiplier = 4.0f;

    // Category damage multipliers
    public float passiveDamageMultiplier = 1.0f;
    public float critterDamageMultiplier = 1.0f;
    public float hostileDamageMultiplier = 1.5f;
    public float eliteDamageMultiplier = 2.0f;
    public float minibossDamageMultiplier = 2.5f;
    public float worldbossDamageMultiplier = 3.0f;

    // Blood Moon settings
    public boolean bloodMoonEnabled = false;
    public int bloodMoonIntervalDays = 7;
    public int bloodMoonDurationHours = 3;
    public int bloodMoonStartHour = 20;

    // Blood Moon category enabled
    public boolean bloodMoonHostileEnabled = false;
    public boolean bloodMoonEliteEnabled = false;
    public boolean bloodMoonMinibossEnabled = false;
    public boolean bloodMoonWorldbossEnabled = false;

    // Blood Moon health multipliers
    public float bloodMoonHostileHealthMultiplier = 3.0f;
    public float bloodMoonEliteHealthMultiplier = 4.0f;
    public float bloodMoonMinibossHealthMultiplier = 5.0f;
    public float bloodMoonWorldbossHealthMultiplier = 6.0f;

    // Blood Moon damage multipliers
    public float bloodMoonHostileDamageMultiplier = 2.5f;
    public float bloodMoonEliteDamageMultiplier = 3.0f;
    public float bloodMoonMinibossDamageMultiplier = 3.5f;
    public float bloodMoonWorldbossDamageMultiplier = 4.0f;

    // Blood Moon XP
    public boolean bloodMoonXpMultiplierEnabled = false;
    public float bloodMoonXpMultiplier = 2.0f;
    public boolean bloodMoonMoneyMultiplierEnabled = false;
    public float bloodMoonMoneyMultiplier = 2.0f;

    // Blood Moon Drops
    public boolean bloodMoonDropsEnabled = false;
    

    // Player Death Settings
    public boolean playerDeathSettingsEnabled = false;
    public int playerItemDurabilityLossPercent = 20;
    public int playerItemDropPercent = 20;
    
    // Blood Moon Death Settings
    public boolean bloodMoonDeathSettingsEnabled = false;
    public int bloodMoonItemDurabilityLossPercent = 50;
    public int bloodMoonItemDropPercent = 50;

    // Transient runtime state (não salvo)
    private transient boolean bloodMoonActive = false;
    private transient Long forcedBloodMoonEndHourOfEpoch = null;
    private transient Long bloodMoonStartHourOfEpoch = null;
    private transient Long bloodMoonEndHourOfEpoch = null;
    private transient long lastProcessedHourOfEpoch = -1;

    public WorldHardcoreConfig() {
    }

    /**
     * Aplica defaults globais do HardcoreMode para este mundo.
     * Sempre força o mundo a iniciar desativado.
     */
    public void applyDefaultsFromGlobal(HardcoreModeConfig global) {
        if (global == null) {
            return;
        }

        this.enabled = false;
        this.healthMultiplier = global.healthMultiplier;
        this.damageMultiplier = global.damageMultiplier;

        this.passiveEnabled = global.passiveEnabled;
        this.critterEnabled = global.critterEnabled;
        this.hostileEnabled = global.hostileEnabled;
        this.eliteEnabled = global.eliteEnabled;
        this.minibossEnabled = global.minibossEnabled;
        this.worldbossEnabled = global.worldbossEnabled;

        this.passiveHealthMultiplier = global.passiveHealthMultiplier;
        this.critterHealthMultiplier = global.critterHealthMultiplier;
        this.hostileHealthMultiplier = global.hostileHealthMultiplier;
        this.eliteHealthMultiplier = global.eliteHealthMultiplier;
        this.minibossHealthMultiplier = global.minibossHealthMultiplier;
        this.worldbossHealthMultiplier = global.worldbossHealthMultiplier;

        this.passiveDamageMultiplier = global.passiveDamageMultiplier;
        this.critterDamageMultiplier = global.critterDamageMultiplier;
        this.hostileDamageMultiplier = global.hostileDamageMultiplier;
        this.eliteDamageMultiplier = global.eliteDamageMultiplier;
        this.minibossDamageMultiplier = global.minibossDamageMultiplier;
        this.worldbossDamageMultiplier = global.worldbossDamageMultiplier;

        this.bloodMoonEnabled = global.bloodMoonEnabled;
        this.bloodMoonIntervalDays = global.bloodMoonIntervalDays;
        this.bloodMoonDurationHours = global.bloodMoonDurationHours;
        this.bloodMoonStartHour = global.bloodMoonStartHour;

        this.bloodMoonHostileEnabled = global.bloodMoonHostileEnabled;
        this.bloodMoonEliteEnabled = global.bloodMoonEliteEnabled;
        this.bloodMoonMinibossEnabled = global.bloodMoonMinibossEnabled;
        this.bloodMoonWorldbossEnabled = global.bloodMoonWorldbossEnabled;

        this.bloodMoonHostileHealthMultiplier = global.bloodMoonHostileHealthMultiplier;
        this.bloodMoonEliteHealthMultiplier = global.bloodMoonEliteHealthMultiplier;
        this.bloodMoonMinibossHealthMultiplier = global.bloodMoonMinibossHealthMultiplier;
        this.bloodMoonWorldbossHealthMultiplier = global.bloodMoonWorldbossHealthMultiplier;

        this.bloodMoonHostileDamageMultiplier = global.bloodMoonHostileDamageMultiplier;
        this.bloodMoonEliteDamageMultiplier = global.bloodMoonEliteDamageMultiplier;
        this.bloodMoonMinibossDamageMultiplier = global.bloodMoonMinibossDamageMultiplier;
        this.bloodMoonWorldbossDamageMultiplier = global.bloodMoonWorldbossDamageMultiplier;

        this.bloodMoonXpMultiplierEnabled = global.bloodMoonXpMultiplierEnabled;
        this.bloodMoonXpMultiplier = global.bloodMoonXpMultiplier;
        this.bloodMoonMoneyMultiplierEnabled = global.bloodMoonMoneyMultiplierEnabled;
        this.bloodMoonMoneyMultiplier = global.bloodMoonMoneyMultiplier;
        this.bloodMoonDropsEnabled = global.bloodMoonDropsEnabled;

        this.playerDeathSettingsEnabled = global.playerDeathSettingsEnabled;
        this.playerItemDurabilityLossPercent = global.playerItemDurabilityLossPercent;
        this.playerItemDropPercent = global.playerItemDropPercent;

        this.bloodMoonDeathSettingsEnabled = global.bloodMoonDeathSettingsEnabled;
        this.bloodMoonItemDurabilityLossPercent = global.bloodMoonItemDurabilityLossPercent;
        this.bloodMoonItemDropPercent = global.bloodMoonItemDropPercent;
    }

    /**
     * Copia as configurações de outro config para este.
     */
    public void copyFrom(WorldHardcoreConfig other) {
        if (other == null) return;
        
        this.enabled = other.enabled;
        this.healthMultiplier = other.healthMultiplier;
        this.damageMultiplier = other.damageMultiplier;
        
        this.passiveEnabled = other.passiveEnabled;
        this.critterEnabled = other.critterEnabled;
        this.hostileEnabled = other.hostileEnabled;
        this.eliteEnabled = other.eliteEnabled;
        this.minibossEnabled = other.minibossEnabled;
        this.worldbossEnabled = other.worldbossEnabled;
        
        this.passiveHealthMultiplier = other.passiveHealthMultiplier;
        this.critterHealthMultiplier = other.critterHealthMultiplier;
        this.hostileHealthMultiplier = other.hostileHealthMultiplier;
        this.eliteHealthMultiplier = other.eliteHealthMultiplier;
        this.minibossHealthMultiplier = other.minibossHealthMultiplier;
        this.worldbossHealthMultiplier = other.worldbossHealthMultiplier;
        
        this.passiveDamageMultiplier = other.passiveDamageMultiplier;
        this.critterDamageMultiplier = other.critterDamageMultiplier;
        this.hostileDamageMultiplier = other.hostileDamageMultiplier;
        this.eliteDamageMultiplier = other.eliteDamageMultiplier;
        this.minibossDamageMultiplier = other.minibossDamageMultiplier;
        this.worldbossDamageMultiplier = other.worldbossDamageMultiplier;
        
        this.bloodMoonEnabled = other.bloodMoonEnabled;
        this.bloodMoonIntervalDays = other.bloodMoonIntervalDays;
        this.bloodMoonDurationHours = other.bloodMoonDurationHours;
        this.bloodMoonStartHour = other.bloodMoonStartHour;
        
        this.bloodMoonHostileEnabled = other.bloodMoonHostileEnabled;
        this.bloodMoonEliteEnabled = other.bloodMoonEliteEnabled;
        this.bloodMoonMinibossEnabled = other.bloodMoonMinibossEnabled;
        this.bloodMoonWorldbossEnabled = other.bloodMoonWorldbossEnabled;
        
        this.bloodMoonHostileHealthMultiplier = other.bloodMoonHostileHealthMultiplier;
        this.bloodMoonEliteHealthMultiplier = other.bloodMoonEliteHealthMultiplier;
        this.bloodMoonMinibossHealthMultiplier = other.bloodMoonMinibossHealthMultiplier;
        this.bloodMoonWorldbossHealthMultiplier = other.bloodMoonWorldbossHealthMultiplier;
        
        this.bloodMoonHostileDamageMultiplier = other.bloodMoonHostileDamageMultiplier;
        this.bloodMoonEliteDamageMultiplier = other.bloodMoonEliteDamageMultiplier;
        this.bloodMoonMinibossDamageMultiplier = other.bloodMoonMinibossDamageMultiplier;
        this.bloodMoonWorldbossDamageMultiplier = other.bloodMoonWorldbossDamageMultiplier;

        this.bloodMoonXpMultiplierEnabled = other.bloodMoonXpMultiplierEnabled;
        this.bloodMoonXpMultiplier = other.bloodMoonXpMultiplier;
        this.bloodMoonMoneyMultiplierEnabled = other.bloodMoonMoneyMultiplierEnabled;
        this.bloodMoonMoneyMultiplier = other.bloodMoonMoneyMultiplier;
        this.bloodMoonDropsEnabled = other.bloodMoonDropsEnabled;
        
        this.playerDeathSettingsEnabled = other.playerDeathSettingsEnabled;
        this.playerItemDurabilityLossPercent = other.playerItemDurabilityLossPercent;
        this.playerItemDropPercent = other.playerItemDropPercent;
        
        this.bloodMoonDeathSettingsEnabled = other.bloodMoonDeathSettingsEnabled;
        this.bloodMoonItemDurabilityLossPercent = other.bloodMoonItemDurabilityLossPercent;
        this.bloodMoonItemDropPercent = other.bloodMoonItemDropPercent;
    }

    // Blood Moon runtime state getters/setters
    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    public void setBloodMoonActive(boolean active) {
        this.bloodMoonActive = active;
    }

    public Long getForcedBloodMoonEndHourOfEpoch() {
        return forcedBloodMoonEndHourOfEpoch;
    }

    public void setForcedBloodMoonEndHourOfEpoch(Long value) {
        this.forcedBloodMoonEndHourOfEpoch = value;
    }

    public Long getBloodMoonStartHourOfEpoch() {
        return bloodMoonStartHourOfEpoch;
    }

    public void setBloodMoonStartHourOfEpoch(Long value) {
        this.bloodMoonStartHourOfEpoch = value;
    }

    public Long getBloodMoonEndHourOfEpoch() {
        return bloodMoonEndHourOfEpoch;
    }

    public void setBloodMoonEndHourOfEpoch(Long value) {
        this.bloodMoonEndHourOfEpoch = value;
    }

    public long getLastProcessedHourOfEpoch() {
        return lastProcessedHourOfEpoch;
    }

    public void setLastProcessedHourOfEpoch(long value) {
        this.lastProcessedHourOfEpoch = value;
    }

    public void clearBloodMoonState() {
        this.bloodMoonActive = false;
        this.forcedBloodMoonEndHourOfEpoch = null;
        this.bloodMoonStartHourOfEpoch = null;
        this.bloodMoonEndHourOfEpoch = null;
    }
}
