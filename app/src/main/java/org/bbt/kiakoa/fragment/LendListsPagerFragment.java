package org.bbt.kiakoa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lend_list_pager, container, false);

        // get viewpager
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        return view;
    }



    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return LendListFragment.newInstance(position);
        }
    }


    /**
     * An interface to manage Lend item click
     */
    public interface OnItemSelectedListener {
        void onItemSelected(Lend lend);
    }

}
