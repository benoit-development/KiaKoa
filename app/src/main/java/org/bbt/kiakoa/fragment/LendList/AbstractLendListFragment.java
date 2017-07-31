package org.bbt.kiakoa.fragment.LendList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.bbt.kiakoa.MainActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LendItemDialog;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

import java.util.ArrayList;

/**
 * Fragment displaying list of {@link org.bbt.kiakoa.model.Lend}
 */
abstract public class AbstractLendListFragment extends ListFragment implements LendLists.OnLendListsChangedListener {

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
    LendListAdapter lendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_lend_list, container, false);

        // Attach floating button
        fab = inflate.findViewById(R.id.lend_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // customize list
        ListView listView = inflate.findViewById(android.R.id.list);
        listView.setEmptyView(inflate.findViewById(R.id.empty_element));
        lendAdapter = new LendListAdapter(getActivity(), getLendList());
        listView.setAdapter(lendAdapter);

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        lendAdapter.notifyDataSetChanged();
        LendLists.getInstance().registerOnLendListsChangedListener(this, TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        LendLists.getInstance().unregisterOnLendListsChangedListener(this, TAG);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "Item clicked: " + id);
        ((MainActivity) getActivity()).displayLendDetails(getLendList().get(position));
    }

    /**
     * {@link android.view.View.OnClickListener} getter for {@link FloatingActionButton} on this {@link android.app.Fragment}
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
                    public void onLendSet(String item) {
                        addLend(new Lend(item));
                    }
                });
                newItemDialog.show(ft, getLendListId());
            }
        };
    }

    /**
     * list used to display the lend list of this {@link ListFragment}
     * @return the list to populate {@link ListView}
     */
    abstract protected ArrayList<Lend> getLendList();

    /**
     * Id to identify list used in subclasses
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
                public void onLendSet(String item) {
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
    }
}
