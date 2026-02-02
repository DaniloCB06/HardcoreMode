package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class HardcoreWorldSettingsPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_REFRESH = "@Refresh";
    public static final String KEY_WORLD_TOGGLE_PREFIX = "@WorldToggle_";

    // Suporte para até 20 mundos
    public static final BuilderCodec<HardcoreWorldSettingsPageEventData> CODEC;
    
    static {
        var builder = BuilderCodec
                .builder(HardcoreWorldSettingsPageEventData.class, HardcoreWorldSettingsPageEventData::new)
                .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                        (data, value) -> data.goBack = value,
                        data -> data.goBack)
                .add()
                .append(new KeyedCodec<>(KEY_REFRESH, Codec.BOOLEAN),
                        (data, value) -> data.refresh = value,
                        data -> data.refresh)
                .add();
        
        // Adicionar suporte para toggles de mundos (0-19)
        for (int i = 0; i < 20; i++) {
            final int index = i;
            builder = builder
                    .append(new KeyedCodec<>(KEY_WORLD_TOGGLE_PREFIX + i, Codec.BOOLEAN),
                            (data, value) -> data.worldToggles.put(index, value),
                            data -> data.worldToggles.get(index))
                    .add();
        }
        
        CODEC = builder.build();
    }

    private Boolean goBack;
    private Boolean refresh;
    private final Map<Integer, Boolean> worldToggles = new HashMap<>();

    public HardcoreWorldSettingsPageEventData() {
    }

    public Boolean getGoBack() {
        return goBack;
    }

    public Boolean getRefresh() {
        return refresh;
    }

    public Map<Integer, Boolean> getWorldToggles() {
        return worldToggles;
    }

    public Boolean getWorldToggle(int index) {
        return worldToggles.get(index);
    }
}
