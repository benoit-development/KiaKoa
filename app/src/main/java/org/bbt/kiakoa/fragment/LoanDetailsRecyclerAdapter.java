package org.bbt.kiakoa.fragment;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.tools.ItemClickRecyclerAdapter;

import java.text.DateFormat;

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
class LoanDetailsRecyclerAdapter extends ItemClickRecyclerAdapter<LoanDetailsRecyclerAdapter.ViewHolder> {

    /**
     * Loan to be displayed
     */
    private Loan loan;

    /**
     * Loan setter. setting a new loan will refresh adapter.
     *
     * @param loan loan details to be displayed
     */
    public void setLoan(Loan loan) {
        this.loan = loan;
        notifyDataSetChanged();
    }

    /**
     * View Holder class for the recycler
     */
    static class ViewHolder extends ItemClickRecyclerAdapter.ViewHolder {
        final ImageView icon;
        final TextView description;
        final TextView value;
        final ImageView image;

        /**
         * Constructor
         *
         * @param view view
         */
        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            description = view.findViewById(R.id.description);
            value = view.findViewById(R.id.value);
            image = view.findViewById(R.id.image);
        }
    }

    @Override
    public int getItemCount() {
        if (loan != null) {
            return 4;
        } else {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loan_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        // Bind the data efficiently with the holder.
        switch (position) {
            case 0:
                // item
                holder.icon.setImageResource(R.drawable.ic_item_24dp);
                holder.description.setText(R.string.item);
                holder.value.setText(loan.getItem());
                holder.image.setVisibility(View.GONE);
                break;
            case 1:
                // loan item picture
                holder.description.setText(R.string.picture);
                holder.icon.setImageResource(R.drawable.ic_picture_24dp);
                String pictureUri = loan.getItemPicture();
                if (pictureUri != null) {
                    holder.image.setImageURI(Uri.parse(pictureUri));
                    holder.image.setVisibility(View.VISIBLE);
                    holder.value.setVisibility(View.INVISIBLE);
                } else {
                    holder.image.setVisibility(View.GONE);
                    holder.value.setVisibility(View.VISIBLE);
                    holder.value.setText(R.string.no_picture);
                }
                break;
            case 2:
                // loan date
                holder.icon.setImageResource(R.drawable.ic_event_24dp);
                holder.description.setText(R.string.loan_date);
                holder.value.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(loan.getLoanDate()));
                holder.image.setVisibility(View.GONE);
                break;
            case 3:
                // loan contact
                holder.description.setText(R.string.contact);
                holder.image.setVisibility(View.GONE);
                holder.icon.setImageResource(R.drawable.ic_contact_24dp);
                // contact details
                Contact contact = loan.getContact();
                if (contact != null) {
                    // set name if possible
                    holder.value.setText(contact.getName());
                    // set image if possible
                    String photoUri = contact.getPhotoUri();
                    if (photoUri != null) {
                        holder.image.setImageURI(Uri.parse(photoUri));
                        holder.image.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.value.setText(R.string.no_contact);
                }
                break;
        }
    }

}
