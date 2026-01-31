package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class HardcoreBloodMoonDropsPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_DROP_ENABLED_PREFIX = "@DropEnabled_";
    public static final String KEY_REMOVE_DROP_PREFIX = "@RemoveDrop_";
    public static final String KEY_RELOAD_CONFIG = "@ReloadConfig";

    // Note: Dynamic event keys will be captured through individual KeyedCodec entries added at runtime
    // For now, we'll use a simpler approach with just the static keys
    public static final BuilderCodec<HardcoreBloodMoonDropsPageEventData> CODEC = BuilderCodec
            .builder(HardcoreBloodMoonDropsPageEventData.class, HardcoreBloodMoonDropsPageEventData::new)
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                    (data, value) -> data.goBack = value,
                    data -> data.goBack)
            .add()
            .append(new KeyedCodec<>(KEY_RELOAD_CONFIG, Codec.BOOLEAN),
                    (data, value) -> data.reloadConfig = value,
                    data -> data.reloadConfig)
            .add()
            .build();

    private Boolean goBack;
    private Boolean reloadConfig;
    private final Map<String, Boolean> dropChanges = new HashMap<>();

    public HardcoreBloodMoonDropsPageEventData() {
    }

    public Boolean getGoBack() {
        return goBack;
    }

    public void setGoBack(Boolean goBack) {
        this.goBack = goBack;
    }

    public Boolean getReloadConfig() {
        return reloadConfig;
    }

    public void setReloadConfig(Boolean reloadConfig) {
        this.reloadConfig = reloadConfig;
    }

    public void addDropChange(String key, Boolean value) {
        if (value != null) {
            dropChanges.put(key, value);
        }
    }

    public Boolean getDropChange(String key) {
        return dropChanges.get(key);
    }

    public Map<String, Boolean> getAllDropChanges() {
        return dropChanges;
    }
}
