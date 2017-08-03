package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.bbt.kiakoa.R;

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

    /**
     * contact for a lend
     */
    private AutoCompleteTextView contactEditText;

    private OnLendContactSetListener onLendContactSetListener;

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
        /*
        ContentResolver cr = getActivity().getContentResolver();
        String[] projection={ContactsContract.CommonDataKinds.Email._ID,ContactsContract.CommonDataKinds.Email.ADDRESS};
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, null, null, null);

        getActivity().startManagingCursor(cursor);
        String[] from = new String[] { ContactsContract.CommonDataKinds.Email.ADDRESS};
        int[] to = new int[] { android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to);

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[] {},
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE '%" + constraint + "%'",
                        null, null);
            }
        });

        edt_Contact.setAdapter(adapter);*/
        contactEditText.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, new String[]{"pouet", "prout"}));

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


}
