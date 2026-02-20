package com.example.plugin;

import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobCategoryResolver {
    private static final String CLASSIFICATION_FILE = "Category_Mobs.txt";
    private static final String JSON_CLASSIFICATION_FILE = "HardcoreModeCategories.json";
    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "mobs_(\\w+)\\s*=\\s*\\{([^}]*)\\}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ENTRY_PATTERN = Pattern.compile("\"([^\"]+)\"");
    private static final Pattern JSON_ENTRY_PATTERN = Pattern.compile(
            "\\{\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"pattern\"\\s*:\\s*\"([^\"]+)\"\\s*}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private final List<CategoryPattern> patterns;
    private final Path dataDirectory;
    private final Path jsonPath;

    public MobCategoryResolver() {
        this(null);
    }

    public MobCategoryResolver(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.jsonPath = resolveJsonPath();
        this.patterns = new ArrayList<>();
        loadPatterns();
    }

    public MobCategory resolve(NPCEntity npcEntity) {
        if (npcEntity == null) {
            return MobCategory.NONE;
        }

        for (String id : collectCandidateIds(npcEntity)) {
            MobCategory category = resolve(id);
            if (category != MobCategory.NONE) {
                return category;
            }
        }

        return MobCategory.NONE;
    }

    public MobCategory resolve(String creatureId) {
        if (creatureId == null || creatureId.isEmpty()) {
            return MobCategory.NONE;
        }

        String normalized = creatureId.trim();
        CategoryPattern best = null;

        for (CategoryPattern entry : patterns) {
            if (!entry.pattern.matcher(normalized).matches()) {
                continue;
            }
            if (best == null || entry.isMoreSpecificThan(best)) {
                best = entry;
            }
        }

        return best == null ? MobCategory.NONE : best.category;
    }

    int getPatternCount() {
        return patterns.size();
    }

    private String loadClassificationText() {
        Path external = dataDirectory != null ? dataDirectory.resolve(CLASSIFICATION_FILE) : Path.of(CLASSIFICATION_FILE);
        if (Files.isRegularFile(external)) {
            try {
                return Files.readString(external, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
            }
        }

        try (InputStream stream = MobCategoryResolver.class.getClassLoader()
                .getResourceAsStream(CLASSIFICATION_FILE)) {
            if (stream != null) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        return "";
    }

    public List<CategoryEntry> getEntries() {
        List<CategoryEntry> entries = new ArrayList<>();
        for (CategoryPattern p : patterns) {
            entries.add(new CategoryEntry(p.category, p.rawPattern));
        }
        return Collections.unmodifiableList(entries);
    }

    public void reload() {
        patterns.clear();
        loadPatterns();
    }

    public boolean addEntry(MobCategory category, String rawPattern) {
        if (category == null || rawPattern == null) {
            return false;
        }

        String trimmed = rawPattern.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        for (CategoryPattern existing : patterns) {
            if (existing.rawPattern.equalsIgnoreCase(trimmed)) {
                return false;
            }
        }

        int order = getNextOrder();
        Pattern regex = compileGlob(trimmed);
        patterns.add(new CategoryPattern(category, trimmed, regex, order));
        saveToJson();
        return true;
    }

    public boolean removeEntry(MobCategory category, String rawPattern) {
        if (category == null || rawPattern == null) {
            return false;
        }

        String trimmed = rawPattern.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        boolean removed = patterns.removeIf(
                entry -> entry.category == category && entry.rawPattern.equalsIgnoreCase(trimmed)
        );
        if (removed) {
            saveToJson();
        }
        return removed;
    }

    private void loadPatterns() {
        List<CategoryPattern> loaded = tryLoadFromJson();
        if (loaded.isEmpty()) {
            String sourceText = loadClassificationText();
            loaded = buildPatternsFromText(sourceText);
            writeJsonSnapshot(loaded);
        }
        patterns.addAll(loaded);
    }

    private List<CategoryPattern> tryLoadFromJson() {
        if (jsonPath == null || !Files.isRegularFile(jsonPath)) {
            return Collections.emptyList();
        }

        try {
            String content = Files.readString(jsonPath, StandardCharsets.UTF_8);
            return buildPatternsFromJson(content);
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
    }

    private List<CategoryPattern> buildPatternsFromJson(String json) {
        List<CategoryPattern> result = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return result;
        }

        Matcher matcher = JSON_ENTRY_PATTERN.matcher(json);
        int order = 0;
        while (matcher.find()) {
            String categoryKey = matcher.group(1);
            String rawPattern = matcher.group(2);
            MobCategory category = MobCategory.fromFileKey(categoryKey);
            if (category == MobCategory.NONE) {
                continue;
            }
            Pattern regex = compileGlob(rawPattern);
            result.add(new CategoryPattern(category, rawPattern, regex, order++));
        }
        return result;
    }

    private List<CategoryPattern> buildPatternsFromText(String sourceText) {
        List<CategoryPattern> result = new ArrayList<>();
        if (sourceText == null || sourceText.isEmpty()) {
            return result;
        }

        Matcher sectionMatcher = SECTION_PATTERN.matcher(sourceText);
        int order = 0;
        while (sectionMatcher.find()) {
            String sectionKey = sectionMatcher.group(1);
            MobCategory category = MobCategory.fromFileKey(sectionKey);
            if (category == MobCategory.NONE) {
                continue;
            }

            String body = sectionMatcher.group(2);
            Matcher entryMatcher = ENTRY_PATTERN.matcher(body);
            while (entryMatcher.find()) {
                String raw = entryMatcher.group(1);
                Pattern regex = compileGlob(raw);
                result.add(new CategoryPattern(category, raw, regex, order++));
            }
        }

        return result;
    }

    private void writeJsonSnapshot(List<CategoryPattern> loaded) {
        if (jsonPath == null || loaded.isEmpty()) {
            return;
        }
        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String json = serializeToJson(loaded);
            Files.writeString(jsonPath, json, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private int getNextOrder() {
        int max = -1;
        for (CategoryPattern entry : patterns) {
            if (entry.order > max) {
                max = entry.order;
            }
        }
        return max + 1;
    }

    private void saveToJson() {
        if (jsonPath == null) {
            return;
        }
        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String json = serializeToJson(patterns);
            Files.writeString(jsonPath, json, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private String serializeToJson(List<CategoryPattern> loaded) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"entries\": [\n");
        for (int i = 0; i < loaded.size(); i++) {
            CategoryPattern p = loaded.get(i);
            sb.append("    {\"category\": \"")
                    .append(escapeJson(p.category.name()))
                    .append("\", \"pattern\": \"")
                    .append(escapeJson(p.rawPattern))
                    .append("\"}");
            if (i < loaded.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private Path resolveJsonPath() {
        if (dataDirectory != null) {
            return dataDirectory.resolve(JSON_CLASSIFICATION_FILE);
        }
        return Path.of(JSON_CLASSIFICATION_FILE);
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

    private Set<String> collectCandidateIds(NPCEntity npcEntity) {
        Set<String> ids = new LinkedHashSet<>();
        addIfPresent(ids, npcEntity.getRoleName());
        addIfPresent(ids, npcEntity.getNPCTypeId());

        Role role = npcEntity.getRole();
        if (role != null) {
            addIfPresent(ids, role.getRoleName());
        }

        return ids;
    }

    private void addIfPresent(Set<String> ids, String value) {
        if (value != null && !value.isEmpty()) {
            ids.add(value);
        }
    }

    private static final class CategoryPattern {
        private final MobCategory category;
        private final Pattern pattern;
        private final int literalLength;
        private final int wildcardCount;
        private final int order;
        private final String rawPattern;

        private CategoryPattern(MobCategory category, String rawPattern, Pattern pattern, int order) {
            this.category = category;
            this.pattern = pattern;
            int stars = (int) rawPattern.chars().filter(ch -> ch == '*').count();
            this.literalLength = rawPattern.length() - stars;
            this.wildcardCount = stars;
            this.order = order;
            this.rawPattern = rawPattern;
        }

        private boolean isMoreSpecificThan(CategoryPattern other) {
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

    public static final class CategoryEntry {
        public final MobCategory category;
        public final String pattern;

        public CategoryEntry(MobCategory category, String pattern) {
            this.category = category;
            this.pattern = pattern;
        }
    }
}
