package org.bbt.kiakoa.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.SettingsFragment;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Loan unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoanUnitTest {

    private static final String LOAN_ITEM = "a label";
    private static final String LOAN_ANOTHER_ITEM = "a another label";
    final private Loan loanTest = new Loan(LOAN_ITEM);
    final private Contact contactTest = new Contact(17, "pouet", "photo_uri");
    /**
     * a {@link Context}
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void loan_parcelable() throws Exception {

        // get parcel from instance
        Parcel parcel = Parcel.obtain();
        loanTest.writeToParcel(parcel, loanTest.describeContents());
        parcel.setDataPosition(0);

        // build another instance from parcel
        Loan newLoan = Loan.CREATOR.createFromParcel(parcel);

        assertEquals(LOAN_ITEM, newLoan.getItem());
        assertEquals(loanTest.getId(), newLoan.getId());
        assertEquals(loanTest.getLoanDate(), newLoan.getLoanDate());
        // no contact
        assertNull(newLoan.getContact());

        // with a contact
        loanTest.setContact(contactTest);
        parcel = Parcel.obtain();
        loanTest.writeToParcel(parcel, loanTest.describeContents());
        parcel.setDataPosition(0);
        newLoan = Loan.CREATOR.createFromParcel(parcel);
        assertEquals(17, newLoan.getContact().getId());
        assertEquals("pouet", newLoan.getContact().getName());
        assertEquals("photo_uri", newLoan.getContact().getPhotoUri());
    }

    @Test
    public void get_picture() {
        // no contact
        assertNull(loanTest.getPicture());
        // contact without photo
        loanTest.setContact(new Contact("pouet"));
        assertNull(loanTest.getPicture());
        // contact with picture
        loanTest.setContact(contactTest);
        assertEquals(Uri.parse(contactTest.getPhotoUri()), loanTest.getPicture());
        // item with picture
        loanTest.setItemPicture("coucou");
        assertEquals(Uri.parse("coucou"), loanTest.getPicture());
    }

    @Test
    public void loan_class_definition() throws Exception {

        assertEquals(LOAN_ITEM, loanTest.getItem());

        loanTest.setItem(LOAN_ANOTHER_ITEM);
        assertEquals(LOAN_ANOTHER_ITEM, loanTest.getItem());
    }

    @Test
    public void json_test() throws Exception {

        // serialize then instantiate
        String json = loanTest.toJson();
        Loan newLoan = new Gson().fromJson(json, Loan.class);

        assertEquals(LOAN_ITEM, newLoan.getItem());
        assertEquals(loanTest.getId(), newLoan.getId());
    }

    @Test
    public void test_id() throws Exception {

        Loan anotherLoan = new Loan("Another Loan");

        long id1 = loanTest.getId();
        long id2 = anotherLoan.getId();

        assertNotEquals(id1, id2);
    }

    @Test
    public void test_equals() {
        // new lend with the same item but different ID
        Loan anotherLoan = new Loan(LOAN_ITEM);
        assertFalse(loanTest.equals(anotherLoan));

        // build another lend with same id
        Loan sameLoan = new Gson().fromJson(loanTest.toJson(), Loan.class);
        assertTrue(loanTest.equals(sameLoan));

        // equality must be used for Loan deletion. This case represents when Loan is passed and rebuild using parcel
        ArrayList<Loan> list = new ArrayList<>();
        list.add(loanTest);
        list.add(anotherLoan);
        assertEquals(2, list.size());
        list.remove(sameLoan);
        assertEquals(1, list.size());
    }

    @SuppressLint("ApplySharedPref")
    @Test
    public void get_level() {
        // clear sharedpreferences data
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.clear();
        editor.putBoolean(SettingsFragment.KEY_ENABLE_ALERTS, true);
        editor.commit();

        // get default values
        assertEquals(context.getResources().getInteger(R.integer.yellow_alert_default_value), LoanAlertLevel.getYellowLevel(context));
        assertEquals(context.getResources().getInteger(R.integer.red_alert_default_value), LoanAlertLevel.getRedLevel(context));

        // false values
        editor.putString(SettingsFragment.KEY_YELLOW_ALERT, "pouet");
        editor.putString(SettingsFragment.KEY_RED_ALERT, "prout");
        editor.commit();
        assertEquals(context.getResources().getInteger(R.integer.yellow_alert_default_value), LoanAlertLevel.getYellowLevel(context));
        assertEquals(context.getResources().getInteger(R.integer.red_alert_default_value), LoanAlertLevel.getRedLevel(context));

        // custom values
        editor.putString(SettingsFragment.KEY_YELLOW_ALERT, "10");
        editor.putString(SettingsFragment.KEY_RED_ALERT, "20");
        editor.commit();
        assertEquals(10, LoanAlertLevel.getYellowLevel(context));
        assertEquals(20, LoanAlertLevel.getRedLevel(context));

        // test method
        long dayInMillis = 86400000L;
        loanTest.setLoanDate(System.currentTimeMillis());
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 5 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 10 * dayInMillis);
        assertEquals(LoanAlertLevel.YELLOW, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 15 * dayInMillis);
        assertEquals(LoanAlertLevel.YELLOW, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 20 * dayInMillis);
        assertEquals(LoanAlertLevel.RED, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 25 * dayInMillis);
        assertEquals(LoanAlertLevel.RED, loanTest.getAlertLevel(context));

        // disable alerts
        editor.putBoolean(SettingsFragment.KEY_ENABLE_ALERTS, false);
        editor.commit();
        loanTest.setLoanDate(System.currentTimeMillis());
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 5 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 10 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 15 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 20 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
        loanTest.setLoanDate(System.currentTimeMillis() - 25 * dayInMillis);
        assertEquals(LoanAlertLevel.NONE, loanTest.getAlertLevel(context));
    }


    @SuppressLint("ApplySharedPref")
    @Test
    public void is_alerts_enabled() {
        // clear sharedpreferences data
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        // get default values
        assertTrue(LoanAlertLevel.isAlertActive(context));

        // force value
        editor.putBoolean(SettingsFragment.KEY_ENABLE_ALERTS, false);
        editor.commit();
        assertFalse(LoanAlertLevel.isAlertActive(context));
    }
}