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

import java.util.Locale;

public class HardcoreRemoveMobCategoryPage extends InteractiveCustomUIPage<HardcoreRemoveMobCategoryPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreRemoveMobCategoryPage.ui";
    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String PATTERN_INFO_ID = "#PatternInfoLabel.Text";
    private static final String CATEGORY_INFO_ID = "#CategoryInfoLabel.Text";

    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String CONFIRM_BUTTON_PATH = "#BottomButtonsContainer #ConfirmButton";
    private static final String CONFIRM_VALUE_PATH = "#BottomButtonsContainer #ConfirmValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final MobCategoryResolver.CategoryEntry entry;
    private final MobCategory returnFilter;
    private final int returnPage;

    public HardcoreRemoveMobCategoryPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategoryResolver.CategoryEntry entry,
            MobCategory returnFilter,
            int returnPage
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreRemoveMobCategoryPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.entry = entry;
        this.returnFilter = returnFilter;
        this.returnPage = Math.max(0, returnPage);
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commands,
            UIEventBuilder events,
            Store<EntityStore> store
    ) {
        commands.append(PAGE_PATH);

        commands.set(PAGE_TITLE_ID, "Remove Mob Category");
        commands.set(PATTERN_INFO_ID, "Pattern: " + (entry != null ? entry.pattern : "Unknown"));
        commands.set(CATEGORY_INFO_ID, "Category: " + formatCategoryName(entry != null ? entry.category : null));

        EventData cancelData = EventData.of(HardcoreRemoveMobCategoryPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH, cancelData, false);

        EventData confirmData = EventData.of(HardcoreRemoveMobCategoryPageEventData.KEY_CONFIRM, CONFIRM_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CONFIRM_BUTTON_PATH, confirmData, false);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreRemoveMobCategoryPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getCancel())) {
            openMobCategoriesPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getConfirm())) {
            if (entry != null) {
                plugin.getMobCategoryResolver().removeEntry(entry.category, entry.pattern);
            }
            openMobCategoriesPage(ref, store);
        }
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

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }
}
