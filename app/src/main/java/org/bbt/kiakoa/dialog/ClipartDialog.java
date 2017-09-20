package org.bbt.kiakoa.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.Miscellaneous;

/**
 * Dialog used to add a contact to the loan
 */
public class ClipartDialog extends DialogFragment {

    /**
     * For log
     */
    private static final String TAG = "ClipartDialog";

    /**
     * {@link Loan} to update
     */
    private Loan loan;

    /**
     * Create a new instance of {@link ClipartDialog}
     *
     * @param loan loan
     */
    public static ClipartDialog newInstance(Loan loan) {
        ClipartDialog dialog = new ClipartDialog();
        Bundle args = new Bundle();
        args.putParcelable("loan", loan);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        loan = getArguments().getParcelable("loan");

        // Inflate view
        @SuppressLint
                ("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_clipart, null, false);

        // setup GridView
        GridView gridView = view.findViewById(R.id.grid_view);
        gridView.setAdapter(new ClipartAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "Selecting clipart : " + id + " (position " + position + ")");
                loan.setItemPicture(String.valueOf(id));
                LoanLists.getInstance().updateLoan(loan, getContext());
                dismiss();
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_brush_24dp)
                .setTitle(R.string.clipart)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancelling clipart selection");
                    }
                })
                .create();
    }

    /**
     * Adapter for the GridView displaying clipart
     */
    private class ClipartAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Miscellaneous.clipartList.size();
        }

        @Override
        public Object getItem(int position) {
            return Miscellaneous.clipartList.get(position).getValue();
        }

        @Override
        public long getItemId(int position) {
            return Miscellaneous.clipartList.get(position).getKey();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ViewHolder holder;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_image_clipart, viewGroup, false);
                holder = new ViewHolder();
                holder.image = convertView.findViewById(R.id.alert);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setImageResource((Integer) getItem(position));

            return convertView;
        }

        /**
         * View holder
         */
        private class ViewHolder {
            ImageView image;
        }
    }
}
