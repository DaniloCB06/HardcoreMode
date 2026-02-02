package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.config.HardcoreModeConfig;
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
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HardcoreWorldSettingsPage extends InteractiveCustomUIPage<HardcoreWorldSettingsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreWorldSettingsPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String SECTION_DESCRIPTION_ID = "#SectionDescription.Text";
    private static final String WORLD_LIST_ID = "#WorldList";
    private static final String WORLD_ROW_PATH = "Pages/HardcoreWorldToggleRow.ui";
    
    private static final String BACK_BUTTON_PATH = "#BottomButtonsContainer #BackButton";
    private static final String BACK_VALUE_PATH = "#BottomButtonsContainer #BackValue.Value";
    private static final String REFRESH_BUTTON_PATH = "#BottomButtonsContainer #RefreshButton";
    private static final String REFRESH_VALUE_PATH = "#BottomButtonsContainer #RefreshValue.Value";
    
    // Pagination constants
    private static final int ITEMS_PER_PAGE = 6;
    private static final String PREV_PAGE_BUTTON_PATH = "#PaginationContainer #PrevPageContainer #PrevPageButton";
    private static final String PREV_PAGE_VALUE_PATH = "#PaginationContainer #PrevPageContainer #PrevPageValue.Value";
    private static final String NEXT_PAGE_BUTTON_PATH = "#PaginationContainer #NextPageContainer #NextPageButton";
    private static final String NEXT_PAGE_VALUE_PATH = "#PaginationContainer #NextPageContainer #NextPageValue.Value";
    private static final String PAGE_INFO_ID = "#PaginationContainer #PageInfoContainer #PageInfoLabel.Text";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private List<String> worldNames;
    private int currentPage = 0;

    public HardcoreWorldSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, 0);
    }

    public HardcoreWorldSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef, int page) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreWorldSettingsPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.worldNames = new ArrayList<>();
        this.currentPage = page;
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
        loadAllWorldNames();
        buildWorldList(commands, events);
        bindPaginationButtons(commands, events);
        bindNavigation(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreWorldSettingsPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getGoBack())) {
            openGeneralSettings(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getRefresh())) {
            reopenPage(ref, store, 0);
            return;
        }

        // Handle pagination
        if (Boolean.TRUE.equals(data.getPrevPage())) {
            int newPage = Math.max(0, currentPage - 1);
            reopenPage(ref, store, newPage);
            return;
        }

        if (Boolean.TRUE.equals(data.getNextPage())) {
            int totalPages = getTotalPages(worldNames.size());
            int newPage = Math.min(totalPages - 1, currentPage + 1);
            reopenPage(ref, store, newPage);
            return;
        }

        // Handle world toggle changes
        Map<Integer, Boolean> toggles = data.getWorldToggles();
        if (toggles != null && !toggles.isEmpty()) {
            HardcoreModeConfig config = plugin.getConfigData();
            boolean changed = false;
            
            for (Map.Entry<Integer, Boolean> entry : toggles.entrySet()) {
                int index = entry.getKey();
                Boolean enabled = entry.getValue();
                
                if (enabled != null && index >= 0 && index < worldNames.size()) {
                    String worldName = worldNames.get(index);
                    boolean currentEnabled = config.isWorldEnabled(worldName);
                    
                    if (enabled != currentEnabled) {
                        config.setWorldEnabled(worldName, enabled);
                        changed = true;
                    }
                }
            }
            
            if (changed) {
                plugin.getConfig().save();
                // Reabrir a página para refletir as mudanças
                reopenPage(ref, store, currentPage);
            }
        }
    }

    private int getTotalPages(int totalItems) {
        return Math.max(1, (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE));
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "World Settings");
        commands.set(SECTION_DESCRIPTION_ID, "Enable or disable HardcoreMode effects for each world");
    }

    private void loadAllWorldNames() {
        worldNames.clear();
        
        // Obter lista de mundos do Universe
        Universe universe = Universe.get();
        if (universe == null) {
            return;
        }
        
        // Obter todos os mundos (Map<String, World>)
        Map<String, World> worlds = universe.getWorlds();
        if (worlds == null || worlds.isEmpty()) {
            return;
        }
        
        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            String worldName = entry.getKey();
            World world = entry.getValue();
            if (world != null && worldName != null && !worldName.isEmpty()) {
                worldNames.add(worldName);
            }
        }
    }

    private void buildWorldList(UICommandBuilder commands, UIEventBuilder events) {
        if (worldNames.isEmpty()) {
            addNoWorldsMessage(commands);
            return;
        }
        
        HardcoreModeConfig config = plugin.getConfigData();
        int totalItems = worldNames.size();
        int totalPages = getTotalPages(totalItems);
        
        // Garantir que a página atual é válida
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }
        
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
        
        int displayIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            String worldName = worldNames.get(i);
            addWorldRow(commands, events, displayIndex, i, worldName, config.isWorldEnabled(worldName));
            displayIndex++;
        }
        
        if (displayIndex == 0) {
            addNoWorldsMessage(commands);
        }
    }

    private void addWorldRow(
            UICommandBuilder commands,
            UIEventBuilder events,
            int displayIndex,
            int globalIndex,
            String worldName,
            boolean enabled
    ) {
        String entry = WORLD_LIST_ID + "[" + displayIndex + "]";
        commands.append(WORLD_LIST_ID, WORLD_ROW_PATH);
        
        // Definir nome do mundo
        commands.set(entry + " #WorldName.Text", worldName);
        
        // Definir status
        String status = enabled ? "Status: Enabled (HardcoreMode active)" : "Status: Disabled (HardcoreMode inactive)";
        commands.set(entry + " #WorldStatus.Text", status);
        
        // Definir valor do toggle
        commands.set(entry + " #WorldToggle.Value", enabled);
        
        // Bind do evento de toggle (usa o índice global para identificar corretamente o mundo)
        String togglePath = entry + " #WorldToggle";
        String valuePath = entry + " #WorldToggle.Value";
        
        EventData toggleData = EventData.of(
                HardcoreWorldSettingsPageEventData.KEY_WORLD_TOGGLE_PREFIX + globalIndex,
                valuePath
        );
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, togglePath, toggleData, false);
    }

    private void addNoWorldsMessage(UICommandBuilder commands) {
        // Adicionar mensagem quando não há mundos
        String entry = WORLD_LIST_ID + "[0]";
        commands.append(WORLD_LIST_ID, WORLD_ROW_PATH);
        commands.set(entry + " #WorldName.Text", "No worlds found");
        commands.set(entry + " #WorldStatus.Text", "Try clicking 'Refresh Worlds' after loading a world");
        commands.set(entry + " #WorldToggle.Visible", false);
    }

    private void bindPaginationButtons(UICommandBuilder commands, UIEventBuilder events) {
        int totalItems = worldNames.size();
        int totalPages = getTotalPages(totalItems);
        
        // Atualizar texto da página
        commands.set(PAGE_INFO_ID, "Page " + (currentPage + 1) + " of " + totalPages);
        
        // Desabilitar botão "Previous" se estiver na primeira página
        if (currentPage <= 0) {
            commands.set(PREV_PAGE_BUTTON_PATH + ".Visible", false);
        } else {
            commands.set(PREV_PAGE_BUTTON_PATH + ".Visible", true);
            EventData prevData = EventData.of(
                    HardcoreWorldSettingsPageEventData.KEY_PREV_PAGE,
                    PREV_PAGE_VALUE_PATH
            );
            events.addEventBinding(CustomUIEventBindingType.Activating, PREV_PAGE_BUTTON_PATH, prevData, false);
        }
        
        // Desabilitar botão "Next" se estiver na última página
        if (currentPage >= totalPages - 1) {
            commands.set(NEXT_PAGE_BUTTON_PATH + ".Visible", false);
        } else {
            commands.set(NEXT_PAGE_BUTTON_PATH + ".Visible", true);
            EventData nextData = EventData.of(
                    HardcoreWorldSettingsPageEventData.KEY_NEXT_PAGE,
                    NEXT_PAGE_VALUE_PATH
            );
            events.addEventBinding(CustomUIEventBindingType.Activating, NEXT_PAGE_BUTTON_PATH, nextData, false);
        }
    }

    private void bindNavigation(UIEventBuilder events) {
        EventData backData = EventData.of(
                HardcoreWorldSettingsPageEventData.KEY_GO_BACK,
                BACK_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH, backData, false);

        EventData refreshData = EventData.of(
                HardcoreWorldSettingsPageEventData.KEY_REFRESH,
                REFRESH_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, REFRESH_BUTTON_PATH, refreshData, false);
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
            Store<EntityStore> store,
            int page
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreWorldSettingsPage(plugin, playerRef, page));
    }
}
