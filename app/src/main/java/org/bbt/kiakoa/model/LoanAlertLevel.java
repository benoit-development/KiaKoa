package org.bbt.kiakoa.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.SettingsFragment;

/**
 * Enum for alert levels
 */
public enum LoanAlertLevel {
    NONE,
    YELLOW,
    RED;

    /**
     * Get color to apply depending to alert level
     *
     * @return color resource id or 0 for {@link LoanAlertLevel#NONE} status
     */
    public int getColorLevel() {
        int result = 0;
        switch (this) {
            case YELLOW:
                result = R.color.alertYellowText;
                break;
            case RED:
                result = R.color.alertRedText;
                break;
        }
        return result;
    }

    /**
     * Get yellow alert level in days
     *
     * @param context context to access {@link android.content.SharedPreferences}
     * @return yellow level value
     */
    public static int getYellowLevel(Context context) {
        return getLevel(context, SettingsFragment.KEY_YELLOW_ALERT, R.integer.yellow_alert_default_value);
    }

    /**
     * Get red alert level in days
     *
     * @param context context to access {@link android.content.SharedPreferences}
     * @return red level value
     */
    public static int getRedLevel(Context context) {
        return getLevel(context, SettingsFragment.KEY_RED_ALERT, R.integer.red_alert_default_value);
    }

    /**
     * Get a level value from context
     * @param context a context
     * @param key {@link SharedPreferences} key
     * @param defaultValueId default value resource id
     * @return level in days
     */
    private static int getLevel(Context context, String key, int defaultValueId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultValue = context.getResources().getInteger(defaultValueId);
        try {
            return Integer.valueOf(prefs.getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * check if alert are enabled
     *
     * @param context a context
     * @return is alert active
     */
    public static boolean isAlertActive(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean defaultValue = context.getResources().getBoolean(R.bool.alert_enabled_default_value);
        try {
            return prefs.getBoolean(SettingsFragment.KEY_ENABLE_ALERTS, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
