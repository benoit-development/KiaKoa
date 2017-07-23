package org.bbt.kiakoa;

import android.app.Application;
import android.util.Log;

import org.bbt.kiakoa.model.LendLists;

/**
 * Custom {@link Application} class
 */
public class LendApplication extends Application {

    /**
     * Tag for logs
     */
    private static final String TAG = "LendApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LendList instance and set its context
        Log.i(TAG, "onCreate: init lend lists");
        LendLists.getInstance().initLists(this);
    }
}
