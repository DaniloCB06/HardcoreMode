package com.example.plugin.config;

import com.example.plugin.MobCategory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodMoonDropConfig {
    private static final String JSON_FILE = "HardcoreModeBloodMoonDrops.json";
    private static final Random RANDOM = new Random();

    private static final Pattern DROP_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*," +
            "\\s*\"enabled\"\\s*:\\s*(true|false)\\s*," +
            "\\s*\"itemId\"\\s*:\\s*\"([^\"]+)\"\\s*," +
            "\\s*\"minQuantity\"\\s*:\\s*(\\d+)\\s*," +
            "\\s*\"maxQuantity\"\\s*:\\s*(\\d+)\\s*," +
            "\\s*\"dropChance\"\\s*:\\s*([\\d.]+)\\s*\\}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private final Map<MobCategory, List<DropEntry>> dropEntries = new EnumMap<>(MobCategory.class);
    private final Path dataDirectory;
    private final Path jsonPath;

    public BloodMoonDropConfig() {
        this(null);
    }

    public BloodMoonDropConfig(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.jsonPath = resolveJsonPath();
        ensureDefaultConfigExists();
        loadDropConfig();
    }

    public void reload() {
        dropEntries.clear();
        loadDropConfig();
    }

    public List<DropEntry> getDropEntries(MobCategory category) {
        return dropEntries.getOrDefault(category, List.of());
    }

    @Deprecated
    public DropEntry getDropEntry(MobCategory category) {
        List<DropEntry> entries = dropEntries.get(category);
        return (entries != null && !entries.isEmpty()) ? entries.get(0) : null;
    }

    public boolean isDropEnabled(MobCategory category) {
        List<DropEntry> entries = dropEntries.get(category);
        if (entries == null) return false;
        return entries.stream().anyMatch(e -> e.enabled);
    }

    @Deprecated
    public String getDropItem(MobCategory category) {
        DropEntry entry = getDropEntry(category);
        return entry != null ? entry.itemId : null;
    }

    @Deprecated
    public int getDropQuantity(MobCategory category) {
        DropEntry entry = getDropEntry(category);
        return entry != null ? entry.getRandomQuantity() : 0;
    }

    @Deprecated
    public float getDropChance(MobCategory category) {
        DropEntry entry = getDropEntry(category);
        return entry != null ? entry.dropChance : 0.0f;
    }

    private void loadDropConfig() {
        String jsonContent = loadJsonContent();
        if (jsonContent == null || jsonContent.isEmpty()) {
            return;
        }

        Matcher matcher = DROP_ENTRY_PATTERN.matcher(jsonContent);
        while (matcher.find()) {
            String categoryKey = matcher.group(1);
            boolean enabled = Boolean.parseBoolean(matcher.group(2));
            String itemId = matcher.group(3);
            int minQuantity = Integer.parseInt(matcher.group(4));
            int maxQuantity = Integer.parseInt(matcher.group(5));
            float dropChance = Float.parseFloat(matcher.group(6));

            MobCategory category = MobCategory.fromFileKey(categoryKey);
            if (category == MobCategory.NONE) {
                continue;
            }

            dropEntries.computeIfAbsent(category, k -> new ArrayList<>())
                       .add(new DropEntry(enabled, itemId, minQuantity, maxQuantity, dropChance));
        }
    }

    private String loadJsonContent() {
        Path external = dataDirectory != null ? dataDirectory.resolve(JSON_FILE) : Path.of(JSON_FILE);
        if (Files.isRegularFile(external)) {
            try {
                return Files.readString(external, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
            }
        }

        try (InputStream stream = BloodMoonDropConfig.class.getClassLoader()
                .getResourceAsStream(JSON_FILE)) {
            if (stream != null) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        return null;
    }

    private Path resolveJsonPath() {
        if (dataDirectory != null) {
            return dataDirectory.resolve(JSON_FILE);
        }
        return Path.of(JSON_FILE);
    }

    private void ensureDefaultConfigExists() {
        if (jsonPath == null || Files.isRegularFile(jsonPath)) {
            return;
        }

        try {
            Path parent = jsonPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.writeString(jsonPath, getDefaultJsonContent(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private String getDefaultJsonContent() {
        return "{\n" +
               "  \"description\": \"Blood Moon drop configuration. Each category can have multiple items.\",\n" +
               "  \"note\": \"minQuantity and maxQuantity define the quantity range. Drop amount is randomized between these values.\",\n" +
               "  \"drops\": [\n" +
               "    {\n" +
               "      \"category\": \"HOSTILE\",\n" +
               "      \"enabled\": true,\n" +
               "      \"itemId\": \"Ingredient_Bar_Iron\",\n" +
               "      \"minQuantity\": 1,\n" +
               "      \"maxQuantity\": 2,\n" +
               "      \"dropChance\": 50.0\n" +
               "    },\n" +
               "    {\n" +
               "      \"category\": \"HOSTILE\",\n" +
               "      \"enabled\": true,\n" +
               "      \"itemId\": \"Ingredient_Void_Essence\",\n" +
               "      \"minQuantity\": 1,\n" +
               "      \"maxQuantity\": 1,\n" +
               "      \"dropChance\": 25.0\n" +
               "    },\n" +
               "    {\n" +
               "      \"category\": \"ELITE\",\n" +
               "      \"enabled\": true,\n" +
               "      \"itemId\": \"Ingredient_Bar_Thorium\",\n" +
               "      \"minQuantity\": 2,\n" +
               "      \"maxQuantity\": 3,\n" +
               "      \"dropChance\": 60.0\n" +
               "    },\n" +
               "    {\n" +
               "      \"category\": \"MINIBOSS\",\n" +
               "      \"enabled\": true,\n" +
               "      \"itemId\": \"Ingredient_Bar_Adamantite\",\n" +
               "      \"minQuantity\": 3,\n" +
               "      \"maxQuantity\": 5,\n" +
               "      \"dropChance\": 75.0\n" +
               "    },\n" +
               "    {\n" +
               "      \"category\": \"WORLDBOSS\",\n" +
               "      \"enabled\": true,\n" +
               "      \"itemId\": \"Ingredient_Bar_Mithril\",\n" +
               "      \"minQuantity\": 5,\n" +
               "      \"maxQuantity\": 8,\n" +
               "      \"dropChance\": 100.0\n" +
               "    }\n" +
               "  ]\n" +
               "}\n";
    }

    public static final class DropEntry {
        private static final Random RANDOM = new Random();
        
        public final boolean enabled;
        public final String itemId;
        public final int minQuantity;
        public final int maxQuantity;
        public final float dropChance;

        public DropEntry(boolean enabled, String itemId, int minQuantity, int maxQuantity, float dropChance) {
            this.enabled = enabled;
            this.itemId = itemId;
            this.minQuantity = Math.max(1, minQuantity);
            this.maxQuantity = Math.max(this.minQuantity, maxQuantity);
            this.dropChance = dropChance;
        }

        public int getRandomQuantity() {
            if (minQuantity >= maxQuantity) {
                return minQuantity;
            }
            return minQuantity + RANDOM.nextInt(maxQuantity - minQuantity + 1);
        }

        public boolean shouldDrop() {
            if (dropChance >= 100.0f) return true;
            if (dropChance <= 0.0f) return false;
            return RANDOM.nextFloat() * 100.0f < dropChance;
        }
    }
}
