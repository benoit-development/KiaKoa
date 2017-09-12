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
import org.bbt.kiakoa.tools.Miscellaneous;

/**
 * Dialog used to select image from various sources
 */
public class PictureDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "PictureDialog";

    /**
     * Loan to update
     */
    private Loan loan;

    /**
     * Create a new instance of PictureDialog
     *
     * @param loan loan to update
     */
    public static PictureDialog selectPicture(@NonNull Loan loan) {
        PictureDialog pictureDialog = new PictureDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        pictureDialog.setArguments(args);
        return pictureDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Show dialog to select a picture");

        loan = getArguments().getParcelable("loan");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_picture_24dp)
                .setTitle(R.string.picture)
                .setItems(R.array.picture_action_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0:
                                Log.i(TAG, "Picture from camera requested");
                                Miscellaneous.takePicture(getTargetFragment());
                                break;
                            case 1:
                                Log.i(TAG, "Picture from gallery requested");
                                Miscellaneous.showImageGallery(getTargetFragment());
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        if ((loan != null) && loan.getPicture() != null) {
            dialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "Picture deletion requested");
                    loan.setItemPicture(null);
                    LoanLists.getInstance().updateLoan(loan, getContext());
                }
            });
        }
        return dialogBuilder.create();

    }
}
