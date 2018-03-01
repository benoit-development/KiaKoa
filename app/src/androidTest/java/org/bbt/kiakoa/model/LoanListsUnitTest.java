package org.bbt.kiakoa.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

    @After
    public void tearDown() throws Exception {
        LoanLists.getInstance().clearLists(context);
    }


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

    @Test
    public void is_lent() {
        Loan loan = new Loan(1, "lent");

        // no list
        assertFalse(loan.isLent());

        // lent
        loanLists.saveLent(loan, context);
        assertTrue(loan.isLent());

        // lent
        loanLists.saveBorrowed(loan, context);
        assertFalse(loan.isLent());
    }

    @Test
    public void test_purge() {
        LoanLists lists = LoanLists.getInstance();

        // loans
        // today
        Loan loanToday = new Loan(1, "today");
        // yesterday
        Loan loan1Day = new Loan(2, "2 days");
        Calendar oneDay = Calendar.getInstance();
        oneDay.add(Calendar.DATE, -2);
        loan1Day.setLoanDate(oneDay.getTimeInMillis());
        // last week
        Loan loan1Week = new Loan(3, "2 weeks");
        Calendar oneWeek = Calendar.getInstance();
        oneWeek.add(Calendar.DATE, -14);
        loan1Week.setLoanDate(oneWeek.getTimeInMillis());
        // last month
        Loan loan1Month = new Loan(4, "2 months");
        Calendar oneMonth = Calendar.getInstance();
        oneMonth.add(Calendar.MONTH, -2);
        loan1Month.setLoanDate(oneMonth.getTimeInMillis());
        // last year
        Loan loan1Year = new Loan(5, "1 year");
        Calendar oneYear = Calendar.getInstance();
        oneYear.add(Calendar.YEAR, -1);
        loan1Year.setLoanDate(oneYear.getTimeInMillis());

        lists.getLentList().add(loanToday);
        lists.getBorrowedList().add(loan1Day);
        lists.getLentList().add(loan1Week);
        lists.getBorrowedList().add(loan1Month);
        lists.getLentList().add(loan1Year);

        // purgeWeek
        lists.purgeWeek();
        assertNotNull(lists.findLoanById(1));
        assertNotNull(lists.findLoanById(2));
        assertNull(lists.findLoanById(3));
        assertNull(lists.findLoanById(4));
        assertNull(lists.findLoanById(5));

        lists.getLentList().add(loan1Week);
        lists.getBorrowedList().add(loan1Month);
        lists.getLentList().add(loan1Year);

        // purgeMonth
        lists.purgeMonth();
        assertNotNull(lists.findLoanById(1));
        assertNotNull(lists.findLoanById(2));
        assertNotNull(lists.findLoanById(3));
        assertNull(lists.findLoanById(4));
        assertNull(lists.findLoanById(5));

    }


}