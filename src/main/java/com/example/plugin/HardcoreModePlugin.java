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
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
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
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.attitude.AttitudeView;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import java.util.concurrent.atomic.AtomicReference;

public class HardcoreModePlugin extends JavaPlugin {
    private static HardcoreModePlugin instance;
    private static final String RPG_LEVELING_PLUGIN_CLASS = "org.zuxaw.plugin.RPGLevelingPlugin";
    private static final String RPG_LEVELING_CONFIG_CLASS = "org.zuxaw.plugin.config.LevelingConfig";

    private final Config<HardcoreModeConfig> config;
    private final HardcoreMobSetupSystem mobSetupSystem;
    private final HardcoreMobDamageSystem mobDamageSystem;
    private final HardcoreBloodMoonSystem bloodMoonSystem;
    private final HardcorePlayerDeathConfigSystem playerDeathConfigSystem;
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
        this.mobSetupSystem = new HardcoreMobSetupSystem(this);
        this.mobDamageSystem = new HardcoreMobDamageSystem(this);
        this.bloodMoonSystem = new HardcoreBloodMoonSystem(this);
        this.playerDeathConfigSystem = new HardcorePlayerDeathConfigSystem(this);
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

    @Override
    protected void setup() {
        normalizeConfig();
        getEntityStoreRegistry().registerSystem(mobSetupSystem);
        getEntityStoreRegistry().registerSystem(mobDamageSystem);
        getEntityStoreRegistry().registerSystem(bloodMoonSystem);
        getEntityStoreRegistry().registerSystem(playerDeathConfigSystem);
        getEntityStoreRegistry().registerSystem(mobStatRefreshSystem);
        getEntityStoreRegistry().registerSystem(playerPresenceSystem);
        getCommandRegistry().registerCommand(new HardcoreGuiCommand(this));
    }

    @Override
    protected void start() {
        // No startup work needed.
    }

    public void applyHealthModifier(EntityStatMap statMap, MobDisposition disposition) {
        HardcoreModeConfig data = config.get();
        int healthStat = DefaultEntityStatTypes.getHealth();
        String key = HardcoreMobSetupSystem.HEALTH_MODIFIER_KEY;
        float multiplier = getHealthMultiplier(disposition);

        if (!isMobEnabled(disposition) || multiplier <= 1.0f) {
            statMap.removeModifier(healthStat, key);
            statMap.maximizeStatValue(healthStat);
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.MULTIPLICATIVE,
                Math.max(1.0f, multiplier)
        );
        statMap.putModifier(healthStat, key, modifier);
        statMap.maximizeStatValue(healthStat);
    }

    public float getHealthMultiplier(MobDisposition disposition) {
        HardcoreModeConfig data = config.get();
        switch (disposition) {
            case PEACEFUL:
                return resolveMultiplier(data.peacefulHealthMultiplier, data.healthMultiplier);
            case NEUTRAL:
                return resolveMultiplier(data.neutralHealthMultiplier, data.healthMultiplier);
            case HOSTILE:
            default:
                float base = resolveMultiplier(data.hostileHealthMultiplier, data.healthMultiplier);
                if (isBloodMoonActive()) {
                    return resolveMultiplier(data.bloodMoonHostileHealthMultiplier, base);
                }
                return base;
        }
    }

    public float getDamageMultiplier(MobDisposition disposition) {
        HardcoreModeConfig data = config.get();
        switch (disposition) {
            case PEACEFUL:
                return resolveMultiplier(data.peacefulDamageMultiplier, data.damageMultiplier);
            case NEUTRAL:
                return resolveMultiplier(data.neutralDamageMultiplier, data.damageMultiplier);
            case HOSTILE:
            default:
                float base = resolveMultiplier(data.hostileDamageMultiplier, data.damageMultiplier);
                if (isBloodMoonActive()) {
                    return resolveMultiplier(data.bloodMoonHostileDamageMultiplier, base);
                }
                return base;
        }
    }

    public boolean isCategoryEnabled(MobDisposition disposition) {
        HardcoreModeConfig data = config.get();
        switch (disposition) {
            case PEACEFUL:
                return data.peacefulEnabled;
            case NEUTRAL:
                return data.neutralEnabled;
            case HOSTILE:
            default:
                return data.hostileEnabled;
        }
    }

    public boolean isMobEnabled(MobDisposition disposition) {
        if (disposition == MobDisposition.HOSTILE && isBloodMoonActive()) {
            return true;
        }
        return isCategoryEnabled(disposition);
    }

    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    public void refreshBloodMoonState(Store<EntityStore> store, boolean applyToMobs) {
        boolean active = computeBloodMoonActive(store);
        boolean changed = active != bloodMoonActive;

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
        refreshBloodMoonState(store, true);
    }

    public MobDisposition resolveMobDisposition(Store<EntityStore> store, NPCEntity npcEntity) {
        return resolveMobDispositionInternal(store, npcEntity, getAnyPlayerRef(store));
    }

    public MobDisposition resolveMobDisposition(
            Store<EntityStore> store,
            NPCEntity npcEntity,
            Ref<EntityStore> playerRef
    ) {
        return resolveMobDispositionInternal(store, npcEntity, playerRef);
    }

    private MobDisposition resolveMobDispositionInternal(
            Store<EntityStore> store,
            NPCEntity npcEntity,
            Ref<EntityStore> playerRef
    ) {
        Role role = npcEntity == null ? null : npcEntity.getRole();
        if (role == null) {
            return MobDisposition.NEUTRAL;
        }

        Attitude attitude = resolveAttitude(store, role, playerRef);
        if (attitude == null) {
            return MobDisposition.NEUTRAL;
        }

        switch (attitude) {
            case HOSTILE:
                return MobDisposition.HOSTILE;
            case NEUTRAL:
                return MobDisposition.NEUTRAL;
            case IGNORE:
            case FRIENDLY:
            case REVERED:
                return MobDisposition.PEACEFUL;
            default:
                return MobDisposition.NEUTRAL;
        }
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
            Ref<EntityStore> playerRef
    ) {
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
            MobDisposition disposition = resolveMobDisposition(store, npcEntity, playerRef);
            applyHealthModifier(statMap, disposition);
        }
    }

    private void normalizeConfig() {
        HardcoreModeConfig data = config.get();
        boolean changed = false;

        if (data.peacefulHealthMultiplier <= 0.0f) {
            data.peacefulHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.peacefulDamageMultiplier <= 0.0f) {
            data.peacefulDamageMultiplier = data.damageMultiplier;
            changed = true;
        }
        if (data.neutralHealthMultiplier <= 0.0f) {
            data.neutralHealthMultiplier = data.healthMultiplier;
            changed = true;
        }
        if (data.neutralDamageMultiplier <= 0.0f) {
            data.neutralDamageMultiplier = data.damageMultiplier;
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
        if (data.bloodMoonHostileHealthMultiplier <= 0.0f) {
            data.bloodMoonHostileHealthMultiplier = data.hostileHealthMultiplier;
            changed = true;
        }
        if (data.bloodMoonHostileDamageMultiplier <= 0.0f) {
            data.bloodMoonHostileDamageMultiplier = data.hostileDamageMultiplier;
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

        long scheduledDay = epochDay - Math.floorMod(epochDay, intervalDays);
        long startHourOfEpoch = (scheduledDay * 24L) + startHour;
        if (currentHourOfEpoch < startHourOfEpoch) {
            startHourOfEpoch -= intervalDays * 24L;
        }

        long endHourOfEpoch = startHourOfEpoch + durationHours;
        return currentHourOfEpoch >= startHourOfEpoch && currentHourOfEpoch < endHourOfEpoch;
    }

    private void announceBloodMoon(boolean started, Store<EntityStore> store) {
        String chatText = started ? "Blood Moon has begun." : "Blood Moon has ended.";
        Universe universe = Universe.get();
        if (universe != null) {
            universe.sendMessage(buildRedTinyMessage(chatText));
        }

        com.hypixel.hytale.server.core.Message title = com.hypixel.hytale.server.core.Message.raw("Blood Moon");
        com.hypixel.hytale.server.core.Message subtitle = com.hypixel.hytale.server.core.Message.raw(
                started ? "has begun" : "has ended"
        );
        EventTitleUtil.showEventTitleToUniverse(
                title,
                subtitle,
                true,
                EventTitleUtil.DEFAULT_ZONE,
                EventTitleUtil.DEFAULT_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION
        );
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

    private Attitude resolveAttitude(Store<EntityStore> store, Role role, Ref<EntityStore> playerRef) {
        if (store == null || role == null) {
            return Attitude.NEUTRAL;
        }

        if (playerRef == null || !playerRef.isValid()) {
            return defaultPlayerAttitude(role);
        }

        Blackboard blackboard = store.getResource(Blackboard.getResourceType());
        if (blackboard == null) {
            return defaultPlayerAttitude(role);
        }

        AttitudeView view = blackboard.getView(AttitudeView.class, playerRef, store);
        if (view == null) {
            return defaultPlayerAttitude(role);
        }

        return view.getAttitude(playerRef, role, playerRef, store);
    }

    private Attitude defaultPlayerAttitude(Role role) {
        if (role == null || role.getWorldSupport() == null) {
            return Attitude.NEUTRAL;
        }

        Attitude attitude = role.getWorldSupport().getDefaultPlayerAttitude();
        return attitude == null ? Attitude.NEUTRAL : attitude;
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
}
