package org.bbt.kiakoa.fragment.LoanList;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.exception.LoanException;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.tools.ListItemClickRecyclerAdapter;

import java.util.ArrayList;

/**
 * Adapter for the loan list {@link RecyclerView}
 */
class LoanRecyclerAdapter extends ListItemClickRecyclerAdapter<LoanRecyclerAdapter.ViewHolder, Loan> {

    /**
     * A context
     */
    private Context context;

    // Provide a reference to the views for a loan
    class ViewHolder extends ListItemClickRecyclerAdapter.ViewHolder {

        private final ImageView pictureView;
        private final ImageView alertView;
        private final TextView itemView;
        private final TextView contactView;
        private final TextView durationView;
        private final TextView delayView;

        /**
         * Constructor
         * @param view view
         */
        private ViewHolder(View view) {
            super(view);
            this.pictureView = view.findViewById(R.id.picture);
            this.alertView = view.findViewById(R.id.alert);
            this.itemView = view.findViewById(R.id.item);
            this.contactView = view.findViewById(R.id.contact);
            this.durationView = view.findViewById(R.id.duration);
            this.delayView = view.findViewById(R.id.delay);
        }
    }

    /**
     * Constructor
     * @param loanList displayed loan list
     */
    LoanRecyclerAdapter(ArrayList<Loan> loanList) {
        setItemList(loanList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        // get item
        Loan loan = getItem(position);

        // item
        holder.itemView.setText(loan.getItem());
        // contact
        Contact contact = loan.getContact();
        if ((contact != null)) {
            holder.contactView.setText(contact.getName());
            holder.contactView.setVisibility(View.VISIBLE);
        } else {
            holder.contactView.setVisibility(View.GONE);
        }
        // duration
        String loanDateString = loan.getLoanDateString();
        holder.durationView.setText(loanDateString);
        holder.alertView.setVisibility(View.GONE);
        // delay
        if (loan.isReturned()) {
            holder.delayView.setText(loan.getDurationString(context));
        } else {
            holder.delayView.setText(loan.getReturnDateDelayString(context));
        }
        // delay is > 0
        holder.alertView.setVisibility(View.GONE);
        holder.delayView.setTextColor(ContextCompat.getColor(context, R.color.colorTextLight));
        try {
            if ((!loan.isReturned()) && (loan.getDatesDifferenceInDays() > 0)) {
                holder.delayView.setTextColor(ContextCompat.getColor(context, R.color.alertRedText));
                holder.alertView.setVisibility(View.VISIBLE);
            }
        } catch (LoanException e) {
            // no return date
        }

        // pictureView
        Uri picture = loan.getPicture();
        if (picture != null) {
            holder.pictureView.setImageURI(picture);
            holder.pictureView.setPadding(0, 0, 0, 0);
        } else {
            holder.pictureView.setImageResource(R.drawable.ic_item_24dp);
            int padding = (int) context.getResources().getDimension(R.dimen.list_item_icon_padding);
            holder.pictureView.setPadding(padding, padding, padding, padding);
        }
    }
}