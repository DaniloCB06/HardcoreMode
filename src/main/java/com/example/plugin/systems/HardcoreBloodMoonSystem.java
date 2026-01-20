package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;

    public HardcoreBloodMoonSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void tick(float deltaSeconds, int tick, Store<EntityStore> store) {
        plugin.refreshBloodMoonState(store, true);
    }
}
