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
public class LendDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * For log
     */
    private static final String TAG = "LendDateDialog";

    /**
     * listener
     */
    private OnLendDateSetListener onLendDateSetListener;

    /**
     * Create a new instance of {@link LendDateDialog} with a lend date set
     *
     * @param lendDate lend date
     */
    public static LendDateDialog newInstance(long lendDate) {
        LendDateDialog dialog = new LendDateDialog();
        Bundle args = new Bundle();
        args.putLong("lend_date", lendDate);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getArguments().getLong("lend_date", System.currentTimeMillis()));
        return new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "New date picked : " + year + " " + month + " " + day);
        if (onLendDateSetListener != null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            onLendDateSetListener.onLendSet(cal.getTimeInMillis());
        }
    }

    /**
     * lister setter
     * @param listener lend date listener
     */
    public void setOnLendDateSetListener(OnLendDateSetListener listener) {
        this.onLendDateSetListener = listener;
    }

    /**
     * Listener interference called when lend item type
     */
    public interface OnLendDateSetListener {
        /**
         * Called when lend date has been set
         *
         * @param lendDate new lend date
         */
        void onLendSet(long lendDate);
    }
}
