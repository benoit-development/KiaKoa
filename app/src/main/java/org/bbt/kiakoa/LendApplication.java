package org.bbt.kiakoa;

import android.app.Application;
import android.util.Log;

import org.bbt.kiakoa.model.LendLists;

/**
 * Custom {@link Application} class
 */
public class LendApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LendList instance and set its context
        Log.i("LendApplication", "onCreate: init lend lists");
        LendLists.getInstance().initLists(this);
    }
}
