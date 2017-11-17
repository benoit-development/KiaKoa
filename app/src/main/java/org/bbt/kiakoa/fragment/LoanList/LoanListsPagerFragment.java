package org.bbt.kiakoa.fragment.LoanList;

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
import org.bbt.kiakoa.model.LoanLists;

/**
 * {@link android.app.Fragment} displaying a loan lists
 *
 * @author Benoit Bousquet
 */
public class LoanListsPagerFragment extends Fragment implements LoanLists.OnLoanListsChangedListener {

    /**
     * For log
     */
    private static final String TAG = "LoanListsPagerFragment";

    /**
     * {@link ViewPager} displaying different loan lists
     */
    private ViewPager viewPager;

    /**
     * Adapter for {@link ViewPager}
     */
    private LoanPagerAdapter loanPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_list_pager, container, false);

        // get viewpager
        viewPager = view.findViewById(R.id.pager);

        // Pager adapter
        loanPagerAdapter = new LoanPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(loanPagerAdapter);

        // tab layout
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
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

    @Override
    public void onLoanListsChanged() {
        loanPagerAdapter.notifyDataSetChanged();
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
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new LentListFragment();
                    break;
                default:
                    fragment = new BorrowedListFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case 0:
                    title = getString(R.string.lent);
                    break;
                default:
                    title = getString(R.string.borrowed);
                    break;
            }
            return title;
        }
    }

}
