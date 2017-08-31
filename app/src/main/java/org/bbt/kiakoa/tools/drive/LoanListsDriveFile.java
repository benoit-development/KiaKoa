package org.bbt.kiakoa.tools.drive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Preferences;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * User drive file storing loans saved on its Google Drive account
 * if user activated and accepted de feature
 * <p>
 * This class manage the life cycle of this file
 */
public class LoanListsDriveFile {

    /**
     * For logs
     */
    private static final String TAG = "LoanListsDriveFile";

    /**
     * loans json filename
     */
    private static final String LOANS_JSON_FILENAME = "loans.json";

    /**
     * A context
     */
    @NonNull
    private final Context context;

    /**
     * An active instance of {@link GoogleApiClient}
     */
    private final GoogleApiClient googleApiClient;

    /**
     * Constructor
     *
     * @param googleApiClient an active google client
     */
    public LoanListsDriveFile(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        this.context = googleApiClient.getContext();
    }

    /**
     * Get loans lists file content asynchronously
     */
    public void syncLoanLists() {
        Log.i(TAG, "Starting sync process");
        if (googleApiClient.isConnected()) {
            Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(getLoansJsonFileCallback);
        } else {
            Log.e(TAG, "Google API client not connected, should not happen");
        }
    }

    /**
     * CallBack saving file app folder
     */
    final private ResultCallback<DriveApi.DriveContentsResult> getLoansJsonFileCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {

                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "Error while trying to create new file contents");
                        return;
                    }

                    // DriveFolder appFolder = Drive.DriveApi.getAppFolder(googleApiClient);
                    DriveFolder appFolder = Drive.DriveApi.getAppFolder(googleApiClient);
                    Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, LOANS_JSON_FILENAME))
                            .build();
                    appFolder.queryChildren(googleApiClient, query).setResultCallback(checkUpdateCallback);
                }
            };

    /**
     * Callback executed when File metadata found
     */
    private final ResultCallback<DriveApi.MetadataBufferResult> checkUpdateCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(@NonNull DriveApi.MetadataBufferResult
                                             metadataBufferResult) {
                    Log.w(TAG, "Query to retrieve JSON loans file succeed");
                    MetadataBuffer metaDataBuffer = metadataBufferResult.getMetadataBuffer();
                    if (metaDataBuffer.getCount() > 0) {
                        Log.i(TAG, "JSON loans file found");
                        Metadata metaData = metadataBufferResult.getMetadataBuffer().get(0);
                        final DriveId driveId = metaData.getDriveId();
                        long lastModifiedDate = metaData.getModifiedDate().getTime();

                        // compare last import an file on google drive
                        if (lastModifiedDate > Preferences.getLastLoanListsUpdate(context)) {
                            Log.i(TAG, "File on Google Drive newer than last import");
                            new RetrieveDriveFileContentsAsyncTask(context) {
                                @Override
                                protected void onPostExecute(String result) {
                                    super.onPostExecute(result);
                                    if (result == null) {
                                        Log.e(TAG, "Error while reading from the file");
                                    }
                                    Log.i(TAG, "File contents: " + result);
                                    if (LoanLists.fromJson(result, context)) {
                                        Preferences.setLastLoanListsUpdate(System.currentTimeMillis(), context);
                                    } else {
                                        new UpdateLoansJsonFileContentAsyncTask(context).execute(driveId.asDriveFile());
                                    }
                                }
                            }.execute(driveId);
                        } else {
                            Log.i(TAG, "File on Google Drive older than last import. No need to sync.");
                            new UpdateLoansJsonFileContentAsyncTask(context).execute(driveId.asDriveFile());
                        }

                    } else {
                        Log.w(TAG, "No JSON loans file found");
                        createLoansJsonFileContent();
                    }
                    metadataBufferResult.release();
                }
            };

    /**
     * Create a new Loans json files in app folder
     */
    private void createLoansJsonFileContent() {
        Log.i(TAG, LOANS_JSON_FILENAME + " file creation requested");

        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed creating " + LOANS_JSON_FILENAME + " file : " + driveContentsResult.getStatus());
                    return;
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(LOANS_JSON_FILENAME)
                        .setMimeType("application/json")
                        .build();
                // Create a file in the app folder
                Drive.DriveApi.getAppFolder(googleApiClient)
                        .createFile(googleApiClient, changeSet, driveContentsResult.getDriveContents())
                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override
                            public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                if (driveFileResult.getStatus().isSuccess()) {
                                    Log.i(TAG, LOANS_JSON_FILENAME + " created");
                                    new UpdateLoansJsonFileContentAsyncTask(context).execute(driveFileResult.getDriveFile());
                                } else {
                                    Log.e(TAG, "Failed creating " + LOANS_JSON_FILENAME + " file : " + driveFileResult.getStatus());
                                }
                            }
                        });
            }
        });


    }

    /**
     * {@link android.os.AsyncTask} updating loans.json file with current loans
     */
    private class UpdateLoansJsonFileContentAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        /**
         * Constructor
         * @param context a context
         */
        private UpdateLoansJsonFileContentAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            Log.i(TAG, LOANS_JSON_FILENAME + " file update requested");
            DriveApi.DriveContentsResult driveContentsResult = file.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();

            if (!driveContentsResult.getStatus().isSuccess()) {
                Log.e(TAG, "Failed opening " + LOANS_JSON_FILENAME + " file : " + driveContentsResult.getStatus());
                return false;
            }

            try {
                Log.i(TAG, LOANS_JSON_FILENAME + " opened");
                String json = LoanLists.getInstance().toJson();
                Log.i(TAG, "JSON created : " + json);

                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream os = driveContents.getOutputStream();
                PrintStream printStream = new PrintStream(os);
                printStream.write(json.getBytes());
                printStream.close();
                if (driveContents.commit(googleApiClient, null).await().isSuccess()) {
                    Log.i(TAG, LOANS_JSON_FILENAME + " content updated");
                } else {
                    Log.e(TAG, "Failed updating " + LOANS_JSON_FILENAME + " content");
                }

                Preferences.setLastLoanListsUpdate(System.currentTimeMillis(), context);
                return true;

            } catch (IOException e) {
                Log.e(TAG, "Failed writing " + LOANS_JSON_FILENAME + " file : " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Log.e(TAG, "Error while editing contents");
            } else {
                Log.i(TAG, "Successfully edited contents");
                Preferences.setSyncNeeded(false, context);
            }
        }

    }

}
