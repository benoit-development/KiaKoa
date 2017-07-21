package org.bbt.kiakoa.fragment.LendList;

import android.content.Intent;
import android.view.View;

import org.bbt.kiakoa.LendFormActivity;

/**
 * Lend from fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LendFromListFragment extends AbstractLendListFragment {

    @Override
    public View.OnClickListener getFABOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LendFormActivity.class);
                intent.putExtra(LendFormActivity.EXTRA_LEND_LIST_ACTION, LendFormActivity.EXTRA_NEW_LEND_FROM);
                startActivity(intent);
            }
        };
    }
}
