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

import java.util.Objects;

/**
 * Dialog used to pick an item
 */
public class LendItemDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "LendItemDialog";
    private static final int ACTION_CREATE = 0;
    private static final int ACTION_UPDATE = 1;

    /**
     * item for a lend
     */
    private EditText itemEditText;

    private OnLendItemTypedListener onLendItemTypedListener;

    /**
     * Create a new instance of LendItemDialog
     */
    public static LendItemDialog newInstance() {
        LendItemDialog dialog = new LendItemDialog();
        Bundle args = new Bundle();
        args.putInt("action", ACTION_CREATE);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Create a new instance of LendItemDialog with item
     *
     * @param item lend item
     */
    public static LendItemDialog newInstance(String item) {
        LendItemDialog dialog = new LendItemDialog();
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lend_item, null, false);
        itemEditText = view.findViewById(R.id.item);
        String item = getArguments().getString("item", "");
        itemEditText.setText(item);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle((ACTION_CREATE == getArguments().getInt("action", ACTION_CREATE))?R.string.new_lend:R.string.item)
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
                            if (onLendItemTypedListener != null) {
                                Log.i(TAG, "new item : " + item);
                                onLendItemTypedListener.onLendCreated(item);
                            } else {
                                Log.w(TAG, "No listener to call, lend item typed");
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
     * Set listener from Lend item typed
     *
     * @param onLendItemTypedListener listener
     */
    public void setOnLendItemTypedListener(OnLendItemTypedListener onLendItemTypedListener) {
        this.onLendItemTypedListener = onLendItemTypedListener;
    }

    /**
     * Listener interference called when lend item type
     */
    public interface OnLendItemTypedListener {
        /**
         * Called when lend has been created
         *
         * @param lend created lend
         */
        void onLendCreated(String lend);
    }
}
