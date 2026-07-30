package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.BloodMoonDropConfig;
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

public class HardcoreBloodMoonDropsPage extends InteractiveCustomUIPage<HardcoreBloodMoonDropsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreBloodMoonDropsPage.ui";
    private static final String DROP_ROW_PATH = "Pages/HardcoreBloodMoonDropRow.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String WARNING_TEXT_ID = "#WarningText.Text";
    private static final String SEARCH_INPUT_PATH = "#FiltersContainer #SearchInput";
    private static final String SEARCH_VALUE_PATH = "#FiltersContainer #SearchInput.Value";
    private static final String ITEMS_LIST_ID = "#ItemsList";
    private static final String PAGE_INFO_ID = "#PageInfo.Text";
    private static final String TOTAL_ENTRIES_ID = "#TotalEntriesText.Text";
    private static final String PREV_BUTTON_PATH = "#PaginationContainer #PrevButton";
    private static final String PREV_VALUE_PATH = "#PaginationContainer #PrevValue.Value";
    private static final String NEXT_BUTTON_PATH = "#PaginationContainer #NextButton";
    private static final String NEXT_VALUE_PATH = "#PaginationContainer #NextValue.Value";
    private static final String BACK_BUTTON_PATH = "#BottomButtonsContainer #BackButton";
    private static final String BACK_VALUE_PATH = "#BottomButtonsContainer #BackValue.Value";
    private static final String ADD_BUTTON_PATH = "#BottomButtonsContainer #AddButton";
    private static final String ADD_VALUE_PATH = "#BottomButtonsContainer #AddValue.Value";
    private static final String RELOAD_BUTTON_PATH = "#BottomButtonsContainer #ReloadButton";
    private static final String RELOAD_VALUE_PATH = "#BottomButtonsContainer #ReloadValue.Value";
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
    private static final int ITEMS_PER_PAGE = 15;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final List<DropRowData> dropsList = new ArrayList<>();
    private MobCategory currentFilter;
    private String currentSearch;
    private int currentPage;

    public HardcoreBloodMoonDropsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, null, "", 0);
    }

    public HardcoreBloodMoonDropsPage(HardcoreModePlugin plugin, PlayerRef playerRef, int currentPage) {
        this(plugin, playerRef, null, "", currentPage);
    }

    public HardcoreBloodMoonDropsPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory currentFilter,
            String currentSearch,
            int currentPage
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreBloodMoonDropsPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.currentFilter = currentFilter;
        this.currentSearch = currentSearch != null ? currentSearch : "";
        this.currentPage = Math.max(0, currentPage);
        loadDropsList();
    }

    private void loadDropsList() {
        dropsList.clear();
        BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
        for (MobCategory category : MobCategory.values()) {
            if (category == MobCategory.NONE || category == MobCategory.PASSIVE || category == MobCategory.CRITTER) {
                continue;
            }

            for (BloodMoonDropConfig.DropEntry entry : dropConfig.getDropEntries(category)) {
                dropsList.add(new DropRowData(category, entry));
            }
        }
        dropsList.sort(Comparator
                .comparing((DropRowData row) -> row.category.ordinal())
                .thenComparing(row -> row.entry.itemId.toLowerCase(Locale.US)));
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
        buildDropsList(commands, events);
        bindSearch(events);
        bindFilterButtons(events);
        bindPaginationButtons(events);
        bindNavigation(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreBloodMoonDropsPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getGoBack())) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getReloadConfig())) {
            plugin.getBloodMoonDropConfig().reload();
            loadDropsList();
            currentPage = 0;
            refreshPage(false);
            return;
        }

        if (Boolean.TRUE.equals(data.getAddDrop())) {
            openAddDropPage(ref, store);
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
            if (currentPage < getTotalPages(getFilteredDrops()) - 1) {
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

        List<DropRowData> visibleDrops = getPageDrops();

        int enabledIndex = data.getEnabledRowIndex();
        if (enabledIndex >= 0 && enabledIndex < visibleDrops.size()) {
            DropRowData row = visibleDrops.get(enabledIndex);
            Boolean enabledValue = data.getEnabledValueAtRow(enabledIndex);
            if (enabledValue != null && enabledValue != row.entry.enabled) {
                plugin.getBloodMoonDropConfig().setDropEnabled(row.category, row.entry.itemId, enabledValue);
                loadDropsList();
            }
            return;
        }

        int editIndex = data.getEditRowIndex();
        if (editIndex >= 0 && editIndex < visibleDrops.size()) {
            openEditDropPage(ref, store, visibleDrops.get(editIndex));
            return;
        }

        int removeIndex = data.getRemoveRowIndex();
        if (removeIndex >= 0 && removeIndex < visibleDrops.size()) {
            DropRowData row = visibleDrops.get(removeIndex);
            openRemoveDropConfirmation(ref, store, row.category, row.entry.itemId);
        }
    }

    private void fillHeader(UICommandBuilder commands, boolean includeSearchValue) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Blood Moon Drops");
        commands.set(
                WARNING_TEXT_ID,
                "Search, edit or remove individual drops here. For bulk edits, change 'HardcoreModeBloodMoonDrops.json' in 'com.example_HardcoreMode'."
        );
        if (includeSearchValue) {
            commands.set(SEARCH_VALUE_PATH, currentSearch);
        }
    }

    private void buildDropsList(UICommandBuilder commands, UIEventBuilder events) {
        commands.clear(ITEMS_LIST_ID);
        List<DropRowData> filteredDrops = getFilteredDrops();
        int totalPages = getTotalPages(filteredDrops);
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        commands.set(PAGE_INFO_ID, "Page " + (currentPage + 1) + " of " + totalPages);
        commands.set(TOTAL_ENTRIES_ID, "Total: " + filteredDrops.size() + " drops");

        List<DropRowData> pageDrops = getPageDrops(filteredDrops);
        for (int i = 0; i < pageDrops.size(); i++) {
            DropRowData dropData = pageDrops.get(i);
            String rowId = ITEMS_LIST_ID + "[" + i + "]";
            commands.append(ITEMS_LIST_ID, DROP_ROW_PATH);
            commands.set(rowId + " #Enabled.Value", dropData.entry.enabled);
            commands.set(rowId + " #Category.Text", formatCategoryName(dropData.category));
            commands.set(rowId + " #ItemId.Text", dropData.entry.itemId);
            commands.set(rowId + " #MinQuantity.Text", String.valueOf(dropData.entry.minQuantity));
            commands.set(rowId + " #MaxQuantity.Text", String.valueOf(dropData.entry.maxQuantity));
            commands.set(rowId + " #DropChance.Text", String.format(Locale.US, "%.1f%%", dropData.entry.dropChance));

            String enabledKey = HardcoreBloodMoonDropsPageEventData.getEnabledKeyForRow(i);
            if (enabledKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.ValueChanged,
                        rowId + " #Enabled",
                        EventData.of(enabledKey, rowId + " #Enabled.Value"),
                        false
                );
            }

            String editKey = HardcoreBloodMoonDropsPageEventData.getEditKeyForRow(i);
            if (editKey != null) {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        rowId + " #EditButton",
                        EventData.of(editKey, rowId + " #EditValue.Value"),
                        false
                );
            }

            String removeKey = HardcoreBloodMoonDropsPageEventData.getRemoveKeyForRow(i);
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

    private List<DropRowData> getFilteredDrops() {
        List<DropRowData> filtered = new ArrayList<>();
        String normalizedSearch = currentSearch == null ? "" : currentSearch.trim().toLowerCase(Locale.US);
        for (DropRowData entry : dropsList) {
            if (currentFilter != null && entry.category != currentFilter) {
                continue;
            }
            if (!normalizedSearch.isEmpty()) {
                String haystack = (entry.entry.itemId + " " + formatCategoryName(entry.category)).toLowerCase(Locale.US);
                if (!haystack.contains(normalizedSearch)) {
                    continue;
                }
            }
            filtered.add(entry);
        }
        return filtered;
    }

    private List<DropRowData> getPageDrops() {
        return getPageDrops(getFilteredDrops());
    }

    private List<DropRowData> getPageDrops(List<DropRowData> filteredDrops) {
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredDrops.size());
        if (start >= filteredDrops.size()) {
            return List.of();
        }
        return filteredDrops.subList(start, end);
    }

    private int getTotalPages(List<DropRowData> filteredDrops) {
        return Math.max(1, (int) Math.ceil((double) filteredDrops.size() / ITEMS_PER_PAGE));
    }

    private void bindSearch(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                SEARCH_INPUT_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_SEARCH_TEXT, SEARCH_VALUE_PATH),
                false
        );
    }

    private void bindFilterButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ALL_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_FILTER_ALL, FILTER_ALL_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_HOSTILE_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_FILTER_HOSTILE, FILTER_HOSTILE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_ELITE_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_FILTER_ELITE, FILTER_ELITE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_MINIBOSS_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_FILTER_MINIBOSS, FILTER_MINIBOSS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, FILTER_WORLDBOSS_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_FILTER_WORLDBOSS, FILTER_WORLDBOSS_VALUE_PATH), false);
    }

    private void bindPaginationButtons(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, PREV_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_PREV_PAGE, PREV_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_NEXT_PAGE, NEXT_VALUE_PATH), false);
    }

    private void bindNavigation(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_GO_BACK, BACK_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, ADD_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_ADD_DROP, ADD_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, RELOAD_BUTTON_PATH,
                EventData.of(HardcoreBloodMoonDropsPageEventData.KEY_RELOAD_CONFIG, RELOAD_VALUE_PATH), false);
    }

    private void openAddDropPage(Ref<EntityStore> ref, Store<EntityStore> store) {
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
                new HardcoreAddDropPage(plugin, playerRef, currentFilter, currentSearch, currentPage)
        );
    }

    private void openEditDropPage(Ref<EntityStore> ref, Store<EntityStore> store, DropRowData row) {
        if (store == null || ref == null || row == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                HardcoreAddDropPage.forEdit(
                        plugin,
                        playerRef,
                        currentFilter,
                        currentSearch,
                        currentPage,
                        row.category,
                        row.entry.itemId,
                        row.entry.minQuantity,
                        row.entry.maxQuantity,
                        row.entry.dropChance
                )
        );
    }

    private void openRemoveDropConfirmation(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            MobCategory category,
            String itemId
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        BloodMoonDropConfig.DropEntry entry = null;
        for (BloodMoonDropConfig.DropEntry candidate : plugin.getBloodMoonDropConfig().getDropEntries(category)) {
            if (candidate.itemId.equals(itemId)) {
                entry = candidate;
                break;
            }
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                new HardcoreRemoveDropPage(
                        plugin,
                        playerRef,
                        category,
                        itemId,
                        entry,
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

    private void refreshPage(boolean includeSearchValue) {
        UICommandBuilder updateCommands = new UICommandBuilder();
        UIEventBuilder updateEvents = new UIEventBuilder();
        fillHeader(updateCommands, includeSearchValue);
        buildDropsList(updateCommands, updateEvents);
        bindSearch(updateEvents);
        bindFilterButtons(updateEvents);
        bindPaginationButtons(updateEvents);
        bindNavigation(updateEvents);
        sendUpdate(updateCommands, updateEvents, false);
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name().toLowerCase(Locale.US);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static class DropRowData {
        final MobCategory category;
        final BloodMoonDropConfig.DropEntry entry;

        DropRowData(MobCategory category, BloodMoonDropConfig.DropEntry entry) {
            this.category = category;
            this.entry = entry;
        }
    }
}
