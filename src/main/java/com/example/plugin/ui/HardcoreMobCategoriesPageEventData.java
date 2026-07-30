package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreMobCategoriesPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_RELOAD_CONFIG = "@ReloadConfig";
    public static final String KEY_SEARCH_TEXT = "@SearchText";
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

    public static final String KEY_EDIT_0 = "@Edit0";
    public static final String KEY_EDIT_1 = "@Edit1";
    public static final String KEY_EDIT_2 = "@Edit2";
    public static final String KEY_EDIT_3 = "@Edit3";
    public static final String KEY_EDIT_4 = "@Edit4";
    public static final String KEY_EDIT_5 = "@Edit5";
    public static final String KEY_EDIT_6 = "@Edit6";
    public static final String KEY_EDIT_7 = "@Edit7";
    public static final String KEY_EDIT_8 = "@Edit8";
    public static final String KEY_EDIT_9 = "@Edit9";
    public static final String KEY_EDIT_10 = "@Edit10";
    public static final String KEY_EDIT_11 = "@Edit11";
    public static final String KEY_EDIT_12 = "@Edit12";
    public static final String KEY_EDIT_13 = "@Edit13";
    public static final String KEY_EDIT_14 = "@Edit14";
    public static final String KEY_EDIT_15 = "@Edit15";
    public static final String KEY_EDIT_16 = "@Edit16";
    public static final String KEY_EDIT_17 = "@Edit17";

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
    public static final String KEY_REMOVE_14 = "@Remove14";
    public static final String KEY_REMOVE_15 = "@Remove15";
    public static final String KEY_REMOVE_16 = "@Remove16";
    public static final String KEY_REMOVE_17 = "@Remove17";

    public static final BuilderCodec<HardcoreMobCategoriesPageEventData> CODEC = BuilderCodec
            .builder(HardcoreMobCategoriesPageEventData.class, HardcoreMobCategoriesPageEventData::new)
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN), (data, value) -> data.goBack = value, data -> data.goBack).add()
            .append(new KeyedCodec<>(KEY_RELOAD_CONFIG, Codec.BOOLEAN), (data, value) -> data.reloadConfig = value, data -> data.reloadConfig).add()
            .append(new KeyedCodec<>(KEY_SEARCH_TEXT, Codec.STRING), (data, value) -> data.searchText = value, data -> data.searchText).add()
            .append(new KeyedCodec<>(KEY_FILTER_ALL, Codec.BOOLEAN), (data, value) -> data.filterAll = value, data -> data.filterAll).add()
            .append(new KeyedCodec<>(KEY_FILTER_HOSTILE, Codec.BOOLEAN), (data, value) -> data.filterHostile = value, data -> data.filterHostile).add()
            .append(new KeyedCodec<>(KEY_FILTER_ELITE, Codec.BOOLEAN), (data, value) -> data.filterElite = value, data -> data.filterElite).add()
            .append(new KeyedCodec<>(KEY_FILTER_MINIBOSS, Codec.BOOLEAN), (data, value) -> data.filterMiniboss = value, data -> data.filterMiniboss).add()
            .append(new KeyedCodec<>(KEY_FILTER_WORLDBOSS, Codec.BOOLEAN), (data, value) -> data.filterWorldboss = value, data -> data.filterWorldboss).add()
            .append(new KeyedCodec<>(KEY_FILTER_PASSIVE, Codec.BOOLEAN), (data, value) -> data.filterPassive = value, data -> data.filterPassive).add()
            .append(new KeyedCodec<>(KEY_FILTER_CRITTER, Codec.BOOLEAN), (data, value) -> data.filterCritter = value, data -> data.filterCritter).add()
            .append(new KeyedCodec<>(KEY_PREV_PAGE, Codec.BOOLEAN), (data, value) -> data.prevPage = value, data -> data.prevPage).add()
            .append(new KeyedCodec<>(KEY_NEXT_PAGE, Codec.BOOLEAN), (data, value) -> data.nextPage = value, data -> data.nextPage).add()
            .append(new KeyedCodec<>(KEY_ADD_ENTRY, Codec.BOOLEAN), (data, value) -> data.addEntry = value, data -> data.addEntry).add()
            .append(new KeyedCodec<>(KEY_EDIT_0, Codec.BOOLEAN), (data, value) -> data.edit0 = value, data -> data.edit0).add()
            .append(new KeyedCodec<>(KEY_EDIT_1, Codec.BOOLEAN), (data, value) -> data.edit1 = value, data -> data.edit1).add()
            .append(new KeyedCodec<>(KEY_EDIT_2, Codec.BOOLEAN), (data, value) -> data.edit2 = value, data -> data.edit2).add()
            .append(new KeyedCodec<>(KEY_EDIT_3, Codec.BOOLEAN), (data, value) -> data.edit3 = value, data -> data.edit3).add()
            .append(new KeyedCodec<>(KEY_EDIT_4, Codec.BOOLEAN), (data, value) -> data.edit4 = value, data -> data.edit4).add()
            .append(new KeyedCodec<>(KEY_EDIT_5, Codec.BOOLEAN), (data, value) -> data.edit5 = value, data -> data.edit5).add()
            .append(new KeyedCodec<>(KEY_EDIT_6, Codec.BOOLEAN), (data, value) -> data.edit6 = value, data -> data.edit6).add()
            .append(new KeyedCodec<>(KEY_EDIT_7, Codec.BOOLEAN), (data, value) -> data.edit7 = value, data -> data.edit7).add()
            .append(new KeyedCodec<>(KEY_EDIT_8, Codec.BOOLEAN), (data, value) -> data.edit8 = value, data -> data.edit8).add()
            .append(new KeyedCodec<>(KEY_EDIT_9, Codec.BOOLEAN), (data, value) -> data.edit9 = value, data -> data.edit9).add()
            .append(new KeyedCodec<>(KEY_EDIT_10, Codec.BOOLEAN), (data, value) -> data.edit10 = value, data -> data.edit10).add()
            .append(new KeyedCodec<>(KEY_EDIT_11, Codec.BOOLEAN), (data, value) -> data.edit11 = value, data -> data.edit11).add()
            .append(new KeyedCodec<>(KEY_EDIT_12, Codec.BOOLEAN), (data, value) -> data.edit12 = value, data -> data.edit12).add()
            .append(new KeyedCodec<>(KEY_EDIT_13, Codec.BOOLEAN), (data, value) -> data.edit13 = value, data -> data.edit13).add()
            .append(new KeyedCodec<>(KEY_EDIT_14, Codec.BOOLEAN), (data, value) -> data.edit14 = value, data -> data.edit14).add()
            .append(new KeyedCodec<>(KEY_EDIT_15, Codec.BOOLEAN), (data, value) -> data.edit15 = value, data -> data.edit15).add()
            .append(new KeyedCodec<>(KEY_EDIT_16, Codec.BOOLEAN), (data, value) -> data.edit16 = value, data -> data.edit16).add()
            .append(new KeyedCodec<>(KEY_EDIT_17, Codec.BOOLEAN), (data, value) -> data.edit17 = value, data -> data.edit17).add()
            .append(new KeyedCodec<>(KEY_REMOVE_0, Codec.BOOLEAN), (data, value) -> data.remove0 = value, data -> data.remove0).add()
            .append(new KeyedCodec<>(KEY_REMOVE_1, Codec.BOOLEAN), (data, value) -> data.remove1 = value, data -> data.remove1).add()
            .append(new KeyedCodec<>(KEY_REMOVE_2, Codec.BOOLEAN), (data, value) -> data.remove2 = value, data -> data.remove2).add()
            .append(new KeyedCodec<>(KEY_REMOVE_3, Codec.BOOLEAN), (data, value) -> data.remove3 = value, data -> data.remove3).add()
            .append(new KeyedCodec<>(KEY_REMOVE_4, Codec.BOOLEAN), (data, value) -> data.remove4 = value, data -> data.remove4).add()
            .append(new KeyedCodec<>(KEY_REMOVE_5, Codec.BOOLEAN), (data, value) -> data.remove5 = value, data -> data.remove5).add()
            .append(new KeyedCodec<>(KEY_REMOVE_6, Codec.BOOLEAN), (data, value) -> data.remove6 = value, data -> data.remove6).add()
            .append(new KeyedCodec<>(KEY_REMOVE_7, Codec.BOOLEAN), (data, value) -> data.remove7 = value, data -> data.remove7).add()
            .append(new KeyedCodec<>(KEY_REMOVE_8, Codec.BOOLEAN), (data, value) -> data.remove8 = value, data -> data.remove8).add()
            .append(new KeyedCodec<>(KEY_REMOVE_9, Codec.BOOLEAN), (data, value) -> data.remove9 = value, data -> data.remove9).add()
            .append(new KeyedCodec<>(KEY_REMOVE_10, Codec.BOOLEAN), (data, value) -> data.remove10 = value, data -> data.remove10).add()
            .append(new KeyedCodec<>(KEY_REMOVE_11, Codec.BOOLEAN), (data, value) -> data.remove11 = value, data -> data.remove11).add()
            .append(new KeyedCodec<>(KEY_REMOVE_12, Codec.BOOLEAN), (data, value) -> data.remove12 = value, data -> data.remove12).add()
            .append(new KeyedCodec<>(KEY_REMOVE_13, Codec.BOOLEAN), (data, value) -> data.remove13 = value, data -> data.remove13).add()
            .append(new KeyedCodec<>(KEY_REMOVE_14, Codec.BOOLEAN), (data, value) -> data.remove14 = value, data -> data.remove14).add()
            .append(new KeyedCodec<>(KEY_REMOVE_15, Codec.BOOLEAN), (data, value) -> data.remove15 = value, data -> data.remove15).add()
            .append(new KeyedCodec<>(KEY_REMOVE_16, Codec.BOOLEAN), (data, value) -> data.remove16 = value, data -> data.remove16).add()
            .append(new KeyedCodec<>(KEY_REMOVE_17, Codec.BOOLEAN), (data, value) -> data.remove17 = value, data -> data.remove17).add()
            .build();

    private Boolean goBack;
    private Boolean reloadConfig;
    private String searchText;
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
    private Boolean edit0, edit1, edit2, edit3, edit4, edit5, edit6, edit7, edit8, edit9;
    private Boolean edit10, edit11, edit12, edit13, edit14;
    private Boolean edit15, edit16, edit17;
    private Boolean remove0, remove1, remove2, remove3, remove4, remove5, remove6, remove7, remove8, remove9;
    private Boolean remove10, remove11, remove12, remove13, remove14;
    private Boolean remove15, remove16, remove17;

    public HardcoreMobCategoriesPageEventData() {
    }

    public Boolean getGoBack() { return goBack; }
    public Boolean getReloadConfig() { return reloadConfig; }
    public String getSearchText() { return searchText; }
    public Boolean getFilterAll() { return filterAll; }
    public Boolean getFilterHostile() { return filterHostile; }
    public Boolean getFilterElite() { return filterElite; }
    public Boolean getFilterMiniboss() { return filterMiniboss; }
    public Boolean getFilterWorldboss() { return filterWorldboss; }
    public Boolean getFilterPassive() { return filterPassive; }
    public Boolean getFilterCritter() { return filterCritter; }
    public Boolean getPrevPage() { return prevPage; }
    public Boolean getNextPage() { return nextPage; }
    public Boolean getAddEntry() { return addEntry; }

    public int getEditRowIndex() {
        if (Boolean.TRUE.equals(edit0)) return 0;
        if (Boolean.TRUE.equals(edit1)) return 1;
        if (Boolean.TRUE.equals(edit2)) return 2;
        if (Boolean.TRUE.equals(edit3)) return 3;
        if (Boolean.TRUE.equals(edit4)) return 4;
        if (Boolean.TRUE.equals(edit5)) return 5;
        if (Boolean.TRUE.equals(edit6)) return 6;
        if (Boolean.TRUE.equals(edit7)) return 7;
        if (Boolean.TRUE.equals(edit8)) return 8;
        if (Boolean.TRUE.equals(edit9)) return 9;
        if (Boolean.TRUE.equals(edit10)) return 10;
        if (Boolean.TRUE.equals(edit11)) return 11;
        if (Boolean.TRUE.equals(edit12)) return 12;
        if (Boolean.TRUE.equals(edit13)) return 13;
        if (Boolean.TRUE.equals(edit14)) return 14;
        if (Boolean.TRUE.equals(edit15)) return 15;
        if (Boolean.TRUE.equals(edit16)) return 16;
        if (Boolean.TRUE.equals(edit17)) return 17;
        return -1;
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
        if (Boolean.TRUE.equals(remove14)) return 14;
        if (Boolean.TRUE.equals(remove15)) return 15;
        if (Boolean.TRUE.equals(remove16)) return 16;
        if (Boolean.TRUE.equals(remove17)) return 17;
        return -1;
    }

    public static String getEditKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_EDIT_0;
            case 1 -> KEY_EDIT_1;
            case 2 -> KEY_EDIT_2;
            case 3 -> KEY_EDIT_3;
            case 4 -> KEY_EDIT_4;
            case 5 -> KEY_EDIT_5;
            case 6 -> KEY_EDIT_6;
            case 7 -> KEY_EDIT_7;
            case 8 -> KEY_EDIT_8;
            case 9 -> KEY_EDIT_9;
            case 10 -> KEY_EDIT_10;
            case 11 -> KEY_EDIT_11;
            case 12 -> KEY_EDIT_12;
            case 13 -> KEY_EDIT_13;
            case 14 -> KEY_EDIT_14;
            case 15 -> KEY_EDIT_15;
            case 16 -> KEY_EDIT_16;
            case 17 -> KEY_EDIT_17;
            default -> null;
        };
    }

    public static String getRemoveKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_REMOVE_0;
            case 1 -> KEY_REMOVE_1;
            case 2 -> KEY_REMOVE_2;
            case 3 -> KEY_REMOVE_3;
            case 4 -> KEY_REMOVE_4;
            case 5 -> KEY_REMOVE_5;
            case 6 -> KEY_REMOVE_6;
            case 7 -> KEY_REMOVE_7;
            case 8 -> KEY_REMOVE_8;
            case 9 -> KEY_REMOVE_9;
            case 10 -> KEY_REMOVE_10;
            case 11 -> KEY_REMOVE_11;
            case 12 -> KEY_REMOVE_12;
            case 13 -> KEY_REMOVE_13;
            case 14 -> KEY_REMOVE_14;
            case 15 -> KEY_REMOVE_15;
            case 16 -> KEY_REMOVE_16;
            case 17 -> KEY_REMOVE_17;
            default -> null;
        };
    }
}
