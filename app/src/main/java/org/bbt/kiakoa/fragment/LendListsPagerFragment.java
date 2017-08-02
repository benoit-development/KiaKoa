package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.LendList.LendArchiveListFragment;
import org.bbt.kiakoa.fragment.LendList.LendFromListFragment;
import org.bbt.kiakoa.fragment.LendList.LendToListFragment;

/**
 * {@link android.app.Fragment} displaying lend lists
 *
 * @author Benoit Bousquet
 */
public class LendListsPagerFragment extends Fragment {

    /**
     * For log
     */
    private static final String TAG = "LendListsPagerFragment";

    /**
     * Names for lend lists
     */
    private String[] lendListsNames = new String[]{};

    /**
     * Pager adapter
     */
    private LendPagerAdapter lendPagerAdapter;

    /**
     * {@link ViewPager} displaying different lend lists
     */
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_list_pager, container, false);

        // init lend lists names
        lendListsNames = getResources().getStringArray(R.array.lend_lists_names);

        // get viewpager
        viewPager = view.findViewById(R.id.pager);
        lendPagerAdapter = new LendPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(lendPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setActivityTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActivityTitle(viewPager.getCurrentItem());
    }

    /**
     * Display the asked page of the pager
     *
     * @param pageNumber page number
     */
    public void showPage(int pageNumber) {
        if (pageNumber < viewPager.getAdapter().getCount()) {
            Log.i(TAG, "Change lend list displayed : " + pageNumber);
            viewPager.setCurrentItem(pageNumber, true);
        } else {
            Log.i(TAG, "Lend list page incorrect : " + pageNumber);
        }
    }

    /**
     * update title in activity
     * @param page selected page in viewpager
     */
    public void setActivityTitle(int page) {
        Log.i(TAG, "Change Activity Title : " + page);
        switch (page) {
            case 0:
                // lend to
                getActivity().setTitle(R.string.title_lend_to);
                break;
            case 1:
                // lend from
                getActivity().setTitle(R.string.title_lend_from);
                break;
            case 2:
                // lend archive
                getActivity().setTitle(R.string.title_archive);
                break;
        }
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
