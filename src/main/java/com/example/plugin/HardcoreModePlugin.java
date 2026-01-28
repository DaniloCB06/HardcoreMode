package com.example.plugin;

import com.example.plugin.commands.HardcoreGuiCommand;
import com.example.plugin.config.HardcoreModeConfig;
import com.example.plugin.systems.HardcoreMobDamageSystem;
import com.example.plugin.systems.HardcoreMobSetupSystem;
import com.example.plugin.systems.HardcoreBloodMoonSystem;
import com.example.plugin.systems.HardcorePlayerDeathConfigSystem;
import com.example.plugin.systems.HardcoreMobStatRefreshSystem;
import com.example.plugin.systems.HardcorePlayerPresenceSystem;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HardcoreModePlugin extends JavaPlugin {
    private static HardcoreModePlugin instance;
    private static final String RPG_LEVELING_PLUGIN_CLASS = "org.zuxaw.plugin.RPGLevelingPlugin";
    private static final String RPG_LEVELING_CONFIG_CLASS = "org.zuxaw.plugin.config.LevelingConfig";

    // Generic fix: Store<EntityStore> instead of EntityStore
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<Store<EntityStore>> activeStoreRef = new AtomicReference<>();

    private final Config<HardcoreModeConfig> config;
    private final MobCategoryResolver mobCategoryResolver;
    private final HardcoreMobSetupSystem mobSetupSystem;
    private final HardcoreMobDamageSystem mobDamageSystem;
    private final HardcoreBloodMoonSystem bloodMoonSystem;
    private final HardcoreMobStatRefreshSystem mobStatRefreshSystem;
    private final HardcorePlayerPresenceSystem playerPresenceSystem;

    private Ref<EntityStore> cachedPlayerRef;
    private boolean bloodMoonActive;
    private Long forcedBloodMoonEndHourOfEpoch;

    private boolean rpgLevelingChecked;
    private boolean rpgLevelingAvailable;
    private java.lang.reflect.Method rpgGetInstanceMethod;
    private java.lang.reflect.Method rpgGetConfigMethod;
    private java.lang.reflect.Method rpgGetRateExpMethod;
    private java.lang.reflect.Method rpgSetRateExpMethod;
    private Double rpgLevelingBaseRateExp;
    private float rpgLevelingAppliedMultiplier = 1.0f;
    private boolean rpgLevelingBloodMoonApplied;

    public HardcoreModePlugin(JavaPluginInit init) {
        super(init);
        this.config = withConfig("HardcoreMode", HardcoreModeConfig.CODEC);
        this.mobCategoryResolver = new MobCategoryResolver(getDataDirectory());
        this.mobSetupSystem = new HardcoreMobSetupSystem(this);
        this.mobDamageSystem = new HardcoreMobDamageSystem(this);
        this.bloodMoonSystem = new HardcoreBloodMoonSystem(this);
        this.mobStatRefreshSystem = new HardcoreMobStatRefreshSystem(this);
        this.playerPresenceSystem = new HardcorePlayerPresenceSystem(this);
        instance = this;
    }

    public static HardcoreModePlugin get() {
        return instance;
    }

    public HardcoreModeConfig getConfigData() {
        return config.get();
    }

    public Config<HardcoreModeConfig> getConfig() {
        return config;
    }

    public MobCategory resolveMobCategory(String creatureId) {
        return mobCategoryResolver.resolve(creatureId);
    }

    public MobCategoryResolver getMobCategoryResolver() {
        return mobCategoryResolver;
    }

    @Override
    protected void setup() {
        normalizeConfig();

        getEntityStoreRegistry().registerSystem(mobSetupSystem);
        getEntityStoreRegistry().registerSystem(mobDamageSystem);
        getEntityStoreRegistry().registerSystem(bloodMoonSystem);

        // ✅ Registro com fallback (dependência de DropPlayerDeathItems fica opcional)
        registerPlayerDeathConfigSystemWithFallback();

        getEntityStoreRegistry().registerSystem(mobStatRefreshSystem);
        getEntityStoreRegistry().registerSystem(playerPresenceSystem);

        getCommandRegistry().registerCommand(new HardcoreGuiCommand(this));
    }

    private void registerPlayerDeathConfigSystemWithFallback() {
        HardcorePlayerDeathConfigSystem.DependencyMode[] modes = new HardcorePlayerDeathConfigSystem.DependencyMode[] {
                HardcorePlayerDeathConfigSystem.DependencyMode.STRICT,
                HardcorePlayerDeathConfigSystem.DependencyMode.AFTER_CONFIG,
                HardcorePlayerDeathConfigSystem.DependencyMode.NONE
        };

        for (HardcorePlayerDeathConfigSystem.DependencyMode mode : modes) {
            try {
                getEntityStoreRegistry().registerSystem(new HardcorePlayerDeathConfigSystem(this, mode));
                System.out.println("[HardcoreMode] PlayerDeathConfigSystem registrado com mode=" + mode);
                return;
            } catch (Throwable t) {
                System.out.println(
                        "[HardcoreMode] Falha ao registrar PlayerDeathConfigSystem com mode=" + mode + " -> " + t);
            }
        }

        System.out.println(
                "[HardcoreMode] PlayerDeathConfigSystem não pôde ser registrado. O mod continuará sem alterar morte do player.");
    }

    @Override
    protected void start() {
        // No startup work needed.
        startHeartbeat();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopHeartbeat));
    }

    public void applyHealthModifier(EntityStatMap statMap, MobCategory category) {
        HardcoreModeConfig data = config.get();
        int healthStat = DefaultEntityStatTypes.getHealth();
        String key = HardcoreMobSetupSystem.HEALTH_MODIFIER_KEY;
        float multiplier = getHealthMultiplier(category);

        if (!isMobEnabled(category) || multiplier <= 1.0f) {
            statMap.removeModifier(healthStat, key);
            statMap.maximizeStatValue(healthStat);
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.MULTIPLICATIVE,
                Math.max(1.0f, multiplier));
        statMap.putModifier(healthStat, key, modifier);
        statMap.maximizeStatValue(healthStat);
    }

    public float getHealthMultiplier(MobCategory category) {
        HardcoreModeConfig data = config.get();
        float base = resolveCategoryMultiplier(category, true);
        if (isBloodMoonActive() && isBloodMoonAffected(category)) {
            switch (category) {
                case ELITE:
                    return resolveMultiplier(data.bloodMoonEliteHealthMultiplier, base);
                case MINIBOSS:
                    return resolveMultiplier(data.bloodMoonMinibossHealthMultiplier, base);
                case WORLDBOSS:
                    return resolveMultiplier(data.bloodMoonWorldbossHealthMultiplier, base);
                case HOSTILE:
                default:
                    return resolveMultiplier(data.bloodMoonHostileHealthMultiplier, base);
            }
        }
        return base;
    }

    public float getDamageMultiplier(MobCategory category) {
        HardcoreModeConfig data = config.get();
        float base = resolveCategoryMultiplier(category, false);
        if (isBloodMoonActive() && isBloodMoonAffected(category)) {
            switch (category) {
                case ELITE:
                    return resolveMultiplier(data.bloodMoonEliteDamageMultiplier, base);
                case MINIBOSS:
                    return resolveMultiplier(data.bloodMoonMinibossDamageMultiplier, base);
                case WORLDBOSS:
                    return resolveMultiplier(data.bloodMoonWorldbossDamageMultiplier, base);
                case HOSTILE:
                default:
                    return resolveMultiplier(data.bloodMoonHostileDamageMultiplier, base);
            }
        }
        return base;
    }

    private float resolveCategoryMultiplier(MobCategory category, boolean health) {
        HardcoreModeConfig data = config.get();
        switch (category) {
            case PASSIVE:
                return resolveMultiplier(health ? data.passiveHealthMultiplier : data.passiveDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case CRITTER:
                return resolveMultiplier(health ? data.critterHealthMultiplier : data.critterDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case HOSTILE:
                return resolveMultiplier(health ? data.hostileHealthMultiplier : data.hostileDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case ELITE:
                return resolveMultiplier(health ? data.eliteHealthMultiplier : data.eliteDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case MINIBOSS:
                return resolveMultiplier(health ? data.minibossHealthMultiplier : data.minibossDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case WORLDBOSS:
                return resolveMultiplier(health ? data.worldbossHealthMultiplier : data.worldbossDamageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
            case NONE:
            default:
                return resolveMultiplier(health ? data.healthMultiplier : data.damageMultiplier,
                        health ? data.healthMultiplier : data.damageMultiplier);
        }
    }

    public boolean isBloodMoonAffected(MobCategory category) {
        switch (category) {
            case HOSTILE:
            case ELITE:
            case MINIBOSS:
            case WORLDBOSS:
                return true;
            default:
                return false;
        }
    }

    public boolean isCategoryEnabled(MobCategory category) {
        HardcoreModeConfig data = config.get();
        switch (category) {
            case PASSIVE:
                return data.passiveEnabled;
            case CRITTER:
                return data.critterEnabled;
            case HOSTILE:
                return data.hostileEnabled;
            case ELITE:
                return data.eliteEnabled;
            case MINIBOSS:
                return data.minibossEnabled;
            case WORLDBOSS:
                return data.worldbossEnabled;
            case NONE:
            default:
                return false;
        }
    }

    public boolean isMobEnabled(MobCategory category) {
        if (isBloodMoonActive() && isBloodMoonAffected(category)) {
            HardcoreModeConfig data = config.get();
            switch (category) {
                case ELITE:
                    return data.bloodMoonEliteEnabled;
                case MINIBOSS:
                    return data.bloodMoonMinibossEnabled;
                case WORLDBOSS:
                    return data.bloodMoonWorldbossEnabled;
                case HOSTILE:
                default:
                    return data.bloodMoonHostileEnabled;
            }
        }
        return isCategoryEnabled(category);
    }

    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    public void refreshBloodMoonState(Store<EntityStore> store, boolean applyToMobs) {
        boolean active = computeBloodMoonActive(store);
        boolean changed = active != bloodMoonActive;

        if (changed) {
            System.out.println("[HardcoreDebug] Blood Moon State Changed! New Active: " + active + " Old: "
                    + bloodMoonActive + " StoreHash: " + System.identityHashCode(store));
        }

        bloodMoonActive = active;
        syncRpgLevelingMultiplier(active);
        if (changed) {
            announceBloodMoon(active, store);
            if (applyToMobs) {
                applyToExistingMobs(store);
            }
        }
    }

    public void forceBloodMoonNow(Store<EntityStore> store) {
        if (store == null) {
            return;
        }

        HardcoreModeConfig data = config.get();
        int durationHours = data.bloodMoonDurationHours;
        if (!isValidBloodMoonDuration(durationHours)) {
            return;
        }

        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) {
            return;
        }

        long currentHourOfEpoch = getCurrentHourOfEpoch(time);
        forcedBloodMoonEndHourOfEpoch = currentHourOfEpoch + durationHours;
        System.out.println("[HardcoreDebug] Forcing Blood Moon Now. Duration: " + durationHours + " CurrentHour: "
                + currentHourOfEpoch + " EndHour: " + forcedBloodMoonEndHourOfEpoch + " StoreHash: "
                + System.identityHashCode(store));
        refreshBloodMoonState(store, true);
    }

    public MobCategory resolveMobCategory(NPCEntity npcEntity) {
        return mobCategoryResolver.resolve(npcEntity);
    }

    public void applyToExistingMobs(Store<EntityStore> store) {
        if (store == null) {
            return;
        }

        Ref<EntityStore> playerRef = getAnyPlayerRef(store);
        applyToExistingMobs(store, playerRef);
    }

    public void applyToExistingMobs(Store<EntityStore> store, Ref<EntityStore> playerRef) {
        if (store == null || playerRef == null || !playerRef.isValid()) {
            return;
        }

        cachedPlayerRef = playerRef;
        Query<EntityStore> query = Query.any();
        store.forEachChunk(query, (chunk, commandBuffer) -> {
            applyToChunk(store, chunk, playerRef);
            return true;
        });
    }

    private void applyToChunk(
            Store<EntityStore> store,
            ArchetypeChunk<EntityStore> chunk,
            Ref<EntityStore> playerRef) {
        if (playerRef == null || !playerRef.isValid()) {
            return;
        }

        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        ComponentType<EntityStore, EntityStatMap> statType = EntityStatMap.getComponentType();
        if (statType == null) {
            return;
        }

        int size = chunk.size();
        for (int i = 0; i < size; i++) {
            if (playerType != null && chunk.getComponent(i, playerType) != null) {
                continue;
            }

            EntityStatMap statMap = chunk.getComponent(i, statType);
            if (statMap == null) {
                continue;
            }

            NPCEntity npcEntity = npcType == null ? null : chunk.getComponent(i, npcType);
            MobCategory category = resolveMobCategory(npcEntity);
            applyHealthModifier(statMap, category);
        }
    }

    private void normalizeConfig() {
        HardcoreModeConfig data = config.get();
        boolean changed = false;

        if (data.passiveHealthMultiplier <= 0.0f) {
            data.passiveHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.passiveDamageMultiplier <= 0.0f) {
            data.passiveDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.critterHealthMultiplier <= 0.0f) {
            data.critterHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.critterDamageMultiplier <= 0.0f) {
            data.critterDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.hostileHealthMultiplier <= 0.0f) {
            data.hostileHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.hostileDamageMultiplier <= 0.0f) {
            data.hostileDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.eliteHealthMultiplier <= 0.0f) {
            data.eliteHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.eliteDamageMultiplier <= 0.0f) {
            data.eliteDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.minibossHealthMultiplier <= 0.0f) {
            data.minibossHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.minibossDamageMultiplier <= 0.0f) {
            data.minibossDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.worldbossHealthMultiplier <= 0.0f) {
            data.worldbossHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.worldbossDamageMultiplier <= 0.0f) {
            data.worldbossDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.bloodMoonHostileHealthMultiplier <= 0.0f) {
            data.bloodMoonHostileHealthMultiplier = data.hostileHealthMultiplier;
            changed = true;
        }
        if (data.bloodMoonHostileDamageMultiplier <= 0.0f) {
            data.bloodMoonHostileDamageMultiplier = data.hostileDamageMultiplier;
            changed = true;
        }
        if (data.bloodMoonEliteHealthMultiplier <= 0.0f) {
            data.bloodMoonEliteHealthMultiplier = data.eliteHealthMultiplier;
            changed = true;
        }
        if (data.bloodMoonEliteDamageMultiplier <= 0.0f) {
            data.bloodMoonEliteDamageMultiplier = data.eliteDamageMultiplier;
            changed = true;
        }
        if (data.bloodMoonMinibossHealthMultiplier <= 0.0f) {
            data.bloodMoonMinibossHealthMultiplier = data.minibossHealthMultiplier;
            changed = true;
        }
        if (data.bloodMoonMinibossDamageMultiplier <= 0.0f) {
            data.bloodMoonMinibossDamageMultiplier = data.minibossDamageMultiplier;
            changed = true;
        }
        if (data.bloodMoonWorldbossHealthMultiplier <= 0.0f) {
            data.bloodMoonWorldbossHealthMultiplier = data.worldbossHealthMultiplier;
            changed = true;
        }
        if (data.bloodMoonWorldbossDamageMultiplier <= 0.0f) {
            data.bloodMoonWorldbossDamageMultiplier = data.worldbossDamageMultiplier;
            changed = true;
        }
        if (!isValidBloodMoonDuration(data.bloodMoonDurationHours)) {
            data.bloodMoonDurationHours = 3;
            changed = true;
        }

        if (changed) {
            config.save();
        }
    }

    private float resolveMultiplier(float categoryValue, float legacyValue) {
        return categoryValue > 0.0f ? categoryValue : legacyValue;
    }

    private boolean computeBloodMoonActive(Store<EntityStore> store) {
        if (store == null) {
            return false;
        }

        HardcoreModeConfig data = config.get();
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) {
            return false;
        }

        long epochDay = time.getGameDateTime().toLocalDate().toEpochDay();
        int currentHour = time.getCurrentHour();
        long currentHourOfEpoch = (epochDay * 24L) + currentHour;

        Long forcedEnd = forcedBloodMoonEndHourOfEpoch;
        if (forcedEnd != null) {
            if (currentHourOfEpoch < forcedEnd) {
                return true;
            }
            System.out.println(
                    "[HardcoreDebug] Forced Blood Moon Expired. Current: " + currentHourOfEpoch + " End: " + forcedEnd);
            forcedBloodMoonEndHourOfEpoch = null;
        }

        if (!data.bloodMoonEnabled) {
            return false;
        }

        int intervalDays = data.bloodMoonIntervalDays;
        if (intervalDays <= 0) {
            return false;
        }

        int durationHours = data.bloodMoonDurationHours;
        if (!isValidBloodMoonDuration(durationHours)) {
            return false;
        }

        int startHour = data.bloodMoonStartHour;
        if (startHour < 0) {
            startHour = 0;
        } else if (startHour > 23) {
            startHour = 23;
        }

        // Calculate 'Current' Window (aligned to current epoch block)
        long scheduledDayCurrent = epochDay - Math.floorMod(epochDay, intervalDays);
        long startEpochCurrent = (scheduledDayCurrent * 24L) + startHour;
        long endEpochCurrent = startEpochCurrent + durationHours;

        // Calculate 'Previous' Window (one interval ago, handling overlap)
        long startEpochPrev = startEpochCurrent - (intervalDays * 24L);
        long endEpochPrev = startEpochPrev + durationHours;

        boolean activeCurrent = currentHourOfEpoch >= startEpochCurrent && currentHourOfEpoch < endEpochCurrent;
        boolean activePrev = currentHourOfEpoch >= startEpochPrev && currentHourOfEpoch < endEpochPrev;
        boolean active = activeCurrent || activePrev;

        // Debug Log: Trigger if active, OR if we are close to the Current Window start
        // (next 24h or last 24h)
        // This helps debug why it might NOT be starting.
        if (active || (currentHourOfEpoch >= startEpochCurrent - 24 && currentHourOfEpoch <= startEpochCurrent + 24)) {
            System.out.println("[HardcoreDebug] Natural Calc: CurrentEpoch=" + currentHourOfEpoch
                    + " EpochDay=" + epochDay
                    + " Interval=" + intervalDays
                    + " StartCur=" + startEpochCurrent
                    + " EndCur=" + endEpochCurrent
                    + " ActiveCur=" + activeCurrent
                    + " StartPrev=" + startEpochPrev
                    + " EndPrev=" + endEpochPrev
                    + " ActivePrev=" + activePrev
                    + " FINAL_ACTIVE=" + active);
        }

        return active;
    }

    private void announceBloodMoon(boolean started, Store<EntityStore> store) {
        String chatText = started ? "Blood Moon has begun." : "Blood Moon has ended.";
        Universe universe = Universe.get();
        if (universe != null) {
            universe.sendMessage(buildRedTinyMessage(chatText));
        }

        com.hypixel.hytale.server.core.Message title = com.hypixel.hytale.server.core.Message.raw("Blood Moon");
        com.hypixel.hytale.server.core.Message subtitle = com.hypixel.hytale.server.core.Message.raw(
                started ? "has begun" : "has ended");
        EventTitleUtil.showEventTitleToUniverse(
                title,
                subtitle,
                true,
                EventTitleUtil.DEFAULT_ZONE,
                EventTitleUtil.DEFAULT_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION);
    }

    private boolean isValidBloodMoonDuration(int hours) {
        return hours == 1 || hours == 3 || hours == 6 || hours == 9 || hours == 12;
    }

    private com.hypixel.hytale.server.core.Message buildRedTinyMessage(String text) {
        try {
            Class<?> tinyMsgClass = Class.forName("fi.sulku.hytale.TinyMsg");
            java.lang.reflect.Method parse = tinyMsgClass.getMethod("parse", String.class);
            Object result = parse.invoke(null, "<red>" + text + "</red>");
            if (result instanceof com.hypixel.hytale.server.core.Message) {
                return (com.hypixel.hytale.server.core.Message) result;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // TinyMessage is optional; fall back to a raw message if unavailable.
        }

        return com.hypixel.hytale.server.core.Message.raw(text);
    }

    private long getCurrentHourOfEpoch(WorldTimeResource time) {
        long epochDay = time.getGameDateTime().toLocalDate().toEpochDay();
        return (epochDay * 24L) + time.getCurrentHour();
    }

    public Ref<EntityStore> getAnyPlayerRef(Store<EntityStore> store) {
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (store == null || playerType == null) {
            return null;
        }

        if (cachedPlayerRef != null
                && cachedPlayerRef.isValid()
                && store.getComponent(cachedPlayerRef, playerType) != null) {
            return cachedPlayerRef;
        }

        AtomicReference<Ref<EntityStore>> found = new AtomicReference<>();
        store.forEachChunk(Query.any(), (chunk, commandBuffer) -> {
            int size = chunk.size();
            for (int i = 0; i < size; i++) {
                if (chunk.getComponent(i, playerType) != null) {
                    found.set(chunk.getReferenceTo(i));
                    return false;
                }
            }
            return true;
        });

        cachedPlayerRef = found.get();
        return found.get();
    }

    public boolean isRpgLevelingAvailable() {
        if (!ensureRpgLevelingAccess()) {
            return false;
        }

        try {
            return rpgGetInstanceMethod.invoke(null) != null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private void syncRpgLevelingMultiplier(boolean active) {
        Object levelingConfig = getRpgLevelingConfig();
        if (levelingConfig == null) {
            rpgLevelingBloodMoonApplied = false;
            rpgLevelingAppliedMultiplier = 1.0f;
            rpgLevelingBaseRateExp = null;
            return;
        }

        if (!active) {
            if (rpgLevelingBloodMoonApplied && rpgLevelingBaseRateExp != null) {
                setRpgRateExp(levelingConfig, rpgLevelingBaseRateExp);
            }
            rpgLevelingBloodMoonApplied = false;
            rpgLevelingAppliedMultiplier = 1.0f;
            rpgLevelingBaseRateExp = null;
            return;
        }

        float multiplier = config.get().bloodMoonXpMultiplier;
        if (!rpgLevelingBloodMoonApplied) {
            rpgLevelingBaseRateExp = getRpgRateExp(levelingConfig);
        }

        if (rpgLevelingBaseRateExp == null) {
            return;
        }

        if (rpgLevelingBloodMoonApplied
                && Math.abs(rpgLevelingAppliedMultiplier - multiplier) <= 0.0001f) {
            return;
        }

        double target = rpgLevelingBaseRateExp * Math.max(0.0f, multiplier);
        if (setRpgRateExp(levelingConfig, target)) {
            rpgLevelingAppliedMultiplier = multiplier;
            rpgLevelingBloodMoonApplied = true;
        }
    }

    private Object getRpgLevelingConfig() {
        if (!ensureRpgLevelingAccess()) {
            return null;
        }

        try {
            Object pluginInstance = rpgGetInstanceMethod.invoke(null);
            if (pluginInstance == null) {
                return null;
            }

            Object configWrapper = rpgGetConfigMethod.invoke(pluginInstance);
            if (configWrapper instanceof Config) {
                return ((Config<?>) configWrapper).get();
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }

        return null;
    }

    private Double getRpgRateExp(Object levelingConfig) {
        if (levelingConfig == null) {
            return null;
        }

        try {
            Object result = rpgGetRateExpMethod.invoke(levelingConfig);
            if (result instanceof Double) {
                return (Double) result;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }

        return null;
    }

    private boolean setRpgRateExp(Object levelingConfig, double value) {
        if (levelingConfig == null) {
            return false;
        }

        try {
            rpgSetRateExpMethod.invoke(levelingConfig, value);
            return true;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private boolean ensureRpgLevelingAccess() {
        if (rpgLevelingChecked) {
            return rpgLevelingAvailable;
        }

        rpgLevelingChecked = true;
        try {
            Class<?> pluginClass = Class.forName(RPG_LEVELING_PLUGIN_CLASS);
            Class<?> configClass = Class.forName(RPG_LEVELING_CONFIG_CLASS);
            rpgGetInstanceMethod = pluginClass.getMethod("get");
            rpgGetConfigMethod = pluginClass.getMethod("getConfig");
            rpgGetRateExpMethod = configClass.getMethod("getRateExp");
            rpgSetRateExpMethod = configClass.getMethod("setRateExp", double.class);
            rpgLevelingAvailable = true;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            rpgLevelingAvailable = false;
        }

        return rpgLevelingAvailable;
    }

    /**
     * Sets the active store reference for the heartbeat scheduler.
     * Called by the TickingSystem when it processes a tick (if it processes).
     */
    /**
     * Sets the active store reference for the heartbeat scheduler.
     * Called by the TickingSystem when it processes a tick (if it processes).
     */
    public void setActiveStore(Store<EntityStore> store) {
        if (store != null) {
            activeStoreRef.set(store);
        }
    }

    private void startHeartbeat() {
        // Run every 1 second
        scheduler.scheduleAtFixedRate(this::checkBloodMoonHeartbeat, 1, 1, TimeUnit.SECONDS);
        System.out.println("[HardcoreMode] Scheduler Heartbeat started.");
    }

    private void stopHeartbeat() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        System.out.println("[HardcoreMode] Scheduler Heartbeat stopped.");
    }

    private void checkBloodMoonHeartbeat() {
        try {
            Store<EntityStore> store = activeStoreRef.get();
            if (store == null) {
                return;
            }

            // Verify if WorldTimeResource still exists or if world invalid
            WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
            if (time == null) {
                return;
            }

            // Force refresh. If the server loop is sleeping, this ensures we still catch
            // the time change
            // (assuming time resource is advancing locally or we just need to catch up).
            refreshBloodMoonState(store, true);
        } catch (Exception e) {
            System.err.println("[HardcoreMode] Heartbeat error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
