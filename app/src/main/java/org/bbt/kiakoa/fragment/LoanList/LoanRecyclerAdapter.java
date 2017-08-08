package org.bbt.kiakoa.fragment.LoanList;

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
import org.bbt.kiakoa.model.Loan;

import java.util.ArrayList;

/**
 * Adapter for the loan list {@link RecyclerView}
 */
class LoanRecyclerAdapter extends RecyclerView.Adapter<LoanRecyclerAdapter.ViewHolder> {

    /**
     * For log
     */
    private static final String TAG = "LoanRecycler.ViewHolder";

    /**
     * lind list data set
     */
    private final ArrayList<Loan> loanList;

    /**
     * loan click listener
     */
    private OnLoanClickListener clickListener;

    // Provide a reference to the views for a loan
    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView pictureView;
        private final TextView itemView;
        private final TextView contactView;
        private final TextView durationView;
        private Loan loan;

        /**
         * Constructor
         * @param view view
         */
        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Loan view clicked");
                    if (clickListener != null) {
                        Log.i(TAG, "");
                        clickListener.onLoanClick(loan);
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
         * Set a new loan then update view
         * @param loan loan to display
         */
        public void setLoan(Loan loan) {
            this.loan = loan;
            // item
            itemView.setText(loan.getItem());
            // contact
            Contact contact = loan.getContact();
            if ((contact != null)) {
                contactView.setText(contact.getName());
                contactView.setVisibility(View.VISIBLE);
            } else {
                contactView.setVisibility(View.GONE);
            }
            // duration
            int days = loan.getDatesDifferenceInDays();
            durationView.setText(context.getResources().getQuantityString(R.plurals.plural_day, days, days));
            // pictureView
            Uri picture = loan.getPicture();
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
    LoanRecyclerAdapter(ArrayList<Loan> loanList) {
        this.loanList = loanList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LoanRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loan_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind the data efficiently with the holder.
        holder.setLoan(loanList.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return loanList.size();
    }

    /**
     * Listener interface used when a loan is clicked by the user
     */
    interface OnLoanClickListener {
        /**
         * Method called when a loan is clicked by user
         * @param loan clicked loan
         */
        void onLoanClick(Loan loan);
    }

    /**
     * Set {@link OnLoanClickListener}
     * @param listener listener
     */
    void setOnLoanClickListener(OnLoanClickListener listener) {
        clickListener = listener;
    }
}