package org.bbt.kiakoa;

import android.app.Application;
import android.util.Log;

import org.bbt.kiakoa.model.LoanLists;

/**
 * Custom {@link Application} class
 */
public class LoanApplication extends Application {

    /**
     * Tag for logs
     */
    private static final String TAG = "LoanApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LoanLists instance and set its context
        Log.i(TAG, "onCreate: init loan lists");
        LoanLists.getInstance().initLists(this);
    }
}
