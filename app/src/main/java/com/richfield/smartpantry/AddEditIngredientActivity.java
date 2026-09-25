package com.richfield.smartpantry;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;

import java.util.Calendar;

/**
 * Add / Edit Ingredient screen.
 *
 * One Activity serves both jobs: it is launched with no extras to CREATE a
 * pantry item, or with EXTRA_ITEM_ID to UPDATE an existing one. The result is
 * returned to PantryFragment with setResult() so the list can refresh.
 *
 * All input validation required by Section 3.1 happens in validateForm().
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.richfield.smartpantry.EXTRA_ITEM_ID";

    private static final String[] UNITS =
            {"piece", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"};

    private TextInputLayout nameLayout, quantityLayout, unitLayout;
    private TextInputEditText nameInput, quantityInput, expiryInput;
    private AutoCompleteTextView unitInput;

    private DatabaseHelper db;
    private long itemId = -1;          // -1 means "new item"
    private String expiryIso = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = DatabaseHelper.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // The white back arrow is set in XML, so the click listener is all
        // that is needed to return to the previous screen.
        toolbar.setNavigationOnClickListener(v -> finish());

        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        unitLayout = findViewById(R.id.layout_unit);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        unitInput = findViewById(R.id.input_unit);
        expiryInput = findViewById(R.id.input_expiry);

        unitInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UNITS));
        expiryInput.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.button_clear_expiry).setOnClickListener(v -> clearExpiry());

        MaterialButton saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> saveItem());

        // Decide whether we are creating or editing, based on the Intent extra.
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            toolbar.setTitle(R.string.title_edit_ingredient);
            saveButton.setText(R.string.action_update);
            loadExistingItem();
        } else {
            toolbar.setTitle(R.string.title_add_ingredient);
        }
    }

    private void loadExistingItem() {
        PantryItem item = db.getPantryItem(itemId);
        if (item == null) {
            Toast.makeText(this, R.string.error_item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        nameInput.setText(item.getName());
        quantityInput.setText(formatQuantity(item.getQuantity()));
        unitInput.setText(item.getUnit(), false);
        expiryIso = item.getExpiryDate();
        expiryInput.setText(DateUtils.toDisplay(expiryIso));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (picker, year, month, day) -> {
            expiryIso = DateUtils.toIso(year, month, day);
            expiryInput.setText(DateUtils.toDisplay(expiryIso));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void clearExpiry() {
        expiryIso = null;
        expiryInput.setText("");
    }

    /**
     * Validates every field and shows an inline error on the offending input.
     * Returns true only when the whole form is safe to save.
     */
    private boolean validateForm() {
        boolean valid = true;

        nameLayout.setError(null);
        quantityLayout.setError(null);
        unitLayout.setError(null);

        String name = text(nameInput);
        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2) {
            nameLayout.setError(getString(R.string.error_name_too_short));
            valid = false;
        }

        String quantityText = text(quantityInput);
        if (quantityText.isEmpty()) {
            quantityLayout.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    quantityLayout.setError(getString(R.string.error_quantity_positive));
                    valid = false;
                } else if (quantity > 100000) {
                    quantityLayout.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                quantityLayout.setError(getString(R.string.error_quantity_number));
                valid = false;
            }
        }

        String unit = unitInput.getText().toString().trim();
        if (unit.isEmpty()) {
            unitLayout.setError(getString(R.string.error_unit_required));
            valid = false;
        } else if (!isKnownUnit(unit)) {
            unitLayout.setError(getString(R.string.error_unit_unknown));
            valid = false;
        }

        // An expiry date already in the past is allowed but warned about, since
        // the user may be recording something they still intend to use.
        if (expiryIso != null && DateUtils.daysUntil(expiryIso) < 0) {
            Toast.makeText(this, R.string.warning_expiry_past, Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    private boolean isKnownUnit(String unit) {
        for (String known : UNITS) {
            if (known.equalsIgnoreCase(unit)) return true;
        }
        return false;
    }

    private void saveItem() {
        if (!validateForm()) return;

        PantryItem item = new PantryItem();
        item.setId(itemId);
        item.setName(text(nameInput));
        item.setQuantity(Double.parseDouble(text(quantityInput)));
        item.setUnit(unitInput.getText().toString().trim().toLowerCase());
        item.setExpiryDate(expiryIso);

        if (itemId == -1) {
            long newId = db.insertPantryItem(item);          // CREATE
            if (newId == -1) {
                Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.message_item_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(item);                        // UPDATE
            Toast.makeText(this, R.string.message_item_updated, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK, new Intent());
        finish();
    }

    /** Shows "3" rather than "3.0" when the stored quantity is a whole number. */
    private String formatQuantity(double quantity) {
        return quantity == Math.floor(quantity)
                ? String.valueOf((long) quantity)
                : String.valueOf(quantity);
    }

    private String text(View view) {
        return ((TextInputEditText) view).getText() == null
                ? ""
                : ((TextInputEditText) view).getText().toString().trim();
    }
}
