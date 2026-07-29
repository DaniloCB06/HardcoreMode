package com.example.plugin.config;

import com.example.plugin.MobCategory;
import com.example.plugin.MobCategoryResolver;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

import java.io.IOException;
import java.io.InputStream;
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
    private static final String LEGACY_GROUPED_JSON_FILE = "HardcoreModeMoneyMobsDropsByCategory.json";

    private static final Pattern ENABLED_PATTERN = Pattern.compile(
            "\"enabled\"\\s*:\\s*(true|false)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern GROUPED_CATEGORY_PATTERN = Pattern.compile(
            "\\{\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"defaultAmount\"\\s*:\\s*([-]?[\\d.]+)\\s*,\\s*\"mobs\"\\s*:\\s*\\[(.*?)]\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern GROUPED_MOB_PATTERN = Pattern.compile(
            "\\{\\s*\"pattern\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"amount\"\\s*:\\s*([-]?[\\d.]+)\\s*,\\s*\"override\"\\s*:\\s*(true|false)\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern LEGACY_CATEGORY_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"amount\"\\s*:\\s*([-]?[\\d.]+)\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern LEGACY_MOB_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"pattern\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"amount\"\\s*:\\s*([-]?[\\d.]+)\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private final Map<MobCategory, Double> categoryAmounts = new EnumMap<>(MobCategory.class);
    private final Map<String, MoneyPatternEntry> mobEntries = new LinkedHashMap<>();
    private final MobCategoryResolver categoryResolver;
    private final Path jsonPath;
    private final Path legacyGroupedJsonPath;
    private boolean enabled = false;

    public MobMoneyDropConfig(Path dataDirectory, MobCategoryResolver categoryResolver) {
        this.categoryResolver = categoryResolver;
        this.jsonPath = dataDirectory != null ? dataDirectory.resolve(JSON_FILE) : Path.of(JSON_FILE);
        this.legacyGroupedJsonPath = dataDirectory != null ? dataDirectory.resolve(LEGACY_GROUPED_JSON_FILE) : Path.of(LEGACY_GROUPED_JSON_FILE);
        initializeDefaults();
        ensureDefaultConfigExists();
        reload();
    }

    public synchronized void reload() {
        initializeDefaults();
        mobEntries.clear();
        enabled = false;

        Path sourcePath = selectPreferredSourcePath();
        if (sourcePath == null || !Files.isRegularFile(sourcePath)) {
            saveToJson();
            return;
        }

        try {
            String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
            loadEnabled(content);
            if (!loadGroupedContent(content)) {
                loadLegacyContent(content);
            }
        } catch (IOException ignored) {
        }

        saveToJson();
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
        setMobAmount(pattern, inferCategoryForPattern(pattern), amount);
    }

    public synchronized void setMobAmount(String pattern, MobCategory category, double amount) {
        String normalized = normalizePattern(pattern);
        if (normalized == null) {
            return;
        }
        mobEntries.put(
                normalized,
                new MoneyPatternEntry(normalized, sanitizeAmount(amount), mobEntries.size(), normalizeCategory(category))
        );
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
            entries.add(new MobMoneyEntry(entry.rawPattern, sanitizeAmount(entry.amount), entry.category));
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
        if (Files.isRegularFile(jsonPath) || Files.isRegularFile(legacyGroupedJsonPath)) {
            return;
        }

        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (InputStream stream = MobMoneyDropConfig.class.getClassLoader().getResourceAsStream(JSON_FILE)) {
                if (stream != null) {
                    Files.writeString(jsonPath, new String(stream.readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                    return;
                }
            }
        } catch (IOException ignored) {
        }

        saveToJson();
    }

    private boolean loadGroupedContent(String content) {
        Matcher categoryMatcher = GROUPED_CATEGORY_PATTERN.matcher(content);
        int order = 0;
        boolean found = false;

        while (categoryMatcher.find()) {
            found = true;
            MobCategory category = MobCategory.fromFileKey(categoryMatcher.group(1));
            if (category == MobCategory.NONE) {
                continue;
            }

            double defaultAmount = sanitizeAmount(parseAmount(categoryMatcher.group(2)));
            categoryAmounts.put(category, defaultAmount);

            Matcher mobMatcher = GROUPED_MOB_PATTERN.matcher(categoryMatcher.group(3));
            while (mobMatcher.find()) {
                String pattern = normalizePattern(mobMatcher.group(1));
                if (pattern == null) {
                    continue;
                }

                double amount = sanitizeAmount(parseAmount(mobMatcher.group(2)));
                boolean override = Boolean.parseBoolean(mobMatcher.group(3).toLowerCase(Locale.US));
                if (override) {
                    mobEntries.put(pattern, new MoneyPatternEntry(pattern, amount, order++, category));
                }
            }
        }

        return found;
    }

    private void loadLegacyContent(String content) {
        loadLegacyCategories(content);
        loadLegacyMobEntries(content);
    }

    private void loadLegacyCategories(String content) {
        Matcher matcher = LEGACY_CATEGORY_ENTRY_PATTERN.matcher(content);
        while (matcher.find()) {
            MobCategory category = MobCategory.fromFileKey(matcher.group(1));
            if (category == MobCategory.NONE) {
                continue;
            }
            categoryAmounts.put(category, sanitizeAmount(parseAmount(matcher.group(2))));
        }
    }

    private void loadLegacyMobEntries(String content) {
        Matcher matcher = LEGACY_MOB_ENTRY_PATTERN.matcher(content);
        int order = 0;
        while (matcher.find()) {
            String pattern = normalizePattern(matcher.group(1));
            if (pattern == null) {
                continue;
            }
            mobEntries.put(
                    pattern,
                    new MoneyPatternEntry(
                            pattern,
                            sanitizeAmount(parseAmount(matcher.group(2))),
                            order++,
                            inferCategoryForPattern(pattern)
                    )
            );
        }
    }

    private void loadEnabled(String content) {
        Matcher matcher = ENABLED_PATTERN.matcher(content);
        if (matcher.find()) {
            enabled = Boolean.parseBoolean(matcher.group(1).toLowerCase(Locale.US));
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
            String serialized = serializeToJson();
            writeJson(jsonPath, serialized);
            deleteLegacyGroupedJson();
        } catch (IOException ignored) {
        }
    }

    private void writeJson(Path path, String content) throws IOException {
        if (path == null) {
            return;
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    private void deleteLegacyGroupedJson() {
        if (legacyGroupedJsonPath == null || !Files.isRegularFile(legacyGroupedJsonPath)) {
            return;
        }

        try {
            Files.deleteIfExists(legacyGroupedJsonPath);
        } catch (IOException ignored) {
        }
    }

    private String serializeToJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"description\": \"Money rewards grouped by category using the same structure as Category_Mobs.txt.\",\n");
        sb.append("  \"note\": \"Category defaults apply to the whole category. Set override=true for a mob-specific value.\",\n");
        sb.append("  \"enabled\": ").append(enabled).append(",\n");
        sb.append("  \"categories\": [\n");

        Map<MobCategory, List<String>> groupedPatterns = buildGroupedPatterns();
        List<MobCategory> orderedCategories = new ArrayList<>(groupedPatterns.keySet());
        for (int i = 0; i < orderedCategories.size(); i++) {
            MobCategory category = orderedCategories.get(i);
            double defaultAmount = getCategoryAmount(category);
            List<String> patterns = groupedPatterns.getOrDefault(category, List.of());

            sb.append("    {\n");
            sb.append("      \"category\": \"").append(category.getFileKey()).append("\",\n");
            sb.append("      \"defaultAmount\": ").append(formatAmount(defaultAmount)).append(",\n");
            sb.append("      \"mobs\": [\n");
            for (int j = 0; j < patterns.size(); j++) {
                String pattern = patterns.get(j);
                MoneyPatternEntry overrideEntry = mobEntries.get(normalizePattern(pattern));
                boolean override = overrideEntry != null;
                double amount = override ? overrideEntry.amount : defaultAmount;
                sb.append("        {\"pattern\": \"")
                        .append(escapeJson(pattern))
                        .append("\", \"amount\": ")
                        .append(formatAmount(amount))
                        .append(", \"override\": ")
                        .append(override)
                        .append("}");
                if (j < patterns.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("      ]\n");
            sb.append("    }");
            if (i < orderedCategories.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private Map<MobCategory, List<String>> buildGroupedPatterns() {
        Map<MobCategory, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        for (MobCategory category : MobCategory.values()) {
            if (category != MobCategory.NONE) {
                grouped.put(category, new LinkedHashSet<>());
            }
        }

        if (categoryResolver != null) {
            Map<MobCategory, List<String>> resolverEntries = categoryResolver.getEntriesByCategory();
            for (Map.Entry<MobCategory, List<String>> entry : resolverEntries.entrySet()) {
                LinkedHashSet<String> values = grouped.get(entry.getKey());
                if (values != null) {
                    values.addAll(entry.getValue());
                }
            }
        }

        for (MoneyPatternEntry entry : mobEntries.values()) {
            if (entry.category == MobCategory.NONE) {
                continue;
            }
            grouped.computeIfAbsent(entry.category, ignored -> new LinkedHashSet<>()).add(entry.rawPattern);
        }

        Map<MobCategory, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<MobCategory, LinkedHashSet<String>> entry : grouped.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    private Path selectPreferredSourcePath() {
        boolean hasPrimary = Files.isRegularFile(jsonPath);
        boolean hasGrouped = Files.isRegularFile(legacyGroupedJsonPath);
        if (hasPrimary) {
            return jsonPath;
        }
        if (hasGrouped) {
            return legacyGroupedJsonPath;
        }

        try (InputStream stream = MobMoneyDropConfig.class.getClassLoader().getResourceAsStream(JSON_FILE)) {
            if (stream != null) {
                writeJson(jsonPath, new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                return jsonPath;
            }
        } catch (IOException ignored) {
        }

        if (hasPrimary) {
            return jsonPath;
        }
        return null;
    }

    private MobCategory inferCategoryForPattern(String pattern) {
        String normalized = normalizePattern(pattern);
        if (normalized == null || categoryResolver == null) {
            return MobCategory.NONE;
        }

        for (MobCategoryResolver.CategoryEntry entry : categoryResolver.getEntries()) {
            if (entry.pattern.equalsIgnoreCase(normalized)) {
                return normalizeCategory(entry.category);
            }
        }

        return MobCategory.NONE;
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

    private static MobCategory normalizeCategory(MobCategory category) {
        return category == null ? MobCategory.NONE : category;
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
        public final MobCategory category;

        public MobMoneyEntry(String pattern, double amount, MobCategory category) {
            this.pattern = pattern;
            this.amount = sanitizeAmount(amount);
            this.category = normalizeCategory(category);
        }
    }

    private static final class MoneyPatternEntry {
        private final String rawPattern;
        private final Pattern pattern;
        private final int literalLength;
        private final int wildcardCount;
        private final int order;
        private final double amount;
        private final MobCategory category;

        private MoneyPatternEntry(String rawPattern, double amount, int order, MobCategory category) {
            this.rawPattern = rawPattern;
            this.pattern = compileGlob(rawPattern);
            int stars = (int) rawPattern.chars().filter(ch -> ch == '*').count();
            this.literalLength = rawPattern.length() - stars;
            this.wildcardCount = stars;
            this.order = order;
            this.amount = sanitizeAmount(amount);
            this.category = normalizeCategory(category);
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
