package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;
    
    // Rastrear a última hora processada POR STORE/MUNDO
    // Isso evita que mundos com tempos diferentes interfiram uns nos outros
    private final Map<Store<EntityStore>, Long> lastHourByStore = 
            Collections.synchronizedMap(new WeakHashMap<>());

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

        // Verificar se já processamos esta hora PARA ESTE STORE específico
        Long lastHour;
        synchronized (lastHourByStore) {
            lastHour = lastHourByStore.get(store);
            if (lastHour != null && lastHour == currentHourOfEpoch) {
                return;
            }
            lastHourByStore.put(store, currentHourOfEpoch);
        }

        plugin.setActiveStore(store);
        plugin.refreshBloodMoonState(store, true);
    }
}
