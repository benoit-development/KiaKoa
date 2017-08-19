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
 * {@link BroadcastReceiver} receving action from application notifications
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {


    public static final String INTENT_ACTION_RETURNED = "org.bbt.kiakoa.ACTION_RETURN_LOAN";
    public static final String EXTRA_LOAN = "extra_loan";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    /**
     * For log
     */
    private static final String TAG = "NotificationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case INTENT_ACTION_RETURNED:
                Log.i(TAG, "Request to put a loan in returned list");

                // get notification id
                int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
                if (notificationId == 0) {
                    Log.e(TAG, "Failed getting notification id");
                } else {

                    // getting loan
                    Loan loan = intent.getParcelableExtra(EXTRA_LOAN);
                    if (loan == null) {
                        // error while retrieving loan
                        Log.e(TAG, "Failed getting loan to set as returned");
                    } else {
                        // loan successfully got
                        LoanLists.getInstance().addReturned(loan, context);
                        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
                        Toast.makeText(context, R.string.loan_returned, Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}
