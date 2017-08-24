package org.bbt.kiakoa.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Settings;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_NOTIFICATION = "enable_date_return_notification";
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_NOTIFICATION:
                boolean notificationEnabled = Settings.isReturnDateNotificationEnabled(getContext());
                Log.i(TAG, "notification enabled parameter changed : " + notificationEnabled);
                if (notificationEnabled) {
                    LoanLists.getInstance().scheduleAllLoanNotification(getContext());
                } else {
                    LoanLists.getInstance().cancelAllLoanNotificationSchedule(getContext());
                }
                break;
        }
    }
}
