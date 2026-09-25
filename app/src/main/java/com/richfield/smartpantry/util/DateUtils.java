package com.richfield.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Helpers for the optional expiry date on a pantry item. */
public final class DateUtils {

    private static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private static final SimpleDateFormat DISPLAY =
            new SimpleDateFormat("dd MMM yyyy", Locale.UK);

    private DateUtils() {
    }

    public static String toIso(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return ISO.format(calendar.getTime());
    }

    public static String toDisplay(String isoDate) {
        Date date = parse(isoDate);
        return date == null ? "" : DISPLAY.format(date);
    }

    /**
     * Whole days from today until the given date. Negative means already
     * expired. Returns Integer.MAX_VALUE when no date was recorded.
     */
    public static int daysUntil(String isoDate) {
        Date date = parse(isoDate);
        if (date == null) return Integer.MAX_VALUE;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long diff = date.getTime() - today.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    private static Date parse(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) return null;
        try {
            return ISO.parse(isoDate);
        } catch (ParseException e) {
            return null;
        }
    }
}
