package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.MobCategoryResolver;
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
import java.util.List;
import java.util.Locale;

public class HardcoreMobCategoriesPage extends InteractiveCustomUIPage<HardcoreMobCategoriesPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreMobCategoriesPage.ui";
    private static final String MOB_ROW_PATH = "Pages/HardcoreMobCategoryRow.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String WARNING_TEXT_ID = "#WarningText.Text";
    private static final String SEARCH_INPUT_PATH = "#FiltersContainer #SearchInput";
    private static final String SEARCH_VALUE_PATH = "#FiltersContainer #SearchInput.Value";
    private static final String ITEMS_LIST_ID = "#ItemsList";
    private static final String RESULTS_COUNT_ID = "#ResultsCountLabel.Text";
    private static final String PAGE_INFO_ID = "#PageInfoLabel.Text";
    private static final String BACK_BUTTON_PATH = "#BottomButtonsContainer #BackButton";
    private static final String BACK_VALUE_PATH = "#BottomButtonsContainer #BackValue.Value";
    private static final String ADD_BUTTON_PATH = "#BottomButtonsContainer #AddButton";
    private static final String ADD_VALUE_PATH = "#BottomButtonsContainer #AddValue.Value";
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
    private static final int ITEMS_PER_PAGE = 18;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final List<MobCategoryResolver.CategoryEntry> allEntries = new ArrayList<>();
    private MobCategory currentFilter;
    private String currentSearch;
    private int currentPage;

    public HardcoreMobCategoriesPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, null, "", 0);
    }

    public HardcoreMobCategoriesPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory filter,
            int page
    ) {
        this(plugin, playerRef, filter, "", page);
    }

    public HardcoreMobCategoriesPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory filter,
            String search,
            int page
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreMobCategoriesPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.currentFilter = filter;
        this.currentSearch = search != null ? search : "";
        this.currentPage = Math.max(0, page);
        loadEntries();
    }

    private void loadEntries() {
        allEntries.clear();
        allEntries.addAll(plugin.getMobCategoryResolver().getEntries());
        allEntries.sort(Comparator.comparing(entry -> entry.pattern.toLowerCase(Locale.US)));
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
        buildMobsList(commands, events);
        bindSearch(events);
        bindFilterButtons(events);
        bindPaginationButtons(events);
        bindNavigation(events);
        bindCrudButtons(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreMobCategoriesPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getGoBack())) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getReloadConfig())) {
            plugin.getMobCategoryResolver().reload();
            loadEntries();
            currentPage = 0;
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

        if (Boolean.TRUE.equals(data.getAddEntry())) {
            openAddEntryPage(ref, store);
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

        List<MobCategoryResolver.CategoryEntry> visibleEntries = getPageEntries();

        int editIndex = data.getEditRowIndex();
        if (editIndex >= 0 && editIndex < visibleEntries.size()) {
            openEditEntryPage(ref, store, visibleEntries.get(editIndex));
            return;
        }

        int removeIndex = data.getRemoveRowIndex();
        if (removeIndex >= 0 && removeIndex < visibleEntries.size()) {
            openRemoveConfirmation(ref, store, visibleEntries.get(removeIndex));
        }
    }

    private void fillHeader(UICommandBuilder commands, boolean includeSearchValue) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Mob Categories");
        commands.set(
                WARNING_TEXT_ID,
                "Search and edit individual patterns here. For bulk edits, change 'HardcoreModeCategories.json' in 'com.example_HardcoreMode'."
        );
        if (includeSearchValue) {
            commands.set(SEARCH_VALUE_PATH, currentSearch);
        }
    }

    private void buildMobsList(UICommandBuilder commands, UIEventBuilder events) {
        commands.clear(ITEMS_LIST_ID);
        List<MobCategoryResolver.CategoryEntry> filteredEntries = getFilteredEntries();
        int totalPages = getTotalPages(filteredEntries);
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        commands.set(PAGE_INFO_ID, "Page " + (currentPage + 1) + " of " + totalPages);
        commands.set(RESULTS_COUNT_ID, "Total: " + filteredEntries.size() + " entries");

        List<MobCategoryResolver.CategoryEntry> pageEntries = getPageEntries(filteredEntries);
        for (int i = 0; i < pageEntries.size(); i++) {
            MobCategoryResolver.CategoryEntry entry = pageEntries.get(i);
            String rowId = ITEMS_LIST_ID + "[" + i + "]";
            commands.append(ITEMS_LIST_ID, MOB_ROW_PATH);
            commands.set(rowId + " #MobId.Text", entry.pattern);
            commands.set(rowId + " #Category.Text", formatCategoryName(entry.category));

            String editKey = HardcoreMobCategoriesPageEventData.getEditKeyForRow(i);
            if (editKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #EditButton",
                        EventData.of(editKey, rowId + " #EditValue.Value"),
                        false
                );
            }

            String removeKey = HardcoreMobCategoriesPageEventData.getRemoveKeyForRow(i);
            if (removeKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #RemoveButton",
                        EventData.of(removeKey, rowId + " #RemoveValue.Value"),
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

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name().toLowerCase(Locale.US);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private void bindSearch(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                SEARCH_INPUT_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_SEARCH_TEXT, SEARCH_VALUE_PATH),
                false
        );
    }

    private void bindFilterButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ALL_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_ALL, FILTER_ALL_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_PASSIVE_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_PASSIVE, FILTER_PASSIVE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_CRITTER_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_CRITTER, FILTER_CRITTER_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_HOSTILE_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_HOSTILE, FILTER_HOSTILE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ELITE_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_ELITE, FILTER_ELITE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_MINIBOSS_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_MINIBOSS, FILTER_MINIBOSS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_WORLDBOSS_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_FILTER_WORLDBOSS, FILTER_WORLDBOSS_VALUE_PATH), false);
    }

    private void bindPaginationButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, PREV_PAGE_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_PREV_PAGE, PREV_PAGE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_PAGE_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_NEXT_PAGE, NEXT_PAGE_VALUE_PATH), false);
    }

    private void bindNavigation(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_GO_BACK, BACK_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, RELOAD_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_RELOAD_CONFIG, RELOAD_VALUE_PATH), false);
    }

    private void bindCrudButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, ADD_BUTTON_PATH,
                EventData.of(HardcoreMobCategoriesPageEventData.KEY_ADD_ENTRY, ADD_VALUE_PATH), false);
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

    private void openAddEntryPage(Ref<EntityStore> ref, Store<EntityStore> store) {
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
                new HardcoreAddMobCategoryPage(plugin, playerRef, currentFilter, currentSearch, currentPage)
        );
    }

    private void openEditEntryPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            MobCategoryResolver.CategoryEntry entry
    ) {
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
                HardcoreAddMobCategoryPage.forEdit(
                        plugin,
                        playerRef,
                        currentFilter,
                        currentSearch,
                        currentPage,
                        entry.category,
                        entry.pattern
                )
        );
    }

    private void openRemoveConfirmation(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            MobCategoryResolver.CategoryEntry entry
    ) {
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
                new HardcoreRemoveMobCategoryPage(plugin, playerRef, entry, currentFilter, currentSearch, currentPage)
        );
    }

    private void refreshPage(boolean includeSearchValue) {
        UICommandBuilder updateCommands = new UICommandBuilder();
        UIEventBuilder updateEvents = new UIEventBuilder();
        fillHeader(updateCommands, includeSearchValue);
        buildMobsList(updateCommands, updateEvents);
        bindSearch(updateEvents);
        bindFilterButtons(updateEvents);
        bindPaginationButtons(updateEvents);
        bindNavigation(updateEvents);
        bindCrudButtons(updateEvents);
        sendUpdate(updateCommands, updateEvents, false);
    }
}
