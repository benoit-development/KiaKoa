package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
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

/**
 * Dialog used to pick an item
 */
public class LoanItemDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "LoanItemDialog";
    private static final int ACTION_CREATE = 0;
    private static final int ACTION_UPDATE = 1;

    /**
     * item for a loan
     */
    private EditText itemEditText;

    private OnLoanItemSetListener onLoanItemSetListener;

    /**
     * Create a new instance of LoanItemDialog
     */
    public static LoanItemDialog newInstance() {
        LoanItemDialog dialog = new LoanItemDialog();
        Bundle args = new Bundle();
        args.putInt("action", ACTION_CREATE);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Create a new instance of LoanItemDialog with item
     *
     * @param item loan item
     */
    public static LoanItemDialog newInstance(String item) {
        LoanItemDialog dialog = new LoanItemDialog();
        Bundle args = new Bundle();
        args.putString("item", item);
        args.putInt("action", ACTION_UPDATE);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_loan_item, null, false);
        itemEditText = view.findViewById(R.id.item);
        String item = getArguments().getString("item", "");
        itemEditText.setText(item);

        // clear button
        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEditText.setText("");
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle((ACTION_CREATE == getArguments().getInt("action", ACTION_CREATE)) ? R.string.new_loan : R.string.item)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();

        // manually manage ok button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // Change positive button click listener to manage dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item = itemEditText.getText().toString();
                        if (item.length() > 0) {
                            if (onLoanItemSetListener != null) {
                                Log.i(TAG, "new item : " + item);
                                onLoanItemSetListener.onItemSet(item);
                            } else {
                                Log.w(TAG, "No listener to call, loan item typed");
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
     * Set listener from Loan item typed
     *
     * @param onLoanItemSetListener listener
     */
    public void setOnLoanItemSetListener(OnLoanItemSetListener onLoanItemSetListener) {
        this.onLoanItemSetListener = onLoanItemSetListener;
    }

    /**
     * Listener interference called when loan item type
     */
    public interface OnLoanItemSetListener {
        /**
         * Called when item has been set
         *
         * @param item new item
         */
        void onItemSet(String item);
    }
}