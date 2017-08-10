package org.bbt.kiakoa.fragment.LoanList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Loan archive fragment list
 *
 * @author Benoît BOUSQUET
 */
public class ArchivedListFragment extends AbstractLoanListFragment {

    /**
     * Tag for logs
     */
    private static final String TAG = "ArchivedListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Change "plus" button to a "empty" button
        fab.setImageResource(R.drawable.ic_delete_white_24dp);

        return view;
    }

    @Override
    public View.OnClickListener getFABOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "fabOnclickListener: empty archived list");
                Toast.makeText(getActivity(), "Empty list", Toast.LENGTH_LONG).show();
                LoanLists.getInstance().getArchivedList().clear();
                loanAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    protected ArrayList<Loan> getLoanList() {
        return LoanLists.getInstance().getArchivedList();
    }

    @Override
    protected String getLoanListId() {
        return LoanLists.SHARED_PREFERENCES_ARCHIVED_ID;
    }

    @Override
    protected void addLoan(Loan loan) {
        // this class does not directly add Loan
        // no action to do here
    }
}
