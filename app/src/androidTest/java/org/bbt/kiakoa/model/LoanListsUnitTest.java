package org.bbt.kiakoa.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Loan unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoanListsUnitTest {

    /**
     * a {@link Context}
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    /**
     * Test instance
     */
    private final LoanLists loanLists = LoanLists.getInstance();

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

        // clear lists
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
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

        //update loan to list
        loanLists.clearLists(context);
        loanLists.saveBorrowed(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(1, loanLists.getBorrowedList().size());

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
    }

    @Test
    public void export_import_to_json() {

        // loans to be exported and imported
        Loan lentLoan = new Loan(1, "lent");
        lentLoan.setContact(new Contact(1, "name", "uri"));
        Loan borrowedLoan = new Loan(2, "borrowed");

        // add to LoanLists
        loanLists.clearLists(context);
        loanLists.saveLent(lentLoan, context);
        loanLists.saveBorrowed(borrowedLoan, context);
        String jsonExport = loanLists.toJson();

        // clear lists
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLoanCount());

        // import json
        assertTrue(LoanLists.getInstance().fromJson(jsonExport, context));
        assertEquals(2, loanLists.getLoanCount());
        assertEquals(lentLoan, loanLists.getLentList().get(0));
        assertEquals(lentLoan.getContact().getId(), loanLists.getLentList().get(0).getContact().getId());
        assertEquals(lentLoan.getContact().getName(), loanLists.getLentList().get(0).getContact().getName());
        assertEquals(lentLoan.getContact().getPhotoUri(), loanLists.getLentList().get(0).getContact().getPhotoUri());
        assertEquals(borrowedLoan, loanLists.getBorrowedList().get(0));

    }

    @Test
    public void comparable_list() {
        LoanList list = new LoanList();

        // loans
        Loan loanReturned2 = new Loan("b");
        loanReturned2.setReturned();
        Loan loanReturned1 = new Loan("a");
        loanReturned1.setReturned();
        Loan loanNotReturned2 = new Loan("b");
        Loan loanNotReturned1 = new Loan("a");

        // add
        list.add(loanReturned2);
        list.add(loanReturned1);
        list.add(loanNotReturned2);
        list.add(loanNotReturned1);

        // sort
        Collections.sort(list, new Loan.LoanComparator());

        // test ^^
        assertEquals(list.get(0), loanNotReturned1);
        assertEquals(list.get(1), loanNotReturned2);
        assertEquals(list.get(2), loanReturned1);
        assertEquals(list.get(3), loanReturned2);

        // count returned loans
        assertEquals(2, list.getInProgressCount());
        assertEquals(2, list.getReturnedCount());
    }
}