package org.bbt.kiakoa.model;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.bbt.kiakoa.exception.LoanException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

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

    /**
     * a {@link Context}
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    private static final String LOAN_ITEM = "a label";
    private static final String LOAN_ANOTHER_ITEM = "a another label";
    final private Loan loanTest = new Loan(LOAN_ITEM);
    final private Contact contactTest = new Contact(17, "pouet", "photo_uri");

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
        assertEquals(loanTest.getReturnDate(), newLoan.getReturnDate());
        assertEquals(loanTest.getColor(context), newLoan.getColor(context));
        assertFalse(newLoan.isReturned());
        // no contact
        assertNull(newLoan.getContact());

        // with a contact
        loanTest.setContact(contactTest);

        // with state as returned
        loanTest.setReturned();
        parcel = Parcel.obtain();
        loanTest.writeToParcel(parcel, loanTest.describeContents());
        parcel.setDataPosition(0);
        newLoan = Loan.CREATOR.createFromParcel(parcel);
        assertEquals(17, newLoan.getContact().getId());
        assertEquals("pouet", newLoan.getContact().getName());
        assertEquals("photo_uri", newLoan.getContact().getPhotoUri());
        assertTrue(newLoan.isReturned());

        // with a color forced
        loanTest.setColor(android.R.color.holo_red_light);
        parcel = Parcel.obtain();
        loanTest.writeToParcel(parcel, loanTest.describeContents());
        parcel.setDataPosition(0);
        newLoan = Loan.CREATOR.createFromParcel(parcel);
        assertEquals(android.R.color.holo_red_light, newLoan.getColor(context));
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

    @Test
    public void date_difference() {

        // with no return date
        loanTest.setReturnDate(-1);
        try {
            assertEquals(-1, loanTest.getDatesDifferenceInDays());
            assertTrue(false);
        } catch (LoanException e) {
            assertTrue(true);
        }

        // calendar instance
        final Calendar cal = Calendar.getInstance();

        try {
            // with a return date
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(0, loanTest.getDatesDifferenceInDays());
            // minus 5 days
            cal.add(Calendar.DAY_OF_MONTH, -5);
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(5, loanTest.getDatesDifferenceInDays());
            // plus 2 days
            cal.add(Calendar.DAY_OF_MONTH, 7);
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(-2, loanTest.getDatesDifferenceInDays());
        } catch (LoanException e) {
            assertTrue(false);
        }

    }

    @Test
    public void date_duration() {

        // with no return date
        loanTest.setReturnDate(-1);
        try {
            assertEquals(-1, loanTest.getDurationInDays());
            assertTrue(false);
        } catch (LoanException e) {
            assertTrue(true);
        }

        // calendar instance
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -3);

        try {
            // with a return date
            loanTest.setLoanDate(cal.getTimeInMillis());
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(0, loanTest.getDurationInDays());
            // minus 5 days
            cal.add(Calendar.DAY_OF_MONTH, -5);
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(0, loanTest.getDurationInDays()); // because date return was lower then loan date
            // plus 2 days
            cal.add(Calendar.DAY_OF_MONTH, 7);
            loanTest.setReturnDate(cal.getTimeInMillis());
            assertEquals(7, loanTest.getDurationInDays());
        } catch (LoanException e) {
            assertTrue(false);
        }

    }

    @Test
    public void has_contact_id() {
        loanTest.setContact(null);
        assertFalse(loanTest.hasContactId());

        loanTest.setContact(new Contact("Pouet"));
        assertFalse(loanTest.hasContactId());

        loanTest.setContact(new Contact(34, "Pouet", null));
        assertTrue(loanTest.hasContactId());
    }

    @Test
    public void has_contact() {
        loanTest.setContact(null);
        assertFalse(loanTest.hasContact());

        loanTest.setContact(new Contact(""));
        assertFalse(loanTest.hasContact());

        loanTest.setContact(new Contact("coucou"));
        assertTrue(loanTest.hasContact());
    }

    @Test
    public void is_valid() {

        loanTest.setItem(null);
        loanTest.setContact(null);

        // no item
        assertFalse(loanTest.isValid());
        //empty item
        loanTest.setItem("");
        assertFalse(loanTest.isValid());
        // with item
        loanTest.setItem("item");
        assertTrue(loanTest.isValid());

        // with invalid contact
        loanTest.setContact(new Contact(""));
        assertFalse(loanTest.isValid());
        // with valid contact
        loanTest.setContact(new Contact("contact"));
        assertTrue(loanTest.isValid());

    }
}