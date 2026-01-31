package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
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

public class HardcoreGeneralSettingsPage extends InteractiveCustomUIPage<HardcoreGeneralSettingsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreGeneralSettingsPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String SETTINGS_LIST_ID = "#SettingsList";
    private static final String BACK_BUTTON_PATH = "#BottomButtonsContainer #BackButton";
    private static final String BACK_VALUE_PATH = "#BottomButtonsContainer #BackValue.Value";
    private static final String BLOOD_MOON_DROPS_BUTTON_PATH = "#BloodMoonDropsButtonRow #BloodMoonDropsButton";
    private static final String BLOOD_MOON_DROPS_VALUE_PATH = "#BloodMoonDropsButtonRow #BloodMoonDropsValue.Value";
    private static final String BLOOD_MOON_DROPS_BUTTON_TEXT_ID = "#BloodMoonDropsButton.Text";
    private static final String BLOOD_MOON_DROPS_DESCRIPTION_ID = "#BloodMoonDropsDescription.Text";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;

    public HardcoreGeneralSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreGeneralSettingsPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
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
        buildSettingsList(commands, events);
        bindButtons(events);
        bindNavigation(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreGeneralSettingsPageEventData data
    ) {
        if (data == null) {
            return;
        }

        Boolean goBack = data.getGoBack();
        Boolean openBloodMoonDrops = data.getOpenBloodMoonDrops();

        if (Boolean.TRUE.equals(goBack)) {
            openMainMenu(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(openBloodMoonDrops)) {
            openBloodMoonDrops(ref, store);
            return;
        }

        // Aqui você adicionará a lógica para lidar com as configurações gerais
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "General Settings");
        commands.set(BLOOD_MOON_DROPS_BUTTON_TEXT_ID, "Blood Moon Drops");
        commands.set(BLOOD_MOON_DROPS_DESCRIPTION_ID, "Configure item drops during Blood Moon events");
    }

    private void buildSettingsList(UICommandBuilder commands, UIEventBuilder events) {
        // Por enquanto, a lista está vazia
        // Você adicionará os controles de configuração aqui mais tarde
    }

    private void bindButtons(UIEventBuilder events) {
        EventData bloodMoonDropsData = EventData.of(
                HardcoreGeneralSettingsPageEventData.KEY_OPEN_BLOOD_MOON_DROPS,
                BLOOD_MOON_DROPS_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BLOOD_MOON_DROPS_BUTTON_PATH, bloodMoonDropsData, false);
    }

    private void bindNavigation(UIEventBuilder events) {
        EventData backData = EventData.of(
                HardcoreGeneralSettingsPageEventData.KEY_GO_BACK,
                BACK_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH, backData, false);
    }

    private void openMainMenu(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreMainMenuPage(plugin, playerRef));
    }

    private void openBloodMoonDrops(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreBloodMoonDropsPage(plugin, playerRef));
    }
}
