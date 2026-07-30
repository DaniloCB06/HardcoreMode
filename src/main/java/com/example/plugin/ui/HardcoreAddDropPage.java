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

    private static final String DEFAULT_ITEM_ID = "Ingredient_Bar_Iron";
    private static final int MIN_QTY_MIN = 1;
    private static final int MIN_QTY_MAX = 999;
    private static final int MAX_QTY_MIN = 1;
    private static final int MAX_QTY_MAX = 999;
    private static final float CHANCE_MIN = 0.0f;
    private static final float CHANCE_MAX = 100.0f;
    private static final int QTY_STEP = 1;
    private static final float CHANCE_STEP = 1.0f;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final MobCategory returnFilter;
    private final String returnSearch;
    private final int returnPage;
    private final boolean editMode;
    private final MobCategory originalCategory;
    private final String originalItemId;
    private MobCategory selectedCategory = MobCategory.HOSTILE;
    private String itemId = DEFAULT_ITEM_ID;
    private int minQuantity = 1;
    private int maxQuantity = 1;
    private float dropChance = 50.0f;

    public HardcoreAddDropPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        this(plugin, playerRef, null, "", 0, MobCategory.HOSTILE, DEFAULT_ITEM_ID, 1, 1, 50.0f, false, null, null);
    }

    public HardcoreAddDropPage(HardcoreModePlugin plugin, PlayerRef playerRef, int returnPage) {
        this(plugin, playerRef, null, "", returnPage, MobCategory.HOSTILE, DEFAULT_ITEM_ID, 1, 1, 50.0f, false, null, null);
    }

    public HardcoreAddDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage
    ) {
        this(plugin, playerRef, returnFilter, returnSearch, returnPage,
                MobCategory.HOSTILE, DEFAULT_ITEM_ID, 1, 1, 50.0f, false, null, null);
    }

    public HardcoreAddDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage,
            MobCategory selectedCategory,
            String itemId,
            int minQuantity,
            int maxQuantity,
            float dropChance
    ) {
        this(plugin, playerRef, returnFilter, returnSearch, returnPage,
                selectedCategory, itemId, minQuantity, maxQuantity, dropChance, false, null, null);
    }

    public static HardcoreAddDropPage forEdit(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage,
            MobCategory originalCategory,
            String originalItemId,
            int minQuantity,
            int maxQuantity,
            float dropChance
    ) {
        return new HardcoreAddDropPage(
                plugin,
                playerRef,
                returnFilter,
                returnSearch,
                returnPage,
                originalCategory,
                originalItemId,
                minQuantity,
                maxQuantity,
                dropChance,
                true,
                originalCategory,
                originalItemId
        );
    }

    private HardcoreAddDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage,
            MobCategory selectedCategory,
            String itemId,
            int minQuantity,
            int maxQuantity,
            float dropChance,
            boolean editMode,
            MobCategory originalCategory,
            String originalItemId
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreAddDropPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.returnFilter = returnFilter;
        this.returnSearch = returnSearch != null ? returnSearch : "";
        this.returnPage = Math.max(0, returnPage);
        this.editMode = editMode;
        this.originalCategory = originalCategory;
        this.originalItemId = originalItemId;
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

        if (Boolean.TRUE.equals(data.getCancel())) {
            openBloodMoonDropsPage(ref, store);
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

        if (Boolean.TRUE.equals(data.getSave())) {
            String resolvedItemId = itemId != null && !itemId.trim().isEmpty() ? itemId.trim() : DEFAULT_ITEM_ID;
            int minQty = minQuantity;
            int maxQty = Math.max(minQty, maxQuantity);
            float chance = dropChance;

            BloodMoonDropConfig dropConfig = plugin.getBloodMoonDropConfig();
            if (editMode) {
                dropConfig.updateDropEntry(
                        originalCategory,
                        originalItemId,
                        selectedCategory,
                        resolvedItemId,
                        minQty,
                        maxQty,
                        chance
                );
            } else {
                dropConfig.addDropEntry(selectedCategory, resolvedItemId, minQty, maxQty, chance);
            }

            openBloodMoonDropsPage(ref, store);
            return;
        }

        if (clamped) {
            sendFormUpdate();
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, editMode ? "Edit Blood Moon Drop" : "Add Blood Moon Drop");
        commands.set(SELECTED_CATEGORY_ID, "Selected: " + formatCategoryName(selectedCategory));
    }

    private void fillForm(UICommandBuilder commands) {
        commands.set(ITEM_ID_VALUE_PATH, itemId != null ? itemId : DEFAULT_ITEM_ID);
        commands.set(MIN_QUANTITY_VALUE_PATH, String.valueOf(minQuantity));
        commands.set(MAX_QUANTITY_VALUE_PATH, String.valueOf(maxQuantity));
        commands.set(DROP_CHANCE_VALUE_PATH, formatChance(dropChance));
    }

    private void bindEvents(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, ITEM_ID_INPUT_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ITEM_ID, ITEM_ID_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, HOSTILE_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_HOSTILE, HOSTILE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, ELITE_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_ELITE, ELITE_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, MINIBOSS_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_MINIBOSS, MINIBOSS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, WORLDBOSS_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_WORLDBOSS, WORLDBOSS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MIN_QUANTITY_INPUT_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_MIN_QUANTITY, MIN_QUANTITY_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, MAX_QUANTITY_INPUT_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_MAX_QUANTITY, MAX_QUANTITY_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, DROP_CHANCE_INPUT_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_DROP_CHANCE, DROP_CHANCE_VALUE_PATH), false);
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
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, SAVE_BUTTON_PATH,
                EventData.of(HardcoreAddDropPageEventData.KEY_SAVE, SAVE_VALUE_PATH), false);
    }

    private void openBloodMoonDropsPage(Ref<EntityStore> ref, Store<EntityStore> store) {
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
                new HardcoreBloodMoonDropsPage(plugin, playerRef, returnFilter, returnSearch, returnPage)
        );
    }

    private void reopenPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        HardcoreAddDropPage page = editMode
                ? new HardcoreAddDropPage(
                        plugin,
                        playerRef,
                        returnFilter,
                        returnSearch,
                        returnPage,
                        selectedCategory,
                        itemId,
                        minQuantity,
                        maxQuantity,
                        dropChance,
                        true,
                        originalCategory,
                        originalItemId
                )
                : new HardcoreAddDropPage(
                        plugin,
                        playerRef,
                        returnFilter,
                        returnSearch,
                        returnPage,
                        selectedCategory,
                        itemId,
                        minQuantity,
                        maxQuantity,
                        dropChance
                );

        player.getPageManager().openCustomPage(ref, store, page);
    }

    private boolean applyMinQuantityInput(String value) {
        Integer parsed = parseInt(value);
        if (parsed == null) {
            return false;
        }
        int clampedValue = clamp(parsed, MIN_QTY_MIN, MIN_QTY_MAX);
        boolean changed = clampedValue != minQuantity;
        minQuantity = clampedValue;
        if (maxQuantity < minQuantity) {
            maxQuantity = minQuantity;
            changed = true;
        }
        return changed || clampedValue != parsed;
    }

    private boolean applyMaxQuantityInput(String value) {
        Integer parsed = parseInt(value);
        if (parsed == null) {
            return false;
        }
        int clampedValue = clamp(parsed, MAX_QTY_MIN, MAX_QTY_MAX);
        if (clampedValue < minQuantity) {
            clampedValue = minQuantity;
        }
        boolean changed = clampedValue != maxQuantity;
        maxQuantity = clampedValue;
        return changed || clampedValue != parsed;
    }

    private boolean applyDropChanceInput(String value) {
        Float parsed = parseFloat(value);
        if (parsed == null) {
            return false;
        }
        float clampedValue = clamp(parsed, CHANCE_MIN, CHANCE_MAX);
        boolean changed = Float.compare(clampedValue, dropChance) != 0;
        dropChance = clampedValue;
        return changed || Float.compare(clampedValue, parsed) != 0;
    }

    private void handleAdjustAction(String action) {
        if ("min_qty:-".equalsIgnoreCase(action)) {
            minQuantity = clamp(minQuantity - QTY_STEP, MIN_QTY_MIN, MIN_QTY_MAX);
            if (maxQuantity < minQuantity) {
                maxQuantity = minQuantity;
            }
        } else if ("min_qty:+".equalsIgnoreCase(action)) {
            minQuantity = clamp(minQuantity + QTY_STEP, MIN_QTY_MIN, MIN_QTY_MAX);
            if (maxQuantity < minQuantity) {
                maxQuantity = minQuantity;
            }
        } else if ("max_qty:-".equalsIgnoreCase(action)) {
            maxQuantity = clamp(maxQuantity - QTY_STEP, MAX_QTY_MIN, MAX_QTY_MAX);
            if (maxQuantity < minQuantity) {
                maxQuantity = minQuantity;
            }
        } else if ("max_qty:+".equalsIgnoreCase(action)) {
            maxQuantity = clamp(maxQuantity + QTY_STEP, MAX_QTY_MIN, MAX_QTY_MAX);
            if (maxQuantity < minQuantity) {
                maxQuantity = minQuantity;
            }
        } else if ("chance:-".equalsIgnoreCase(action)) {
            dropChance = clamp(dropChance - CHANCE_STEP, CHANCE_MIN, CHANCE_MAX);
        } else if ("chance:+".equalsIgnoreCase(action)) {
            dropChance = clamp(dropChance + CHANCE_STEP, CHANCE_MIN, CHANCE_MAX);
        }
    }

    private void sendFormUpdate() {
        UICommandBuilder updateCommands = new UICommandBuilder();
        fillForm(updateCommands);
        sendUpdate(updateCommands, new UIEventBuilder(), false);
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Float parseFloat(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Float.parseFloat(value.trim().replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private float clamp(float value, float min, float max) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private String formatChance(float value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name().toLowerCase(Locale.US);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
