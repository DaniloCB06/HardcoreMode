package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobDisposition;
import com.example.plugin.config.HardcoreModeConfig;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;
import java.util.function.Consumer;

public class HardcoreSettingsPage extends InteractiveCustomUIPage<HardcoreSettingsPageEventData> {
    private static final String PAGE_PATH = "Pages/HardcoreModePage.ui";
    private static final String ENTRY_TOGGLE_PATH = "Pages/HardcoreToggleRow.ui";
    private static final String ENTRY_CATEGORY_TOGGLE_PATH = "Pages/HardcoreCategoryToggleRow.ui";
    private static final String ENTRY_SLIDER_PATH = "Pages/HardcoreSliderRow.ui";
    private static final String ENTRY_DURATION_PATH = "Pages/HardcoreBloodMoonDurationRow.ui";
    private static final String ENTRY_FORCE_PATH = "Pages/HardcoreBloodMoonForceRow.ui";
    private static final String ENTRY_HEADER_PATH = "Pages/HardcoreHeaderRow.ui";
    private static final String ENEMY_LIST_ID = "#EnemySettingsList";
    private static final String BLOOD_MOON_LIST_ID = "#BloodMoonSettingsList";

    private static final String PAGE_TITLE_ID = "#PageTitle.Text";
    private static final String ENEMY_SECTION_TITLE_ID = "#EnemySectionTitle.Text";
    private static final String BLOOD_MOON_SECTION_TITLE_ID = "#BloodMoonSectionTitle.Text";

    private static final float MIN_MULTIPLIER = 1.0f;
    private static final float MAX_MULTIPLIER = 5.0f;
    private static final float STEP = 0.25f;
    private static final float EPSILON = 0.0001f;
    private static final int MIN_BLOOD_MOON_DAYS = 1;
    private static final int MAX_BLOOD_MOON_DAYS = 30;
    private static final float BLOOD_MOON_DAY_STEP = 1.0f;
    private static final int MIN_BLOOD_MOON_HOUR = 0;
    private static final int MAX_BLOOD_MOON_HOUR = 23;
    private static final float BLOOD_MOON_HOUR_STEP = 1.0f;
    private static final int BLOOD_MOON_DURATION_1H = 1;
    private static final int BLOOD_MOON_DURATION_3H = 3;
    private static final int BLOOD_MOON_DURATION_6H = 6;
    private static final int BLOOD_MOON_DURATION_9H = 9;
    private static final int BLOOD_MOON_DURATION_12H = 12;
    private static final int MIN_PERCENT = 0;
    private static final int MAX_PERCENT = 100;
    private static final float PERCENT_STEP = 10.0f;

    private final HardcoreModePlugin plugin;

    public HardcoreSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HardcoreSettingsPageEventData.CODEC);
        this.plugin = plugin;
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
        buildSettingsList(commands, events);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, HardcoreSettingsPageEventData data) {
        if (data == null) {
            return;
        }

        Boolean enabled = data.getEnabled();
        Float globalHealthMultiplier = data.getGlobalHealthMultiplier();
        Float globalDamageMultiplier = data.getGlobalDamageMultiplier();
        Boolean peacefulEnabled = data.getPeacefulEnabled();
        Float peacefulHealthMultiplier = data.getPeacefulHealthMultiplier();
        Float peacefulDamageMultiplier = data.getPeacefulDamageMultiplier();
        Boolean neutralEnabled = data.getNeutralEnabled();
        Float neutralHealthMultiplier = data.getNeutralHealthMultiplier();
        Float neutralDamageMultiplier = data.getNeutralDamageMultiplier();
        Boolean hostileEnabled = data.getHostileEnabled();
        Float hostileHealthMultiplier = data.getHostileHealthMultiplier();
        Float hostileDamageMultiplier = data.getHostileDamageMultiplier();
        Boolean bloodMoonEnabled = data.getBloodMoonEnabled();
        Float bloodMoonIntervalDays = data.getBloodMoonIntervalDays();
        Float bloodMoonStartHour = data.getBloodMoonStartHour();
        Boolean bloodMoonDuration1h = data.getBloodMoonDuration1h();
        Boolean bloodMoonDuration3h = data.getBloodMoonDuration3h();
        Boolean bloodMoonDuration6h = data.getBloodMoonDuration6h();
        Boolean bloodMoonDuration9h = data.getBloodMoonDuration9h();
        Boolean bloodMoonDuration12h = data.getBloodMoonDuration12h();
        Float bloodMoonHostileHealthMultiplier = data.getBloodMoonHostileHealthMultiplier();
        Float bloodMoonHostileDamageMultiplier = data.getBloodMoonHostileDamageMultiplier();
        Boolean bloodMoonForce = data.getBloodMoonForce();
        Boolean playerDeathSettingsEnabled = data.getPlayerDeathSettingsEnabled();
        Float playerItemDurabilityLossPercent = data.getPlayerItemDurabilityLossPercent();
        Float playerItemDropPercent = data.getPlayerItemDropPercent();
        if (enabled == null
                && globalHealthMultiplier == null
                && globalDamageMultiplier == null
                && peacefulEnabled == null
                && peacefulHealthMultiplier == null
                && peacefulDamageMultiplier == null
                && neutralEnabled == null
                && neutralHealthMultiplier == null
                && neutralDamageMultiplier == null
                && hostileEnabled == null
                && hostileHealthMultiplier == null
                && hostileDamageMultiplier == null
                && bloodMoonEnabled == null
                && bloodMoonIntervalDays == null
                && bloodMoonStartHour == null
                && bloodMoonDuration1h == null
                && bloodMoonDuration3h == null
                && bloodMoonDuration6h == null
                && bloodMoonDuration9h == null
                && bloodMoonDuration12h == null
                && bloodMoonHostileHealthMultiplier == null
                && bloodMoonHostileDamageMultiplier == null
                && bloodMoonForce == null
                && playerDeathSettingsEnabled == null
                && playerItemDurabilityLossPercent == null
                && playerItemDropPercent == null) {
            return;
        }

        HardcoreModeConfig config = plugin.getConfigData();
        boolean changed = false;
        boolean refresh = false;

        if (enabled != null && enabled != config.enabled) {
            config.enabled = enabled;
            config.peacefulEnabled = enabled;
            config.neutralEnabled = enabled;
            config.hostileEnabled = enabled;
            changed = true;
        }

        changed |= applyMultiplier(
                globalHealthMultiplier,
                config.healthMultiplier,
                value -> {
                    config.healthMultiplier = value;
                    config.peacefulHealthMultiplier = value;
                    config.neutralHealthMultiplier = value;
                    config.hostileHealthMultiplier = value;
                }
        );
        changed |= applyMultiplier(
                globalDamageMultiplier,
                config.damageMultiplier,
                value -> {
                    config.damageMultiplier = value;
                    config.peacefulDamageMultiplier = value;
                    config.neutralDamageMultiplier = value;
                    config.hostileDamageMultiplier = value;
                }
        );
        changed |= applyEnabled(
                peacefulEnabled,
                config.peacefulEnabled,
                value -> config.peacefulEnabled = value
        );
        changed |= applyMultiplier(
                peacefulHealthMultiplier,
                config.peacefulHealthMultiplier,
                value -> config.peacefulHealthMultiplier = value
        );
        changed |= applyMultiplier(
                peacefulDamageMultiplier,
                config.peacefulDamageMultiplier,
                value -> config.peacefulDamageMultiplier = value
        );
        changed |= applyEnabled(
                neutralEnabled,
                config.neutralEnabled,
                value -> config.neutralEnabled = value
        );
        changed |= applyMultiplier(
                neutralHealthMultiplier,
                config.neutralHealthMultiplier,
                value -> config.neutralHealthMultiplier = value
        );
        changed |= applyMultiplier(
                neutralDamageMultiplier,
                config.neutralDamageMultiplier,
                value -> config.neutralDamageMultiplier = value
        );
        changed |= applyEnabled(
                hostileEnabled,
                config.hostileEnabled,
                value -> config.hostileEnabled = value
        );
        changed |= applyMultiplier(
                hostileHealthMultiplier,
                config.hostileHealthMultiplier,
                value -> config.hostileHealthMultiplier = value
        );
        changed |= applyMultiplier(
                hostileDamageMultiplier,
                config.hostileDamageMultiplier,
                value -> config.hostileDamageMultiplier = value
        );
        changed |= applyEnabled(
                bloodMoonEnabled,
                config.bloodMoonEnabled,
                value -> config.bloodMoonEnabled = value
        );
        changed |= applyIntSetting(
                bloodMoonIntervalDays,
                config.bloodMoonIntervalDays,
                MIN_BLOOD_MOON_DAYS,
                MAX_BLOOD_MOON_DAYS,
                BLOOD_MOON_DAY_STEP,
                value -> config.bloodMoonIntervalDays = value
        );
        changed |= applyIntSetting(
                bloodMoonStartHour,
                config.bloodMoonStartHour,
                MIN_BLOOD_MOON_HOUR,
                MAX_BLOOD_MOON_HOUR,
                BLOOD_MOON_HOUR_STEP,
                value -> config.bloodMoonStartHour = value
        );
        DurationUpdate durationUpdate = applyDurationSelection(
                bloodMoonDuration1h,
                bloodMoonDuration3h,
                bloodMoonDuration6h,
                bloodMoonDuration9h,
                bloodMoonDuration12h,
                config
        );
        changed |= durationUpdate.changed;
        refresh |= durationUpdate.refresh;
        changed |= applyFloatSetting(
                bloodMoonHostileHealthMultiplier,
                config.bloodMoonHostileHealthMultiplier,
                MIN_MULTIPLIER,
                MAX_MULTIPLIER,
                STEP,
                value -> config.bloodMoonHostileHealthMultiplier = value
        );
        changed |= applyFloatSetting(
                bloodMoonHostileDamageMultiplier,
                config.bloodMoonHostileDamageMultiplier,
                MIN_MULTIPLIER,
                MAX_MULTIPLIER,
                STEP,
                value -> config.bloodMoonHostileDamageMultiplier = value
        );
        changed |= applyEnabled(
                playerDeathSettingsEnabled,
                config.playerDeathSettingsEnabled,
                value -> config.playerDeathSettingsEnabled = value
        );
        changed |= applyIntSetting(
                playerItemDurabilityLossPercent,
                config.playerItemDurabilityLossPercent,
                MIN_PERCENT,
                MAX_PERCENT,
                PERCENT_STEP,
                value -> config.playerItemDurabilityLossPercent = value
        );
        changed |= applyIntSetting(
                playerItemDropPercent,
                config.playerItemDropPercent,
                MIN_PERCENT,
                MAX_PERCENT,
                PERCENT_STEP,
                value -> config.playerItemDropPercent = value
        );

        if (Boolean.TRUE.equals(bloodMoonForce)) {
            plugin.forceBloodMoonNow(store);
        }

        if (!changed && !refresh) {
            return;
        }

        if (changed) {
            plugin.getConfig().save();
            plugin.refreshBloodMoonState(store, false);
            plugin.applyToExistingMobs(store);
        }

        UICommandBuilder updateCommands = new UICommandBuilder();
        UIEventBuilder updateEvents = new UIEventBuilder();
        fillHeader(updateCommands);
        buildSettingsList(updateCommands, updateEvents);
        sendUpdate(updateCommands, updateEvents, false);
    }

    private void fillHeader(UICommandBuilder commands) {
        commands.set(PAGE_TITLE_ID, "Hardcore Mode");
        commands.set(ENEMY_SECTION_TITLE_ID, "Enemy Settings");
        commands.set(BLOOD_MOON_SECTION_TITLE_ID, "Blood Moon");
    }

    private void buildSettingsList(UICommandBuilder commands, UIEventBuilder events) {
        HardcoreModeConfig config = plugin.getConfigData();
        commands.clear(ENEMY_LIST_ID);
        commands.clear(BLOOD_MOON_LIST_ID);

        float peacefulHealth = plugin.getHealthMultiplier(MobDisposition.PEACEFUL);
        float peacefulDamage = plugin.getDamageMultiplier(MobDisposition.PEACEFUL);
        float neutralHealth = plugin.getHealthMultiplier(MobDisposition.NEUTRAL);
        float neutralDamage = plugin.getDamageMultiplier(MobDisposition.NEUTRAL);
        float hostileHealth = plugin.getHealthMultiplier(MobDisposition.HOSTILE);
        float hostileDamage = plugin.getDamageMultiplier(MobDisposition.HOSTILE);

        int enemyIndex = 0;
        enemyIndex = addToggleEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Global Hardcore Mode: " + (config.enabled ? "ON" : "OFF"),
                config.enabled
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Global Health",
                config.healthMultiplier,
                HardcoreSettingsPageEventData.KEY_GLOBAL_HEALTH
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Global Damage",
                config.damageMultiplier,
                HardcoreSettingsPageEventData.KEY_GLOBAL_DAMAGE
        );
        enemyIndex = addCategoryToggleEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Peaceful Creatures: " + (config.peacefulEnabled ? "ON" : "OFF"),
                config.peacefulEnabled,
                HardcoreSettingsPageEventData.KEY_PEACEFUL_ENABLED
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Health",
                peacefulHealth,
                HardcoreSettingsPageEventData.KEY_PEACEFUL_HEALTH
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Damage",
                peacefulDamage,
                HardcoreSettingsPageEventData.KEY_PEACEFUL_DAMAGE
        );
        enemyIndex = addCategoryToggleEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Neutral Creatures: " + (config.neutralEnabled ? "ON" : "OFF"),
                config.neutralEnabled,
                HardcoreSettingsPageEventData.KEY_NEUTRAL_ENABLED
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Health",
                neutralHealth,
                HardcoreSettingsPageEventData.KEY_NEUTRAL_HEALTH
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Damage",
                neutralDamage,
                HardcoreSettingsPageEventData.KEY_NEUTRAL_DAMAGE
        );
        enemyIndex = addCategoryToggleEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Hostile Creatures: " + (config.hostileEnabled ? "ON" : "OFF"),
                config.hostileEnabled,
                HardcoreSettingsPageEventData.KEY_HOSTILE_ENABLED
        );
        enemyIndex = addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Health",
                hostileHealth,
                HardcoreSettingsPageEventData.KEY_HOSTILE_HEALTH
        );
        addSliderEntry(
                commands,
                events,
                ENEMY_LIST_ID,
                enemyIndex,
                "Mob Damage",
                hostileDamage,
                HardcoreSettingsPageEventData.KEY_HOSTILE_DAMAGE
        );

        int bloodIndex = 0;
        bloodIndex = addCategoryToggleEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Blood Moon: " + (config.bloodMoonEnabled ? "ON" : "OFF"),
                config.bloodMoonEnabled,
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_ENABLED
        );
        bloodIndex = addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Every X Days",
                config.bloodMoonIntervalDays,
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_INTERVAL_DAYS,
                MIN_BLOOD_MOON_DAYS,
                MAX_BLOOD_MOON_DAYS,
                BLOOD_MOON_DAY_STEP,
                this::formatDays
        );
        bloodIndex = addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Start Hour",
                config.bloodMoonStartHour,
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_START_HOUR,
                MIN_BLOOD_MOON_HOUR,
                MAX_BLOOD_MOON_HOUR,
                BLOOD_MOON_HOUR_STEP,
                this::formatHour
        );
        bloodIndex = addBloodMoonDurationEntry(
                commands,
                events,
                bloodIndex,
                config.bloodMoonDurationHours
        );
        bloodIndex = addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Hostile Health",
                config.bloodMoonHostileHealthMultiplier,
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_HOSTILE_HEALTH
        );
        bloodIndex = addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Hostile Damage",
                config.bloodMoonHostileDamageMultiplier,
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_HOSTILE_DAMAGE
        );
        bloodIndex = addForceBloodMoonEntry(commands, events, bloodIndex);
        bloodIndex = addHeaderEntry(
                commands,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Player Settings"
        );
        bloodIndex = addCategoryToggleEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Death Settings: " + (config.playerDeathSettingsEnabled ? "ON" : "OFF"),
                config.playerDeathSettingsEnabled,
                HardcoreSettingsPageEventData.KEY_PLAYER_DEATH_SETTINGS_ENABLED
        );
        bloodIndex = addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Item Durability Loss",
                config.playerItemDurabilityLossPercent,
                HardcoreSettingsPageEventData.KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT,
                MIN_PERCENT,
                MAX_PERCENT,
                PERCENT_STEP,
                this::formatPercent
        );
        addSliderEntry(
                commands,
                events,
                BLOOD_MOON_LIST_ID,
                bloodIndex,
                "Inventory Drop Loss",
                config.playerItemDropPercent,
                HardcoreSettingsPageEventData.KEY_PLAYER_ITEM_DROP_PERCENT,
                MIN_PERCENT,
                MAX_PERCENT,
                PERCENT_STEP,
                this::formatPercent
        );
    }

    private int addToggleEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            String listId,
            int index,
            String label,
            boolean checkboxValue
    ) {
        String entry = listId + "[" + index + "]";
        commands.append(listId, ENTRY_TOGGLE_PATH);
        commands.set(entry + " #Label.Text", label);
        commands.set(entry + " #Toggle.Value", checkboxValue);

        EventData toggleData = EventData.of(
                HardcoreSettingsPageEventData.KEY_ENABLED,
                entry + " #Toggle.Value"
        );
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, entry + " #Toggle", toggleData, false);

        return index + 1;
    }

    private int addHeaderEntry(
            UICommandBuilder commands,
            String listId,
            int index,
            String label
    ) {
        String entry = listId + "[" + index + "]";
        commands.append(listId, ENTRY_HEADER_PATH);
        commands.set(entry + " #Label.Text", label);
        return index + 1;
    }

    private int addCategoryToggleEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            String listId,
            int index,
            String label,
            boolean checkboxValue,
            String key
    ) {
        String entry = listId + "[" + index + "]";
        commands.append(listId, ENTRY_CATEGORY_TOGGLE_PATH);
        commands.set(entry + " #Label.Text", label);
        commands.set(entry + " #Toggle.Value", checkboxValue);

        EventData toggleData = EventData.of(
                key,
                entry + " #Toggle.Value"
        );
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, entry + " #Toggle", toggleData, false);

        return index + 1;
    }

    private int addSliderEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            String listId,
            int index,
            String label,
            float value,
            String key
    ) {
        return addSliderEntry(
                commands,
                events,
                listId,
                index,
                label,
                value,
                key,
                MIN_MULTIPLIER,
                MAX_MULTIPLIER,
                STEP,
                this::formatMultiplierText
        );
    }

    private int addSliderEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            String listId,
            int index,
            String label,
            float value,
            String key,
            float min,
            float max,
            float step,
            java.util.function.Function<Float, String> formatter
    ) {
        String entry = listId + "[" + index + "]";
        commands.append(listId, ENTRY_SLIDER_PATH);
        commands.set(entry + " #Label.Text", label);
        commands.set(entry + " #Slider.Min", min);
        commands.set(entry + " #Slider.Max", max);
        commands.set(entry + " #Slider.Step", step);
        commands.set(entry + " #Slider.Value", clamp(value, min, max, step));
        commands.set(entry + " #Value.Text", formatter.apply(value));

        EventData sliderData = EventData.of(key, entry + " #Slider.Value");
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, entry + " #Slider", sliderData, false);

        return index + 1;
    }

    private int addBloodMoonDurationEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            int index,
            int selectedHours
    ) {
        String entry = BLOOD_MOON_LIST_ID + "[" + index + "]";
        commands.append(BLOOD_MOON_LIST_ID, ENTRY_DURATION_PATH);
        commands.set(entry + " #OneHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_1H);
        commands.set(entry + " #ThreeHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_3H);
        commands.set(entry + " #SixHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_6H);
        commands.set(entry + " #NineHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_9H);
        commands.set(entry + " #TwelveHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_12H);

        EventData oneHourData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_1H,
                entry + " #OneHourToggle.Value"
        );
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                entry + " #OneHourToggle",
                oneHourData,
                false
        );

        EventData threeHourData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_3H,
                entry + " #ThreeHourToggle.Value"
        );
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                entry + " #ThreeHourToggle",
                threeHourData,
                false
        );

        EventData sixHourData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_6H,
                entry + " #SixHourToggle.Value"
        );
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                entry + " #SixHourToggle",
                sixHourData,
                false
        );

        EventData nineHourData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_9H,
                entry + " #NineHourToggle.Value"
        );
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                entry + " #NineHourToggle",
                nineHourData,
                false
        );

        EventData twelveHourData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_12H,
                entry + " #TwelveHourToggle.Value"
        );
        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                entry + " #TwelveHourToggle",
                twelveHourData,
                false
        );

        return index + 1;
    }

    private int addForceBloodMoonEntry(
            UICommandBuilder commands,
            UIEventBuilder events,
            int index
    ) {
        String entry = BLOOD_MOON_LIST_ID + "[" + index + "]";
        commands.append(BLOOD_MOON_LIST_ID, ENTRY_FORCE_PATH);

        EventData forceData = EventData.of(
                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_FORCE,
                entry + " #ForceValue.Value"
        );
        events.addEventBinding(CustomUIEventBindingType.Activating, entry + " #ForceButton", forceData, false);

        return index + 1;
    }

    private boolean applyMultiplier(Float incoming, float currentValue, Consumer<Float> setter) {
        return applyFloatSetting(incoming, currentValue, MIN_MULTIPLIER, MAX_MULTIPLIER, STEP, setter);
    }

    private boolean applyFloatSetting(
            Float incoming,
            float currentValue,
            float min,
            float max,
            float step,
            Consumer<Float> setter
    ) {
        if (incoming == null) {
            return false;
        }

        float next = clamp(incoming, min, max, step);
        if (Math.abs(next - currentValue) <= EPSILON) {
            return false;
        }

        setter.accept(next);
        return true;
    }

    private boolean applyEnabled(Boolean incoming, boolean currentValue, Consumer<Boolean> setter) {
        if (incoming == null || incoming == currentValue) {
            return false;
        }

        setter.accept(incoming);
        return true;
    }

    private boolean applyIntSetting(
            Float incoming,
            int currentValue,
            int min,
            int max,
            float step,
            java.util.function.IntConsumer setter
    ) {
        if (incoming == null) {
            return false;
        }

        int next = Math.round(clamp(incoming, min, max, step));
        if (next == currentValue) {
            return false;
        }

        setter.accept(next);
        return true;
    }

    private DurationUpdate applyDurationSelection(
            Boolean oneHour,
            Boolean threeHour,
            Boolean sixHour,
            Boolean nineHour,
            Boolean twelveHour,
            HardcoreModeConfig config
    ) {
        int next = config.bloodMoonDurationHours;
        boolean changed = false;
        boolean refresh = false;

        if (Boolean.TRUE.equals(oneHour)) {
            next = BLOOD_MOON_DURATION_1H;
            refresh = true;
        } else if (Boolean.TRUE.equals(threeHour)) {
            next = BLOOD_MOON_DURATION_3H;
            refresh = true;
        } else if (Boolean.TRUE.equals(sixHour)) {
            next = BLOOD_MOON_DURATION_6H;
            refresh = true;
        } else if (Boolean.TRUE.equals(nineHour)) {
            next = BLOOD_MOON_DURATION_9H;
            refresh = true;
        } else if (Boolean.TRUE.equals(twelveHour)) {
            next = BLOOD_MOON_DURATION_12H;
            refresh = true;
        } else if (oneHour != null || threeHour != null || sixHour != null || nineHour != null || twelveHour != null) {
            refresh = true;
        } else {
            return new DurationUpdate(false, false);
        }

        if (next != config.bloodMoonDurationHours) {
            config.bloodMoonDurationHours = next;
            changed = true;
        }

        return new DurationUpdate(changed, refresh);
    }

    private static final class DurationUpdate {
        private final boolean changed;
        private final boolean refresh;

        private DurationUpdate(boolean changed, boolean refresh) {
            this.changed = changed;
            this.refresh = refresh;
        }
    }

    private float clamp(float value, float min, float max, float step) {
        return Math.max(min, Math.min(max, snapToStep(value, step)));
    }

    private float snapToStep(float value, float step) {
        return Math.round(value / step) * step;
    }

    private String formatMultiplier(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private String formatMultiplierText(float value) {
        return "x" + formatMultiplier(value);
    }

    private String formatDays(float value) {
        int days = Math.round(value);
        return days + (days == 1 ? " day" : " days");
    }

    private String formatHour(float value) {
        int hour = Math.round(value);
        return String.format(Locale.ROOT, "%02d:00", hour);
    }

    private String formatPercent(float value) {
        int percent = Math.round(value);
        return percent + "%";
    }
}
