package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcorePlayerPresenceSystem extends RefChangeSystem<EntityStore, Player> {
    private final HardcoreModePlugin plugin;

    public HardcorePlayerPresenceSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Query<EntityStore> getQuery() {
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        return playerType == null ? Query.any() : playerType;
    }

    @Override
    public ComponentType<EntityStore, Player> componentType() {
        return Player.getComponentType();
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            Player component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        plugin.refreshBloodMoonStateIfNeeded(store, true);
        plugin.applyToExistingMobs(store, ref);
        plugin.syncBloodMoonVisualsForPlayer(component, store, plugin.getWorldName(store));
        
    }

    @Override
    public void onComponentSet(
            Ref<EntityStore> ref,
            Player previous,
            Player current,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        plugin.refreshBloodMoonStateIfNeeded(store, true);
        plugin.applyToExistingMobs(store, ref);
        plugin.syncBloodMoonVisualsForPlayer(current, store, plugin.getWorldName(store));
    }

    @Override
    public void onComponentRemoved(
            Ref<EntityStore> ref,
            Player component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
    }
}
