package org.bbt.kiakoa.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.bbt.kiakoa.BuildConfig;
import org.bbt.kiakoa.R;

/**
 * Activity displaying information about this application
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * For logs
     */
    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle(R.string.about);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        // version name
        ((TextView) findViewById(R.id.version_name)).setText(BuildConfig.VERSION_NAME);

        // set click listeners
        // creator to send me an email
        findViewById(R.id.creator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"benoit.android@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_body));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.e(TAG, "There are no email clients installed.");
                }
            }
        });

        // licence to open licence site
        findViewById(R.id.licence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/benoit-development/KiaKoa/blob/master/LICENSE"));
                startActivity(i);
            }
        });

        // rate application
        findViewById(R.id.rate_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market back stack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        // privacy policy
        findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blogdebenoit.wordpress.com/my-loan-lists-privacy-policy/"));
                startActivity(i);
            }
        });

        // readme
        findViewById(R.id.readme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/benoit-development/KiaKoa/blob/master/README.md"));
                startActivity(i);
            }
        });
    }
}
