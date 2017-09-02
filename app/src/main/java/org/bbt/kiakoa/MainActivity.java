package org.bbt.kiakoa;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.bbt.kiakoa.dialog.ClearAllDialog;
import org.bbt.kiakoa.fragment.LoanDetails.LoanDetailsFragment;
import org.bbt.kiakoa.fragment.LoanList.AbstractLoanListFragment;
import org.bbt.kiakoa.fragment.LoanList.LoanListsPagerFragment;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Preferences;
import org.bbt.kiakoa.tools.drive.GoogleApiClientTools;
import org.bbt.kiakoa.tools.drive.LoanListsDriveFile;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoanLists.OnLoanListsChangedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * For Log
     */
    private static final String TAG = "MainActivity";

    /**
     * extra key used by notification to display a loan
     */
    public static final String EXTRA_NOTIFICATION_LOAN = "loan";

    /**
     * Used for google drive loans sync
     */
    private static final int RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE = 1234;

    /**
     * For navigation
     */
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayoutAdapter mDrawerLayoutAdapter;

    /**
     * {@link android.support.v4.app.Fragment} displaying Loan detail
     * Only exists on large screens
     */
    private LoanDetailsFragment loanDetailsFragment;

    /**
     * {@link android.support.v4.app.Fragment} displaying Loans lists
     */
    private LoanListsPagerFragment loanListsPager;

    /**
     * google api client for loans sync
     */
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find fragments
        loanListsPager = (LoanListsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.loan_pager_frag);
        loanDetailsFragment = (LoanDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.loan_details_frag);

        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLeft = (LinearLayout) findViewById(R.id.left_drawer);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        // Set the adapter for the list view
        mDrawerLayoutAdapter = new DrawerLayoutAdapter();
        mDrawerList.setAdapter(mDrawerLayoutAdapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        // Toggler
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Check if this activity is displayed because of a loan notification
        try {
            Loan notificationLoan = getIntent().getParcelableExtra(EXTRA_NOTIFICATION_LOAN);
            if (notificationLoan != null) {
                Log.i(TAG, "This activity has been launch from notification");
                Log.i(TAG, "Displaying loan : " + notificationLoan.getItem());
                displayLoanDetails(notificationLoan);
            }
        } catch (Exception e) {
            // something wrong occurred
            Log.e(TAG, "Error retrieving loan from notification : " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayoutAdapter.notifyDataSetChanged();
        LoanLists.getInstance().registerOnLoanListsChangedListener(this, TAG);
        syncLoansOnGoogleDrive();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoanLists.getInstance().unregisterOnLoanListsChangedListener(this, TAG);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Start process to connect and sync loans on Google drive
     */
    private void syncLoansOnGoogleDrive() {
        if (Preferences.isGoogleDriveSyncEnabled(this)) {
            if (Preferences.isSyncNeeded(this)) {
                Log.i(TAG, "Start GoogleDrive sync process");
                googleApiClient = GoogleApiClientTools.getGoogleApiClient(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                googleApiClient.connect();
            } else {
                Log.i(TAG, "GoogleDrive sync process not needed");
            }
        } else {
            Log.i(TAG, "GoogleDrive sync process disabled");
        }
    }

    /**
     * Method called by an {@link AbstractLoanListFragment} to
     * display a loan details in {@link LoanDetailsFragment}
     *
     * @param loan loan to display
     */
    public void displayLoanDetails(Loan loan) {
        if (loanDetailsFragment != null) {
            // update fragment with selected loan
            loanDetailsFragment.setLoan(loan);
        } else {
            if (loan != null) {
                // launch activity to display loan details
                Intent intent = new Intent(this, LoanDetailsActivity.class);
                intent.putExtra(LoanDetailsActivity.EXTRA_LOAN, loan);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        // Manage click on burger icon in toolbar
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "item clicked : " + position);

        // change the displayed Loan list
        // first position of loan lists is 1 (0 is the first header)
        switch (position) {
            case 1:
            case 2:
            case 3:
                // change displayed page in viewpager
                loanListsPager.showPage(position - 1);
                break;
            case 5:
                // launch setting activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case 6:
                Log.i(TAG, "Loans share requested");
                String title = getString(R.string.app_name);
                Intent sendIntent = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_SUBJECT, title)
                        .putExtra(Intent.EXTRA_TEXT, LoanLists.getInstance().toShareText(this))
                        .setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, title));
                break;
            case 7:
                // check if there are loan lists to be clear
                if (LoanLists.getInstance().getLoanCount() == 0) {
                    Toast.makeText(this, R.string.all_loan_lists_already_empty, Toast.LENGTH_SHORT).show();
                } else {
                    // show dialog to confirm the user desire to clear all lists
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment clearAllDialog = getSupportFragmentManager().findFragmentByTag("confirm_clear");
                    if (clearAllDialog != null) {
                        ft.remove(clearAllDialog);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog
                    ClearAllDialog newClearAllDialog = ClearAllDialog.newInstance();
                    newClearAllDialog.show(ft, "confirm_clear");
                }
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerLeft);
    }


    @Override
    public void onLoanListsChanged() {
        mDrawerLayoutAdapter.notifyDataSetChanged();
        // try saving change
        syncLoansOnGoogleDrive();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to google drive");
        LoanListsDriveFile loanListsDriveFile = new LoanListsDriveFile(googleApiClient);
        loanListsDriveFile.syncLoanLists();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended to google drive");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed to google drive");
        if (connectionResult.hasResolution()) {
            try {
                Log.i(TAG, "Try starting resolution");
                connectionResult.startResolutionForResult(this, RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE);
                return;
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "No resolution");
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE).show();
        }
        Toast.makeText(this, R.string.connection_google_drive_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESOLVE_GOOGLE_DRIVE_CONNECTION_REQUEST_CODE:
                Log.i(TAG, "Google Drive result received");
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Google Drive result received OK");
                    googleApiClient.connect();
                } else {
                    Log.i(TAG, "Google Drive result received KO");
                }
                break;
        }

    }

    /**
     * Adapter for the {@link ListView} inside the {@link DrawerLayout} of this activity
     */
    private class DrawerLayoutAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

        private final LayoutInflater inflater;

        DrawerLayoutAdapter() {
            inflater = (LayoutInflater.from(getBaseContext()));
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * 2 types possibles TYPE_ITEM and TYPE_HEADER
         *
         * @return type count
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * Return the type depending on the position
         */
        @Override
        public int getItemViewType(int position) {
            if ((position == 0) || (position == 4)) {
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            int itemViewType = getItemViewType(position);

            if (view == null) {
                holder = new ViewHolder();
                if (itemViewType == TYPE_HEADER) {

                    view = inflater.inflate(R.layout.adapter_drawer_layout_header, viewGroup, false);
                    view.setEnabled(false);
                    view.setOnClickListener(null);
                    // Creates a ViewHolderHeader
                    holder.text = view.findViewById(R.id.text);
                    // no icon by default
                    holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                } else {

                    view = inflater.inflate(R.layout.adapter_drawer_layout_item, viewGroup, false);
                    // Creates a ViewHolder
                    holder.icon = view.findViewById(R.id.icon);
                    holder.text = view.findViewById(R.id.text);
                    holder.badge = view.findViewById(R.id.badge);

                }
                view.setTag(holder);
            } else {
                // Get the ViewHolder
                holder = (ViewHolder) view.getTag();
            }

            // Bind the data efficiently with the holder.
            int iconId = 0;
            int textId = 0;
            int loanCount = 0;
            switch (position) {
                case 0:
                    textId = R.string.loan_lists;
                    // check google drive icon status
                    int gDriveIcon = 0;
                    if (Preferences.isGoogleDriveSyncEnabled(MainActivity.this)) {
                        gDriveIcon = (Preferences.isSyncNeeded(MainActivity.this)) ? R.drawable.ic_drive_red_16dp : R.drawable.ic_drive_green_16dp;
                    }
                    holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, gDriveIcon, 0);
                    break;
                case 1:
                    iconId = R.drawable.ic_lent_24dp;
                    textId = R.string.lent;
                    loanCount = LoanLists.getInstance().getLentList().size();
                    break;
                case 2:
                    iconId = R.drawable.ic_borrowed_24dp;
                    textId = R.string.borrowed;
                    loanCount = LoanLists.getInstance().getBorrowedList().size();
                    break;
                case 3:
                    iconId = R.drawable.ic_return_24dp;
                    textId = R.string.returned;
                    loanCount = LoanLists.getInstance().getReturnedList().size();
                    break;
                case 4:
                    textId = R.string.tools;
                    break;
                case 5:
                    iconId = R.drawable.ic_settings_24dp;
                    textId = R.string.settings;
                    break;
                case 6:
                    iconId = R.drawable.ic_share_24dp;
                    textId = R.string.share;
                    break;
                case 7:
                    iconId = R.drawable.ic_delete_forever_24dp;
                    textId = R.string.clear_all_loan_lists;
                    break;
            }
            if (iconId != 0) {
                holder.icon.setImageResource(iconId);
            }
            if (textId != 0) {
                holder.text.setText(textId);
            }
            if (loanCount != 0) {
                holder.badge.setText(String.valueOf(loanCount));
            }
            if (itemViewType == TYPE_ITEM) {
                holder.badge.setVisibility((loanCount == 0) ? View.GONE : View.VISIBLE);
            }

            return view;
        }

        private class ViewHolder {
            ImageView icon;
            TextView text;
            TextView badge;
        }

    }

}
