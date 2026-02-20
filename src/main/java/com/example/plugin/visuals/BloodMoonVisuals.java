package com.example.plugin.visuals;

import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateWeathers;
import com.hypixel.hytale.server.core.asset.type.ambiencefx.config.AmbienceFX;
import com.hypixel.hytale.server.core.asset.type.weather.config.TimeColorAlpha;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.ColorAlpha;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BloodMoonVisuals {
    public static final String BLOOD_MOON_WEATHER_ID = "Hardcore_Blood_Moon";
    private static final String[] TRORK_CAMP_MUSIC_FALLBACKS = new String[]{
            "Mus_Trork_Camp",
            "Mus_Trork_Campfire",
            "Mus_Trork_Outpost",
            "Ambience_Trork_Camp",
            "Trork_Camp_Music"
    };

    private static final byte MOON_ALPHA = (byte) 255;
    private static final byte MOON_RED = (byte) 255;
    private static final byte MOON_GREEN = (byte) 20;
    private static final byte MOON_BLUE = (byte) 20;

    private static volatile boolean weatherFieldsReady;
    private static Field moonColorsField;
    private static Field moonGlowColorsField;
    private static Field cachedPacketField;

    private final Set<String> weatherLockedWorlds = ConcurrentHashMap.newKeySet();
    private final Set<String> musicLockedWorlds = ConcurrentHashMap.newKeySet();

    public void applyWorldVisuals(Store<EntityStore> store, String worldName, boolean active) {
        if (store == null) {
            return;
        }

        World world = resolveWorld(store, worldName);
        if (world == null) {
            return;
        }

        if (active) {
            forceBloodMoonWeather(store, worldName, world);
            forceBloodMoonMusic(store, worldName, world);
            setMoonRed(world.getPlayers());
        } else {
            clearBloodMoonWeather(store, worldName, world);
            clearBloodMoonMusic(store, worldName, world);
            setMoonNormal(world.getPlayers());
        }
    }

    public void applyMoonForPlayer(Player player, boolean active) {
        if (player == null) {
            return;
        }
        List<Player> players = Collections.singletonList(player);
        if (active) {
            setMoonRed(players);
        } else {
            setMoonNormal(players);
        }
    }

    private void forceBloodMoonWeather(Store<EntityStore> store, String worldName, World world) {
        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());
        if (weatherResource == null) {
            return;
        }

        String key = resolveWorldKey(worldName, world);
        weatherResource.setForcedWeather(BLOOD_MOON_WEATHER_ID);
        weatherLockedWorlds.add(key);
    }

    private void clearBloodMoonWeather(Store<EntityStore> store, String worldName, World world) {
        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());
        if (weatherResource == null) {
            return;
        }

        String key = resolveWorldKey(worldName, world);
        boolean wasLocked = weatherLockedWorlds.remove(key);
        int forcedIndex = weatherResource.getForcedWeatherIndex();
        int bloodMoonIndex = Weather.getAssetMap().getIndex(BLOOD_MOON_WEATHER_ID);
        if (!wasLocked && forcedIndex != bloodMoonIndex) {
            return;
        }
        if (forcedIndex == bloodMoonIndex) {
            weatherResource.setForcedWeather(null);
        }
    }

    private void forceBloodMoonMusic(Store<EntityStore> store, String worldName, World world) {
        AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
        if (ambienceResource == null) {
            return;
        }

        String musicId = resolveTrorkCampMusicId();
        if (musicId == null || musicId.isBlank()) {
            return;
        }

        String key = resolveWorldKey(worldName, world);
        ambienceResource.setForcedMusicAmbience(musicId);
        musicLockedWorlds.add(key);
    }

    private void clearBloodMoonMusic(Store<EntityStore> store, String worldName, World world) {
        AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
        if (ambienceResource == null) {
            return;
        }

        String key = resolveWorldKey(worldName, world);
        boolean wasLocked = musicLockedWorlds.remove(key);

        String musicId = resolveTrorkCampMusicId();
        if (musicId == null || musicId.isBlank()) {
            return;
        }

        int forcedIndex = ambienceResource.getForcedMusicIndex();
        int musicIndex = AmbienceFX.getAssetMap().getIndex(musicId);
        if (!wasLocked && forcedIndex != musicIndex) {
            return;
        }
        if (forcedIndex == musicIndex) {
            ambienceResource.setForcedMusicAmbience(null);
        }
    }

    private String resolveWorldKey(String worldName, World world) {
        if (worldName != null && !worldName.isBlank()) {
            return worldName;
        }
        if (world != null) {
            return world.getName();
        }
        return "unknown";
    }

    private World resolveWorld(Store<EntityStore> store, String worldName) {
        Universe universe = Universe.get();
        if (universe == null) {
            return null;
        }

        if (worldName != null) {
            World world = universe.getWorlds().get(worldName);
            if (world != null) {
                return world;
            }
        }

        for (World world : universe.getWorlds().values()) {
            if (world == null) {
                continue;
            }
            try {
                EntityStore entityStore = world.getEntityStore();
                if (entityStore != null && entityStore.getStore() == store) {
                    return world;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static String resolveTrorkCampMusicId() {
        String resolved = findTrorkCampMusicId();
        if (resolved != null && !resolved.isBlank()) {
            return resolved;
        }

        for (String candidate : TRORK_CAMP_MUSIC_FALLBACKS) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            int index = AmbienceFX.getAssetMap().getIndex(candidate);
            if (index != AmbienceFX.EMPTY_ID) {
                return candidate;
            }
        }

        return null;
    }

    private static String findTrorkCampMusicId() {
        Map<String, AmbienceFX> assets = AmbienceFX.getAssetMap().getAssetMap();
        if (assets == null || assets.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, AmbienceFX> entry : assets.entrySet()) {
            String id = entry.getKey();
            if (id == null) {
                continue;
            }
            String lower = id.toLowerCase();
            if (!lower.contains("trork")) {
                continue;
            }
            if (!(lower.contains("camp") || lower.contains("outpost") || lower.contains("encamp"))) {
                continue;
            }

            AmbienceFX fx = entry.getValue();
            if (fx != null && fx.getMusic() != null) {
                return id;
            }
        }

        return null;
    }

    private void setMoonRed(List<Player> players) {
        sendMoonUpdate(players, true);
    }

    private void setMoonNormal(List<Player> players) {
        sendMoonUpdate(players, false);
    }

    private void sendMoonUpdate(List<Player> players, boolean red) {
        if (players == null || players.isEmpty()) {
            return;
        }

        if (!ensureWeatherFields()) {
            return;
        }

        Map<Integer, com.hypixel.hytale.protocol.Weather> updated = new HashMap<>();
        Weather.getAssetMap().getAssetMap().forEach((id, weather) -> {
            if (weather == null) {
                return;
            }
            if (red) {
                updateWeatherMoonColors(updated, id, weather);
            } else {
                updateWeatherNormal(updated, id, weather);
            }
        });

        if (updated.isEmpty()) {
            return;
        }

        UpdateWeathers packet = new UpdateWeathers(
                UpdateType.AddOrUpdate,
                Weather.getAssetMap().getNextIndex(),
                updated
        );

        for (Player player : players) {
            if (player == null) {
                continue;
            }
            try {
                player.getPlayerConnection().writeNoCache(packet);
            } catch (Exception ignored) {
            }
        }
    }

    private void updateWeatherMoonColors(
            Map<Integer, com.hypixel.hytale.protocol.Weather> updated,
            String id,
            Weather weather
    ) {
        TimeColorAlpha[] moonColors = weather.getMoonColors();
        TimeColorAlpha[] moonGlowColors = weather.getMoonGlowColors();
        if (moonColors == null) {
            return;
        }

        try {
            TimeColorAlpha[] newMoonColors = Arrays.stream(moonColors)
                    .map(BloodMoonVisuals::toRedMoonColor)
                    .toArray(TimeColorAlpha[]::new);
            TimeColorAlpha[] newMoonGlowColors = moonGlowColors == null ? null
                    : Arrays.stream(moonGlowColors)
                    .map(BloodMoonVisuals::toRedMoonColor)
                    .toArray(TimeColorAlpha[]::new);

            moonColorsField.set(weather, newMoonColors);
            if (moonGlowColors != null) {
                moonGlowColorsField.set(weather, newMoonGlowColors);
            }
            cachedPacketField.set(weather, null);

            int index = Weather.getAssetMap().getIndex(id);
            updated.put(index, weather.toPacket());
        } catch (Exception ignored) {
        } finally {
            try {
                moonColorsField.set(weather, moonColors);
            } catch (Exception ignored) {
            }
            if (moonGlowColors != null) {
                try {
                    moonGlowColorsField.set(weather, moonGlowColors);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void updateWeatherNormal(
            Map<Integer, com.hypixel.hytale.protocol.Weather> updated,
            String id,
            Weather weather
    ) {
        try {
            cachedPacketField.set(weather, null);
            int index = Weather.getAssetMap().getIndex(id);
            updated.put(index, weather.toPacket());
        } catch (Exception ignored) {
        }
    }

    private static TimeColorAlpha toRedMoonColor(TimeColorAlpha original) {
        return new TimeColorAlpha(
                original.getHour(),
                new ColorAlpha(MOON_ALPHA, MOON_RED, MOON_GREEN, MOON_BLUE)
        );
    }

    private static boolean ensureWeatherFields() {
        if (weatherFieldsReady) {
            return true;
        }
        synchronized (BloodMoonVisuals.class) {
            if (weatherFieldsReady) {
                return true;
            }
            try {
                moonColorsField = Weather.class.getDeclaredField("moonColors");
                moonGlowColorsField = Weather.class.getDeclaredField("moonGlowColors");
                cachedPacketField = Weather.class.getDeclaredField("cachedPacket");
                moonColorsField.setAccessible(true);
                moonGlowColorsField.setAccessible(true);
                cachedPacketField.setAccessible(true);
                weatherFieldsReady = true;
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }
}
