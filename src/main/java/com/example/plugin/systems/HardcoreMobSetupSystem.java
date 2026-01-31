package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

public class HardcoreMobSetupSystem extends HolderSystem<EntityStore> {
    public static final String HEALTH_MODIFIER_KEY = "HardcoreMode.HealthMultiplier";
    private final HardcoreModePlugin plugin;

    public HardcoreMobSetupSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void onEntityAdd(Holder<EntityStore> holder, AddReason reason, Store<EntityStore> store) {

        plugin.refreshBloodMoonStateIfNeeded(store, true);

        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType != null && holder.getComponent(playerType) != null) {
            // Player entrou -> aplica nos mobs existentes (se tiver player, bom momento)
            plugin.applyToExistingMobs(store);
            return;
        }

        ComponentType<EntityStore, EntityStatMap> statType = EntityStatMap.getComponentType();
        if (statType == null) {
            return;
        }

        EntityStatMap statMap = holder.getComponent(statType);
        if (statMap == null) {
            return;
        }

        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        NPCEntity npcEntity = npcType == null ? null : holder.getComponent(npcType);

        plugin.applyHealthModifier(statMap, plugin.resolveMobCategory(npcEntity));
    }

    @Override
    public void onEntityRemoved(Holder<EntityStore> holder, RemoveReason reason, Store<EntityStore> store) {
        // No cleanup needed.
    }
}
