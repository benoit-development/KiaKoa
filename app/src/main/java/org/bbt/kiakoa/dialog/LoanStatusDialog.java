package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to pick a status
 */
public class LoanStatusDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "LoanStatusDialog";

    /**
     * Create a new instance of {@link LoanStatusDialog}
     *
     * @param loan loan to update
     */
    public static LoanStatusDialog newInstance(Loan loan) {
        LoanStatusDialog dialog = new LoanStatusDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Loan loan = getArguments().getParcelable("loan");
        if (loan == null) {
            Log.e(TAG, "No loan found, should not happen");
            dismiss();
        }

        //noinspection ConstantConditions
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.status)
                .setSingleChoiceItems(R.array.status_list, loan.getStatus().getIndex(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoanLists loanLists = LoanLists.getInstance();
                        switch (i) {
                            case 0:
                                loanLists.addLent(loan, getContext());
                                break;
                            case 1:
                                loanLists.addBorrowed(loan, getContext());
                                break;
                            case 2:
                                loanLists.addArchived(loan, getContext());
                                break;
                            default:
                                Log.e(TAG, "None status found, should not happen");
                                break;
                        }
                        dismiss();
                    }
                })
                .create();
    }
}
