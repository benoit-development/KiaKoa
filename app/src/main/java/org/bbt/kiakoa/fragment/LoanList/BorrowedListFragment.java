package org.bbt.kiakoa.fragment.LoanList;

import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Loan from fragment list
 *
 * @author Benoît BOUSQUET
 */
public class BorrowedListFragment extends AbstractLoanListFragment {

    @Override
    protected ArrayList<Loan> getLoanList() {
        return LoanLists.getInstance().getBorrowedList();
    }

    @Override
    protected String getLoanListId() {
        return LoanLists.SHARED_PREFERENCES_BORROWED_ID;
    }

    @Override
    protected void addLoan(Loan loan) {
        LoanLists.getInstance().addBorrowed(loan, getContext());
    }
}
