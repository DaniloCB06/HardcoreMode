package com.example.plugin.config;

import com.example.plugin.MobCategory;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobMoneyDropConfig {
    private static final String JSON_FILE = "HardcoreModeMoneyMobsDrops.json";
    private static final Pattern ENABLED_PATTERN = Pattern.compile(
            "\"enabled\"\\s*:\\s*(true|false)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CATEGORY_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"amount\"\\s*:\\s*([-]?[\\d.]+)\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern MOB_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"pattern\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"amount\"\\s*:\\s*([-]?[\\d.]+)\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private final Map<MobCategory, Double> categoryAmounts = new EnumMap<>(MobCategory.class);
    private final Map<String, MoneyPatternEntry> mobEntries = new LinkedHashMap<>();
    private final Path jsonPath;
    private boolean enabled = true;

    public MobMoneyDropConfig(Path dataDirectory) {
        this.jsonPath = dataDirectory != null ? dataDirectory.resolve(JSON_FILE) : Path.of(JSON_FILE);
        initializeDefaults();
        ensureDefaultConfigExists();
        reload();
    }

    public synchronized void reload() {
        initializeDefaults();
        mobEntries.clear();
        enabled = true;

        if (!Files.isRegularFile(jsonPath)) {
            return;
        }

        try {
            String content = Files.readString(jsonPath, StandardCharsets.UTF_8);
            loadEnabled(content);
            loadCategories(content);
            loadMobEntries(content);
        } catch (IOException ignored) {
        }
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        saveToJson();
    }

    public synchronized double getCategoryAmount(MobCategory category) {
        if (category == null || category == MobCategory.NONE) {
            return 0.0d;
        }
        return sanitizeAmount(categoryAmounts.getOrDefault(category, 0.0d));
    }

    public synchronized void setCategoryAmount(MobCategory category, double amount) {
        if (category == null || category == MobCategory.NONE) {
            return;
        }
        categoryAmounts.put(category, sanitizeAmount(amount));
        saveToJson();
    }

    public synchronized void clearCategoryAmount(MobCategory category) {
        if (category == null || category == MobCategory.NONE) {
            return;
        }
        categoryAmounts.put(category, 0.0d);
        saveToJson();
    }

    public synchronized boolean hasMobOverride(String pattern) {
        if (pattern == null) {
            return false;
        }
        return mobEntries.containsKey(normalizePattern(pattern));
    }

    public synchronized double getMobOverrideAmount(String pattern) {
        if (pattern == null) {
            return 0.0d;
        }
        MoneyPatternEntry entry = mobEntries.get(normalizePattern(pattern));
        return entry == null ? 0.0d : sanitizeAmount(entry.amount);
    }

    public synchronized void setMobAmount(String pattern, double amount) {
        String normalized = normalizePattern(pattern);
        if (normalized == null) {
            return;
        }
        mobEntries.put(normalized, new MoneyPatternEntry(normalized, sanitizeAmount(amount), mobEntries.size()));
        saveToJson();
    }

    public synchronized void clearMobAmount(String pattern) {
        setMobAmount(pattern, 0.0d);
    }

    public synchronized void removeMobOverride(String pattern) {
        String normalized = normalizePattern(pattern);
        if (normalized == null) {
            return;
        }
        if (mobEntries.remove(normalized) != null) {
            saveToJson();
        }
    }

    public synchronized void removeMobOverrides(Collection<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (String pattern : patterns) {
            String normalized = normalizePattern(pattern);
            if (normalized != null) {
                changed |= mobEntries.remove(normalized) != null;
            }
        }

        if (changed) {
            saveToJson();
        }
    }

    public synchronized double getEffectiveAmount(String creaturePatternOrId, MobCategory category) {
        MoneyPatternEntry best = findBestEntry(creaturePatternOrId);
        if (best != null) {
            return sanitizeAmount(best.amount);
        }
        return getCategoryAmount(category);
    }

    public synchronized double resolveAmount(NPCEntity npcEntity, MobCategory category) {
        if (!enabled || npcEntity == null || category == null || category == MobCategory.NONE) {
            return 0.0d;
        }

        MoneyPatternEntry best = null;
        for (String candidateId : collectCandidateIds(npcEntity)) {
            MoneyPatternEntry candidateEntry = findBestEntry(candidateId);
            if (candidateEntry != null && (best == null || candidateEntry.isMoreSpecificThan(best))) {
                best = candidateEntry;
            }
        }

        if (best != null) {
            return sanitizeAmount(best.amount);
        }

        return getCategoryAmount(category);
    }

    public synchronized List<MobMoneyEntry> getMobEntries() {
        List<MobMoneyEntry> entries = new ArrayList<>();
        for (MoneyPatternEntry entry : mobEntries.values()) {
            entries.add(new MobMoneyEntry(entry.rawPattern, sanitizeAmount(entry.amount)));
        }
        return Collections.unmodifiableList(entries);
    }

    private void initializeDefaults() {
        for (MobCategory category : MobCategory.values()) {
            if (category != MobCategory.NONE) {
                categoryAmounts.put(category, 0.0d);
            }
        }
    }

    private void ensureDefaultConfigExists() {
        if (Files.isRegularFile(jsonPath)) {
            return;
        }

        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(jsonPath, getDefaultJsonContent(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private void loadCategories(String content) {
        Matcher matcher = CATEGORY_ENTRY_PATTERN.matcher(content);
        while (matcher.find()) {
            MobCategory category = MobCategory.fromFileKey(matcher.group(1));
            if (category == MobCategory.NONE) {
                continue;
            }
            categoryAmounts.put(category, sanitizeAmount(parseAmount(matcher.group(2))));
        }
    }

    private void loadEnabled(String content) {
        Matcher matcher = ENABLED_PATTERN.matcher(content);
        if (matcher.find()) {
            enabled = Boolean.parseBoolean(matcher.group(1).toLowerCase(Locale.US));
        }
    }

    private void loadMobEntries(String content) {
        Matcher matcher = MOB_ENTRY_PATTERN.matcher(content);
        int order = 0;
        while (matcher.find()) {
            String pattern = normalizePattern(matcher.group(1));
            if (pattern == null) {
                continue;
            }
            mobEntries.put(pattern, new MoneyPatternEntry(pattern, sanitizeAmount(parseAmount(matcher.group(2))), order++));
        }
    }

    private MoneyPatternEntry findBestEntry(String creaturePatternOrId) {
        if (creaturePatternOrId == null || creaturePatternOrId.isBlank()) {
            return null;
        }

        String normalized = creaturePatternOrId.trim();
        MoneyPatternEntry best = null;
        for (MoneyPatternEntry entry : mobEntries.values()) {
            if (!entry.pattern.matcher(normalized).matches()) {
                continue;
            }
            if (best == null || entry.isMoreSpecificThan(best)) {
                best = entry;
            }
        }
        return best;
    }

    private void saveToJson() {
        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(jsonPath, serializeToJson(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private String serializeToJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"description\": \"Money rewards for creature kills. Category values are the default reward, and mob patterns can override them.\",\n");
        sb.append("  \"note\": \"Category edits affect the whole category. Mob edits override a single mob pattern.\",\n");
        sb.append("  \"enabled\": ").append(enabled).append(",\n");
        sb.append("  \"categoryAmounts\": [\n");

        List<MobCategory> categories = new ArrayList<>();
        for (MobCategory category : MobCategory.values()) {
            if (category != MobCategory.NONE) {
                categories.add(category);
            }
        }

        for (int i = 0; i < categories.size(); i++) {
            MobCategory category = categories.get(i);
            sb.append("    {\"category\": \"")
                    .append(category.getFileKey())
                    .append("\", \"amount\": ")
                    .append(formatAmount(getCategoryAmount(category)))
                    .append("}");
            if (i < categories.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ],\n");
        sb.append("  \"mobAmounts\": [\n");

        List<MoneyPatternEntry> values = new ArrayList<>(mobEntries.values());
        for (int i = 0; i < values.size(); i++) {
            MoneyPatternEntry entry = values.get(i);
            sb.append("    {\"pattern\": \"")
                    .append(escapeJson(entry.rawPattern))
                    .append("\", \"amount\": ")
                    .append(formatAmount(entry.amount))
                    .append("}");
            if (i < values.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String getDefaultJsonContent() {
        return "{\n" +
                "  \"description\": \"Money rewards for creature kills. Category values are the default reward, and mob patterns can override them.\",\n" +
                "  \"note\": \"Category edits affect the whole category. Mob edits override a single mob pattern.\",\n" +
                "  \"enabled\": true,\n" +
                "  \"categoryAmounts\": [\n" +
                "    {\"category\": \"PASSIVE\", \"amount\": 0.0},\n" +
                "    {\"category\": \"CRITTER\", \"amount\": 0.0},\n" +
                "    {\"category\": \"HOSTILE\", \"amount\": 0.0},\n" +
                "    {\"category\": \"ELITE\", \"amount\": 0.0},\n" +
                "    {\"category\": \"MINIBOSS\", \"amount\": 0.0},\n" +
                "    {\"category\": \"WORLDBOSS\", \"amount\": 0.0}\n" +
                "  ],\n" +
                "  \"mobAmounts\": []\n" +
                "}\n";
    }

    private static Set<String> collectCandidateIds(NPCEntity npcEntity) {
        Set<String> ids = new LinkedHashSet<>();
        addIfPresent(ids, npcEntity.getRoleName());
        addIfPresent(ids, npcEntity.getNPCTypeId());

        Role role = npcEntity.getRole();
        if (role != null) {
            addIfPresent(ids, role.getRoleName());
        }

        return ids;
    }

    private static void addIfPresent(Set<String> ids, String value) {
        if (value != null && !value.isBlank()) {
            ids.add(value.trim());
        }
    }

    private static String normalizePattern(String pattern) {
        if (pattern == null) {
            return null;
        }
        String normalized = pattern.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static double parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0.0d;
        }
        try {
            return Double.parseDouble(raw.trim().replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return 0.0d;
        }
    }

    private static double sanitizeAmount(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            return 0.0d;
        }
        return Math.max(0.0d, amount);
    }

    private static String formatAmount(double amount) {
        return String.format(Locale.US, "%.2f", sanitizeAmount(amount));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static Pattern compileGlob(String raw) {
        String[] parts = raw.split("\\*", -1);
        StringBuilder builder = new StringBuilder("^");
        for (int i = 0; i < parts.length; i++) {
            builder.append(Pattern.quote(parts[i]));
            if (i < parts.length - 1) {
                builder.append(".*");
            }
        }
        builder.append("$");
        return Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE);
    }

    public static final class MobMoneyEntry {
        public final String pattern;
        public final double amount;

        public MobMoneyEntry(String pattern, double amount) {
            this.pattern = pattern;
            this.amount = sanitizeAmount(amount);
        }
    }

    private static final class MoneyPatternEntry {
        private final String rawPattern;
        private final Pattern pattern;
        private final int literalLength;
        private final int wildcardCount;
        private final int order;
        private final double amount;

        private MoneyPatternEntry(String rawPattern, double amount, int order) {
            this.rawPattern = rawPattern;
            this.pattern = compileGlob(rawPattern);
            int stars = (int) rawPattern.chars().filter(ch -> ch == '*').count();
            this.literalLength = rawPattern.length() - stars;
            this.wildcardCount = stars;
            this.order = order;
            this.amount = sanitizeAmount(amount);
        }

        private boolean isMoreSpecificThan(MoneyPatternEntry other) {
            if (other == null) {
                return true;
            }
            if (literalLength != other.literalLength) {
                return literalLength > other.literalLength;
            }
            if (wildcardCount != other.wildcardCount) {
                return wildcardCount < other.wildcardCount;
            }
            return order < other.order;
        }
    }
}
