package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.MobCategoryResolver;
import com.example.plugin.config.MobMoneyDropConfig;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HardcoreMoneyMobDropsPage extends InteractiveCustomUIPage<HardcoreMoneyMobDropsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreMoneyMobDropsPage.ui";
    private static final String CATEGORY_ROW_PATH = "Pages/HardcoreMoneyCategoryRow.ui";
    private static final String MOB_ROW_PATH = "Pages/HardcoreMoneyMobDropRow.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String WARNING_TEXT_ID = "#WarningText.Text";
    private static final String MONEY_DROPS_LABEL_ID = "#MoneyDropsCard #MoneyDropsLabel.Text";
    private static final String MONEY_DROPS_DESCRIPTION_ID = "#MoneyDropsCard #MoneyDropsDescription.Text";
    private static final String MONEY_DROPS_TOGGLE_PATH = "#MoneyDropsCard #MoneyDropsToggle";
    private static final String MONEY_DROPS_TOGGLE_VALUE_PATH = "#MoneyDropsCard #MoneyDropsToggle.Value";
    private static final String SEARCH_INPUT_PATH = "#FiltersContainer #SearchInput";
    private static final String SEARCH_VALUE_PATH = "#FiltersContainer #SearchInput.Value";
    private static final String CATEGORY_LIST_ID = "#CategoryList";
    private static final String ITEMS_LIST_ID = "#ItemsList";
    private static final String RESULTS_COUNT_ID = "#ResultsCountLabel.Text";
    private static final String PAGE_INFO_ID = "#PageInfoLabel.Text";
    private static final String BACK_BUTTON_PATH = "#BottomButtonsContainer #BackButton";
    private static final String BACK_VALUE_PATH = "#BottomButtonsContainer #BackValue.Value";
    private static final String RELOAD_BUTTON_PATH = "#BottomButtonsContainer #ReloadButton";
    private static final String RELOAD_VALUE_PATH = "#BottomButtonsContainer #ReloadValue.Value";
    private static final String PREV_PAGE_BUTTON_PATH = "#PaginationContainer #PrevPageButton";
    private static final String PREV_PAGE_VALUE_PATH = "#PaginationContainer #PrevPageValue.Value";
    private static final String NEXT_PAGE_BUTTON_PATH = "#PaginationContainer #NextPageButton";
    private static final String NEXT_PAGE_VALUE_PATH = "#PaginationContainer #NextPageValue.Value";
    private static final String FILTER_ALL_BUTTON_PATH = "#FiltersContainer #FilterAllButton";
    private static final String FILTER_ALL_VALUE_PATH = "#FiltersContainer #FilterAllValue.Value";
    private static final String FILTER_HOSTILE_BUTTON_PATH = "#FiltersContainer #FilterHostileButton";
    private static final String FILTER_HOSTILE_VALUE_PATH = "#FiltersContainer #FilterHostileValue.Value";
    private static final String FILTER_ELITE_BUTTON_PATH = "#FiltersContainer #FilterEliteButton";
    private static final String FILTER_ELITE_VALUE_PATH = "#FiltersContainer #FilterEliteValue.Value";
    private static final String FILTER_MINIBOSS_BUTTON_PATH = "#FiltersContainer #FilterMinibossButton";
    private static final String FILTER_MINIBOSS_VALUE_PATH = "#FiltersContainer #FilterMinibossValue.Value";
    private static final String FILTER_WORLDBOSS_BUTTON_PATH = "#FiltersContainer #FilterWorldbossButton";
    private static final String FILTER_WORLDBOSS_VALUE_PATH = "#FiltersContainer #FilterWorldbossValue.Value";
    private static final String FILTER_PASSIVE_BUTTON_PATH = "#FiltersContainer #FilterPassiveButton";
    private static final String FILTER_PASSIVE_VALUE_PATH = "#FiltersContainer #FilterPassiveValue.Value";
    private static final String FILTER_CRITTER_BUTTON_PATH = "#FiltersContainer #FilterCritterButton";
    private static final String FILTER_CRITTER_VALUE_PATH = "#FiltersContainer #FilterCritterValue.Value";
    private static final int ITEMS_PER_PAGE = 8;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final List<MobCategoryResolver.CategoryEntry> allEntries = new ArrayList<>();
    private final List<MobCategory> categoryRows = List.of(
            MobCategory.PASSIVE,
            MobCategory.CRITTER,
            MobCategory.HOSTILE,
            MobCategory.ELITE,
            MobCategory.MINIBOSS,
            MobCategory.WORLDBOSS
    );
    private MobCategory currentFilter;
    private String currentSearch;
    private int currentPage;

    public HardcoreMoneyMobDropsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, null, "", 0);
    }

    public HardcoreMoneyMobDropsPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory currentFilter,
            String currentSearch,
            int currentPage
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreMoneyMobDropsPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.currentFilter = currentFilter;
        this.currentSearch = currentSearch == null ? "" : currentSearch;
        this.currentPage = Math.max(0, currentPage);
        loadEntries();
    }

    private void loadEntries() {
        allEntries.clear();
        Map<String, MobCategoryResolver.CategoryEntry> uniqueEntries = new LinkedHashMap<>();
        for (MobCategoryResolver.CategoryEntry entry : plugin.getMobCategoryResolver().getEntries()) {
            uniqueEntries.putIfAbsent(entry.pattern, entry);
        }
        allEntries.addAll(uniqueEntries.values());
        allEntries.sort(Comparator.comparing((MobCategoryResolver.CategoryEntry entry) -> entry.pattern.toLowerCase(Locale.US)));
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commands,
            UIEventBuilder events,
            Store<EntityStore> store
    ) {
        commands.append(PAGE_PATH);
        fillHeader(commands, true);
        buildCategoryRows(commands, events);
        buildMobRows(commands, events);
        bindMoneyToggle(events);
        bindFilterButtons(events);
        bindPaginationButtons(events);
        bindNavigation(events);
        bindSearch(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreMoneyMobDropsPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getGoBack())) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getReloadConfig())) {
            plugin.getMobMoneyDropConfig().reload();
            plugin.getMobCategoryResolver().reload();
            loadEntries();
            currentPage = 0;
            refreshPage(false);
            return;
        }

        Boolean incomingMoneyDropsEnabled = data.getMoneyDropsEnabled();
        if (incomingMoneyDropsEnabled != null
                && incomingMoneyDropsEnabled != plugin.getMobMoneyDropConfig().isEnabled()) {
            plugin.getMobMoneyDropConfig().setEnabled(incomingMoneyDropsEnabled);
            refreshPage(false);
            return;
        }

        String incomingSearch = data.getSearchText();
        if (incomingSearch != null && !incomingSearch.equals(currentSearch)) {
            currentSearch = incomingSearch;
            currentPage = 0;
            refreshPage(false);
            return;
        }

        if (Boolean.TRUE.equals(data.getPrevPage()) && currentPage > 0) {
            currentPage--;
            refreshPage(false);
            return;
        }

        if (Boolean.TRUE.equals(data.getNextPage())) {
            if (currentPage < getTotalPages(getFilteredEntries()) - 1) {
                currentPage++;
                refreshPage(false);
            }
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterAll())) {
            currentFilter = null;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterPassive())) {
            currentFilter = MobCategory.PASSIVE;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterCritter())) {
            currentFilter = MobCategory.CRITTER;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterHostile())) {
            currentFilter = MobCategory.HOSTILE;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterElite())) {
            currentFilter = MobCategory.ELITE;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterMiniboss())) {
            currentFilter = MobCategory.MINIBOSS;
            currentPage = 0;
            refreshPage(false);
            return;
        }
        if (Boolean.TRUE.equals(data.getFilterWorldboss())) {
            currentFilter = MobCategory.WORLDBOSS;
            currentPage = 0;
            refreshPage(false);
            return;
        }

        int categoryEditIndex = data.getCategoryEditRowIndex();
        if (categoryEditIndex >= 0 && categoryEditIndex < categoryRows.size()) {
            openCategoryEditPage(ref, store, categoryRows.get(categoryEditIndex));
            return;
        }

        int categoryClearIndex = data.getCategoryClearRowIndex();
        if (categoryClearIndex >= 0 && categoryClearIndex < categoryRows.size()) {
            clearCategory(categoryRows.get(categoryClearIndex));
            refreshPage(false);
            return;
        }

        List<MobCategoryResolver.CategoryEntry> visibleEntries = getPageEntries();

        int mobEditIndex = data.getMobEditRowIndex();
        if (mobEditIndex >= 0 && mobEditIndex < visibleEntries.size()) {
            openMobEditPage(ref, store, visibleEntries.get(mobEditIndex));
            return;
        }

        int mobClearIndex = data.getMobClearRowIndex();
        if (mobClearIndex >= 0 && mobClearIndex < visibleEntries.size()) {
            plugin.getMobMoneyDropConfig().clearMobAmount(visibleEntries.get(mobClearIndex).pattern);
            refreshPage(false);
        }
    }

    private void fillHeader(UICommandBuilder commands, boolean includeSearchValue) {
        boolean moneyDropsEnabled = plugin.getMobMoneyDropConfig().isEnabled();
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Money Mobs Drops");
        commands.set(WARNING_TEXT_ID, plugin.getMoneyRewardStatusMessage());
        commands.set(MONEY_DROPS_LABEL_ID, "Money Drops: " + (moneyDropsEnabled ? "ON" : "OFF"));
        commands.set(
                MONEY_DROPS_DESCRIPTION_ID,
                moneyDropsEnabled
                        ? "Creature money rewards are active. Disabling this will stop all money drops, including during Blood Moon."
                        : "Creature money rewards are currently disabled. No creature will deposit money until this is enabled again."
        );
        commands.set(MONEY_DROPS_TOGGLE_VALUE_PATH, moneyDropsEnabled);
        if (includeSearchValue) {
            commands.set(SEARCH_VALUE_PATH, currentSearch);
        }
    }

    private void buildCategoryRows(UICommandBuilder commands, UIEventBuilder events) {
        commands.clear(CATEGORY_LIST_ID);
        MobMoneyDropConfig config = plugin.getMobMoneyDropConfig();
        for (int i = 0; i < categoryRows.size(); i++) {
            MobCategory category = categoryRows.get(i);
            String rowId = CATEGORY_LIST_ID + "[" + i + "]";
            commands.append(CATEGORY_LIST_ID, CATEGORY_ROW_PATH);
            commands.set(rowId + " #Category.Text", formatCategoryName(category));
            commands.set(rowId + " #Amount.Text", formatAmount(config.getCategoryAmount(category)));

            String editKey = HardcoreMoneyMobDropsPageEventData.getCategoryEditKeyForRow(i);
            if (editKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #EditButton",
                        EventData.of(editKey, rowId + " #EditValue.Value"),
                        false
                );
            }

            String clearKey = HardcoreMoneyMobDropsPageEventData.getCategoryClearKeyForRow(i);
            if (clearKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #ClearButton",
                        EventData.of(clearKey, rowId + " #ClearValue.Value"),
                        false
                );
            }
        }
    }

    private void buildMobRows(UICommandBuilder commands, UIEventBuilder events) {
        commands.clear(ITEMS_LIST_ID);
        List<MobCategoryResolver.CategoryEntry> filteredEntries = getFilteredEntries();
        int totalPages = getTotalPages(filteredEntries);
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        commands.set(PAGE_INFO_ID, "Page " + (currentPage + 1) + " of " + totalPages);
        commands.set(RESULTS_COUNT_ID, "Total: " + filteredEntries.size() + " mob patterns");

        List<MobCategoryResolver.CategoryEntry> pageEntries = getPageEntries(filteredEntries);
        MobMoneyDropConfig config = plugin.getMobMoneyDropConfig();
        for (int i = 0; i < pageEntries.size(); i++) {
            MobCategoryResolver.CategoryEntry entry = pageEntries.get(i);
            String rowId = ITEMS_LIST_ID + "[" + i + "]";
            commands.append(ITEMS_LIST_ID, MOB_ROW_PATH);
            commands.set(rowId + " #MobPattern.Text", entry.pattern);
            commands.set(rowId + " #Category.Text", formatCategoryName(entry.category));
            commands.set(rowId + " #Amount.Text", formatAmount(config.getEffectiveAmount(entry.pattern, entry.category)));

            String editKey = HardcoreMoneyMobDropsPageEventData.getMobEditKeyForRow(i);
            if (editKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #EditButton",
                        EventData.of(editKey, rowId + " #EditValue.Value"),
                        false
                );
            }

            String clearKey = HardcoreMoneyMobDropsPageEventData.getMobClearKeyForRow(i);
            if (clearKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #ClearButton",
                        EventData.of(clearKey, rowId + " #ClearValue.Value"),
                        false
                );
            }
        }
    }

    private List<MobCategoryResolver.CategoryEntry> getFilteredEntries() {
        List<MobCategoryResolver.CategoryEntry> filtered = new ArrayList<>();
        String normalizedSearch = currentSearch == null ? "" : currentSearch.trim().toLowerCase(Locale.US);

        for (MobCategoryResolver.CategoryEntry entry : allEntries) {
            if (currentFilter != null && entry.category != currentFilter) {
                continue;
            }
            if (!normalizedSearch.isEmpty()) {
                String haystack = (entry.pattern + " " + formatCategoryName(entry.category)).toLowerCase(Locale.US);
                if (!haystack.contains(normalizedSearch)) {
                    continue;
                }
            }
            filtered.add(entry);
        }

        return filtered;
    }

    private List<MobCategoryResolver.CategoryEntry> getPageEntries() {
        return getPageEntries(getFilteredEntries());
    }

    private List<MobCategoryResolver.CategoryEntry> getPageEntries(List<MobCategoryResolver.CategoryEntry> filteredEntries) {
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredEntries.size());
        if (start >= filteredEntries.size()) {
            return List.of();
        }
        return filteredEntries.subList(start, end);
    }

    private int getTotalPages(List<MobCategoryResolver.CategoryEntry> filteredEntries) {
        return Math.max(1, (int) Math.ceil((double) filteredEntries.size() / ITEMS_PER_PAGE));
    }

    private void bindSearch(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                SEARCH_INPUT_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_SEARCH_TEXT, SEARCH_VALUE_PATH),
                false
        );
    }

    private void bindMoneyToggle(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                MONEY_DROPS_TOGGLE_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_MONEY_DROPS_ENABLED, MONEY_DROPS_TOGGLE_VALUE_PATH),
                false
        );
    }

    private void bindFilterButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ALL_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_ALL, FILTER_ALL_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_PASSIVE_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_PASSIVE, FILTER_PASSIVE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_CRITTER_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_CRITTER, FILTER_CRITTER_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_HOSTILE_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_HOSTILE, FILTER_HOSTILE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ELITE_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_ELITE, FILTER_ELITE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_MINIBOSS_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_MINIBOSS, FILTER_MINIBOSS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_WORLDBOSS_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_FILTER_WORLDBOSS, FILTER_WORLDBOSS_VALUE_PATH), false);
    }

    private void bindPaginationButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, PREV_PAGE_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_PREV_PAGE, PREV_PAGE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_PAGE_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_NEXT_PAGE, NEXT_PAGE_VALUE_PATH), false);
    }

    private void bindNavigation(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_GO_BACK, BACK_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, RELOAD_BUTTON_PATH,
                EventData.of(HardcoreMoneyMobDropsPageEventData.KEY_RELOAD_CONFIG, RELOAD_VALUE_PATH), false);
    }

    private void clearCategory(MobCategory category) {
        List<String> patterns = new ArrayList<>();
        for (MobCategoryResolver.CategoryEntry entry : allEntries) {
            if (entry.category == category) {
                patterns.add(entry.pattern);
            }
        }
        plugin.getMobMoneyDropConfig().clearCategoryAmount(category);
        plugin.getMobMoneyDropConfig().removeMobOverrides(patterns);
    }

    private void openCategoryEditPage(Ref<EntityStore> ref, Store<EntityStore> store, MobCategory category) {
        if (store == null || ref == null) {
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                HardcoreEditMoneyDropPage.forCategory(
                        plugin,
                        playerRef,
                        category,
                        currentFilter,
                        currentSearch,
                        currentPage
                )
        );
    }

    private void openMobEditPage(Ref<EntityStore> ref, Store<EntityStore> store, MobCategoryResolver.CategoryEntry entry) {
        if (store == null || ref == null || entry == null) {
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                HardcoreEditMoneyDropPage.forMob(
                        plugin,
                        playerRef,
                        entry.category,
                        entry.pattern,
                        currentFilter,
                        currentSearch,
                        currentPage
                )
        );
    }

    private void openGeneralSettings(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreGeneralSettingsPage(plugin, playerRef));
    }

    private void reopenPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            MobCategory filter,
            String search,
            int page
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                new HardcoreMoneyMobDropsPage(plugin, playerRef, filter, search, page)
        );
    }

    private void refreshPage(boolean includeSearchValue) {
        UICommandBuilder updateCommands = new UICommandBuilder();
        UIEventBuilder updateEvents = new UIEventBuilder();
        fillHeader(updateCommands, includeSearchValue);
        buildCategoryRows(updateCommands, updateEvents);
        buildMobRows(updateCommands, updateEvents);
        bindMoneyToggle(updateEvents);
        bindFilterButtons(updateEvents);
        bindPaginationButtons(updateEvents);
        bindNavigation(updateEvents);
        bindSearch(updateEvents);
        sendUpdate(updateCommands, updateEvents, false);
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name().toLowerCase(Locale.US);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String formatAmount(double amount) {
        return String.format(Locale.US, "%.2f", Math.max(0.0d, amount));
    }
}
