package org.bbt.kiakoa.fragment.LendList;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.bbt.kiakoa.R;

/**
 * Fragment displaying list of {@link org.bbt.kiakoa.model.Lend}
 */
abstract public class AbstractLendListFragment extends ListFragment {

    /**
     * Floating button used for various actions (add, empty, ...) depending on the list to display
     */
    protected FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.lend_list_fragment, container, false);

        // Attach floating button
        fab = inflate.findViewById(R.id.lend_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // customize list
        ((ListView) inflate.findViewById(android.R.id.list)).setEmptyView(inflate.findViewById(R.id.emptyElement));

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO set adapter to display lend list
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("AbstractLendListFragmen", "Item clicked: " + id);
    }

    /**
     * {@link android.view.View.OnClickListener} getter for {@link FloatingActionButton} on this {@link android.app.Fragment}
     * @return the listener
     */
    abstract public View.OnClickListener getFABOnClickListener();
}
