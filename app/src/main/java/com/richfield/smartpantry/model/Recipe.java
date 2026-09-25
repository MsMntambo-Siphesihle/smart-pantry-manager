package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe plus the ingredients it requires. Recipes are seeded into the
 * database on first run (see RecipeSeeder) and are never edited by the user.
 */
public class Recipe {

    private long id;
    private String name;
    private String category;
    private String steps;   // steps separated by "|" in the database
    private String icon;    // representative emoji, e.g. "\uD83C\uDF73" for an omelette
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String category, String steps, String icon) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.steps = steps;
        this.icon = icon;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSteps() { return steps; }

    /** Falls back to a generic pot-of-food emoji if no icon was seeded. */
    public String getIcon() {
        return (icon == null || icon.isEmpty())
                ? com.richfield.smartpantry.util.IngredientIcons.DEFAULT_ICON
                : icon;
    }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void addIngredient(RecipeIngredient ingredient) { ingredients.add(ingredient); }

    /** Splits the stored steps string into a numbered list for the detail screen. */
    public List<String> getStepList() {
        List<String> list = new ArrayList<>();
        if (steps == null || steps.trim().isEmpty()) return list;
        for (String step : steps.split("\\|")) {
            if (!step.trim().isEmpty()) list.add(step.trim());
        }
        return list;
    }
}
