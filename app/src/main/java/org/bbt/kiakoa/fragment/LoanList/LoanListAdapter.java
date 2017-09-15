package org.bbt.kiakoa.fragment.LoanList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the loan list {@link RecyclerView}
 */
class LoanListAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CARD = 1;

    /**
     * color list
     */
    private TypedArray colors;

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
            holder.text = convertView.findViewById(R.id.text);
            holder.initial = convertView.findViewById(R.id.initial);
            holder.picture = convertView.findViewById(R.id.picture);
            holder.alert = convertView.findViewById(R.id.alert);
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
                textId = R.string.in_progress;
            } else {
                textId = R.string.returned;
            }
            holder.text.setText(textId);

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
            holder.alert.setVisibility(View.GONE);
            // return date
            if (loan.hasReturnDate()) {
                holder.dateSeparator.setVisibility(View.VISIBLE);
                holder.returnDate.setVisibility(View.VISIBLE);
                holder.returnDate.setText(loan.getReturnDateString(context));
                // returnDate is > 0
                try {
                    if ((!loan.isReturned()) && (loan.getDatesDifferenceInDays() > 0)) {
                        holder.returnDate.setTextColor(ContextCompat.getColor(context, R.color.alertRedText));
                        holder.alert.setVisibility(View.VISIBLE);
                    } else {
                        holder.returnDate.setTextColor(ContextCompat.getColor(context, R.color.colorTextLight));
                        holder.alert.setVisibility(View.GONE);
                    }
                } catch (LoanException e) {
                    // no return date
                }
            } else {
                holder.dateSeparator.setVisibility(View.GONE);
                holder.returnDate.setVisibility(View.GONE);
            }

            // picture
            Uri picture = loan.getPicture();
            if (picture != null) {
                holder.picture.setImageURI(picture);
                holder.picture.setPadding(0, 0, 0, 0);
                holder.initial.setVisibility(View.GONE);
            } else {
                // initial
                holder.initial.setVisibility(View.VISIBLE);
                holder.initial.setText(loan.getItem().substring(0, 1).toUpperCase());
                holder.picture.setImageURI(null);
                ((GradientDrawable) holder.picture.getBackground()).setColor(pickColor(loan.getItem(), context));
            }
        }
        return convertView;
    }

    // Provide a reference to the views for a loan
    static class ViewHolder {

        TextView text;
        CircleImageView picture;
        TextView initial;
        ImageView alert;
        TextView item;
        TextView contact;
        TextView loanDate;
        TextView returnDate;
        ImageView dateSeparator;
    }


    /**
     * Pick a color with item name
     *
     * @param item    item
     * @param context a context
     * @return a color
     */
    private int pickColor(String item, Context context) {

        // init resources if not done
        if (colors == null) {
            colors = context.getResources().obtainTypedArray(R.array.letter_tile_colors);
        }
        // get color
        return colors.getColor(Math.abs(item.hashCode()) % colors.length(), colors.getIndex(0));
    }

}