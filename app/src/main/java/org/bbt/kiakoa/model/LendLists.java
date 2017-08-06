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
 * Class managing all current lends
 */
public class LendLists {

    /**
     * Lend to lists identifier for shared preferences
     */
    private static final String LEND_LISTS = "lend_lists";

    /**
     * Lend to list identifier for shared preferences
     */
    public static final String LEND_TO = "lend_to";

    /**
     * Lend from list identifier for shared preferences
     */
    public static final String LEND_FROM = "lend_from";

    /**
     * Lend archive list identifier for shared preferences
     */
    public static final String LEND_ARCHIVE = "lend_archive";

    /**
     * Tag for logs
     */
    private static final String TAG = "LendLists";

    /**
     * lend list of loaned items
     */
    private ArrayList<Lend> lendToList;

    /**
     * lend list from loaned items
     */
    private ArrayList<Lend> lendFromList;

    /**
     * lend list archive loaned items
     */
    private ArrayList<Lend> lendArchiveList;

    /**
     * Singleton instance
     */
    private static LendLists instance;

    /**
     * Lend Comparator based on lend date
     */
    private final Comparator<Lend> lendDateComparator = new Comparator<Lend>() {
        @Override
        public int compare(Lend lend1, Lend lend2) {
            return Long.compare(lend1.getLendDate(), lend2.getLendDate());
        }
    };

    /**
     * Listener lend lists changed
     */
    private final ArrayList<OnLendListsChangedListener> onLendListsChangedListeners = new ArrayList<>();

    /**
     * Instance getter
     *
     * @return singleton instance
     */
    public static LendLists getInstance() {

        if (instance == null) {
            // Create the instance
            Log.i(TAG, "getInstance: Creating instance");
            instance = new LendLists();
        }

        return instance;
    }

    /**
     * Hidden constructor
     */
    private LendLists() {

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

            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            lendToList = new Gson().fromJson(sharedPref.getString(LEND_TO, "[]"), new TypeToken<ArrayList<Lend>>() {
            }.getType());
            lendFromList = new Gson().fromJson(sharedPref.getString(LEND_FROM, "[]"), new TypeToken<ArrayList<Lend>>() {
            }.getType());
            lendArchiveList = new Gson().fromJson(sharedPref.getString(LEND_ARCHIVE, "[]"), new TypeToken<ArrayList<Lend>>() {
            }.getType());

            sortLists();

        } else {

            Log.i(TAG, "initLists: init empty lists");

            lendToList = new ArrayList<>();
            lendFromList = new ArrayList<>();
            lendArchiveList = new ArrayList<>();
        }

        Log.d(TAG, "initLists: lend to count:      " + lendToList.size());
        Log.d(TAG, "initLists: lend from count:    " + lendFromList.size());
        Log.d(TAG, "initLists: lend archive count: " + lendArchiveList.size());
    }

    /**
     * Sort all the lists
     */
    private void sortLists() {
        sortList(lendToList);
        sortList(lendFromList);
        sortList(lendArchiveList);
    }

    /**
     * Sort a list depending on the chosen sort
     * @param lendList list to be sorted
     */
    private void sortList(ArrayList<Lend> lendList) {
        Collections.sort(lendList, lendDateComparator);
    }

    /**
     * add a {@link Lend} to the LendTo list
     *
     * @param lend    lend to add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendTo(Lend lend, Context context) {
        Log.i(TAG, "addLendTo: " + lend.toJson());
        boolean result = lendToList.add(lend);

        // if there is a context, then save list
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LEND_TO, new Gson().toJson(lendToList));
            editor.apply();
        }

        if (result) {
            sortList(lendToList);
            notifyLendListsChanged();
        }

        return result;
    }


    /**
     * add a {@link Lend} from the LendTo list
     *
     * @param lend    lend from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendFrom(Lend lend, Context context) {
        Log.i(TAG, "addLendFrom: " + lend.toJson());
        boolean result = lendFromList.add(lend);

        // if there is a context, then save list
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LEND_FROM, new Gson().toJson(lendFromList));
            editor.apply();
        }

        if (result) {
            sortList(lendFromList);
            notifyLendListsChanged();
        }

        return result;
    }


    /**
     * add a {@link Lend} from the LendTo list
     *
     * @param lend    lend from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendArchive(Lend lend, Context context) {
        Log.i(TAG, "addLendArchive: " + lend.toJson());

        // remove this lend from its previous lists
        lendToList.remove(lend);
        lendFromList.remove(lend);

        // add to archive list
        boolean result = lendArchiveList.add(lend);

        // if there is a context, then save list
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LEND_ARCHIVE, new Gson().toJson(lendArchiveList));
            editor.apply();
        }

        if (result) {
            sortList(lendArchiveList);
            notifyLendListsChanged();
        }

        return result;
    }

    /**
     * Update a {@link Lend} present in one of the lists
     *
     * @param lend    {@link Lend} to update
     * @param context a {@link Context}
     * @return update result (success or not)
     */
    public boolean updateLend(Lend lend, Context context) {
        boolean result = false;

        // remove this lend from lists (based on its ID)
        if (lendToList.remove(lend)) {
            // lend removed from lend to list
            result = addLendTo(lend, context);
            sortList(lendToList);
        }
        if (lendFromList.remove(lend)) {
            // lend removed from lend from list
            result |= addLendFrom(lend, context);
            sortList(lendFromList);
        }
        if (lendArchiveList.remove(lend)) {
            // lend removed from lend archive list
            result |= addLendArchive(lend, context);
            sortList(lendArchiveList);
        }

        if (result) {
            notifyLendListsChanged();
        }

        return result;
    }

    /**
     * Lend to list getter
     *
     * @return lend to list
     */
    public ArrayList<Lend> getLendToList() {
        return lendToList;
    }

    /**
     * Lend from list getter
     *
     * @return lend from list
     */
    public ArrayList<Lend> getLendFromList() {
        return lendFromList;
    }

    /**
     * Lend archive list getter
     *
     * @return lend archive list
     */
    public ArrayList<Lend> getLendArchiveList() {
        return lendArchiveList;
    }

    /**
     * empty every lists
     *
     * @param context a {@link Context}
     */
    public void clearLists(Context context) {
        Log.i(TAG, "clearLists");

        if (context != null) {
            // remove data from shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(LEND_TO);
            editor.remove(LEND_FROM);
            editor.remove(LEND_ARCHIVE);
            editor.apply();
        }

        // empty lists
        lendToList.clear();
        lendFromList.clear();
        lendArchiveList.clear();

        // notify changes
        notifyLendListsChanged();
    }

    /**
     * Method called to notify changes
     */
    public void notifyLendListsChanged() {
        for(OnLendListsChangedListener listener : onLendListsChangedListeners) {
            if (listener != null) {
                listener.onLendListsChanged();
            }
        }
    }

    /**
     * Register a listener for changes in this {@link LendLists}
     * @param listener listener to be registered
     * @param id id for log
     */
    public void registerOnLendListsChangedListener(OnLendListsChangedListener listener, String id) {
        Log.i(TAG, "Adding OnLendListsChangedListener : " + id);
        onLendListsChangedListeners.add(listener);
    }

    /**
     * Unregister a listener for changes in this {@link LendLists}
     * @param listener listener to be unregistered
     * @param id id for log
     */
    public void unregisterOnLendListsChangedListener(OnLendListsChangedListener listener, String id) {
        Log.i(TAG, "Removing OnLendListsChangedListener : " + id);
        onLendListsChangedListeners.remove(listener);
    }

    /**
     * Listener when lend lists changed
     */
    public interface OnLendListsChangedListener {
        /**
         * Called when lend lists changed
         */
        void onLendListsChanged();
    }

    /**
     * to get the number of lends in all lists
     *
     * @return lend count
     */
    public int getLendCount() {
        return lendToList.size() + lendFromList.size() + lendArchiveList.size();
    }

}
