package org.bbt.kiakoa.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * {@link BroadcastReceiver} receiving action from application notifications
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    // constants to be used
    public static final String INTENT_ACTION_RETURN_LOAN = "org.bbt.kiakoa.ACTION_RETURN_LOAN";
    public static final String INTENT_ACTION_ADD_WEEK = "org.bbt.kiakoa.ACTION_ADD_WEEK";
    public static final String INTENT_ACTION_LOAN_CONTACT = "org.bbt.kiakoa.ACTION_LOAN_CONTACT";
    public static final String EXTRA_LOAN = "extra_loan";

    /**
     * For log
     */
    private static final String TAG = "NotificationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

        // getting loan
        Loan loan = intent.getParcelableExtra(EXTRA_LOAN);
        // loan lists manager instance
        LoanLists loanLists = LoanLists.getInstance();

        switch (intent.getAction()) {
            case INTENT_ACTION_RETURN_LOAN:
                Log.i(TAG, "Request to put a loan in returned list");

                if (loan == null) {
                    // error while retrieving data
                    Log.e(TAG, "Failed getting data");
                } else {
                    // data successfully got
                    loanLists.saveReturned(loan, context);
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(loan.getNotificationId());
                    Toast.makeText(context, R.string.loan_returned, Toast.LENGTH_SHORT).show();
                }

                break;

            case INTENT_ACTION_ADD_WEEK:
                Log.i(TAG, "Request to add a week to this loan");

                if (loan == null) {
                    // error while retrieving data
                    Log.e(TAG, "Failed getting data");
                } else {
                    // data successfully got
                    loan.setReturnDate(System.currentTimeMillis() + (Loan.DAYS_IN_MILLIS * 7));
                    loanLists.updateLoan(loan, context);
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(loan.getNotificationId());
                    Toast.makeText(context, R.string.return_date_updated, Toast.LENGTH_SHORT).show();
                }

                break;

            case INTENT_ACTION_LOAN_CONTACT:
                Log.i(TAG, "Request to display contact card");
                loan.displayContactCard(context);
                break;

        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
