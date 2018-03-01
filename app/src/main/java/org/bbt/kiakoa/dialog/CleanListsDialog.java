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
 * Dialog used to clean all lists
 */
public class CleanListsDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "CleanListsDialog";

    /**
     * Create a new instance of CleanListsDialog
     */
    public static CleanListsDialog newInstance() {
        return new CleanListsDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to clean loans");
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_delete_sweep_24dp)
                .setTitle(R.string.clean_loan_lists)
                .setItems(R.array.purge_choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Clean requested : " + which);
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
