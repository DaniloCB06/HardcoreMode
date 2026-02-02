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
import java.util.Set;

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

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private List<String> worldNames;

    public HardcoreWorldSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreWorldSettingsPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.worldNames = new ArrayList<>();
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
        buildWorldList(commands, events);
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
            reopenPage(ref, store);
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
                reopenPage(ref, store);
            }
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "World Settings");
        commands.set(SECTION_DESCRIPTION_ID, "Enable or disable HardcoreMode effects for each world");
    }

    private void buildWorldList(UICommandBuilder commands, UIEventBuilder events) {
        worldNames.clear();
        
        // Obter lista de mundos do Universe
        Universe universe = Universe.get();
        if (universe == null) {
            addNoWorldsMessage(commands);
            return;
        }
        
        // Obter todos os mundos (Map<String, World>)
        Map<String, World> worlds = universe.getWorlds();
        if (worlds == null || worlds.isEmpty()) {
            addNoWorldsMessage(commands);
            return;
        }
        
        HardcoreModeConfig config = plugin.getConfigData();
        int index = 0;
        
        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            String worldName = entry.getKey();
            World world = entry.getValue();
            if (world == null || worldName == null || worldName.isEmpty()) continue;
            
            worldNames.add(worldName);
            addWorldRow(commands, events, index, worldName, config.isWorldEnabled(worldName));
            index++;
        }
        
        if (index == 0) {
            addNoWorldsMessage(commands);
        }
    }

    private void addWorldRow(
            UICommandBuilder commands,
            UIEventBuilder events,
            int index,
            String worldName,
            boolean enabled
    ) {
        String entry = WORLD_LIST_ID + "[" + index + "]";
        commands.append(WORLD_LIST_ID, WORLD_ROW_PATH);
        
        // Definir nome do mundo
        commands.set(entry + " #WorldName.Text", worldName);
        
        // Definir status
        String status = enabled ? "Status: Enabled (HardcoreMode active)" : "Status: Disabled (HardcoreMode inactive)";
        commands.set(entry + " #WorldStatus.Text", status);
        
        // Definir valor do toggle
        commands.set(entry + " #WorldToggle.Value", enabled);
        
        // Bind do evento de toggle
        String togglePath = entry + " #WorldToggle";
        String valuePath = entry + " #WorldToggle.Value";
        
        EventData toggleData = EventData.of(
                HardcoreWorldSettingsPageEventData.KEY_WORLD_TOGGLE_PREFIX + index,
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
            Store<EntityStore> store
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreWorldSettingsPage(plugin, playerRef));
    }
}
