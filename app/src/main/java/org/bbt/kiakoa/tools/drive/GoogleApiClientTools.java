package org.bbt.kiakoa.tools.drive;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Tools related to {@link GoogleApiClient}
 */
public class GoogleApiClientTools {

    /**
     * Get a well configured GoogleApiClient.Builder
     *
     * @param context a context
     * @return client builder instance
     */
    @NonNull
    public static GoogleApiClient.Builder getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER);
    }
}
