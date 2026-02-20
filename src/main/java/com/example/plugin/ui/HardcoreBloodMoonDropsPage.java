package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.BloodMoonDropConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
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

public class HardcoreBloodMoonDropsPage extends InteractiveCustomUIPage<HardcoreBloodMoonDropsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreBloodMoonDropsPage.ui";
    private static final String DROP_ROW_PATH = "Pages/HardcoreBloodMoonDropRow.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
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
    private static final int ITEMS_PER_PAGE = 13;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final List<DropRowData> dropsList = new ArrayList<>();
    private int currentPage = 0;

    public HardcoreBloodMoonDropsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, 0);
    }

    public HardcoreBloodMoonDropsPage(HardcoreModePlugin plugin, PlayerRef playerRef, int currentPage) {
        super(playerRef, CustomPageLifetime.CanDismiss, buildDynamicCodec(plugin));
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.currentPage = currentPage;
        loadDropsList();
    }

    private void loadDropsList() {
        dropsList.clear();
        BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
        
        for (MobCategory category : MobCategory.values()) {
            if (category == MobCategory.NONE || category == MobCategory.PASSIVE || category == MobCategory.CRITTER) {
                continue;
            }
            
            List<BloodMoonDropConfig.DropEntry> entries = dropConfig.getDropEntries(category);
            for (BloodMoonDropConfig.DropEntry entry : entries) {
                dropsList.add(new DropRowData(category, entry));
            }
        }
    }

    private static BuilderCodec<HardcoreBloodMoonDropsPageEventData> buildDynamicCodec(HardcoreModePlugin plugin) {
        BuilderCodec.Builder<HardcoreBloodMoonDropsPageEventData> builder = BuilderCodec
                .builder(HardcoreBloodMoonDropsPageEventData.class, HardcoreBloodMoonDropsPageEventData::new)
                .append(new KeyedCodec<>(HardcoreBloodMoonDropsPageEventData.KEY_GO_BACK, Codec.BOOLEAN),
                        (data, value) -> data.setGoBack(value),
                        data -> data.getGoBack())
                .add()
                .append(new KeyedCodec<>(HardcoreBloodMoonDropsPageEventData.KEY_RELOAD_CONFIG, Codec.BOOLEAN),
                        (data, value) -> data.setReloadConfig(value),
                        data -> data.getReloadConfig())
                .add()
                .append(new KeyedCodec<>(HardcoreBloodMoonDropsPageEventData.KEY_ADD_DROP, Codec.BOOLEAN),
                        (data, value) -> data.setAddDrop(value),
                        data -> data.getAddDrop())
                .add()
                .append(new KeyedCodec<>(HardcoreBloodMoonDropsPageEventData.KEY_PREV_PAGE, Codec.BOOLEAN),
                        (data, value) -> data.setPrevPage(value),
                        data -> data.getPrevPage())
                .add()
                .append(new KeyedCodec<>(HardcoreBloodMoonDropsPageEventData.KEY_NEXT_PAGE, Codec.BOOLEAN),
                        (data, value) -> data.setNextPage(value),
                        data -> data.getNextPage())
                .add();

        // Add dynamic keys for each drop
        BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
        for (MobCategory category : MobCategory.values()) {
            if (category == MobCategory.NONE || category == MobCategory.PASSIVE || category == MobCategory.CRITTER) {
                continue;
            }
            
            List<BloodMoonDropConfig.DropEntry> entries = dropConfig.getDropEntries(category);
            for (BloodMoonDropConfig.DropEntry entry : entries) {
                String enabledKey = HardcoreBloodMoonDropsPageEventData.KEY_DROP_ENABLED_PREFIX + 
                                   category.name() + "_" + entry.itemId;
                String removeKey = HardcoreBloodMoonDropsPageEventData.KEY_REMOVE_DROP_PREFIX + 
                                  category.name() + "_" + entry.itemId;
                
                builder.append(new KeyedCodec<>(enabledKey, Codec.BOOLEAN),
                        (data, value) -> data.addDropChange(enabledKey, value),
                        data -> data.getDropChange(enabledKey))
                .add()
                .append(new KeyedCodec<>(removeKey, Codec.BOOLEAN),
                        (data, value) -> data.addDropChange(removeKey, value),
                        data -> data.getDropChange(removeKey))
                .add();
            }
        }

        return builder.build();
    }

    private static BuilderCodec<HardcoreBloodMoonDropsPageEventData> createCodec() {
        return HardcoreBloodMoonDropsPageEventData.CODEC;
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
        updatePaginationInfo(commands);
        buildDropsList(commands, events);
        bindNavigation(events);
        bindPaginationButtons(events);
    }
    
    private int getTotalPages() {
        return (int) Math.ceil((double) dropsList.size() / ITEMS_PER_PAGE);
    }

    private int getTotalPagesSafe() {
        return Math.max(1, getTotalPages());
    }

    private void clampCurrentPage() {
        int totalPages = getTotalPagesSafe();
        if (currentPage < 0) {
            currentPage = 0;
        } else if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
    }
    
    private void updatePaginationInfo(UICommandBuilder commands) {
        clampCurrentPage();
        int totalPages = getTotalPagesSafe();
        int displayPage = currentPage + 1;
        commands.set(PAGE_INFO_ID, "Page " + displayPage + " of " + totalPages);
        commands.set(TOTAL_ENTRIES_ID, "Total: " + dropsList.size() + " drops");
    }
    
    private void bindPaginationButtons(UIEventBuilder events) {
        EventData prevData = EventData.of(
                HardcoreBloodMoonDropsPageEventData.KEY_PREV_PAGE,
                PREV_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, PREV_BUTTON_PATH, prevData, false);

        EventData nextData = EventData.of(
                HardcoreBloodMoonDropsPageEventData.KEY_NEXT_PAGE,
                NEXT_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_BUTTON_PATH, nextData, false);
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

        Boolean goBack = data.getGoBack();
        Boolean reloadConfig = data.getReloadConfig();
        Boolean addDrop = data.getAddDrop();
        Boolean prevPage = data.getPrevPage();
        Boolean nextPage = data.getNextPage();

        if (Boolean.TRUE.equals(goBack)) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(reloadConfig)) {
            plugin.getBloodMoonDropConfig().reload();
            reopenPageWithCurrentPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(addDrop)) {
            openAddDropPage(ref, store);
            return;
        }
        
        if (Boolean.TRUE.equals(prevPage)) {
            if (currentPage > 0) {
                currentPage--;
                reopenPageWithCurrentPage(ref, store);
            }
            return;
        }
        
        if (Boolean.TRUE.equals(nextPage)) {
            int totalPages = getTotalPagesSafe();
            if (currentPage < totalPages - 1) {
                currentPage++;
                reopenPageWithCurrentPage(ref, store);
            }
            return;
        }

        // Process checkbox and button changes
        java.util.Map<String, Boolean> changes = data.getAllDropChanges();
        if (changes != null && !changes.isEmpty()) {
            BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
            
            for (java.util.Map.Entry<String, Boolean> change : changes.entrySet()) {
                String key = change.getKey();
                Boolean value = change.getValue();
                
                if (key.startsWith(HardcoreBloodMoonDropsPageEventData.KEY_REMOVE_DROP_PREFIX) && Boolean.TRUE.equals(value)) {
                    String identifier = key.substring(HardcoreBloodMoonDropsPageEventData.KEY_REMOVE_DROP_PREFIX.length());
                    String[] parts = identifier.split("_", 2);
                    if (parts.length == 2) {
                        try {
                            MobCategory category = MobCategory.valueOf(parts[0]);
                            String itemId = parts[1];
                            openRemoveDropConfirmation(ref, store, category, itemId);
                            return;
                        } catch (IllegalArgumentException e) {
                            return;
                        }
                    }
                }

                // Handle enabled checkbox changes
                if (key.startsWith(HardcoreBloodMoonDropsPageEventData.KEY_DROP_ENABLED_PREFIX)) {
                    String identifier = key.substring(HardcoreBloodMoonDropsPageEventData.KEY_DROP_ENABLED_PREFIX.length());
                    String[] parts = identifier.split("_", 2);
                    
                    if (parts.length == 2) {
                        try {
                            MobCategory category = MobCategory.valueOf(parts[0]);
                            String itemId = parts[1];
                            dropConfig.setDropEnabled(category, itemId, value != null && value);
                        } catch (IllegalArgumentException e) {
                            // Invalid category, ignore
                        }
                    }
                }
            }
            
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Blood Moon Drops");
    }

    private void buildDropsList(UICommandBuilder commands, UIEventBuilder events) {
        clampCurrentPage();
        // Calculate pagination
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, dropsList.size());
        
        // Build UI rows for displayed drops only
        int rowIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            DropRowData dropData = dropsList.get(i);
            String rowId = ITEMS_LIST_ID + "[" + rowIndex + "]";
            commands.append(ITEMS_LIST_ID, DROP_ROW_PATH);

            String enabledPath = rowId + " #Enabled.Value";
            String categoryPath = rowId + " #Category.Text";
            String itemIdPath = rowId + " #ItemId.Text";
            String minQuantityPath = rowId + " #MinQuantity.Text";
            String maxQuantityPath = rowId + " #MaxQuantity.Text";
            String dropChancePath = rowId + " #DropChance.Text";

            commands.set(enabledPath, dropData.entry.enabled);
            commands.set(categoryPath, formatCategoryName(dropData.category));
            commands.set(itemIdPath, dropData.entry.itemId);
            commands.set(minQuantityPath, String.valueOf(dropData.entry.minQuantity));
            commands.set(maxQuantityPath, String.valueOf(dropData.entry.maxQuantity));
            commands.set(dropChancePath, String.format(Locale.US, "%.1f%%", dropData.entry.dropChance));

            // Bind checkbox event with unique key
            String eventKey = HardcoreBloodMoonDropsPageEventData.KEY_DROP_ENABLED_PREFIX + 
                             dropData.category.name() + "_" + dropData.entry.itemId;
            EventData enabledData = EventData.of(eventKey, enabledPath);
            events.addEventBinding(
                com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType.ValueChanged,
                rowId + " #Enabled",
                enabledData,
                false
            );

            // Bind remove button
            String removeKey = HardcoreBloodMoonDropsPageEventData.KEY_REMOVE_DROP_PREFIX + 
                              dropData.category.name() + "_" + dropData.entry.itemId;
            String removeValuePath = rowId + " #RemoveValue.Value";
            EventData removeData = EventData.of(removeKey, removeValuePath);
            events.addEventBinding(
                com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType.Activating,
                rowId + " #RemoveButton",
                removeData,
                false
            );

            rowIndex++;
        }
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) return "Unknown";
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }

    private void bindNavigation(UIEventBuilder events) {
        EventData backData = EventData.of(
                HardcoreBloodMoonDropsPageEventData.KEY_GO_BACK,
                BACK_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH, backData, false);

        EventData addData = EventData.of(
                HardcoreBloodMoonDropsPageEventData.KEY_ADD_DROP,
                ADD_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, ADD_BUTTON_PATH, addData, false);

        EventData reloadData = EventData.of(
                HardcoreBloodMoonDropsPageEventData.KEY_RELOAD_CONFIG,
                RELOAD_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, RELOAD_BUTTON_PATH, reloadData, false);
    }

    private void openAddDropPage(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreAddDropPage(plugin, playerRef, currentPage));
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
        List<BloodMoonDropConfig.DropEntry> entries = plugin.getBloodMoonDropConfig().getDropEntries(category);
        for (BloodMoonDropConfig.DropEntry candidate : entries) {
            if (candidate.itemId.equals(itemId)) {
                entry = candidate;
                break;
            }
        }

        player.getPageManager().openCustomPage(
                ref,
                store,
                new HardcoreRemoveDropPage(plugin, playerRef, category, itemId, entry, currentPage)
        );
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

    private void reopenPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store
    ) {
        reopenPageWithCurrentPage(ref, store);
    }
    
    private void reopenPageWithCurrentPage(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreBloodMoonDropsPage(plugin, playerRef, currentPage));
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
