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
 * Dialog used to clear archive list
 */
public class ClearArchiveDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "ClearArchiveDialog";

    /**
     * Create a new instance
     */
    public static ClearArchiveDialog newInstance() {
        return new ClearArchiveDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to clear archive list");
        int loanCount = LoanLists.getInstance().getArchivedList().size();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_warning_24dp)
                .setTitle(getString(R.string.clear_archive_list_question) + " (" + getResources().getQuantityString(R.plurals.plural_item, loanCount, loanCount) + ")")
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoanLists.getInstance().clearArchiveList(getContext());
                        Toast.makeText(getContext(), R.string.archive_loan_list_cleared, Toast.LENGTH_SHORT).show();
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
