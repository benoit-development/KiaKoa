package org.bbt.kiakoa;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.bbt.kiakoa.fragment.SettingsFragment;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Preferences;

/**
 * Activity displaying application settings
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * For logs
     */
    private static final String TAG = "SettingsActivity";

    /**
     * Fragment displaying settings
     */
    private SettingsFragment settingFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // fragment
        settingFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settings_fragment);

        // toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        settingFragment.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "A SharedPreference changed : " + key);
        switch (key) {
            case Preferences.KEY_NOTIFICATION:
                boolean notificationEnabled = Preferences.isReturnDateNotificationEnabled(this);
                Log.i(TAG, "notification enabled parameter changed : " + notificationEnabled);
                if (notificationEnabled) {
                    LoanLists.getInstance().scheduleAllLoanNotification(this);
                } else {
                    LoanLists.getInstance().cancelAllLoanNotificationSchedule(this);
                }
                break;
            case Preferences.KEY_GOOGLE_DRIVE_SYNC:
                if (Preferences.isGoogleDriveSyncEnabled(this)) {
                    Log.i(TAG, "Google Sync has been enabled. Set sync as needed.");
                    Preferences.setSyncNeeded(true, this);
                }
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
