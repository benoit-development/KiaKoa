package org.bbt.kiakoa.fragment.LendList;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Contact;
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
            holder.picture = view.findViewById(R.id.picture);
            holder.item = view.findViewById(R.id.item);
            holder.contact = view.findViewById(R.id.contact);
            holder.duration = view.findViewById(R.id.duration);

            view.setTag(holder);
        } else {
            // Get the ViewHolder
            holder = (ViewHolder) view.getTag();
        }

        // Bind the data efficiently with the holder.
        Lend lend = lendList.get(i);
        // item
        holder.item.setText(lend.getItem());
        // contact
        Contact contact = lend.getContact();
        if ((contact != null)) {
            holder.contact.setText(contact.getName());
            holder.contact.setVisibility(View.VISIBLE);
        } else {
            holder.contact.setVisibility(View.GONE);
        }
        // duration
        int days = lend.getDatesDifferenceInDays();
        holder.duration.setText(context.getResources().getQuantityString(R.plurals.plural_day, days, days));
        // picture
        Uri picture = lend.getPicture();
        if (picture != null) {
            holder.picture.setImageURI(picture);
            holder.picture.setPadding(0, 0, 0, 0);
        } else {
            holder.picture.setImageResource(R.drawable.ic_item_24dp);
            int padding = (int) context.getResources().getDimension(R.dimen.list_item_icon_padding);
            holder.picture.setPadding(padding, padding, padding, padding);
        }

        return view;
    }

    private static class ViewHolder {
        ImageView picture;
        TextView item;
        TextView contact;
        TextView duration;
    }
}
