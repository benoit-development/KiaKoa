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
import android.widget.EditText;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to pick an item
 */
public class LoanItemDialog extends DialogFragment {

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

        // arguments
        int action = getArguments().getInt("action", ACTION_CREATE);
        String list = null;
        Loan loan = null;
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
            list = getArguments().getString("list", LoanLists.SHARED_PREFERENCES_LENT_ID);
        }

        // clear button
        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEditText.setText("");
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle((ACTION_CREATE == action) ? R.string.new_loan : R.string.item)
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

        // manually manage ok button
        final Loan finalLoan = loan;
        final String finalList = list;
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item = itemEditText.getText().toString();
                        if (item.length() > 0) {
                            if (finalLoan != null) {
                                // update existing item
                                Log.w(TAG, "update existing loan : " + item);
                                finalLoan.setItem(item);
                                LoanLists.getInstance().updateLoan(finalLoan, getContext());
                            } else {
                                // new item to add
                                Log.i(TAG, "new item : " + item);
                                Loan newLoan = new Loan(item);
                                if (LoanLists.SHARED_PREFERENCES_BORROWED_ID.equals(finalList)) {
                                    LoanLists.getInstance().addBorrowed(newLoan, getContext());
                                } else {
                                    LoanLists.getInstance().addLent(newLoan, getContext());
                                }
                            }
                            dismiss();
                        } else {
                            Log.i(TAG, "item can't be empty");
                            Toast.makeText(getContext(), R.string.item_cant_be_blank, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        return dialog;
    }
}
