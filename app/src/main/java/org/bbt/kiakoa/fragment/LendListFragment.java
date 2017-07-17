package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.R;

/**
 * Fragment displaying list of {@link org.bbt.kiakoa.model.Lend}
 */
public class LendListFragment extends ListFragment {

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static LendListFragment newInstance(int num) {
        LendListFragment f = new LendListFragment();
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, new String[]{"1", "2", "3", "4", "5", "6"}));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }
}
