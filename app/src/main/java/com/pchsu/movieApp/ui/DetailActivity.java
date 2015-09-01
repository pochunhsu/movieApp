package com.pchsu.movieApp.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.adapter.PagerAdapter;

public class DetailActivity extends AppCompatActivity {

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
/*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new Fragment_movieDetail())
                    .commit();
        }
        */
    }

}
