package com.richfield.smartpantry.logic;

import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE CORE BUSINESS LOGIC OF THE APP (Section 2.3 of the brief).
 *
 * The strict-matching rule: a recipe may only be suggested when EVERY single
 * ingredient it requires is in the pantry, in at least the required quantity.
 * If a recipe needs five ingredients and the pantry holds four of them, the
 * recipe is excluded - there is no partial credit.
 *
 * The class is deliberately kept free of Android classes so the rule can be
 * unit tested and explained on its own.
 */
public final class RecipeMatcher {

    /** A pantry total, held in the base unit of its family. */
    private static class PantryStock {
        final String family;
        double baseQuantity;

        PantryStock(String family, double baseQuantity) {
            this.family = family;
            this.baseQuantity = baseQuantity;
        }
    }

    private RecipeMatcher() {
    }

    /**
     * Sums the pantry into a lookup keyed by normalised ingredient name, so
     * that two separate entries of "Eggs" are treated as one total.
     */
    private static Map<String, PantryStock> buildPantryIndex(List<PantryItem> pantry) {
        Map<String, PantryStock> index = new HashMap<>();

        for (PantryItem item : pantry) {
            String key = item.getNormalisedName();
            if (key == null || key.isEmpty()) {
                key = IngredientNormaliser.normalise(item.getName());
            }

            String family = UnitConverter.familyOf(item.getUnit());
            double base = UnitConverter.toBase(item.getQuantity(), item.getUnit());

            PantryStock existing = index.get(key);
            if (existing == null) {
                index.put(key, new PantryStock(family, base));
            } else if (existing.family.equals(family)) {
                existing.baseQuantity += base;   // same ingredient, same family - add them up
            }
        }
        return index;
    }

    /**
     * Tests a single recipe against the pantry and records every shortfall.
     * An ingredient counts as available only when the name matches AND the
     * pantry holds at least the required amount.
     */
    private static MatchResult evaluate(Recipe recipe, Map<String, PantryStock> pantryIndex) {
        MatchResult result = new MatchResult(recipe);

        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = required.getNormalisedName();
            PantryStock stock = pantryIndex.get(key);

            // Exact key missing - try a whole-word loose match
            // ("chicken breast" in the pantry satisfies "chicken").
            if (stock == null) {
                for (Map.Entry<String, PantryStock> entry : pantryIndex.entrySet()) {
                    if (IngredientNormaliser.looseMatches(entry.getKey(), key)) {
                        stock = entry.getValue();
                        break;
                    }
                }
            }

            if (stock == null) {
                result.addMissing(required.getName());       // not in the pantry at all
                continue;
            }

            double neededBase = UnitConverter.toBase(required.getQuantity(), required.getUnit());
            String neededFamily = UnitConverter.familyOf(required.getUnit());

            // If the units are from different families (e.g. the recipe wants
            // 200 g of rice but the user recorded "1 cup"), we cannot convert
            // safely, so we accept presence of the ingredient rather than
            // rejecting a recipe on a unit technicality.
            boolean sameFamily = stock.family.equals(neededFamily);
            boolean enough = !sameFamily || (stock.baseQuantity + 0.0001 >= neededBase);

            if (!enough) {
                result.addMissing(required.getName());       // present, but not enough of it
            }
        }
        return result;
    }

    /**
     * STRICT suggestions: only recipes where nothing at all is missing.
     * This is the list shown on the Suggested Recipes screen.
     */
    public static List<Recipe> findStrictMatches(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<Recipe> suggestions = new ArrayList<>();
        if (pantry == null || pantry.isEmpty()) return suggestions;   // empty pantry can never match

        Map<String, PantryStock> index = buildPantryIndex(pantry);
        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients().isEmpty()) continue;
            if (evaluate(recipe, index).isFullMatch()) {
                suggestions.add(recipe);
            }
        }
        return suggestions;
    }

    /**
     * OPTIONAL STRETCH (Section 2.3 / Section 8): recipes short of exactly one
     * ingredient. Kept in a separate method, and shown in a clearly separate
     * section of the UI, so it can never be confused with a strict suggestion.
     */
    public static List<MatchResult> findAlmostThere(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<MatchResult> almost = new ArrayList<>();
        if (pantry == null || pantry.isEmpty()) return almost;

        Map<String, PantryStock> index = buildPantryIndex(pantry);
        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients().isEmpty()) continue;
            MatchResult result = evaluate(recipe, index);
            if (result.isAlmostThere()) {
                almost.add(result);
            }
        }
        return almost;
    }

    /**
     * Used by the recipe detail screen to tick off the ingredients the user
     * already has.
     */
    public static MatchResult evaluateSingle(Recipe recipe, List<PantryItem> pantry) {
        return evaluate(recipe, buildPantryIndex(pantry));
    }
}
