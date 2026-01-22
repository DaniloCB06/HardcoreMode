package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobDisposition;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

public class HardcoreMobDamageSystem extends DamageEventSystem {
    private final HardcoreModePlugin plugin;

    public HardcoreMobDamageSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(
            int index,
            ArchetypeChunk<EntityStore> chunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            Damage damage
    ) {
        plugin.refreshBloodMoonState(store, true);
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        Ref<EntityStore> playerRef = null;
        Ref<EntityStore> targetRef = chunk.getReferenceTo(index);
        if (playerType != null && targetRef != null && targetRef.isValid()) {
            if (store.getComponent(targetRef, playerType) != null) {
                playerRef = targetRef;
            }
        }

        if (playerRef == null || !playerRef.isValid()) {
            playerRef = plugin.getAnyPlayerRef(store);
        }

        if (playerRef == null || !playerRef.isValid()) {
            return;
        }

        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }

        Ref<EntityStore> sourceRef = ((Damage.EntitySource) source).getRef();
        if (sourceRef == null || !sourceRef.isValid()) {
            return;
        }

        if (playerType != null && store.getComponent(sourceRef, playerType) != null) {
            return;
        }

        if (EntityStatMap.getComponentType() == null
                || store.getComponent(sourceRef, EntityStatMap.getComponentType()) == null) {
            return;
        }

        NPCEntity npcEntity = NPCEntity.getComponentType() == null
                ? null
                : store.getComponent(sourceRef, NPCEntity.getComponentType());
        MobDisposition disposition = plugin.resolveMobDisposition(store, npcEntity, playerRef);
        if (!plugin.isMobEnabled(disposition)) {
            return;
        }
        float multiplier = plugin.getDamageMultiplier(disposition);
        if (multiplier <= 1.0f) {
            return;
        }

        damage.setAmount(Math.max(0.0f, damage.getAmount() * multiplier));
    }
}
