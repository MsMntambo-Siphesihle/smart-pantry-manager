package com.richfield.smartpantry.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Turns a free-text ingredient name into a stable matching key.
 *
 * The brief (Section 2.3) says a naive exact-string match that breaks on
 * "tomato" vs "tomatoes" will be marked down, so every name - both in the
 * pantry and in the seeded recipes - is pushed through this class before it
 * is ever compared. This is deliberately simple rule-based normalisation,
 * not NLP.
 */
public final class IngredientNormaliser {

    /** Descriptive words that do not change WHAT the ingredient is. */
    private static final Set<String> NOISE_WORDS = new HashSet<>(Arrays.asList(
            "fresh", "frozen", "dried", "chopped", "sliced", "diced", "minced",
            "grated", "ground", "large", "medium", "small", "ripe", "raw",
            "cooked", "whole", "plain", "of", "a", "an", "the"
    ));

    private IngredientNormaliser() {
        // Utility class - never instantiated.
    }

    /**
     * Lower-cases, strips punctuation and descriptive words, then singularises
     * each remaining word. "3 Large Ripe Tomatoes!" -> "tomato".
     */
    public static String normalise(String rawName) {
        if (rawName == null) return "";

        String cleaned = rawName.toLowerCase()
                .replaceAll("[^a-z\\s]", " ")   // drop digits and punctuation
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            if (NOISE_WORDS.contains(word)) continue;
            if (result.length() > 0) result.append(" ");
            result.append(singularise(word));
        }

        // If the user typed only noise words, fall back to the cleaned string
        // so that we never return an empty key.
        return result.length() == 0 ? cleaned : result.toString();
    }

    /**
     * Very small English plural-to-singular rule set. It only needs to handle
     * the everyday food words this app deals with.
     */
    private static String singularise(String word) {
        if (word.length() <= 3) return word;                 // "egg" stays "egg", "oil" stays "oil"
        if (word.endsWith("ss")) return word;                // "glass", "grass"
        if (word.endsWith("ies")) return word.substring(0, word.length() - 3) + "y";  // berries -> berry
        if (word.endsWith("oes")) return word.substring(0, word.length() - 2);        // tomatoes -> tomato
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes")) {
            return word.substring(0, word.length() - 2);     // peaches -> peach
        }
        if (word.endsWith("s")) return word.substring(0, word.length() - 1);          // eggs -> egg
        return word;
    }

    /**
     * Fallback comparison for compound names: "chicken breast" in the pantry
     * should satisfy a recipe asking for "chicken". Whole words only, so
     * "corn" never accidentally matches "cornflour".
     */
    public static boolean looseMatches(String pantryKey, String recipeKey) {
        if (pantryKey.equals(recipeKey)) return true;
        return containsWholePhrase(pantryKey, recipeKey) || containsWholePhrase(recipeKey, pantryKey);
    }

    private static boolean containsWholePhrase(String haystack, String needle) {
        if (needle.isEmpty()) return false;
        return (" " + haystack + " ").contains(" " + needle + " ");
    }
}
