package org.bbt.kiakoa;

import android.app.Application;

import org.bbt.kiakoa.model.LendLists;

/**
 * Custom {@link Application} class
 */
public class LendApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LendList instance and set its context
        LendLists.getInstance().initLists(this);
    }
}
