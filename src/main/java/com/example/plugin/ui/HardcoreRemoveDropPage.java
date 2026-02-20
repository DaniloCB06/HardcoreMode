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

public class HardcoreRemoveDropPage extends InteractiveCustomUIPage<HardcoreRemoveDropPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreRemoveDropPage.ui";

    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String ITEM_INFO_ID = "#ItemInfoLabel.Text";
    private static final String CATEGORY_INFO_ID = "#CategoryInfoLabel.Text";
    private static final String QUANTITY_INFO_ID = "#QuantityInfoLabel.Text";
    private static final String CHANCE_INFO_ID = "#ChanceInfoLabel.Text";

    private static final String CANCEL_BUTTON_PATH = "#BottomButtonsContainer #CancelButton";
    private static final String CANCEL_VALUE_PATH = "#BottomButtonsContainer #CancelValue.Value";
    private static final String CONFIRM_BUTTON_PATH = "#BottomButtonsContainer #ConfirmButton";
    private static final String CONFIRM_VALUE_PATH = "#BottomButtonsContainer #ConfirmValue.Value";

    private final HardcoreModePlugin plugin;
    private final PlayerRef playerRef;
    private final MobCategory category;
    private final String itemId;
    private final BloodMoonDropConfig.DropEntry entry;
    private final int returnPage;

    public HardcoreRemoveDropPage(
            HardcoreModePlugin plugin,
            PlayerRef playerRef,
            MobCategory category,
            String itemId,
            BloodMoonDropConfig.DropEntry entry,
            int returnPage
    ) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreRemoveDropPageEventData.CODEC);
        this.plugin = plugin;
        this.playerRef = playerRef;
        this.category = category;
        this.itemId = itemId;
        this.entry = entry;
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

        commands.set(PAGE_TITLE_ID, "Remove Drop");
        commands.set(ITEM_INFO_ID, "Item: " + (itemId != null ? itemId : "Unknown"));
        commands.set(CATEGORY_INFO_ID, "Category: " + formatCategoryName(category));

        if (entry != null) {
            commands.set(QUANTITY_INFO_ID, "Quantity: " + entry.minQuantity + " - " + entry.maxQuantity);
            commands.set(CHANCE_INFO_ID, String.format(Locale.US, "Chance: %.1f%%", entry.dropChance));
        } else {
            commands.set(QUANTITY_INFO_ID, "Quantity: -");
            commands.set(CHANCE_INFO_ID, "Chance: -");
        }

        EventData cancelData = EventData.of(HardcoreRemoveDropPageEventData.KEY_CANCEL, CANCEL_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CANCEL_BUTTON_PATH, cancelData, false);

        EventData confirmData = EventData.of(HardcoreRemoveDropPageEventData.KEY_CONFIRM, CONFIRM_VALUE_PATH);
        events.addEventBinding(CustomUIEventBindingType.Activating, CONFIRM_BUTTON_PATH, confirmData, false);
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreRemoveDropPageEventData data
    ) {
        if (data == null) {
            return;
        }

        if (Boolean.TRUE.equals(data.getCancel())) {
            openDropsPage(ref, store);
            return;
        }

        if (Boolean.TRUE.equals(data.getConfirm())) {
            if (category != null && itemId != null) {
                plugin.getBloodMoonDropConfig().removeDropEntry(category, itemId);
            }
            openDropsPage(ref, store);
        }
    }

    private void openDropsPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (store == null || ref == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store,
                new HardcoreBloodMoonDropsPage(plugin, playerRef, returnPage));
    }

    private String formatCategoryName(MobCategory category) {
        if (category == null) {
            return "Unknown";
        }
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }
}
