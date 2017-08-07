package org.bbt.kiakoa.fragment.LendList;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import org.bbt.kiakoa.dialog.LendItemDialog;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Fragment displaying list of {@link org.bbt.kiakoa.model.Lend}
 */
abstract public class AbstractLendListFragment extends Fragment implements LendLists.OnLendListsChangedListener {

    /**
     * Tag for logs
     */
    private static final String TAG = "AbstractLendListFragmen";

    /**
     * Floating button used for various actions (add, empty, ...) depending on the list to display
     */
    FloatingActionButton fab;

    /**
     * Adapter used to display Lend list
     */
    LendRecyclerAdapter lendAdapter;

    /**
     * Displayed when no lend is is the list
     */
    private TextView emptyTextView;

    /**
     * {@link RecyclerView} displaying lends
     */
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_list, container, false);

        // Attach floating button
        fab = view.findViewById(R.id.lend_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // recycler
        recyclerView = view.findViewById(R.id.recycler);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter
        lendAdapter = new LendRecyclerAdapter(getLendList());
        lendAdapter.setOnLendClickListener(new LendRecyclerAdapter.OnLendClickListener() {
            @Override
            public void onLendClick(Lend lend) {
                Log.i(TAG, "Item clicked: " + lend.getItem());
                ((MainActivity) getActivity()).displayLendDetails(lend);
            }
        });
        recyclerView.setAdapter(lendAdapter);

        // empty text view
        emptyTextView = view.findViewById(R.id.empty_element);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lendAdapter.notifyDataSetChanged();
        updateView();
        LendLists.getInstance().registerOnLendListsChangedListener(this, TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        LendLists.getInstance().unregisterOnLendListsChangedListener(this, TAG);
    }

    /**
     * Method to update view visibility
     */
    protected void updateView() {
        if (lendAdapter.getItemCount() == 0) {
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

                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(getLendListId());
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                LendItemDialog newItemDialog = LendItemDialog.newInstance();
                newItemDialog.setOnLendItemSetListener(new LendItemDialog.OnLendItemSetListener() {
                    @Override
                    public void onItemSet(String item) {
                        Lend lend = new Lend(item);
                        addLend(lend);
                        ((MainActivity) getActivity()).displayLendDetails(lend);
                    }
                });
                newItemDialog.show(ft, getLendListId());
            }
        };
    }

    /**
     * list used to display the lend list of this {@link ListFragment}
     *
     * @return the list to populate {@link ListView}
     */
    abstract protected ArrayList<Lend> getLendList();

    /**
     * Id to identify list used in subclasses
     *
     * @return list id
     */
    abstract protected String getLendListId();

    /**
     * Add a new created {@link Lend} to {@link LendLists}
     */
    abstract protected void addLend(Lend lend);

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // attach this activity to the dialog if exists
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(getLendListId());
        if ((prev != null) && (prev instanceof LendItemDialog)) {
            ((LendItemDialog) prev).setOnLendItemSetListener(new LendItemDialog.OnLendItemSetListener() {
                @Override
                public void onItemSet(String item) {
                    addLend(new Lend(item));
                    lendAdapter.notifyDataSetChanged();
                }
            });
        }
        ft.commit();
    }

    @Override
    public void onLendListsChanged() {
        lendAdapter.notifyDataSetChanged();
        updateView();
    }
}
