package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreRemoveDropPageEventData {
    public static final String KEY_CANCEL = "@Cancel";
    public static final String KEY_CONFIRM = "@Confirm";

    public static final BuilderCodec<HardcoreRemoveDropPageEventData> CODEC = BuilderCodec
            .builder(HardcoreRemoveDropPageEventData.class, HardcoreRemoveDropPageEventData::new)
            .append(new KeyedCodec<>(KEY_CANCEL, Codec.BOOLEAN),
                    (data, value) -> data.cancel = value,
                    data -> data.cancel)
            .add()
            .append(new KeyedCodec<>(KEY_CONFIRM, Codec.BOOLEAN),
                    (data, value) -> data.confirm = value,
                    data -> data.confirm)
            .add()
            .build();

    private Boolean cancel;
    private Boolean confirm;

    public HardcoreRemoveDropPageEventData() {
    }

    public Boolean getCancel() {
        return cancel;
    }

    public Boolean getConfirm() {
        return confirm;
    }
}
