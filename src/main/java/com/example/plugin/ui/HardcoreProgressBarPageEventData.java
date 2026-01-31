package com.example.plugin.ui;

import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * EventData for the Blood Moon progress bar page.
 * Since the bar only receives data from the server, it doesn't need to process client events.
 */
public class HardcoreProgressBarPageEventData {
    public static final BuilderCodec<HardcoreProgressBarPageEventData> CODEC = BuilderCodec
            .builder(HardcoreProgressBarPageEventData.class, HardcoreProgressBarPageEventData::new)
            .build();

    public HardcoreProgressBarPageEventData() {
    }
}
