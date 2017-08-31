package org.bbt.kiakoa.fragment.LoanList;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LoanItemDialog;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.ListItemClickRecyclerAdapter;

import java.util.ArrayList;

/**
 * Fragment displaying list of {@link Loan}
 */
abstract public class AbstractLoanListFragment extends Fragment implements LoanLists.OnLoanListsChangedListener {

    /**
     * Floating button used for various actions (add, empty, ...) depending on the list to display
     */
    FloatingActionButton fab;

    /**
     * Adapter used to display Loan list
     */
    private LoanRecyclerAdapter loanAdapter;

    /**
     * Displayed when no loan is is the list
     */
    private TextView emptyTextView;

    /**
     * {@link RecyclerView} displaying loans
     */
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_list, container, false);

        // Attach floating button
        fab = view.findViewById(R.id.loan_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // recycler
        recyclerView = view.findViewById(R.id.recycler);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter
        loanAdapter = new LoanRecyclerAdapter(getLoanList());
        loanAdapter.setOnItemClickListener(new ListItemClickRecyclerAdapter.OnListItemClickListener<Loan>() {

            @Override
            public void onListItemClick(Loan item) {
                Log.i(getLogTag(), "Item clicked: " + item.getItem());
                ((MainActivity) getActivity()).displayLoanDetails(item);
            }

        });
        recyclerView.setAdapter(loanAdapter);

        // empty text view
        emptyTextView = view.findViewById(R.id.empty_element);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loanAdapter.notifyDataSetChanged();
        updateView();
        LoanLists.getInstance().registerOnLoanListsChangedListener(this, getLogTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        LoanLists.getInstance().unregisterOnLoanListsChangedListener(this, getLogTag());
    }

    /**
     * Abstract class to define tag for logs
     * @return tag
     */
    protected abstract String getLogTag();

    /**
     * Method to update view visibility
     */
    void updateView() {
        if (loanAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    /**
     * {@link android.view.View.OnClickListener} getter for {@link FloatingActionButton} on this {@link android.app.Fragment}
     *
     * @return the listener
     */
    View.OnClickListener getFABOnClickListener() {
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
        loanAdapter.setItemList(getLoanList());
        loanAdapter.notifyDataSetChanged();
        updateView();
    }
}
