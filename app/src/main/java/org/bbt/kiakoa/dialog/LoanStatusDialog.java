package org.bbt.kiakoa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanStatus;

/**
 * Dialog used to pick a status
 */
public class LoanStatusDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "LoanStatusDialog";

    /**
     * listener
     */
    private OnLoanStatusSetListener onLoanStatusSetListener;

    /**
     * Create a new instance of {@link LoanStatusDialog}
     *
     * @param status loan status
     */
    public static LoanStatusDialog newInstance(@NonNull LoanStatus status) {
        LoanStatusDialog dialog = new LoanStatusDialog();
        Bundle args = new Bundle();
        args.putSerializable("status", status);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LoanStatus status = (LoanStatus) getArguments().getSerializable("status");
        status = (status != null)?status:LoanStatus.NONE;

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.status)
                .setSingleChoiceItems(R.array.status_list, status.getIndex(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onLoanStatusSetListener != null) {
                            LoanStatus result;
                            switch (i) {
                                case 0:
                                    result = LoanStatus.LENT;
                                    break;
                                case 1:
                                    result = LoanStatus.BORROWED;
                                    break;
                                case 2:
                                    result = LoanStatus.ARCHIVED;
                                    break;
                                default:
                                    result = LoanStatus.NONE;
                                    break;
                            }
                            Log.i(TAG, "Selected status : " + result);
                            onLoanStatusSetListener.onStatusSet(result);
                        }
                        dismiss();
                    }
                })
                .create();
    }

    /**
     * Set listener from Loan status chosen
     *
     * @param onLoanStatusSetListener listener
     */
    public void setOnLoanStatusSetListener(OnLoanStatusSetListener onLoanStatusSetListener) {
        this.onLoanStatusSetListener = onLoanStatusSetListener;
    }

    /**
     * Listener interference called when loan status is set
     */
    public interface OnLoanStatusSetListener {
        /**
         * Called when status has been set
         *
         * @param status new status
         */
        void onStatusSet(LoanStatus status);
    }
}
