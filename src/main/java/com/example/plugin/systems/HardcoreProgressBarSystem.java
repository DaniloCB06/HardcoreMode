package com.example.plugin.systems;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.ui.HardcoreProgressBarHud;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HardcoreProgressBarSystem extends TickingSystem<EntityStore> {
    private final HardcoreModePlugin plugin;
    
    private final Map<Ref<EntityStore>, Float> lastProgress = new ConcurrentHashMap<>();
    
    private int tickCounter = 0;
    private static final int UPDATE_INTERVAL_TICKS = 20;
    
    private boolean wasBloodMoonActive = false;

    public HardcoreProgressBarSystem(HardcoreModePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void tick(float deltaSeconds, int tick, Store<EntityStore> store) {
        if (store == null) {
            return;
        }

        tickCounter++;
        if (tickCounter < UPDATE_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        boolean bloodMoonActive = plugin.isBloodMoonActive();
        boolean hudEnabled = plugin.getConfigData().bloodMoonHudEnabled;
        
        if (bloodMoonActive && hudEnabled) {
            float progress = plugin.getBloodMoonProgress(store);
            int hoursRemaining = plugin.getBloodMoonHoursRemaining(store);
            
            updateAllPlayers(store, progress, hoursRemaining);
            wasBloodMoonActive = true;
        } else if (wasBloodMoonActive || (bloodMoonActive && !hudEnabled)) {
            removeHudFromAllPlayers(store);
            lastProgress.clear();
            wasBloodMoonActive = false;
        }
    }

    /**
     * Updates the HUD for all online players
     */
    private void updateAllPlayers(Store<EntityStore> store, float progress, int hoursRemaining) {
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType == null) {
            return;
        }

        Query<EntityStore> playerQuery = Query.any();
        store.forEachChunk(playerQuery, (chunk, commandBuffer) -> {
            int size = chunk.size();
            for (int i = 0; i < size; i++) {
                Player player = chunk.getComponent(i, playerType);
                if (player == null) {
                    continue;
                }

                Ref<EntityStore> ref = chunk.getReferenceTo(i);
                if (ref == null || !ref.isValid()) {
                    continue;
                }

                // Check if update is needed (progress changed >5%)
                Float lastProg = lastProgress.get(ref);
                if (lastProg == null || Math.abs(lastProg - progress) > 0.05f) {
                    HardcoreProgressBarHud hud = new HardcoreProgressBarHud(
                        player.getPlayerRef(),
                        progress,
                        hoursRemaining,
                        true
                    );
                    player.getHudManager().setCustomHud(player.getPlayerRef(), hud);
                    lastProgress.put(ref, progress);
                }
            }
            return true;
        });
    }
    
    /**
     * Removes the HUD from all online players
     */
    private void removeHudFromAllPlayers(Store<EntityStore> store) {
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType == null) {
            return;
        }

        Query<EntityStore> playerQuery = Query.any();
        store.forEachChunk(playerQuery, (chunk, commandBuffer) -> {
            int size = chunk.size();
            for (int i = 0; i < size; i++) {
                Player player = chunk.getComponent(i, playerType);
                if (player == null) {
                    continue;
                }

                Ref<EntityStore> ref = chunk.getReferenceTo(i);
                if (ref == null || !ref.isValid()) {
                    continue;
                }

                // Remove a HUD usando EmptyHud
                HardcoreProgressBarHud.EmptyHud emptyHud = new HardcoreProgressBarHud.EmptyHud(player.getPlayerRef());
                player.getHudManager().setCustomHud(player.getPlayerRef(), emptyHud);
            }
            return true;
        });
    }

    /**
     * Clears the state (useful when shutting down the server)
     */
    public void cleanup() {
        lastProgress.clear();
        wasBloodMoonActive = false;
    }
}
