package com.example.plugin;

import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolve a creature ID (ex.: {@code Dragon_Fire}) to a {@link MobCategory}
 * using the mapping provided in {@code Criaturas_classificadas.txt}.
 *
 * - Supports wildcard entries (e.g. {@code Frog_*}, {@code *_Cub}, {@code Piranha*}).
 * - Keeps the order from the source file; first pattern match wins.
 * - Falls back to {@link MobCategory#NONE} when no entry matches.
 */
public class MobCategoryResolver {
    private static final Logger LOGGER = Logger.getLogger(MobCategoryResolver.class.getName());
    private static final String CLASSIFICATION_FILE = "Criaturas_classificadas.txt";
    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "mobs_(\\w+)\\s*=\\s*\\{([^}]*)\\}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ENTRY_PATTERN = Pattern.compile("\"([^\"]+)\"");

    private final List<CategoryPattern> patterns;

    public MobCategoryResolver() {
        this(loadClassificationText());
    }

    MobCategoryResolver(String sourceText) {
        this.patterns = buildPatterns(sourceText);
    }

    /**
     * Resolve category for an NPC entity, trying multiple identifiers.
     */
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

    /**
     * Resolve category for a raw creature ID (e.g., "Frog_Blue").
     */
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

    /**
     * Exposes loaded pattern count for sanity checks/tests.
     */
    int getPatternCount() {
        return patterns.size();
    }

    private static String loadClassificationText() {
        // 1) Try external file (same directory as the mod jar / working dir)
        Path external = Path.of(CLASSIFICATION_FILE);
        if (Files.isRegularFile(external)) {
            try {
                return Files.readString(external, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to read " + external.toAbsolutePath(), e);
            }
        }

        // 2) Fallback to classpath resource bundled with the mod
        try (InputStream stream = MobCategoryResolver.class.getClassLoader()
                .getResourceAsStream(CLASSIFICATION_FILE)) {
            if (stream != null) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read bundled classification resource.", e);
        }

        LOGGER.warning("No creature classification source found; all mobs will map to NONE.");
        return "";
    }

    private List<CategoryPattern> buildPatterns(String sourceText) {
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
                LOGGER.fine("Ignoring unknown category key: " + sectionKey);
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

    private static Pattern compileGlob(String raw) {
        // Convert '*' into '.*' and escape everything else.
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

        private CategoryPattern(MobCategory category, String rawPattern, Pattern pattern, int order) {
            this.category = category;
            this.pattern = pattern;
            int stars = (int) rawPattern.chars().filter(ch -> ch == '*').count();
            this.literalLength = rawPattern.length() - stars;
            this.wildcardCount = stars;
            this.order = order;
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
}
