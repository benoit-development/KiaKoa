package org.bbt.kiakoa.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Settings;

/**
 * Fragment managing settings
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String KEY_NOTIFICATION = "enable_date_return_notification";
    public static final String KEY_GOOGLE_DRIVE_SYNC = "enable_google_drive_sync";
    private static final String TAG = "SettingsFragment";
    private static final int RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE = 1234;

    /**
     * For google drive loans sync
     */
    private GoogleApiClient mGoogleApiClient;
    private SwitchPreference gDrivePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // client to connect to google api if needed
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // get Preference instance
        gDrivePreference = (SwitchPreference) getPreferenceManager().findPreference(KEY_GOOGLE_DRIVE_SYNC);
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
            case KEY_NOTIFICATION:
                boolean notificationEnabled = Settings.isReturnDateNotificationEnabled(getContext());
                Log.i(TAG, "notification enabled parameter changed : " + notificationEnabled);
                if (notificationEnabled) {
                    LoanLists.getInstance().scheduleAllLoanNotification(getContext());
                } else {
                    LoanLists.getInstance().cancelAllLoanNotificationSchedule(getContext());
                }
                break;
            case KEY_GOOGLE_DRIVE_SYNC:
                boolean gDriveEnabled = Settings.isGoogleDriveSyncEnabled(getContext());
                if (gDriveEnabled) {
                    Log.i(TAG, "Trying google drive connection");
                    mGoogleApiClient.connect();
                } else {
                    Log.i(TAG, "Trying google drive disconnection");
                    mGoogleApiClient.disconnect();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to google drive");
        Toast.makeText(getContext(), R.string.connection_google_drive_succeed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended to google drive");
        disableKeyGoogleDriveSync();
        Toast.makeText(getContext(), R.string.connection_google_drive_suspended, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed to google drive");
        if (connectionResult.hasResolution()) {
            try {
                Log.i(TAG, "Try starting resolution");
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE);
                return;
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
                disableKeyGoogleDriveSync();
            }
        } else {
            Log.i(TAG, "No resolution");
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), connectionResult.getErrorCode(), RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE).show();
            disableKeyGoogleDriveSync();
        }
        Toast.makeText(getContext(), R.string.connection_google_drive_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE:
                Log.i(TAG, "Google Drive result received");
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Google Drive result received OK");
                    mGoogleApiClient.connect();
                } else {
                    Log.i(TAG, "Google Drive result received KO");
                    disableKeyGoogleDriveSync();
                }
                break;
        }

    }

    /**
     * Disable google sync preference
     */
    private void disableKeyGoogleDriveSync() {
        gDrivePreference.setChecked(false);
    }
}
