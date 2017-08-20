package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import org.bbt.kiakoa.R;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_NOTIFICATION = "enable_date_return_notification";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
