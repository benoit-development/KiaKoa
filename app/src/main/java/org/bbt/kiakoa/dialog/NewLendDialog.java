package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Lend;

/**
 * Dialog used to create a new {@link org.bbt.kiakoa.model.Lend
 */
public class NewLendDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "NewLendDialog";
    /**
     * item of the new lend
     */
    private EditText itemEditText;
    private OnLendCreatedListener onLendCreatedListener;

    /**
     * Create a new instance of NewLendDialog
     */
    public static NewLendDialog newInstance() {
        return new NewLendDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate view
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_lend, null, false);
        itemEditText = view.findViewById(R.id.item);


        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.new_lend)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        // manually
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item = itemEditText.getText().toString();
                        if (item.length() > 0) {
                            if (onLendCreatedListener != null) {
                                Log.i(TAG, "new item : " + item);
                                onLendCreatedListener.onLendCreated(new Lend(item));
                            } else {
                                Log.w(TAG, "No listener to call, lend creation cancelled");
                            }
                            dismiss();
                        } else {
                            Log.i(TAG, "item can't be empty");
                            Toast.makeText(getContext(), R.string.item_cant_be_blank, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        return dialog;
    }

    /**
     * Set lisener from Lend creation
     *
     * @param onLendCreatedListener listener
     */
    public void setOnLendCreatedListener(OnLendCreatedListener onLendCreatedListener) {
        this.onLendCreatedListener = onLendCreatedListener;
    }

    /**
     * Listener interference called when lend created
     */
    public interface OnLendCreatedListener {
        /**
         * Called when lend has been created
         *
         * @param lend created lend
         */
        void onLendCreated(Lend lend);
    }
}
