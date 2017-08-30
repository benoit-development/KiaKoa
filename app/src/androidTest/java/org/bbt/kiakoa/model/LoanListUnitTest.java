package org.bbt.kiakoa.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Loan unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoanListUnitTest {

    /**
     * a {@link Context}
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    private LoanLists loanLists = LoanLists.getInstance();

    @Test
    public void test_instance() throws Exception {

        // test instance
        assertTrue(LoanLists.getInstance() != null);

    }

    @Test
    public void test_loan_lists() throws Exception {

        // init lists
        loanLists.initLists(context);
        loanLists.clearLists(context);

        // add some lent
        Loan loan1 = new Loan("1");
        assertTrue(loanLists.saveLent(loan1, context));
        assertTrue(loanLists.saveLent(new Loan("2"), context));
        assertTrue(loanLists.saveLent(new Loan("3"), context));

        assertEquals(3, loanLists.getLentList().size());


        // add some borrowed
        Loan loan4 = new Loan("4");
        assertTrue(loanLists.saveBorrowed(loan4, context));
        assertTrue(loanLists.saveBorrowed(new Loan("5"), context));
        assertTrue(loanLists.saveBorrowed(new Loan("6"), context));

        assertEquals(3, loanLists.getBorrowedList().size());


        // add some returned loan
        assertTrue(loanLists.saveReturned(loan1, context));
        assertEquals(2, loanLists.getLentList().size());
        assertEquals(3, loanLists.getBorrowedList().size());
        assertEquals(1, loanLists.getReturnedList().size());
        assertEquals(6, loanLists.getLoanCount());

        assertTrue(loanLists.saveReturned(loan4, context));
        assertEquals(2, loanLists.getLentList().size());
        assertEquals(2, loanLists.getBorrowedList().size());
        assertEquals(2, loanLists.getReturnedList().size());
        assertEquals(6, loanLists.getLoanCount());

        // clear returned list
        loanLists.clearReturnedList(context);
        assertEquals(2, loanLists.getLentList().size());
        assertEquals(2, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getReturnedList().size());
        assertEquals(4, loanLists.getLoanCount());

        // clear lists
        assertTrue(loanLists.saveReturned(new Loan("7"), context));
        assertTrue(loanLists.saveReturned(new Loan("8"), context));
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getReturnedList().size());
        assertEquals(0, loanLists.getLoanCount());

    }


    @Test
    public void get_instance() throws Exception {

        // get a valid instance
        assertTrue(LoanLists.getInstance() != null);

    }

    @Test
    public void loan_lists_shared_preferences() {

        // clear SharedPreferences
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLentList().size());

        assertTrue(loanLists.saveLent(new Loan("1"), context));
        assertTrue(loanLists.saveLent(new Loan("2"), context));
        assertTrue(loanLists.saveLent(new Loan("3"), context));

        assertEquals(3, loanLists.getLentList().size());

    }

    @Test
    public void update_lists() {

        // clear lists
        loanLists.clearLists(context);

        // init loans for tests
        Loan loan = new Loan("a loan");
        Loan sameLoan = new Gson().fromJson(loan.toJson(), Loan.class);
        Loan anotherLoan = new Loan("another loan");

        // update on empty lists
        assertFalse(loanLists.updateLoan(loan, context));

        //update loan to list
        loanLists.clearLists(context);
        loanLists.saveLent(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(1, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getReturnedList().size());

        //update loan to list
        loanLists.clearLists(context);
        loanLists.saveBorrowed(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(1, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getReturnedList().size());

        //update loan to list
        loanLists.clearLists(context);
        loanLists.saveReturned(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(1, loanLists.getReturnedList().size());

    }

    @Test
    public void loan_states() {
        Loan loan = new Loan("test");

        // not in any list
        assertEquals(LoanStatus.NONE, loan.getStatus());
        assertFalse(loan.isReturned());

        // in lent list
        loanLists.saveLent(loan, context);
        assertEquals(LoanStatus.LENT, loan.getStatus());
        assertFalse(loan.isReturned());

        // in borrowed list
        loanLists.clearLists(context);
        loanLists.saveBorrowed(loan, context);
        assertEquals(LoanStatus.BORROWED, loan.getStatus());
        assertFalse(loan.isReturned());

        // moved in return list
        loanLists.saveReturned(loan, context);
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertTrue(loan.isReturned());
    }

    @Test
    public void delete() {
        loanLists.clearLists(context);
        Loan loan1 = new Loan(1, "pouet");
        Loan loan2 = new Loan(2, "prout");

        assertFalse(loanLists.deleteLoan(loan1, context));

        loanLists.saveLent(loan1, context);
        loanLists.saveBorrowed(loan2, context);
        assertTrue(loanLists.deleteLoan(loan1, context));
        assertEquals(1, loanLists.getLoanCount());

        loanLists.saveBorrowed(loan1, context);
        assertTrue(loanLists.deleteLoan(loan1, context));
        assertEquals(1, loanLists.getLoanCount());

        loanLists.saveReturned(loan1, context);
        assertTrue(loanLists.deleteLoan(loan1, context));
        assertEquals(1, loanLists.getLoanCount());
    }

    @Test
    public void export_import_to_json() {

        // loans to be exported and imported
        Loan lentLoan = new Loan(1, "lent");
        lentLoan.setContact(new Contact(1, "name", "uri"));
        Loan borrowedLoan = new Loan(2, "borrowed");
        Loan returnedLoan = new Loan(3, "returned");

        // add to LoanLists
        loanLists.clearLists(context);
        loanLists.saveLent(lentLoan, context);
        loanLists.saveBorrowed(borrowedLoan, context);
        loanLists.saveReturned(returnedLoan, context);
        String jsonExport = loanLists.toJson();

        // clear lists
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLoanCount());

        // import json
        assertTrue(LoanLists.fromJson(jsonExport, context));
        loanLists = LoanLists.getInstance();
        assertEquals(3, loanLists.getLoanCount());
        assertEquals(lentLoan, loanLists.getLentList().get(0));
        assertEquals(lentLoan.getContact().getId(), loanLists.getLentList().get(0).getContact().getId());
        assertEquals(lentLoan.getContact().getName(), loanLists.getLentList().get(0).getContact().getName());
        assertEquals(lentLoan.getContact().getPhotoUri(), loanLists.getLentList().get(0).getContact().getPhotoUri());
        assertEquals(borrowedLoan, loanLists.getBorrowedList().get(0));
        assertEquals(returnedLoan, loanLists.getReturnedList().get(0));

    }
}