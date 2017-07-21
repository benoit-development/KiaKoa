package org.bbt.kiakoa;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Activity displaying form to create/update lends
 *
 * @author Benoît BOUSQUET
 */
public class LendFormActivity extends AppCompatActivity {

    public static final String EXTRA_LEND_LIST_ACTION = "org.bbt.kiakoi.LEND_LIST_ACTION";
    public static final int EXTRA_NEW_LEND_TO = 0;
    public static final int EXTRA_NEW_LEND_FROM = 1;
    public static final int EXTRA_UPDATE_LEND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_form);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Log.i("LendFormActivity", "onCreate: init activity");

        // determine which action is asked
        switch (getIntent().getIntExtra(EXTRA_LEND_LIST_ACTION, -1)) {
            case EXTRA_NEW_LEND_TO:
                Log.i("LendFormActivity", "onCreate: new lend to");
                break;
            case EXTRA_NEW_LEND_FROM:
                Log.i("LendFormActivity", "onCreate: new lend from");
                break;
            case EXTRA_UPDATE_LEND:
                Log.i("LendFormActivity", "onCreate: update lend");
                break;
            default:
                Log.i("LendFormActivity", "onCreate: error with extra action");
                finish();
                break;
        }
    }
}
