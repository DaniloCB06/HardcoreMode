package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.ui.HardcoreSettingsPage.SettingsSection;
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

public class HardcoreMainMenuPage extends InteractiveCustomUIPage<HardcoreMainMenuPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreMainMenuPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SUBTITLE_ID = "#Subtitle.Text";
    private static final String ENEMY_BUTTON_TEXT_ID = "#EnemyButton.Text";
    private static final String BLOOD_BUTTON_TEXT_ID = "#BloodMoonButton.Text";
    private static final String PLAYER_BUTTON_TEXT_ID = "#PlayerButton.Text";
    private static final String GENERAL_BUTTON_TEXT_ID = "#GeneralButton.Text";
    private static final String ENEMY_DESCRIPTION_ID = "#EnemyDescription.Text";
    private static final String BLOOD_DESCRIPTION_ID = "#BloodMoonDescription.Text";
    private static final String PLAYER_DESCRIPTION_ID = "#PlayerDescription.Text";
    private static final String GENERAL_DESCRIPTION_ID = "#GeneralDescription.Text";

    private static final String ENEMY_BUTTON_PATH = "#EnemyButtonRow #EnemyButton";
    private static final String ENEMY_VALUE_PATH = "#EnemyButtonRow #EnemyValue.Value";
    private static final String BLOOD_BUTTON_PATH = "#BloodMoonButtonRow #BloodMoonButton";
    private static final String BLOOD_VALUE_PATH = "#BloodMoonButtonRow #BloodMoonValue.Value";
    private static final String PLAYER_BUTTON_PATH = "#PlayerButtonRow #PlayerButton";
    private static final String PLAYER_VALUE_PATH = "#PlayerButtonRow #PlayerValue.Value";
    private static final String GENERAL_BUTTON_PATH = "#GeneralButtonRow #GeneralButton";
    private static final String GENERAL_VALUE_PATH = "#GeneralButtonRow #GeneralValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;

    public HardcoreMainMenuPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreMainMenuPageEventData.CODEC);
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
        fillTexts(commands);
        bindButtons(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreMainMenuPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getOpenEnemySettings())) {
            openSection(ref, store, SettingsSection.ENEMY);
            return;
        }

        if (Boolean.TRUE.equals(data.getOpenBloodMoonSettings())) {
            openSection(ref, store, SettingsSection.BLOOD_MOON);
            return;
        }

        if (Boolean.TRUE.equals(data.getOpenPlayerSettings())) {
            openSection(ref, store, SettingsSection.PLAYER);
            return;
        }

        if (Boolean.TRUE.equals(data.getOpenGeneralSettings())) {
            openGeneralSettings(ref, store);
        }
    }

    private void fillTexts(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SUBTITLE_ID, "Choose what you want to configure");
        commands.set(ENEMY_BUTTON_TEXT_ID, "Enemy Settings");
        commands.set(BLOOD_BUTTON_TEXT_ID, "Blood Moon");
        commands.set(PLAYER_BUTTON_TEXT_ID, "Player Settings");
        commands.set(GENERAL_BUTTON_TEXT_ID, "General Settings");
        commands.set(ENEMY_DESCRIPTION_ID, "Adjust health/damage and enablement by mob disposition");
        commands.set(BLOOD_DESCRIPTION_ID, "Interval, start time, duration and Blood Moon multipliers");
        commands.set(PLAYER_DESCRIPTION_ID, "Death penalties and item loss percentages");
        commands.set(GENERAL_DESCRIPTION_ID, "Configure general mod settings");
    }

    private void bindButtons(UIEventBuilder events) {
        EventData enemyData = EventData.of(
                HardcoreMainMenuPageEventData.KEY_OPEN_ENEMY,
                ENEMY_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, ENEMY_BUTTON_PATH, enemyData, false);

        EventData bloodData = EventData.of(
                HardcoreMainMenuPageEventData.KEY_OPEN_BLOOD_MOON,
                BLOOD_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, BLOOD_BUTTON_PATH, bloodData, false);

        EventData playerData = EventData.of(
                HardcoreMainMenuPageEventData.KEY_OPEN_PLAYER,
                PLAYER_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, PLAYER_BUTTON_PATH, playerData, false);

        EventData generalData = EventData.of(
                HardcoreMainMenuPageEventData.KEY_OPEN_GENERAL,
                GENERAL_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, GENERAL_BUTTON_PATH, generalData, false);
    }

    private void openSection(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            SettingsSection section
    ) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreSettingsPage(plugin, playerRef, section));
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
}
