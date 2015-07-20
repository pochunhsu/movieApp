package com.pchsu.movieApp.ui;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment_movieDetail extends Fragment{
    private Context mContext;
    private DetailActivity mActivity;
    private Resources mResources;

    private MovieInfo mMovie;

    @Bind(R.id.topSection) RelativeLayout mTopSec;
    @Bind(R.id.middleSection) RelativeLayout mMiddleSec;
    @Bind(R.id.titleImage) ImageView mTitleImage;
    @Bind(R.id.titleLabel) TextView mTitleLabel;
    @Bind(R.id.fullTitleLabel) TextView mFullTitleLabel;
    @Bind(R.id.posterImage) ImageView mPosterImage;
    @Bind(R.id.ratingBar) RatingBar mRatingBar;
    @Bind(R.id.ratingLabel) TextView mRatingText;
    @Bind(R.id.releaseDateLabel) TextView mReleaseDateText;
    @Bind(R.id.overviewLabel) TextView mOverViewText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (DetailActivity) mContext;

        Intent intent = mActivity.getIntent();
        Parcelable parcelable = intent.getParcelableExtra(MainActivity.MOVIE_INFO);
        mMovie = (MovieInfo) parcelable;

        // program the sections' height based on the screen size and the weight integer specified in xml
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mResources = getResources();
        int topSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.topSecWeight) / 100;
        int middleSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.middleSecWeight) / 100;

        // inflate the view and instantiate all elements
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        // set heights for the top and middle sections
        mTopSec.getLayoutParams().height = topSecHeight;
        mMiddleSec.getLayoutParams().height = middleSecHeight;

        // using picasso api to load images from web
        String ImageUrl = mContext.getString(R.string.imageUrlPath) + mMovie.getBackDropPath();
        Picasso.with(mContext).load(ImageUrl).into(mTitleImage);

        ImageUrl = mContext.getString(R.string.imageUrlPath) + mMovie.getPosterPath();
        Picasso.with(mContext).load(ImageUrl).into(mPosterImage);

        // load content into each elements
        mTitleLabel.setText(mMovie.getTitle());
        mFullTitleLabel.setText(mMovie.getTitle());
        mReleaseDateText.setText(mMovie.getReleaseDate());
        mOverViewText.setText(mMovie.getOverview());

        double rating = convertRating_10to5(mMovie.getVote());
        mRatingText.setText( rating + " / 5.0");
        mRatingBar.setRating((float)rating);

        return rootView;
    }

    // convert 10-star rating to 5-star
    private double convertRating_10to5 (double rating){
        double result = rating / 2;
        result = Math.round(result* 10.0) / 10.0;
        return result;
    }

}
