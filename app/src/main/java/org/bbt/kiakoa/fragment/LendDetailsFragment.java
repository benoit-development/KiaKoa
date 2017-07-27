package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Lend;

/**
 * Activity displaying {@link LendDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LendDetailsFragment extends Fragment {

    /**
     * Current lend
     */
    private Lend lend;

    /**
     * To display item
     */
    private TextView itemTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_lend_details, container, false);

        // get useful view elements
        itemTextView = view.findViewById(R.id.item);

        return view;
    }

    /**
     * Set the lend to display in this fragment
     * @param lend lend to display
     */
    public void setLend(Lend lend) {
        this.lend = lend;
        updateView();
    }

    /**
     * Update the data of the view with current lend
     */
    private void updateView() {
        if (lend != null) {
            itemTextView.setText(lend.getItem());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lend_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
