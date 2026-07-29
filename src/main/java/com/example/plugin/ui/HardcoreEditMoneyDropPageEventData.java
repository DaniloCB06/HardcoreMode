package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreEditMoneyDropPageEventData {
    public static final String KEY_AMOUNT = "@Amount";
    public static final String KEY_ADJUST_ACTION = "@AdjustAction";
    public static final String KEY_CANCEL = "@Cancel";
    public static final String KEY_SAVE = "@Save";

    public static final BuilderCodec<HardcoreEditMoneyDropPageEventData> CODEC = BuilderCodec
            .builder(HardcoreEditMoneyDropPageEventData.class, HardcoreEditMoneyDropPageEventData::new)
            .append(new KeyedCodec<>(KEY_AMOUNT, Codec.STRING),
                    (data, value) -> data.amount = value,
                    data -> data.amount)
            .add()
            .append(new KeyedCodec<>(KEY_ADJUST_ACTION, Codec.STRING),
                    (data, value) -> data.adjustAction = value,
                    data -> data.adjustAction)
            .add()
            .append(new KeyedCodec<>(KEY_CANCEL, Codec.BOOLEAN),
                    (data, value) -> data.cancel = value,
                    data -> data.cancel)
            .add()
            .append(new KeyedCodec<>(KEY_SAVE, Codec.BOOLEAN),
                    (data, value) -> data.save = value,
                    data -> data.save)
            .add()
            .build();

    private String amount;
    private String adjustAction;
    private Boolean cancel;
    private Boolean save;

    public HardcoreEditMoneyDropPageEventData() {
    }

    public String getAmount() {
        return amount;
    }

    public String getAdjustAction() {
        return adjustAction;
    }

    public Boolean getCancel() {
        return cancel;
    }

    public Boolean getSave() {
        return save;
    }
}
