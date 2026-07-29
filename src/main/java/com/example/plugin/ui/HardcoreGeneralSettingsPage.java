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
    private static final String MONEY_MOBS_DROPS_BUTTON_PATH = "#MoneyMobsDropsButtonRow #MoneyMobsDropsButton";
    private static final String MONEY_MOBS_DROPS_VALUE_PATH = "#MoneyMobsDropsButtonRow #MoneyMobsDropsValue.Value";
    private static final String MONEY_MOBS_DROPS_BUTTON_TEXT_ID = "#MoneyMobsDropsButton.Text";
    private static final String MONEY_MOBS_DROPS_DESCRIPTION_ID = "#MoneyMobsDropsDescription.Text";
    private static final String MOB_CATEGORIES_BUTTON_PATH = "#MobCategoriesButtonRow #MobCategoriesButton";
    private static final String MOB_CATEGORIES_VALUE_PATH = "#MobCategoriesButtonRow #MobCategoriesValue.Value";
    private static final String MOB_CATEGORIES_BUTTON_TEXT_ID = "#MobCategoriesButton.Text";
    private static final String MOB_CATEGORIES_DESCRIPTION_ID = "#MobCategoriesDescription.Text";
    private static final String WORLD_SETTINGS_BUTTON_PATH = "#WorldSettingsButtonRow #WorldSettingsButton";
    private static final String WORLD_SETTINGS_VALUE_PATH = "#WorldSettingsButtonRow #WorldSettingsValue.Value";
    private static final String WORLD_SETTINGS_BUTTON_TEXT_ID = "#WorldSettingsButton.Text";
    private static final String WORLD_SETTINGS_DESCRIPTION_ID = "#WorldSettingsDescription.Text";

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
        Boolean openMoneyMobsDrops = data.getOpenMoneyMobsDrops();
        Boolean openMobCategories = data.getOpenMobCategories();
        Boolean openWorldSettings = data.getOpenWorldSettings();

        if (Boolean.TRUE.equals(goBack)) {
            openMainMenu(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(openBloodMoonDrops)) {
            openBloodMoonDrops(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(openMoneyMobsDrops)) {
            openMoneyMobsDrops(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(openMobCategories)) {
            openMobCategories(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(openWorldSettings)) {
            openWorldSettings(ref, store);
            return;
        }

        // Aqui você adicionará a lógica para lidar com as configurações gerais
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "General Settings");
        commands.set(BLOOD_MOON_DROPS_BUTTON_TEXT_ID, "Blood Moon Drops");
        commands.set(BLOOD_MOON_DROPS_DESCRIPTION_ID, "Configure item drops during Blood Moon events");
        commands.set(MONEY_MOBS_DROPS_BUTTON_TEXT_ID, "Money Mobs Drops");
        commands.set(MONEY_MOBS_DROPS_DESCRIPTION_ID, "Configure money rewards for creatures and categories");
        commands.set(MOB_CATEGORIES_BUTTON_TEXT_ID, "Mob Categories");
        commands.set(MOB_CATEGORIES_DESCRIPTION_ID, "View and manage creature category assignments");
        commands.set(WORLD_SETTINGS_BUTTON_TEXT_ID, "World Settings");
        commands.set(WORLD_SETTINGS_DESCRIPTION_ID, "Enable or disable HardcoreMode for specific worlds");
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

        EventData mobCategoriesData = EventData.of(
                HardcoreGeneralSettingsPageEventData.KEY_OPEN_MOB_CATEGORIES,
                MOB_CATEGORIES_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, MOB_CATEGORIES_BUTTON_PATH, mobCategoriesData, false);

        EventData moneyMobsDropsData = EventData.of(
                HardcoreGeneralSettingsPageEventData.KEY_OPEN_MONEY_MOBS_DROPS,
                MONEY_MOBS_DROPS_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, MONEY_MOBS_DROPS_BUTTON_PATH, moneyMobsDropsData, false);

        EventData worldSettingsData = EventData.of(
                HardcoreGeneralSettingsPageEventData.KEY_OPEN_WORLD_SETTINGS,
                WORLD_SETTINGS_VALUE_PATH
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, WORLD_SETTINGS_BUTTON_PATH, worldSettingsData, false);
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

    private void openMobCategories(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreMobCategoriesPage(plugin, playerRef));
    }

    private void openMoneyMobsDrops(
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

        player.getPageManager().openCustomPage(ref, store, new HardcoreMoneyMobDropsPage(plugin, playerRef));
    }

    private void openWorldSettings(
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
