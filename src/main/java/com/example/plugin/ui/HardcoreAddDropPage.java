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

import java.util.Locale;

public class HardcoreAddDropPage extends InteractiveCustomUIPage<HardcoreAddDropPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreAddDropPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String SELECTED_CATEGORY_ID = "#SelectedCategory.Text";
    
    private static final String HOSTILE_BUTTON_PATH = "#HostileButton";
    private static final String HOSTILE_VALUE_PATH = "#HostileValue.Value";
    private static final String ELITE_BUTTON_PATH = "#EliteButton";
    private static final String ELITE_VALUE_PATH = "#EliteValue.Value";
    private static final String MINIBOSS_BUTTON_PATH = "#MinibossButton";
    private static final String MINIBOSS_VALUE_PATH = "#MinibossValue.Value";
    private static final String WORLDBOSS_BUTTON_PATH = "#WorldbossButton";
    private static final String WORLDBOSS_VALUE_PATH = "#WorldbossValue.Value";
    
    private static final String MIN_QUANTITY_SLIDER_PATH = "#MinQuantitySlider";
    private static final String MIN_QUANTITY_VALUE_PATH = "#MinQuantitySlider.Value";
    private static final String MIN_QUANTITY_LABEL_PATH = "#MinQuantityValue.Text";
    private static final String MAX_QUANTITY_SLIDER_PATH = "#MaxQuantitySlider";
    private static final String MAX_QUANTITY_VALUE_PATH = "#MaxQuantitySlider.Value";
    private static final String MAX_QUANTITY_LABEL_PATH = "#MaxQuantityValue.Text";
    private static final String DROP_CHANCE_SLIDER_PATH = "#DropChanceSlider";
    private static final String DROP_CHANCE_VALUE_PATH = "#DropChanceSlider.Value";
    private static final String DROP_CHANCE_LABEL_PATH = "#DropChanceValue.Text";
    
    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String SAVE_BUTTON_PATH = "#BottomButtonsContainer #SaveButton";
    private static final String SAVE_VALUE_PATH = "#BottomButtonsContainer #SaveValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private MobCategory selectedCategory = MobCategory.HOSTILE;

    public HardcoreAddDropPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreAddDropPageEventData.CODEC);
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
        bindEvents(events);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreAddDropPageEventData data
    ) {
        if (data == null) {
            return;
        }

        // Handle cancel
        if (Boolean.TRUE.equals(data.getCancel())) {
            openBloodMoonDropsPage(ref, store);
            return;
        }

        // Handle category selection
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

        // Handle save
        if (Boolean.TRUE.equals(data.getSave())) {
            // Note: TextField values cannot be read via EventData in Hytale's CustomUI system
            // Using a fixed default value - users must edit JSON manually for custom items
            String itemId = "Ingredient_Bar_Iron";

            Float minQty = data.getMinQuantity();
            Float maxQty = data.getMaxQuantity();
            Float dropChance = data.getDropChance();

            int minQuantity = minQty != null ? Math.round(minQty) : 1;
            int maxQuantity = maxQty != null ? Math.round(maxQty) : 1;
            float chance = dropChance != null ? dropChance : 50.0f;

            // Ensure max >= min
            if (maxQuantity < minQuantity) {
                maxQuantity = minQuantity;
            }

            // Add the drop entry
            BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
            dropConfig.addDropEntry(selectedCategory, itemId.trim(), minQuantity, maxQuantity, chance);

            // Go back to drops page
            openBloodMoonDropsPage(ref, store);
            return;
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Add Blood Moon Drop");
        updateSelectedCategoryLabel(commands);
    }

    private void updateSelectedCategoryLabel(UICommandBuilder commands) {
        String categoryName = formatCategoryName(selectedCategory);
        commands.set(SELECTED_CATEGORY_ID, "Selected: " + categoryName);
        
        // Update button states
        commands.set(HOSTILE_VALUE_PATH, selectedCategory == MobCategory.HOSTILE);
        commands.set(ELITE_VALUE_PATH, selectedCategory == MobCategory.ELITE);
        commands.set(MINIBOSS_VALUE_PATH, selectedCategory == MobCategory.MINIBOSS);
        commands.set(WORLDBOSS_VALUE_PATH, selectedCategory == MobCategory.WORLDBOSS);
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) return "Unknown";
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }

    private void bindEvents(UIEventBuilder events) {
        // Category buttons
        EventData hostileData = EventData.of(HardcoreAddDropPageEventData.KEY_HOSTILE, HOSTILE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, HOSTILE_BUTTON_PATH, hostileData, false);

        EventData eliteData = EventData.of(HardcoreAddDropPageEventData.KEY_ELITE, ELITE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, ELITE_BUTTON_PATH, eliteData, false);

        EventData minibossData = EventData.of(HardcoreAddDropPageEventData.KEY_MINIBOSS, MINIBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, MINIBOSS_BUTTON_PATH, minibossData, false);

        EventData worldbossData = EventData.of(HardcoreAddDropPageEventData.KEY_WORLDBOSS, WORLDBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, WORLDBOSS_BUTTON_PATH, worldbossData, false);

        // Sliders
        EventData minQtyData = EventData.of(HardcoreAddDropPageEventData.KEY_MIN_QUANTITY, MIN_QUANTITY_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MIN_QUANTITY_SLIDER_PATH, minQtyData, false);

        EventData maxQtyData = EventData.of(HardcoreAddDropPageEventData.KEY_MAX_QUANTITY, MAX_QUANTITY_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MAX_QUANTITY_SLIDER_PATH, maxQtyData, false);

        EventData dropChanceData = EventData.of(HardcoreAddDropPageEventData.KEY_DROP_CHANCE, DROP_CHANCE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, DROP_CHANCE_SLIDER_PATH, dropChanceData, false);

        // Navigation buttons
        EventData cancelData = EventData.of(HardcoreAddDropPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH, cancelData, false);

        EventData saveData = EventData.of(HardcoreAddDropPageEventData.KEY_SAVE, SAVE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, SAVE_BUTTON_PATH, saveData, false);
    }

    private void openBloodMoonDropsPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreBloodMoonDropsPage(plugin, playerRef));
    }

    private void reopenPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreAddDropPage(plugin, playerRef));
    }
}
