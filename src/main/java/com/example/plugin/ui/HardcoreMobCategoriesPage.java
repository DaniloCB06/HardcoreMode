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
import java.util.List;
import java.util.Locale;

public class HardcoreMobCategoriesPage extends InteractiveCustomUIPage<HardcoreMobCategoriesPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreMobCategoriesPage.ui";
    private static final String MOB_ROW_PATH = "Pages/HardcoreMobCategoryRow.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
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

    // Filter button paths
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

    private static final int ITEMS_PER_PAGE = 14;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final List<MobCategoryResolver.CategoryEntry> allEntries = new ArrayList<>();
    private MobCategory currentFilter = null;
    private int currentPage = 0;

    public HardcoreMobCategoriesPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, null, 0);
    }

    public HardcoreMobCategoriesPage(HardcoreModePlugin plugin, PlayerRef playerRef, MobCategory filter, int page) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreMobCategoriesPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.currentFilter = filter;
        this.currentPage = page;
        loadEntries();
    }

    private void loadEntries() {
        allEntries.clear();
        MobCategoryResolver resolver = plugin.getMobCategoryResolver();
        allEntries.addAll(resolver.getEntries());
    }

    private List<MobCategoryResolver.CategoryEntry> getFilteredEntries() {
        if (currentFilter == null) {
            return allEntries;
        }
        
        List<MobCategoryResolver.CategoryEntry> filtered = new ArrayList<>();
        for (MobCategoryResolver.CategoryEntry entry : allEntries) {
            if (entry.category == currentFilter) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private int getTotalPages(int totalItems) {
        return Math.max(1, (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE));
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commands,
            UIEventBuilder events,
            Store<EntityStore> store
    ) {
        commands.append(PAGE_PATH);
        fillHeader(commands);
        buildMobsList(commands, events);
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

        Boolean goBack = data.getGoBack();
        Boolean reloadConfig = data.getReloadConfig();

        if (Boolean.TRUE.equals(goBack)) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(reloadConfig)) {
            plugin.getMobCategoryResolver().reload();
            reopenPage(ref, store, currentFilter, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getAddEntry())) {
            openAddEntryPage(ref, store);
            return;
        }

        int removeIndex = data.getRemoveRowIndex();
        if (removeIndex >= 0) {
            openRemoveConfirmation(ref, store, removeIndex);
            return;
        }

        // Handle pagination
        if (Boolean.TRUE.equals(data.getPrevPage())) {
            if (currentPage > 0) {
                reopenPage(ref, store, currentFilter, currentPage - 1);
            }
            return;
        }

        if (Boolean.TRUE.equals(data.getNextPage())) {
            List<MobCategoryResolver.CategoryEntry> filtered = getFilteredEntries();
            int totalPages = getTotalPages(filtered.size());
            if (currentPage < totalPages - 1) {
                reopenPage(ref, store, currentFilter, currentPage + 1);
            }
            return;
        }

        // Handle filter buttons - reset to page 0 when changing filter
        if (Boolean.TRUE.equals(data.getFilterAll())) {
            reopenPage(ref, store, null, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterHostile())) {
            reopenPage(ref, store, MobCategory.HOSTILE, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterElite())) {
            reopenPage(ref, store, MobCategory.ELITE, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterMiniboss())) {
            reopenPage(ref, store, MobCategory.MINIBOSS, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterWorldboss())) {
            reopenPage(ref, store, MobCategory.WORLDBOSS, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterPassive())) {
            reopenPage(ref, store, MobCategory.PASSIVE, 0);
            return;
        }

        if (Boolean.TRUE.equals(data.getFilterCritter())) {
            reopenPage(ref, store, MobCategory.CRITTER, 0);
            return;
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Mob Categories");
    }

    private void buildMobsList(UICommandBuilder commands, UIEventBuilder events) {
        List<MobCategoryResolver.CategoryEntry> filteredEntries = getFilteredEntries();
        int totalItems = filteredEntries.size();
        int totalPages = getTotalPages(totalItems);
        
        // Ensure current page is valid
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        // Calculate start and end indices for current page
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
        
        // Update results count
        String filterText = currentFilter != null ? " (" + formatCategoryName(currentFilter) + ")" : "";
        commands.set(RESULTS_COUNT_ID, "Total: " + totalItems + " entries" + filterText);
        
        // Update page info
        commands.set(PAGE_INFO_ID, "Page " + (currentPage + 1) + " of " + totalPages);

        // Build UI rows for current page
        int rowIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            MobCategoryResolver.CategoryEntry entry = filteredEntries.get(i);
            
            String rowId = ITEMS_LIST_ID + "[" + rowIndex + "]";
            commands.append(ITEMS_LIST_ID, MOB_ROW_PATH);

            String mobIdPath = rowId + " #MobId.Text";
            String categoryPath = rowId + " #Category.Text";
            String removeButtonPath = rowId + " #RemoveButton";
            String removeValuePath = rowId + " #RemoveValue.Value";

            commands.set(mobIdPath, entry.pattern);
            commands.set(categoryPath, formatCategoryName(entry.category));

            String removeKey = HardcoreMobCategoriesPageEventData.getRemoveKeyForRow(rowIndex);
            if (removeKey != null) {
                EventData removeData = EventData.of(removeKey, removeValuePath);
                events.addEventBinding(CustomUIEventBindingType.Activating, removeButtonPath, removeData, false);
            }

            rowIndex++;
        }
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) return "Unknown";
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }

    private void bindFilterButtons(UIEventBuilder events) {
        EventData filterAllData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_ALL,
                FILTER_ALL_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ALL_BUTTON_PATH, filterAllData, false);

        EventData filterHostileData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_HOSTILE,
                FILTER_HOSTILE_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_HOSTILE_BUTTON_PATH, filterHostileData, false);

        EventData filterEliteData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_ELITE,
                FILTER_ELITE_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ELITE_BUTTON_PATH, filterEliteData, false);

        EventData filterMinibossData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_MINIBOSS,
                FILTER_MINIBOSS_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_MINIBOSS_BUTTON_PATH, filterMinibossData, false);

        EventData filterWorldbossData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_WORLDBOSS,
                FILTER_WORLDBOSS_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_WORLDBOSS_BUTTON_PATH, filterWorldbossData, false);

        EventData filterPassiveData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_PASSIVE,
                FILTER_PASSIVE_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_PASSIVE_BUTTON_PATH, filterPassiveData, false);

        EventData filterCritterData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_FILTER_CRITTER,
                FILTER_CRITTER_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_CRITTER_BUTTON_PATH, filterCritterData, false);
    }

    private void bindPaginationButtons(UIEventBuilder events) {
        EventData prevPageData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_PREV_PAGE,
                PREV_PAGE_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, PREV_PAGE_BUTTON_PATH, prevPageData, false);

        EventData nextPageData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_NEXT_PAGE,
                NEXT_PAGE_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_PAGE_BUTTON_PATH, nextPageData, false);
    }

    private void bindNavigation(UIEventBuilder events) {
        EventData backData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_GO_BACK,
                BACK_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH, backData, false);

        EventData reloadData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_RELOAD_CONFIG,
                RELOAD_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, RELOAD_BUTTON_PATH, reloadData, false);
    }

    private void bindCrudButtons(UIEventBuilder events) {
        EventData addData = EventData.of(
                HardcoreMobCategoriesPageEventData.KEY_ADD_ENTRY,
                ADD_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, ADD_BUTTON_PATH, addData, false);
    }

    private void openGeneralSettings(
            Ref<EntityStore> ref,
            Store<EntityStore> store
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreGeneralSettingsPage(plugin, playerRef));
    }

    private void openAddEntryPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store
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
                new HardcoreAddMobCategoryPage(plugin, playerRef, currentFilter, currentPage)
        );
    }

    private void openRemoveConfirmation(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            int rowIndex
    ) {
        List<MobCategoryResolver.CategoryEntry> filteredEntries = getFilteredEntries();
        int globalIndex = (currentPage * ITEMS_PER_PAGE) + rowIndex;

        if (globalIndex < 0 || globalIndex >= filteredEntries.size()) {
            return;
        }

        MobCategoryResolver.CategoryEntry entry = filteredEntries.get(globalIndex);

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
                new HardcoreRemoveMobCategoryPage(plugin, playerRef, entry, currentFilter, currentPage)
        );
    }

    private void reopenPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            MobCategory filter,
            int page
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreMobCategoriesPage(plugin, playerRef, filter, page));
    }
}
