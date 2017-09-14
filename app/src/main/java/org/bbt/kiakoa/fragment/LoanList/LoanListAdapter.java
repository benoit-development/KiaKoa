package org.bbt.kiakoa.fragment.LoanList;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.exception.LoanException;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanList;

/**
 * Adapter for the loan list {@link RecyclerView}
 */
class LoanListAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CARD = 1;

    /**
     * Loan list to display
     */
    private final LoanList loanList;

    @Override
    public int getCount() {
        return loanList.size() + 2;
    }

    @Override
    public Object getItem(int i) {
        int indexInLoanList;
        if (i <= loanList.getInProgressCount()) {
            indexInLoanList = i - 1;
        } else {
            indexInLoanList = i - 2;
        }
        return loanList.get(indexInLoanList);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == 0) || (position == loanList.getInProgressCount() + 1)) {
            return TYPE_HEADER;
        } else {
            return TYPE_CARD;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItemViewType(position) == TYPE_HEADER) {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_loan_item_separator, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_loan_item, parent, false);
            }
            holder.textView = convertView.findViewById(R.id.text);
            holder.pictureView = convertView.findViewById(R.id.picture);
            holder.alertView = convertView.findViewById(R.id.alert);
            holder.itemView = convertView.findViewById(R.id.item);
            holder.contactView = convertView.findViewById(R.id.contact);
            holder.durationView = convertView.findViewById(R.id.duration);
            holder.delayView = convertView.findViewById(R.id.delay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (getItemViewType(position) == TYPE_HEADER) {

            int textId;
            if (position == 0) {
                textId = R.string.in_progress;
            } else {
                textId = R.string.returned;
            }
            holder.textView.setText(textId);

        } else {

            // get item
            Loan loan = (Loan) getItem(position);

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
        return convertView;
    }

    // Provide a reference to the views for a loan
    static class ViewHolder {

        TextView textView;
        ImageView pictureView;
        ImageView alertView;
        TextView itemView;
        TextView contactView;
        TextView durationView;
        TextView delayView;
    }

    /**
     * Constructor
     *
     * @param loanList displayed loan list
     */
    LoanListAdapter(LoanList loanList) {
        this.loanList = loanList;
    }

}