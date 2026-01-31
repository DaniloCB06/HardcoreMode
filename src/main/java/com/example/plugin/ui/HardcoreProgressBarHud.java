package com.example.plugin.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * Custom HUD that displays the Blood Moon progress bar.
 * Unlike InteractiveCustomUIPage, this element doesn't pause the game.
 */
public class HardcoreProgressBarHud extends CustomUIHud {
    private static final String HUD_PATH = "Hud/HardcoreProgressBar.ui";
    
    private float currentProgress;
    private int hoursRemaining;
    private boolean isVisible;
    
    public HardcoreProgressBarHud(PlayerRef playerRef, float progress, int hoursRemaining, boolean visible) {
        super(playerRef);
        this.currentProgress = Math.max(0.0f, Math.min(1.0f, progress));
        this.hoursRemaining = hoursRemaining;
        this.isVisible = visible;
    }
    
    @Override
    protected void build(UICommandBuilder commands) {
        commands.append(HUD_PATH);
        
        if (isVisible) {
            // 18 segments = ~10 minute intervals each (180 min / 18 = 10 min)
            int visibleSegments = (int) Math.ceil(currentProgress * 18.0f);
            
            // Update segment visibility (empties from right to left)
            for (int i = 0; i < 18; i++) {
                boolean isSegmentVisible = i < visibleSegments;
                commands.set("#ProgressSegment" + i + ".Visible", isSegmentVisible);
            }
            
            // Update color for all segments
            String color = getProgressColor(currentProgress);
            for (int i = 0; i < 18; i++) {
                commands.set("#ProgressSegment" + i + ".Background.Color", color);
            }
            
            // Update percentage text
            int percentage = Math.round(currentProgress * 100.0f);
            commands.set("#ProgressText.Text", percentage + "%");
            
            commands.set("#BloodMoonProgressContainer.Visible", true);
        } else {
            commands.set("#BloodMoonProgressContainer.Visible", false);
        }
    }
    /**
     * Returns the bar color based on progress
     * 100-50%: Dark red (#CC0000)
     * 50-25%: Orange (#FF6600)
     * 25-0%: Yellow (#FFCC00)
     */
    private String getProgressColor(float progress) {
        if (progress > 0.5f) {
            return "#CC0000"; // Red
        } else if (progress > 0.25f) {
            return "#FF6600"; // Orange
        } else {
            return "#FFCC00"; // Yellow
        }
    }
    
    /**
     * Empty HUD used to remove the Blood Moon HUD from the screen
     */
    public static class EmptyHud extends CustomUIHud {
        public EmptyHud(PlayerRef playerRef) {
            super(playerRef);
        }
        
        @Override
        protected void build(UICommandBuilder builder) {
            // Intencionalmente vazio - remove a UI
        }
    }
}
