package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.richfield.smartpantry.AddEditIngredientActivity;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.Prefs;

import java.util.List;

/**
 * Pantry List screen - the READ and DELETE half of CRUD.
 *
 * The list is reloaded in onResume() so that anything created or edited on
 * AddEditIngredientActivity is visible the moment the user comes back, which
 * is the Activity/Fragment lifecycle doing the work rather than a manual
 * refresh button.
 */
public class PantryFragment extends Fragment implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView countView;

    private ActivityResultLauncher<Intent> editLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        // Modern replacement for startActivityForResult - the result tells us
        // the pantry changed so the list is refreshed.
        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> loadPantry());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_pantry);
        emptyView = view.findViewById(R.id.text_empty_pantry);
        countView = view.findViewById(R.id.text_pantry_count);

        adapter = new PantryAdapter(this, Prefs.isExpiryAlertsEnabled(requireContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_item);
        fab.setOnClickListener(v -> {
            // CREATE - no id extra means "new item".
            Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
            editLauncher.launch(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);

        boolean empty = items.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        countView.setText(getString(R.string.label_pantry_count, items.size()));
    }

    @Override
    public void onEditRequested(PantryItem item) {
        // UPDATE - passing the row id through the Intent tells the next screen
        // to load and edit that record instead of creating a new one.
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        editLauncher.launch(intent);
    }

    @Override
    public void onDeleteRequested(PantryItem item) {
        // DELETE - always confirmed first so a single tap cannot destroy data.
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, item.getName()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    Toast.makeText(requireContext(), R.string.message_item_deleted,
                            Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .show();
    }
}
