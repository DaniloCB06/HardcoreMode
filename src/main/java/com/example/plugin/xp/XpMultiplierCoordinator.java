package com.example.plugin.xp;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.config.WorldHardcoreConfig;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public final class XpMultiplierCoordinator {
    public static final String CONFLICT_MESSAGE =
            "There must only be one MOD with experience leveling for the setup to work.";

    private static final PluginIdentifier RPG_LEVELING_ID = PluginIdentifier.fromString("Zuxaw:RPGLeveling");
    private static final PluginIdentifier ENDLESS_LEVELING_ID = PluginIdentifier.fromString("Airijko:EndlessLevelingCore");

    private final HardcoreModePlugin plugin;
    private final Map<String, Float> worldsWithXpMultiplier = new ConcurrentHashMap<>();
    private final RpgLevelingProvider rpgLevelingProvider = new RpgLevelingProvider();
    private final EndlessLevelingProvider endlessLevelingProvider = new EndlessLevelingProvider();

    public XpMultiplierCoordinator(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        getActiveProvider();
    }

    public void close() {
        endlessLevelingProvider.close();
        restoreAllProviders();
    }

    public boolean isSupported() {
        return getActiveProvider() != null;
    }

    public boolean hasConflict() {
        return isPluginPresent(RPG_LEVELING_ID) && isPluginPresent(ENDLESS_LEVELING_ID);
    }

    public boolean isRpgLevelingAvailable() {
        return isPluginPresent(RPG_LEVELING_ID)
                && !isPluginPresent(ENDLESS_LEVELING_ID)
                && rpgLevelingProvider.isReady();
    }

    public String getStatusMessage() {
        if (hasConflict()) {
            return CONFLICT_MESSAGE;
        }
        if (isSupported()) {
            return "";
        }
        if (isPluginPresent(RPG_LEVELING_ID)) {
            return "RPGLeveling mod found but its XP API could not be accessed.";
        }
        if (isPluginPresent(ENDLESS_LEVELING_ID)) {
            return "EndlessLevelingCore mod found but its XP API could not be accessed.";
        }
        return "No supported XP leveling mod found.";
    }

    public void syncWorld(String worldName, boolean active, WorldHardcoreConfig worldConfig, boolean worldEnabled) {
        if (worldConfig == null) {
            clearWorld(worldName);
            return;
        }

        String normalizedWorldName = normalizeWorldName(worldName);
        boolean shouldApply = active && worldEnabled && worldConfig.bloodMoonXpMultiplierEnabled;
        if (shouldApply) {
            worldsWithXpMultiplier.put(normalizedWorldName, worldConfig.bloodMoonXpMultiplier);
        } else {
            worldsWithXpMultiplier.remove(normalizedWorldName);
        }

        XpProvider provider = getActiveProvider();
        if (provider == endlessLevelingProvider) {
            rpgLevelingProvider.restoreBaseValue();
            endlessLevelingProvider.restoreBaseValue();
            return;
        }

        applyGlobalXpMultiplier();
    }

    public void clearWorld(String worldName) {
        String normalizedWorldName = normalizeWorldName(worldName);
        worldsWithXpMultiplier.remove(normalizedWorldName);

        XpProvider provider = getActiveProvider();
        if (provider == endlessLevelingProvider) {
            rpgLevelingProvider.restoreBaseValue();
            endlessLevelingProvider.restoreBaseValue();
            return;
        }

        applyGlobalXpMultiplier();
    }

    private void applyGlobalXpMultiplier() {
        XpProvider provider = getActiveProvider();
        if (provider == null) {
            restoreAllProviders();
            return;
        }

        restoreInactiveProviders(provider);
        if (provider != rpgLevelingProvider) {
            return;
        }

        Float multiplier = getEffectiveMultiplier();
        if (multiplier == null) {
            provider.restoreBaseValue();
            return;
        }

        provider.applyMultiplier(multiplier);
    }

    private Float getEffectiveMultiplier() {
        Float multiplier = null;
        for (Float candidate : worldsWithXpMultiplier.values()) {
            if (candidate == null) {
                continue;
            }
            if (multiplier == null || candidate > multiplier) {
                multiplier = candidate;
            }
        }
        return multiplier;
    }

    private void restoreAllProviders() {
        rpgLevelingProvider.restoreBaseValue();
        endlessLevelingProvider.restoreBaseValue();
    }

    private void restoreInactiveProviders(XpProvider activeProvider) {
        if (activeProvider != rpgLevelingProvider) {
            rpgLevelingProvider.restoreBaseValue();
        }
        if (activeProvider != endlessLevelingProvider) {
            endlessLevelingProvider.restoreBaseValue();
        }
    }

    private XpProvider getActiveProvider() {
        boolean hasRpg = isPluginPresent(RPG_LEVELING_ID);
        boolean hasEndless = isPluginPresent(ENDLESS_LEVELING_ID);
        if (hasRpg && hasEndless) {
            return null;
        }
        if (hasRpg && rpgLevelingProvider.isReady()) {
            return rpgLevelingProvider;
        }
        if (hasEndless && endlessLevelingProvider.isReady()) {
            return endlessLevelingProvider;
        }
        return null;
    }

    private boolean isPluginPresent(PluginIdentifier identifier) {
        return getLoadedPlugin(identifier) != null;
    }

    private PluginBase getLoadedPlugin(PluginIdentifier identifier) {
        PluginManager pluginManager = PluginManager.get();
        if (pluginManager == null || identifier == null) {
            return null;
        }
        try {
            return pluginManager.getPlugin(identifier);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private String normalizeWorldName(String worldName) {
        return worldName == null || worldName.isBlank() ? "unknown" : worldName;
    }

    private void logWarning(String message, Throwable error) {
        if (error == null) {
            plugin.getLogger().at(Level.WARNING).log("%s", message);
            return;
        }
        plugin.getLogger().at(Level.WARNING).log(
                "%s (%s: %s)",
                message,
                error.getClass().getSimpleName(),
                error.getMessage()
        );
    }

    public boolean usesPerEntityMultiplier() {
        return getActiveProvider() == endlessLevelingProvider;
    }

    public void syncEntity(Ref<EntityStore> ref, float multiplier) {
        if (!usesPerEntityMultiplier()) {
            return;
        }

        endlessLevelingProvider.applyEntityMultiplier(ref, multiplier);
    }

    public void clearEntity(Ref<EntityStore> ref) {
        if (!usesPerEntityMultiplier()) {
            return;
        }

        endlessLevelingProvider.clearEntityMultiplier(ref);
    }

    private interface XpProvider {
        boolean isReady();

        boolean applyMultiplier(float multiplier);

        boolean restoreBaseValue();
    }

    private final class RpgLevelingProvider implements XpProvider {
        private static final String RPG_LEVELING_PLUGIN_CLASS = "org.zuxaw.plugin.RPGLevelingPlugin";
        private static final String RPG_LEVELING_CONFIG_CLASS = "org.zuxaw.plugin.config.LevelingConfig";

        private boolean checked;
        private boolean available;
        private Method getInstanceMethod;
        private Method getConfigMethod;
        private Method getRateExpMethod;
        private Method setRateExpMethod;
        private Double baseRateExp;
        private float appliedMultiplier = 1.0f;
        private boolean applied;

        @Override
        public boolean isReady() {
            if (!ensureAccess()) {
                return false;
            }

            try {
                return getInstanceMethod.invoke(null) != null;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return false;
            }
        }

        @Override
        public boolean applyMultiplier(float multiplier) {
            Object levelingConfig = getLevelingConfig();
            if (levelingConfig == null) {
                clearState();
                return false;
            }

            if (!applied) {
                baseRateExp = getRateExp(levelingConfig);
            }
            if (baseRateExp == null) {
                return false;
            }

            float normalizedMultiplier = Math.max(0.0f, multiplier);
            if (applied && Math.abs(appliedMultiplier - normalizedMultiplier) <= 0.0001f) {
                return true;
            }

            double target = baseRateExp * normalizedMultiplier;
            if (!setRateExp(levelingConfig, target)) {
                return false;
            }

            appliedMultiplier = normalizedMultiplier;
            applied = true;
            return true;
        }

        @Override
        public boolean restoreBaseValue() {
            if (!applied) {
                clearState();
                return true;
            }

            Object levelingConfig = getLevelingConfig();
            if (levelingConfig == null || baseRateExp == null) {
                clearState();
                return false;
            }

            boolean restored = setRateExp(levelingConfig, baseRateExp);
            clearState();
            return restored;
        }

        private void clearState() {
            applied = false;
            appliedMultiplier = 1.0f;
            baseRateExp = null;
        }

        private Object getLevelingConfig() {
            if (!ensureAccess()) {
                return null;
            }

            try {
                Object pluginInstance = getInstanceMethod.invoke(null);
                if (pluginInstance == null) {
                    return null;
                }

                Object configWrapper = getConfigMethod.invoke(pluginInstance);
                if (configWrapper instanceof Config) {
                    return ((Config<?>) configWrapper).get();
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }

            return null;
        }

        private Double getRateExp(Object levelingConfig) {
            if (levelingConfig == null) {
                return null;
            }

            try {
                Object result = getRateExpMethod.invoke(levelingConfig);
                if (result instanceof Number) {
                    return ((Number) result).doubleValue();
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
            return null;
        }

        private boolean setRateExp(Object levelingConfig, double value) {
            if (levelingConfig == null) {
                return false;
            }

            try {
                setRateExpMethod.invoke(levelingConfig, value);
                return true;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return false;
            }
        }

        private boolean ensureAccess() {
            if (checked) {
                return available;
            }

            checked = true;
            try {
                Class<?> pluginClass = Class.forName(RPG_LEVELING_PLUGIN_CLASS);
                Class<?> configClass = Class.forName(RPG_LEVELING_CONFIG_CLASS);
                getInstanceMethod = pluginClass.getMethod("get");
                getConfigMethod = pluginClass.getMethod("getConfig");
                getRateExpMethod = configClass.getMethod("getRateExp");
                setRateExpMethod = configClass.getMethod("setRateExp", double.class);
                available = true;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                available = false;
            }

            return available;
        }
    }

    private final class EndlessLevelingProvider implements XpProvider {
        private static final String API_CLASS = "com.airijko.endlessleveling.api.EndlessLevelingAPI";
        private static final String LEVELING_MANAGER_CLASS =
                "com.airijko.endlessleveling.leveling.LevelingManager";
        private static final String XP_SOURCE_CLASS = "com.airijko.endlessleveling.xpstats.XpSource";

        private final BiConsumer<UUID, Double> xpGrantListener = this::onXpGranted;
        private final ThreadLocal<Boolean> applyingFallback = ThreadLocal.withInitial(() -> false);

        private boolean checked;
        private boolean available;
        private boolean listenerRegistered;
        private boolean readbackWarningLogged;
        private Method apiGetMethod;
        private Method setEntityXpMultiplierMethod;
        private Method clearEntityXpMultiplierMethod;
        private Method getEntityXpMultiplierMethod;
        private Method addXpGrantListenerMethod;
        private Method removeXpGrantListenerMethod;
        private Method getCurrentGrantSourceNameMethod;
        private Method apiLevelingManagerMethod;
        private Method adjustRawXpMethod;
        private Method xpSourceValueOfMethod;
        private Field currentMobKillGrantField;
        private Field mobKillGrantStoreField;
        private Field mobKillGrantEntityMultiplierField;

        @Override
        public boolean isReady() {
            Object api = getApiInstance();
            return api != null && ensureXpGrantListener(api);
        }

        @Override
        public boolean applyMultiplier(float multiplier) {
            return true;
        }

        @Override
        public boolean restoreBaseValue() {
            return true;
        }

        private Object getApiInstance() {
            if (!ensureApiAccess()) {
                return null;
            }

            try {
                return apiGetMethod.invoke(null);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }

        private void applyEntityMultiplier(Ref<EntityStore> ref, float multiplier) {
            if (!ensureApiAccess() || ref == null || !ref.isValid()) {
                return;
            }

            int entityIndex = ref.getIndex();
            if (entityIndex < 0) {
                return;
            }

            Object api = getApiInstance();
            if (api == null) {
                return;
            }

            double normalizedMultiplier = Double.isFinite(multiplier) && multiplier > 1.0f
                    ? multiplier
                    : 1.0d;

            try {
                if (normalizedMultiplier <= 1.0d) {
                    clearEntityXpMultiplierMethod.invoke(api, entityIndex);
                } else {
                    setEntityXpMultiplierMethod.invoke(api, entityIndex, normalizedMultiplier);
                }
                verifyEntityMultiplier(api, entityIndex, normalizedMultiplier);
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to sync EndlessLeveling entity XP multiplier", error);
            }
        }

        private void verifyEntityMultiplier(Object api, int entityIndex, double expected) throws ReflectiveOperationException {
            Object result = getEntityXpMultiplierMethod.invoke(api, entityIndex);
            double actual = result instanceof Number number ? number.doubleValue() : 1.0d;
            if (!readbackWarningLogged && Math.abs(actual - expected) > 0.0001d) {
                readbackWarningLogged = true;
                logWarning("EndlessLeveling did not retain the entity XP multiplier", null);
            }
        }

        private void clearEntityMultiplier(Ref<EntityStore> ref) {
            if (!ensureApiAccess() || ref == null || !ref.isValid()) {
                return;
            }

            int entityIndex = ref.getIndex();
            Object api = entityIndex < 0 ? null : getApiInstance();
            if (api == null) {
                return;
            }

            try {
                clearEntityXpMultiplierMethod.invoke(api, entityIndex);
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to clear EndlessLeveling entity XP multiplier", error);
            }
        }

        private synchronized boolean ensureXpGrantListener(Object api) {
            if (listenerRegistered) {
                return true;
            }

            try {
                addXpGrantListenerMethod.invoke(api, xpGrantListener);
                listenerRegistered = true;
                return true;
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to register the EndlessLeveling XP integration", error);
                return false;
            }
        }

        private void onXpGranted(UUID playerUuid, Double grantedXp) {
            if (Boolean.TRUE.equals(applyingFallback.get())
                    || playerUuid == null
                    || grantedXp == null
                    || !Double.isFinite(grantedXp)
                    || grantedXp <= 0.0d) {
                return;
            }

            Object api = getApiInstance();
            if (api == null) {
                return;
            }

            try {
                String sourceName = (String) getCurrentGrantSourceNameMethod.invoke(api);
                if (!isMobKillSource(sourceName)) {
                    return;
                }

                Object context = getCurrentMobKillGrant(api);
                if (context == null) {
                    return;
                }

                @SuppressWarnings("unchecked")
                Store<EntityStore> store = (Store<EntityStore>) mobKillGrantStoreField.get(context);
                double desiredMultiplier = plugin.getBloodMoonXpMultiplierForStore(store);
                double appliedMultiplier = mobKillGrantEntityMultiplierField.getDouble(context);
                if (!Double.isFinite(appliedMultiplier) || appliedMultiplier <= 0.0d) {
                    appliedMultiplier = 1.0d;
                }

                if (desiredMultiplier <= appliedMultiplier + 0.0001d) {
                    return;
                }

                double missingXp = grantedXp * ((desiredMultiplier / appliedMultiplier) - 1.0d);
                if (!Double.isFinite(missingXp) || missingXp <= 0.0d) {
                    return;
                }

                Object levelingManager = apiLevelingManagerMethod.invoke(api);
                Object xpSource = xpSourceValueOfMethod.invoke(null, sourceName);
                if (levelingManager == null || xpSource == null) {
                    return;
                }

                applyingFallback.set(true);
                adjustRawXpMethod.invoke(levelingManager, playerUuid, missingXp, xpSource);
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to apply the EndlessLeveling Blood Moon XP fallback", error);
            } finally {
                applyingFallback.remove();
            }
        }

        private Object getCurrentMobKillGrant(Object api) throws IllegalAccessException {
            Object value = currentMobKillGrantField.get(api);
            return value instanceof ThreadLocal<?> threadLocal ? threadLocal.get() : null;
        }

        private boolean isMobKillSource(String sourceName) {
            return "MOB_KILL".equals(sourceName)
                    || "PARTY_KILL".equals(sourceName)
                    || "PARTY_SHARE".equals(sourceName);
        }

        private synchronized void close() {
            if (!listenerRegistered) {
                return;
            }

            Object api = getApiInstance();
            if (api != null) {
                try {
                    removeXpGrantListenerMethod.invoke(api, xpGrantListener);
                } catch (ReflectiveOperationException | RuntimeException error) {
                    logWarning("Failed to unregister the EndlessLeveling XP integration", error);
                }
            }
            listenerRegistered = false;
        }

        private boolean ensureApiAccess() {
            if (checked) {
                return available;
            }

            checked = true;
            try {
                Class<?> apiClass = Class.forName(API_CLASS);
                Class<?> levelingManagerClass = Class.forName(LEVELING_MANAGER_CLASS);
                Class<?> xpSourceClass = Class.forName(XP_SOURCE_CLASS);
                Class<?> grantContextClass = Class.forName(API_CLASS + "$MobKillGrantContext");

                apiGetMethod = apiClass.getMethod("get");
                setEntityXpMultiplierMethod = apiClass.getMethod("setEntityXpMultiplier", int.class, double.class);
                clearEntityXpMultiplierMethod = apiClass.getMethod("clearEntityXpMultiplier", int.class);
                getEntityXpMultiplierMethod = apiClass.getMethod("getEntityXpMultiplier", int.class);
                addXpGrantListenerMethod = apiClass.getMethod("addXpGrantListener", BiConsumer.class);
                removeXpGrantListenerMethod = apiClass.getMethod("removeXpGrantListener", BiConsumer.class);
                getCurrentGrantSourceNameMethod = apiClass.getMethod("getCurrentGrantSourceName");

                apiLevelingManagerMethod = apiClass.getDeclaredMethod("levelingManager");
                apiLevelingManagerMethod.setAccessible(true);
                adjustRawXpMethod = levelingManagerClass.getMethod(
                        "adjustRawXp",
                        UUID.class,
                        double.class,
                        xpSourceClass
                );
                xpSourceValueOfMethod = xpSourceClass.getMethod("valueOf", String.class);

                currentMobKillGrantField = apiClass.getDeclaredField("currentMobKillGrant");
                currentMobKillGrantField.setAccessible(true);
                mobKillGrantStoreField = grantContextClass.getDeclaredField("store");
                mobKillGrantStoreField.setAccessible(true);
                mobKillGrantEntityMultiplierField = grantContextClass.getDeclaredField("entityXpMult");
                mobKillGrantEntityMultiplierField.setAccessible(true);
                available = true;
            } catch (ReflectiveOperationException | RuntimeException error) {
                available = false;
                logWarning("Failed to access EndlessLeveling runtime XP API", error);
            }

            return available;
        }
    }
}
