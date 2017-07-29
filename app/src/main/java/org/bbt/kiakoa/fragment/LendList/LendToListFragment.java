package org.bbt.kiakoa.fragment.LendList;

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
    protected ArrayList<Lend> getLendList() {
        return LendLists.getInstance().getLendToList();
    }

    @Override
    protected String getLendListId() {
        return LendLists.LEND_TO;
    }

    @Override
    protected void addLend(Lend lend) {
        LendLists.getInstance().addLendTo(lend, getContext());
    }
}
