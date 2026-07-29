package com.example.plugin.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HardcoreMoneyMobDropsPageEventData {
    public static final String KEY_GO_BACK = "@GoBack";
    public static final String KEY_RELOAD_CONFIG = "@ReloadConfig";
    public static final String KEY_MONEY_DROPS_ENABLED = "@MoneyDropsEnabled";
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

    public static final String KEY_CATEGORY_EDIT_0 = "@CategoryEdit0";
    public static final String KEY_CATEGORY_EDIT_1 = "@CategoryEdit1";
    public static final String KEY_CATEGORY_EDIT_2 = "@CategoryEdit2";
    public static final String KEY_CATEGORY_EDIT_3 = "@CategoryEdit3";
    public static final String KEY_CATEGORY_EDIT_4 = "@CategoryEdit4";
    public static final String KEY_CATEGORY_EDIT_5 = "@CategoryEdit5";
    public static final String KEY_CATEGORY_CLEAR_0 = "@CategoryClear0";
    public static final String KEY_CATEGORY_CLEAR_1 = "@CategoryClear1";
    public static final String KEY_CATEGORY_CLEAR_2 = "@CategoryClear2";
    public static final String KEY_CATEGORY_CLEAR_3 = "@CategoryClear3";
    public static final String KEY_CATEGORY_CLEAR_4 = "@CategoryClear4";
    public static final String KEY_CATEGORY_CLEAR_5 = "@CategoryClear5";

    public static final String KEY_MOB_EDIT_0 = "@MobEdit0";
    public static final String KEY_MOB_EDIT_1 = "@MobEdit1";
    public static final String KEY_MOB_EDIT_2 = "@MobEdit2";
    public static final String KEY_MOB_EDIT_3 = "@MobEdit3";
    public static final String KEY_MOB_EDIT_4 = "@MobEdit4";
    public static final String KEY_MOB_EDIT_5 = "@MobEdit5";
    public static final String KEY_MOB_EDIT_6 = "@MobEdit6";
    public static final String KEY_MOB_EDIT_7 = "@MobEdit7";
    public static final String KEY_MOB_EDIT_8 = "@MobEdit8";
    public static final String KEY_MOB_EDIT_9 = "@MobEdit9";
    public static final String KEY_MOB_CLEAR_0 = "@MobClear0";
    public static final String KEY_MOB_CLEAR_1 = "@MobClear1";
    public static final String KEY_MOB_CLEAR_2 = "@MobClear2";
    public static final String KEY_MOB_CLEAR_3 = "@MobClear3";
    public static final String KEY_MOB_CLEAR_4 = "@MobClear4";
    public static final String KEY_MOB_CLEAR_5 = "@MobClear5";
    public static final String KEY_MOB_CLEAR_6 = "@MobClear6";
    public static final String KEY_MOB_CLEAR_7 = "@MobClear7";
    public static final String KEY_MOB_CLEAR_8 = "@MobClear8";
    public static final String KEY_MOB_CLEAR_9 = "@MobClear9";

    public static final BuilderCodec<HardcoreMoneyMobDropsPageEventData> CODEC = BuilderCodec
            .builder(HardcoreMoneyMobDropsPageEventData.class, HardcoreMoneyMobDropsPageEventData::new)
            .append(new KeyedCodec<>(KEY_GO_BACK, Codec.BOOLEAN),
                    (data, value) -> data.goBack = value,
                    data -> data.goBack)
            .add()
            .append(new KeyedCodec<>(KEY_RELOAD_CONFIG, Codec.BOOLEAN),
                    (data, value) -> data.reloadConfig = value,
                    data -> data.reloadConfig)
            .add()
            .append(new KeyedCodec<>(KEY_MONEY_DROPS_ENABLED, Codec.BOOLEAN),
                    (data, value) -> data.moneyDropsEnabled = value,
                    data -> data.moneyDropsEnabled)
            .add()
            .append(new KeyedCodec<>(KEY_SEARCH_TEXT, Codec.STRING),
                    (data, value) -> data.searchText = value,
                    data -> data.searchText)
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
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_0, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit0 = value,
                    data -> data.categoryEdit0)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_1, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit1 = value,
                    data -> data.categoryEdit1)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_2, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit2 = value,
                    data -> data.categoryEdit2)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_3, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit3 = value,
                    data -> data.categoryEdit3)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_4, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit4 = value,
                    data -> data.categoryEdit4)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_EDIT_5, Codec.BOOLEAN),
                    (data, value) -> data.categoryEdit5 = value,
                    data -> data.categoryEdit5)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_0, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear0 = value,
                    data -> data.categoryClear0)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_1, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear1 = value,
                    data -> data.categoryClear1)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_2, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear2 = value,
                    data -> data.categoryClear2)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_3, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear3 = value,
                    data -> data.categoryClear3)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_4, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear4 = value,
                    data -> data.categoryClear4)
            .add()
            .append(new KeyedCodec<>(KEY_CATEGORY_CLEAR_5, Codec.BOOLEAN),
                    (data, value) -> data.categoryClear5 = value,
                    data -> data.categoryClear5)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_0, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit0 = value,
                    data -> data.mobEdit0)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_1, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit1 = value,
                    data -> data.mobEdit1)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_2, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit2 = value,
                    data -> data.mobEdit2)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_3, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit3 = value,
                    data -> data.mobEdit3)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_4, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit4 = value,
                    data -> data.mobEdit4)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_5, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit5 = value,
                    data -> data.mobEdit5)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_6, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit6 = value,
                    data -> data.mobEdit6)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_7, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit7 = value,
                    data -> data.mobEdit7)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_8, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit8 = value,
                    data -> data.mobEdit8)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_EDIT_9, Codec.BOOLEAN),
                    (data, value) -> data.mobEdit9 = value,
                    data -> data.mobEdit9)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_0, Codec.BOOLEAN),
                    (data, value) -> data.mobClear0 = value,
                    data -> data.mobClear0)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_1, Codec.BOOLEAN),
                    (data, value) -> data.mobClear1 = value,
                    data -> data.mobClear1)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_2, Codec.BOOLEAN),
                    (data, value) -> data.mobClear2 = value,
                    data -> data.mobClear2)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_3, Codec.BOOLEAN),
                    (data, value) -> data.mobClear3 = value,
                    data -> data.mobClear3)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_4, Codec.BOOLEAN),
                    (data, value) -> data.mobClear4 = value,
                    data -> data.mobClear4)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_5, Codec.BOOLEAN),
                    (data, value) -> data.mobClear5 = value,
                    data -> data.mobClear5)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_6, Codec.BOOLEAN),
                    (data, value) -> data.mobClear6 = value,
                    data -> data.mobClear6)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_7, Codec.BOOLEAN),
                    (data, value) -> data.mobClear7 = value,
                    data -> data.mobClear7)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_8, Codec.BOOLEAN),
                    (data, value) -> data.mobClear8 = value,
                    data -> data.mobClear8)
            .add()
            .append(new KeyedCodec<>(KEY_MOB_CLEAR_9, Codec.BOOLEAN),
                    (data, value) -> data.mobClear9 = value,
                    data -> data.mobClear9)
            .add()
            .build();

    private Boolean goBack;
    private Boolean reloadConfig;
    private Boolean moneyDropsEnabled;
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
    private Boolean categoryEdit0, categoryEdit1, categoryEdit2, categoryEdit3, categoryEdit4, categoryEdit5;
    private Boolean categoryClear0, categoryClear1, categoryClear2, categoryClear3, categoryClear4, categoryClear5;
    private Boolean mobEdit0, mobEdit1, mobEdit2, mobEdit3, mobEdit4, mobEdit5, mobEdit6, mobEdit7, mobEdit8, mobEdit9;
    private Boolean mobClear0, mobClear1, mobClear2, mobClear3, mobClear4, mobClear5, mobClear6, mobClear7, mobClear8, mobClear9;

    public HardcoreMoneyMobDropsPageEventData() {
    }

    public Boolean getGoBack() { return goBack; }
    public Boolean getReloadConfig() { return reloadConfig; }
    public Boolean getMoneyDropsEnabled() { return moneyDropsEnabled; }
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

    public int getCategoryEditRowIndex() {
        if (Boolean.TRUE.equals(categoryEdit0)) return 0;
        if (Boolean.TRUE.equals(categoryEdit1)) return 1;
        if (Boolean.TRUE.equals(categoryEdit2)) return 2;
        if (Boolean.TRUE.equals(categoryEdit3)) return 3;
        if (Boolean.TRUE.equals(categoryEdit4)) return 4;
        if (Boolean.TRUE.equals(categoryEdit5)) return 5;
        return -1;
    }

    public int getCategoryClearRowIndex() {
        if (Boolean.TRUE.equals(categoryClear0)) return 0;
        if (Boolean.TRUE.equals(categoryClear1)) return 1;
        if (Boolean.TRUE.equals(categoryClear2)) return 2;
        if (Boolean.TRUE.equals(categoryClear3)) return 3;
        if (Boolean.TRUE.equals(categoryClear4)) return 4;
        if (Boolean.TRUE.equals(categoryClear5)) return 5;
        return -1;
    }

    public int getMobEditRowIndex() {
        if (Boolean.TRUE.equals(mobEdit0)) return 0;
        if (Boolean.TRUE.equals(mobEdit1)) return 1;
        if (Boolean.TRUE.equals(mobEdit2)) return 2;
        if (Boolean.TRUE.equals(mobEdit3)) return 3;
        if (Boolean.TRUE.equals(mobEdit4)) return 4;
        if (Boolean.TRUE.equals(mobEdit5)) return 5;
        if (Boolean.TRUE.equals(mobEdit6)) return 6;
        if (Boolean.TRUE.equals(mobEdit7)) return 7;
        if (Boolean.TRUE.equals(mobEdit8)) return 8;
        if (Boolean.TRUE.equals(mobEdit9)) return 9;
        return -1;
    }

    public int getMobClearRowIndex() {
        if (Boolean.TRUE.equals(mobClear0)) return 0;
        if (Boolean.TRUE.equals(mobClear1)) return 1;
        if (Boolean.TRUE.equals(mobClear2)) return 2;
        if (Boolean.TRUE.equals(mobClear3)) return 3;
        if (Boolean.TRUE.equals(mobClear4)) return 4;
        if (Boolean.TRUE.equals(mobClear5)) return 5;
        if (Boolean.TRUE.equals(mobClear6)) return 6;
        if (Boolean.TRUE.equals(mobClear7)) return 7;
        if (Boolean.TRUE.equals(mobClear8)) return 8;
        if (Boolean.TRUE.equals(mobClear9)) return 9;
        return -1;
    }

    public static String getCategoryEditKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_CATEGORY_EDIT_0;
            case 1 -> KEY_CATEGORY_EDIT_1;
            case 2 -> KEY_CATEGORY_EDIT_2;
            case 3 -> KEY_CATEGORY_EDIT_3;
            case 4 -> KEY_CATEGORY_EDIT_4;
            case 5 -> KEY_CATEGORY_EDIT_5;
            default -> null;
        };
    }

    public static String getCategoryClearKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_CATEGORY_CLEAR_0;
            case 1 -> KEY_CATEGORY_CLEAR_1;
            case 2 -> KEY_CATEGORY_CLEAR_2;
            case 3 -> KEY_CATEGORY_CLEAR_3;
            case 4 -> KEY_CATEGORY_CLEAR_4;
            case 5 -> KEY_CATEGORY_CLEAR_5;
            default -> null;
        };
    }

    public static String getMobEditKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_MOB_EDIT_0;
            case 1 -> KEY_MOB_EDIT_1;
            case 2 -> KEY_MOB_EDIT_2;
            case 3 -> KEY_MOB_EDIT_3;
            case 4 -> KEY_MOB_EDIT_4;
            case 5 -> KEY_MOB_EDIT_5;
            case 6 -> KEY_MOB_EDIT_6;
            case 7 -> KEY_MOB_EDIT_7;
            case 8 -> KEY_MOB_EDIT_8;
            case 9 -> KEY_MOB_EDIT_9;
            default -> null;
        };
    }

    public static String getMobClearKeyForRow(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> KEY_MOB_CLEAR_0;
            case 1 -> KEY_MOB_CLEAR_1;
            case 2 -> KEY_MOB_CLEAR_2;
            case 3 -> KEY_MOB_CLEAR_3;
            case 4 -> KEY_MOB_CLEAR_4;
            case 5 -> KEY_MOB_CLEAR_5;
            case 6 -> KEY_MOB_CLEAR_6;
            case 7 -> KEY_MOB_CLEAR_7;
            case 8 -> KEY_MOB_CLEAR_8;
            case 9 -> KEY_MOB_CLEAR_9;
            default -> null;
        };
    }
}
