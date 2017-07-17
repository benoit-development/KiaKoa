package org.bbt.kiakoa.fragment;

import android.app.ListFragment;

import org.bbt.kiakoa.model.Lend;

/**
 * {@link android.app.Fragment} displaying lend lists
 *
 * @author Benoit Bousquet
 */
public class LendListsPagerFragment extends ListFragment {


    /**
     * An interface to manage Lend item click
     */
    public interface OnItemSelectedListener {
        void onItemSelected(Lend lend);
    }
}
