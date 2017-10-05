package org.bbt.kiakoa.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.bbt.kiakoa.activity.MainActivity;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Service to populate Widget {@link android.widget.ListView}
 */
class LoanAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LoanAppWidgetFactory(this.getApplicationContext());
    }

    private class LoanAppWidgetFactory implements RemoteViewsFactory {

        private final Context context;

        public LoanAppWidgetFactory(Context applicationContext) {
            this.context=applicationContext;
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
            return LoanLists.getInstance().getLentList().size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews row = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);

            Loan loan = LoanLists.getInstance().getLentList().get(i);
            row.setTextViewText(android.R.id.text1, loan.getItem());


            Intent intent = new Intent();
            Bundle extras=new Bundle();
            extras.putParcelable(MainActivity.EXTRA_LOAN, loan);
            intent.putExtras(extras);
            row.setOnClickFillInIntent(android.R.id.text1, intent);

            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
