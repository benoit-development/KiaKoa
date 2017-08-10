package org.bbt.kiakoa.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanAlertLevel;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * For log
     */
    private static final String TAG = "SettingsFragment";
    public static final String KEY_ENABLE_ALERTS = "enable_alert";
    public static final String KEY_YELLOW_ALERT = "yellow_alert";
    public static final String KEY_RED_ALERT = "red_alert";

    /**
     * preference context
     */
    private Context context;
    private SwitchPreference enableAlert;
    private EditTextPreference yellowAlert;
    private EditTextPreference redAlert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // theme to manage enable/disable state of the preferences
        context = getPreferenceScreen().getContext();

        // get preferences
        enableAlert = (SwitchPreference) findPreference("enable_alert");
        yellowAlert = (EditTextPreference) findPreference(KEY_YELLOW_ALERT);
        redAlert = (EditTextPreference) findPreference(KEY_RED_ALERT);
    }

    /**
     * Update summaries depending on preference values
     */
    private void updateSummaries() {
        // yellow alert
        int yellowValue = Integer.valueOf(yellowAlert.getText());
        yellowAlert.setSummary(context.getResources().getQuantityString(R.plurals.plural_day, yellowValue, yellowValue));

        // red alert
        int redValue = Integer.valueOf(redAlert.getText());
        redAlert.setSummary(context.getResources().getQuantityString(R.plurals.plural_day, redValue, redValue));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummaries();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case KEY_ENABLE_ALERTS:
                boolean enableAlertChecked = enableAlert.isChecked();
                yellowAlert.setEnabled(enableAlertChecked);
                redAlert.setEnabled(enableAlertChecked);
                Log.i(TAG, "warning and red alert enabled : " + enableAlertChecked);
                break;
            case KEY_YELLOW_ALERT:
                try {
                    String value = sharedPreferences.getString(key, String.valueOf(getResources().getInteger(R.integer.yellow_alert_default_value)));
                    int yellowValue = Integer.valueOf(value);
                    int redValue = Integer.valueOf(redAlert.getText());
                    if (yellowValue >= redValue) {
                        yellowValue = redValue - 1;
                        yellowAlert.setText(String.valueOf(yellowValue));
                        Log.w(TAG, "yellow alert can't be greater than red alert");
                        Toast.makeText(context, R.string.yellow_greater_than_red_warning, Toast.LENGTH_SHORT).show();
                    }
                    updateSummaries();
                } catch (NumberFormatException e) {
                    yellowAlert.setText(String.valueOf(LoanAlertLevel.getYellowLevel(context)));
                    Log.e(TAG, "Format exception. Should not happen because of type numer on this preference.");
                    Log.e(TAG, "Message : " + e.getMessage());
                }
                break;
            case KEY_RED_ALERT:
                try {
                    int yellowValue = Integer.valueOf(yellowAlert.getText());
                    int redValue = Integer.valueOf(sharedPreferences.getString(key, String.valueOf(getResources().getInteger(R.integer.red_alert_default_value))));
                    if (yellowValue >= redValue) {
                        redValue = yellowValue + 1;
                        redAlert.setText(String.valueOf(redValue));
                        Log.w(TAG, "red alert can't be lower than yellow alert");
                        Toast.makeText(context, R.string.red_lower_than_yellow_warning, Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    redAlert.setText(String.valueOf(LoanAlertLevel.getRedLevel(context)));
                    Log.e(TAG, "Format exception. Should not happen because of type numer on this preference.");
                    Log.e(TAG, "Message : " + e.getMessage());
                }
                break;
        }
        updateSummaries();
    }
}
