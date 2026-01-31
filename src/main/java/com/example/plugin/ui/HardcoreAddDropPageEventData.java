package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreAddDropPageEventData {
    public static final String KEY_CANCEL = "@Cancel";
    public static final String KEY_SAVE = "@Save";
    public static final String KEY_HOSTILE = "@Hostile";
    public static final String KEY_ELITE = "@Elite";
    public static final String KEY_MINIBOSS = "@Miniboss";
    public static final String KEY_WORLDBOSS = "@Worldboss";
    public static final String KEY_ITEM_ID = "@ItemId";
    public static final String KEY_MIN_QUANTITY = "@MinQuantity";
    public static final String KEY_MAX_QUANTITY = "@MaxQuantity";
    public static final String KEY_DROP_CHANCE = "@DropChance";

    public static final BuilderCodec<HardcoreAddDropPageEventData> CODEC = BuilderCodec
            .builder(HardcoreAddDropPageEventData.class, HardcoreAddDropPageEventData::new)
            .append(new KeyedCodec<>(KEY_CANCEL, Codec.BOOLEAN),
                    (data, value) -> data.cancel = value,
                    data -> data.cancel)
            .add()
            .append(new KeyedCodec<>(KEY_SAVE, Codec.BOOLEAN),
                    (data, value) -> data.save = value,
                    data -> data.save)
            .add()
            .append(new KeyedCodec<>(KEY_HOSTILE, Codec.BOOLEAN),
                    (data, value) -> data.hostile = value,
                    data -> data.hostile)
            .add()
            .append(new KeyedCodec<>(KEY_ELITE, Codec.BOOLEAN),
                    (data, value) -> data.elite = value,
                    data -> data.elite)
            .add()
            .append(new KeyedCodec<>(KEY_MINIBOSS, Codec.BOOLEAN),
                    (data, value) -> data.miniboss = value,
                    data -> data.miniboss)
            .add()
            .append(new KeyedCodec<>(KEY_WORLDBOSS, Codec.BOOLEAN),
                    (data, value) -> data.worldboss = value,
                    data -> data.worldboss)
            .add()
            .append(new KeyedCodec<>(KEY_ITEM_ID, Codec.STRING),
                    (data, value) -> data.itemId = value,
                    data -> data.itemId)
            .add()
            .append(new KeyedCodec<>(KEY_MIN_QUANTITY, Codec.FLOAT),
                    (data, value) -> data.minQuantity = value,
                    data -> data.minQuantity)
            .add()
            .append(new KeyedCodec<>(KEY_MAX_QUANTITY, Codec.FLOAT),
                    (data, value) -> data.maxQuantity = value,
                    data -> data.maxQuantity)
            .add()
            .append(new KeyedCodec<>(KEY_DROP_CHANCE, Codec.FLOAT),
                    (data, value) -> data.dropChance = value,
                    data -> data.dropChance)
            .add()
            .build();

    private Boolean cancel;
    private Boolean save;
    private Boolean hostile;
    private Boolean elite;
    private Boolean miniboss;
    private Boolean worldboss;
    private String itemId;
    private Float minQuantity;
    private Float maxQuantity;
    private Float dropChance;

    public HardcoreAddDropPageEventData() {
    }

    public Boolean getCancel() {
        return cancel;
    }

    public Boolean getSave() {
        return save;
    }

    public Boolean getHostile() {
        return hostile;
    }

    public Boolean getElite() {
        return elite;
    }

    public Boolean getMiniboss() {
        return miniboss;
    }

    public Boolean getWorldboss() {
        return worldboss;
    }

    public String getItemId() {
        return itemId;
    }

    public Float getMinQuantity() {
        return minQuantity;
    }

    public Float getMaxQuantity() {
        return maxQuantity;
    }

    public Float getDropChance() {
        return dropChance;
    }
}
