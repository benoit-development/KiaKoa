package org.bbt.kiakoa.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.bbt.kiakoa.R;

import java.util.ArrayList;

/**
 * Dialog used to add a contact to the lend
 */
public class LendContactDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "LendContactDialog";
    private static final int ACTION_CREATE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int CONTACT_LOADER_ID = 2;

    /**
     * contact for a lend
     */
    private AutoCompleteTextView contactEditText;

    private OnLendContactSetListener onLendContactSetListener;

    // Defines the asynchronous callback for the contacts data loader
    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                // Create and return the actual cursor loader for the contacts data
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Log.i(TAG, "Contact loader created");
                    // Define the columns to retrieve
                    String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts.PHOTO_URI};
                    // Construct the loader
                    CursorLoader cursorLoader = new CursorLoader(getContext(),
                            ContactsContract.Contacts.CONTENT_URI, // URI
                            projectionFields, // projection fields
                            null, // the selection criteria
                            null, // the selection args
                            null // the sort order
                    );
                    // Return the loader for use
                    return cursorLoader;
                };

                // When the system finishes retrieving the Cursor through the CursorLoader,
                // a call to the onLoadFinished() method takes place.
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    // The swapCursor() method assigns the new Cursor to the adapter
                    contactAdapter.swapCursor(cursor);
                }

                // This method is triggered when the loader is being reset
                // and the loader data is no longer available. Called if the data
                // in the provider changes and the Cursor becomes stale.
                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    // Clear the Cursor we were using with another call to the swapCursor()
                    contactAdapter.swapCursor(null);
                }
            };
    private SimpleCursorAdapter contactAdapter;

    /**
     * Create a new instance of {@link LendContactDialog} with a contact
     *
     * @param contact lend contact
     */
    public static LendContactDialog newInstance(String contact) {
        LendContactDialog dialog = new LendContactDialog();
        Bundle args = new Bundle();
        args.putString("contact", contact);
        args.putInt("action", ACTION_UPDATE);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lend_contact, null, false);
        contactEditText = view.findViewById(R.id.contact);
        String contact = getArguments().getString("contact", "");
        contactEditText.setText(contact);

        // setup automcomplete adapter
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Read contact permission granted, attache adapter to search in contact list");
            // Column data from cursor to bind views from
            String[] uiBindFrom = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI};
            // View IDs which will have the respective column data inserted
            int[] uiBindTo = {R.id.contact_name, R.id.contact_icon};
            // Create the simple cursor adapter to use for our list
            // specifying the template to inflate (item_contact),
            contactAdapter = new SimpleCursorAdapter(
                    getContext(),
                    R.layout.autocomplete_lend_contact,
                    null,
                    uiBindFrom,
                    uiBindTo,
                    0);
            contactEditText.setAdapter(contactAdapter);

            // Initialize the loader with a special ID and the defined callbacks from above
            getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(),contactsLoader);
        }


        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle((ACTION_CREATE == getArguments().getInt("action", ACTION_CREATE)) ? R.string.new_lend : R.string.contact)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        // manually
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String contact = contactEditText.getText().toString();
                        if (onLendContactSetListener != null) {
                            Log.i(TAG, "new contact : " + contact);
                            onLendContactSetListener.onContactSet(contact);
                        } else {
                            Log.w(TAG, "No listener to call, lend contact typed");
                        }
                        dismiss();
                    }
                });
            }
        });


        return dialog;
    }

    /**
     * Set listener from Lend contact typed
     *
     * @param onLendContactSetListener listener
     */
    public void setOnLendContactSetListener(OnLendContactSetListener onLendContactSetListener) {
        this.onLendContactSetListener = onLendContactSetListener;
    }

    /**
     * Listener interference called when lend contact is set
     */
    public interface OnLendContactSetListener {
        /**
         * Called when contact has been set
         *
         * @param contact new contact
         */
        void onContactSet(String contact);
    }

    /**
     * Adapter serving in
     */
    private class ContactAdapter extends BaseAdapter {

        private ArrayList<String> contactList = new ArrayList<>();

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public Object getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }

        class ViewHolder {
            TextView text;
        }
    }

}
