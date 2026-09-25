package com.richfield.smartpantry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.richfield.smartpantry.ui.PantryFragment;
import com.richfield.smartpantry.ui.SettingsFragment;
import com.richfield.smartpantry.ui.SuggestedRecipesFragment;

/**
 * Single host Activity. It holds the bottom navigation bar (the working
 * navigation element required by Section 3.1) and swaps between the three
 * top-level fragments: Pantry, Suggestions and Settings.
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);

        // Only load the default tab on a cold start; on rotation the
        // FragmentManager restores the fragment that was already showing.
        if (savedInstanceState == null) {
            showFragment(new PantryFragment(), getString(R.string.title_pantry));
        }
    }

    private boolean onNavItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_pantry) {
            showFragment(new PantryFragment(), getString(R.string.title_pantry));
            return true;
        } else if (id == R.id.nav_suggestions) {
            showFragment(new SuggestedRecipesFragment(), getString(R.string.title_suggestions));
            return true;
        } else if (id == R.id.nav_settings) {
            showFragment(new SettingsFragment(), getString(R.string.title_settings));
            return true;
        }
        return false;
    }

    private void showFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        toolbar.setTitle(title);
    }
}
