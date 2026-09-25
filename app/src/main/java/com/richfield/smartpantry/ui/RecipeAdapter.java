package com.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Adapter for both recipe lists on the Suggestions screen. A
 * MatchResult with an empty "missing" list is a strict suggestion; one with a
 * single missing ingredient is an "Almost There" row, and the subtitle makes
 * the difference obvious to the user.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<MatchResult> results = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<MatchResult> newResults) {
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView iconView;
        private final TextView nameView;
        private final TextView categoryView;
        private final TextView subtitleView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.text_recipe_icon);
            nameView = itemView.findViewById(R.id.text_recipe_name);
            categoryView = itemView.findViewById(R.id.text_recipe_category);
            subtitleView = itemView.findViewById(R.id.text_recipe_subtitle);
        }

        void bind(MatchResult result) {
            Recipe recipe = result.getRecipe();
            iconView.setText(recipe.getIcon());
            nameView.setText(recipe.getName());
            categoryView.setText(recipe.getCategory());

            if (result.isFullMatch()) {
                subtitleView.setText(itemView.getContext().getString(
                        R.string.label_uses_ingredients, ingredientSummary(recipe)));
            } else {
                subtitleView.setText(result.getMissingSummary());
            }

            itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
        }

        private String ingredientSummary(Recipe recipe) {
            StringBuilder builder = new StringBuilder();
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(ingredient.getName());
            }
            return builder.toString();
        }
    }
}
