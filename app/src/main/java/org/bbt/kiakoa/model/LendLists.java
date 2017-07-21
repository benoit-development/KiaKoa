package org.bbt.kiakoa.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

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
    private static final String LEND_TO = "lend_to";

    /**
     * Lend from list identifier for shared preferences
     */
    private static final String LEND_FROM = "lend_from";

    /**
     * Lend archive list identifier for shared preferences
     */
    private static final String LEND_ARCHIVE = "lend_archive";

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
     * Instance getter
     *
     * @return singleton instance
     */
    public static LendLists getInstance() {

        if (instance == null) {
            // Create the instance
            Log.i("LendLists", "getInstance: Creating instance");
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

            Log.i("LendLists", "initLists: init lists from shared preferences");

            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            lendToList = new Gson().fromJson(sharedPref.getString(LEND_TO, "[]"), new TypeToken<ArrayList<String>>() {
            }.getType());
            lendFromList = new Gson().fromJson(sharedPref.getString(LEND_FROM, "[]"), new TypeToken<ArrayList<String>>() {
            }.getType());
            lendArchiveList = new Gson().fromJson(sharedPref.getString(LEND_ARCHIVE, "[]"), new TypeToken<ArrayList<String>>() {
            }.getType());
        } else {

            Log.i("LendLists", "initLists: init empty lists");

            lendToList = new ArrayList<>();
            lendFromList = new ArrayList<>();
            lendArchiveList = new ArrayList<>();
        }

        Log.d("LendLists", "initLists: lend to count:      " + lendToList.size());
        Log.d("LendLists", "initLists: lend from count:    " + lendFromList.size());
        Log.d("LendLists", "initLists: lend archive count: " + lendArchiveList.size());
    }

    /**
     * Serialize instance to Json
     *
     * @return Json {@link String}
     */
    public String toJson() {
        return new Gson().toJson(this);
    }


    /**
     * add a {@link Lend} to the LendTo list
     *
     * @param lend lend to add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendTo(Lend lend, Context context) {
        Log.i("LendLists", "addLendTo: " + lend.toJson());
        boolean result = lendToList.add(lend);

        // if there is a context, then save list
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LEND_TO, new Gson().toJson(lendToList));
            editor.apply();
        }

        return result;
    }


    /**
     * add a {@link Lend} from the LendTo list
     *
     * @param lend lend from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendFrom(Lend lend, Context context) {
        Log.i("LendLists", "addLendFrom: " + lend.toJson());
        boolean result = lendFromList.add(lend);

        // if there is a context, then save list
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LEND_FROM, new Gson().toJson(lendFromList));
            editor.apply();
        }

        return result;
    }


    /**
     * add a {@link Lend} from the LendTo list
     *
     * @param lend lend from add
     * @param context a {@link Context}
     * @return if add is success or not
     */
    public boolean addLendArchive(Lend lend, Context context) {
        Log.i("LendLists", "addLendArchive: " + lend.toJson());

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
     * @param context a {@link Context}
     */
    public void clearLists(Context context) {
        Log.i("LendLists", "clearLists");

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
    }
}
