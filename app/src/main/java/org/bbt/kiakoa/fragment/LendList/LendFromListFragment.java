package org.bbt.kiakoa.fragment.LendList;

import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

import java.util.ArrayList;

/**
 * Lend from fragment list
 *
 * @author Benoît BOUSQUET
 */
public class LendFromListFragment extends AbstractLendListFragment {

    @Override
    protected ArrayList<Lend> getLendList() {
        return LendLists.getInstance().getLendFromList();
    }

    @Override
    protected String getLendListId() {
        return LendLists.LEND_FROM;
    }

    @Override
    protected void addLend(Lend lend) {
        LendLists.getInstance().addLendFrom(lend, getContext());
    }
}
