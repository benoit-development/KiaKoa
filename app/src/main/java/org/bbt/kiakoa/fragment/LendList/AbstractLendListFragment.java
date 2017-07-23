package org.bbt.kiakoa.fragment.LendList;

import android.content.Context;
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
import org.bbt.kiakoa.model.Lend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment displaying list of {@link org.bbt.kiakoa.model.Lend}
 */
abstract public class AbstractLendListFragment extends ListFragment {

    /**
     * Tag for logs
     */
    private static final String TAG = "AbstractLendListFragmen";

    /**
     * Floating button used for various actions (add, empty, ...) depending on the list to display
     */
    FloatingActionButton fab;
    private LendArrayAdapter lendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.lend_list_fragment, container, false);

        // Attach floating button
        fab = inflate.findViewById(R.id.lend_list_fab);
        fab.setOnClickListener(getFABOnClickListener());

        // customize list
        ListView listView = inflate.findViewById(android.R.id.list);
        listView.setEmptyView(inflate.findViewById(R.id.emptyElement));
        lendAdapter = new LendArrayAdapter(getActivity(), getLendList());
        listView.setAdapter(lendAdapter);

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        lendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "Item clicked: " + id);
    }

    /**
     * Adapter for {@link ListView}
     */
    private class LendArrayAdapter extends ArrayAdapter<Lend> {

        final HashMap<String, Integer> mIdMap = new HashMap<>();

        public LendArrayAdapter(Context context,
                                List<Lend> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i).toString(), i);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    /**
     * {@link android.view.View.OnClickListener} getter for {@link FloatingActionButton} on this {@link android.app.Fragment}
     * @return the listener
     */
    abstract protected View.OnClickListener getFABOnClickListener();

    /**
     * list used to display the lend list of this {@link ListFragment}
     * @return the list to populate {@link ListView}
     */
    abstract protected ArrayList<Lend> getLendList();
}
