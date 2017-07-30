package org.bbt.kiakoa.fragment.LendList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Lend;

import java.util.ArrayList;

/**
 * Adapter to display {@link org.bbt.kiakoa.model.Lend} List
 */
class LendListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Lend> lendList;
    private final LayoutInflater inflater;

    LendListAdapter(Context applicationContext, ArrayList<Lend> lendList) {
        this.context = applicationContext;
        this.lendList = lendList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return lendList.size();
    }

    @Override
    public Object getItem(int i) {
        return lendList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return lendList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.adapter_lend_item, viewGroup, false);

            // Creates a ViewHolder
            holder = new ViewHolder();
            holder.item = view.findViewById(R.id.item);
            holder.duration = view.findViewById(R.id.duration);

            view.setTag(holder);
        } else {
            // Get the ViewHolder
            holder = (ViewHolder) view.getTag();
        }

        // Bind the data efficiently with the holder.
        Lend lend = lendList.get(i);
        holder.item.setText(lend.getItem());
        int days = lend.getDatesDifferenceInDays();
        holder.duration.setText(context.getResources().getQuantityString(R.plurals.plural_day, days, days));

        return view;
    }

    private static class ViewHolder {
        TextView item;
        TextView duration;
    }
}
