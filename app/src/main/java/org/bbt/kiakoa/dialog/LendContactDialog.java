package org.bbt.kiakoa.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Contact;

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

    /**
     * Asynchronous callback for the contacts data loader
     */
    private final LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String search = contactEditText.getText().toString();
                    Log.i(TAG, "Contact loader created. search : " + search);

                    // Define the columns to retrieve
                    String[] projectionFields = new String[]{Contacts._ID,
                            Contacts.DISPLAY_NAME,
                            Contacts.PHOTO_URI};

                    // Construct the loader
                    return new CursorLoader(getContext(),
                            Contacts.CONTENT_URI, // URI
                            projectionFields, // projection fields
                            Contacts.DISPLAY_NAME + " LIKE ?", // the selection criteria
                            new String[]{"%" + search + "%"}, // the selection args
                            Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC" // the sort order
                    );
                }

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

    /**
     * Adapter managing display of contact
     */
    private ContactAdapter contactAdapter;

    /**
     * Create a new instance of {@link LendContactDialog} with a contact
     *
     * @param contact lend contact
     */
    public static LendContactDialog newInstance(Contact contact) {
        LendContactDialog dialog = new LendContactDialog();
        Bundle args = new Bundle();
        args.putParcelable("contact", contact);
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
        Contact contact = getArguments().getParcelable("contact");
        if (contact == null) {
            contact = new Contact("");
        }
        contactEditText.setText(contact.getName());

        // Init adapter
        contactAdapter = new ContactAdapter();
        contactEditText.setAdapter(contactAdapter);

        // setup automcomplete adapter
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Read contact permission granted, attach adapter to search in contact list");

            // Listener on autocompete edit text to reload the contact loader
            contactEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.i(TAG, "Text has changed, reload contact adapter");
                    getActivity().getSupportLoaderManager().restartLoader(CONTACT_LOADER_ID, new Bundle(), contactsLoader);
                }
            });

            // Initialize the loader with a special ID and the defined callbacks from above
            getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), contactsLoader);

            // Init listener on item selected in autompletion list
            contactEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // set lend contact from user contact list
                    setLendContact(contactAdapter.getContact(i));
                }
            });
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
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setLendContact(null);
                    }
                })
                .create();

        // manually manage ok button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // set lend contact only with name
                        setLendContact(new Contact(contactEditText.getText().toString()));
                    }
                });
            }
        });


        return dialog;
    }

    /**
     * set contact then close dialog
     * @param contact new contact
     */
    private void setLendContact(@Nullable Contact contact) {

        // unset contact if it's not complete (a valid name)
        if (contact == null || (contact.getName() == null) || (contact.getName().length() == 0)) {
            Log.i(TAG, "No contact to set");
            contact = null;
        } else {
            Log.i(TAG, "new contact : " + contact.getName());
        }

        // notify listener
        if (onLendContactSetListener != null) {
            onLendContactSetListener.onContactSet(contact);
        } else {
            Log.w(TAG, "No listener to call, lend contact typed");
        }
        dismiss();
    }

    /**
     * Adapter class for contact list
     */
    private class ContactAdapter extends SimpleCursorAdapter {

        private ContactAdapter() {
            super(
                    getContext(),
                    R.layout.autocomplete_lend_contact,
                    null,
                    new String[]{Contacts.DISPLAY_NAME, Contacts.PHOTO_URI},
                    new int[]{R.id.contact_name, R.id.contact_icon},
                    0);
            setViewBinder(getViewBinder());
        }

        /**
         * To retrieve selected contact
         *
         * @param position contact position
         * @return contact (can be null)
         */
        @Nullable
        private Contact getContact(int position) {
            Object item = super.getItem(position);
            if (item instanceof Cursor) {
                Cursor cursor = (Cursor) item;
                return new Contact(
                        cursor.getLong(cursor.getColumnIndex(Contacts._ID)),
                        cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndex(Contacts.PHOTO_URI)));
            } else {
                return null;
            }
        }

        @Override
        public ViewBinder getViewBinder() {
            return new ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    String image = cursor.getString(cursor.getColumnIndex(Contacts.PHOTO_URI));
                    if ((image == null) && (view instanceof ImageView)) {
                        ((ImageView) view).setImageResource(R.drawable.ic_contact_gray_24dp);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }
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
        void onContactSet(Contact contact);
    }

}
