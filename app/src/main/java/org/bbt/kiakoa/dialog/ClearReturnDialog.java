package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import org.bbt.kiakoa.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to clear return list
 */
public class ClearReturnDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "ClearReturnDialog";

    /**
     * Create a new instance
     */
    public static ClearReturnDialog newInstance() {
        return new ClearReturnDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to clear returned list");
        int loanCount = LoanLists.getInstance().getReturnedList().size();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_return_24dp)
                .setTitle(R.string.returned)
                .setMessage(getString(R.string.clear_return_list_question) + " (" + getResources().getQuantityString(R.plurals.plural_item, loanCount, loanCount) + ")")
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoanLists.getInstance().clearReturnedList(getContext());
                        Toast.makeText(getContext(), R.string.return_loan_list_cleared, Toast.LENGTH_SHORT).show();
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
