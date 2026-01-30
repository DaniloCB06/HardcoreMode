package com.example.plugin.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * HUD customizado que exibe a barra de progresso da Blood Moon.
 * Diferente de InteractiveCustomUIPage, este elemento não pausa o jogo.
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
            // 18 segmentos = intervalo de ~10 minutos cada (180 min / 18 = 10 min)
            int visibleSegments = (int) Math.ceil(currentProgress * 18.0f);
            
            // Atualiza visibilidade dos segmentos (esvazia da direita para esquerda)
            for (int i = 0; i < 18; i++) {
                boolean isSegmentVisible = i < visibleSegments;
                commands.set("#ProgressSegment" + i + ".Visible", isSegmentVisible);
            }
            
            // Atualiza a cor de todos os segmentos
            String color = getProgressColor(currentProgress);
            for (int i = 0; i < 18; i++) {
                commands.set("#ProgressSegment" + i + ".Background.Color", color);
            }
            
            // Atualiza o texto da porcentagem
            int percentage = Math.round(currentProgress * 100.0f);
            commands.set("#ProgressText.Text", percentage + "%");
            
            commands.set("#BloodMoonProgressContainer.Visible", true);
        } else {
            commands.set("#BloodMoonProgressContainer.Visible", false);
        }
    }
    /**
     * Retorna a cor da barra baseada no progresso
     * 100-50%: Vermelho escuro (#CC0000)
     * 50-25%: Laranja (#FF6600)
     * 25-0%: Amarelo (#FFCC00)
     */
    private String getProgressColor(float progress) {
        if (progress > 0.5f) {
            return "#CC0000"; // Vermelho
        } else if (progress > 0.25f) {
            return "#FF6600"; // Laranja
        } else {
            return "#FFCC00"; // Amarelo
        }
    }
    
    /**
     * HUD vazio usado para remover a Blood Moon HUD da tela
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
