package org.bbt.kiakoa;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.bbt.kiakoa.model.Lend;

/**
 * Activity displaying form to create/update lends
 *
 * @author Benoît BOUSQUET
 */
public class LendFormActivity extends AppCompatActivity implements TextWatcher {

    public static final String EXTRA_LEND_LIST_ACTION = "org.bbt.kiakoi.LEND_LIST_ACTION";
    public static final int EXTRA_NEW_LEND_TO = 0;
    public static final int EXTRA_NEW_LEND_FROM = 1;
    public static final int EXTRA_UPDATE_LEND = 2;

    /**
     * Current {@link Lend} edited
     */
    private Lend lend;

    /**
     * Tag for logs
     */
    private static final String TAG = "LendFormActivity";

    /**
     * Id of the action from LendFormActivity::EXTRA_LEND_LIST_ACTION
     * @see LendFormActivity::EXTRA_NEW_LEND_FROM
     * @see LendFormActivity::EXTRA_NEW_LEND_TO
     * @see LendFormActivity::EXTRA_UPDATE_LEND
     */
    private int extraAction;

    /**
     * itemEditText form input
     */
    private EditText itemEditText;

    /**
     * Menu to save changes
     */
    private MenuItem doneMenu;

    /**
     * Activity toolbar
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_form);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        doneMenu = toolbar.getMenu().findItem(R.id.action_done);
        setSupportActionBar(toolbar);

        // Form
        itemEditText = (EditText) findViewById(R.id.item);
        itemEditText.addTextChangedListener(this);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Log.i("LendFormActivity", "onCreate: init activity");

        // determine which action is asked
        extraAction = getIntent().getIntExtra(EXTRA_LEND_LIST_ACTION, -1);
        switch (extraAction) {
            case EXTRA_NEW_LEND_TO:
            case EXTRA_NEW_LEND_FROM:
                Log.i(TAG, "onCreate: new lend : " + extraAction);
                lend = new Lend(null);
            case EXTRA_UPDATE_LEND:
                Log.i(TAG, "onCreate: update lend");
                break;
            default:
                Log.i(TAG, "onCreate: error with extra action");
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lend_form, menu);
        doneMenu = menu.findItem(R.id.action_done);
        validateForm();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_done:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Nothing to do
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Nothing to do
    }

    @Override
    public void afterTextChanged(Editable editable) {
        validateForm();
    }

    /**
     * This method will validate form to activate the done button
     */
    private void validateForm() {
        // check edit text
        String item = itemEditText.getText().toString();
        if (item.length() == 0) {
            // item is empty
            itemEditText.setError(getString(R.string.error_item_empty));
            doneMenu.setEnabled(false);
        } else {
            // item is correct
            lend.setItem(item);
            doneMenu.setEnabled(true);
        }
    }
}
