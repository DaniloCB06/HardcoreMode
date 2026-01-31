package com.example.plugin.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcoreProgressBarPage extends InteractiveCustomUIPage<HardcoreProgressBarPageEventData> {
    private static final String PAGE_PATH = "Hud/HardcoreProgressBar.ui";
    
    private final float currentProgress;
    private final int hoursRemaining;
    private final boolean isVisible;

    public HardcoreProgressBarPage(float progress, int hoursRemaining) {
        super(null, CustomPageLifetime.CanDismiss, HardcoreProgressBarPageEventData.CODEC);
        this.currentProgress = Math.max(0.0f, Math.min(1.0f, progress));
        this.hoursRemaining = hoursRemaining;
        this.isVisible = this.currentProgress > 0.0f;
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commands,
            UIEventBuilder events,
            Store<EntityStore> store
    ) {
        commands.append(PAGE_PATH);
        
        if (isVisible) {
            // Update percentage text
            int percentage = Math.round(currentProgress * 100.0f);
            commands.set("#ProgressText.Text", percentage + "%");
            
            // Update remaining time
            String timeText;
            if (hoursRemaining > 1) {
                timeText = "Time Remaining: " + hoursRemaining + " hours";
            } else if (hoursRemaining == 1) {
                timeText = "Time Remaining: 1 hour";
            } else {
                timeText = "Time Remaining: < 1 hour";
            }
            commands.set("#TimeRemaining.Text", timeText);
            
            // Change bar color as progress decreases
            String color = getProgressColor(currentProgress);
            commands.set("#ProgressFill.Background.Color", color);
            
            commands.set("#BloodMoonProgressContainer.Visible", true);
        } else {
            commands.set("#BloodMoonProgressContainer.Visible", false);
        }
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            HardcoreProgressBarPageEventData data
    ) {
    }

    private String getProgressColor(float progress) {
        if (progress > 0.5f) {
            return "#CC0000";
        } else if (progress > 0.25f) {
            return "#FF6600";
        } else {
            return "#FFCC00";
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public float getCurrentProgress() {
        return currentProgress;
    }
}
