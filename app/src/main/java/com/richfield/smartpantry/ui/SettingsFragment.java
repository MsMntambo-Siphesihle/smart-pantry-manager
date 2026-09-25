package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.util.Prefs;

/**
 * Settings screen (the fifth screen required by Section 3.1).
 * Preferences are written to SharedPreferences the moment they are changed.
 */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialSwitch expirySwitch = view.findViewById(R.id.switch_expiry_alerts);
        expirySwitch.setChecked(Prefs.isExpiryAlertsEnabled(requireContext()));
        expirySwitch.setOnCheckedChangeListener((button, checked) ->
                Prefs.setExpiryAlertsEnabled(requireContext(), checked));

        MaterialSwitch almostSwitch = view.findViewById(R.id.switch_almost_there);
        almostSwitch.setChecked(Prefs.isAlmostThereEnabled(requireContext()));
        almostSwitch.setOnCheckedChangeListener((button, checked) ->
                Prefs.setAlmostThereEnabled(requireContext(), checked));

        RadioGroup unitGroup = view.findViewById(R.id.group_units);
        RadioButton metric = view.findViewById(R.id.radio_metric);
        RadioButton imperial = view.findViewById(R.id.radio_imperial);

        if (Prefs.UNITS_IMPERIAL.equals(Prefs.getUnitSystem(requireContext()))) {
            imperial.setChecked(true);
        } else {
            metric.setChecked(true);
        }

        unitGroup.setOnCheckedChangeListener((group, checkedId) ->
                Prefs.setUnitSystem(requireContext(),
                        checkedId == R.id.radio_imperial ? Prefs.UNITS_IMPERIAL : Prefs.UNITS_METRIC));

        // Small database summary, handy when demonstrating persistence on video.
        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        ((TextView) view.findViewById(R.id.text_db_summary)).setText(
                getString(R.string.settings_db_summary,
                        db.countRecipes(), db.getAllPantryItems().size()));
    }
}
