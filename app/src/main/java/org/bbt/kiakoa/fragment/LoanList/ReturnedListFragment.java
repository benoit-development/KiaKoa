package org.bbt.kiakoa.fragment.LoanList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.ClearReturnDialog;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Loan returned fragment list
 *
 * @author Benoît BOUSQUET
 */
public class ReturnedListFragment extends AbstractLoanListFragment {

    /**
     * Tag for logs
     */
    private static final String TAG = "ReturnedListFragment";

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

                Log.i(TAG, "fabOnclickListener: clear returned list");

                if (LoanLists.getInstance().getReturnedList().size() == 0) {
                    Log.i(TAG, "fabOnclickListener: list already cleared");
                    Toast.makeText(getContext(), R.string.return_loan_lists_already_empty, Toast.LENGTH_SHORT).show();
                } else {
                    // show dialog to confirm returned list clearing
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("clear_returned");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    ClearReturnDialog newDialog = ClearReturnDialog.newInstance();
                    newDialog.show(ft, "clear_returned");
                }
            }
        };
    }

    @Override
    protected ArrayList<Loan> getLoanList() {
        return LoanLists.getInstance().getReturnedList();
    }

    @Override
    protected String getLoanListId() {
        return LoanLists.SHARED_PREFERENCES_RETURNED_ID;
    }
}
