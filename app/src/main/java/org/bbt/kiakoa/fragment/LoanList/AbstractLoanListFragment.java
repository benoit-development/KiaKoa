package org.bbt.kiakoa.fragment.LoanList;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.bbt.kiakoa.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LoanItemDialog;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.util.ArrayList;

/**
 * Fragment displaying list of {@link Loan}
 */
abstract public class AbstractLoanListFragment extends ListFragment implements LoanLists.OnLoanListsChangedListener, AdapterView.OnItemClickListener {

    /**
     * Adapter in charge of creating views
     */
    private LoanListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loan_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Attach floating button
        //Floating button used for various actions (add, empty, ...) depending on the list to display
        FloatingActionButton fab = view.findViewById(R.id.loan_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // adapter
        listAdapter = new LoanListAdapter(getLoanList());
        getListView().setAdapter(listAdapter);
        getListView().setOnItemClickListener(this);
        getListView().setEmptyView(view.findViewById(R.id.empty_element));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ((MainActivity) getActivity()).displayLoanDetails((Loan) adapterView.getItemAtPosition(position));
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
        LoanLists.getInstance().registerOnLoanListsChangedListener(this, getLogTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        LoanLists.getInstance().unregisterOnLoanListsChangedListener(this, getLogTag());
    }

    /**
     * Abstract class to define tag for logs
     *
     * @return tag
     */
    protected abstract String getLogTag();

    /**
     * {@link android.view.View.OnClickListener} getter for {@link FloatingActionButton} on this {@link android.app.Fragment}
     *
     * @return the listener
     */
    private View.OnClickListener getFABOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(getLoanListId());
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                LoanItemDialog newItemDialog = LoanItemDialog.newInstance(getLoanListId());
                newItemDialog.show(ft, getLoanListId());
            }
        };
    }

    /**
     * list used to display the loan list of this {@link ListFragment}
     *
     * @return the list to populate {@link ListView}
     */
    abstract protected ArrayList<Loan> getLoanList();

    /**
     * Id to identify list used in subclasses
     *
     * @return list id
     */
    abstract protected String getLoanListId();

    @Override
    public void onLoanListsChanged() {
        Log.i(getLogTag(), "Notified that loan lists changed");
        listAdapter.notifyDataSetChanged();
    }

}
