package com.richfield.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Small wrapper around SharedPreferences for the Settings screen.
 * User preferences are separate from the recipe data, so they do not belong
 * in SQLite.
 */
public final class Prefs {

    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_ALMOST_THERE = "show_almost_there";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_IMPERIAL = "imperial";

    private Prefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean isExpiryAlertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public static boolean isAlmostThereEnabled(Context context) {
        return prefs(context).getBoolean(KEY_ALMOST_THERE, true);
    }

    public static void setAlmostThereEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_ALMOST_THERE, enabled).apply();
    }

    public static String getUnitSystem(Context context) {
        return prefs(context).getString(KEY_UNIT_SYSTEM, UNITS_METRIC);
    }

    public static void setUnitSystem(Context context, String system) {
        prefs(context).edit().putString(KEY_UNIT_SYSTEM, system).apply();
    }
}
