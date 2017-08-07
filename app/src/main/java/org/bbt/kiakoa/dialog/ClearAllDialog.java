package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.bbt.kiakoa.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LendLists;

/**
 * Dialog used to pick an item
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
        int lendCount = LendLists.getInstance().getLendCount();
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.clear_all_lists_question) + " (" + getResources().getQuantityString(R.plurals.plural_item, lendCount, lendCount) + ")")
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LendLists.getInstance().clearLists(getContext());
                        ((MainActivity) getActivity()).displayLendDetails(null);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();

    }
}
