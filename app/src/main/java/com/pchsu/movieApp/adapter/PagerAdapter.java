package com.pchsu.movieApp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pchsu.movieApp.ui.Fragment_movieDetail;
import com.pchsu.movieApp.ui.Fragment_movieReview;
import com.pchsu.movieApp.ui.Fragment_movieTrailer;

public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String mTabTitles[] = new String[] { "Overview", "Trailer", "Review" };
    private Context mContext;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position){
            case 0: fragment = Fragment_movieDetail.newInstance(); break;
            case 1: fragment = Fragment_movieTrailer.newInstance(); break;
            case 2: fragment = Fragment_movieReview.newInstance(); break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return mTabTitles[position];
    }
}
