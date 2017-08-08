package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.fragment.LoanList.ArchiveListFragment;
import org.bbt.kiakoa.fragment.LoanList.BorrowedListFragment;
import org.bbt.kiakoa.fragment.LoanList.LoanToListFragment;

/**
 * {@link android.app.Fragment} displaying a loan lists
 *
 * @author Benoit Bousquet
 */
public class LoanListsPagerFragment extends Fragment {

    /**
     * For log
     */
    private static final String TAG = "LoanListsPagerFragment";

    /**
     * {@link ViewPager} displaying different loan lists
     */
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_list_pager, container, false);

        // get viewpager
        viewPager = view.findViewById(R.id.pager);

        // Pager adapter
        LoanPagerAdapter loanPagerAdapter = new LoanPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(loanPagerAdapter);
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

        // TabLayout to display dots
        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

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
            Log.i(TAG, "Change loan list displayed : " + pageNumber);
            viewPager.setCurrentItem(pageNumber, true);
        } else {
            Log.i(TAG, "Loan list page incorrect : " + pageNumber);
        }
    }

    /**
     * update title in activity
     *
     * @param page selected page in viewpager
     */
    private void setActivityTitle(int page) {
        Log.i(TAG, "Change Activity Title : " + page);
        switch (page) {
            case 0:
                // lent
                getActivity().setTitle(R.string.title_lent);
                break;
            case 1:
                // borrowed
                getActivity().setTitle(R.string.title_borrowed);
                break;
            case 2:
                // archive
                getActivity().setTitle(R.string.title_archive);
                break;
        }
    }

    /**
     * Adapter managing pager and fragments displayed inside
     */
    private class LoanPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         *
         * @param fm fragment manager
         */
        private LoanPagerAdapter(FragmentManager fm) {
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
                    fragment = new LoanToListFragment();
                    break;
                case 1:
                    fragment = new BorrowedListFragment();
                    break;
                default:
                    fragment = new ArchiveListFragment();
                    break;
            }

            return fragment;
        }
    }

}
