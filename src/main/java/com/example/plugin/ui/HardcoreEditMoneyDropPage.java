package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.MobCategoryResolver;
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

public class HardcoreEditMoneyDropPage extends InteractiveCustomUIPage<HardcoreEditMoneyDropPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreEditMoneyDropPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String SECTION_TITLE_ID = "#SectionTitle.Text";
    private static final String TARGET_LABEL_ID = "#TargetLabel.Text";
    private static final String CURRENT_VALUE_ID = "#CurrentValue.Text";
    private static final String NOTE_LABEL_ID = "#NoteLabel.Text";
    private static final String AMOUNT_INPUT_PATH = "#AmountInput";
    private static final String AMOUNT_VALUE_PATH = "#AmountInput.Value";
    private static final String AMOUNT_MINUS_PATH = "#AmountMinus";
    private static final String AMOUNT_PLUS_PATH = "#AmountPlus";
    private static final String ADJUST_MINUS_VALUE_PATH = "#AdjustValues #AdjustAmountMinus.Value";
    private static final String ADJUST_PLUS_VALUE_PATH = "#AdjustValues #AdjustAmountPlus.Value";
    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String SAVE_BUTTON_PATH = "#BottomButtonsContainer #SaveButton";
    private static final String SAVE_VALUE_PATH = "#BottomButtonsContainer #SaveValue.Value";
    private static final double AMOUNT_MIN = 0.0d;
    private static final double AMOUNT_MAX = 1_000_000.0d;
    private static final double STEP = 1.0d;

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final MobCategory targetCategory;
    private final String targetPattern;
    private final MobCategory returnFilter;
    private final String returnSearch;
    private final int returnPage;
    private final boolean categoryMode;
    private double amount;

    private HardcoreEditMoneyDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory targetCategory,
            String targetPattern,
            boolean categoryMode,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreEditMoneyDropPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.targetCategory = targetCategory;
        this.targetPattern = targetPattern;
        this.categoryMode = categoryMode;
        this.returnFilter = returnFilter;
        this.returnSearch = returnSearch == null ? "" : returnSearch;
        this.returnPage = Math.max(0, returnPage);
        this.amount = resolveInitialAmount();
    }

    public static HardcoreEditMoneyDropPage forCategory(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory category,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage
    ) {
        return new HardcoreEditMoneyDropPage(plugin, playerRef, category, null, true, returnFilter, returnSearch, returnPage);
    }

    public static HardcoreEditMoneyDropPage forMob(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory category,
            String pattern,
            MobCategory returnFilter,
            String returnSearch,
            int returnPage
    ) {
        return new HardcoreEditMoneyDropPage(plugin, playerRef, category, pattern, false, returnFilter, returnSearch, returnPage);
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
            HardcoreEditMoneyDropPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (data.getAmount() != null) {
            amount = clamp(parseAmount(data.getAmount()));
        }

        if (data.getAdjustAction() != null) {
            handleAdjustAction(data.getAdjustAction());
            reopenPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getCancel())) {
            openMoneyPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getSave())) {
            saveAmount();
            openMoneyPage(ref, store);
        }
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(SECTION_TITLE_ID, categoryMode ? "Edit Category Money" : "Edit Mob Money");
        commands.set(TARGET_LABEL_ID, categoryMode
                ? "Category: " + formatCategoryName(targetCategory)
                : "Mob Pattern: " + targetPattern);
        commands.set(CURRENT_VALUE_ID, "Current Money: " + formatAmount(amount));
        commands.set(NOTE_LABEL_ID, categoryMode
                ? "Saving here updates the default reward for the whole category and clears old mob-specific overrides in that category."
                : "Saving here overrides only this mob pattern. Clear on the previous page forces this mob to reward 0 money.");
        commands.set(AMOUNT_VALUE_PATH, formatAmount(amount));
    }

    private void bindEvents(UIEventBuilder events) {
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, AMOUNT_INPUT_PATH,
                EventData.of(HardcoreEditMoneyDropPageEventData.KEY_AMOUNT, AMOUNT_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, AMOUNT_MINUS_PATH,
                EventData.of(HardcoreEditMoneyDropPageEventData.KEY_ADJUST_ACTION, ADJUST_MINUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, AMOUNT_PLUS_PATH,
                EventData.of(HardcoreEditMoneyDropPageEventData.KEY_ADJUST_ACTION, ADJUST_PLUS_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH,
                EventData.of(HardcoreEditMoneyDropPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, SAVE_BUTTON_PATH,
                EventData.of(HardcoreEditMoneyDropPageEventData.KEY_SAVE, SAVE_VALUE_PATH), false);
    }

    private double resolveInitialAmount() {
        if (categoryMode) {
            return plugin.getMobMoneyDropConfig().getCategoryAmount(targetCategory);
        }
        return plugin.getMobMoneyDropConfig().getEffectiveAmount(targetPattern, targetCategory);
    }

    private void saveAmount() {
        if (categoryMode) {
            List<String> patterns = new ArrayList<>();
            for (MobCategoryResolver.CategoryEntry entry : plugin.getMobCategoryResolver().getEntries()) {
                if (entry.category == targetCategory) {
                    patterns.add(entry.pattern);
                }
            }
            plugin.getMobMoneyDropConfig().setCategoryAmount(targetCategory, amount);
            plugin.getMobMoneyDropConfig().removeMobOverrides(patterns);
            return;
        }

        plugin.getMobMoneyDropConfig().setMobAmount(targetPattern, amount);
    }

    private void handleAdjustAction(String action) {
        if ("amount:-".equalsIgnoreCase(action)) {
            amount = clamp(amount - STEP);
        } else if ("amount:+".equalsIgnoreCase(action)) {
            amount = clamp(amount + STEP);
        }
    }

    private void openMoneyPage(Ref<EntityStore> ref, Store<EntityStore> store) {
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
                new HardcoreMoneyMobDropsPage(plugin, playerRef, returnFilter, returnSearch, returnPage)
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

        HardcoreEditMoneyDropPage reopened = new HardcoreEditMoneyDropPage(
                plugin,
                playerRef,
                targetCategory,
                targetPattern,
                categoryMode,
                returnFilter,
                returnSearch,
                returnPage
        );
        reopened.amount = amount;
        player.getPageManager().openCustomPage(ref, store, reopened);
    }

    private double parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return amount;
        }
        try {
            return Double.parseDouble(raw.trim().replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return amount;
        }
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return amount;
        }
        return Math.max(AMOUNT_MIN, Math.min(AMOUNT_MAX, value));
    }

    private String formatCategoryName(MobCategory category) {
        String name = category.name().toLowerCase(Locale.US);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String formatAmount(double value) {
        return String.format(Locale.US, "%.2f", Math.max(0.0d, value));
    }
}
