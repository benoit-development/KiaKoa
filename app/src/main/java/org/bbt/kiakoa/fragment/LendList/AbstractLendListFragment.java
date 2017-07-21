package org.bbt.kiakoa.fragment.LendList;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, new String[]{"1", "2", "3", "4", "5", "6"}));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }
}
