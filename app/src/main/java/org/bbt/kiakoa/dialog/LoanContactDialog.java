package org.bbt.kiakoa.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to add a contact to the loan
 */
public class LoanContactDialog extends DialogFragment implements TextView.OnEditorActionListener {

    /**
     * For log
     */
    private static final String TAG = "LoanContactDialog";

    /**
     * Request code to pick a contact
     */
    private static final int PICK_CONTACT_REQUEST_CODE = 987;

    /**
     * contact for a loan
     */
    private AutoCompleteTextView contactEditText;

    /**
     * Instance of the current contact in loan
     */
    private Contact currentContact;

    /**
     * {@link Loan} to update
     */
    private Loan loan;

    /**
     * Create a new instance of {@link LoanContactDialog} with a contact
     *
     * @param loan loan
     */
    public static LoanContactDialog newInstance(Loan loan) {
        LoanContactDialog dialog = new LoanContactDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_loan_contact, null, false);

        // custom autocomplete field
        contactEditText = view.findViewById(R.id.contact);
        loan = getArguments().getParcelable("loan");
        if (loan != null) {
            currentContact = loan.getContact();
            if (currentContact == null) {
                currentContact = new Contact("");
            }
        } else {
            Log.e(TAG, "loan not found, should not happen");
            dismiss();
        }
        contactEditText.setText(currentContact.getName());
        contactEditText.setOnEditorActionListener(this);

        // pick contact button
        View pickContactView = view.findViewById(R.id.pick_contact);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "READ_CONTACT permission granted");
            pickContactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "User choose to pick a contact from its contact list");
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST_CODE);
                }
            });
        } else {
            Log.i(TAG, "READ_CONTACT permission not granted");
            pickContactView.setVisibility(View.GONE);
        }

        // clear button
        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactEditText.setText("");
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_contact_24dp)
                .setTitle(R.string.contact)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // set loan contact only with name
                        String newName = contactEditText.getText().toString();
                        if (!currentContact.getName().equals(newName)) {
                            setLoanContact(new Contact(newName));
                        } else {
                            setLoanContact(currentContact);
                        }
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
                        setLoanContact(null);
                    }
                })
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT_REQUEST_CODE:
                Log.i(TAG, "Result of contact pick received");
                Contact contact = null;
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            Cursor cursor = null;
                            try {
                                cursor = getContext().getContentResolver().query(uri,
                                        new String[]{
                                                ContactsContract.Contacts._ID,
                                                ContactsContract.Contacts.DISPLAY_NAME,
                                                ContactsContract.Contacts.PHOTO_URI
                                        },
                                        null,
                                        null,
                                        null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    contact = new Contact(
                                            cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
                                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                                    );
                                }
                            } finally {
                                if ((cursor != null) && (!cursor.isClosed()))
                                    cursor.close();
                            }
                        }
                    }
                }

                // check if contact is valid
                if ((contact != null) && (contact.isValid())) {
                    Log.i(TAG, "Contact successfully picked");
                    setLoanContact(contact);
                } else {
                    Log.w(TAG, "Failed picking contact");
                }
                break;
        }
    }

    /**
     * set contact then close dialog
     *
     * @param contact new loan contact
     */
    private void setLoanContact(Contact contact) {

        // unset contact if it's not complete (a valid name)
        if (contact == null || (contact.getName().length() == 0)) {
            Log.i(TAG, "No contact to set");
            contact = null;
        } else {
            Log.i(TAG, "new contact : " + contact.getName());
        }

        // notify listener
        if (loan != null) {
            loan.setContact(contact);
            LoanLists.getInstance().updateLoan(loan, getContext());
        } else {
            Log.w(TAG, "No loan to update");
        }
        dismiss();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Log.i(TAG, "User clocked on actionDone keyboard button");
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            }
            String newContactName = contactEditText.getText().toString();
            if ((currentContact == null) || (!newContactName.equals(currentContact.getName()))) {
                currentContact = new Contact(newContactName);
            }
            setLoanContact(currentContact);

            return true;
        }
        return false;
    }
}
