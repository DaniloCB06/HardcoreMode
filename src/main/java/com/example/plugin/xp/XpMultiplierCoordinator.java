package com.example.plugin.xp;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.config.WorldHardcoreConfig;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.util.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
            if (shouldApply) {
                endlessLevelingProvider.applyWorldMultiplier(normalizedWorldName, worldConfig.bloodMoonXpMultiplier);
            } else {
                endlessLevelingProvider.clearWorldMultiplier(normalizedWorldName);
            }
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
            endlessLevelingProvider.clearWorldMultiplier(normalizedWorldName);
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

        private boolean checked;
        private boolean available;
        private Method apiGetMethod;
        private Method setWorldXpMultiplierMethod;
        private Method getWorldOverrideValueByWorldNameMethod;
        private Field runtimeWorldOverridesField;
        private final Set<String> managedWorlds = ConcurrentHashMap.newKeySet();
        private final Set<String> snapshotWorlds = ConcurrentHashMap.newKeySet();
        private final Set<String> worldsWithPreviousExperienceOverride = ConcurrentHashMap.newKeySet();
        private final Map<String, Object> previousExperienceOverrides = new ConcurrentHashMap<>();

        @Override
        public boolean isReady() {
            if (!ensureApiAccess()) {
                return false;
            }

            try {
                return apiGetMethod.invoke(null) != null;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return false;
            }
        }

        @Override
        public boolean applyMultiplier(float multiplier) {
            boolean applied = true;
            for (Map.Entry<String, Float> entry : worldsWithXpMultiplier.entrySet()) {
                Float worldMultiplier = entry.getValue();
                if (worldMultiplier == null) {
                    continue;
                }
                applied &= applyWorldMultiplier(entry.getKey(), worldMultiplier);
            }
            return applied;
        }

        @Override
        public boolean restoreBaseValue() {
            boolean restored = true;
            for (String worldName : new ArrayList<>(snapshotWorlds)) {
                restored &= restoreWorldSnapshot(worldName);
            }
            managedWorlds.clear();
            snapshotWorlds.clear();
            worldsWithPreviousExperienceOverride.clear();
            previousExperienceOverrides.clear();
            return restored;
        }

        private boolean applyWorldMultiplier(String worldName, float multiplier) {
            if (!ensureApiAccess() || worldName == null || worldName.isBlank()) {
                return false;
            }

            Object api = getApiInstance();
            if (api == null) {
                return false;
            }

            snapshotWorldOverrideIfNeeded(api, worldName);

            try {
                setWorldXpMultiplierMethod.invoke(api, worldName, (double) Math.max(0.0f, multiplier));
                managedWorlds.add(worldName);
                return true;
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to apply EndlessLeveling world XP multiplier", error);
                return false;
            }
        }

        private boolean clearWorldMultiplier(String worldName) {
            if (worldName == null || worldName.isBlank()) {
                return true;
            }
            return restoreWorldSnapshot(worldName);
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

        private void snapshotWorldOverrideIfNeeded(Object api, String worldName) {
            if (api == null || !snapshotWorlds.add(worldName)) {
                return;
            }

            Object previousOverride = readWorldExperienceOverride(api, worldName);
            if (previousOverride != null) {
                worldsWithPreviousExperienceOverride.add(worldName);
                previousExperienceOverrides.put(worldName, deepCopyValue(previousOverride));
            }
        }

        private Object readWorldExperienceOverride(Object api, String worldName) {
            if (api == null || worldName == null || worldName.isBlank()) {
                return null;
            }

            try {
                return getWorldOverrideValueByWorldNameMethod.invoke(api, worldName, "Experience");
            } catch (ReflectiveOperationException | RuntimeException error) {
                logWarning("Failed to read EndlessLeveling world XP override", error);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private boolean restoreWorldSnapshot(String worldName) {
            if (!ensureApiAccess() || worldName == null || worldName.isBlank()) {
                return false;
            }

            Object api = getApiInstance();
            if (api == null) {
                return false;
            }

            managedWorlds.remove(worldName);
            snapshotWorlds.remove(worldName);

            try {
                Object rawOverrides = runtimeWorldOverridesField.get(api);
                if (!(rawOverrides instanceof Map<?, ?>)) {
                    return false;
                }

                Map<String, Object> runtimeWorldOverrides = (Map<String, Object>) rawOverrides;
                Object worldOverridesObject = runtimeWorldOverrides.get(worldName);
                if (worldOverridesObject instanceof Map<?, ?>) {
                    Map<String, Object> worldOverrides = (Map<String, Object>) worldOverridesObject;
                    if (worldsWithPreviousExperienceOverride.remove(worldName)) {
                        Object previous = previousExperienceOverrides.remove(worldName);
                        worldOverrides.put("Experience", deepCopyValue(previous));
                    } else {
                        worldOverrides.remove("Experience");
                    }

                    if (worldOverrides.isEmpty()) {
                        runtimeWorldOverrides.remove(worldName);
                    }
                } else if (worldsWithPreviousExperienceOverride.remove(worldName)) {
                    Map<String, Object> replacement = new LinkedHashMap<>();
                    replacement.put("Experience", deepCopyValue(previousExperienceOverrides.remove(worldName)));
                    runtimeWorldOverrides.put(worldName, replacement);
                } else {
                    previousExperienceOverrides.remove(worldName);
                }

                return true;
            } catch (IllegalAccessException | RuntimeException error) {
                logWarning("Failed to restore EndlessLeveling world XP override", error);
                return false;
            }
        }

        private Object deepCopyValue(Object value) {
            if (value instanceof Map<?, ?> sourceMap) {
                Map<String, Object> copy = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                    if (entry.getKey() != null) {
                        copy.put(String.valueOf(entry.getKey()), deepCopyValue(entry.getValue()));
                    }
                }
                return copy;
            }
            return value;
        }

        private boolean ensureApiAccess() {
            if (checked) {
                return available;
            }

            checked = true;
            try {
                Class<?> apiClass = Class.forName(API_CLASS);
                apiGetMethod = apiClass.getMethod("get");
                setWorldXpMultiplierMethod = apiClass.getMethod("setWorldXpMultiplier", String.class, double.class);
                getWorldOverrideValueByWorldNameMethod = apiClass.getMethod(
                        "getWorldOverrideValueByWorldName",
                        String.class,
                        String.class
                );
                runtimeWorldOverridesField = apiClass.getDeclaredField("runtimeWorldOverrides");
                runtimeWorldOverridesField.setAccessible(true);
                available = true;
            } catch (ReflectiveOperationException | RuntimeException error) {
                available = false;
                logWarning("Failed to access EndlessLeveling runtime XP API", error);
            }

            return available;
        }
    }
}
