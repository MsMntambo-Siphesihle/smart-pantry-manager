package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richfield.smartpantry.logic.IngredientNormaliser;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLiteOpenHelper for the Smart Pantry Manager.
 *
 * Holds three tables:
 *   pantry_items        - the user's own data (full CRUD)
 *   recipes             - seeded on first run
 *   recipe_ingredients  - seeded on first run, child of recipes
 *
 * Because the database file lives on the device, everything the user adds is
 * still there the next time the app is opened (Section 3.2 of the brief).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 2;   // v2 added recipes.icon

    // ---- pantry_items -------------------------------------------------
    public static final String T_PANTRY = "pantry_items";
    public static final String P_ID = "_id";
    public static final String P_NAME = "name";
    public static final String P_NORMALISED = "normalised_name";
    public static final String P_QTY = "quantity";
    public static final String P_UNIT = "unit";
    public static final String P_EXPIRY = "expiry_date";

    // ---- recipes ------------------------------------------------------
    public static final String T_RECIPES = "recipes";
    public static final String R_ID = "_id";
    public static final String R_NAME = "name";
    public static final String R_CATEGORY = "category";
    public static final String R_STEPS = "steps";
    public static final String R_ICON = "icon";

    // ---- recipe_ingredients -------------------------------------------
    public static final String T_RECIPE_ING = "recipe_ingredients";
    public static final String RI_ID = "_id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_NAME = "name";
    public static final String RI_NORMALISED = "normalised_name";
    public static final String RI_QTY = "quantity";
    public static final String RI_UNIT = "unit";

    private static DatabaseHelper instance;

    /** Single shared instance so the database is never opened twice at once. */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " (" +
                P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                P_NAME + " TEXT NOT NULL, " +
                P_NORMALISED + " TEXT NOT NULL, " +
                P_QTY + " REAL NOT NULL, " +
                P_UNIT + " TEXT NOT NULL, " +
                P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_NAME + " TEXT NOT NULL, " +
                R_CATEGORY + " TEXT, " +
                R_STEPS + " TEXT NOT NULL, " +
                R_ICON + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPE_ING + " (" +
                RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RI_RECIPE_ID + " INTEGER NOT NULL, " +
                RI_NAME + " TEXT NOT NULL, " +
                RI_NORMALISED + " TEXT NOT NULL, " +
                RI_QTY + " REAL NOT NULL, " +
                RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + RI_RECIPE_ID + ") REFERENCES " +
                T_RECIPES + "(" + R_ID + ") ON DELETE CASCADE)");

        // Pre-load the recipe collection required by Section 2.2.
        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // =====================================================================
    // CREATE
    // =====================================================================
    public long insertPantryItem(@NonNull PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(T_PANTRY, null, toValues(item));
    }

    // =====================================================================
    // READ
    // =====================================================================
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(T_PANTRY, null, null, null, null, null, P_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    @Nullable
    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(T_PANTRY, null, P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) return readPantryItem(cursor);
        } finally {
            cursor.close();
        }
        return null;
    }

    // =====================================================================
    // UPDATE
    // =====================================================================
    public int updatePantryItem(@NonNull PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(T_PANTRY, toValues(item), P_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    // =====================================================================
    // DELETE
    // =====================================================================
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(T_PANTRY, P_ID + "=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(P_NAME, item.getName().trim());
        // The matching key is calculated once, on write, so the matcher never
        // has to re-parse text while scrolling a list.
        values.put(P_NORMALISED, IngredientNormaliser.normalise(item.getName()));
        values.put(P_QTY, item.getQuantity());
        values.put(P_UNIT, item.getUnit());
        values.put(P_EXPIRY, item.getExpiryDate());
        return values;
    }

    private PantryItem readPantryItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(P_ID)),
                c.getString(c.getColumnIndexOrThrow(P_NAME)),
                c.getString(c.getColumnIndexOrThrow(P_NORMALISED)),
                c.getDouble(c.getColumnIndexOrThrow(P_QTY)),
                c.getString(c.getColumnIndexOrThrow(P_UNIT)),
                c.getString(c.getColumnIndexOrThrow(P_EXPIRY)));
    }

    // =====================================================================
    // Recipes (read only - seeded data)
    // =====================================================================

    /** Loads every recipe together with its ingredient rows, in two queries. */
    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        Map<Long, Recipe> byId = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor rc = db.query(T_RECIPES, null, null, null, null, null, R_NAME + " ASC");
        try {
            while (rc.moveToNext()) {
                Recipe recipe = new Recipe(
                        rc.getLong(rc.getColumnIndexOrThrow(R_ID)),
                        rc.getString(rc.getColumnIndexOrThrow(R_NAME)),
                        rc.getString(rc.getColumnIndexOrThrow(R_CATEGORY)),
                        rc.getString(rc.getColumnIndexOrThrow(R_STEPS)),
                        rc.getString(rc.getColumnIndexOrThrow(R_ICON)));
                recipes.add(recipe);
                byId.put(recipe.getId(), recipe);
            }
        } finally {
            rc.close();
        }

        Cursor ic = db.query(T_RECIPE_ING, null, null, null, null, null, RI_ID + " ASC");
        try {
            while (ic.moveToNext()) {
                long recipeId = ic.getLong(ic.getColumnIndexOrThrow(RI_RECIPE_ID));
                Recipe parent = byId.get(recipeId);
                if (parent == null) continue;
                parent.addIngredient(new RecipeIngredient(
                        ic.getLong(ic.getColumnIndexOrThrow(RI_ID)),
                        recipeId,
                        ic.getString(ic.getColumnIndexOrThrow(RI_NAME)),
                        ic.getString(ic.getColumnIndexOrThrow(RI_NORMALISED)),
                        ic.getDouble(ic.getColumnIndexOrThrow(RI_QTY)),
                        ic.getString(ic.getColumnIndexOrThrow(RI_UNIT))));
            }
        } finally {
            ic.close();
        }
        return recipes;
    }

    @Nullable
    public Recipe getRecipe(long recipeId) {
        for (Recipe recipe : getAllRecipesWithIngredients()) {
            if (recipe.getId() == recipeId) return recipe;
        }
        return null;
    }

    public int countRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + T_RECIPES, null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }
}
