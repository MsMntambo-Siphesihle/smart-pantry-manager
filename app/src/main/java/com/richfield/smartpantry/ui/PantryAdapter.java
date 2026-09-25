package com.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.IngredientIcons;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView.Adapter binding pantry rows to item_pantry.xml
 * (Section 3.1: at least one RecyclerView with a custom Adapter).
 *
 * The adapter holds no database code of its own - it simply renders the list
 * it is given and reports clicks back to the Fragment through a listener.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Lets the hosting Fragment react to taps without the adapter knowing about SQLite. */
    public interface OnItemActionListener {
        void onEditRequested(PantryItem item);
        void onDeleteRequested(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;
    private final boolean showExpiryWarnings;

    public PantryAdapter(OnItemActionListener listener, boolean showExpiryWarnings) {
        this.listener = listener;
        this.showExpiryWarnings = showExpiryWarnings;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView iconView;
        private final TextView nameView;
        private final TextView quantityView;
        private final TextView expiryView;
        private final ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.text_item_icon);
            nameView = itemView.findViewById(R.id.text_item_name);
            quantityView = itemView.findViewById(R.id.text_item_quantity);
            expiryView = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }

        void bind(PantryItem item) {
            // The icon is looked up from the normalised name, so "Eggs",
            // "egg" and "3 large eggs" all resolve to the same 🥚 badge.
            iconView.setText(IngredientIcons.getIcon(item.getNormalisedName()));
            nameView.setText(item.getName());
            quantityView.setText(item.getDisplayQuantity());

            int days = DateUtils.daysUntil(item.getExpiryDate());
            if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
                expiryView.setVisibility(View.GONE);
            } else {
                expiryView.setVisibility(View.VISIBLE);
                if (showExpiryWarnings && days < 0) {
                    expiryView.setText(itemView.getContext().getString(R.string.label_expired));
                    expiryView.setBackgroundResource(R.drawable.bg_status_missing);
                } else if (showExpiryWarnings && days <= 3) {
                    expiryView.setText(itemView.getContext()
                            .getString(R.string.label_expiring_soon, days));
                    expiryView.setBackgroundResource(R.drawable.bg_status_warning);
                } else {
                    expiryView.setText(itemView.getContext().getString(
                            R.string.label_expires_on, DateUtils.toDisplay(item.getExpiryDate())));
                    expiryView.setBackgroundResource(R.drawable.bg_status_neutral);
                }
            }

            itemView.setOnClickListener(v -> listener.onEditRequested(item));
            deleteButton.setOnClickListener(v -> listener.onDeleteRequested(item));
        }
    }
}
