package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.config.HardcoreModeConfig;
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

import java.util.Set;

public class HardcorePlayerDeathConfigSystem extends DeathSystems.OnDeathSystem {
    private final HardcoreModePlugin plugin;

    public HardcorePlayerDeathConfigSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
                new SystemDependency<>(Order.AFTER, DeathSystems.PlayerDropItemsConfig.class),
                new SystemDependency<>(Order.BEFORE, DeathSystems.DropPlayerDeathItems.class)
        );
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            DeathComponent component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        HardcoreModeConfig config = plugin.getConfigData();
        if (!config.playerDeathSettingsEnabled) {
            return;
        }

        int durabilityPercent = clampPercent(config.playerItemDurabilityLossPercent);
        int dropPercent = clampPercent(config.playerItemDropPercent);

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
