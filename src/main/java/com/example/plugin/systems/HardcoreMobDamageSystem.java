package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
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
        // ✅ Só recalcula se a HORA mudou (e se mudou, aplica em mobs existentes também)
        plugin.refreshBloodMoonStateIfNeeded(store, true);

        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }

        Ref<EntityStore> sourceRef = ((Damage.EntitySource) source).getRef();
        if (sourceRef == null || !sourceRef.isValid()) {
            return;
        }

        // Ignora se o atacante for player
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType != null && store.getComponent(sourceRef, playerType) != null) {
            return;
        }

        ComponentType<EntityStore, EntityStatMap> statType = EntityStatMap.getComponentType();
        if (statType == null || store.getComponent(sourceRef, statType) == null) {
            return;
        }

        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        NPCEntity npcEntity = npcType == null ? null : store.getComponent(sourceRef, npcType);

        MobCategory category = plugin.resolveMobCategory(npcEntity);
        if (!plugin.isMobEnabled(category)) {
            return;
        }

        float multiplier = plugin.getDamageMultiplier(category);
        if (multiplier <= 1.0f) {
            return;
        }

        damage.setAmount(Math.max(0.0f, damage.getAmount() * multiplier));
    }
}
