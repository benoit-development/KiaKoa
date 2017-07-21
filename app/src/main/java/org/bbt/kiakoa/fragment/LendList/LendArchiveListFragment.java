package org.bbt.kiakoa.fragment.LendList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bbt.kiakoa.R;

/**
 * Lend archive fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LendArchiveListFragment extends AbstractLendListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Change "plus" button to a "empty" button
        fab.setImageResource(R.drawable.ic_delete_white_24dp);

        return view;
    }

    @Override
    public View.OnClickListener getFABOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("LendArchiveListFragment", "fabOnclickListener: empty archive");
                Toast.makeText(getActivity(), "Empty list", Toast.LENGTH_LONG).show();
                // LendLists.getInstance().emptyArchive(getActivity());
            }
        };
    }
}
