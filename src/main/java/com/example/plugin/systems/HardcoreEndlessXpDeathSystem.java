package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.LinkedHashSet;
import java.util.Set;

public final class HardcoreEndlessXpDeathSystem extends DeathSystems.OnDeathSystem {
    private static final String ENDLESS_XP_EVENT_SYSTEM =
            "com.airijko.endlessleveling.leveling.XpEventSystem";

    private final HardcoreModePlugin plugin;

    public HardcoreEndlessXpDeathSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        Set<Dependency<EntityStore>> dependencies = new LinkedHashSet<>();
        try {
            Class<?> xpEventSystem = Class.forName(ENDLESS_XP_EVENT_SYSTEM);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Dependency<EntityStore> dependency = new SystemDependency(Order.BEFORE, (Class) xpEventSystem);
            dependencies.add(dependency);
        } catch (ClassNotFoundException ignored) {
            // EndlessLeveling is optional.
        }
        return dependencies;
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            DeathComponent component,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        if (ref == null || !ref.isValid() || store == null || !plugin.usesEntityXpMultipliers()) {
            return;
        }

        NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
        MobCategory category = plugin.resolveMobCategory(npc);
        plugin.applyBloodMoonXpMultiplier(store, ref, category);
    }
}
