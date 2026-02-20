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

    private static final String ITEM_ID_INPUT_PATH = "#ItemIdInput";
    private static final String ITEM_ID_VALUE_PATH = "#ItemIdInput.Value";
    
    private static final String HOSTILE_BUTTON_PATH = "#HostileButton";
    private static final String HOSTILE_VALUE_PATH = "#HostileValue.Value";
    private static final String ELITE_BUTTON_PATH = "#EliteButton";
    private static final String ELITE_VALUE_PATH = "#EliteValue.Value";
    private static final String MINIBOSS_BUTTON_PATH = "#MinibossButton";
    private static final String MINIBOSS_VALUE_PATH = "#MinibossValue.Value";
    private static final String WORLDBOSS_BUTTON_PATH = "#WorldbossButton";
    private static final String WORLDBOSS_VALUE_PATH = "#WorldbossValue.Value";
    
    private static final String MIN_QUANTITY_INPUT_PATH = "#MinQuantityInput";
    private static final String MIN_QUANTITY_VALUE_PATH = "#MinQuantityInput.Value";
    private static final String MAX_QUANTITY_INPUT_PATH = "#MaxQuantityInput";
    private static final String MAX_QUANTITY_VALUE_PATH = "#MaxQuantityInput.Value";
    private static final String DROP_CHANCE_INPUT_PATH = "#DropChanceInput";
    private static final String DROP_CHANCE_VALUE_PATH = "#DropChanceInput.Value";

    private static final String MIN_QUANTITY_MINUS_PATH = "#MinQuantityMinus";
    private static final String MIN_QUANTITY_PLUS_PATH = "#MinQuantityPlus";
    private static final String MAX_QUANTITY_MINUS_PATH = "#MaxQuantityMinus";
    private static final String MAX_QUANTITY_PLUS_PATH = "#MaxQuantityPlus";
    private static final String DROP_CHANCE_MINUS_PATH = "#DropChanceMinus";
    private static final String DROP_CHANCE_PLUS_PATH = "#DropChancePlus";

    private static final String ADJUST_MIN_QUANTITY_MINUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustMinQuantityMinus.Value";
    private static final String ADJUST_MIN_QUANTITY_PLUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustMinQuantityPlus.Value";
    private static final String ADJUST_MAX_QUANTITY_MINUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustMaxQuantityMinus.Value";
    private static final String ADJUST_MAX_QUANTITY_PLUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustMaxQuantityPlus.Value";
    private static final String ADJUST_CHANCE_MINUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustChanceMinus.Value";
    private static final String ADJUST_CHANCE_PLUS_VALUE_PATH =
            "#FormContainer #AdjustValues #AdjustChancePlus.Value";
    
    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String SAVE_BUTTON_PATH = "#BottomButtonsContainer #SaveButton";
    private static final String SAVE_VALUE_PATH = "#BottomButtonsContainer #SaveValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private MobCategory selectedCategory = MobCategory.HOSTILE;
    private String itemId = DEFAULT_ITEM_ID;
    private int minQuantity = 1;
    private int maxQuantity = 1;
    private float dropChance = 50.0f;
    private final int returnPage;

    private static final String DEFAULT_ITEM_ID = "Ingredient_Bar_Iron";
    private static final int MIN_QTY_MIN = 1;
    private static final int MIN_QTY_MAX = 999;
    private static final int MAX_QTY_MIN = 1;
    private static final int MAX_QTY_MAX = 999;
    private static final float CHANCE_MIN = 0.0f;
    private static final float CHANCE_MAX = 100.0f;
    private static final int QTY_STEP = 1;
    private static final float CHANCE_STEP = 1.0f;

    public HardcoreAddDropPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, 0);
    }

    public HardcoreAddDropPage(HardcoreModePlugin plugin, PlayerRef playerRef, int returnPage) {
        this(plugin, playerRef, returnPage, MobCategory.HOSTILE, DEFAULT_ITEM_ID, 1, 1, 50.0f);
    }

    public HardcoreAddDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            int returnPage,
            MobCategory selectedCategory,
            String itemId,
            int minQuantity,
            int maxQuantity,
            float dropChance
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreAddDropPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.returnPage = Math.max(0, returnPage);
        if (selectedCategory != null) {
            this.selectedCategory = selectedCategory;
        }
        if (itemId != null && !itemId.trim().isEmpty()) {
            this.itemId = itemId;
        }
        this.minQuantity = clamp(minQuantity, MIN_QTY_MIN, MIN_QTY_MAX);
        this.maxQuantity = clamp(maxQuantity, MAX_QTY_MIN, MAX_QTY_MAX);
        if (this.maxQuantity < this.minQuantity) {
            this.maxQuantity = this.minQuantity;
        }
        this.dropChance = clamp(dropChance, CHANCE_MIN, CHANCE_MAX);
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
            HardcoreAddDropPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (data.getItemId() != null) {
            itemId = data.getItemId();
        }

        boolean clamped = false;

        if (data.getMinQuantity() != null) {
            clamped |= applyMinQuantityInput(data.getMinQuantity());
        }
        if (data.getMaxQuantity() != null) {
            clamped |= applyMaxQuantityInput(data.getMaxQuantity());
        }
        if (data.getDropChance() != null) {
            clamped |= applyDropChanceInput(data.getDropChance());
        }

        if (data.getAdjustAction() != null) {
            handleAdjustAction(data.getAdjustAction());
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
            String resolvedItemId = itemId != null && !itemId.trim().isEmpty()
                    ? itemId.trim()
                    : DEFAULT_ITEM_ID;

            int minQty = minQuantity;
            int maxQty = maxQuantity;
            float chance = dropChance;

            // Ensure max >= min
            if (maxQty < minQty) {
                maxQty = minQty;
            }

            // Add the drop entry
            BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
            dropConfig.addDropEntry(selectedCategory, resolvedItemId, minQty, maxQty, chance);

            // Go back to drops page
            openBloodMoonDropsPage(ref, store);
            return;
        }

        if (clamped) {
            sendFormUpdate();
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, "Add Blood Moon Drop");
        updateSelectedCategoryLabel(commands);
    }

    private void fillForm(UICommandBuilder commands) {
        String resolvedItemId = itemId != null ? itemId : DEFAULT_ITEM_ID;
        commands.set(ITEM_ID_VALUE_PATH, resolvedItemId);

        commands.set(MIN_QUANTITY_VALUE_PATH, String.valueOf(minQuantity));
        commands.set(MAX_QUANTITY_VALUE_PATH, String.valueOf(maxQuantity));
        commands.set(DROP_CHANCE_VALUE_PATH, formatChance(dropChance));
    }

    private void updateSelectedCategoryLabel(UICommandBuilder commands) {
        String categoryName = formatCategoryName(selectedCategory);
        commands.set(SELECTED_CATEGORY_ID, "Selected: " + categoryName);
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) return "Unknown";
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }

    private void bindEvents(UIEventBuilder events) {
        EventData itemIdData = EventData.of(HardcoreAddDropPageEventData.KEY_ITEM_ID, ITEM_ID_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, ITEM_ID_INPUT_PATH, itemIdData, false);

        // Category buttons
        EventData hostileData = EventData.of(HardcoreAddDropPageEventData.KEY_HOSTILE, HOSTILE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, HOSTILE_BUTTON_PATH, hostileData, false);

        EventData eliteData = EventData.of(HardcoreAddDropPageEventData.KEY_ELITE, ELITE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, ELITE_BUTTON_PATH, eliteData, false);

        EventData minibossData = EventData.of(HardcoreAddDropPageEventData.KEY_MINIBOSS, MINIBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, MINIBOSS_BUTTON_PATH, minibossData, false);

        EventData worldbossData = EventData.of(HardcoreAddDropPageEventData.KEY_WORLDBOSS, WORLDBOSS_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, WORLDBOSS_BUTTON_PATH, worldbossData, false);

        // Quantity and chance inputs
        EventData minQtyData = EventData.of(HardcoreAddDropPageEventData.KEY_MIN_QUANTITY, MIN_QUANTITY_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MIN_QUANTITY_INPUT_PATH, minQtyData, false);

        EventData maxQtyData = EventData.of(HardcoreAddDropPageEventData.KEY_MAX_QUANTITY, MAX_QUANTITY_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MAX_QUANTITY_INPUT_PATH, maxQtyData, false);

        EventData dropChanceData = EventData.of(HardcoreAddDropPageEventData.KEY_DROP_CHANCE, DROP_CHANCE_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, DROP_CHANCE_INPUT_PATH, dropChanceData, false);

        // Adjust buttons
        events.addEventBinding(CustomUIEventBindingType.Activating, MIN_QUANTITY_MINUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_MIN_QUANTITY_MINUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, MIN_QUANTITY_PLUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_MIN_QUANTITY_PLUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, MAX_QUANTITY_MINUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_MAX_QUANTITY_MINUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, MAX_QUANTITY_PLUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_MAX_QUANTITY_PLUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, DROP_CHANCE_MINUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_CHANCE_MINUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, DROP_CHANCE_PLUS_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ADJUST, ADJUST_CHANCE_PLUS_VALUE_PATH), false);

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

        player.getPageManager().openCustomPage(ref, store, new HardcoreBloodMoonDropsPage(plugin, playerRef, returnPage));
    }

    private void reopenPage(Ref<EntityStore> ref, Store<EntityStore> store) {
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
                new HardcoreAddDropPage(
                        plugin,
                        playerRef,
                        returnPage,
                        selectedCategory,
                        itemId,
                        minQuantity,
                        maxQuantity,
                        dropChance
                )
        );
    }

    private boolean applyMinQuantityInput(String value) {
        Integer parsed = parseInt(value);
        if (parsed == null) {
            return false;
        }
        int clamped = clamp(parsed, MIN_QTY_MIN, MIN_QTY_MAX);
        boolean changed = clamped != minQuantity;
        minQuantity = clamped;
        if (maxQuantity < minQuantity) {
            maxQuantity = minQuantity;
            changed = true;
        }
        return clamped != parsed || changed;
    }

    private boolean applyMaxQuantityInput(String value) {
        Integer parsed = parseInt(value);
        if (parsed == null) {
            return false;
        }
        int clamped = clamp(parsed, MAX_QTY_MIN, MAX_QTY_MAX);
        boolean changed = clamped != maxQuantity;
        maxQuantity = clamped;
        if (maxQuantity < minQuantity) {
            maxQuantity = minQuantity;
            changed = true;
        }
        return clamped != parsed || changed;
    }

    private boolean applyDropChanceInput(String value) {
        Float parsed = parseFloat(value);
        if (parsed == null) {
            return false;
        }
        float clamped = clamp(parsed, CHANCE_MIN, CHANCE_MAX);
        boolean changed = Math.abs(clamped - dropChance) > 0.0001f;
        dropChance = clamped;
        return Math.abs(clamped - parsed) > 0.0001f || changed;
    }

    private void handleAdjustAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return;
        }

        String normalized = action.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "min_qty:-":
                minQuantity = clamp(minQuantity - QTY_STEP, MIN_QTY_MIN, MIN_QTY_MAX);
                if (maxQuantity < minQuantity) {
                    maxQuantity = minQuantity;
                }
                break;
            case "min_qty:+":
                minQuantity = clamp(minQuantity + QTY_STEP, MIN_QTY_MIN, MIN_QTY_MAX);
                if (maxQuantity < minQuantity) {
                    maxQuantity = minQuantity;
                }
                break;
            case "max_qty:-":
                maxQuantity = clamp(maxQuantity - QTY_STEP, MAX_QTY_MIN, MAX_QTY_MAX);
                if (maxQuantity < minQuantity) {
                    maxQuantity = minQuantity;
                }
                break;
            case "max_qty:+":
                maxQuantity = clamp(maxQuantity + QTY_STEP, MAX_QTY_MIN, MAX_QTY_MAX);
                if (maxQuantity < minQuantity) {
                    minQuantity = maxQuantity;
                }
                break;
            case "chance:-":
                dropChance = clamp(dropChance - CHANCE_STEP, CHANCE_MIN, CHANCE_MAX);
                break;
            case "chance:+":
                dropChance = clamp(dropChance + CHANCE_STEP, CHANCE_MIN, CHANCE_MAX);
                break;
            default:
                return;
        }

        sendFormUpdate();
    }

    private void sendFormUpdate() {
        UICommandBuilder update = new UICommandBuilder();
        update.set(MIN_QUANTITY_VALUE_PATH, String.valueOf(minQuantity));
        update.set(MAX_QUANTITY_VALUE_PATH, String.valueOf(maxQuantity));
        update.set(DROP_CHANCE_VALUE_PATH, formatChance(dropChance));
        sendUpdate(update);
    }

    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Float parseFloat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim().replace(',', '.');
        try {
            return Float.parseFloat(normalized);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String formatChance(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) {
            return String.valueOf(Math.round(value));
        }
        return String.format(Locale.US, "%.1f", value);
    }

    private int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
