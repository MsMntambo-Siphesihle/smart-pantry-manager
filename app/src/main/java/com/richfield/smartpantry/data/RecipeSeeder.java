package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.richfield.smartpantry.logic.IngredientNormaliser;

/**
 * Pre-loads the recipe collection the first time the database is created
 * (Section 2.2 of the brief asks for 15-20 recipes; 20 are seeded here).
 *
 * Everything is inserted inside a single transaction so that a failure part
 * way through cannot leave the app with half a recipe book.
 */
final class RecipeSeeder {

    private RecipeSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            add(db, "Cheese Omelette", "\uD83C\uDF73", "Breakfast",
                    "Beat the eggs with a pinch of salt.|Melt the butter in a non-stick pan over medium heat.|"
                            + "Pour in the eggs and cook until almost set.|Scatter the cheese over one half, fold and serve.",
                    new String[]{"Eggs", "Cheddar Cheese", "Butter", "Salt"},
                    new double[]{3, 40, 10, 1},
                    new String[]{"piece", "g", "g", "tsp"});

            add(db, "Tomato Pasta", "\uD83C\uDF5D", "Main",
                    "Boil the pasta in salted water until al dente.|Fry the garlic in olive oil for one minute.|"
                            + "Add the chopped tomatoes and simmer for 10 minutes.|Toss the drained pasta through the sauce.",
                    new String[]{"Pasta", "Tomatoes", "Garlic", "Olive Oil", "Salt"},
                    new double[]{200, 3, 2, 15, 1},
                    new String[]{"g", "piece", "piece", "ml", "tsp"});

            add(db, "Vegetable Fried Rice", "\uD83C\uDF5A", "Main",
                    "Heat the oil in a wok over high heat.|Scramble the eggs and set them aside.|"
                            + "Stir-fry the diced carrot and peas for three minutes.|Add the cooked rice, soy sauce and eggs, and toss through.",
                    new String[]{"Rice", "Eggs", "Peas", "Carrots", "Soy Sauce", "Oil"},
                    new double[]{300, 2, 80, 1, 15, 15},
                    new String[]{"g", "piece", "g", "piece", "ml", "ml"});

            add(db, "Banana Pancakes", "\uD83E\uDD5E", "Breakfast",
                    "Mash the bananas in a mixing bowl.|Whisk in the eggs, milk and sugar.|"
                            + "Fold in the flour until just combined.|Cook spoonfuls in a hot pan until bubbles form, then flip.",
                    new String[]{"Flour", "Milk", "Eggs", "Bananas", "Sugar"},
                    new double[]{150, 200, 2, 2, 30},
                    new String[]{"g", "ml", "piece", "piece", "g"});

            add(db, "Tuna Sandwich", "\uD83E\uDD6A", "Lunch",
                    "Drain the tuna and flake it into a bowl.|Mix in the mayonnaise and finely chopped onion.|"
                            + "Spread over one slice of bread, top with the second and cut in half.",
                    new String[]{"Bread", "Tuna", "Mayonnaise", "Onion"},
                    new double[]{2, 100, 20, 1},
                    new String[]{"piece", "g", "g", "piece"});

            add(db, "Scrambled Eggs on Toast", "\uD83C\uDF73", "Breakfast",
                    "Whisk the eggs with the milk.|Melt the butter in a pan over low heat.|"
                            + "Stir the eggs slowly until softly set.|Toast the bread and spoon the eggs over the top.",
                    new String[]{"Eggs", "Bread", "Butter", "Milk"},
                    new double[]{3, 2, 10, 20},
                    new String[]{"piece", "piece", "g", "ml"});

            add(db, "Crispy Potato Wedges", "\uD83C\uDF5F", "Side",
                    "Heat the oven to 200 degrees Celsius.|Cut the potatoes into wedges.|"
                            + "Toss with the oil, paprika and salt.|Bake for 35 minutes, turning once.",
                    new String[]{"Potatoes", "Oil", "Paprika", "Salt"},
                    new double[]{4, 20, 1, 1},
                    new String[]{"piece", "ml", "tsp", "tsp"});

            add(db, "Chicken Stir Fry", "\uD83E\uDD58", "Main",
                    "Slice the chicken into strips.|Fry over high heat in the oil until browned.|"
                            + "Add the sliced pepper and onion and cook for four minutes.|Stir the soy sauce through and serve.",
                    new String[]{"Chicken", "Peppers", "Onion", "Soy Sauce", "Oil"},
                    new double[]{300, 1, 1, 20, 15},
                    new String[]{"g", "piece", "piece", "ml", "ml"});

            add(db, "Lentil Soup", "\uD83C\uDF72", "Main",
                    "Soften the chopped onion, carrot and garlic in a pot.|Add the lentils and stock.|"
                            + "Simmer for 30 minutes until the lentils are soft.|Blend lightly for a thicker texture.",
                    new String[]{"Lentils", "Carrots", "Onion", "Garlic", "Vegetable Stock"},
                    new double[]{200, 2, 1, 2, 500},
                    new String[]{"g", "piece", "piece", "piece", "ml"});

            add(db, "Pap and Tomato Relish", "\uD83C\uDF5B", "Main",
                    "Boil salted water and stir in the maize meal.|Cover and steam for 30 minutes, stirring occasionally.|"
                            + "Fry the onion in the oil, then add the chopped tomatoes.|Simmer the relish for 15 minutes and serve over the pap.",
                    new String[]{"Maize Meal", "Tomatoes", "Onion", "Oil", "Salt"},
                    new double[]{250, 3, 1, 15, 1},
                    new String[]{"g", "piece", "piece", "ml", "tsp"});

            add(db, "French Toast", "\uD83C\uDF5E", "Breakfast",
                    "Beat the eggs with the milk, sugar and cinnamon.|Soak each slice of bread in the mixture.|"
                            + "Fry in a hot pan for two minutes a side until golden.",
                    new String[]{"Bread", "Eggs", "Milk", "Cinnamon", "Sugar"},
                    new double[]{4, 2, 100, 1, 20},
                    new String[]{"piece", "piece", "ml", "tsp", "g"});

            add(db, "Creamy Mushroom Pasta", "\uD83C\uDF5D", "Main",
                    "Boil the pasta until al dente.|Fry the sliced mushrooms in butter until golden.|"
                            + "Add the garlic, then pour in the cream and reduce for five minutes.|Fold the pasta through the sauce.",
                    new String[]{"Pasta", "Mushrooms", "Cream", "Garlic", "Butter"},
                    new double[]{200, 200, 150, 2, 15},
                    new String[]{"g", "g", "ml", "piece", "g"});

            add(db, "Bean and Cheese Wraps", "\uD83C\uDF2F", "Lunch",
                    "Warm the beans with the chopped onion in a pan.|Heat the tortillas briefly.|"
                            + "Spoon in the beans, cheese and sliced tomato.|Roll up tightly and serve.",
                    new String[]{"Tortillas", "Beans", "Onion", "Cheese", "Tomatoes"},
                    new double[]{2, 200, 1, 50, 1},
                    new String[]{"piece", "g", "piece", "g", "piece"});

            add(db, "Banana Smoothie", "\uD83E\uDD64", "Drink",
                    "Peel the bananas and break them into chunks.|Add the milk, yoghurt and honey to a blender.|"
                            + "Blend until smooth and pour into a glass.",
                    new String[]{"Bananas", "Milk", "Yoghurt", "Honey"},
                    new double[]{2, 250, 100, 15},
                    new String[]{"piece", "ml", "g", "ml"});

            add(db, "Garlic Butter Rice", "\uD83C\uDF5A", "Side",
                    "Rinse the rice until the water runs clear.|Fry the crushed garlic in the butter.|"
                            + "Add the rice and twice its volume of salted water.|Cover and simmer for 15 minutes.",
                    new String[]{"Rice", "Butter", "Garlic", "Salt"},
                    new double[]{250, 20, 3, 1},
                    new String[]{"g", "g", "piece", "tsp"});

            add(db, "Carrot and Coriander Soup", "\uD83C\uDF72", "Main",
                    "Chop the carrots and onion roughly.|Soften them in a pot for five minutes.|"
                            + "Add the stock and simmer for 25 minutes.|Blend smooth and stir the coriander through.",
                    new String[]{"Carrots", "Onion", "Vegetable Stock", "Coriander"},
                    new double[]{4, 1, 500, 10},
                    new String[]{"piece", "piece", "ml", "g"});

            add(db, "Beef Mince Bolognese", "\uD83C\uDF5D", "Main",
                    "Brown the mince in a deep pan.|Add the chopped onion and garlic and cook until soft.|"
                            + "Stir in the chopped tomatoes and simmer for 25 minutes.|Serve over boiled pasta.",
                    new String[]{"Beef Mince", "Tomatoes", "Onion", "Garlic", "Pasta"},
                    new double[]{400, 4, 1, 2, 250},
                    new String[]{"g", "piece", "piece", "piece", "g"});

            add(db, "Chocolate Mug Cake", "\uD83E\uDDC1", "Dessert",
                    "Mix the flour, sugar and cocoa in a large mug.|Stir in the milk and oil until smooth.|"
                            + "Microwave on high for 90 seconds.|Let it stand for a minute before eating.",
                    new String[]{"Flour", "Sugar", "Cocoa Powder", "Milk", "Oil"},
                    new double[]{40, 30, 15, 45, 15},
                    new String[]{"g", "g", "g", "ml", "ml"});

            add(db, "Egg Fried Noodles", "\uD83C\uDF5C", "Main",
                    "Cook the noodles and drain them.|Scramble the eggs in the hot oil.|"
                            + "Add the noodles and sliced spring onions.|Season with soy sauce and toss through.",
                    new String[]{"Noodles", "Eggs", "Spring Onions", "Soy Sauce", "Oil"},
                    new double[]{200, 2, 2, 15, 10},
                    new String[]{"g", "piece", "piece", "ml", "ml"});

            add(db, "Baked Cinnamon Apples", "\uD83C\uDF4E", "Dessert",
                    "Heat the oven to 180 degrees Celsius.|Core the apples and slice them thickly.|"
                            + "Toss with the sugar and cinnamon and dot with butter.|Bake for 25 minutes until soft.",
                    new String[]{"Apples", "Butter", "Sugar", "Cinnamon"},
                    new double[]{3, 20, 30, 1},
                    new String[]{"piece", "g", "g", "tsp"});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** Inserts one recipe (with its representative emoji icon) and its ingredient rows. */
    private static void add(SQLiteDatabase db, String name, String icon, String category, String steps,
                            String[] ingredientNames, double[] quantities, String[] units) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.R_NAME, name);
        recipeValues.put(DatabaseHelper.R_CATEGORY, category);
        recipeValues.put(DatabaseHelper.R_STEPS, steps);
        recipeValues.put(DatabaseHelper.R_ICON, icon);
        long recipeId = db.insert(DatabaseHelper.T_RECIPES, null, recipeValues);

        for (int i = 0; i < ingredientNames.length; i++) {
            ContentValues ing = new ContentValues();
            ing.put(DatabaseHelper.RI_RECIPE_ID, recipeId);
            ing.put(DatabaseHelper.RI_NAME, ingredientNames[i]);
            ing.put(DatabaseHelper.RI_NORMALISED, IngredientNormaliser.normalise(ingredientNames[i]));
            ing.put(DatabaseHelper.RI_QTY, quantities[i]);
            ing.put(DatabaseHelper.RI_UNIT, units[i]);
            db.insert(DatabaseHelper.T_RECIPE_ING, null, ing);
        }
    }
}
