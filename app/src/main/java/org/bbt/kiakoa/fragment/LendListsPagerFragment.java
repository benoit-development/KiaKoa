package org.bbt.kiakoa.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_list_pager, container, false);

        // get viewpager
        ViewPager viewPager = view.findViewById(R.id.pager);
        lendPagerAdapter = new LendPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(lendPagerAdapter);

        // Manage tabs of the view pager
        TabLayout tabLayout = view.findViewById(R.id.tabs);
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
        lendPagerAdapter.notifyDataSetChanged();
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
            String title;
            FragmentActivity activity = LendListsPagerFragment.this.getActivity();

            // LendLists to get lend numbers
            LendLists lendLists = LendLists.getInstance();
            int lendToNb = lendLists.getLendToList().size();
            int lendFromNb = lendLists.getLendFromList().size();
            int lendArchiveNb = lendLists.getLendArchiveList().size();

            switch (position) {
                case 0:
                    String titleLabel = activity.getString(R.string.tab_title_lend_to);
                    String titleNb = (lendToNb > 0)?" " + lendToNb:"";
                    title = titleLabel + titleNb;
                    break;
                case 1:
                    titleLabel = activity.getString(R.string.tab_title_lend_from);
                    titleNb = (lendFromNb > 0) ? " " + lendFromNb : "";
                    title = titleLabel + titleNb;
                    break;
                case 2:
                    titleLabel = activity.getString(R.string.tab_title_archive);
                    titleNb = (lendArchiveNb > 0) ? " " + lendArchiveNb : "";
                    title = titleLabel + titleNb;
                    break;
                default:
                    title = "";
                    break;
            }
            return title;
        }
    }

}
