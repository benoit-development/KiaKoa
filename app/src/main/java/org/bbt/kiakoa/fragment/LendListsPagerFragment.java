package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.LendList.LendArchiveListFragment;
import org.bbt.kiakoa.fragment.LendList.LendFromListFragment;
import org.bbt.kiakoa.fragment.LendList.LendToListFragment;
import org.bbt.kiakoa.model.Lend;

/**
 * {@link android.app.Fragment} displaying lend lists
 *
 * @author Benoit Bousquet
 */
public class LendListsPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lend_list_pager, container, false);

        // get viewpager
        ViewPager viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new LendPagerAdapter(getChildFragmentManager()));

        // Manage tabs of the view pager
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    /**
     * Adapter managing pager and fragments displayed inside
     */
    private class LendPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
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

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title = "";
            FragmentActivity activity = LendListsPagerFragment.this.getActivity();
            switch (position) {
                case 0:
                    title = activity.getString(R.string.tab_title_lend_to);
                    break;
                case 1:
                    title = activity.getString(R.string.tab_title_lend_from);
                    break;
                case 2:
                    title = activity.getString(R.string.tab_title_archive);
                    break;
            }
            return title;
        }
    }


    /**
     * An interface to manage Lend item click
     */
    public interface OnItemSelectedListener {
        void onItemSelected(Lend lend);
    }

}
