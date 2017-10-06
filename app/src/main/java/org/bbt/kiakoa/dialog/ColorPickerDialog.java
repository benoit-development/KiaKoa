package org.bbt.kiakoa.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Dialog used to pick a loan color
 */
public class ColorPickerDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "ColorPickerDialog";

    /**
     * loan to create/update
     */
    private Loan loan;

    /**
     * Create a new instance of ColorPickerDialog with item
     *
     * @param loan loan to be updated
     */
    public static ColorPickerDialog newInstance(Loan loan) {
        ColorPickerDialog dialog = new ColorPickerDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get loan to update
        loan = getArguments().getParcelable("loan");

        return ColorPickerDialogBuilder
                .with(getContext())
                .setTitle(R.string.color)
                .initialColor(loan.getColor(getContext()))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .noSliders()
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] allColors) {
                        Log.i(TAG, "New color for loan : " + selectedColor);
                        loan.setColor(selectedColor);
                        LoanLists.getInstance().updateLoan(loan, getContext());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).build();
    }
}
