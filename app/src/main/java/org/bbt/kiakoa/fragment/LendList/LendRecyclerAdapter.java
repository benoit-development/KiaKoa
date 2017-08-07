package org.bbt.kiakoa.fragment.LendList;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Lend;

import java.util.ArrayList;

/**
 * Adapter for the lend list {@link RecyclerView}
 */
class LendRecyclerAdapter extends RecyclerView.Adapter<LendRecyclerAdapter.ViewHolder> {

    /**
     * For log
     */
    private static final String TAG = "LendRecycler.ViewHolder";

    /**
     * lind list data set
     */
    private final ArrayList<Lend> lendList;

    /**
     * lend click listener
     */
    private OnLendClickListener clickListener;

    // Provide a reference to the views for a lend
    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView pictureView;
        private final TextView itemView;
        private final TextView contactView;
        private final TextView durationView;
        private Lend lend;

        /**
         * Constructor
         * @param view view
         */
        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Lend view clicked");
                    if (clickListener != null) {
                        Log.i(TAG, "");
                        clickListener.onLendClick(lend);
                    } else {
                        Log.w(TAG, "No listener to call :-(");
                    }
                }
            });
            this.context = view.getContext();
            this.pictureView = view.findViewById(R.id.picture);
            this.itemView = view.findViewById(R.id.item);
            this.contactView = view.findViewById(R.id.contact);
            this.durationView = view.findViewById(R.id.duration);
        }

        /**
         * Set a new lend then update view
         * @param lend lend to display
         */
        public void setLend(Lend lend) {
            this.lend = lend;
            // item
            itemView.setText(lend.getItem());
            // contact
            Contact contact = lend.getContact();
            if ((contact != null)) {
                contactView.setText(contact.getName());
                contactView.setVisibility(View.VISIBLE);
            } else {
                contactView.setVisibility(View.GONE);
            }
            // duration
            int days = lend.getDatesDifferenceInDays();
            durationView.setText(context.getResources().getQuantityString(R.plurals.plural_day, days, days));
            // pictureView
            Uri picture = lend.getPicture();
            if (picture != null) {
                pictureView.setImageURI(picture);
                pictureView.setPadding(0, 0, 0, 0);
            } else {
                pictureView.setImageResource(R.drawable.ic_item_24dp);
                int padding = (int) context.getResources().getDimension(R.dimen.list_item_icon_padding);
                pictureView.setPadding(padding, padding, padding, padding);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    LendRecyclerAdapter(ArrayList<Lend> lendList) {
        this.lendList = lendList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LendRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lend_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind the data efficiently with the holder.
        holder.setLend(lendList.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lendList.size();
    }

    /**
     * Listener interface used when a lend is clicked by the user
     */
    interface OnLendClickListener {
        /**
         * Method called when a lend is clicked by user
         * @param lend clicked lend
         */
        void onLendClick(Lend lend);
    }

    /**
     * Set {@link OnLendClickListener}
     * @param listener listener
     */
    void setOnLendClickListener (OnLendClickListener listener) {
        clickListener = listener;
    }
}