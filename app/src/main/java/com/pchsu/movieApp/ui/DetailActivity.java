package com.pchsu.movieApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.adapter.PagerAdapter;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.utility.Communication;
import com.pchsu.movieApp.utility.MovieAppUtility;

public class DetailActivity extends AppCompatActivity
                            implements Communication {

    private Intent mShareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        PagerAdapter mPagerAdapter;

        // The ViewPager that will host the section contents.
        ViewPager mViewPager;
        // Settings for sliding tab layout
        // (1) FragmentAdapter (2) ViewPager (3) Tablayout

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), DetailActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        // Set up tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_item_share){
            if (MovieAppUtility.isNetworkAvailable(getApplicationContext())) {
                if (mShareIntent != null) {
                    startActivity(Intent.createChooser(mShareIntent, "Share via"));
                } else {
                    Toast.makeText(getApplicationContext(), "Null Share Intent !", Toast.LENGTH_SHORT).show();
                }
            }else{
                alertUserAboutError("No Network for sharing!");
            }
        }
        return true;
    }
    // Call to update the share intent
    public void setShareIntent(Intent shareIntent) {
        if (shareIntent == null) return;
        mShareIntent = shareIntent;
    }

    //
    // implementation of callback interface
    //
    public boolean isTwoPane() {
        return false;
    }

    public void alertUserAboutError(String error_msg) {
        AlertDialogFragment dialog = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(AlertDialogFragment.ERR_MSG_TAG, error_msg);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), AlertDialogFragment.ERR_MSG_TAG);
    }

    // no use ; empty interface ; these are only used in tablet mode
    public void onMovieSelected (MovieInfo movie){}
    public void renewPosterDisplay(){}
    public MovieInfo requestDefaultMovie(){return null;}
}
