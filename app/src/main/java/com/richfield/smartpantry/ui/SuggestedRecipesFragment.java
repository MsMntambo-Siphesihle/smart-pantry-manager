package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.RecipeDetailActivity;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.logic.RecipeMatcher;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.util.Prefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggested Recipes screen.
 *
 * Runs the strict-matching rule (Section 2.3) against the current pantry and
 * lists ONLY the recipes that can be cooked right now. The optional
 * "Almost There" list is rendered in a visually separate section below, and
 * can be switched off entirely from Settings.
 */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;

    private RecyclerView strictList;
    private RecyclerView almostList;
    private TextView emptyView;
    private TextView strictHeader;
    private TextView almostHeader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        strictList = view.findViewById(R.id.recycler_suggestions);
        almostList = view.findViewById(R.id.recycler_almost);
        emptyView = view.findViewById(R.id.text_empty_suggestions);
        strictHeader = view.findViewById(R.id.text_header_suggestions);
        almostHeader = view.findViewById(R.id.text_header_almost);

        strictAdapter = new RecipeAdapter(this);
        almostAdapter = new RecipeAdapter(this);

        strictList.setLayoutManager(new LinearLayoutManager(requireContext()));
        strictList.setAdapter(strictAdapter);
        strictList.setNestedScrollingEnabled(false);

        almostList.setLayoutManager(new LinearLayoutManager(requireContext()));
        almostList.setAdapter(almostAdapter);
        almostList.setNestedScrollingEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSuggestions();   // recalculated every time the tab is opened
    }

    private void refreshSuggestions() {
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipesWithIngredients();

        // ---- strict matches: nothing missing at all -----------------------
        List<Recipe> strict = RecipeMatcher.findStrictMatches(allRecipes, pantry);
        List<MatchResult> strictResults = new ArrayList<>();
        for (Recipe recipe : strict) {
            strictResults.add(new MatchResult(recipe));   // empty missing list
        }
        strictAdapter.setResults(strictResults);

        boolean noSuggestions = strictResults.isEmpty();
        emptyView.setVisibility(noSuggestions ? View.VISIBLE : View.GONE);
        strictList.setVisibility(noSuggestions ? View.GONE : View.VISIBLE);
        strictHeader.setText(getString(R.string.header_you_can_cook, strictResults.size()));

        // Friendly message instead of a blank screen (Section 2.2).
        if (noSuggestions) {
            emptyView.setText(pantry.isEmpty()
                    ? getString(R.string.empty_pantry_no_recipes)
                    : getString(R.string.empty_no_matches));
        }

        // ---- optional "Almost There" section (bonus, Section 8) -----------
        if (Prefs.isAlmostThereEnabled(requireContext())) {
            List<MatchResult> almost = RecipeMatcher.findAlmostThere(allRecipes, pantry);
            almostAdapter.setResults(almost);
            boolean show = !almost.isEmpty();
            almostHeader.setVisibility(show ? View.VISIBLE : View.GONE);
            almostList.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
            almostHeader.setVisibility(View.GONE);
            almostList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        // Explicit Intent carrying the recipe id to the detail screen.
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
