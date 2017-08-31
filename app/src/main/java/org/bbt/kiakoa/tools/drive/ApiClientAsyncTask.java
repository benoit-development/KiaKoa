// Copyright 2013 Google Inc. All Rights Reserved.

package org.bbt.kiakoa.tools.drive;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.concurrent.CountDownLatch;

/**
 * An AsyncTask that maintains a connected client.
 */
abstract class ApiClientAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    /**
     * For logs
     */
    private static final String TAG = "ApiClientAsyncTask";

    /**
     * {@link GoogleApiClient} instance
     */
    private final GoogleApiClient mClient;

    /**
     * Constructor
     *
     * @param context a context
     */
    ApiClientAsyncTask(Context context) {
        GoogleApiClient.Builder builder = GoogleApiClientTools.getGoogleApiClient(context);
        mClient = builder.build();
    }

    @Override
    @SafeVarargs
    protected final Result doInBackground(Params... params) {
        Log.d(TAG, "in background");
        final CountDownLatch latch = new CountDownLatch(1);
        mClient.registerConnectionCallbacks(new ConnectionCallbacks() {
            @Override
            public void onConnectionSuspended(int cause) {
            }

            @Override
            public void onConnected(Bundle arg0) {
                latch.countDown();
            }
        });
        mClient.registerConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult arg0) {
                latch.countDown();
            }
        });
        mClient.connect();
        try {
            latch.await();
        } catch (InterruptedException e) {
            return null;
        }
        if (!mClient.isConnected()) {
            return null;
        }
        try {
            return doInBackgroundConnected(params);
        } finally {
            mClient.disconnect();
        }
    }

    /**
     * Override this method to perform a computation on a background thread, while the client is
     * connected.
     */
    @SuppressWarnings("unchecked")
    protected abstract Result doInBackgroundConnected(Params... params);

    /**
     * Gets the GoogleApiClient owned by this async task.
     */
    GoogleApiClient getGoogleApiClient() {
        return mClient;
    }
}