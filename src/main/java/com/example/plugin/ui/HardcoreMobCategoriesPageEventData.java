package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreMobCategoriesPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_RELOAD_CONFIG = "@ReloadConfig";
    public static final String KEY_FILTER_ALL = "@FilterAll";
    public static final String KEY_FILTER_HOSTILE = "@FilterHostile";
    public static final String KEY_FILTER_ELITE = "@FilterElite";
    public static final String KEY_FILTER_MINIBOSS = "@FilterMiniboss";
    public static final String KEY_FILTER_WORLDBOSS = "@FilterWorldboss";
    public static final String KEY_FILTER_PASSIVE = "@FilterPassive";
    public static final String KEY_FILTER_CRITTER = "@FilterCritter";
    public static final String KEY_PREV_PAGE = "@PrevPage";
    public static final String KEY_NEXT_PAGE = "@NextPage";

    public static final BuilderCodec<HardcoreMobCategoriesPageEventData> CODEC = BuilderCodec
            .builder(HardcoreMobCategoriesPageEventData.class, HardcoreMobCategoriesPageEventData::new)
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                    (data, value) -> data.goBack = value,
                    data -> data.goBack)
            .add()
            .append(new KeyedCodec<>(KEY_RELOAD_CONFIG, Codec.BOOLEAN),
                    (data, value) -> data.reloadConfig = value,
                    data -> data.reloadConfig)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_ALL, Codec.BOOLEAN),
                    (data, value) -> data.filterAll = value,
                    data -> data.filterAll)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_HOSTILE, Codec.BOOLEAN),
                    (data, value) -> data.filterHostile = value,
                    data -> data.filterHostile)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_ELITE, Codec.BOOLEAN),
                    (data, value) -> data.filterElite = value,
                    data -> data.filterElite)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_MINIBOSS, Codec.BOOLEAN),
                    (data, value) -> data.filterMiniboss = value,
                    data -> data.filterMiniboss)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_WORLDBOSS, Codec.BOOLEAN),
                    (data, value) -> data.filterWorldboss = value,
                    data -> data.filterWorldboss)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_PASSIVE, Codec.BOOLEAN),
                    (data, value) -> data.filterPassive = value,
                    data -> data.filterPassive)
            .add()
            .append(new KeyedCodec<>(KEY_FILTER_CRITTER, Codec.BOOLEAN),
                    (data, value) -> data.filterCritter = value,
                    data -> data.filterCritter)
            .add()
            .append(new KeyedCodec<>(KEY_PREV_PAGE, Codec.BOOLEAN),
                    (data, value) -> data.prevPage = value,
                    data -> data.prevPage)
            .add()
            .append(new KeyedCodec<>(KEY_NEXT_PAGE, Codec.BOOLEAN),
                    (data, value) -> data.nextPage = value,
                    data -> data.nextPage)
            .add()
            .build();

    private Boolean goBack;
    private Boolean reloadConfig;
    private Boolean filterAll;
    private Boolean filterHostile;
    private Boolean filterElite;
    private Boolean filterMiniboss;
    private Boolean filterWorldboss;
    private Boolean filterPassive;
    private Boolean filterCritter;
    private Boolean prevPage;
    private Boolean nextPage;

    public HardcoreMobCategoriesPageEventData() {
    }

    public Boolean getGoBack() {
        return goBack;
    }

    public Boolean getReloadConfig() {
        return reloadConfig;
    }

    public Boolean getFilterAll() {
        return filterAll;
    }

    public Boolean getFilterHostile() {
        return filterHostile;
    }

    public Boolean getFilterElite() {
        return filterElite;
    }

    public Boolean getFilterMiniboss() {
        return filterMiniboss;
    }

    public Boolean getFilterWorldboss() {
        return filterWorldboss;
    }

    public Boolean getFilterPassive() {
        return filterPassive;
    }

    public Boolean getFilterCritter() {
        return filterCritter;
    }

    public Boolean getPrevPage() {
        return prevPage;
    }

    public Boolean getNextPage() {
        return nextPage;
    }
}
