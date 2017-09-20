package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.LoanDetails.LoanDetailsFragment;
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
     * Intent to take a picture from camera
     */
    private final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_item_picture, null, false);

        // check if camera is available
        PackageManager packageManager = getContext().getPackageManager();
        boolean cameraAvailable = (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) && (takePictureIntent.resolveActivity(packageManager) != null));

        // customize view
        // camera
        View cameraView = view.findViewById(R.id.image_camera);
        if (cameraAvailable) {
            cameraView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Picture from camera requested");
                    Miscellaneous.takePicture(getTargetFragment());
                    dismiss();
                }
            });
        } else {
            view.findViewById(R.id.camera_button_container).setVisibility(View.GONE);
        }
        // gallery
        view.findViewById(R.id.image_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Picture from gallery requested");
                Miscellaneous.showImageGallery(getTargetFragment());
                dismiss();
            }
        });
        // clipart
        view.findViewById(R.id.image_clipart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Picture from clipart list requested");
                ((LoanDetailsFragment) getTargetFragment()).showClipartDialog();
                dismiss();
            }
        });

        // build dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_picture_24dp)
                .setTitle(R.string.picture)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        if ((loan != null) && (loan.getItemPicture() != null)) {
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
