package org.bbt.kiakoa.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Dialog used to pick an item
 */
public class LoanDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * For log
     */
    private static final String TAG = "LoanDateDialog";

    /**
     * listener
     */
    private OnLoanDateSetListener onLoanDateSetListener;

    /**
     * Create a new instance of {@link LoanDateDialog} with a loan date set
     *
     * @param loanDate loan date
     */
    public static LoanDateDialog newInstance(long loanDate) {
        LoanDateDialog dialog = new LoanDateDialog();
        Bundle args = new Bundle();
        args.putLong("loan_date", loanDate);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getArguments().getLong("loan_date", System.currentTimeMillis()));
        return new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "New date picked : " + year + " " + month + " " + day);
        if (onLoanDateSetListener != null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            onLoanDateSetListener.onDateSet(cal.getTimeInMillis());
        }
    }

    /**
     * lister setter
     * @param listener loan date listener
     */
    public void setOnLoanDateSetListener(OnLoanDateSetListener listener) {
        this.onLoanDateSetListener = listener;
    }

    /**
     * Listener interference called when loan item type
     */
    public interface OnLoanDateSetListener {
        /**
         * Called when loan date has been set
         *
         * @param loanDate new loan date
         */
        void onDateSet(long loanDate);
    }
}
