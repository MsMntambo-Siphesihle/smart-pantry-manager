package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The outcome of testing one recipe against the user's pantry.
 * A recipe is only "suggested" when missing.isEmpty() is true - this is the
 * strict-matching rule required by Section 2.3 of the brief.
 */
public class MatchResult {

    private final Recipe recipe;
    private final List<String> missing = new ArrayList<>();

    public MatchResult(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() { return recipe; }

    public List<String> getMissing() { return missing; }

    public void addMissing(String ingredientName) { missing.add(ingredientName); }

    /** True only when EVERY required ingredient is present in sufficient quantity. */
    public boolean isFullMatch() { return missing.isEmpty(); }

    /** True when exactly one ingredient is short - used by the optional "Almost There" list. */
    public boolean isAlmostThere() { return missing.size() == 1; }

    /**
     * Built with a StringBuilder rather than String.join because the app
     * supports API 24 and String.join only exists from API 26.
     */
    public String getMissingSummary() {
        if (missing.isEmpty()) return "";
        StringBuilder builder = new StringBuilder("Missing: ");
        for (int i = 0; i < missing.size(); i++) {
            if (i > 0) builder.append(", ");
            builder.append(missing.get(i));
        }
        return builder.toString();
    }
}
