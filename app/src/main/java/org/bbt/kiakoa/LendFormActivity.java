package org.bbt.kiakoa;

import android.app.Activity;
import android.content.Intent;
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
import org.bbt.kiakoa.model.LendLists;

/**
 * Activity displaying form to create/update lends
 *
 * @author Benoît BOUSQUET
 */
public class LendFormActivity extends AppCompatActivity implements TextWatcher {

    public static final String EXTRA_LEND_LIST_ACTION = "org.bbt.kiakoi.LEND_LIST_ACTION";
    public static final String EXTRA_LEND_TO_UPDATE = "org.bbt.kiakoi.LEND_TO_UPDATE";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_form);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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
                break;
            case EXTRA_UPDATE_LEND:
                Log.i(TAG, "onCreate: update lend");
                lend = getIntent().getParcelableExtra(EXTRA_LEND_TO_UPDATE);
                if (lend == null) {
                    Log.i(TAG, "onCreate: no lend sent, finishing activity");
                    finish();
                } else {
                    Log.d(TAG, "onCreate: lend to update : " + lend.toJson());
                    itemEditText.setText(lend.getItem());
                }
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
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        doneMenu = menu.findItem(R.id.action_done);
        validateForm();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_done:
                Log.i(TAG, "Save action requested");
                Log.d(TAG, "Lend : " + lend.toJson());
                if (validateForm()) {
                    boolean addResult = false;
                    switch (extraAction) {
                        case EXTRA_NEW_LEND_TO:
                            addResult = LendLists.getInstance().addLendTo(lend, getBaseContext());
                            break;
                        case EXTRA_NEW_LEND_FROM:
                            addResult = LendLists.getInstance().addLendFrom(lend, getBaseContext());
                            break;
                        case EXTRA_UPDATE_LEND:
                            addResult = LendLists.getInstance().updateLend(lend, getBaseContext());
                            break;
                    }
                    if (addResult) {
                        Log.i(TAG, "Lend correctly saved");
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK,returnIntent);
                    }
                    finish();
                    return addResult;
                } else {
                    Log.i(TAG, "Lend is incorrect to be saved");
                    return false;
                }
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
    private boolean validateForm() {
        Log.i(TAG, "Form validation requested");

        // result
        boolean result;

        // check item label
        String item = itemEditText.getText().toString();
        if (item.length() == 0) {
            // item is empty
            itemEditText.setError(null);
            if (doneMenu != null) {
                doneMenu.setEnabled(false);
            }
            result = false;
        } else {
            // item is correct
            lend.setItem(item);
            if (doneMenu != null) {
                doneMenu.setEnabled(true);
            }
            result = true;
        }

        Log.i(TAG, "Form validation requested : " + ((result)?"true":"false"));
        return result;
    }
}
