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
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

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
     * loan to update
     */
    private Loan loan;

    /**
     * Create a new instance of {@link ReturnDateDialog} with a return date set
     *
     * @param loan loan to update
     */
    public static ReturnDateDialog newInstance(Loan loan) {
        ReturnDateDialog dialog = new ReturnDateDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();

        loan = getArguments().getParcelable("loan");
        if (loan == null) {
            Log.e(TAG, "No loan found, should not happen");
            dismiss();
        }
        long returnDate = loan.getReturnDate();
        if (returnDate != -1) {
            cal.setTimeInMillis(returnDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        // can't delete return date if loan is returned
        if (!loan.isReturned()) {
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "Delete return date");
                    setReturnDate(-1);
                }
            });
        }

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "New return date picked : " + year + " " + month + " " + day);
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        setReturnDate(cal.getTimeInMillis());
    }

    /**
     * Set loan return date
     * @param returnDate return date
     */
    private void setReturnDate(long returnDate) {
        loan.setReturnDate(returnDate);
        LoanLists.getInstance().updateLoan(loan, getContext());
    }
}
