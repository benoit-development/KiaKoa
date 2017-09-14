package org.bbt.kiakoa.fragment.LoanDetails;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.exception.LoanException;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;

/**
 * Adapter in charge of displaying {@link Loan} details.
 * Here is the details displayed :
 * <ol>
 * <li>item</li>
 * <li>picture</li>
 * <li>loan date</li>
 * <li>contact</li>
 * </ol>
 */
class LoanDetailsAdapter extends BaseAdapter {

    /**
     * Loan to be displayed
     */
    private Loan loan;

    /**
     * Loan setter. setting a new loan will refresh adapter.
     *
     * @param loan loan details to be displayed
     */
    public void setLoan(@NonNull Loan loan) {
        this.loan = loan;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (loan != null) {
            return 6;
        } else {
            return 0;
        }
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
     * View Holder class for the recycler
     */
    static class ViewHolder {
        ImageView icon;
        TextView description;
        TextView value;
        ImageView image;
        ImageView circle;
        Switch switchView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_loan_detail_item, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.icon);
            holder.description = convertView.findViewById(R.id.description);
            holder.value = convertView.findViewById(R.id.value);
            holder.image = convertView.findViewById(R.id.image);
            holder.circle = convertView.findViewById(R.id.circle);
            holder.switchView = convertView.findViewById(R.id.switchView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // by default visible
        holder.value.setVisibility(View.VISIBLE);
        // by default hidden
        holder.image.setVisibility(View.GONE);
        holder.circle.setVisibility(View.GONE);
        holder.switchView.setVisibility(View.GONE);

        switch (position) {
            case 0:
                // item
                holder.icon.setImageResource(R.drawable.ic_item_24dp);
                holder.description.setText(R.string.item);
                holder.value.setText(loan.getItem());
                break;
            case 1:
                // returned
                holder.icon.setImageResource(R.drawable.ic_return_24dp);
                holder.description.setText(R.string.returned);
                holder.value.setVisibility(View.INVISIBLE);
                holder.switchView.setVisibility(View.VISIBLE);
                holder.switchView.setChecked(loan.isReturned());
                break;
            case 2:
                // loan picture
                holder.description.setText(R.string.picture);
                holder.icon.setImageResource(R.drawable.ic_picture_24dp);
                String pictureUri = loan.getItemPicture();
                if (pictureUri != null) {
                    holder.circle.setImageURI(Uri.parse(pictureUri));
                    holder.circle.setVisibility(View.VISIBLE);
                    holder.value.setText("");
                } else {
                    holder.value.setVisibility(View.VISIBLE);
                    holder.value.setText(R.string.no_picture);
                }
                break;
            case 3:
                // loan date
                holder.icon.setImageResource(R.drawable.ic_loan_date_24dp);
                holder.description.setText(R.string.loan_date);
                holder.value.setText(loan.getLoanDateString());
                break;
            case 4:
                // return date
                holder.icon.setImageResource(R.drawable.ic_return_date_24dp);
                holder.description.setText(R.string.return_date);
                holder.value.setText(loan.getReturnDateString(context));
                try {
                    if ((!loan.isReturned()) && (loan.getDatesDifferenceInDays() > 0)) {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.image.setImageResource(R.drawable.ic_alert_red_24dp);
                    }
                } catch (LoanException e) {
                    // nothing to do visibility already gone
                }
                break;
            case 5:
                // loan contact
                holder.description.setText(R.string.contact);
                holder.icon.setImageResource(R.drawable.ic_contact_24dp);
                // contact details
                Contact contact = loan.getContact();
                if (contact != null) {
                    // set name if possible
                    holder.value.setText(contact.getName());
                    // set circle if possible
                    String photoUri = contact.getPhotoUri();
                    if (photoUri != null) {
                        holder.circle.setImageURI(Uri.parse(photoUri));
                        holder.circle.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.value.setText(R.string.no_contact);
                }
                break;
        }

        return convertView;
    }

}
