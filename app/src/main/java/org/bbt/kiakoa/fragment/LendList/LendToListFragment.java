package org.bbt.kiakoa.fragment.LendList;

import android.content.Intent;
import android.view.View;

import org.bbt.kiakoa.LendFormActivity;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

import java.util.ArrayList;

/**
 * Lend to fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LendToListFragment extends AbstractLendListFragment {

    @Override
    public View.OnClickListener getFABOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LendFormActivity.class);
                intent.putExtra(LendFormActivity.EXTRA_LEND_LIST_ACTION, LendFormActivity.EXTRA_NEW_LEND_TO);
                startActivity(intent);
            }
        };
    }

    @Override
    protected ArrayList<Lend> getLendList() {
        return LendLists.getInstance().getLendToList();
    }
}
