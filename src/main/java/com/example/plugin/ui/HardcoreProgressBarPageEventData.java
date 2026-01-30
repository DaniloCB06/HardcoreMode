package com.example.plugin.ui;

import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * EventData para a página da barra de progresso da Blood Moon.
 * Como a barra só recebe dados do servidor, não precisa processar eventos do cliente.
 */
public class HardcoreProgressBarPageEventData {
    public static final BuilderCodec<HardcoreProgressBarPageEventData> CODEC = BuilderCodec
            .builder(HardcoreProgressBarPageEventData.class, HardcoreProgressBarPageEventData::new)
            .build();

    public HardcoreProgressBarPageEventData() {
        // Construtor vazio - não precisa de dados
    }
}
