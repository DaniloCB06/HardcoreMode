package com.example.plugin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobCategoryResolverTest {
    private static MobCategoryResolver resolver;

    @BeforeAll
    static void setup() {
        resolver = new MobCategoryResolver();
        assertTrue(resolver.getPatternCount() > 0, "Classification patterns should be loaded.");
    }

    @Test
    void matchesExactEntry() {
        assertEquals(MobCategory.PASSIVE, resolver.resolve("Camel_Calf"));
    }

    @Test
    void matchesWildcardSuffix() {
        assertEquals(MobCategory.CRITTER, resolver.resolve("Frog_Blue"));
    }

    @Test
    void prefersMoreSpecificOverGenericWildcard() {
        assertEquals(MobCategory.MINIBOSS, resolver.resolve("Zombie_Aberrant"));
    }

    @Test
    void matchesPrefixWildcard() {
        assertEquals(MobCategory.CRITTER, resolver.resolve("Wolf_Cub"));
    }

    @Test
    void returnsNoneWhenUnknown() {
        assertEquals(MobCategory.NONE, resolver.resolve("Completely_Unknown_Mob"));
    }
}
