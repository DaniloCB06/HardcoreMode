package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreAddMobCategoryPageEventData {
    public static final String KEY_CANCEL = "@Cancel";
    public static final String KEY_SAVE = "@Save";
    public static final String KEY_PATTERN = "@Pattern";
    public static final String KEY_HOSTILE = "@Hostile";
    public static final String KEY_ELITE = "@Elite";
    public static final String KEY_MINIBOSS = "@Miniboss";
    public static final String KEY_WORLDBOSS = "@Worldboss";
    public static final String KEY_PASSIVE = "@Passive";
    public static final String KEY_CRITTER = "@Critter";

    public static final BuilderCodec<HardcoreAddMobCategoryPageEventData> CODEC = BuilderCodec
            .builder(HardcoreAddMobCategoryPageEventData.class, HardcoreAddMobCategoryPageEventData::new)
            .append(new KeyedCodec<>(KEY_CANCEL, Codec.BOOLEAN),
                    (data, value) -> data.cancel = value,
                    data -> data.cancel)
            .add()
            .append(new KeyedCodec<>(KEY_SAVE, Codec.BOOLEAN),
                    (data, value) -> data.save = value,
                    data -> data.save)
            .add()
            .append(new KeyedCodec<>(KEY_PATTERN, Codec.STRING),
                    (data, value) -> data.pattern = value,
                    data -> data.pattern)
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
            .append(new KeyedCodec<>(KEY_PASSIVE, Codec.BOOLEAN),
                    (data, value) -> data.passive = value,
                    data -> data.passive)
            .add()
            .append(new KeyedCodec<>(KEY_CRITTER, Codec.BOOLEAN),
                    (data, value) -> data.critter = value,
                    data -> data.critter)
            .add()
            .build();

    private Boolean cancel;
    private Boolean save;
    private String pattern;
    private Boolean hostile;
    private Boolean elite;
    private Boolean miniboss;
    private Boolean worldboss;
    private Boolean passive;
    private Boolean critter;

    public HardcoreAddMobCategoryPageEventData() {
    }

    public Boolean getCancel() {
        return cancel;
    }

    public Boolean getSave() {
        return save;
    }

    public String getPattern() {
        return pattern;
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

    public Boolean getPassive() {
        return passive;
    }

    public Boolean getCritter() {
        return critter;
    }
}
