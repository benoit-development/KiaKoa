package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lend_details, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    /**
     * Set the lend to display in this fragment
     *
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
            getActivity().setTitle(lend.getItem());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lend_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("lend", lend);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            setLend((Lend) savedInstanceState.getParcelable("lend"));
        }
    }
}
