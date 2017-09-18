package org.bbt.kiakoa.model;

import java.util.ArrayList;

/**
 * An {@link ArrayList} of {@link Loan} for the application usages. the {@link LoanLists} Manage a set of this class
 */
public class LoanList extends ArrayList<Loan> {

    /**
     * Get number in progress loan in this list
     *
     * @return count
     */
    public int getInProgressCount() {
        int count = 0;
        for (Loan loan : this) {
            count += (!loan.isReturned()) ? 1 : 0;
        }
        return count;
    }

    /**
     * Get number returned loan in this list
     *
     * @return count
     */
    public int getReturnedCount() {
        return size() - getInProgressCount();
    }

}
