package org.bbt.kiakoa.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bbt.kiakoa.R;

/**
 * Class to access {@link SharedPreferences}
 */
public class Preferences {

    public static final String KEY_NOTIFICATION = "enable_date_return_notification";

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
}
