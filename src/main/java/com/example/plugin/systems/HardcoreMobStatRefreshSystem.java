package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobDisposition;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

public class HardcoreMobStatRefreshSystem extends RefChangeSystem<EntityStore, EntityStatMap> {
    private final HardcoreModePlugin plugin;

    public HardcoreMobStatRefreshSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Query<EntityStore> getQuery() {
        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        return npcType == null ? Query.any() : npcType;
    }

    @Override
    public ComponentType<EntityStore, EntityStatMap> componentType() {
        return EntityStatMap.getComponentType();
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            EntityStatMap component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        apply(ref, component, store);
    }

    @Override
    public void onComponentSet(
            Ref<EntityStore> ref,
            EntityStatMap previous,
            EntityStatMap current,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        apply(ref, current, store);
    }

    @Override
    public void onComponentRemoved(
            Ref<EntityStore> ref,
            EntityStatMap component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        // No cleanup needed.
    }

    private void apply(Ref<EntityStore> ref, EntityStatMap statMap, Store<EntityStore> store) {
        if (store == null || ref == null || !ref.isValid() || statMap == null) {
            return;
        }

        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType != null && store.getComponent(ref, playerType) != null) {
            return;
        }

        Ref<EntityStore> playerRef = plugin.getAnyPlayerRef(store);
        if (playerRef == null || !playerRef.isValid()) {
            return;
        }

        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        NPCEntity npcEntity = npcType == null ? null : store.getComponent(ref, npcType);
        MobDisposition disposition = plugin.resolveMobDisposition(store, npcEntity, playerRef);
        plugin.applyHealthModifier(statMap, disposition);
    }
}
