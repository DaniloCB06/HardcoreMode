package com.example.plugin;

import com.example.plugin.commands.HardcoreGuiCommand;
import com.example.plugin.config.BloodMoonDropConfig;
import com.example.plugin.config.HardcoreModeConfig;
import com.example.plugin.config.MobMoneyDropConfig;
import com.example.plugin.config.WorldConfigManager;
import com.example.plugin.config.WorldHardcoreConfig;
import com.example.plugin.money.VaultEconomyCoordinator;
import com.example.plugin.systems.HardcoreBloodMoonDropSystem;
import com.example.plugin.systems.HardcoreBloodMoonSystem;
import com.example.plugin.systems.HardcoreEndlessXpDeathSystem;
import com.example.plugin.systems.HardcoreMoneyDropSystem;
import com.example.plugin.systems.HardcoreMobDamageSystem;
import com.example.plugin.systems.HardcoreMobSetupSystem;
import com.example.plugin.systems.HardcoreMobStatRefreshSystem;
import com.example.plugin.systems.HardcorePlayerDeathConfigSystem;
import com.example.plugin.systems.HardcorePlayerPresenceSystem;
import com.example.plugin.visuals.BloodMoonVisuals;
import com.example.plugin.xp.XpMultiplierCoordinator;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class HardcoreModePlugin extends JavaPlugin {
    private static HardcoreModePlugin instance;
    private static final String RPG_LEVELING_PLUGIN_CLASS = "org.zuxaw.plugin.RPGLevelingPlugin";
    private static final String RPG_LEVELING_CONFIG_CLASS = "org.zuxaw.plugin.config.LevelingConfig";

    private final AtomicReference<Store<EntityStore>> activeStoreRef = new AtomicReference<>();

    private final Config<HardcoreModeConfig> config;
    private final BloodMoonDropConfig bloodMoonDropConfig;
    private final MobMoneyDropConfig mobMoneyDropConfig;
    private final MobCategoryResolver mobCategoryResolver;
    private final WorldConfigManager worldConfigManager;
    private final HardcoreMobSetupSystem mobSetupSystem;
    private final HardcoreMobDamageSystem mobDamageSystem;
    private final HardcoreBloodMoonSystem bloodMoonSystem;
    private final HardcoreBloodMoonDropSystem bloodMoonDropSystem;
    private final HardcoreMoneyDropSystem moneyDropSystem;
    private final HardcoreMobStatRefreshSystem mobStatRefreshSystem;
    private final HardcoreEndlessXpDeathSystem endlessXpDeathSystem;
    private final HardcorePlayerPresenceSystem playerPresenceSystem;
    private final BloodMoonVisuals bloodMoonVisuals;
    private final VaultEconomyCoordinator vaultEconomyCoordinator;

    private volatile Ref<EntityStore> cachedPlayerRef;
    private volatile Store<EntityStore> cachedPlayerStore;
    
    // Cache de nome do mundo por store para performance
    private final Map<Store<EntityStore>, String> worldNameCache = 
            Collections.synchronizedMap(new WeakHashMap<>());
    private final XpMultiplierCoordinator xpMultiplierCoordinator;

    private boolean rpgLevelingChecked;
    private boolean rpgLevelingAvailable;
    private java.lang.reflect.Method rpgGetInstanceMethod;
    private java.lang.reflect.Method rpgGetConfigMethod;
    private java.lang.reflect.Method rpgGetRateExpMethod;
    private java.lang.reflect.Method rpgSetRateExpMethod;
    private Double rpgLevelingBaseRateExp;
    private float rpgLevelingAppliedMultiplier = 1.0f;
    private boolean rpgLevelingBloodMoonApplied;
    
    // Rastreia quais mundos têm Blood Moon ativa com XP multiplier habilitado
    private final Map<String, Float> worldsWithXpMultiplier = new ConcurrentHashMap<>();

    public HardcoreModePlugin(JavaPluginInit init) {
        super(init);
        this.config = withConfig("HardcoreMode", HardcoreModeConfig.CODEC);
        this.bloodMoonDropConfig = new BloodMoonDropConfig(getDataDirectory());
        this.mobCategoryResolver = new MobCategoryResolver(getDataDirectory());
        this.mobMoneyDropConfig = new MobMoneyDropConfig(getDataDirectory(), this.mobCategoryResolver);
        this.worldConfigManager = new WorldConfigManager(getDataDirectory(), this::getConfigData);
        this.mobSetupSystem = new HardcoreMobSetupSystem(this);
        this.mobDamageSystem = new HardcoreMobDamageSystem(this);
        this.bloodMoonSystem = new HardcoreBloodMoonSystem(this);
        this.bloodMoonDropSystem = new HardcoreBloodMoonDropSystem(this);
        this.moneyDropSystem = new HardcoreMoneyDropSystem(this);
        this.mobStatRefreshSystem = new HardcoreMobStatRefreshSystem(this);
        this.endlessXpDeathSystem = new HardcoreEndlessXpDeathSystem(this);
        this.playerPresenceSystem = new HardcorePlayerPresenceSystem(this);
        this.bloodMoonVisuals = new BloodMoonVisuals();
        this.xpMultiplierCoordinator = new XpMultiplierCoordinator(this);
        this.vaultEconomyCoordinator = new VaultEconomyCoordinator(getName());
        instance = this;

        migrateLegacyWorldDefaults();
    }

    private void migrateLegacyWorldDefaults() {
        try {
            java.util.Set<String> storedWorlds = worldConfigManager.getStoredWorlds();
            boolean migrated = config.get().migrateLegacyWorldSettings(storedWorlds);
            if (migrated) {
                config.save();
            }
        } catch (Exception ignored) {
        }
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
    
    public WorldConfigManager getWorldConfigManager() {
        return worldConfigManager;
    }
    
    /**
     * Obtém a configuração do HardcoreMode para um mundo específico.
     */
    public WorldHardcoreConfig getWorldConfig(String worldName) {
        return worldConfigManager.getWorldConfig(worldName);
    }
    
    /**
     * Obtém a configuração do HardcoreMode para o mundo de um Store.
     */
    public WorldHardcoreConfig getWorldConfig(Store<EntityStore> store) {
        String worldName = getWorldName(store);
        if (worldName == null) {
            // Fallback: tentar usar o mundo em cache se disponível
            if (cachedPlayerStore != null) {
                String cachedWorldName = getWorldName(cachedPlayerStore);
                if (cachedWorldName != null) {
                    return worldConfigManager.getWorldConfig(cachedWorldName);
                }
            }
            // Último recurso: retornar config do primeiro mundo disponível
            Universe universe = Universe.get();
            if (universe != null) {
                Map<String, World> worlds = universe.getWorlds();
                if (worlds != null && !worlds.isEmpty()) {
                    String firstWorldName = worlds.keySet().iterator().next();
                    return worldConfigManager.getWorldConfig(firstWorldName);
                }
            }
            return new WorldHardcoreConfig(); // Config padrão se nada funcionar
        }
        return worldConfigManager.getWorldConfig(worldName);
    }
    
    /**
     * Obtém o nome do primeiro mundo disponível no Universe.
     * Útil como fallback quando não é possível determinar o mundo específico.
     * @return O nome do primeiro mundo, ou null se não houver mundos
     */
    public String getFirstAvailableWorldName() {
        Universe universe = Universe.get();
        if (universe == null) return null;
        
        Map<String, World> worlds = universe.getWorlds();
        if (worlds == null || worlds.isEmpty()) return null;
        
        return worlds.keySet().iterator().next();
    }

    public MobCategory resolveMobCategory(String creatureId) {
        return mobCategoryResolver.resolve(creatureId);
    }

    public MobCategory resolveMobCategory(NPCEntity npcEntity) {
        return mobCategoryResolver.resolve(npcEntity);
    }

    public MobCategoryResolver getMobCategoryResolver() {
        return mobCategoryResolver;
    }

    public BloodMoonDropConfig getBloodMoonDropConfig() {
        return bloodMoonDropConfig;
    }

    public MobMoneyDropConfig getMobMoneyDropConfig() {
        return mobMoneyDropConfig;
    }

    public VaultEconomyCoordinator getVaultEconomyCoordinator() {
        return vaultEconomyCoordinator;
    }

    @Override
    protected void setup() {
        normalizeConfig();

        getEntityStoreRegistry().registerSystem(mobSetupSystem);
        getEntityStoreRegistry().registerSystem(mobDamageSystem);
        getEntityStoreRegistry().registerSystem(bloodMoonSystem);
        getEntityStoreRegistry().registerSystem(bloodMoonDropSystem);
        getEntityStoreRegistry().registerSystem(moneyDropSystem);
        getEntityStoreRegistry().registerSystem(endlessXpDeathSystem);

        registerPlayerDeathConfigSystemWithFallback();

        getEntityStoreRegistry().registerSystem(mobStatRefreshSystem);
        getEntityStoreRegistry().registerSystem(playerPresenceSystem);

        xpMultiplierCoordinator.initialize();

        getCommandRegistry().registerCommand(new HardcoreGuiCommand(this));
        registerPlayerWorldSync();
    }

    private void registerPlayerWorldSync() {
        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
            if (event == null) {
                return;
            }

            World world = event.getWorld();
            if (world == null) {
                return;
            }

            String worldName = world.getName();

            EntityStore entityStore = world.getEntityStore();
            if (entityStore == null) {
                return;
            }

            Store<EntityStore> store = entityStore.getStore();
            if (store == null) {
                return;
            }

            if (worldName != null && !worldName.isEmpty()) {
                worldNameCache.put(store, worldName);
            }

            Holder<EntityStore> holder = event.getHolder();
            if (holder == null) {
                return;
            }

            ComponentType<EntityStore, Player> playerType = Player.getComponentType();
            if (playerType == null) {
                return;
            }

            Player player = holder.getComponent(playerType);
            if (player == null) {
                return;
            }

            syncBloodMoonVisualsForPlayer(player, store, worldName);
        });
    }

    @Override
    protected void start() {
    }

    @Override
    protected void shutdown() {
        xpMultiplierCoordinator.close();
    }

    private void registerPlayerDeathConfigSystemWithFallback() {
        HardcorePlayerDeathConfigSystem.DependencyMode[] modes = new HardcorePlayerDeathConfigSystem.DependencyMode[]{
                HardcorePlayerDeathConfigSystem.DependencyMode.STRICT,
                HardcorePlayerDeathConfigSystem.DependencyMode.AFTER_CONFIG,
                HardcorePlayerDeathConfigSystem.DependencyMode.NONE
        };

        for (HardcorePlayerDeathConfigSystem.DependencyMode mode : modes) {
            try {
                getEntityStoreRegistry().registerSystem(new HardcorePlayerDeathConfigSystem(this, mode));
                return;
            } catch (Throwable t) {
            }
        }
    }


    public void refreshBloodMoonStateIfNeeded(Store<EntityStore> store, boolean applyToMobs) {
        if (store == null) return;

        // Obter configuração do mundo
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (worldConfig == null) return;
        // Blood Moon funciona independentemente do Enemy Settings (worldConfig.enabled)
        
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return;

        long currentHourOfEpoch = getCurrentHourOfEpoch(time);

        // Verificar se já processamos esta hora para este mundo
        if (worldConfig.getLastProcessedHourOfEpoch() == currentHourOfEpoch) {
            return;
        }
        worldConfig.setLastProcessedHourOfEpoch(currentHourOfEpoch);

        refreshBloodMoonState(store, applyToMobs);
    }

    public void applyHealthModifier(Store<EntityStore> store, EntityStatMap statMap, MobCategory category) {
        int healthStat = DefaultEntityStatTypes.getHealth();
        String key = HardcoreMobSetupSystem.HEALTH_MODIFIER_KEY;
        float multiplier = getHealthMultiplier(store, category);

        if (!isMobEnabled(store, category) || multiplier <= 1.0f) {
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
    
    // Método legacy para compatibilidade
    public void applyHealthModifier(EntityStatMap statMap, MobCategory category) {
        applyHealthModifier(cachedPlayerStore, statMap, category);
    }

    public float getHealthMultiplier(Store<EntityStore> store, MobCategory category) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        float base = resolveCategoryMultiplier(worldConfig, category, true);
        if (isBloodMoonActive(store) && isBloodMoonAffected(worldConfig, category)) {
            switch (category) {
                case ELITE:
                    return resolveMultiplier(worldConfig.bloodMoonEliteHealthMultiplier, base);
                case MINIBOSS:
                    return resolveMultiplier(worldConfig.bloodMoonMinibossHealthMultiplier, base);
                case WORLDBOSS:
                    return resolveMultiplier(worldConfig.bloodMoonWorldbossHealthMultiplier, base);
                case HOSTILE:
                default:
                    return resolveMultiplier(worldConfig.bloodMoonHostileHealthMultiplier, base);
            }
        }
        return base;
    }
    
    // Método legacy para compatibilidade
    public float getHealthMultiplier(MobCategory category) {
        return getHealthMultiplier(cachedPlayerStore, category);
    }

    public float getDamageMultiplier(Store<EntityStore> store, MobCategory category) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        float base = resolveCategoryMultiplier(worldConfig, category, false);
        if (isBloodMoonActive(store) && isBloodMoonAffected(worldConfig, category)) {
            switch (category) {
                case ELITE:
                    return resolveMultiplier(worldConfig.bloodMoonEliteDamageMultiplier, base);
                case MINIBOSS:
                    return resolveMultiplier(worldConfig.bloodMoonMinibossDamageMultiplier, base);
                case WORLDBOSS:
                    return resolveMultiplier(worldConfig.bloodMoonWorldbossDamageMultiplier, base);
                case HOSTILE:
                default:
                    return resolveMultiplier(worldConfig.bloodMoonHostileDamageMultiplier, base);
            }
        }
        return base;
    }
    
    // Método legacy para compatibilidade
    public float getDamageMultiplier(MobCategory category) {
        return getDamageMultiplier(cachedPlayerStore, category);
    }

    public float getMoneyMultiplier(Store<EntityStore> store) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (worldConfig == null || !isBloodMoonActive(store) || !worldConfig.bloodMoonMoneyMultiplierEnabled) {
            return 1.0f;
        }
        return worldConfig.bloodMoonMoneyMultiplier > 0.0f ? worldConfig.bloodMoonMoneyMultiplier : 2.0f;
    }

    public float getMoneyMultiplier() {
        return getMoneyMultiplier(cachedPlayerStore);
    }

    public void applyBloodMoonXpMultiplier(Store<EntityStore> store, Ref<EntityStore> ref, MobCategory category) {
        if (ref == null) {
            return;
        }

        if (!xpMultiplierCoordinator.usesPerEntityMultiplier()) {
            return;
        }

        if (store == null || category == null || category == MobCategory.NONE) {
            xpMultiplierCoordinator.clearEntity(ref);
            return;
        }

        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        boolean shouldApply = worldConfig != null
                && isWorldEnabledForStore(store)
                && isBloodMoonActive(store)
                && worldConfig.bloodMoonXpMultiplierEnabled
                && isBloodMoonAffected(worldConfig, category);

        float multiplier = shouldApply && worldConfig.bloodMoonXpMultiplier > 1.0f
                ? worldConfig.bloodMoonXpMultiplier
                : 1.0f;

        xpMultiplierCoordinator.syncEntity(ref, multiplier);
    }

    public float getBloodMoonXpMultiplierForStore(Store<EntityStore> store) {
        if (store == null || !isWorldEnabledForStore(store)) {
            return 1.0f;
        }

        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (worldConfig == null
                || !worldConfig.isBloodMoonActive()
                || !worldConfig.bloodMoonXpMultiplierEnabled) {
            return 1.0f;
        }

        return Math.max(1.0f, worldConfig.bloodMoonXpMultiplier);
    }

    private float resolveCategoryMultiplier(WorldHardcoreConfig worldConfig, MobCategory category, boolean health) {
        switch (category) {
            case PASSIVE:
                return resolveMultiplier(health ? worldConfig.passiveHealthMultiplier : worldConfig.passiveDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case CRITTER:
                return resolveMultiplier(health ? worldConfig.critterHealthMultiplier : worldConfig.critterDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case HOSTILE:
                return resolveMultiplier(health ? worldConfig.hostileHealthMultiplier : worldConfig.hostileDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case ELITE:
                return resolveMultiplier(health ? worldConfig.eliteHealthMultiplier : worldConfig.eliteDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case MINIBOSS:
                return resolveMultiplier(health ? worldConfig.minibossHealthMultiplier : worldConfig.minibossDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case WORLDBOSS:
                return resolveMultiplier(health ? worldConfig.worldbossHealthMultiplier : worldConfig.worldbossDamageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
            case NONE:
            default:
                return resolveMultiplier(health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier,
                        health ? worldConfig.healthMultiplier : worldConfig.damageMultiplier);
        }
    }

    public boolean isBloodMoonAffected(WorldHardcoreConfig worldConfig, MobCategory category) {
        if (worldConfig == null) return false;

        switch (category) {
            case HOSTILE:
                return worldConfig.bloodMoonHostileEnabled;
            case ELITE:
                return worldConfig.bloodMoonEliteEnabled;
            case MINIBOSS:
                return worldConfig.bloodMoonMinibossEnabled;
            case WORLDBOSS:
                return worldConfig.bloodMoonWorldbossEnabled;
            default:
                return false;
        }
    }
    
    // Método legacy
    public boolean isBloodMoonAffected(MobCategory category) {
        return isBloodMoonAffected(getWorldConfig(cachedPlayerStore), category);
    }

    public boolean isCategoryEnabled(WorldHardcoreConfig worldConfig, MobCategory category) {
        switch (category) {
            case PASSIVE:
                return worldConfig.passiveEnabled;
            case CRITTER:
                return worldConfig.critterEnabled;
            case HOSTILE:
                return worldConfig.hostileEnabled;
            case ELITE:
                return worldConfig.eliteEnabled;
            case MINIBOSS:
                return worldConfig.minibossEnabled;
            case WORLDBOSS:
                return worldConfig.worldbossEnabled;
            case NONE:
            default:
                return false;
        }
    }
    
    // Método legacy
    public boolean isCategoryEnabled(MobCategory category) {
        return isCategoryEnabled(getWorldConfig(cachedPlayerStore), category);
    }

    public boolean isMobEnabled(Store<EntityStore> store, MobCategory category) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (isBloodMoonActive(store) && isBloodMoonAffected(worldConfig, category)) {
            switch (category) {
                case ELITE:
                    return worldConfig.bloodMoonEliteEnabled;
                case MINIBOSS:
                    return worldConfig.bloodMoonMinibossEnabled;
                case WORLDBOSS:
                    return worldConfig.bloodMoonWorldbossEnabled;
                case HOSTILE:
                default:
                    return worldConfig.bloodMoonHostileEnabled;
            }
        }
        return isCategoryEnabled(worldConfig, category);
    }
    
    // Método legacy
    public boolean isMobEnabled(MobCategory category) {
        return isMobEnabled(cachedPlayerStore, category);
    }

    public boolean isBloodMoonActive(Store<EntityStore> store) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        return worldConfig != null && worldConfig.isBloodMoonActive();
    }
    
    // Método legacy - retorna true se qualquer mundo tem Blood Moon ativa
    public boolean isBloodMoonActive() {
        return isBloodMoonActive(cachedPlayerStore);
    }
    
    public float getBloodMoonProgress(Store<EntityStore> store) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (worldConfig == null || !worldConfig.isBloodMoonActive()) {
            return 0.0f;
        }
        
        Long startHour = worldConfig.getBloodMoonStartHourOfEpoch();
        Long endHour = worldConfig.getBloodMoonEndHourOfEpoch();
        if (startHour == null || endHour == null) {
            return 0.0f;
        }
        
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return 0.0f;
        
        long currentHourOfEpoch = getCurrentHourOfEpoch(time);
        long totalDuration = endHour - startHour;
        
        if (totalDuration <= 0) return 0.0f;
        
        long elapsed = currentHourOfEpoch - startHour;
        long remaining = endHour - currentHourOfEpoch;
        
        if (remaining <= 0) return 0.0f;
        if (elapsed < 0) return 1.0f;
        
        return Math.max(0.0f, Math.min(1.0f, (float) remaining / (float) totalDuration));
    }
    
    public int getBloodMoonHoursRemaining(Store<EntityStore> store) {
        WorldHardcoreConfig worldConfig = getWorldConfig(store);
        if (worldConfig == null || !worldConfig.isBloodMoonActive()) {
            return 0;
        }
        
        Long endHour = worldConfig.getBloodMoonEndHourOfEpoch();
        if (endHour == null) {
            return 0;
        }
        
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return 0;
        
        long currentHourOfEpoch = getCurrentHourOfEpoch(time);
        long remaining = endHour - currentHourOfEpoch;
        
        return Math.max(0, (int) remaining);
    }

    public void syncBloodMoonVisualsForPlayer(Player player, Store<EntityStore> store, String worldName) {
        if (player == null || store == null) {
            return;
        }
        String resolvedWorldName = worldName != null && !worldName.isBlank() ? worldName : getWorldName(store);
        boolean active;
        if (resolvedWorldName != null) {
            WorldHardcoreConfig worldConfig = getWorldConfig(resolvedWorldName);
            active = worldConfig != null && worldConfig.isBloodMoonActive()
                    && config.get().isWorldEnabled(resolvedWorldName);
        } else {
            active = isWorldEnabledForStore(store) && isBloodMoonActive(store);
        }
        bloodMoonVisuals.applyWorldVisuals(store, resolvedWorldName, active);
    }

    public void refreshBloodMoonState(Store<EntityStore> store, boolean applyToMobs) {
        String worldName = getWorldName(store);
        refreshBloodMoonState(store, worldName, applyToMobs);
    }
    
    public void refreshBloodMoonState(Store<EntityStore> store, String worldName, boolean applyToMobs) {
        if (store == null) return;
        
        // Verificar se o mundo está habilitado para HardcoreMode
        String resolvedWorldName = worldName != null ? worldName : getWorldName(store);
        
        // Blood Moon não é permitida no Forgotten Temple
        if ("Forgotten Temple".equals(resolvedWorldName)) {
            WorldHardcoreConfig worldConfig = worldName != null ? getWorldConfig(worldName) : getWorldConfig(store);
            if (worldConfig != null && worldConfig.isBloodMoonActive()) {
                worldConfig.setBloodMoonActive(false);
                worldConfig.clearBloodMoonState();
                // Remove do mapa de XP multipliers e recalcula
                if (resolvedWorldName != null) {
                    xpMultiplierCoordinator.clearWorld(resolvedWorldName);
                }
                bloodMoonVisuals.applyWorldVisuals(store, resolvedWorldName, false);
            }
            return;
        }
        
        if (resolvedWorldName != null && !config.get().isWorldEnabled(resolvedWorldName)) {
            // Mundo desabilitado - garantir que Blood Moon está desativada
            WorldHardcoreConfig worldConfig = worldName != null ? getWorldConfig(worldName) : getWorldConfig(store);
            if (worldConfig != null && worldConfig.isBloodMoonActive()) {
                worldConfig.setBloodMoonActive(false);
                worldConfig.clearBloodMoonState();
                // Remove do mapa de XP multipliers e recalcula
                xpMultiplierCoordinator.clearWorld(resolvedWorldName);
                bloodMoonVisuals.applyWorldVisuals(store, resolvedWorldName, false);
            }
            return;
        }
        
        WorldHardcoreConfig worldConfig;
        if (worldName != null) {
            worldConfig = getWorldConfig(worldName);
        } else {
            worldConfig = getWorldConfig(store);
        }
        if (worldConfig == null) return;
        
        boolean active = computeBloodMoonActive(store, worldConfig);
        boolean changed = active != worldConfig.isBloodMoonActive();

        if (changed) {
            if (!active) {
                worldConfig.clearBloodMoonState();
            }
        }

        worldConfig.setBloodMoonActive(active);
        
        // Sync RPG leveling apenas se houver mudança
        if (changed) {
            syncBloodMoonXpMultiplier(resolvedWorldName, active, worldConfig);
            announceBloodMoon(active, store);
            bloodMoonVisuals.applyWorldVisuals(store, resolvedWorldName, active);
            if (applyToMobs) {
                applyToExistingMobs(store);
            }
        }
    }

    public void forceBloodMoonNow(Store<EntityStore> store) {
        String worldName = getWorldName(store);
        forceBloodMoonNow(store, worldName);
    }
    
    public void forceBloodMoonNow(Store<EntityStore> store, String worldName) {
        if (store == null) return;

        // Verificar se o mundo está habilitado para HardcoreMode
        String resolvedWorldName = worldName != null ? worldName : getWorldName(store);
        
        // Blood Moon não é permitida no Forgotten Temple
        if ("Forgotten Temple".equals(resolvedWorldName)) {
            return;
        }
        
        if (resolvedWorldName != null && !config.get().isWorldEnabled(resolvedWorldName)) {
            // Mundo desabilitado - não permitir forçar Blood Moon
            return;
        }

        WorldHardcoreConfig worldConfig;
        if (worldName != null) {
            worldConfig = getWorldConfig(worldName);
        } else {
            worldConfig = getWorldConfig(store);
        }
        if (worldConfig == null) return;
        
        int durationHours = worldConfig.bloodMoonDurationHours;
        if (!isValidBloodMoonDuration(durationHours)) return;
        
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return;

        long currentHourOfEpoch = getCurrentHourOfEpoch(time);
        worldConfig.setForcedBloodMoonEndHourOfEpoch(currentHourOfEpoch + durationHours);

        refreshBloodMoonState(store, worldName, true);
    }

    public void refreshBloodMoonXpMultiplierState(Store<EntityStore> store, String worldName) {
        String resolvedWorldName = worldName != null ? worldName : getWorldName(store);
        if (resolvedWorldName == null) {
            return;
        }

        WorldHardcoreConfig worldConfig = getWorldConfig(resolvedWorldName);
        if (worldConfig == null) {
            xpMultiplierCoordinator.clearWorld(resolvedWorldName);
            return;
        }

        boolean active;
        if ("Forgotten Temple".equals(resolvedWorldName) || !config.get().isWorldEnabled(resolvedWorldName)) {
            active = false;
        } else if (store != null) {
            active = computeBloodMoonActive(store, worldConfig);
        } else {
            active = worldConfig.isBloodMoonActive();
        }

        syncBloodMoonXpMultiplier(resolvedWorldName, active, worldConfig);
    }

    public void refreshAllWorldBloodMoonStates(boolean applyToMobs) {
        Universe universe = Universe.get();
        if (universe == null) {
            return;
        }

        Map<String, World> worlds = universe.getWorlds();
        if (worlds == null || worlds.isEmpty()) {
            return;
        }

        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            World world = entry.getValue();
            if (world == null || world.getEntityStore() == null) {
                continue;
            }
            refreshBloodMoonState(world.getEntityStore().getStore(), entry.getKey(), applyToMobs);
        }
    }

    public void applyToExistingMobs(Store<EntityStore> store) {
        if (store == null) return;
        
        // Verificar se o HardcoreMode está habilitado para este mundo
        if (!isWorldEnabledForStore(store)) {
            return;
        }

        Ref<EntityStore> playerRef = getAnyPlayerRef(store);
        applyToExistingMobs(store, playerRef);
    }

    public void applyToExistingMobs(Store<EntityStore> store, Ref<EntityStore> playerRef) {
        if (store == null) {
            return;
        }
        
        // Verificar se o HardcoreMode está habilitado para este mundo
        if (!isWorldEnabledForStore(store)) {
            return;
        }
        
        if (playerRef == null || !playerRef.isValid()) {
            // Sem jogadores online, nada a fazer
            return;
        }

        cachedPlayerRef = playerRef;
        cachedPlayerStore = store;

        try {
            Query<EntityStore> query = Query.any();
            store.forEachChunk(query, (chunk, commandBuffer) -> {
                try {
                    applyToChunk(store, chunk, playerRef);
                } catch (Exception e) {
                    // Ignore chunk errors and continue
                }
                return true;
            });
        } catch (Exception e) {
            // Ignore errors when applying stats
        }
    }

    private void applyToChunk(Store<EntityStore> store, ArchetypeChunk<EntityStore> chunk, Ref<EntityStore> playerRef) {
        if (playerRef == null || !playerRef.isValid()) return;
        if (chunk == null) return;

        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        ComponentType<EntityStore, EntityStatMap> statType = EntityStatMap.getComponentType();
        if (statType == null) return;

        try {
            int size = chunk.size();
            for (int i = 0; i < size; i++) {
                try {
                    if (playerType != null && chunk.getComponent(i, playerType) != null) {
                        continue;
                    }

                    EntityStatMap statMap = chunk.getComponent(i, statType);
                    if (statMap == null) continue;

                    NPCEntity npcEntity = npcType == null ? null : chunk.getComponent(i, npcType);
                    MobCategory category = resolveMobCategory(npcEntity);
                    applyHealthModifier(store, statMap, category);
                } catch (Exception e) {
                    // Ignore individual entity errors
                }
            }
        } catch (Exception e) {
            // Ignore chunk processing errors
        }
    }

    private void normalizeConfig() {
        HardcoreModeConfig data = config.get();
        boolean changed = false;

        if (data.passiveHealthMultiplier <= 0.0f) { data.passiveHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.passiveDamageMultiplier <= 0.0f) { data.passiveDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.critterHealthMultiplier <= 0.0f) { data.critterHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.critterDamageMultiplier <= 0.0f) { data.critterDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.hostileHealthMultiplier <= 0.0f) { data.hostileHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.hostileDamageMultiplier <= 0.0f) { data.hostileDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.eliteHealthMultiplier <= 0.0f) { data.eliteHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.eliteDamageMultiplier <= 0.0f) { data.eliteDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.minibossHealthMultiplier <= 0.0f) { data.minibossHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.minibossDamageMultiplier <= 0.0f) { data.minibossDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.worldbossHealthMultiplier <= 0.0f) { data.worldbossHealthMultiplier = data.healthMultiplier; changed = true; }
        if (data.worldbossDamageMultiplier <= 0.0f) { data.worldbossDamageMultiplier = data.damageMultiplier; changed = true; }
        if (data.bloodMoonHostileHealthMultiplier <= 0.0f) { data.bloodMoonHostileHealthMultiplier = data.hostileHealthMultiplier; changed = true; }
        if (data.bloodMoonHostileDamageMultiplier <= 0.0f) { data.bloodMoonHostileDamageMultiplier = data.hostileDamageMultiplier; changed = true; }
        if (data.bloodMoonEliteHealthMultiplier <= 0.0f) { data.bloodMoonEliteHealthMultiplier = data.eliteHealthMultiplier; changed = true; }
        if (data.bloodMoonEliteDamageMultiplier <= 0.0f) { data.bloodMoonEliteDamageMultiplier = data.eliteDamageMultiplier; changed = true; }
        if (data.bloodMoonMinibossHealthMultiplier <= 0.0f) { data.bloodMoonMinibossHealthMultiplier = data.minibossHealthMultiplier; changed = true; }
        if (data.bloodMoonMinibossDamageMultiplier <= 0.0f) { data.bloodMoonMinibossDamageMultiplier = data.minibossDamageMultiplier; changed = true; }
        if (data.bloodMoonWorldbossHealthMultiplier <= 0.0f) { data.bloodMoonWorldbossHealthMultiplier = data.worldbossHealthMultiplier; changed = true; }
        if (data.bloodMoonWorldbossDamageMultiplier <= 0.0f) { data.bloodMoonWorldbossDamageMultiplier = data.worldbossDamageMultiplier; changed = true; }
        if (data.bloodMoonMoneyMultiplier <= 0.0f) { data.bloodMoonMoneyMultiplier = 2.0f; changed = true; }
        if (!isValidBloodMoonDuration(data.bloodMoonDurationHours)) { data.bloodMoonDurationHours = 3; changed = true; }

        if (changed) {
            config.save();
        }
    }

    private float resolveMultiplier(float categoryValue, float legacyValue) {
        return categoryValue > 0.0f ? categoryValue : legacyValue;
    }

    private boolean computeBloodMoonActive(Store<EntityStore> store, WorldHardcoreConfig worldConfig) {
        if (store == null || worldConfig == null) return false;

        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return false;

        long epochDay = time.getGameDateTime().toLocalDate().toEpochDay();
        int currentHour = time.getCurrentHour();
        long currentHourOfEpoch = (epochDay * 24L) + currentHour;

        Long forcedEnd = worldConfig.getForcedBloodMoonEndHourOfEpoch();
        if (forcedEnd != null) {
            if (currentHourOfEpoch < forcedEnd) {
                if (worldConfig.getBloodMoonStartHourOfEpoch() == null) {
                    worldConfig.setBloodMoonStartHourOfEpoch(currentHourOfEpoch);
                }
                worldConfig.setBloodMoonEndHourOfEpoch(forcedEnd);
                return true;
            }

            worldConfig.setForcedBloodMoonEndHourOfEpoch(null);
        }

        if (!worldConfig.bloodMoonEnabled) return false;

        int intervalDays = worldConfig.bloodMoonIntervalDays;
        if (intervalDays <= 0) return false;

        int durationHours = worldConfig.bloodMoonDurationHours;
        if (!isValidBloodMoonDuration(durationHours)) return false;

        int startHour = worldConfig.bloodMoonStartHour;
        if (startHour < 0) startHour = 0;
        else if (startHour > 23) startHour = 23;

        long scheduledDayCurrent = epochDay - Math.floorMod(epochDay, intervalDays);
        long startEpochCurrent = (scheduledDayCurrent * 24L) + startHour;
        long endEpochCurrent = startEpochCurrent + durationHours;

        long startEpochPrev = startEpochCurrent - (intervalDays * 24L);
        long endEpochPrev = startEpochPrev + durationHours;

        boolean activeCurrent = currentHourOfEpoch >= startEpochCurrent && currentHourOfEpoch < endEpochCurrent;
        boolean activePrev = currentHourOfEpoch >= startEpochPrev && currentHourOfEpoch < endEpochPrev;
        boolean active = activeCurrent || activePrev;
        
        if (active) {
            if (activeCurrent) {
                worldConfig.setBloodMoonStartHourOfEpoch(startEpochCurrent);
                worldConfig.setBloodMoonEndHourOfEpoch(endEpochCurrent);
            } else if (activePrev) {
                worldConfig.setBloodMoonStartHourOfEpoch(startEpochPrev);
                worldConfig.setBloodMoonEndHourOfEpoch(endEpochPrev);
            }
        }

        return active;
    }

    private void announceBloodMoon(boolean started, Store<EntityStore> store) {
        String worldName = getWorldName(store);
        String worldInfo = worldName != null ? " in " + worldName : "";
        String chatText = started ? "Blood Moon has begun" + worldInfo + "." : "Blood Moon has ended" + worldInfo + ".";
        
        // Anunciar apenas para jogadores no mesmo mundo via chat
        Universe universe = Universe.get();
        if (universe != null) {
            universe.sendMessage(Message.raw(chatText));
        }

        // Title apenas para jogadores no mundo específico
        Message title = Message.raw("Blood Moon");
        Message subtitle = Message.raw(started ? "has begun" : "has ended");
        
        // Usar showEventTitleToWorld para enviar título apenas para jogadores neste mundo
        if (store != null) {
            EventTitleUtil.showEventTitleToWorld(
                title,
                subtitle,
                true,  // darken
                EventTitleUtil.DEFAULT_ZONE,
                EventTitleUtil.DEFAULT_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION,
                EventTitleUtil.DEFAULT_FADE_DURATION,
                store
            );
        }
    }
    
    private boolean isValidBloodMoonDuration(int hours) {
        return hours == 1 || hours == 3 || hours == 6 || hours == 9 || hours == 12;
    }

    /**
     * Envia uma mensagem de erro para um jogador específico.
     */
    public void sendErrorMessage(PlayerRef playerRef, String text) {
        if (playerRef == null) return;
        try {
            playerRef.sendMessage(Message.raw(text));
        } catch (Exception ignored) {
            // Fallback se sendMessage não funcionar
        }
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

        Ref<EntityStore> cached = cachedPlayerRef;
        Store<EntityStore> cachedStore = cachedPlayerStore;

        if (cached != null && cachedStore == store && cached.isValid()) {
            try {
                if (store.getComponent(cached, playerType) != null) {
                    return cached;
                }
            } catch (Throwable t) {
                cachedPlayerRef = null;
                cachedPlayerStore = null;
            }
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

        Ref<EntityStore> ref = found.get();
        if (ref != null && ref.isValid()) {
            cachedPlayerRef = ref;
            cachedPlayerStore = store;
        }
        return ref;
    }

    public boolean isRpgLevelingAvailable() {
        if (!ensureRpgLevelingAccess()) return false;

        try {
            return rpgGetInstanceMethod.invoke(null) != null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    public boolean isXpMultiplierSupported() {
        return xpMultiplierCoordinator.isSupported();
    }

    public boolean usesEntityXpMultipliers() {
        return xpMultiplierCoordinator.usesPerEntityMultiplier();
    }

    public String getXpMultiplierStatusMessage() {
        return xpMultiplierCoordinator.getStatusMessage();
    }

    public String getMoneyRewardStatusMessage() {
        return vaultEconomyCoordinator.getStatusMessage();
    }

    private void syncBloodMoonXpMultiplier(String worldName, boolean active, WorldHardcoreConfig worldConfig) {
        boolean worldEnabled = worldName != null && config.get().isWorldEnabled(worldName);
        xpMultiplierCoordinator.syncWorld(worldName, active, worldConfig, worldEnabled);
    }

    private void syncRpgLevelingMultiplier(String worldName, boolean active, WorldHardcoreConfig worldConfig) {
        // Atualiza o mapa de mundos com XP multiplier
        if (worldName == null) {
            worldName = "unknown";
        }
        
        // Só adiciona ao mapa se:
        // 1. Blood Moon está ativa
        // 2. XP Multiplier está habilitado para este mundo
        // 3. HardcoreMode está habilitado para este mundo
        boolean isWorldEnabled = config.get().isWorldEnabled(worldName);
        
        if (active && worldConfig.bloodMoonXpMultiplierEnabled && isWorldEnabled) {
            // Adiciona ou atualiza este mundo no mapa
            worldsWithXpMultiplier.put(worldName, worldConfig.bloodMoonXpMultiplier);
        } else {
            // Remove este mundo do mapa (Blood Moon terminou, XP disabled, ou mundo desabilitado)
            worldsWithXpMultiplier.remove(worldName);
        }
        
        // Aplica o multiplicador de XP baseado no estado de TODOS os mundos habilitados
        applyGlobalXpMultiplier(worldName);
    }
    
    /**
     * Aplica o multiplicador de XP global.
     * IMPORTANTE: O RPGLeveling tem uma única configuração global de rateExp que afeta TODOS os mundos.
     * 
     * O multiplicador só é aplicado quando:
     * - O mundo tem HardcoreMode habilitado
     * - O mundo tem Blood Moon ativa
     * - O mundo tem XP Multiplier habilitado
     * 
     * LIMITAÇÃO TÉCNICA: Mesmo que apenas um mundo tenha Blood Moon, o multiplicador
     * de XP afetará TODOS os jogadores em TODOS os mundos. Isso é uma limitação
     * do RPGLeveling que não suporta configurações por mundo.
     */
    private void applyGlobalXpMultiplier(String changedWorldName) {
        Object levelingConfig = getRpgLevelingConfig();
        if (levelingConfig == null) {
            rpgLevelingBloodMoonApplied = false;
            rpgLevelingAppliedMultiplier = 1.0f;
            rpgLevelingBaseRateExp = null;
            worldsWithXpMultiplier.clear();
            return;
        }
        
        // Verifica se o mundo que mudou tem Blood Moon com XP multiplier
        Float worldMultiplier = worldsWithXpMultiplier.get(changedWorldName);
        
        // Se o mundo não tem Blood Moon com XP multiplier ativo
        if (worldMultiplier == null) {
            // Verifica se ainda há outros mundos com Blood Moon ativa
            if (worldsWithXpMultiplier.isEmpty()) {
                // Nenhum mundo tem Blood Moon - restaura o valor original
                if (rpgLevelingBloodMoonApplied && rpgLevelingBaseRateExp != null) {
                    setRpgRateExp(levelingConfig, rpgLevelingBaseRateExp);
                }
                rpgLevelingBloodMoonApplied = false;
                rpgLevelingAppliedMultiplier = 1.0f;
                rpgLevelingBaseRateExp = null;
            }
            // Se ainda há outros mundos com Blood Moon, mantém o multiplicador atual
            return;
        }
        
        // Salva o valor base original se ainda não tiver salvo
        if (!rpgLevelingBloodMoonApplied) {
            rpgLevelingBaseRateExp = getRpgRateExp(levelingConfig);
        }
        
        if (rpgLevelingBaseRateExp == null) return;
        
        float multiplier = worldMultiplier;
        
        // Se o multiplicador não mudou, não precisa aplicar novamente
        if (rpgLevelingBloodMoonApplied && Math.abs(rpgLevelingAppliedMultiplier - multiplier) <= 0.0001f) {
            return;
        }
        
        // Aplica o multiplicador do mundo que teve a mudança
        double target = rpgLevelingBaseRateExp * Math.max(0.0f, multiplier);
        if (setRpgRateExp(levelingConfig, target)) {
            rpgLevelingAppliedMultiplier = multiplier;
            rpgLevelingBloodMoonApplied = true;
        }
    }

    private Object getRpgLevelingConfig() {
        if (!ensureRpgLevelingAccess()) return null;

        try {
            Object pluginInstance = rpgGetInstanceMethod.invoke(null);
            if (pluginInstance == null) return null;

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
        if (levelingConfig == null) return null;

        try {
            Object result = rpgGetRateExpMethod.invoke(levelingConfig);
            if (result instanceof Double) return (Double) result;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
        return null;
    }

    private boolean setRpgRateExp(Object levelingConfig, double value) {
        if (levelingConfig == null) return false;

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

    public void setActiveStore(Store<EntityStore> store) {
        if (store != null) {
            activeStoreRef.set(store);
        }
    }
    
    /**
     * Obtém o nome do mundo a partir de um Store<EntityStore>.
     * Itera pelos mundos do Universe para encontrar o correspondente.
     * @param store O store do mundo
     * @return O nome do mundo, ou null se não encontrado
     */
    public String getWorldName(Store<EntityStore> store) {
        if (store == null) return null;
        
        // Verificar cache primeiro
        String cached = worldNameCache.get(store);
        if (cached != null) {
            return cached;
        }
        
        Universe universe = Universe.get();
        if (universe == null) return null;
        
        Map<String, World> worlds = universe.getWorlds();
        if (worlds == null) return null;
        
        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            World world = entry.getValue();
            if (world == null) continue;
            
            try {
                EntityStore worldEntityStore = world.getEntityStore();
                if (worldEntityStore == null) continue;
                
                // Comparar usando o store interno - tentar múltiplas formas de comparação
                Store<EntityStore> worldStore = worldEntityStore.getStore();
                if (worldStore == store) {
                    worldNameCache.put(store, entry.getKey());
                    return entry.getKey();
                }
                // Também tentar equals caso a referência direta não funcione
                if (worldStore != null && worldStore.equals(store)) {
                    worldNameCache.put(store, entry.getKey());
                    return entry.getKey();
                }
                // Tentar comparar hash codes como última alternativa
                if (worldStore != null && worldStore.hashCode() == store.hashCode()) {
                    // Verificação adicional para evitar falsos positivos
                    if (worldStore.getClass().equals(store.getClass())) {
                        worldNameCache.put(store, entry.getKey());
                        return entry.getKey();
                    }
                }
            } catch (Exception e) {
                // Ignore access errors
            }
        }
        
        return null;
    }
    
    /**
     * Verifica se o HardcoreMode está habilitado para o mundo do Store fornecido.
     * @param store O store do mundo
     * @return true se o mundo está habilitado para HardcoreMode
     */
    public boolean isWorldEnabledForStore(Store<EntityStore> store) {
        if (store == null) return false;
        
        String worldName = getWorldName(store);
        if (worldName == null) {
            // Se não conseguir identificar o mundo, assume que está habilitado
            return true;
        }
        
        return config.get().isWorldEnabled(worldName);
    }
}
