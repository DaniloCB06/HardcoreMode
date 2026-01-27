package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;
    /** Last global tick we processed to avoid running once per store and spamming state changes. */
    private int lastProcessedTick = -1;
    /** Store identity chosen as the source of truth for world time. */
    private int primaryStoreHash = 0;

    public HardcoreBloodMoonSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void tick(float deltaSeconds, int tick, Store<EntityStore> store) {
        // On some servers there are multiple EntityStores (lobby/instances) and not all of them
        // expose a WorldTimeResource. Processing each store was causing the Blood Moon flag to flip
        // back and forth every tick, spamming begin/end messages and reapplying mob stats.
        if (tick == lastProcessedTick) {
            return;
        }

        if (store == null || store.getResource(WorldTimeResource.getResourceType()) == null) {
            return;
        }

        int storeHash = System.identityHashCode(store);
        if (primaryStoreHash == 0) {
            primaryStoreHash = storeHash;
        } else if (primaryStoreHash != storeHash) {
            return;
        }

        lastProcessedTick = tick;
        plugin.refreshBloodMoonState(store, true);
    }
}
