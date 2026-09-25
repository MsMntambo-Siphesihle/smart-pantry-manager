package com.richfield.smartpantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.logic.RecipeMatcher;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;
import com.richfield.smartpantry.util.IngredientIcons;

import java.util.List;

/**
 * Recipe Detail screen. It is started by an explicit Intent carrying the
 * recipe's database id, reads that recipe back from SQLite, and shows the
 * full ingredient list and method.
 *
 * Each ingredient is ticked or crossed by re-running the matcher, so the user
 * can see exactly which item is holding a recipe back.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.richfield.smartpantry.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // The white back arrow is set in XML, so the click listener is all
        // that is needed to return to the previous screen.
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Recipe recipe = db.getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar.setTitle(recipe.getName());
        ((TextView) findViewById(R.id.text_recipe_icon)).setText(recipe.getIcon());
        ((TextView) findViewById(R.id.text_recipe_name)).setText(recipe.getName());
        ((TextView) findViewById(R.id.text_recipe_category)).setText(recipe.getCategory());

        MatchResult match = RecipeMatcher.evaluateSingle(recipe, db.getAllPantryItems());
        renderIngredients(recipe, match);
        renderSteps(recipe.getStepList());

        TextView status = findViewById(R.id.text_match_status);
        if (match.isFullMatch()) {
            status.setText(R.string.detail_you_can_cook_this);
            status.setBackgroundResource(R.drawable.bg_status_ready);
        } else {
            status.setText(match.getMissingSummary());
            status.setBackgroundResource(R.drawable.bg_status_missing);
        }
    }

    private void renderIngredients(Recipe recipe, MatchResult match) {
        LinearLayout container = findViewById(R.id.container_ingredients);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            View row = inflater.inflate(R.layout.item_recipe_ingredient, container, false);
            TextView ingredientIcon = row.findViewById(R.id.text_ingredient_icon);
            TextView label = row.findViewById(R.id.text_ingredient);
            ImageView icon = row.findViewById(R.id.icon_status);

            ingredientIcon.setText(IngredientIcons.getIcon(ingredient.getNormalisedName()));
            label.setText(ingredient.getDisplayLine());
            boolean have = !match.getMissing().contains(ingredient.getName());
            icon.setImageResource(have ? R.drawable.ic_check : R.drawable.ic_close);

            container.addView(row);
        }
    }

    private void renderSteps(List<String> steps) {
        LinearLayout container = findViewById(R.id.container_steps);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_recipe_step, container, false);
            ((TextView) row.findViewById(R.id.text_step_number)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.text_step_body)).setText(steps.get(i));
            container.addView(row);
        }
    }
}
