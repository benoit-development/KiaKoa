package org.bbt.kiakoa.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.activity.MainActivity;

import java.util.Random;

/**
 * Application widget displaying in progress loans on home screen
 */
public class LoanAppWidgetProvider extends AppWidgetProvider {

    /**
     * For logs
     */
    private static final String TAG = "LoanAppWidgetProvider";

    /**
     * Action to launch main activity
     */
    private static final String ACTION_MAIN_ACTIVITY = "org.bbt.kiakoa.action.main.activity";
    /**
     * Action to show a specific loan
     */
    static final String ACTION_SHOW_LOAN = "org.bbt.kiakoa.action.show.loan";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            Log.i(TAG, "Widget ID : " + appWidgetId);

            // create view
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // click on title to launch main activity
            Intent mainActivityIntent = new Intent(context, LoanAppWidgetProvider.class);
            mainActivityIntent.setAction(LoanAppWidgetProvider.ACTION_MAIN_ACTIVITY);
            remoteViews.setOnClickPendingIntent(R.id.title, PendingIntent.getBroadcast(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT));

            // populate ListView
            Intent svcIntent = new Intent(context, LoanAppWidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.fromParts("content", String.valueOf(appWidgetId + new Random().nextInt()), null));
            remoteViews.setRemoteAdapter(R.id.list, svcIntent);



            // update widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action != null) {
            Log.d(TAG, "Action : " + action);

            switch (action) {
                case ACTION_MAIN_ACTIVITY:
                    Log.i(TAG, "Launch main activity");
                    // launch main activity
                    Intent mainActivityIntent = new Intent(context, MainActivity.class);
                    context.startActivity(mainActivityIntent);
                    break;
                case ACTION_SHOW_LOAN:
                    Log.i(TAG, "Display loan required");
                    // launch activity with popup to create a new loan
                    Intent loanIntent = new Intent(context, MainActivity.class);
                    loanIntent.setAction(MainActivity.ACTION_LOAN);
                    loanIntent.putExtra(MainActivity.EXTRA_LOAN, intent.getParcelableExtra(MainActivity.EXTRA_LOAN));
                    context.startActivity(loanIntent);
                    break;
                case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass())));
                    break;
            }

        } else {
            Log.e(TAG, "No action received, should not happen.");
        }

    }
}
