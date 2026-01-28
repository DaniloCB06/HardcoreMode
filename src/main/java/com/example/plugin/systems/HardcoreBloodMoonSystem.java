package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;
    /**
     * Last global tick we processed to avoid running once per store and spamming
     * state changes.
     */
    private int lastProcessedTick = -1;
    /** Store identity chosen as the source of truth for world time. */
    private int primaryStoreHash = 0;

    public HardcoreBloodMoonSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void tick(float deltaSeconds, int tick, Store<EntityStore> store) {
        if (store == null || store.getResource(WorldTimeResource.getResourceType()) == null) {
            return;
        }

        // Simpler logic: Check strictly for WorldTimeResource which implies a valid
        // world/dimension.
        // We REMOVED getAnyPlayerRef because it was causing the system to sleep/stop
        // ticking
        // if the player cache was invalid or chunk iteration failed, preventing the
        // Blood Moon from starting.

        // Prevent processing the same tick multiple times
        if (tick == lastProcessedTick) {
            return;
        }

        lastProcessedTick = tick;

        // Cache the store for the independent heartbeat scheduler
        plugin.setActiveStore(store);

        // Heartbeat log every ~1 minute (assuming 20tps, 1200 ticks) to confirm system
        // is alive during debug
        if (tick % 1200 == 0) {
            System.out.println("[HardcoreDebug] Tumbleweed... Tick: " + tick);
        }

        plugin.refreshBloodMoonState(store, true);
    }
}
