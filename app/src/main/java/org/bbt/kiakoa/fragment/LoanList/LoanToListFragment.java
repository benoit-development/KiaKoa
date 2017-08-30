package org.bbt.kiakoa.fragment.LoanList;

import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Loan to fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LoanToListFragment extends AbstractLoanListFragment {

    @Override
    protected ArrayList<Loan> getLoanList() {
        return LoanLists.getInstance().getLentList();
    }

    @Override
    protected String getLoanListId() {
        return LoanLists.SHARED_PREFERENCES_LENT_KEY;
    }
}
