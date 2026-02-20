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
    public static final String KEY_ADD_ENTRY = "@AddEntry";

    public static final String KEY_REMOVE_0 = "@Remove0";
    public static final String KEY_REMOVE_1 = "@Remove1";
    public static final String KEY_REMOVE_2 = "@Remove2";
    public static final String KEY_REMOVE_3 = "@Remove3";
    public static final String KEY_REMOVE_4 = "@Remove4";
    public static final String KEY_REMOVE_5 = "@Remove5";
    public static final String KEY_REMOVE_6 = "@Remove6";
    public static final String KEY_REMOVE_7 = "@Remove7";
    public static final String KEY_REMOVE_8 = "@Remove8";
    public static final String KEY_REMOVE_9 = "@Remove9";
    public static final String KEY_REMOVE_10 = "@Remove10";
    public static final String KEY_REMOVE_11 = "@Remove11";
    public static final String KEY_REMOVE_12 = "@Remove12";
    public static final String KEY_REMOVE_13 = "@Remove13";

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
            .append(new KeyedCodec<>(KEY_ADD_ENTRY, Codec.BOOLEAN),
                    (data, value) -> data.addEntry = value,
                    data -> data.addEntry)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_0, Codec.BOOLEAN),
                    (data, value) -> data.remove0 = value,
                    data -> data.remove0)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_1, Codec.BOOLEAN),
                    (data, value) -> data.remove1 = value,
                    data -> data.remove1)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_2, Codec.BOOLEAN),
                    (data, value) -> data.remove2 = value,
                    data -> data.remove2)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_3, Codec.BOOLEAN),
                    (data, value) -> data.remove3 = value,
                    data -> data.remove3)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_4, Codec.BOOLEAN),
                    (data, value) -> data.remove4 = value,
                    data -> data.remove4)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_5, Codec.BOOLEAN),
                    (data, value) -> data.remove5 = value,
                    data -> data.remove5)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_6, Codec.BOOLEAN),
                    (data, value) -> data.remove6 = value,
                    data -> data.remove6)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_7, Codec.BOOLEAN),
                    (data, value) -> data.remove7 = value,
                    data -> data.remove7)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_8, Codec.BOOLEAN),
                    (data, value) -> data.remove8 = value,
                    data -> data.remove8)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_9, Codec.BOOLEAN),
                    (data, value) -> data.remove9 = value,
                    data -> data.remove9)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_10, Codec.BOOLEAN),
                    (data, value) -> data.remove10 = value,
                    data -> data.remove10)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_11, Codec.BOOLEAN),
                    (data, value) -> data.remove11 = value,
                    data -> data.remove11)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_12, Codec.BOOLEAN),
                    (data, value) -> data.remove12 = value,
                    data -> data.remove12)
            .add()
            .append(new KeyedCodec<>(KEY_REMOVE_13, Codec.BOOLEAN),
                    (data, value) -> data.remove13 = value,
                    data -> data.remove13)
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
    private Boolean addEntry;
    private Boolean remove0, remove1, remove2, remove3, remove4, remove5, remove6, remove7;
    private Boolean remove8, remove9, remove10, remove11, remove12, remove13;

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

    public Boolean getAddEntry() {
        return addEntry;
    }

    public int getRemoveRowIndex() {
        if (Boolean.TRUE.equals(remove0)) return 0;
        if (Boolean.TRUE.equals(remove1)) return 1;
        if (Boolean.TRUE.equals(remove2)) return 2;
        if (Boolean.TRUE.equals(remove3)) return 3;
        if (Boolean.TRUE.equals(remove4)) return 4;
        if (Boolean.TRUE.equals(remove5)) return 5;
        if (Boolean.TRUE.equals(remove6)) return 6;
        if (Boolean.TRUE.equals(remove7)) return 7;
        if (Boolean.TRUE.equals(remove8)) return 8;
        if (Boolean.TRUE.equals(remove9)) return 9;
        if (Boolean.TRUE.equals(remove10)) return 10;
        if (Boolean.TRUE.equals(remove11)) return 11;
        if (Boolean.TRUE.equals(remove12)) return 12;
        if (Boolean.TRUE.equals(remove13)) return 13;
        return -1;
    }

    public static String getRemoveKeyForRow(int rowIndex) {
        switch (rowIndex) {
            case 0: return KEY_REMOVE_0;
            case 1: return KEY_REMOVE_1;
            case 2: return KEY_REMOVE_2;
            case 3: return KEY_REMOVE_3;
            case 4: return KEY_REMOVE_4;
            case 5: return KEY_REMOVE_5;
            case 6: return KEY_REMOVE_6;
            case 7: return KEY_REMOVE_7;
            case 8: return KEY_REMOVE_8;
            case 9: return KEY_REMOVE_9;
            case 10: return KEY_REMOVE_10;
            case 11: return KEY_REMOVE_11;
            case 12: return KEY_REMOVE_12;
            case 13: return KEY_REMOVE_13;
            default: return null;
        }
    }
}
