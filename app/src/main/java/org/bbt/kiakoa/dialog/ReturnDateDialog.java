package org.bbt.kiakoa.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import org.bbt.kiakoa.R;

import java.util.Calendar;

/**
 * Dialog used to pick a return date
 */
public class ReturnDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * For log
     */
    private static final String TAG = "ReturnDateDialog";

    /**
     * listener
     */
    private OnReturnDateSetListener onReturnDateSetListener;

    /**
     * Create a new instance of {@link ReturnDateDialog} with a return date set
     *
     * @param returnDate return date
     */
    public static ReturnDateDialog newInstance(long returnDate) {
        ReturnDateDialog dialog = new ReturnDateDialog();
        Bundle args = new Bundle();
        args.putLong("return_date", returnDate);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getArguments().getLong("loan_date", System.currentTimeMillis()));
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onReturnDateSetListener.onReturnDateSet(-1);
            }
        });
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "New return date picked : " + year + " " + month + " " + day);
        if (onReturnDateSetListener != null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            onReturnDateSetListener.onReturnDateSet(cal.getTimeInMillis());
        }
    }

    /**
     * lister setter
     * @param listener return date listener
     */
    public void setOnReturnDateSetListener(OnReturnDateSetListener listener) {
        this.onReturnDateSetListener = listener;
    }

    /**
     * Listener interference called when loan item type
     */
    public interface OnReturnDateSetListener {
        /**
         * Called when return date has been set
         *
         * @param returnDate new loan date
         */
        void onReturnDateSet(long returnDate);
    }
}
