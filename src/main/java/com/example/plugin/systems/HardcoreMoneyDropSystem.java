package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.MobMoneyDropConfig;
import com.example.plugin.money.VaultEconomyCoordinator;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Deque;

public class HardcoreMoneyDropSystem extends DeathSystems.OnDeathSystem {
    private static final String[] DAMAGE_SOURCE_METHOD_NAMES = new String[]{
            "getRef",
            "getEntity",
            "getOwningEntity",
            "getTargetEntity",
            "getAttacker",
            "getPlayer",
            "getSourceEntity",
            "getProjectile",
            "getSource"
    };
    private final HardcoreModePlugin plugin;

    public HardcoreMoneyDropSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public com.hypixel.hytale.component.query.Query<EntityStore> getQuery() {
        return com.hypixel.hytale.component.query.Query.and(
                NPCEntity.getComponentType(),
                TransformComponent.getComponentType()
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
    private void addSystemDependencyIfPresent(Set<Dependency<EntityStore>> deps, Order order, String className) {
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
        if (ref == null || deathComponent == null || store == null) {
            return;
        }

        if (!plugin.isWorldEnabledForStore(store)) {
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
        if (category == null || category == MobCategory.NONE) {
            return;
        }

        MobMoneyDropConfig config = plugin.getMobMoneyDropConfig();
        if (config == null) {
            return;
        }

        double amount = config.resolveAmount(npcEntity, category);
        if (amount <= 0.0d) {
            return;
        }

        float bloodMoonMoneyMultiplier = plugin.getMoneyMultiplier(store);
        if (bloodMoonMoneyMultiplier > 1.0f) {
            amount *= bloodMoonMoneyMultiplier;
        }

        PlayerRef killer = resolveKillerPlayer(store, ref, deathComponent);
        if (killer == null) {
            return;
        }

        VaultEconomyCoordinator.DepositResult result = plugin.getVaultEconomyCoordinator().deposit(killer, amount);
        if (!result.success && result.available && result.errorMessage != null && !result.errorMessage.isBlank()) {
            plugin.sendErrorMessage(killer, "Money reward failed: " + result.errorMessage);
        }
    }

    private PlayerRef resolveKillerPlayer(
            Store<EntityStore> store,
            Ref<EntityStore> deadEntityRef,
            DeathComponent deathComponent
    ) {
        PlayerRef fromChain = resolveFromInteractionChain(store, deadEntityRef, deathComponent.getInteractionChain());
        if (fromChain != null) {
            return fromChain;
        }

        Damage deathInfo = deathComponent.getDeathInfo();
        if (deathInfo == null) {
            return null;
        }

        return resolveFromDamageSource(store, deadEntityRef, deathInfo.getSource());
    }

    private PlayerRef resolveFromInteractionChain(
            Store<EntityStore> store,
            Ref<EntityStore> deadEntityRef,
            InteractionChain interactionChain
    ) {
        if (interactionChain == null) {
            return null;
        }

        InteractionContext context = interactionChain.getContext();
        if (context == null) {
            return null;
        }

        PlayerRef player = resolvePlayerFromRef(store, deadEntityRef, context.getEntity());
        if (player != null) {
            return player;
        }

        player = resolvePlayerFromRef(store, deadEntityRef, context.getOwningEntity());
        if (player != null) {
            return player;
        }

        return resolvePlayerFromRef(store, deadEntityRef, context.getTargetEntity());
    }

    @SuppressWarnings("unchecked")
    private PlayerRef resolveFromDamageSource(
            Store<EntityStore> store,
            Ref<EntityStore> deadEntityRef,
            Object damageSource
    ) {
        if (damageSource == null) {
            return null;
        }

        Deque<Object> pending = new ArrayDeque<>();
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        pending.add(damageSource);

        while (!pending.isEmpty()) {
            Object current = pending.removeFirst();
            if (current == null || !visited.add(current)) {
                continue;
            }

            PlayerRef direct = resolvePlayerFromObject(store, deadEntityRef, current);
            if (direct != null) {
                return direct;
            }

            for (String methodName : DAMAGE_SOURCE_METHOD_NAMES) {
                Object value = invokeNoArgMethod(current, methodName);
                PlayerRef player = resolvePlayerFromObject(store, deadEntityRef, value);
                if (player != null) {
                    return player;
                }
                enqueueSearchTarget(pending, value, visited);
            }

            for (Field field : current.getClass().getDeclaredFields()) {
                Object value = readFieldValue(current, field);
                PlayerRef player = resolvePlayerFromObject(store, deadEntityRef, value);
                if (player != null) {
                    return player;
                }
                enqueueSearchTarget(pending, value, visited);
            }
        }

        return null;
    }

    private Object invokeNoArgMethod(Object target, String methodName) {
        if (target == null || methodName == null || methodName.isBlank()) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private Object readFieldValue(Object target, Field field) {
        if (target == null || field == null) {
            return null;
        }

        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private void enqueueSearchTarget(Deque<Object> pending, Object value, Set<Object> visited) {
        if (value == null || visited.contains(value)) {
            return;
        }

        if (value instanceof Ref<?> || value instanceof PlayerRef) {
            return;
        }

        String className = value.getClass().getName();
        if (className.startsWith("java.lang.") || className.startsWith("java.math.")) {
            return;
        }

        pending.addLast(value);
    }

    @SuppressWarnings("unchecked")
    private PlayerRef resolvePlayerFromObject(
            Store<EntityStore> store,
            Ref<EntityStore> deadEntityRef,
            Object value
    ) {
        if (value == null) {
            return null;
        }

        if (value instanceof PlayerRef playerRef) {
            return playerRef.isValid() ? playerRef : null;
        }

        if (value instanceof Ref<?>) {
            try {
                return resolvePlayerFromRef(store, deadEntityRef, (Ref<EntityStore>) value);
            } catch (RuntimeException ignored) {
                return null;
            }
        }

        return null;
    }

    private PlayerRef resolvePlayerFromRef(
            Store<EntityStore> store,
            Ref<EntityStore> deadEntityRef,
            Ref<EntityStore> candidateRef
    ) {
        if (store == null || candidateRef == null || !candidateRef.isValid() || candidateRef.equals(deadEntityRef)) {
            return null;
        }

        try {
            ComponentType<EntityStore, PlayerRef> playerRefType = PlayerRef.getComponentType();
            if (playerRefType != null) {
                PlayerRef direct = store.getComponent(candidateRef, playerRefType);
                if (direct != null) {
                    return direct;
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            ComponentType<EntityStore, com.hypixel.hytale.server.core.entity.entities.Player> playerType =
                    com.hypixel.hytale.server.core.entity.entities.Player.getComponentType();
            if (playerType != null) {
                com.hypixel.hytale.server.core.entity.entities.Player player = store.getComponent(candidateRef, playerType);
                if (player != null) {
                    return player.getPlayerRef();
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }
}
