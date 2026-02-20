package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
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

import java.util.Locale;

public class HardcoreAddMobCategoryPage extends InteractiveCustomUIPage<HardcoreAddMobCategoryPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreAddMobCategoryPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String SELECTED_CATEGORY_ID = "#SelectedCategory.Text";

    private static final String PATTERN_INPUT_PATH = "#PatternInput";
    private static final String PATTERN_VALUE_PATH = "#PatternInput.Value";

    private static final String HOSTILE_BUTTON_PATH = "#HostileButton";
    private static final String HOSTILE_VALUE_PATH = "#HostileValue.Value";
    private static final String ELITE_BUTTON_PATH = "#EliteButton";
    private static final String ELITE_VALUE_PATH = "#EliteValue.Value";
    private static final String MINIBOSS_BUTTON_PATH = "#MinibossButton";
    private static final String MINIBOSS_VALUE_PATH = "#MinibossValue.Value";
    private static final String WORLDBOSS_BUTTON_PATH = "#WorldbossButton";
    private static final String WORLDBOSS_VALUE_PATH = "#WorldbossValue.Value";
    private static final String PASSIVE_BUTTON_PATH = "#PassiveButton";
    private static final String PASSIVE_VALUE_PATH = "#PassiveValue.Value";
    private static final String CRITTER_BUTTON_PATH = "#CritterButton";
    private static final String CRITTER_VALUE_PATH = "#CritterValue.Value";

    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String SAVE_BUTTON_PATH = "#BottomButtonsContainer #SaveButton";
    private static final String SAVE_VALUE_PATH = "#BottomButtonsContainer #SaveValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final MobCategory returnFilter;
    private final int returnPage;
    private MobCategory selectedCategory;
    private String pattern;

    public HardcoreAddMobCategoryPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            int returnPage
    ) {
        this(plugin, playerRef, returnFilter, returnPage,
                returnFilter != null ? returnFilter : MobCategory.HOSTILE,
                "");
    }

    public HardcoreAddMobCategoryPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            int returnPage,
            MobCategory selectedCategory,
            String pattern
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreAddMobCategoryPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.returnFilter = returnFilter;
        this.returnPage = Math.max(0, returnPage);
        this.selectedCategory = selectedCategory != null ? selectedCategory : MobCategory.HOSTILE;
        this.pattern = pattern != null ? pattern : "";
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
        fillForm(commands);
        bindEvents(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreAddMobCategoryPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (data.getPattern() != null) {
            pattern = data.getPattern();
        }

        if (Boolean.TRUE.equals(data.getCancel())) {
            openMobCategoriesPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getHostile())) {
            selectedCategory = MobCategory.HOSTILE;
            reopenPage(ref, store);
            return;
        }
        if (Boolean.TRUE.equals(data.getElite())) {
            selectedCategory = MobCategory.ELITE;
            reopenPage(ref, store);
            return;
        }
        if (Boolean.TRUE.equals(data.getMiniboss())) {
            selectedCategory = MobCategory.MINIBOSS;
            reopenPage(ref, store);
            return;
        }
        if (Boolean.TRUE.equals(data.getWorldboss())) {
            selectedCategory = MobCategory.WORLDBOSS;
            reopenPage(ref, store);
            return;
        }
        if (Boolean.TRUE.equals(data.getPassive())) {
            selectedCategory = MobCategory.PASSIVE;
            reopenPage(ref, store);
            return;
        }
        if (Boolean.TRUE.equals(data.getCritter())) {
            selectedCategory = MobCategory.CRITTER;
            reopenPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getSave())) {
            String trimmed = pattern != null ? pattern.trim() : "";
            if (!trimmed.isEmpty()) {
                plugin.getMobCategoryResolver().addEntry(selectedCategory, trimmed);
            }
            openMobCategoriesPage(ref, store);
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Add Mob Category Entry");
        commands.set(SELECTED_CATEGORY_ID, "Selected: " + formatCategoryName(selectedCategory));
    }

    private void fillForm(UICommandBuilder commands) {
        commands.set(PATTERN_VALUE_PATH, pattern != null ? pattern : "");
    }

    private void bindEvents(UIEventBuilder events) {
        EventData patternData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_PATTERN, PATTERN_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, PATTERN_INPUT_PATH, patternData, false);

        EventData hostileData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_HOSTILE, HOSTILE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, HOSTILE_BUTTON_PATH, hostileData, false);

        EventData eliteData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_ELITE, ELITE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, ELITE_BUTTON_PATH, eliteData, false);

        EventData minibossData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_MINIBOSS, MINIBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, MINIBOSS_BUTTON_PATH, minibossData, false);

        EventData worldbossData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_WORLDBOSS, WORLDBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, WORLDBOSS_BUTTON_PATH, worldbossData, false);

        EventData passiveData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_PASSIVE, PASSIVE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, PASSIVE_BUTTON_PATH, passiveData, false);

        EventData critterData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_CRITTER, CRITTER_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CRITTER_BUTTON_PATH, critterData, false);

        EventData cancelData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH, cancelData, false);

        EventData saveData = EventData.of(HardcoreAddMobCategoryPageEventData.KEY_SAVE, SAVE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, SAVE_BUTTON_PATH, saveData, false);
    }

    private void openMobCategoriesPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store,
                new HardcoreMobCategoriesPage(plugin, playerRef, returnFilter, returnPage));
    }

    private void reopenPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store,
                new HardcoreAddMobCategoryPage(plugin, playerRef, returnFilter, returnPage, selectedCategory, pattern));
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }
}
