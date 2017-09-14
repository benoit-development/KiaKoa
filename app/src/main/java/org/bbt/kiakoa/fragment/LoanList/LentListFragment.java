package org.bbt.kiakoa.fragment.LoanList;

import org.bbt.kiakoa.model.LoanList;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Loan to fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LentListFragment extends AbstractLoanListFragment {

    @Override
    protected String getLogTag() {
        return "LentListFragment";
    }

    @Override
    protected LoanList getLoanList() {
        return LoanLists.getInstance().getLentList();
    }

    @Override
    protected String getLoanListId() {
        return LoanLists.SHARED_PREFERENCES_LENT_KEY;
    }
}
