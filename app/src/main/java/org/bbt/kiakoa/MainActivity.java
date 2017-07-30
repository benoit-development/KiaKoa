package org.bbt.kiakoa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.bbt.kiakoa.fragment.LendDetailsFragment;
import org.bbt.kiakoa.model.Lend;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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
        LendDetailsFragment lendDetailsFragment = (LendDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.lend_details_frag);
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

}
