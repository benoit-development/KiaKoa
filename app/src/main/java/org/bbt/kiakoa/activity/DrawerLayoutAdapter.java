package org.bbt.kiakoa.activity;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Adapter for the {@link ListView} inside the {@link DrawerLayout} of this activity
 */
class DrawerLayoutAdapter extends BaseAdapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

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
        return 8;
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
        return 3;
    }

    /**
     * Return the type depending on the position
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        } else if ((position == 1) || (position == 4)) {
            return TYPE_HEADER;
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

            } else if (itemViewType == TYPE_HEADER) {

                view = inflater.inflate(R.layout.adapter_drawer_layout_header, viewGroup, false);
                view.setEnabled(false);
                view.setOnClickListener(null);
                // Creates a ViewHolderHeader
                holder.text = view.findViewById(R.id.text);
                // no icon by default
                holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            } else {

                view = inflater.inflate(R.layout.adapter_drawer_layout_item, viewGroup, false);
                // Creates a ViewHolder
                holder.icon = view.findViewById(R.id.icon);
                holder.text = view.findViewById(R.id.text);
                holder.badge = view.findViewById(R.id.clipart);

            }
            view.setTag(holder);
        } else {
            // Get the ViewHolder
            holder = (ViewHolder) view.getTag();
        }

        // Bind the data efficiently with the holder.
        int iconId = 0;
        int textId = 0;
        int loanCount = 0;
        switch (position) {
            case 1:
                textId = R.string.loan_lists;
                break;
            case 2:
                iconId = R.drawable.ic_lent_24dp;
                textId = R.string.lent;
                loanCount = LoanLists.getInstance().getLentList().getInProgressCount();
                break;
            case 3:
                iconId = R.drawable.ic_borrowed_24dp;
                textId = R.string.borrowed;
                loanCount = LoanLists.getInstance().getBorrowedList().getInProgressCount();
                break;
            case 4:
                textId = R.string.tools;
                break;
            case 5:
                iconId = R.drawable.ic_settings_24dp;
                textId = R.string.settings;
                break;
            case 6:
                iconId = R.drawable.ic_delete_forever_24dp;
                textId = R.string.clear_all_loan_lists;
                break;
            case 7:
                iconId = R.drawable.ic_about_24dp;
                textId = R.string.about;
                break;
        }
        if (iconId != 0) {
            holder.icon.setImageResource(iconId);
        }
        if (textId != 0) {
            holder.text.setText(textId);
        }
        if (loanCount != 0) {
            holder.badge.setText(String.valueOf(loanCount));
        }
        if (itemViewType == TYPE_ITEM) {
            holder.badge.setVisibility((loanCount == 0) ? View.GONE : View.VISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        ImageView icon;
        TextView text;
        TextView badge;
    }

}
