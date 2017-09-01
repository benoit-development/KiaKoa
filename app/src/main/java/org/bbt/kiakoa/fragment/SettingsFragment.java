package org.bbt.kiakoa.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Preferences;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "A SharedPreference changed : " + key);
        switch (key) {
            case Preferences.KEY_NOTIFICATION:
                boolean notificationEnabled = Preferences.isReturnDateNotificationEnabled(getActivity());
                Log.i(TAG, "notification enabled parameter changed : " + notificationEnabled);
                if (notificationEnabled) {
                    LoanLists.getInstance().scheduleAllLoanNotification(getActivity());
                } else {
                    LoanLists.getInstance().cancelAllLoanNotificationSchedule(getActivity());
                }
                break;
            case Preferences.KEY_GOOGLE_DRIVE_SYNC:
                if (Preferences.isGoogleDriveSyncEnabled(getActivity())) {
                    Log.i(TAG, "Google Sync has been enabled. Set sync as needed.");
                    Preferences.setSyncNeeded(true, getActivity());
                }
                break;
        }
    }
}
