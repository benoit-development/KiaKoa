package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to delete a {@link Loan} from {@link LoanLists}
 */
public class DeleteLoanDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "DeleteLoanDialog";

    /**
     * Create a new instance of DeleteLoanDialog
     *
     * @param loan loan to be deleted
     */
    public static DeleteLoanDialog newInstance(@NonNull Loan loan) {
        DeleteLoanDialog deleteLoanDialog = new DeleteLoanDialog();

        // Supply loan as an argument.
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        deleteLoanDialog.setArguments(args);

        return deleteLoanDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to clear all lists");
        final Loan loan = getArguments().getParcelable("loan");

        if (loan == null) {

            Log.e(TAG, "no loan found, should not happen");
            dismiss();
            return new AlertDialog.Builder(getActivity()).create();

        } else {

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_delete_24dp)
                    .setTitle(loan.getItem())
                    .setMessage(R.string.delete_loan_question)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (LoanLists.getInstance().deleteLoan(loan, getContext())) {
                                Toast.makeText(getContext(), R.string.loan_deleted, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), R.string.error_on_deleting_loan, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();

        }
    }
}
