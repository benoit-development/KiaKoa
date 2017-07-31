package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.LendList.LendArchiveListFragment;
import org.bbt.kiakoa.fragment.LendList.LendFromListFragment;
import org.bbt.kiakoa.fragment.LendList.LendToListFragment;
import org.bbt.kiakoa.model.LendLists;

/**
 * {@link android.app.Fragment} displaying lend lists
 *
 * @author Benoit Bousquet
 */
public class LendListsPagerFragment extends Fragment implements LendLists.OnLendListsChangedListener {

    /**
     * For log
     */
    private static final String TAG = "LendListsPagerFragment";

    /**
     * Pager adapter
     */
    private LendPagerAdapter lendPagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_list_pager, container, false);

        // get viewpager
        ViewPager viewPager = view.findViewById(R.id.pager);
        lendPagerAdapter = new LendPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(lendPagerAdapter);

        // Manage tabs of the view pager
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LendLists.getInstance().registerOnLendListsChangedListener(this, TAG);
    }

    @Override
    public void onLendListsChanged() {
        Log.i(TAG, "LendLists changed, update TabLayout titles");

        FragmentActivity activity = LendListsPagerFragment.this.getActivity();

        // LendLists to get lend numbers
        LendLists lendLists = LendLists.getInstance();

        // tablayout custom view
        for (int position=0; position<tabLayout.getTabCount(); position++) {

            TabLayout.Tab tabAt = tabLayout.getTabAt(position);
            View customView = tabAt.getCustomView();
            if (customView == null) {
                customView = activity.getLayoutInflater().inflate(R.layout.tab_custom_view, null, false);
                tabAt.setCustomView(customView);
            } else {
                customView.getTag();
            }
            // find useful views
            TextView titleView = customView.findViewById(android.R.id.text1);
            TextView badgeView = customView.findViewById(R.id.badge);
            String titleLabel;
            String badgeNb;

            switch (position) {
                case 0:
                    titleLabel = activity.getString(R.string.tab_title_lend_to);
                    int lendToNb = lendLists.getLendToList().size();
                    badgeNb = (lendToNb > 0) ? " " + lendToNb : null;
                    break;
                case 1:
                    titleLabel = activity.getString(R.string.tab_title_lend_from);
                    int lendFromNb = lendLists.getLendFromList().size();
                    badgeNb = (lendFromNb > 0) ? " " + lendFromNb : null;
                    break;
                case 2:
                    titleLabel = activity.getString(R.string.tab_title_archive);
                    int lendArchiveNb = lendLists.getLendArchiveList().size();
                    badgeNb = (lendArchiveNb > 0) ? " " + lendArchiveNb : null;
                    break;
                default:
                    titleLabel = "";
                    badgeNb = "";
                    break;
            }

            titleView.setText(titleLabel);
            badgeView.setText(badgeNb);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LendLists.getInstance().unregisterOnLendListsChangedListener(this, TAG);
    }

    /**
     * Adapter managing pager and fragments displayed inside
     */
    private class LendPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         *
         * @param fm fragment manager
         */
        private LendPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new LendToListFragment();
                    break;
                case 1:
                    fragment = new LendFromListFragment();
                    break;
                default:
                    fragment = new LendArchiveListFragment();
                    break;
            }

            return fragment;
        }
    }

}
