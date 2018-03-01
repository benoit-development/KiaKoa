package org.bbt.kiakoa.activity;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.R;

/**
 * Adapter for the {@link ListView} inside the {@link DrawerLayout} of this activity
 */
class DrawerLayoutAdapter extends BaseAdapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * inflater for layout
     */
    private final LayoutInflater inflater;

    /**
     * Constructor
     *
     * @param context a context
     */
    DrawerLayoutAdapter(Context context) {
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 3 types possibles TYPE_TITLE, TYPE_ITEM and TYPE_HEADER
     *
     * @return type count
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Return the type depending on the position
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        int itemViewType = getItemViewType(position);

        if (view == null) {
            holder = new ViewHolder();
            if (itemViewType == TYPE_TITLE) {
                view = inflater.inflate(R.layout.adapter_drawer_layout_title, viewGroup, false);
                view.setEnabled(false);
                view.setOnClickListener(null);

            } else {

                view = inflater.inflate(R.layout.adapter_drawer_layout_item, viewGroup, false);
                // Creates a ViewHolder
                holder.text = (TextView) view;

            }
            view.setTag(holder);
        } else {
            // Get the ViewHolder
            holder = (ViewHolder) view.getTag();
        }

        // Bind the data efficiently with the holder.
        int iconId = 0;
        int textId = 0;
        switch (position) {
            case 1:
                iconId = R.drawable.ic_settings_24dp;
                textId = R.string.settings;
                break;
            case 2:
                iconId = R.drawable.ic_delete_sweep_24dp;
                textId = R.string.clean_loan_lists;
                break;
            case 3:
                iconId = R.drawable.ic_about_24dp;
                textId = R.string.about;
                break;
        }
        if (iconId != 0) {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
        }
        if (textId != 0) {
            holder.text.setText(textId);
        }

        return view;
    }

    private class ViewHolder {
        TextView text;
    }

}
