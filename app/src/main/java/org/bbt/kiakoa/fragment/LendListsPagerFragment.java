package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.model.Lend;

/**
 * {@link android.app.Fragment} displaying lend lists
 *
 * @author Benoit Bousquet
 */
public class LendListsPagerFragment extends Fragment {

    /**
     * {@link ViewPager} of this fragment
     */
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lend_list_pager, container, false);

        // get viewpager
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new LendPagerAdapter(getChildFragmentManager()));

        // Manage tabs of the view pager
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    /**
     * Adapter managing pager and fragments displayed inside
     */
    private static class LendPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         * @param fm fragment manager
         */
        private LendPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                break;
                case 1:
                break;
            }

            return LendListFragment.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return (String.valueOf(position));
        }
    }


    /**
     * An interface to manage Lend item click
     */
    public interface OnItemSelectedListener {
        void onItemSelected(Lend lend);
    }

}
