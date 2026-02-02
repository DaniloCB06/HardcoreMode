package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.config.WorldHardcoreConfig;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.LinkedHashSet;
import java.util.Set;

public class HardcorePlayerDeathConfigSystem extends DeathSystems.OnDeathSystem {
    public enum DependencyMode {
        STRICT,
        AFTER_CONFIG,
        NONE
    }

    private static final String PLAYER_DROP_CONFIG_CLASS =
            "com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems$PlayerDropItemsConfig";
    private static final String DROP_PLAYER_DEATH_ITEMS_CLASS =
            "com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems$DropPlayerDeathItems";

    private final HardcoreModePlugin plugin;
    private final DependencyMode dependencyMode;

    public HardcorePlayerDeathConfigSystem(HardcoreModePlugin plugin, DependencyMode dependencyMode) {
        this.plugin = plugin;
        this.dependencyMode = dependencyMode;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        Set<Dependency<EntityStore>> deps = new LinkedHashSet<>();

        if (dependencyMode != DependencyMode.NONE) {
            addSystemDependencyIfPresent(deps, Order.AFTER, PLAYER_DROP_CONFIG_CLASS);
        }

        if (dependencyMode == DependencyMode.STRICT) {
            addSystemDependencyIfPresent(deps, Order.BEFORE, DROP_PLAYER_DEATH_ITEMS_CLASS);
        }

        return deps;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addSystemDependencyIfPresent(Set<Dependency<EntityStore>> deps, Order order, String className) {
        Class<?> clazz = tryLoad(className);
        if (clazz != null) {
            deps.add(new SystemDependency(order, (Class) clazz));
        }
    }

    private static Class<?> tryLoad(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            DeathComponent component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        // Use world-specific config instead of global config
        WorldHardcoreConfig config = plugin.getWorldConfig(store);
        if (config == null) {
            return;
        }
        
        // Check if Blood Moon is active for this world
        boolean isBloodMoonActive = plugin.isBloodMoonActive(store);
        
        // Determine which settings to use
        // Priority: Blood Moon Death Settings > Player Death Settings (during Blood Moon)
        // Outside Blood Moon: Only Player Death Settings apply
        int durabilityPercent;
        int dropPercent;
        boolean shouldApply = false;
        
        if (isBloodMoonActive) {
            // During Blood Moon: Blood Moon settings have priority if enabled
            if (config.bloodMoonDeathSettingsEnabled) {
                // Use Blood Moon specific settings
                durabilityPercent = clampPercent(config.bloodMoonItemDurabilityLossPercent);
                dropPercent = clampPercent(config.bloodMoonItemDropPercent);
                shouldApply = true;
            } else if (config.playerDeathSettingsEnabled) {
                // Blood Moon Death Settings OFF, but Player Death Settings ON
                // Use Player Death Settings during Blood Moon
                durabilityPercent = clampPercent(config.playerItemDurabilityLossPercent);
                dropPercent = clampPercent(config.playerItemDropPercent);
                shouldApply = true;
            } else {
                // Both disabled, don't apply custom settings
                return;
            }
        } else {
            // Outside Blood Moon: Only Player Death Settings apply
            if (config.playerDeathSettingsEnabled) {
                durabilityPercent = clampPercent(config.playerItemDurabilityLossPercent);
                dropPercent = clampPercent(config.playerItemDropPercent);
                shouldApply = true;
            } else {
                // Player Death Settings disabled, don't apply custom settings
                return;
            }
        }
        
        if (!shouldApply) {
            return;
        }

        component.setItemsDurabilityLossPercentage(durabilityPercent);

        DeathConfig.ItemsLossMode mode;
        if (dropPercent <= 0) {
            mode = DeathConfig.ItemsLossMode.NONE;
        } else if (dropPercent >= 100) {
            mode = DeathConfig.ItemsLossMode.ALL;
        } else {
            mode = DeathConfig.ItemsLossMode.CONFIGURED;
        }

        component.setItemsLossMode(mode);
        component.setItemsAmountLossPercentage(mode == DeathConfig.ItemsLossMode.ALL ? 100.0 : dropPercent);
    }

    private int clampPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
