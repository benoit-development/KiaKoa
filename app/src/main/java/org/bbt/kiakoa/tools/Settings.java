package org.bbt.kiakoa.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.SettingsFragment;

/**
 * Class to access settings properties
 */
public class Settings {

    /**
     * Check if return date notifications are enabled
     *
     * @param context a context
     * @return notification enabled or not
     */
    public static boolean isReturnDateNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsFragment.KEY_NOTIFICATION, context.getResources().getBoolean(R.bool.notifications_date_return_enabled_default_value));
    }

    /**
     * Check if google drive sync is enabled
     *
     * @param context a context
     * @return google drive sync enabled or not
     */
    public static boolean isGoogleDriveSyncEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsFragment.KEY_GOOGLE_DRIVE_SYNC, context.getResources().getBoolean(R.bool.sync_google_drive_default_value));
    }
}
