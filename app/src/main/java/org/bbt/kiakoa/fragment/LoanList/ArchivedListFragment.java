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
import org.bbt.kiakoa.dialog.ClearArchiveDialog;
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

                Log.i(TAG, "fabOnclickListener: clear archived list");

                if (LoanLists.getInstance().getArchivedList().size() == 0) {
                    Log.i(TAG, "fabOnclickListener: list already cleared");
                    Toast.makeText(getContext(), R.string.archive_loan_lists_already_empty, Toast.LENGTH_SHORT).show();
                } else {
                    // show dialog to confirm archive clearing
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("clear_archive");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    ClearArchiveDialog newDialog = ClearArchiveDialog.newInstance();
                    newDialog.show(ft, "clear_archive");
                }
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
}
