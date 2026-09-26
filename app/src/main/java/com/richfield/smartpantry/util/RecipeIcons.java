package com.richfield.smartpantry.util;

import java.util.HashMap;
import java.util.Map;

public final class RecipeIcons {

    private static final Map<String, String> ICONS = new HashMap<>();

    static {
        ICONS.put("Cheese Omelette",          "\uD83C\uDF73");
        ICONS.put("Tomato Pasta",              "\uD83C\uDF5D");
        ICONS.put("Vegetable Fried Rice",      "\uD83C\uDF5A");
        ICONS.put("Banana Pancakes",           "\uD83E\uDD5E");
        ICONS.put("Tuna Sandwich",             "\uD83E\uDD6A");
        ICONS.put("Scrambled Eggs on Toast",   "\uD83C\uDF73");
        ICONS.put("Crispy Potato Wedges",      "\uD83C\uDF5F");
        ICONS.put("Chicken Stir Fry",          "\uD83E\uDD58");
        ICONS.put("Lentil Soup",               "\uD83C\uDF72");
        ICONS.put("Pap and Tomato Relish",     "\uD83C\uDF5B");
        ICONS.put("French Toast",              "\uD83C\uDF5E");
        ICONS.put("Creamy Mushroom Pasta",     "\uD83C\uDF5D");
        ICONS.put("Bean and Cheese Wraps",     "\uD83C\uDF2F");
        ICONS.put("Banana Smoothie",           "\uD83E\uDD64");
        ICONS.put("Garlic Butter Rice",        "\uD83C\uDF5A");
        ICONS.put("Carrot and Coriander Soup", "\uD83C\uDF72");
        ICONS.put("Beef Mince Bolognese",      "\uD83C\uDF5D");
        ICONS.put("Chocolate Mug Cake",        "\uD83E\uDDC1");
        ICONS.put("Egg Fried Noodles",         "\uD83C\uDF5C");
        ICONS.put("Baked Cinnamon Apples",     "\uD83C\uDF4E");
    }

    private RecipeIcons() {
    }

    public static String getIcon(String recipeName) {
        String icon = ICONS.get(recipeName);
        return (icon == null || icon.isEmpty()) ? IngredientIcons.DEFAULT_ICON : icon;
    }
}