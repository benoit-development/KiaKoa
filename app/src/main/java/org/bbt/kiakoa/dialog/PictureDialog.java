package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

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
    private static final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    /**
     * To pick a picture from local gallery
     */
    private static final int REQUEST_CODE_PICTURE = 567;

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
                    takePicture();
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
                showImageGallery();
            }
        });
        // clipart
        view.findViewById(R.id.image_clipart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Picture from clipart list requested");
                showClipartDialog();
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

    /**
     * Launch activity to take a picture
     */
    private void takePicture() {
        Log.i(TAG, "take picture action requested");
        startActivityForResult(takePictureIntent, REQUEST_CODE_PICTURE);
    }

    /**
     * Launch activity to select a local image from
     */
    private void showImageGallery() {
        Log.i(TAG, "show gallery action requested");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    loan.setItemPicture(getContext(), data);
                    LoanLists.getInstance().updateLoan(loan, getContext());
                    dismiss();
                } else {
                    Log.w(TAG, "No picture returned from camera");
                }
                break;
        }
    }

    /**
     * Display dialog to change picture with a clipart
     */
    private void showClipartDialog() {
        // Create and show the dialog
        ClipartDialog clipartDialog = ClipartDialog.newInstance(loan);
        clipartDialog.show(getFragmentManager(), "clipart");
    }
}
