package org.bbt.kiakoa.model;

import android.support.annotation.NonNull;

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
     * Get all loans in progress in a list
     */
    @NonNull
    public ArrayList<Loan> getInProgressLoanList() {
        ArrayList<Loan> result = new ArrayList<>();
        for (Loan loan : this) {
            if (!loan.isReturned()) {
                result.add(loan);
            }
        }
        return result;
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
