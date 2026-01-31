package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreGeneralSettingsPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_OPEN_BLOOD_MOON_DROPS = "@OpenBloodMoonDrops";

    public static final BuilderCodec<HardcoreGeneralSettingsPageEventData> CODEC = BuilderCodec
            .builder(HardcoreGeneralSettingsPageEventData.class, HardcoreGeneralSettingsPageEventData::new)
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                    (data, value) -> data.goBack = value,
                    data -> data.goBack)
            .add()
            .append(new KeyedCodec<>(KEY_OPEN_BLOOD_MOON_DROPS, Codec.BOOLEAN),
                    (data, value) -> data.openBloodMoonDrops = value,
                    data -> data.openBloodMoonDrops)
            .add()
            .build();

    private Boolean goBack;
    private Boolean openBloodMoonDrops;

    public HardcoreGeneralSettingsPageEventData() {
    }

    public Boolean getGoBack() {
        return goBack;
    }

    public Boolean getOpenBloodMoonDrops() {
        return openBloodMoonDrops;
    }
}
