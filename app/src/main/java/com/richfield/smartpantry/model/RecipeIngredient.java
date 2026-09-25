package com.richfield.smartpantry.model;

/**
 * One line of a recipe's ingredient list, e.g. "3 piece eggs".
 * Mirrors one row of the "recipe_ingredients" table.
 */
public class RecipeIngredient {

    private long id;
    private long recipeId;
    private String name;
    private String normalisedName;
    private double quantity;
    private String unit;

    public RecipeIngredient(long id, long recipeId, String name, String normalisedName,
                            double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.normalisedName = normalisedName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() { return id; }
    public long getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public String getNormalisedName() { return normalisedName; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    public String getDisplayLine() {
        String qty = (quantity == Math.floor(quantity))
                ? String.valueOf((long) quantity)
                : String.valueOf(quantity);
        return qty + " " + unit + " " + name;
    }
}
