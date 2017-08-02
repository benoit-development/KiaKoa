package org.bbt.kiakoa;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.bbt.kiakoa.fragment.LendDetailsFragment;
import org.bbt.kiakoa.fragment.LendListsPagerFragment;
import org.bbt.kiakoa.model.Lend;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /**
     * For Log
     */
    private static final String TAG = "MaintActivity";
    /**
     * For navigation
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    /**
     * {@link android.support.v4.app.Fragment} displaying Lend detail
     * Only exists on large screens
     */
    private LendDetailsFragment lendDetailsFragment;

    /**
     * Names for lend lists
     */
    private String[] lendListsNames = new String[]{};;
    private LendListsPagerFragment lendListsPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init lend lists names
        lendListsNames = getResources().getStringArray(R.array.lend_lists_names);

        // find fragments
        lendListsPager = (LendListsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.lend_pager_frag);
        lendDetailsFragment = (LendDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.lend_details_frag);

        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLeft = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerLayoutAdapter());

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        // Toggler
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        // Manage click on buger icon in toolbar
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "item clicked : " + position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerLeft);

        // change the displayed Lend list
        lendListsPager.showPage(position);
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
            return lendListsNames.length;
        }

        @Override
        public Object getItem(int i) {
            return lendListsNames[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);

                // Creates a ViewHolder
                holder = new ViewHolder();
                // holder.icon = view.findViewById(R.id.icon);
                holder.text = view.findViewById(android.R.id.text1);

                view.setTag(holder);
            } else {
                // Get the ViewHolder
                holder = (ViewHolder) view.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.text.setText(lendListsNames[i]);

            return view;
        }

        private class ViewHolder {
            ImageView icon;
            TextView text;
        }

    }
}
