package org.bbt.kiakoa.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link LoanList} unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoanListUnitTest {

    @Test
    public void test() {
        // init loan list
        LoanList list = new LoanList();
        Loan loan1 = new Loan(1, "1");
        list.add(loan1);
        Loan loan2 = new Loan(2, "2");
        loan2.setReturned();
        list.add(loan2);
        Loan loan3 = new Loan(3, "3");
        list.add(loan3);

        assertEquals(2, list.getInProgressCount());
        assertEquals(1, list.getReturnedCount());
        assertTrue(list.getInProgressLoanList().contains(loan1));
        assertFalse(list.getInProgressLoanList().contains(loan2));
        assertTrue(list.getInProgressLoanList().contains(loan3));
    }

}