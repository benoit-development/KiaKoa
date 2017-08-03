package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LendContactDialog;
import org.bbt.kiakoa.dialog.LendDateDialog;
import org.bbt.kiakoa.dialog.LendItemDialog;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

import java.text.DateFormat;

/**
 * Activity displaying {@link LendDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LendDetailsFragment extends ListFragment implements LendItemDialog.OnLendItemSetListener, LendDateDialog.OnLendDateSetListener, LendContactDialog.OnLendContactSetListener {

    private static final String TAG = "LendDetailsFragment";
    /**
     * Current lend
     */
    private Lend lend;

    /**
     * Instance of used {@link LendDetailsListAdapter}
     */
    private LendDetailsListAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_details_list, container, false);

        // customize list
        ListView listView = view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty_element));
        adapter = new LendDetailsListAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * Set the lend to display in this fragment
     *
     * @param lend lend to display
     */
    public void setLend(Lend lend) {
        this.lend = lend;
        updateView();
    }

    /**
     * Update the data of the view with current lend
     */
    private void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("lend", lend);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            setLend((Lend) savedInstanceState.getParcelable("lend"));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                Log.i(TAG, "Item modification requested");
                Fragment itemDialog = getFragmentManager().findFragmentByTag("item");
                if (itemDialog != null) {
                    ft.remove(itemDialog);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                LendItemDialog newItemDialog = LendItemDialog.newInstance(lend.getItem());
                newItemDialog.setOnLendItemSetListener(this);
                newItemDialog.show(ft, "item");

                break;
            case 1:
                Log.i(TAG, "Lend date modification requested");
                Fragment lendDateDialog = getFragmentManager().findFragmentByTag("lend_date");
                if (lendDateDialog != null) {
                    ft.remove(lendDateDialog);
                }

                // Create and show the dialog
                LendDateDialog newLendDateDialog = LendDateDialog.newInstance(lend.getLendDate());
                newLendDateDialog.setOnLendDateSetListener(this);
                newLendDateDialog.show(ft, "lend_date");

                break;
            case 2:
                Log.i(TAG, "Contact modification requested");
                Fragment contactDialog = getFragmentManager().findFragmentByTag("contact");
                if (contactDialog != null) {
                    ft.remove(contactDialog);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                // TODO get lend current contact name
                LendContactDialog newContactDialog = LendContactDialog.newInstance("");
                newContactDialog.setOnLendContactSetListener(this);
                newContactDialog.show(ft, "contact");

                break;
        }
    }

    @Override
    public void onItemSet(String item) {
        Log.i(TAG, "New item : " + item);
        lend.setItem(item);
        updateLend();
    }

    @Override
    public void onDateSet(long lendDate) {
        Log.i(TAG, "New lend date : " + lendDate);
        lend.setLendDate(lendDate);
        updateLend();
    }

    @Override
    public void onContactSet(String contact) {
        Log.i(TAG, "New lend contact : " + contact);
        // TODO update lend
    }

    /**
     * Update lend and notify for changes
     */
    private void updateLend() {
        LendLists.getInstance().updateLend(lend, getContext());
        adapter.notifyDataSetChanged();
        LendLists.getInstance().notifyLendListsChanged();
    }

    /**
     * Adapter in charge of displaying {@link Lend} details.
     * Here is the details displayed :
     * <ol>
     * <li>item</li>
     * <li>lend date</li>
     * </ol>
     */
    private class LendDetailsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (lend == null) {
                return 0;
            } else {
                return 3;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.adapter_lend_detail_item, viewGroup, false);

                // Creates a ViewHolder
                holder = new LendDetailsListAdapter.ViewHolder();
                holder.icon = view.findViewById(R.id.icon);
                holder.description = view.findViewById(R.id.description);
                holder.value = view.findViewById(R.id.value);

                view.setTag(holder);
            } else {
                // Get the ViewHolder
                holder = (ViewHolder) view.getTag();
            }

            // Bind the data efficiently with the holder.
            switch (i) {
                case 0:
                    // item
                    holder.icon.setImageResource(R.drawable.ic_item_gray_24dp);
                    holder.description.setText(R.string.item);
                    holder.value.setText(lend.getItem());
                    break;
                case 1:
                    // lend date
                    holder.icon.setImageResource(R.drawable.ic_event_gray_24dp);
                    holder.description.setText(R.string.lend_date);
                    holder.value.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(lend.getLendDate()));
                    break;
                case 2:
                    // lend contact
                    holder.icon.setImageResource(R.drawable.ic_contact_gray_24dp);
                    holder.description.setText(R.string.contact);
                    holder.value.setText(R.string.contact);
                    break;
            }

            return view;
        }

        private class ViewHolder {
            ImageView icon;
            TextView description;
            TextView value;
        }
    }
}
