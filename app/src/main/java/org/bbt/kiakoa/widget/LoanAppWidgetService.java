package org.bbt.kiakoa.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.activity.MainActivity;
import org.bbt.kiakoa.exception.LoanException;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Service to populate Widget {@link android.widget.ListView}
 */
public class LoanAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LoanAppWidgetFactory(this.getApplicationContext());
    }

    private class LoanAppWidgetFactory implements RemoteViewsFactory {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_LOAN = 1;
        private static final String TAG = "LoanAppWidgetFactory";

        private final Context context;

        /**
         * lent list
         */
        @NonNull
        private final ArrayList<Loan> lentList;

        /**
         * borrowed list
         */
        @NonNull
        private final ArrayList<Loan> borrowedList;

        public LoanAppWidgetFactory(Context applicationContext) {
            Log.i(TAG, "Creating Widget Factory");
            this.context = applicationContext;
            // get loans to display
            lentList = LoanLists.getInstance().getLentInProgress();
            borrowedList = LoanLists.getInstance().getBorrowedInProgress();
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return lentList.size() //lent
                    + borrowedList.size() // borrowed
                    + 2; // headers
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews row;

            switch (getViewType(position)) {
                case TYPE_HEADER:

                    row = new RemoteViews(context.getPackageName(), R.layout.adapter_loan_item_separator);
                    int textId;
                    if (position == 0) {
                        textId = R.string.lent;
                    } else {
                        textId = R.string.borrowed;
                    }
                    int count = (position == 0)? lentList.size() : borrowedList.size();
                    row.setTextViewText(R.id.text, context.getString(textId) + " [" + count + "]");
                    row.setViewVisibility(R.id.empty_text, View.GONE);

                    break;
                default:

                    Loan loan = getItem(position);

                    row = new RemoteViews(context.getPackageName(), R.layout.widget_adapter_loan_item);
                    if (loan != null) {
                        row.setTextViewText(R.id.item, loan.getItem());
                        row.setTextViewText(R.id.loan_date, loan.getLoanDateString());
                        row.setInt(R.id.color, "setBackgroundColor", loan.getColor(context));
                        // return date
                        if (loan.hasReturnDate()) {
                            row.setViewVisibility(R.id.date_separator, View.VISIBLE);
                            row.setViewVisibility(R.id.return_date, View.VISIBLE);
                            row.setTextViewText(R.id.return_date, loan.getReturnDateString(context));
                            // returnDate is > 0
                            try {
                                if ((!loan.isReturned()) && (loan.getDatesDifferenceInDays() > 0)) {
                                    row.setTextColor(R.id.return_date, ContextCompat.getColor(context, R.color.alertRedText));
                                } else {
                                    row.setTextColor(R.id.return_date, ContextCompat.getColor(context, R.color.colorTextLight));
                                }
                            } catch (LoanException e) {
                                // no return date
                            }
                        } else {
                            row.setViewVisibility(R.id.date_separator, View.GONE);
                            row.setViewVisibility(R.id.return_date, View.GONE);
                        }

                        // click to see details
                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable(MainActivity.EXTRA_LOAN, loan);
                        intent.putExtras(extras);
                        row.setOnClickFillInIntent(android.R.id.text1, intent);
                    } else {
                        Log.e(TAG, "Loan is null, should not happen.");
                    }

                    break;
            }


            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * View type getter depending on View position
         *
         * @param position view position
         * @return view type
         */
        private int getViewType(int position) {
            if ((position == 0) || (position == lentList.size() + 1)) {
                return TYPE_HEADER;
            } else {
                return TYPE_LOAN;
            }
        }

        /**
         * {@link Loan} getter depending on View position
         *
         * @param position view position
         * @return a loan or null if it's a header position
         */
        private Loan getItem(int position) {
            int lentCount = lentList.size();
            if ((position == 0) || (position == lentCount + 1)) {
                // header
                return null;
            } else if (position <= lentCount) {
                return lentList.get(position - 1);
            } else {
                return borrowedList.get(position - lentCount - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
