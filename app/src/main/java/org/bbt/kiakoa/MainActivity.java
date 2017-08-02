package org.bbt.kiakoa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.fragment.LendDetailsFragment;
import org.bbt.kiakoa.fragment.LendListsPagerFragment;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LendLists.OnLendListsChangedListener {

    /**
     * For Log
     */
    private static final String TAG = "MainActivity";
    /**
     * For navigation
     */
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayoutAdapter mDrawerLayoutAdapter;

    /**
     * {@link android.support.v4.app.Fragment} displaying Lend detail
     * Only exists on large screens
     */
    private LendDetailsFragment lendDetailsFragment;

    private LendListsPagerFragment lendListsPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find fragments
        lendListsPager = (LendListsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.lend_pager_frag);
        lendDetailsFragment = (LendDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.lend_details_frag);

        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLeft = (LinearLayout) findViewById(R.id.left_drawer);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        // Set the adapter for the list view
        mDrawerLayoutAdapter = new DrawerLayoutAdapter();
        mDrawerList.setAdapter(mDrawerLayoutAdapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        // Toggler
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LendLists.getInstance().registerOnLendListsChangedListener(this, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LendLists.getInstance().unregisterOnLendListsChangedListener(this, TAG);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Method called by an {@link org.bbt.kiakoa.fragment.LendList.AbstractLendListFragment} to
     * display a lend details in {@link org.bbt.kiakoa.fragment.LendDetailsFragment}
     *
     * @param lend lend to display
     */
    public void displayLendDetails(Lend lend) {
        if (lendDetailsFragment != null) {
            // update fragment with selected lend
            lendDetailsFragment.setLend(lend);
        } else {
            // launch activity to display lend details
            Intent intent = new Intent(this, LendDetailsActivity.class);
            intent.putExtra(LendDetailsActivity.EXTRA_LEND, lend);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        // Manage click on burger icon in toolbar
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "item clicked : " + position);

        mDrawerLayout.closeDrawer(mDrawerLeft);

        // change the displayed Lend list
        lendListsPager.showPage(position);
    }

    @Override
    public void onLendListsChanged() {
        mDrawerLayoutAdapter.notifyDataSetChanged();
    }

    /**
     * Adapter for the {@link ListView} inside the {@link DrawerLayout} of this activity
     */
    private class DrawerLayoutAdapter extends BaseAdapter {

        private final LayoutInflater inflater;

        DrawerLayoutAdapter() {
            inflater = (LayoutInflater.from(getBaseContext()));
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = inflater.inflate(R.layout.adapter_drawer_layout_item, viewGroup, false);

                // Creates a ViewHolder
                holder = new ViewHolder();
                holder.icon = view.findViewById(R.id.icon);
                holder.text = view.findViewById(R.id.text);
                holder.badge = view.findViewById(R.id.badge);

                view.setTag(holder);
            } else {
                // Get the ViewHolder
                holder = (ViewHolder) view.getTag();
            }

            // Bind the data efficiently with the holder.
            int iconId = 0;
            int textId = 0;
            int lendCount = 0;
            switch (i) {
                case 0:
                    iconId = R.drawable.ic_lend_to_gray_24dp;
                    textId = R.string.title_lend_to;
                    lendCount = LendLists.getInstance().getLendToList().size();
                    break;
                case 1:
                    iconId = R.drawable.ic_lend_from_gray_24dp;
                    textId = R.string.title_lend_from;
                    lendCount = LendLists.getInstance().getLendFromList().size();
                    break;
                case 2:
                    iconId = R.drawable.ic_archive_gray_24dp;
                    textId = R.string.title_archive;
                    lendCount = LendLists.getInstance().getLendArchiveList().size();
                    break;
                default:
                    Log.e(TAG, "Index of the view is not correct : " + i);
                    break;
            }
            if(iconId != 0) {
                holder.icon.setImageResource(iconId);
            }
            if(textId != 0) {
                holder.text.setText(textId);
            }
            if (lendCount != 0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(String.valueOf(lendCount));
            } else {
                holder.badge.setVisibility(View.GONE);
            }

            return view;
        }

        private class ViewHolder {
            ImageView icon;
            TextView text;
            TextView badge;
        }

    }
}
