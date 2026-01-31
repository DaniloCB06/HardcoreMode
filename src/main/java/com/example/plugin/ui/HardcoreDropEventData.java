package com.example.plugin.ui;

import com.example.plugin.MobCategory;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreDropEventData {
    private final String categoryKey;
    private final String itemIdKey;
    private final String enabledKey;
    private final String removeKey;
    
    private Boolean enabled;
    private Boolean remove;
    
    public HardcoreDropEventData(MobCategory category, String itemId) {
        this.categoryKey = category.name();
        this.itemIdKey = itemId;
        this.enabledKey = HardcoreBloodMoonDropsPageEventData.KEY_DROP_ENABLED_PREFIX + categoryKey + "_" + itemId;
        this.removeKey = HardcoreBloodMoonDropsPageEventData.KEY_REMOVE_DROP_PREFIX + categoryKey + "_" + itemId;
    }
    
    public BuilderCodec<HardcoreDropEventData> createCodec() {
        return BuilderCodec
                .builder(HardcoreDropEventData.class, () -> new HardcoreDropEventData(
                    MobCategory.valueOf(categoryKey), itemIdKey))
                .append(new KeyedCodec<>(enabledKey, Codec.BOOLEAN),
                        (data, value) -> data.enabled = value,
                        data -> data.enabled)
                .add()
                .append(new KeyedCodec<>(removeKey, Codec.BOOLEAN),
                        (data, value) -> data.remove = value,
                        data -> data.remove)
                .add()
                .build();
    }
    
    public String getCategoryKey() {
        return categoryKey;
    }
    
    public String getItemIdKey() {
        return itemIdKey;
    }
    
    public String getEnabledKey() {
        return enabledKey;
    }
    
    public String getRemoveKey() {
        return removeKey;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public Boolean getRemove() {
        return remove;
    }
}
