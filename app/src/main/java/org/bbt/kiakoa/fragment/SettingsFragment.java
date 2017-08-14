package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import org.bbt.kiakoa.R;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * For log
     */
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
