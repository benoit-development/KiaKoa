package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to clear all lists
 */
public class PurgeListsDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "PurgeListsDialog";

    /**
     * Create a new instance of PurgeListsDialog
     */
    public static PurgeListsDialog newInstance() {
        return new PurgeListsDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to purge loans");
        int loanCount = LoanLists.getInstance().getLoanCount();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_delete_forever_24dp)
                .setTitle(R.string.purgeLoanLists)
                .setItems(R.array.purge_choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Purge requested : " + which);
                        switch (which) {
                            case 0:
                                LoanLists.getInstance().clearLists(getContext());
                                break;
                            case 1:
                                LoanLists.getInstance().purgeWeek();
                                break;
                            case 2:
                                LoanLists.getInstance().purgeMonth();
                                break;
                        }
                    }
                })
                .create();

    }
}
