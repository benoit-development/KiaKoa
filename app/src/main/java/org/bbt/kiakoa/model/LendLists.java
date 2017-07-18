package org.bbt.kiakoa.model;

import android.content.Context;
import android.content.SharedPreferences;

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
     * lend list of loaned items
     */
    private ArrayList<Lend> lendToList;

    /**
     * lend list from loaned items
     */
    private ArrayList<Lend> lendFromList;

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
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            lendToList = new Gson().fromJson(sharedPref.getString(LEND_TO, "[]"), new TypeToken<ArrayList<String>>() {
            }.getType());
            lendFromList = new Gson().fromJson(sharedPref.getString(LEND_FROM, "[]"), new TypeToken<ArrayList<String>>() {
            }.getType());
        } else {
            lendToList = new ArrayList<>();
            lendFromList = new ArrayList<>();
        }
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
     * Lend to list getter
     *
     * @return lend to list
     */
    public ArrayList<Lend> getLendToList() {
        return lendToList;
    }

    /**
     * Lend to list getter
     *
     * @return lend from list
     */
    public ArrayList<Lend> getLendFromList() {
        return lendFromList;
    }

    /**
     * empty every lists
     * @param context a {@link Context}
     */
    public void clearLists(Context context) {
        if (context != null) {
            // remove data from shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences(LEND_LISTS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(LEND_TO);
            editor.remove(LEND_FROM);
            editor.apply();
        }

        // empty lists
        lendToList.clear();
        lendFromList.clear();
    }
}
