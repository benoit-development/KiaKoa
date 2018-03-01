package org.bbt.kiakoa.model;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.bbt.kiakoa.widget.LoanAppWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

/**
 * Class managing all current loans
 */
public class LoanLists {

    /**
     * Loan lists identifier for shared preferences
     */
    private static final String SHARED_PREFERENCES_LOAN_LISTS_ID = "loan_lists";

    /**
     * Lent list identifier for shared preferences
     */
    public static final String SHARED_PREFERENCES_LENT_KEY = "lent";

    /**
     * Borrowed list identifier for shared preferences
     */
    public static final String SHARED_PREFERENCES_BORROWED_KEY = "borrowed";

    /**
     * time of last saved loans in preferences
     */
    private static final String SHARED_PREFERENCES_TIME_KEY = "time";

    /**
     * Tag for logs
     */
    private static final String TAG = "LoanLists";

    /**
     * list of loaned items
     */
    @Expose
    private LoanList lentList;

    /**
     * borrowed loaned items
     */
    @Expose
    private LoanList borrowedList;

    /**
     * Singleton instance
     */
    private static LoanLists instance;

    /**
     * Listener loan lists changed
     */
    private final ArrayList<OnLoanListsChangedListener> onLoanListsChangedListeners = new ArrayList<>();

    /**
     * Instance getter
     *
     * @return singleton instance
     */
    public static LoanLists getInstance() {

        if (instance == null) {
            // Create the instance
            Log.i(TAG, "getInstance: Creating instance");
            instance = new LoanLists();
        }

        return instance;
    }

    /**
     * Hidden constructor
     */
    private LoanLists() {

    }

    /**
     * Init lists from shared preferences
     *
     * @param context a {@link Context}
     */
    public void initLists(Context context) {
        // when context is set, we need to update lists with stored data
        if (context != null) {

            Log.i(TAG, "initLists: init lists from shared preferences");

            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_LOAN_LISTS_ID, Context.MODE_PRIVATE);
            lentList = new Gson().fromJson(sharedPref.getString(SHARED_PREFERENCES_LENT_KEY, "[]"), new TypeToken<LoanList>() {
            }.getType());
            borrowedList = new Gson().fromJson(sharedPref.getString(SHARED_PREFERENCES_BORROWED_KEY, "[]"), new TypeToken<LoanList>() {
            }.getType());

            scheduleAllLoanNotification(context);

        } else {

            Log.i(TAG, "initLists: init empty lists");

            lentList = new LoanList();
            borrowedList = new LoanList();
        }

        Log.d(TAG, "initLists: lent count:     " + lentList.size());
        Log.d(TAG, "initLists: borrowed count: " + borrowedList.size());
    }

    /**
     * Get all lists of the {@link LoanLists} instance
     *
     * @return all lists
     */
    private ArrayList<LoanList> getAllLists() {
        ArrayList<LoanList> result = new ArrayList<>();
        result.add(lentList);
        result.add(borrowedList);
        return result;
    }

    /**
     * save a {@link Loan} to the Lent list
     *
     * @param loan    loan to add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean saveLent(Loan loan, Context context) {
        Log.i(TAG, "saveLent: " + loan.toJson());

        // delete from other lists if necessary
        for (LoanList list : getAllLists()) {
            list.remove(loan);
        }

        boolean result = lentList.add(loan);

        // if there is a context, then save list
        saveInSharedPreferences(context);

        if (result) {
            loan.scheduleNotification(context);
            notifyLoanListsChanged();
        }

        return result;
    }


    /**
     * save a {@link Loan} from the Borrowed list
     *
     * @param loan    loan from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean saveBorrowed(Loan loan, Context context) {
        Log.i(TAG, "saveBorrowed: " + loan.toJson());

        // delete from other lists if necessary
        for (LoanList list : getAllLists()) {
            list.remove(loan);
        }

        boolean result = borrowedList.add(loan);

        // if there is a context, then save list
        saveInSharedPreferences(context);

        if (result) {
            loan.scheduleNotification(context);
            notifyLoanListsChanged();
        }

        return result;
    }

    /**
     * Update a {@link Loan} present in one of the lists
     *
     * @param loan    {@link Loan} to update
     * @param context a {@link Context}
     * @return update result (success or not)
     */
    public boolean updateLoan(Loan loan, Context context) {
        boolean result = false;

        // remove this loan from lists (based on its ID)
        if (lentList.remove(loan)) {
            // loan removed from loan to list
            result = lentList.add(loan);
        }
        if (borrowedList.remove(loan)) {
            // loan removed from loan from list
            result |= borrowedList.add(loan);
        }

        if (result) {
            if (loan.isReturned()) {
                loan.cancelNotificationSchedule(context);
            } else {
                loan.scheduleNotification(context);
            }
            saveInSharedPreferences(context);
            notifyLoanListsChanged();
        }

        return result;
    }

    /**
     * Loan to list getter
     *
     * @return lent list
     */
    public LoanList getLentList() {
        return lentList;
    }

    /**
     * Borrowed list getter
     *
     * @return borrowed list
     */
    public LoanList getBorrowedList() {
        return borrowedList;
    }

    /**
     * empty every lists
     *
     * @param context a {@link Context}
     */
    public void clearLists(Context context) {
        Log.i(TAG, "clearLists");

        // empty lists
        cancelAllLoanNotificationSchedule(context);
        for (LoanList list : getAllLists()) {
            list.clear();
        }
        saveInSharedPreferences(context);

        // notify changes
        notifyLoanListsChanged();
    }

    /**
     * Delete a loan that may be present in lists
     *
     * @param loan    loan to delete
     * @param context a context
     * @return delete result
     */
    public boolean deleteLoan(Loan loan, Context context) {

        // remove this loan from lists (based on its ID)
        boolean result = false;
        for (LoanList list : getAllLists()) {
            result |= list.remove(loan);
        }

        if (result) {
            Log.i(TAG, "deleteLoan : " + loan.getItem() + " deleted.");
            loan.cancelNotificationSchedule(context);
            notifyLoanListsChanged();
            saveInSharedPreferences(context);
        } else {
            Log.i(TAG, "deleteLoan : " + loan.getItem() + " not found, not deleted.");
        }

        return result;
    }

    /**
     * Save asynchronously loan lists in {@link SharedPreferences}
     *
     * @param context a context
     */
    private void saveInSharedPreferences(Context context) {
        // sort lists
        Loan.LoanComparator loanComparator = new Loan.LoanComparator();
        Collections.sort(lentList, loanComparator);
        Collections.sort(borrowedList, loanComparator);
        if (context != null) {
            String jsonLentList = new Gson().toJson(lentList);
            Log.d(TAG, "Saving lent in SharedPreferences : " + jsonLentList);

            String jsonBorrowedList = new Gson().toJson(borrowedList);
            Log.d(TAG, "Saving borrowed in SharedPreferences : " + jsonBorrowedList);

            // saving data from shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_LOAN_LISTS_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SHARED_PREFERENCES_LENT_KEY, jsonLentList);
            editor.putString(SHARED_PREFERENCES_BORROWED_KEY, jsonBorrowedList);
            editor.putLong(SHARED_PREFERENCES_TIME_KEY, System.currentTimeMillis());
            editor.apply();

            // intent for app widget
            try {
                Log.i(TAG, "Intent to refresh app widget");
                Intent intentSync = new Intent(context, LoanAppWidgetProvider.class);
                intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method called to notify changes
     */
    private void notifyLoanListsChanged() {
        Log.i(TAG, "Notifying Loan Lists changed");
        for (OnLoanListsChangedListener listener : onLoanListsChangedListeners) {
            if (listener != null) {
                listener.onLoanListsChanged();
            }
        }
    }

    /**
     * Register a listener for changes in this {@link LoanLists}
     *
     * @param listener listener to be registered
     * @param id       id for log
     */
    public void registerOnLoanListsChangedListener(OnLoanListsChangedListener listener, String id) {
        Log.i(TAG, "Adding OnLoanListsChangedListener : " + id);
        onLoanListsChangedListeners.add(listener);
    }

    /**
     * Unregister a listener for changes in this {@link LoanLists}
     *
     * @param listener listener to be unregistered
     * @param id       id for log
     */
    public void unregisterOnLoanListsChangedListener(OnLoanListsChangedListener listener, String id) {
        Log.i(TAG, "Removing OnLoanListsChangedListener : " + id);
        onLoanListsChangedListeners.remove(listener);
    }

    /**
     * Get all loans lent in progress
     */
    @NonNull
    public ArrayList<Loan> getLentInProgress() {
        return lentList.getInProgressLoanList();
    }

    /**
     * Get all loans borrowed in progress
     */
    @NonNull
    public ArrayList<Loan> getBorrowedInProgress() {
        return borrowedList.getInProgressLoanList();
    }

    /**
     * Listener when loan lists changed
     */
    public interface OnLoanListsChangedListener {
        /**
         * Called when loan lists changed
         */
        void onLoanListsChanged();
    }

    /**
     * to get the number of loans in all lists
     *
     * @return loans count
     */
    public int getLoanCount() {
        return lentList.size() + borrowedList.size();
    }

    /**
     * Search a Loan in lists from its id
     *
     * @param id loan id
     * @return found loan or null
     */
    public Loan findLoanById(long id) {
        Loan result = null;

        // in lent
        for (Loan loan : lentList) {
            if (loan.getId() == id) {
                result = loan;
            }
        }
        // in borrowed
        for (Loan loan : borrowedList) {
            if (loan.getId() == id) {
                result = loan;
            }
        }

        return result;
    }

    /**
     * refresh scheduled notification of all loans
     *
     * @param context a context
     */
    public void scheduleAllLoanNotification(Context context) {
        Log.i(TAG, "Scheduling all notifications");
        for (LoanList list : getAllLists()) {
            for (Loan loan : list) {
                loan.scheduleNotification(context);
            }
        }
    }

    /**
     * cancel scheduled notification of all loans
     *
     * @param context a context
     */
    public void cancelAllLoanNotificationSchedule(Context context) {
        Log.i(TAG, "Cancelling all notifications");
        for (LoanList list : getAllLists()) {
            for (Loan loan : list) {
                loan.cancelNotificationSchedule(context);
            }
        }
    }

    /**
     * Check if this instance is valid
     *
     * @return instance validity
     */
    private boolean isValid() {
        boolean result = true;

        for (LoanList list : getAllLists()) {
            // check if list exists
            if (lentList == null) {
                Log.e(TAG, "lent list null");
                result &= false;
                // break this loop
                break;
            }
            // the list exists, check if loans are valid too
            for (Loan loan : list) {
                result &= loan.isValid();
            }
        }

        return result;
    }

    /**
     * Purge all loans older than one week
     */
    public void purgeWeek() {
        Log.i(TAG, "Week purge requested");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        purgeLists(cal.getTimeInMillis());
    }

    /**
     * Purge all loans older than one month
     */
    public void purgeMonth() {
        Log.i(TAG, "Month purge requested");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        purgeLists(cal.getTimeInMillis());
    }

    /**
     * Purge all loans before the date
     *
     * @param time date for purge
     */
    private void purgeLists(long time) {
        for (LoanList list : getAllLists()) {
            Iterator<Loan> listIterator = list.iterator();
            while (listIterator.hasNext()) {
                Loan loan = listIterator.next();
                if (loan.getLoanDate() <= time) {
                    Log.i(TAG, "Deleting : " + loan.toJson());
                    listIterator.remove();
                }
            }
        }

        notifyLoanListsChanged();
    }
}
