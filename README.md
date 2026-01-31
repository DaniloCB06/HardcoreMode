# HardcoreMode

## Overview
**HardcoreMode** is a comprehensive server-side mod that transforms the difficulty and customization of creatures in Hytale. It allows you to adjust the difficulty level of creatures separated by different categories, features a highly configurable **Blood Moon** event to meet any need, and includes integrations with other mods for an enhanced experience.

### Key Features
- **Advanced Creature Categorization**: Classify and configure creatures by categories (Hostile, Elite, Miniboss, Worldboss, Passive, Critter, Neutral)
- **Highly Configurable Blood Moon Event**: Automatic scheduling, custom multipliers, duration control, and special drops
- **Custom Creature Management**: Add any creature to the mod and assign them to custom categories
- **Personalized Loot Tables**: Configure custom item drops for each category during Blood Moon
- **Mod Integrations**: 
  - **RPGLeveling**: Link XP distribution during Blood Moon events
  - **TinyMessage API**: Enhanced visual chat messages
- **In-Game GUI**: Complete configuration interface with real-time changes
- **Player Death Penalties**: Configurable item loss and durability penalties
- **Multiplayer Optimized**: Thread-safe and fully compatible with multiplayer servers

---

## Dependencies

**Optional Integrations**:
- **TinyMessage API**: Enhanced red chat messages for Blood Moon announcements. Falls back to plain text if not present.
- **RPGLeveling Mod**: Custom XP multipliers during Blood Moon events. Automatically detected.

---

## Commands

### `/hardgui`
Opens the Hardcore Mode configuration menu.
- **Permission**: `hardgui` (admins/ops only)
- **Usage**: `/hardgui`
- Hidden from `/help` for players without permission
- Opens a complete GUI with multiple configuration pages


---

## GUI System

The mod features a complete in-game configuration system divided into multiple pages:

### 📋 Main Menu
Central hub to access all configuration sections:
- **Enemy Settings**: Configure creature categories and multipliers
- **Blood Moon**: Set up the Blood Moon event
- **Player Settings**: Death penalties and item loss
- **General Settings**: Advanced options and creature management

![image](https://media.forgecdn.net/attachments/description/1439115/description_c547c607-a05e-4982-b996-9fd9d2ac0cb0.png)

### ⚔️ Enemy Settings
Configure difficulty for each creature category:

![image](https://media.forgecdn.net/attachments/description/1439115/description_f7cdbf99-dfa9-4928-97a0-ed560f9cd4ef.png)


**Available Categories**:
- **Hostile**: Common aggressive creatures
- **Elite**: Stronger variants with enhanced stats
- **Miniboss**: Mini-boss level creatures
- **Worldboss**: Raid-level boss creatures
- **Passive**: Non-aggressive creatures
- **Critter**: Small ambient creatures
- **Neutral**: Creatures that react to player actions

**Per-Category Settings**:
- Enable/Disable category
- Health Multiplier (1.0x - 5.0x)
- Damage Multiplier (1.0x - 5.0x)

**Global Controls**:
- **Global Hardcore Mode**: Toggle all categories at once
- **Global Health/Damage**: Base multipliers applied to all categories

### 🌙 Blood Moon Settings

Configure the periodic Blood Moon event with advanced options:

![image](https://media.forgecdn.net/attachments/description/1439115/description_c5040825-96c1-40ba-929b-770565d6dc0b.png)

**Event Scheduling**:
- **Enable/Disable**: Turn automatic Blood Moon on/off
- **Interval**: Every X days (1-30 days)
- **Start Hour**: Time of day to begin (0-23)
- **Duration**: 1h / 3h / 6h / 9h / 12h

**Blood Moon Multipliers** (Per Category):
Each creature category has separate Blood Moon settings:
- **Hostile**: Health/Damage multipliers during Blood Moon
- **Elite**: Health/Damage multipliers during Blood Moon
- **Miniboss**: Health/Damage multipliers during Blood Moon
- **Worldboss**: Health/Damage multipliers during Blood Moon

![image](https://media.forgecdn.net/attachments/description/1439115/description_06afebd5-b58d-4ec4-9228-3eaff908b7ff.png)

**Special Features**:
- **XP Multiplier**: Bonus XP during Blood Moon (requires RPGLeveling mod)
- **Blood Moon HUD**: Real-time progress bar showing remaining time
- **Force Blood Moon**: Manually trigger event immediately (It decreases every hour)
- **Death Player Settings**: Increased penalties during Blood Moon
  - Item Durability Loss (%)
  - Inventory Drop Loss (%)

**Blood Moon Drops System**:
- **Custom Loot Tables**: Configure drops per category
- **Drop Management GUI**: Add/remove/enable/disable drops
- Access via "Blood Moon Drops" button in General Settings

### 💀 Player Settings

Configure death penalties:

![image](https://media.forgecdn.net/attachments/description/1439115/description_76781bf1-b95e-4e57-81a6-6224f07ad965.png)

- **Death Settings**: Enable/disable penalties
- **Item Durability Loss**: Percentage of durability lost on death (0-100%)
- **Inventory Drop Loss**: Percentage of items dropped on death (0-100%)

**Blood Moon Death Settings** (separate from normal death):
- Higher penalties during Blood Moon events (You can set it up if you want)
- Independent configuration

### ⚙️ General Settings

Advanced configuration options:

![image](https://media.forgecdn.net/attachments/description/1439115/description_a245a3c6-904d-464a-b5ea-14f768aba135.png)

- **Mob Categories Manager**: View and configure creature classifications
- **Blood Moon Drops Manager**: Configure custom loot tables

---

## Creature Management System

### Mob Categories Page
Access via General Settings → "Mob Categories"

![image](https://media.forgecdn.net/attachments/description/1439115/description_08ffe46a-621b-4b38-a5dd-872721f080c1.png)

**Features**:
- View all creatures registered in the mod
- Filter by category (All, Hostile, Elite, Miniboss, Worldboss, Passive, Critter)
- See current category assignment for each creature
- Pagination support for large lists
- Search and filter capabilities

### Custom Creature Classification
The mod uses a flexible system to classify creatures:

**File**: `HardcoreModeCategories.json`
- Located in server config directory (on: `com.example_HardcoreMode` )
- Categories: PASSIVE, CRITTER, HOSTILE, ELITE, MINIBOSS, WORLDBOSS, NONE ( **All mobs that don’t have a specific category are classified as NONE**)


**Example**:
```
{
  "entries": [
    {"category": "PASSIVE", "pattern": "Sheep"},
    {"category": "CRITTER", "pattern": "Rat"},
    {"category": "HOSTILE", "pattern": "Spider*"},
    {"category": "ELITE", "pattern": "Spawn_Void"},
    {"category": "MINIBOSS", "pattern": "Rex_Cave"},
    {"category": "WORLDBOSS", "pattern": "Dragon_*"}
  ]
}

```

**Fallback System**:
If a creature is not in the file, it uses Hytale's disposition system:
- Aggressive → HOSTILE, ELITE, MINIBOSS and WORLDBOSS
- Passive → PASSIVE
- Neutral → CRITTER

**Adding New Mobs**:

You can only add new mobs to one of the six categories that already exist in the mod (PASSIVE, CRITTER, HOSTILE, ELITE, MINIBOSS, and WORLDBOSS) by editing the `HardcoreModeCategories.json` file located in the server’s default directory (`com.example_HardcoreMode`).


---

## Blood Moon Drops System

### Custom Loot Tables
Configure custom item drops for each category during Blood Moon events.

![image](https://media.forgecdn.net/attachments/description/1439115/description_b6c206e5-53d7-4968-a114-737506caa80d.png)

**Configuration File**: `HardcoreModeBloodMoonDrops.json`
- Located in server config directory (on: `com.example_HardcoreMode` )
- JSON format with per-category drop entries

**Drop Entry Structure**:
```json
{
  "category": "HOSTILE",
  "enabled": true,
  "itemId": "Ingredient_Bar_Iron",
  "minQuantity": 1,
  "maxQuantity": 3,
  "dropChance": 50.0
}
```

**Properties**:
- `category`: Creature category (HOSTILE, ELITE, MINIBOSS, WORLDBOSS)
- `enabled`: Toggle drop on/off
- `itemId`: Hytale item identifier
- `minQuantity`: Minimum items dropped
- `maxQuantity`: Maximum items dropped
- `dropChance`: Probability (0.0-100.0%)

### Blood Moon Drops GUI
Access via General Settings → "Blood Moon Drops"

**Features**:
- View all configured drops
- Enable/disable drops with checkbox
- Remove drops with button
- Add new drops with "Add Drop" button
- Filter by category
- Pagination for large drop lists
- Reload configuration button

**Adding New Drops**:

Only by editing the `HardcoreModeBloodMoonDrops.json` file located in the server’s default directory (`com.example_HardcoreMode`)is it possible to add new drops for each category.

---

## Configuration Files

### HardcoreModeConfig.json
Main configuration file with all mod settings. Generated automatically by the server config system.

**Key Sections**:
- Global hardcore mode settings
- Per-category multipliers
- Blood Moon event configuration
- Player death penalties
- Legacy drop settings (kept for compatibility)

**Note**: You can manually edit values outside GUI slider ranges (e.g., 10x multiplier).

---

## Notes & Tips

- **GUI Slider Limits**: Sliders show 1x-10x, but JSON config accepts any value
- **Blood Moon Override**: Blood Moon multipliers override normal settings during event
- **Category Disable**: Disabling a category removes all buffs, even during Blood Moon (unless Blood Moon category is enabled)
- **Drop Testing**: Use Force Blood Moon to test drop configurations
- **Backup Configs**: Always backup configuration files before major changes
- **Creature IDs**: Use F3 or mod tools to find exact creature identifiers
- **Performance**: Limit drops per category to avoid lag with many creatures
- **Multiplayer**: All players see Blood Moon events, HUD, and drops

---

## Troubleshooting

**Blood Moon not starting**:
- Check if Blood Moon is enabled in settings
- Verify interval days is not 0
- Check server console for errors

**Drops not working**:
- Ensure Blood Moon Drops is enabled
- Verify item IDs are correct
- Check drop chance percentage
- Confirm category matches creature classification

**HUD not showing**:
- Enable Blood Moon HUD in General Settings
- Check if Blood Moon is actually active
- Verify player joined during event
- Other HUDs from other mods can overlap the Blood Moon HUD when they’re updated by ticks.


**Creatures not affected**:
- Check category enable status
- Verify creature classification in Mob Categories
- Review `HardcoreModeCategories.json` file
- Check multiplier values are not 1.0x

---

## Support & Development

For issues, suggestions, or contributions:

* Check configuration files for errors
* Review server console logs
* Test using the **Force Blood Moon** feature
* Verify mod compatibility

If you’re still experiencing an issue that isn’t resolved by the tips above or the information listed here, please join our Discord server (https://discord.gg/pkePSjgrvj) and open a ticket in (`#report-problem`). Include the steps you took to reproduce the error and attach the world/server logs from where it happened.

---

**Enjoy!!   =)**