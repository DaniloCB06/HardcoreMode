package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreMainMenuPageEventData {
    public static final String KEY_OPEN_ENEMY = "@OpenEnemySettings";
    public static final String KEY_OPEN_BLOOD_MOON = "@OpenBloodMoonSettings";
    public static final String KEY_OPEN_PLAYER = "@OpenPlayerSettings";

    public static final BuilderCodec<HardcoreMainMenuPageEventData> CODEC = BuilderCodec
            .builder(HardcoreMainMenuPageEventData.class, HardcoreMainMenuPageEventData::new)
            .append(new KeyedCodec<>(KEY_OPEN_ENEMY, Codec.BOOLEAN),
                    (data, value) -> data.openEnemySettings = value,
                    data -> data.openEnemySettings)
            .add()
            .append(new KeyedCodec<>(KEY_OPEN_BLOOD_MOON, Codec.BOOLEAN),
                    (data, value) -> data.openBloodMoonSettings = value,
                    data -> data.openBloodMoonSettings)
            .add()
            .append(new KeyedCodec<>(KEY_OPEN_PLAYER, Codec.BOOLEAN),
                    (data, value) -> data.openPlayerSettings = value,
                    data -> data.openPlayerSettings)
            .add()
            .build();

    private Boolean openEnemySettings;
    private Boolean openBloodMoonSettings;
    private Boolean openPlayerSettings;

    public HardcoreMainMenuPageEventData() {
    }

    public Boolean getOpenEnemySettings() {
        return openEnemySettings;
    }

    public Boolean getOpenBloodMoonSettings() {
        return openBloodMoonSettings;
    }

    public Boolean getOpenPlayerSettings() {
        return openPlayerSettings;
    }
}
