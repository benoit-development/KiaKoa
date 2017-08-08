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
 */
public class LoanListUnitTest {

    /**
     * a {@link Context}
     */
    private final Context context = InstrumentationRegistry.getContext();

    final private LoanLists loanLists = LoanLists.getInstance();

    @Test
    public void test_instance() throws Exception {

        // test instance
        assertTrue(LoanLists.getInstance() != null);

    }

    @Test
    public void test_loan_lists() throws Exception {

        // init lists
        loanLists.initLists(context);

        // add some lent
        Loan loan1 = new Loan("1");
        assertTrue(loanLists.addLent(loan1, context));
        assertTrue(loanLists.addLent(new Loan("2"), context));
        assertTrue(loanLists.addLent(new Loan("3"), context));

        assertEquals(3, loanLists.getLentList().size());


        // add some borrowed
        Loan loan4 = new Loan("4");
        assertTrue(loanLists.addBorrowed(loan4, context));
        assertTrue(loanLists.addBorrowed(new Loan("5"), context));
        assertTrue(loanLists.addBorrowed(new Loan("6"), context));

        assertEquals(3, loanLists.getBorrowedList().size());


        // add some archive
        assertTrue(loanLists.addArchive(loan1, context));
        assertEquals(2, loanLists.getLentList().size());
        assertEquals(3, loanLists.getBorrowedList().size());
        assertEquals(1, loanLists.getArchiveList().size());
        assertEquals(6, loanLists.getLoanCount());

        assertTrue(loanLists.addArchive(loan4, context));
        assertEquals(2, loanLists.getLentList().size());
        assertEquals(2, loanLists.getBorrowedList().size());
        assertEquals(2, loanLists.getArchiveList().size());
        assertEquals(6, loanLists.getLoanCount());

        // clear lists
        loanLists.clearLists(context);
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getArchiveList().size());
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

        assertTrue(loanLists.addLent(new Loan("1"), context));
        assertTrue(loanLists.addLent(new Loan("2"), context));
        assertTrue(loanLists.addLent(new Loan("3"), context));

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
        loanLists.addLent(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(1, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getArchiveList().size());

        //update loan to list
        loanLists.clearLists(context);
        loanLists.addBorrowed(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(1, loanLists.getBorrowedList().size());
        assertEquals(0, loanLists.getArchiveList().size());

        //update loan to list
        loanLists.clearLists(context);
        loanLists.addArchive(loan, context);
        assertTrue(loanLists.updateLoan(loan, context));
        assertTrue(loanLists.updateLoan(sameLoan, context));
        assertFalse(loanLists.updateLoan(anotherLoan, context));
        assertEquals(0, loanLists.getLentList().size());
        assertEquals(0, loanLists.getBorrowedList().size());
        assertEquals(1, loanLists.getArchiveList().size());

    }

}