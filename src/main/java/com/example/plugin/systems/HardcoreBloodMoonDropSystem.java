package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.BloodMoonDropConfig;
import com.example.plugin.config.HardcoreModeConfig;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.math.vector.Rotation3f;
import org.joml.Vector3d;

import java.util.ArrayList;
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
        return com.hypixel.hytale.component.query.Query.and(
            NPCEntity.getComponentType(),
            TransformComponent.getComponentType(),
            HeadRotation.getComponentType()
        );
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        Set<Dependency<EntityStore>> deps = new LinkedHashSet<>();
        addSystemDependencyIfPresent(deps, Order.AFTER,
            "com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems$TickCorpseRemoval");
        addSystemDependencyIfPresent(deps, Order.BEFORE,
            "com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems$CorpseRemoval");
        return deps;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Dependency<EntityStore> createSystemDependency(Class<?> dependencyClass) {
        return new SystemDependency(Order.AFTER, (Class) dependencyClass);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addSystemDependencyIfPresent(
            Set<Dependency<EntityStore>> deps,
            Order order,
            String className
    ) {
        try {
            Class<?> dependencyClass = Class.forName(className);
            deps.add(new SystemDependency(order, (Class) dependencyClass));
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Override
    public void onComponentAdded(
            Ref<EntityStore> ref,
            DeathComponent deathComponent,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        if (!plugin.isBloodMoonActive(store)) {
            return;
        }

        // Verificar se o HardcoreMode está habilitado para este mundo
        if (!plugin.isWorldEnabledForStore(store)) {
            return;
        }

        com.example.plugin.config.WorldHardcoreConfig worldConfig = plugin.getWorldConfig(store);
        if (worldConfig == null || !worldConfig.bloodMoonDropsEnabled) {
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

        if (!plugin.isBloodMoonAffected(worldConfig, category)) {
            return;
        }

        BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
        if (dropConfig != null) {
            List<BloodMoonDropConfig.DropEntry> entries = dropConfig.getDropEntries(category);
            if (!entries.isEmpty()) {
                processJsonDrops(ref, store, commandBuffer, entries);
                return;
            }
        }

        // Legacy drops usam config global
        HardcoreModeConfig globalConfig = plugin.getConfigData();
        processLegacyDrop(ref, store, commandBuffer, category, globalConfig);
    }

    private void processJsonDrops(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            List<BloodMoonDropConfig.DropEntry> entries
    ) {
        List<ItemStack> drops = new ArrayList<>();
        for (BloodMoonDropConfig.DropEntry entry : entries) {
            if (!entry.enabled || !entry.shouldDrop()) {
                continue;
            }

            int quantity = entry.getRandomQuantity();
            if (quantity <= 0) {
                continue;
            }

            ItemStack itemStack = buildDropItemStack(entry.itemId, quantity);
            if (itemStack != null) {
                drops.add(itemStack);
            }
        }

        spawnDropItems(ref, store, commandBuffer, drops);
    }

    private void processLegacyDrop(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
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

        ItemStack itemStack = buildDropItemStack(itemId, quantity);
        if (itemStack != null) {
            spawnDropItems(ref, store, commandBuffer, List.of(itemStack));
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

    private ItemStack buildDropItemStack(
            String itemId,
            int quantity
    ) {
        if (itemId == null || itemId.isEmpty() || quantity <= 0) {
            return null;
        }

        ItemStack itemStack = new ItemStack(itemId, quantity);
        if (itemStack.isEmpty() || !itemStack.isValid()) {
            return null;
        }
        return itemStack;
    }

    private void spawnDropItems(
            Ref<EntityStore> entityRef,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            List<ItemStack> itemStacks
    ) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return;
        }

        try {
            ComponentType<EntityStore, TransformComponent> transformType = TransformComponent.getComponentType();
            ComponentType<EntityStore, HeadRotation> headRotationType = HeadRotation.getComponentType();
            if (transformType == null || headRotationType == null) {
                return;
            }

            TransformComponent transform = store.getComponent(entityRef, transformType);
            HeadRotation headRotation = store.getComponent(entityRef, headRotationType);
            if (transform == null || headRotation == null) {
                return;
            }

            Vector3d dropPosition = new Vector3d(transform.getPosition()).add(0.0, 1.0, 0.0);
            Rotation3f rotation = new Rotation3f(headRotation.getRotation());
            Holder<EntityStore>[] generatedDrops = ItemComponent.generateItemDrops(
                store,
                itemStacks,
                dropPosition,
                rotation
            );

            if (generatedDrops != null && generatedDrops.length > 0) {
                commandBuffer.addEntities(generatedDrops, AddReason.SPAWN);
            }
        } catch (Exception ignored) {
        }
    }
}
