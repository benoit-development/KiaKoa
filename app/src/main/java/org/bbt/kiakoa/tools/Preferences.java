package org.bbt.kiakoa.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import org.bbt.kiakoa.R;

/**
 * Class to access {@link SharedPreferences}
 */
public class Preferences {

    public static final String KEY_NOTIFICATION = "enable_date_return_notification";
    private static final String KEY_GOOGLE_DRIVE_SYNC = "enable_google_drive_sync";
    private static final String KEY_LAST_LOAN_LISTS_UPDATE = "last_loan_lists_update";


    /**
     * Check if return date notifications are enabled
     *
     * @param context a context
     * @return notification enabled or not
     */
    public static boolean isReturnDateNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_NOTIFICATION, context.getResources().getBoolean(R.bool.notifications_date_return_enabled_default_value));
    }

    /**
     * Check if google drive sync is enabled
     *
     * @param context a context
     * @return google drive sync enabled or not
     */
    public static boolean isGoogleDriveSyncEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_GOOGLE_DRIVE_SYNC, context.getResources().getBoolean(R.bool.sync_google_drive_default_value));
    }

    /**
     * Date of last {@link org.bbt.kiakoa.model.LoanLists} instance update
     *
     * @param context a context
     * @return last sync date or -1 if never set
     */
    public static long getLastLoanListsUpdate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(KEY_LAST_LOAN_LISTS_UPDATE, -1);
    }

    /**
     * setter on date of last {@link org.bbt.kiakoa.model.LoanLists} instance update
     *
     * @param lastDate date to set
     * @param context a context
     */
    public static void setLastLoanListsUpdate(long lastDate, Context context) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(KEY_LAST_LOAN_LISTS_UPDATE, lastDate);
        editor.apply();

    }
}
