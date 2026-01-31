package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.BloodMoonDropConfig;
import com.example.plugin.config.HardcoreModeConfig;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HardcoreBloodMoonDropSystem extends DeathSystems.OnDeathSystem {
    private static final String DEFAULT_HOSTILE_DROP_ITEM = "Ingredient_Bar_Iron";
    private static final String DEFAULT_ELITE_DROP_ITEM = "Ingredient_Bar_Thorium";
    private static final String DEFAULT_MINIBOSS_DROP_ITEM = "Ingredient_Bar_Adamantite";
    private static final String DEFAULT_WORLDBOSS_DROP_ITEM = "Ingredient_Bar_Mithril";

    private final HardcoreModePlugin plugin;

    public HardcoreBloodMoonDropSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public com.hypixel.hytale.component.query.Query<EntityStore> getQuery() {
        return NPCEntity.getComponentType();
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        Set<Dependency<EntityStore>> deps = new LinkedHashSet<>();
        try {
            Class<?> npcDeathClass = Class.forName(
                "com.hypixel.hytale.server.npc.systems.NPCDamageSystems$DropDeathItems"
            );
            @SuppressWarnings({"rawtypes", "unchecked"})
            SystemDependency dep = new SystemDependency(Order.AFTER, (Class) npcDeathClass);
            deps.add(dep);
        } catch (ClassNotFoundException ignored) {
        }
        return deps;
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            DeathComponent deathComponent,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        if (!plugin.isBloodMoonActive()) {
            return;
        }

        HardcoreModeConfig config = plugin.getConfigData();
        if (config == null || !config.bloodMoonDropsEnabled) {
            return;
        }

        ComponentType<EntityStore, NPCEntity> npcType = NPCEntity.getComponentType();
        if (npcType == null) {
            return;
        }

        NPCEntity npcEntity = store.getComponent(ref, npcType);
        if (npcEntity == null) {
            return;
        }

        MobCategory category = plugin.resolveMobCategory(npcEntity);

        if (!plugin.isBloodMoonAffected(category)) {
            return;
        }

        BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
        if (dropConfig != null) {
            List<BloodMoonDropConfig.DropEntry> entries = dropConfig.getDropEntries(category);
            if (!entries.isEmpty()) {
                processJsonDrops(ref, commandBuffer, entries);
                return;
            }
        }

        processLegacyDrop(ref, commandBuffer, category, config);
    }

    private void processJsonDrops(
            Ref<EntityStore> ref,
            CommandBuffer<EntityStore> commandBuffer,
            List<BloodMoonDropConfig.DropEntry> entries
    ) {
        for (BloodMoonDropConfig.DropEntry entry : entries) {
            if (!entry.enabled || !entry.shouldDrop()) {
                continue;
            }

            int quantity = entry.getRandomQuantity();
            if (quantity <= 0) {
                continue;
            }

            try {
                spawnDropItem(ref, commandBuffer, entry.itemId, quantity);
            } catch (Exception ignored) {
            }
        }
    }

    private void processLegacyDrop(
            Ref<EntityStore> ref,
            CommandBuffer<EntityStore> commandBuffer,
            MobCategory category,
            HardcoreModeConfig config
    ) {
        if (!isLegacyDropEnabled(category, config)) {
            return;
        }

        String itemId = getLegacyDropItem(category, config);
        int quantity = getLegacyDropQuantity(category, config);
        float dropChance = getLegacyDropChance(category, config);

        if (itemId == null || itemId.isEmpty() || quantity <= 0) {
            return;
        }

        if (dropChance < 100.0f && Math.random() * 100.0f > dropChance) {
            return;
        }

        try {
            spawnDropItem(ref, commandBuffer, itemId, quantity);
        } catch (Exception ignored) {
        }
    }

    private boolean isLegacyDropEnabled(MobCategory category, HardcoreModeConfig config) {
        switch (category) {
            case HOSTILE:  return config.bloodMoonHostileDropEnabled;
            case ELITE:    return config.bloodMoonEliteDropEnabled;
            case MINIBOSS: return config.bloodMoonMinibossDropEnabled;
            case WORLDBOSS: return config.bloodMoonWorldbossDropEnabled;
            default:       return false;
        }
    }

    private String getLegacyDropItem(MobCategory category, HardcoreModeConfig config) {
        switch (category) {
            case HOSTILE:
                return config.bloodMoonHostileDropItem.isEmpty() 
                    ? DEFAULT_HOSTILE_DROP_ITEM : config.bloodMoonHostileDropItem;
            case ELITE:
                return config.bloodMoonEliteDropItem.isEmpty() 
                    ? DEFAULT_ELITE_DROP_ITEM : config.bloodMoonEliteDropItem;
            case MINIBOSS:
                return config.bloodMoonMinibossDropItem.isEmpty() 
                    ? DEFAULT_MINIBOSS_DROP_ITEM : config.bloodMoonMinibossDropItem;
            case WORLDBOSS:
                return config.bloodMoonWorldbossDropItem.isEmpty() 
                    ? DEFAULT_WORLDBOSS_DROP_ITEM : config.bloodMoonWorldbossDropItem;
            default:
                return null;
        }
    }

    private int getLegacyDropQuantity(MobCategory category, HardcoreModeConfig config) {
        switch (category) {
            case HOSTILE:  return config.bloodMoonHostileDropQuantity;
            case ELITE:    return config.bloodMoonEliteDropQuantity;
            case MINIBOSS: return config.bloodMoonMinibossDropQuantity;
            case WORLDBOSS: return config.bloodMoonWorldbossDropQuantity;
            default:       return 0;
        }
    }

    private float getLegacyDropChance(MobCategory category, HardcoreModeConfig config) {
        switch (category) {
            case HOSTILE:  return config.bloodMoonHostileDropChance;
            case ELITE:    return config.bloodMoonEliteDropChance;
            case MINIBOSS: return config.bloodMoonMinibossDropChance;
            case WORLDBOSS: return config.bloodMoonWorldbossDropChance;
            default:       return 0.0f;
        }
    }

    private void spawnDropItem(
            Ref<EntityStore> entityRef,
            CommandBuffer<EntityStore> commandBuffer,
            String itemId,
            int quantity
    ) {
        ItemStack itemStack = new ItemStack(itemId, quantity);
        if (itemStack.isEmpty() || !itemStack.isValid()) {
            return;
        }

        try {
            ItemUtils.dropItem(entityRef, itemStack, commandBuffer);
        } catch (Exception ignored) {
        }
    }
}
