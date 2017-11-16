package org.bbt.kiakoa.fragment.LoanList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import org.bbt.kiakoa.tools.Miscellaneous;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the loan list {@link RecyclerView}
 */
class LoanListAdapter extends BaseAdapter {


    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LOAN = 1;

    /**
     * For logs
     */
    private static final String TAG = "LoanListAdapter";

    /**
     * Loan list to display
     */
    private final LoanList loanList;

    /**
     * Constructor
     *
     * @param loanList displayed loan list
     */
    LoanListAdapter(LoanList loanList) {
        this.loanList = loanList;
    }

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
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        // header are not clickable
        return getItemViewType(position) == TYPE_LOAN;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == 0) || (position == loanList.getInProgressCount() + 1)) {
            return TYPE_HEADER;
        } else {
            return TYPE_LOAN;
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
            holder.text = convertView.findViewById(R.id.text);
            holder.emptyText = convertView.findViewById(R.id.empty_text);
            holder.initial = convertView.findViewById(R.id.initial);
            holder.clipart = convertView.findViewById(R.id.clipart);
            holder.circle = convertView.findViewById(R.id.circle);
            holder.item = convertView.findViewById(R.id.item);
            holder.contact = convertView.findViewById(R.id.contact);
            holder.loanDate = convertView.findViewById(R.id.loan_date);
            holder.dateSeparator = convertView.findViewById(R.id.date_separator);
            holder.returnDate = convertView.findViewById(R.id.return_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (getItemViewType(position) == TYPE_HEADER) {

            int textId;
            if (position == 0) {
                // in progress header
                textId = R.string.in_progress;
                if (loanList.getInProgressCount() > 0) {
                    holder.emptyText.setVisibility(View.GONE);
                } else {
                    holder.emptyText.setVisibility(View.VISIBLE);
                    holder.emptyText.setText(R.string.no_loan_in_progress);
                }
            } else {
                // returned header
                textId = R.string.returned;
                if (loanList.getReturnedCount() > 0) {
                    holder.emptyText.setVisibility(View.GONE);
                } else {
                    holder.emptyText.setVisibility(View.VISIBLE);
                    holder.emptyText.setText(R.string.no_loan_returned);
                }
            }
            holder.text.setText(textId);
            convertView.setEnabled(false);

        } else {

            // get item
            Loan loan = (Loan) getItem(position);

            // item
            holder.item.setText(loan.getItem());
            // contact
            Contact contact = loan.getContact();
            if ((contact != null)) {
                holder.contact.setText(contact.getName());
                holder.contact.setVisibility(View.VISIBLE);
            } else {
                holder.contact.setVisibility(View.GONE);
            }
            // loanDate
            holder.loanDate.setText(loan.getLoanDateString());
            // return date
            if (loan.hasReturnDate()) {
                holder.dateSeparator.setVisibility(View.VISIBLE);
                holder.returnDate.setVisibility(View.VISIBLE);
                holder.returnDate.setText(loan.getReturnDateString(context));
                // returnDate is > 0
                try {
                    if ((!loan.isReturned()) && (loan.getDatesDifferenceInDays() > 0)) {
                        holder.returnDate.setTextColor(ContextCompat.getColor(context, R.color.alertRedText));
                    } else {
                        holder.returnDate.setTextColor(ContextCompat.getColor(context, R.color.colorTextLight));
                    }
                } catch (LoanException e) {
                    // no return date
                }
            } else {
                holder.dateSeparator.setVisibility(View.GONE);
                holder.returnDate.setVisibility(View.GONE);
            }

            // circle
            holder.circle.setImageDrawable(null);
            holder.initial.setVisibility(View.GONE);
            holder.clipart.setVisibility(View.GONE);
            if (loan.isItemPictureDrawable()) {
                try {
                    int clipartIndex = Integer.valueOf(loan.getItemPictureSafe(context));
                    holder.clipart.setVisibility(View.VISIBLE);

                    holder.clipart.setImageResource(Miscellaneous.getClipartResId(clipartIndex));
                    ((GradientDrawable) holder.circle.getBackground()).setColor(loan.getColor(context));
                } catch (Exception e) {
                    Log.e(TAG, "Failed parsing clipart drawable index. Should not happen.");
                }
            } else {
                Uri picture = loan.getPicture(context);
                if (picture != null) {
                    holder.circle.setImageURI(picture);
                    holder.circle.setPadding(0, 0, 0, 0);
                } else {
                    // initial
                    holder.initial.setVisibility(View.VISIBLE);
                    holder.initial.setText(loan.getItem().substring(0, 1).toUpperCase());
                    holder.circle.setImageURI(null);
                    ((GradientDrawable) holder.circle.getBackground()).setColor(loan.getColor(context));
                }
            }
        }
        return convertView;
    }

    // Provide a reference to the views for a loan
    static class ViewHolder {

        TextView text;
        TextView emptyText;
        CircleImageView circle;
        TextView initial;
        ImageView clipart;
        TextView item;
        TextView contact;
        TextView loanDate;
        TextView returnDate;
        ImageView dateSeparator;

    }
}