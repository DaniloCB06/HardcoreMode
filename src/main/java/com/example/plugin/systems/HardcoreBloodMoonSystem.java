package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;
    private long lastProcessedHourOfEpoch = Long.MIN_VALUE;

    public HardcoreBloodMoonSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void tick(float deltaSeconds, int tick, Store<EntityStore> store) {
        if (store == null) {
            return;
        }

        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) {
            return;
        }

        long epochDay = time.getGameDateTime().toLocalDate().toEpochDay();
        long currentHourOfEpoch = (epochDay * 24L) + (long) time.getCurrentHour();

        if (currentHourOfEpoch == lastProcessedHourOfEpoch) {
            return;
        }
        lastProcessedHourOfEpoch = currentHourOfEpoch;

        plugin.setActiveStore(store);
        plugin.refreshBloodMoonState(store, true);
    }
}
