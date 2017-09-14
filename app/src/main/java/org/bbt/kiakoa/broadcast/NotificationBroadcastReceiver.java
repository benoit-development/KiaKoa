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
    public static final String INTENT_ACTION_LOAN_NOTIFICATION = "org.bbt.kiakoa.ACTION_LOAN_NOTIFICATION";
    public static final String EXTRA_LOAN_ID = "extra_loan";

    /**
     * For log
     */
    private static final String TAG = "NotificationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

        // loan lists manager instance
        LoanLists loanLists = LoanLists.getInstance();
        // getting loan
        Loan loan = loanLists.findLoanById(intent.getLongExtra(EXTRA_LOAN_ID, 0));

        String action = intent.getAction();
        if (action != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            switch (action) {
                case INTENT_ACTION_RETURN_LOAN:
                    Log.i(TAG, "Request to put a loan as returned");

                    if (loan == null) {
                        // error while retrieving data
                        Log.e(TAG, "Failed getting data");
                    } else {
                        // data successfully got
                        loan.setReturned();
                        LoanLists.getInstance().updateLoan(loan, context);
                        if (notificationManager != null) {
                            notificationManager.cancel(loan.getNotificationId());
                        }
                        Toast.makeText(context, R.string.loan_returned, Toast.LENGTH_SHORT).show();
                    }

                    break;

                case INTENT_ACTION_ADD_WEEK:
                    Log.i(TAG, "Request to add a week to this loan");

                    if (loan == null) {
                        // error while retrieving data
                        Log.e(TAG, "Week : Failed getting data");
                    } else {
                        // data successfully got
                        loan.setReturnDate(System.currentTimeMillis() + (Loan.DAYS_IN_MILLIS * 7), context);
                        loanLists.updateLoan(loan, context);
                        if (notificationManager != null) {
                            notificationManager.cancel(loan.getNotificationId());
                        }
                        Toast.makeText(context, R.string.return_date_updated, Toast.LENGTH_SHORT).show();
                    }

                    break;

                case INTENT_ACTION_LOAN_CONTACT:
                    Log.i(TAG, "Request to display card contact");

                    if (loan == null) {
                        // error while retrieving data
                        Log.e(TAG, "Contact : Failed getting data");
                    } else {
                        Log.i(TAG, "Request to display contact card");
                        loan.displayContactCard(context);
                    }
                    break;

                case INTENT_ACTION_LOAN_NOTIFICATION:
                    Log.i(TAG, "Request to show notification for this loan");

                    if (loan == null) {
                        // error while retrieving data
                        Log.e(TAG, "Notification : Failed getting data");
                    } else {
                        Log.i(TAG, "Request to display loan notification");
                        loan.notify(context);
                    }
                    break;

            }

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        } else {
            Log.e(TAG, "No action found in received intent");
        }
    }
}
