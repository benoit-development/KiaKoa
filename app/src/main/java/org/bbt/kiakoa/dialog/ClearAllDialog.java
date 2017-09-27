package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import org.bbt.kiakoa.activity.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to clear all lists
 */
public class ClearAllDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "ClearAllDialog";

    /**
     * Create a new instance of ClearAllDialog
     */
    public static ClearAllDialog newInstance() {
        return new ClearAllDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to clear all lists");
        int loanCount = LoanLists.getInstance().getLoanCount();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_delete_forever_24dp)
                .setTitle(R.string.loan_lists)
                .setMessage(getString(R.string.clear_all_lists_question) + " (" + getResources().getQuantityString(R.plurals.plural_item, loanCount, loanCount) + ")")
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoanLists.getInstance().clearLists(getContext());
                        Toast.makeText(getContext(), R.string.all_loan_lists_cleared, Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).displayLoanDetails(null);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();

    }
}
