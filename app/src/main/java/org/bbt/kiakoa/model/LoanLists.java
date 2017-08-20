package org.bbt.kiakoa.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    public static final String SHARED_PREFERENCES_LENT_ID = "lent";

    /**
     * Borrowed list identifier for shared preferences
     */
    public static final String SHARED_PREFERENCES_BORROWED_ID = "borrowed";

    /**
     * Loan returned list identifier for shared preferences
     */
    public static final String SHARED_PREFERENCES_RETURNED_ID = "returned";

    /**
     * Tag for logs
     */
    private static final String TAG = "LoanLists";

    /**
     * list of loaned items
     */
    private ArrayList<Loan> lentList;

    /**
     * borrowed loaned items
     */
    private ArrayList<Loan> borrowedList;

    /**
     * returned loan items
     */
    private ArrayList<Loan> returnedList;

    /**
     * Singleton instance
     */
    private static LoanLists instance;

    /**
     * Loan Comparator based on loan date
     */
    private final Comparator<Loan> loanDateComparator = new Comparator<Loan>() {
        @Override
        public int compare(Loan loan1, Loan loan2) {
            return Long.compare(loan1.getLoanDate(), loan2.getLoanDate());
        }
    };

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
            lentList = new Gson().fromJson(sharedPref.getString(SHARED_PREFERENCES_LENT_ID, "[]"), new TypeToken<ArrayList<Loan>>() {
            }.getType());
            borrowedList = new Gson().fromJson(sharedPref.getString(SHARED_PREFERENCES_BORROWED_ID, "[]"), new TypeToken<ArrayList<Loan>>() {
            }.getType());
            returnedList = new Gson().fromJson(sharedPref.getString(SHARED_PREFERENCES_RETURNED_ID, "[]"), new TypeToken<ArrayList<Loan>>() {
            }.getType());

            sortLists();

        } else {

            Log.i(TAG, "initLists: init empty lists");

            lentList = new ArrayList<>();
            borrowedList = new ArrayList<>();
            returnedList = new ArrayList<>();
        }

        Log.d(TAG, "initLists: lent count:     " + lentList.size());
        Log.d(TAG, "initLists: borrowed count: " + borrowedList.size());
        Log.d(TAG, "initLists: returned count: " + returnedList.size());
    }

    /**
     * Sort all the lists
     */
    private void sortLists() {
        sortList(lentList);
        sortList(borrowedList);
        sortList(returnedList);
    }

    /**
     * Sort a list depending on the chosen sort
     * @param loanList list to be sorted
     */
    private void sortList(ArrayList<Loan> loanList) {
        Collections.sort(loanList, loanDateComparator);
    }

    /**
     * save a {@link Loan} to the Lent list
     *
     * @param loan loan to add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean saveLent(Loan loan, Context context) {
        Log.i(TAG, "saveLent: " + loan.toJson());

        // delete from other lists if necessary
        lentList.remove(loan);
        borrowedList.remove(loan);
        returnedList.remove(loan);

        boolean result = lentList.add(loan);

        // if there is a context, then save list
        saveInSharedPreferences(context);

        if (result) {
            sortList(lentList);
            notifyLoanListsChanged();
        }

        return result;
    }


    /**
     * save a {@link Loan} from the Borrowed list
     *
     * @param loan loan from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean saveBorrowed(Loan loan, Context context) {
        Log.i(TAG, "saveBorrowed: " + loan.toJson());

        // delete from other lists if necessary
        lentList.remove(loan);
        borrowedList.remove(loan);
        returnedList.remove(loan);

        boolean result = borrowedList.add(loan);

        // if there is a context, then save list
        saveInSharedPreferences(context);

        if (result) {
            sortList(borrowedList);
            notifyLoanListsChanged();
        }

        return result;
    }


    /**
     * save a {@link Loan} from the Returned list
     *
     * @param loan    loan from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean saveReturned(Loan loan, Context context) {
        Log.i(TAG, "saveReturned: " + loan.toJson());

        // remove this loan from its previous lists
        lentList.remove(loan);
        borrowedList.remove(loan);
        returnedList.remove(loan);

        // add to returned list
        boolean result = returnedList.add(loan);

        // if there is a context, then save list
        saveInSharedPreferences(context);

        if (result) {
            sortList(returnedList);
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
        if (lentList.contains(loan)) {
            // loan removed from loan to list
            result = saveLent(loan, context);
            sortList(lentList);
        }
        if (borrowedList.contains(loan)) {
            // loan removed from loan from list
            result |= saveBorrowed(loan, context);
            sortList(borrowedList);
        }
        if (returnedList.contains(loan)) {
            // loan removed from loan returned list
            result |= saveReturned(loan, context);
            sortList(returnedList);
        }

        if (result) {
            notifyLoanListsChanged();
        }

        return result;
    }

    /**
     * Loan to list getter
     *
     * @return lent list
     */
    public ArrayList<Loan> getLentList() {
        return lentList;
    }

    /**
     * Borrowed list getter
     *
     * @return borrowed list
     */
    public ArrayList<Loan> getBorrowedList() {
        return borrowedList;
    }

    /**
     * Returned list getter
     *
     * @return returned list
     */
    public ArrayList<Loan> getReturnedList() {
        return returnedList;
    }

    /**
     * empty every lists
     *
     * @param context a {@link Context}
     */
    public void clearLists(Context context) {
        Log.i(TAG, "clearLists");

        saveInSharedPreferences(context);

        // empty lists
        lentList.clear();
        borrowedList.clear();
        returnedList.clear();

        // notify changes
        notifyLoanListsChanged();
    }

    /**
     * Delete a loan that may be present in lists
     *
     * @param loan loan to delete
     * @param context a context
     * @return delete result
     */
    public boolean deleteLoan(Loan loan, Context context) {

        // remove this loan from lists (based on its ID)
        boolean result = lentList.remove(loan);
        result |= borrowedList.remove(loan);
        result |= returnedList.remove(loan);

        if (result) {
            Log.i(TAG, "deleteLoan : " + loan.getItem() + " deleted.");
            notifyLoanListsChanged();
            saveInSharedPreferences(context);
        } else {
            Log.i(TAG, "deleteLoan : " + loan.getItem() + " not found, not deleted.");
        }

        return result;
    }

    /**
     * Save asynchronously loan lists in {@link SharedPreferences}
     * @param context a context
     */
    private void saveInSharedPreferences(Context context) {
        if (context != null) {
            // remove data from shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_LOAN_LISTS_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SHARED_PREFERENCES_LENT_ID, new Gson().toJson(lentList));
            editor.putString(SHARED_PREFERENCES_BORROWED_ID, new Gson().toJson(borrowedList));
            editor.putString(SHARED_PREFERENCES_RETURNED_ID, new Gson().toJson(returnedList));
            editor.apply();
        }
    }

    /**
     * empty return loan list
     *
     * @param context a {@link Context}
     */
    public void clearReturnedList(Context context) {
        Log.i(TAG, "clearReturnedList");

        // empty list
        returnedList.clear();
        saveInSharedPreferences(context);

        // notify changes
        notifyLoanListsChanged();
    }

    /**
     * Method called to notify changes
     */
    public void notifyLoanListsChanged() {
        for(OnLoanListsChangedListener listener : onLoanListsChangedListeners) {
            if (listener != null) {
                listener.onLoanListsChanged();
            }
        }
    }

    /**
     * Register a listener for changes in this {@link LoanLists}
     * @param listener listener to be registered
     * @param id id for log
     */
    public void registerOnLoanListsChangedListener(OnLoanListsChangedListener listener, String id) {
        Log.i(TAG, "Adding OnLoanListsChangedListener : " + id);
        onLoanListsChangedListeners.add(listener);
    }

    /**
     * Unregister a listener for changes in this {@link LoanLists}
     * @param listener listener to be unregistered
     * @param id id for log
     */
    public void unregisterOnLoanListsChangedListener(OnLoanListsChangedListener listener, String id) {
        Log.i(TAG, "Removing OnLoanListsChangedListener : " + id);
        onLoanListsChangedListeners.remove(listener);
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
        return lentList.size() + borrowedList.size() + returnedList.size();
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
        for(Loan loan : lentList) {
            if (loan.getId() == id) {
                result = loan;
            }
        }
        // in borrowed
        for(Loan loan : borrowedList) {
            if (loan.getId() == id) {
                result = loan;
            }
        }
        // in returned
        for(Loan loan : returnedList) {
            if (loan.getId() == id) {
                result = loan;
            }
        }

        return result;
    }
}
