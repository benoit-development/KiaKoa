package org.bbt.kiakoa;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.bbt.kiakoa.fragment.LoanDetails.LoanDetailsFragment;
import org.bbt.kiakoa.model.Loan;

public class LoanDetailsActivity extends AppCompatActivity {

    /**
     * EXTRA key to provide loan to display
     */
    public static final String EXTRA_LOAN = "org.bbt.kiakoa.loan";

    /**
     * For log
     */
    private static final String TAG = "LoanDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);

        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // getFragment
        LoanDetailsFragment loanDetailFragment = (LoanDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.loan_details);

        // get Loan to display
        Loan loan = getIntent().getParcelableExtra(EXTRA_LOAN);
        if (loan == null) {
            Log.i(TAG, "Loan is null. Ending activity.");
            finish();
        }

        // send loan to fragment
        loanDetailFragment.setLoan(loan);

        // title
        setTitle(R.string.loan_details);
    }
}
