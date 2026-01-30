package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcoreBloodMoonSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;

    // ✅ Travar por hora do mundo (não por tick)
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

        // ✅ Só processa quando a hora muda
        if (currentHourOfEpoch == lastProcessedHourOfEpoch) {
            return;
        }
        lastProcessedHourOfEpoch = currentHourOfEpoch;

        // Compat/debug (não faz lógica fora do tick)
        plugin.setActiveStore(store);

        // ✅ Atualiza estado do Blood Moon (aplica nos mobs apenas se mudou, dentro do plugin)
        plugin.refreshBloodMoonState(store, true);
    }
}
