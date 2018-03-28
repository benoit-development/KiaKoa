package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to pick an item
 */
public class LoanItemDialog extends DialogFragment implements TextView.OnEditorActionListener {

    /**
     * For log
     */
    private static final String TAG = "LoanItemDialog";
    private static final int ACTION_CREATE = 0;
    private static final int ACTION_UPDATE = 1;

    /**
     * item for a loan
     */
    private EditText itemEditText;

    /**
     * loan to create/update
     */
    private Loan loan;

    /**
     * Identify list to save
     */
    private String list;

    /**
     * Create a new instance of LoanItemDialog
     */
    public static LoanItemDialog newInstance(String loanList) {
        LoanItemDialog dialog = new LoanItemDialog();
        Bundle args = new Bundle();
        args.putInt("action", ACTION_CREATE);
        args.putString("list", loanList);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Create a new instance of LoanItemDialog with item
     *
     * @param loan loan to be updated
     */
    public static LoanItemDialog newInstance(Loan loan) {
        LoanItemDialog dialog = new LoanItemDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        args.putInt("action", ACTION_UPDATE);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_loan_item, null, false);
        itemEditText = view.findViewById(R.id.item);
        itemEditText.setOnEditorActionListener(this);

        // arguments
        int action = getArguments().getInt("action", ACTION_CREATE);
        if (action == ACTION_UPDATE) {
            // get loan to update
            loan = getArguments().getParcelable("loan");
            if (loan != null) {
                itemEditText.setText(loan.getItem());
            } else {
                Log.e(TAG, "No loan to update");
                dismiss();
            }
        } else {
            // get list to add new loan
            list = getArguments().getString("list", LoanLists.SHARED_PREFERENCES_LENT_KEY);
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
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

        // set icon and title
        int iconId;
        if (list == null) {
            iconId = R.drawable.ic_item_24dp;
        } else if (LoanLists.SHARED_PREFERENCES_LENT_KEY.equals(list)) {
            iconId = R.drawable.ic_lent_24dp;
        } else {
            iconId = R.drawable.ic_borrowed_24dp;
        }

        // set title
        if (ACTION_CREATE == action) {
            if (LoanLists.SHARED_PREFERENCES_LENT_KEY.equals(list)) {
                dialog.setTitle(R.string.new_loan);
            } else {
                dialog.setTitle(R.string.new_borrowing);
            }
        } else {
            dialog.setTitle(loan.getItem());
        }

        // manually manage ok button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveLoanItem();
                    }
                });
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return dialog;
    }

    /**
     * Save loan from item
     */
    private void saveLoanItem() {
        String item = itemEditText.getText().toString();
        if (item.length() > 0) {
            if (loan != null) {
                // update existing item
                Log.w(TAG, "update existing loan : " + item);
                loan.setItem(item);
                LoanLists.getInstance().updateLoan(loan, getContext());
            } else {
                // new item to add
                Log.i(TAG, "new item : " + item);
                Loan newLoan = new Loan(item);
                if (LoanLists.SHARED_PREFERENCES_BORROWED_KEY.equals(list)) {
                    LoanLists.getInstance().saveBorrowed(newLoan, getContext());
                } else {
                    LoanLists.getInstance().saveLent(newLoan, getContext());
                }
            }
            dismiss();
        } else {
            Log.i(TAG, "item can't be empty");
            Toast.makeText(getContext(), R.string.label_cant_be_blank, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        saveLoanItem();
        dismiss();
        return true;
    }
}
