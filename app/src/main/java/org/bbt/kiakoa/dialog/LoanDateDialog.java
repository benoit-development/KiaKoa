package org.bbt.kiakoa.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.Calendar;

/**
 * Dialog used to pick a loan date
 */
public class LoanDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * For log
     */
    private static final String TAG = "LoanDateDialog";

    /**
     * loan instance to update
     */
    private Loan loan;

    /**
     * Create a new instance of {@link LoanDateDialog} with a loan date set
     *
     * @param loan loan
     */
    public static LoanDateDialog newInstance(Loan loan) {
        LoanDateDialog dialog = new LoanDateDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        this.loan = getArguments().getParcelable("loan");
        if (loan != null) {
            cal.setTimeInMillis(loan.getLoanDate());
        } else {
            Log.e(TAG, "No loan found. should not happen");
            dismiss();
        }
        return new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "New loan date picked : " + year + " " + month + " " + day);
        if (loan != null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            loan.setLoanDate(cal.getTimeInMillis());
            LoanLists.getInstance().updateLoan(loan, getContext());
        }
    }
}
