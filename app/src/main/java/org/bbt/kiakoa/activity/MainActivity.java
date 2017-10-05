package org.bbt.kiakoa.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.ClearAllDialog;
import org.bbt.kiakoa.fragment.LoanDetails.LoanDetailsFragment;
import org.bbt.kiakoa.fragment.LoanList.AbstractLoanListFragment;
import org.bbt.kiakoa.fragment.LoanList.LoanListsPagerFragment;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoanLists.OnLoanListsChangedListener {

    /**
     * For Log
     */
    private static final String TAG = "MainActivity";

    /**
     * extra key used by notification to display a loan
     */
    public static final String EXTRA_LOAN = "loan";

    /**
     * Action to create a new loan
     */
    public static final String ACTION_NEW_LENT = "org.bbt.kiakoa.new.lent";

    /**
     * For navigation
     */
    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerLeft;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find fragments
        loanListsPager = (LoanListsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.loan_pager_frag);
        loanDetailsFragment = (LoanDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.loan_details_frag);

        // toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLeft = findViewById(R.id.left_drawer);
        ListView mDrawerList = findViewById(R.id.left_drawer_list);

        // Set the adapter for the list view
        mDrawerLayoutAdapter = new DrawerLayoutAdapter(this);
        mDrawerList.setAdapter(mDrawerLayoutAdapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        // Toggler
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Check if this activity is displayed because of a loan notification
        try {
            Loan notificationLoan = getIntent().getParcelableExtra(EXTRA_LOAN);
            if (notificationLoan != null) {
                Log.i(TAG, "This activity has been launch from notification or widget");
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
            case 2:
            case 3:
            case 4:
                // change displayed page in viewpager
                loanListsPager.showPage(position - 2);
                break;
            case 5:
                // launch setting activity
                mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, null, 0, 0) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Log.i(TAG, "DrawerLayout closed, launching setting activity");
                        super.onDrawerClosed(drawerView);
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        mDrawerLayout.removeDrawerListener(this);
                    }
                });
                break;
            case 6:
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
            case 7:
                // launch setting activity
                mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, null, 0, 0) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Log.i(TAG, "DrawerLayout closed, launching about activity");
                        super.onDrawerClosed(drawerView);
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        mDrawerLayout.removeDrawerListener(this);
                    }
                });
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerLeft);
    }


    @Override
    public void onLoanListsChanged() {
        mDrawerLayoutAdapter.notifyDataSetChanged();
    }

}
