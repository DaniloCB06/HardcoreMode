# HardcoreMode

## Overview

**HardcoreMode** is a comprehensive server-side mod that transforms the difficulty and customization of creatures in Hytale. It allows you to adjust the difficulty level of creatures separated into different categories, features a highly configurable **Blood Moon** event to meet any need, and includes integrations with other mods for an enhanced experience.

### Key Features

- **Advanced Creature Categorization**: Classify and configure creatures by categories (Hostile, Elite, Miniboss, Worldboss, Passive, Critter)
- **Highly Configurable Blood Moon Event**: Automatic scheduling, custom multipliers, duration control, and special drops
- **Per-World Configuration (>= 3.1.0)**: Different settings per world (Enemy Settings, Blood Moon, Player Settings) and the ability to disable mod effects in specific worlds
- **Custom Creature Management**: Add any creature to the mod and assign them to custom categories
- **Personalized Loot Tables**: Configure custom item drops for each category during Blood Moon
- **Mod Integrations**:
  - **RPGLeveling**: Link XP distribution during Blood Moon events
- **In-Game GUI**: Complete configuration interface with real-time changes
- **Player Death Penalties**: Configurable item loss and durability penalties
- **Multiplayer Optimized**: Thread-safe and fully compatible with multiplayer servers

---

## ⚠️ Update 3.2.0 (Summary) ⚠️

- **Per-world Blood Moon visuals**: red ambience + red moon (and sun) only appear in worlds where Blood Moon is active, with proper cleanup when switching worlds
- **Ambient music**: Trork camp music plays during Blood Moon (when available) *(not working for now...)*
- **Per-world defaults**: new worlds start with HardcoreMode disabled and now inherit the global `HardcoreMode.json` values as a template  
  *(If the file isn’t showing up, enable HardcoreMode in one of your existing worlds and the file will be created. Then just configure it however you prefer and restart the server/world.)*
- **GUI Blood Moon Drops / Mob Categories**: added Add/Remove pages with confirmation and fixed editing fields (quantity/chance as text with +/-)
- **Layout and UX**: spacing/size adjustments on category pages and world name highlight in section titles
- **HUD**: removed the old progress bar

---

## Video Preview

[![HardcoreMode Preview](https://img.youtube.com/vi/EcKTRy2Vubs/maxresdefault.jpg)](https://www.youtube.com/watch?v=EcKTRy2Vubs)

---

## ⚠️ Important Notice (>= 3.1.0)

If you already used the mod and want to update to versions **>= 3.1.0**, you must **delete the folder**:

- `com.example.HardcoreMode`

Then start the server and wait for it to generate the **new folder structure** required for per-world configurations.

Sorry for the inconvenience, but this change is required so per-world settings can work correctly.

### Tips before deleting `com.example.HardcoreMode`

- If you modified creature/category settings, **save the `.json` file** and **replace it** after the new structure is generated
- If you modified Blood Moon drops settings, **save the `.json` file** and **replace it** after the new structure is generated
- If you changed the default creature multipliers per category using the GUI, **take a screenshot** or save your values somewhere and re-apply them after the new structure is generated

---

## Dependencies

**Optional Integrations**:

- **RPGLeveling Mod**: Custom XP multipliers during Blood Moon events. Automatically detected.  
  (https://www.curseforge.com/hytale/mods/rpg-leveling-and-stats)

---

## Commands

### `/hardgui`

Opens the HardcoreMode configuration menu.

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

![Main Menu](https://media.forgecdn.net/attachments/description/1439115/description_c547c607-a05e-4982-b996-9fd9d2ac0cb0.png)

### ⚔️ Enemy Settings

Configure difficulty for each creature category:

> ✅ **Per-world (>= 3.1.0):** Enemy Settings are saved per world.

![Enemy Settings](https://media.forgecdn.net/attachments/description/1439115/description_f7cdbf99-dfa9-4928-97a0-ed560f9cd4ef.png)

**Available Categories**:

- **Passive**: Non-aggressive creatures
- **Critter**: Small ambient creatures
- **Hostile**: Common aggressive creatures
- **Elite**: Stronger variants with enhanced stats
- **Miniboss**: Mini-boss level creatures
- **Worldboss**: Raid-level boss creatures

**Per-Category Settings**:

- Enable/Disable category
- Health Multiplier (1.0x - 10.0x)
- Damage Multiplier (1.0x - 10.0x)

**Global Controls**:

- **Global Hardcore Mode**: Toggle all categories at once
- **Global Health/Damage**: Base multipliers applied to all categories

### 🌙 Blood Moon Settings

Configure the periodic Blood Moon event with advanced options:

> ✅ **Per-world (>= 3.1.0):** Blood Moon settings are saved per world (**drops remain global**).

![Blood Moon Settings](https://media.forgecdn.net/attachments/description/1439115/description_c5040825-96c1-40ba-929b-770565d6dc0b.png)

**Event Scheduling**:

- **Enable/Disable**: Turn automatic Blood Moon on/off
- **Interval**: Every X days (1-30 days)
- **Start Hour**: Time of day to begin (0-23)
- **Duration**: 1h / 3h / 6h / 9h / 12h

**Blood Moon Multipliers** (per category): each creature category has separate Blood Moon settings:

- **Hostile**: Health/Damage multipliers during Blood Moon
- **Elite**: Health/Damage multipliers during Blood Moon
- **Miniboss**: Health/Damage multipliers during Blood Moon
- **Worldboss**: Health/Damage multipliers during Blood Moon

![Blood Moon Multipliers](https://media.forgecdn.net/attachments/description/1439115/description_6c37d4dd-a164-4a25-a329-d233829ccad2.png)

**Special Features**:

- **XP Multiplier**: Bonus XP during Blood Moon (requires RPGLeveling mod)
- **Force Blood Moon**: Manually trigger the event immediately (cooldown decreases every hour)
- **Death Player Settings**: Increased penalties during Blood Moon
  - Item Durability Loss (%)
  - Inventory Drop Loss (%)

**Blood Moon Drops System**:

- **Custom Loot Tables**: Configure drops per category
- **Drop Management GUI**: Add/remove/enable/disable drops
- Access via the **Blood Moon Drops** button in General Settings

### 💀 Player Settings

Configure death penalties:

> ✅ **Per-world (>= 3.1.0):** Player Settings are saved per world.

![Player Settings](https://media.forgecdn.net/attachments/description/1439115/description_76781bf1-b95e-4e57-81a6-6224f07ad965.png)

- **Death Settings**: Enable/disable penalties
- **Item Durability Loss**: Percentage of durability lost on death (0-100%)
- **Inventory Drop Loss**: Percentage of items dropped on death (0-100%)

**Blood Moon Death Settings** (separate from normal death):

- Higher penalties during Blood Moon events (optional)
- Independent configuration

### ⚙️ General Settings

Advanced configuration options:

![General Settings](https://media.forgecdn.net/attachments/description/1439115/description_6325d635-5c48-4694-b35a-a7d934ab8bdc.png)

- **Mob Categories Manager**: View and configure creature classifications
- **Blood Moon Drops Manager**: Configure custom loot tables
- **World Settings**: Enable or disable HardcoreMode effects for specific worlds in the universe

---

## Creature Management System

### Mob Categories Page

Access via General Settings → **Mob Categories**

![Mob Categories](https://media.forgecdn.net/attachments/description/1439115/description_08ffe46a-621b-4b38-a5dd-872721f080c1.png)

**Features**:

- View all creatures registered in the mod
- Filter by category (All, Hostile, Elite, Miniboss, Worldboss, Passive, Critter)
- See current category assignment for each creature
- Pagination support for large lists
- Search and filter capabilities

### Custom Creature Classification

The mod uses a flexible system to classify creatures.

**File**: `HardcoreModeCategories.json`

- Located in the server config directory (in `com.example.HardcoreMode`)
- Categories: `PASSIVE`, `CRITTER`, `HOSTILE`, `ELITE`, `MINIBOSS`, `WORLDBOSS`, `NONE`  
  (**All mobs that don’t have a specific category are classified as `NONE`.**)

**Example**:

```json
{
  "entries": [
    { "category": "PASSIVE", "pattern": "Sheep" },
    { "category": "CRITTER", "pattern": "Rat" },
    { "category": "HOSTILE", "pattern": "Spider*" },
    { "category": "ELITE", "pattern": "Spawn_Void" },
    { "category": "MINIBOSS", "pattern": "Rex_Cave" },
    { "category": "WORLDBOSS", "pattern": "Dragon_*" }
  ]
}
```

**Fallback System**: If a creature is not in the file, it uses Hytale’s disposition system:

- Aggressive → `HOSTILE`, `ELITE`, `MINIBOSS`, and `WORLDBOSS`
- Passive → `PASSIVE`
- Neutral → `CRITTER`

**Adding New Mobs**:

You can only add new mobs to one of the six categories that already exist in the mod (`PASSIVE`, `CRITTER`, `HOSTILE`, `ELITE`, `MINIBOSS`, and `WORLDBOSS`) by editing the `HardcoreModeCategories.json` file located in the server’s default directory (`com.example.HardcoreMode`).

---

## Blood Moon Drops System

### Custom Loot Tables

Configure custom item drops for each category during Blood Moon events.

![Blood Moon Drops](https://media.forgecdn.net/attachments/description/1439115/description_b6c206e5-53d7-4968-a114-737506caa80d.png)

**Configuration File**: `HardcoreModeBloodMoonDrops.json`

- Located in the server config directory (in `com.example.HardcoreMode`)
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

- `category`: Creature category (`HOSTILE`, `ELITE`, `MINIBOSS`, `WORLDBOSS`)
- `enabled`: Toggle drop on/off
- `itemId`: Hytale item identifier
- `minQuantity`: Minimum items dropped
- `maxQuantity`: Maximum items dropped
- `dropChance`: Probability (0.0-100.0%)

### Blood Moon Drops GUI

Access via General Settings → **Blood Moon Drops**

**Features**:

- View all configured drops
- Enable/disable drops with checkbox
- Remove drops with button
- Add new drops with the **Add Drop** button
- Filter by category
- Pagination for large drop lists
- Reload configuration button

**Adding New Drops**:

Only by editing the `HardcoreModeBloodMoonDrops.json` file located in the server’s default directory (`com.example.HardcoreMode`) is it possible to add new drops for each category.

---

## Per-World Configuration (>= 3.1.0)

Starting in **v3.1.0**, you can have **different configurations for each world** inside the same universe.

This means you can run a setup **X** for **World X** while keeping a totally different setup **Y** for **World Y**. You can also **disable the mod effects per world**.

![Per-World Config](https://media.forgecdn.net/attachments/description/1439115/description_33e764ea-433f-4cb7-9b7f-04b14e4afa68.png)

### World-specific settings (per world)

These settings are now **saved per world**:

- **Enemy Settings**
- **Blood Moon**
- **Player Settings**
- **Enable/Disable HardcoreMode effects** per world

### 🌍 Still global (shared across all worlds)

These settings are still **global** (shared across all worlds):

- **Creature settings and categories** (classification and category rules)
- **Blood Moon drops configuration**
- **XP multiplier integration** (active when the **RPGLeveling** mod is detected) is **global** and affects **all worlds** =C

---

## Configuration Files

### HardcoreModeConfig.json (Per World)

**OBS**: Now it uses the world name and becomes: `world_x.json`, and inside it are the world-specific settings.

Main configuration file generated automatically by the server config system.

Starting in **v3.1.0**, the settings related to **Enemy Settings**, **Blood Moon**, and **Player Settings** are **saved per world**.

**Key Sections**:

- Per-category multipliers
- Blood Moon event configuration
- Player death penalties
- Per-world enable/disable options (when available)

### Global JSON Files (Shared)

These files remain **global** (shared across all worlds):

- `HardcoreModeCategories.json` (creatures/categories)
- `HardcoreModeBloodMoonDrops.json` (Blood Moon drops)

**Note**: You can manually edit values outside GUI slider ranges (e.g., >10x multiplier).

### HardcoreModeDisabledWorlds.txt

This file is located in the `config` folder, and it basically stores the worlds where HardcoreMode is disabled.

---

## Notes & Tips

- **GUI Slider Limits**: Sliders show 1x-10x, but JSON config accepts any value
- **Per-World Settings (>= 3.1.0)**: Enemy Settings, Blood Moon, and Player Settings are saved per world
- **Global Settings**: Creature categories, Blood Moon drops, and XP multiplier integration apply to all worlds
- **Blood Moon Override**: Blood Moon multipliers override normal settings during the event
- **Category Disable**: Disabling a category removes all buffs, even during Blood Moon (unless the category is enabled for Blood Moon)
- **Drop Testing**: Use Force Blood Moon to test drop configurations
- **Backup Configs**: Always back up configuration files before major changes
- **Creature IDs**: Use F3 or mod tools to find exact creature identifiers
- **Performance**: Limit drops per category to avoid lag with many creatures
- **Multiplayer**: All players see Blood Moon events and drops

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

**Enjoy!! =)**
