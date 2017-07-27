package org.bbt.kiakoa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.bbt.kiakoa.fragment.LendDetailsFragment;

public class LendDetailsActivity extends AppCompatActivity {

    /**
     * EXTRA key to provide lend to display
     */
    public static final String EXTRA_LEND = "org.bbt.kiakoi.lend";

    /**
     * Fragment displaying lend details
     */
    private LendDetailsFragment lendDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_details);

        // getFragment
        lendDetailFragment = (LendDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.lend_detail);
    }
}
