package com.example.plugin.ui;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.MobCategory;
import com.example.plugin.config.HardcoreModeConfig;
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
import java.util.function.Consumer;

public class HardcoreSettingsPage extends InteractiveCustomUIPage<HardcoreSettingsPageEventData> {
        private static final String PAGE_PATH = "Pages/HardcoreSettingsSectionPage.ui";
        private static final String PAGE_PATH_ENEMY = "Pages/HardcoreSettingsSectionEnemyPage.ui";
        private static final String ENTRY_TOGGLE_PATH = "Pages/HardcoreToggleRow.ui";
        private static final String ENTRY_CATEGORY_TOGGLE_PATH = "Pages/HardcoreCategoryToggleRow.ui";
        private static final String ENTRY_SLIDER_PATH = "Pages/HardcoreSliderRow.ui";
        private static final String ENTRY_TWO_COLUMN_PATH = "Pages/HardcoreTwoColumnRow.ui";
        private static final String ENTRY_SPACER_PATH = "Pages/HardcoreSpacerRow.ui";
        private static final String ENTRY_DURATION_PATH = "Pages/HardcoreBloodMoonDurationRow.ui";
        private static final String ENTRY_FORCE_PATH = "Pages/HardcoreBloodMoonForceRow.ui";
        private static final String ENTRY_HEADER_PATH = "Pages/HardcoreHeaderRow.ui";
        private static final String SETTINGS_LIST_ID = "#SettingsList";
        private static final String BACK_BUTTON_PATH = "#BackContainer #BackButton";
        private static final String BACK_VALUE_PATH = "#BackContainer #BackValue.Value";

        private static final String PAGE_TITLE_ID = "#PageTitle.Text";
        private static final String SECTION_TITLE_ID = "#SectionTitle.Text";

        public enum SettingsSection {
                ENEMY("Enemy Settings"),
                BLOOD_MOON("Blood Moon"),
                PLAYER("Player Settings"),
                ALL("Hardcore Settings");

                private final String displayName;

                SettingsSection(String displayName) {
                        this.displayName = displayName;
                }

                public String getDisplayName() {
                        return displayName;
                }
        }

        private static final float MIN_MULTIPLIER = 1.0f;
        private static final float MAX_MULTIPLIER = 10.0f;
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
        private final SettingsSection section;
        private final PlayerRef playerRef;

        public HardcoreSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef) {
                this(plugin, playerRef, SettingsSection.ALL);
        }

        public HardcoreSettingsPage(HardcoreModePlugin plugin, PlayerRef playerRef, SettingsSection section) {
                super(playerRef, CustomPageLifetime.CanDismiss, HardcoreSettingsPageEventData.CODEC);
                this.plugin = plugin;
                this.section = section == null ? SettingsSection.ALL : section;
                this.playerRef = playerRef;
        }

        @Override
        public void build(
                        Ref<EntityStore> ref,
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        Store<EntityStore> store) {
                commands.append(section == SettingsSection.ENEMY ? PAGE_PATH_ENEMY : PAGE_PATH);
                fillHeader(commands);
                buildSettingsList(commands, events);
                bindNavigation(events);
        }

        @Override
        public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store,
                        HardcoreSettingsPageEventData data) {
                if (data == null) {
                        return;
                }

                Boolean enabled = data.getEnabled();
                Float globalHealthMultiplier = data.getGlobalHealthMultiplier();
                Float globalDamageMultiplier = data.getGlobalDamageMultiplier();
                Boolean passiveEnabled = data.getPassiveEnabled();
                Float passiveHealthMultiplier = data.getPassiveHealthMultiplier();
                Float passiveDamageMultiplier = data.getPassiveDamageMultiplier();
                Boolean critterEnabled = data.getCritterEnabled();
                Float critterHealthMultiplier = data.getCritterHealthMultiplier();
                Float critterDamageMultiplier = data.getCritterDamageMultiplier();
                Boolean hostileEnabled = data.getHostileEnabled();
                Float hostileHealthMultiplier = data.getHostileHealthMultiplier();
                Float hostileDamageMultiplier = data.getHostileDamageMultiplier();
                Boolean eliteEnabled = data.getEliteEnabled();
                Float eliteHealthMultiplier = data.getEliteHealthMultiplier();
                Float eliteDamageMultiplier = data.getEliteDamageMultiplier();
                Boolean minibossEnabled = data.getMinibossEnabled();
                Float minibossHealthMultiplier = data.getMinibossHealthMultiplier();
                Float minibossDamageMultiplier = data.getMinibossDamageMultiplier();
                Boolean worldbossEnabled = data.getWorldbossEnabled();
                Float worldbossHealthMultiplier = data.getWorldbossHealthMultiplier();
                Float worldbossDamageMultiplier = data.getWorldbossDamageMultiplier();
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
                Float bloodMoonXpMultiplier = data.getBloodMoonXpMultiplier();
                Boolean bloodMoonForce = data.getBloodMoonForce();
                Boolean playerDeathSettingsEnabled = data.getPlayerDeathSettingsEnabled();
                Float playerItemDurabilityLossPercent = data.getPlayerItemDurabilityLossPercent();
                Float playerItemDropPercent = data.getPlayerItemDropPercent();
                Boolean goBack = data.getGoBack();

                if (Boolean.TRUE.equals(goBack)) {
                        openMainMenu(ref, store);
                        return;
                }
                if (enabled == null
                                && globalHealthMultiplier == null
                                && globalDamageMultiplier == null
                                && passiveEnabled == null
                                && passiveHealthMultiplier == null
                                && passiveDamageMultiplier == null
                                && critterEnabled == null
                                && critterHealthMultiplier == null
                                && critterDamageMultiplier == null
                                && hostileEnabled == null
                                && hostileHealthMultiplier == null
                                && hostileDamageMultiplier == null
                                && eliteEnabled == null
                                && eliteHealthMultiplier == null
                                && eliteDamageMultiplier == null
                                && minibossEnabled == null
                                && minibossHealthMultiplier == null
                                && minibossDamageMultiplier == null
                                && worldbossEnabled == null
                                && worldbossHealthMultiplier == null
                                && worldbossDamageMultiplier == null
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
                                && bloodMoonXpMultiplier == null
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
                        config.hostileEnabled = enabled;
                        config.passiveEnabled = enabled;
                        config.critterEnabled = enabled;
                        config.eliteEnabled = enabled;
                        config.minibossEnabled = enabled;
                        config.worldbossEnabled = enabled;
                        changed = true;
                }

                changed |= applyMultiplier(
                                globalHealthMultiplier,
                                config.healthMultiplier,
                                value -> {
                                        config.healthMultiplier = value;
                                        config.hostileHealthMultiplier = value;
                                        config.passiveHealthMultiplier = value;
                                        config.critterHealthMultiplier = value;
                                        config.eliteHealthMultiplier = value;
                                        config.minibossHealthMultiplier = value;
                                        config.worldbossHealthMultiplier = value;
                                });
                changed |= applyMultiplier(
                                globalDamageMultiplier,
                                config.damageMultiplier,
                                value -> {
                                        config.damageMultiplier = value;
                                        config.hostileDamageMultiplier = value;
                                        config.passiveDamageMultiplier = value;
                                        config.critterDamageMultiplier = value;
                                        config.eliteDamageMultiplier = value;
                                        config.minibossDamageMultiplier = value;
                                        config.worldbossDamageMultiplier = value;
                                });
                changed |= applyEnabled(
                                passiveEnabled,
                                config.passiveEnabled,
                                value -> config.passiveEnabled = value);
                changed |= applyMultiplier(
                                passiveHealthMultiplier,
                                config.passiveHealthMultiplier,
                                value -> config.passiveHealthMultiplier = value);
                changed |= applyMultiplier(
                                passiveDamageMultiplier,
                                config.passiveDamageMultiplier,
                                value -> config.passiveDamageMultiplier = value);
                changed |= applyEnabled(
                                critterEnabled,
                                config.critterEnabled,
                                value -> config.critterEnabled = value);
                changed |= applyMultiplier(
                                critterHealthMultiplier,
                                config.critterHealthMultiplier,
                                value -> config.critterHealthMultiplier = value);
                changed |= applyMultiplier(
                                critterDamageMultiplier,
                                config.critterDamageMultiplier,
                                value -> config.critterDamageMultiplier = value);
                changed |= applyEnabled(
                                hostileEnabled,
                                config.hostileEnabled,
                                value -> config.hostileEnabled = value);
                changed |= applyMultiplier(
                                hostileHealthMultiplier,
                                config.hostileHealthMultiplier,
                                value -> config.hostileHealthMultiplier = value);
                changed |= applyMultiplier(
                                hostileDamageMultiplier,
                                config.hostileDamageMultiplier,
                                value -> config.hostileDamageMultiplier = value);
                changed |= applyEnabled(
                                eliteEnabled,
                                config.eliteEnabled,
                                value -> config.eliteEnabled = value);
                changed |= applyMultiplier(
                                eliteHealthMultiplier,
                                config.eliteHealthMultiplier,
                                value -> config.eliteHealthMultiplier = value);
                changed |= applyMultiplier(
                                eliteDamageMultiplier,
                                config.eliteDamageMultiplier,
                                value -> config.eliteDamageMultiplier = value);
                changed |= applyEnabled(
                                minibossEnabled,
                                config.minibossEnabled,
                                value -> config.minibossEnabled = value);
                changed |= applyMultiplier(
                                minibossHealthMultiplier,
                                config.minibossHealthMultiplier,
                                value -> config.minibossHealthMultiplier = value);
                changed |= applyMultiplier(
                                minibossDamageMultiplier,
                                config.minibossDamageMultiplier,
                                value -> config.minibossDamageMultiplier = value);
                changed |= applyEnabled(
                                worldbossEnabled,
                                config.worldbossEnabled,
                                value -> config.worldbossEnabled = value);
                changed |= applyMultiplier(
                                worldbossHealthMultiplier,
                                config.worldbossHealthMultiplier,
                                value -> config.worldbossHealthMultiplier = value);
                changed |= applyMultiplier(
                                worldbossDamageMultiplier,
                                config.worldbossDamageMultiplier,
                                value -> config.worldbossDamageMultiplier = value);
                changed |= applyEnabled(
                                bloodMoonEnabled,
                                config.bloodMoonEnabled,
                                value -> config.bloodMoonEnabled = value);
                changed |= applyIntSetting(
                                bloodMoonIntervalDays,
                                config.bloodMoonIntervalDays,
                                MIN_BLOOD_MOON_DAYS,
                                MAX_BLOOD_MOON_DAYS,
                                BLOOD_MOON_DAY_STEP,
                                value -> config.bloodMoonIntervalDays = value);
                changed |= applyIntSetting(
                                bloodMoonStartHour,
                                config.bloodMoonStartHour,
                                MIN_BLOOD_MOON_HOUR,
                                MAX_BLOOD_MOON_HOUR,
                                BLOOD_MOON_HOUR_STEP,
                                value -> config.bloodMoonStartHour = value);
                DurationUpdate durationUpdate = applyDurationSelection(
                                bloodMoonDuration1h,
                                bloodMoonDuration3h,
                                bloodMoonDuration6h,
                                bloodMoonDuration9h,
                                bloodMoonDuration12h,
                                config);
                changed |= durationUpdate.changed;
                refresh |= durationUpdate.refresh;
                changed |= applyFloatSetting(
                                bloodMoonHostileHealthMultiplier,
                                config.bloodMoonHostileHealthMultiplier,
                                MIN_MULTIPLIER,
                                MAX_MULTIPLIER,
                                STEP,
                                value -> config.bloodMoonHostileHealthMultiplier = value);
                changed |= applyFloatSetting(
                                bloodMoonHostileDamageMultiplier,
                                config.bloodMoonHostileDamageMultiplier,
                                MIN_MULTIPLIER,
                                MAX_MULTIPLIER,
                                STEP,
                                value -> config.bloodMoonHostileDamageMultiplier = value);
                changed |= applyFloatSetting(
                                bloodMoonXpMultiplier,
                                config.bloodMoonXpMultiplier,
                                MIN_MULTIPLIER,
                                MAX_MULTIPLIER,
                                STEP,
                                value -> config.bloodMoonXpMultiplier = value);
                changed |= applyEnabled(
                                playerDeathSettingsEnabled,
                                config.playerDeathSettingsEnabled,
                                value -> config.playerDeathSettingsEnabled = value);
                changed |= applyIntSetting(
                                playerItemDurabilityLossPercent,
                                config.playerItemDurabilityLossPercent,
                                MIN_PERCENT,
                                MAX_PERCENT,
                                PERCENT_STEP,
                                value -> config.playerItemDurabilityLossPercent = value);
                changed |= applyIntSetting(
                                playerItemDropPercent,
                                config.playerItemDropPercent,
                                MIN_PERCENT,
                                MAX_PERCENT,
                                PERCENT_STEP,
                                value -> config.playerItemDropPercent = value);

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
                bindNavigation(updateEvents);
                sendUpdate(updateCommands, updateEvents, false);
        }

        private void fillHeader(UICommandBuilder commands) {
                commands.set(PAGE_TITLE_ID, "Hardcore Mode");
                commands.set(SECTION_TITLE_ID, section.getDisplayName());
        }

        private void buildSettingsList(UICommandBuilder commands, UIEventBuilder events) {
                HardcoreModeConfig config = plugin.getConfigData();
                commands.clear(SETTINGS_LIST_ID);

                int index = 0;
                switch (section) {
                        case ENEMY:
                                buildEnemySettings(commands, events, config, index);
                                break;
                        case BLOOD_MOON:
                                buildBloodMoonSettings(commands, events, config, index);
                                break;
                        case PLAYER:
                                buildPlayerSettings(commands, events, config, index);
                                break;
                        case ALL:
                        default:
                                index = buildEnemySettings(commands, events, config, index);
                                index = addHeaderEntry(commands, SETTINGS_LIST_ID, index, "Blood Moon");
                                index = buildBloodMoonSettings(commands, events, config, index);
                                index = addHeaderEntry(commands, SETTINGS_LIST_ID, index, "Player Settings");
                                buildPlayerSettings(commands, events, config, index);
                                break;
                }
        }

        private void bindNavigation(UIEventBuilder events) {
                EventData backData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_GO_BACK,
                                BACK_VALUE_PATH);
                events.addEventBinding(CustomUIEventBindingType.Activating, BACK_BUTTON_PATH, backData, false);
        }

        private int buildEnemySettings(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        HardcoreModeConfig config,
                        int index) {
                float passiveHealth = plugin.getHealthMultiplier(MobCategory.PASSIVE);
                float passiveDamage = plugin.getDamageMultiplier(MobCategory.PASSIVE);
                float critterHealth = plugin.getHealthMultiplier(MobCategory.CRITTER);
                float critterDamage = plugin.getDamageMultiplier(MobCategory.CRITTER);
                float hostileHealth = plugin.getHealthMultiplier(MobCategory.HOSTILE);
                float hostileDamage = plugin.getDamageMultiplier(MobCategory.HOSTILE);
                float eliteHealth = plugin.getHealthMultiplier(MobCategory.ELITE);
                float eliteDamage = plugin.getDamageMultiplier(MobCategory.ELITE);
                float minibossHealth = plugin.getHealthMultiplier(MobCategory.MINIBOSS);
                float minibossDamage = plugin.getDamageMultiplier(MobCategory.MINIBOSS);
                float worldbossHealth = plugin.getHealthMultiplier(MobCategory.WORLDBOSS);
                float worldbossDamage = plugin.getDamageMultiplier(MobCategory.WORLDBOSS);

                index = addToggleEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Global Enemy Settings: " + (config.enabled ? "ON" : "OFF"),
                                config.enabled);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Global Health",
                                config.healthMultiplier,
                                HardcoreSettingsPageEventData.KEY_GLOBAL_HEALTH);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Global Damage",
                                config.damageMultiplier,
                                HardcoreSettingsPageEventData.KEY_GLOBAL_DAMAGE);
                // Two-column grid: left = Passive, Critter, Hostile; right = Elite, Miniboss,
                // Worldboss.
                String gridEntry = SETTINGS_LIST_ID + "[" + index + "]";
                commands.append(SETTINGS_LIST_ID, ENTRY_TWO_COLUMN_PATH);

                String leftList = gridEntry + " #LeftColumn";
                String rightList = gridEntry + " #RightColumn";
                int leftIndex = 0;
                int rightIndex = 0;

                leftIndex = addCategorySection(commands, events, leftList, leftIndex,
                                "Passive Creatures: " + (config.passiveEnabled ? "ON" : "OFF"),
                                config.passiveEnabled,
                                HardcoreSettingsPageEventData.KEY_PASSIVE_ENABLED,
                                passiveHealth,
                                HardcoreSettingsPageEventData.KEY_PASSIVE_HEALTH,
                                passiveDamage,
                                HardcoreSettingsPageEventData.KEY_PASSIVE_DAMAGE);

                leftIndex = addCategorySection(commands, events, leftList, leftIndex,
                                "Critter Creatures: " + (config.critterEnabled ? "ON" : "OFF"),
                                config.critterEnabled,
                                HardcoreSettingsPageEventData.KEY_CRITTER_ENABLED,
                                critterHealth,
                                HardcoreSettingsPageEventData.KEY_CRITTER_HEALTH,
                                critterDamage,
                                HardcoreSettingsPageEventData.KEY_CRITTER_DAMAGE);

                leftIndex = addCategorySection(commands, events, leftList, leftIndex,
                                "Hostile Creatures: " + (config.hostileEnabled ? "ON" : "OFF"),
                                config.hostileEnabled,
                                HardcoreSettingsPageEventData.KEY_HOSTILE_ENABLED,
                                hostileHealth,
                                HardcoreSettingsPageEventData.KEY_HOSTILE_HEALTH,
                                hostileDamage,
                                HardcoreSettingsPageEventData.KEY_HOSTILE_DAMAGE);

                rightIndex = addCategorySection(commands, events, rightList, rightIndex,
                                "Elite Creatures: " + (config.eliteEnabled ? "ON" : "OFF"),
                                config.eliteEnabled,
                                HardcoreSettingsPageEventData.KEY_ELITE_ENABLED,
                                eliteHealth,
                                HardcoreSettingsPageEventData.KEY_ELITE_HEALTH,
                                eliteDamage,
                                HardcoreSettingsPageEventData.KEY_ELITE_DAMAGE);

                rightIndex = addCategorySection(commands, events, rightList, rightIndex,
                                "Minibosses: " + (config.minibossEnabled ? "ON" : "OFF"),
                                config.minibossEnabled,
                                HardcoreSettingsPageEventData.KEY_MINIBOSS_ENABLED,
                                minibossHealth,
                                HardcoreSettingsPageEventData.KEY_MINIBOSS_HEALTH,
                                minibossDamage,
                                HardcoreSettingsPageEventData.KEY_MINIBOSS_DAMAGE);

                addCategorySection(commands, events, rightList, rightIndex,
                                "World Bosses: " + (config.worldbossEnabled ? "ON" : "OFF"),
                                config.worldbossEnabled,
                                HardcoreSettingsPageEventData.KEY_WORLDBOSS_ENABLED,
                                worldbossHealth,
                                HardcoreSettingsPageEventData.KEY_WORLDBOSS_HEALTH,
                                worldbossDamage,
                                HardcoreSettingsPageEventData.KEY_WORLDBOSS_DAMAGE);

                return index + 1;
        }

        private int buildBloodMoonSettings(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        HardcoreModeConfig config,
                        int index) {
                index = addCategoryToggleEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Blood Moon: " + (config.bloodMoonEnabled ? "ON" : "OFF"),
                                config.bloodMoonEnabled,
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_ENABLED);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Every X Days",
                                config.bloodMoonIntervalDays,
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_INTERVAL_DAYS,
                                MIN_BLOOD_MOON_DAYS,
                                MAX_BLOOD_MOON_DAYS,
                                BLOOD_MOON_DAY_STEP,
                                this::formatDays);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Start Hour",
                                config.bloodMoonStartHour,
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_START_HOUR,
                                MIN_BLOOD_MOON_HOUR,
                                MAX_BLOOD_MOON_HOUR,
                                BLOOD_MOON_HOUR_STEP,
                                this::formatHour);
                index = addBloodMoonDurationEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                config.bloodMoonDurationHours);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Hostile Health",
                                config.bloodMoonHostileHealthMultiplier,
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_HOSTILE_HEALTH);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Hostile Damage",
                                config.bloodMoonHostileDamageMultiplier,
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_HOSTILE_DAMAGE);
                if (plugin.isRpgLevelingAvailable()) {
                        index = addSliderEntry(
                                        commands,
                                        events,
                                        SETTINGS_LIST_ID,
                                        index,
                                        "RPG XP Multiplier",
                                        config.bloodMoonXpMultiplier,
                                        HardcoreSettingsPageEventData.KEY_BLOOD_MOON_XP_MULTIPLIER);
                }
                return addForceBloodMoonEntry(commands, events, SETTINGS_LIST_ID, index);
        }

        private int buildPlayerSettings(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        HardcoreModeConfig config,
                        int index) {
                index = addCategoryToggleEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Death Settings: " + (config.playerDeathSettingsEnabled ? "ON" : "OFF"),
                                config.playerDeathSettingsEnabled,
                                HardcoreSettingsPageEventData.KEY_PLAYER_DEATH_SETTINGS_ENABLED);
                index = addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Item Durability Loss",
                                config.playerItemDurabilityLossPercent,
                                HardcoreSettingsPageEventData.KEY_PLAYER_ITEM_DURABILITY_LOSS_PERCENT,
                                MIN_PERCENT,
                                MAX_PERCENT,
                                PERCENT_STEP,
                                this::formatPercent);
                return addSliderEntry(
                                commands,
                                events,
                                SETTINGS_LIST_ID,
                                index,
                                "Inventory Drop Loss",
                                config.playerItemDropPercent,
                                HardcoreSettingsPageEventData.KEY_PLAYER_ITEM_DROP_PERCENT,
                                MIN_PERCENT,
                                MAX_PERCENT,
                                PERCENT_STEP,
                                this::formatPercent);
        }

        private void openMainMenu(Ref<EntityStore> ref, Store<EntityStore> store) {
                if (store == null || ref == null) {
                        return;
                }

                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) {
                        return;
                }

                player.getPageManager().openCustomPage(ref, store, new HardcoreMainMenuPage(plugin, playerRef));
        }

        private int addToggleEntry(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        String listId,
                        int index,
                        String label,
                        boolean checkboxValue) {
                String entry = listId + "[" + index + "]";
                commands.append(listId, ENTRY_TOGGLE_PATH);
                commands.set(entry + " #Label.Text", label);
                commands.set(entry + " #Toggle.Value", checkboxValue);

                EventData toggleData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_ENABLED,
                                entry + " #Toggle.Value");
                events.addEventBinding(CustomUIEventBindingType.ValueChanged, entry + " #Toggle", toggleData, false);

                return index + 1;
        }

        private int addHeaderEntry(
                        UICommandBuilder commands,
                        String listId,
                        int index,
                        String label) {
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
                        String key) {
                String entry = listId + "[" + index + "]";
                commands.append(listId, ENTRY_CATEGORY_TOGGLE_PATH);
                commands.set(entry + " #Label.Text", label);
                commands.set(entry + " #Toggle.Value", checkboxValue);

                EventData toggleData = EventData.of(
                                key,
                                entry + " #Toggle.Value");
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
                        String key) {
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
                                this::formatMultiplierText);
        }

        private int addCategorySection(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        String listId,
                        int index,
                        String label,
                        boolean enabled,
                        String enabledKey,
                        float healthValue,
                        String healthKey,
                        float damageValue,
                        String damageKey) {
                index = addCategoryToggleEntry(commands, events, listId, index, label, enabled, enabledKey);
                index = addSliderEntry(commands, events, listId, index, "Mob Health", healthValue, healthKey);
                index = addSliderEntry(commands, events, listId, index, "Mob Damage", damageValue, damageKey);
                // Spacer to visually separate categories inside the column
                String spacerEntry = listId + "[" + index + "]";
                commands.append(listId, ENTRY_SPACER_PATH);
                return index + 1;
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
                        java.util.function.Function<Float, String> formatter) {
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
                        String listId,
                        int index,
                        int selectedHours) {
                String entry = listId + "[" + index + "]";
                commands.append(listId, ENTRY_DURATION_PATH);
                commands.set(entry + " #OneHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_1H);
                commands.set(entry + " #ThreeHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_3H);
                commands.set(entry + " #SixHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_6H);
                commands.set(entry + " #NineHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_9H);
                commands.set(entry + " #TwelveHourToggle.Value", selectedHours == BLOOD_MOON_DURATION_12H);

                EventData oneHourData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_1H,
                                entry + " #OneHourToggle.Value");
                events.addEventBinding(
                                CustomUIEventBindingType.ValueChanged,
                                entry + " #OneHourToggle",
                                oneHourData,
                                false);

                EventData threeHourData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_3H,
                                entry + " #ThreeHourToggle.Value");
                events.addEventBinding(
                                CustomUIEventBindingType.ValueChanged,
                                entry + " #ThreeHourToggle",
                                threeHourData,
                                false);

                EventData sixHourData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_6H,
                                entry + " #SixHourToggle.Value");
                events.addEventBinding(
                                CustomUIEventBindingType.ValueChanged,
                                entry + " #SixHourToggle",
                                sixHourData,
                                false);

                EventData nineHourData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_9H,
                                entry + " #NineHourToggle.Value");
                events.addEventBinding(
                                CustomUIEventBindingType.ValueChanged,
                                entry + " #NineHourToggle",
                                nineHourData,
                                false);

                EventData twelveHourData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_DURATION_12H,
                                entry + " #TwelveHourToggle.Value");
                events.addEventBinding(
                                CustomUIEventBindingType.ValueChanged,
                                entry + " #TwelveHourToggle",
                                twelveHourData,
                                false);

                return index + 1;
        }

        private int addForceBloodMoonEntry(
                        UICommandBuilder commands,
                        UIEventBuilder events,
                        String listId,
                        int index) {
                String entry = listId + "[" + index + "]";
                commands.append(listId, ENTRY_FORCE_PATH);

                EventData forceData = EventData.of(
                                HardcoreSettingsPageEventData.KEY_BLOOD_MOON_FORCE,
                                entry + " #ForceValue.Value");
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
                        Consumer<Float> setter) {
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
                        java.util.function.IntConsumer setter) {
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
                        HardcoreModeConfig config) {
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
                } else if (oneHour != null || threeHour != null || sixHour != null || nineHour != null
                                || twelveHour != null) {
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
