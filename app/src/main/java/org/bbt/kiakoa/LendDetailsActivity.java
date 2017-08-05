package org.bbt.kiakoa;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.bbt.kiakoa.fragment.LendDetailsFragment;
import org.bbt.kiakoa.model.Lend;

public class LendDetailsActivity extends AppCompatActivity {

    /**
     * EXTRA key to provide lend to display
     */
    public static final String EXTRA_LEND = "org.bbt.kiakoa.lend";

    /**
     * For log
     */
    private static final String TAG = "LendDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_details);

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
        LendDetailsFragment lendDetailFragment = (LendDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.lend_detail);

        // get Lend to display
        Lend lend = getIntent().getParcelableExtra(EXTRA_LEND);
        if (lend == null) {
            Log.i(TAG, "Lend is null. Ending activity.");
            finish();
        }

        // send lend to fragment
        lendDetailFragment.setLend(lend);

        // title
        setTitle(R.string.lend_details);
    }
}
