package com.pchsu.movieApp.ui;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.FavoriteMovieProvider;
import com.pchsu.movieApp.data.MovieContract.FavoriteEntry;
import com.pchsu.movieApp.data.MovieInfo;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment_movieDetail extends Fragment{
    private Context mContext;
    private DetailActivity mActivity;
    private Resources mResources;
    private ContentResolver mResolver;

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
    @Bind(R.id.button_heart_empty) ImageButton mButtonHeartEmpty;
    @Bind(R.id.button_heart_full) ImageButton mButtonHeartFull;

    public static Fragment_movieDetail newInstance() {
        Fragment_movieDetail fragment = new Fragment_movieDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (DetailActivity) mContext;
        mResolver = mContext.getContentResolver();

        Intent intent = mActivity.getIntent();
        Parcelable parcelable = intent.getParcelableExtra(MainActivity.MOVIE_INFO);
        mMovie = (MovieInfo) parcelable;

        // program the sections' height based on the screen size and the weight integer specified in xml
        mResources = getResources();
        DisplayMetrics metrics = mResources.getDisplayMetrics();
        int topSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.topSecWeight) / 100;
        int middleSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.middleSecWeight) / 100;

        // inflate the view and instantiate all elements
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
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
        mRatingBar.setRating((float) rating);

        // check the content provider and display the favorite button accordingly

        Cursor cursor = mResolver.query(FavoriteEntry.CONTENT_URI,
                                        null,
                                        FavoriteEntry.COLUMN_ID + "=?",
                                        new String[]{mMovie.getId() + ""},
                                        null);
        if (cursor.getCount() ==0){
            Log.d(FavoriteMovieProvider.TAG, "query gets no raws");
            mButtonHeartFull.setVisibility(View.INVISIBLE);
            mButtonHeartEmpty.setVisibility(View.VISIBLE);
        }else{
            mButtonHeartEmpty.setVisibility(View.INVISIBLE);
            mButtonHeartFull.setVisibility(View.VISIBLE);
        }

        // add favorite: store information to the content provider
        mButtonHeartEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(FavoriteEntry.COLUMN_ID, mMovie.getId());
                values.put(FavoriteEntry.COLUMN_TITLE, mMovie.getTitle());
                values.put(FavoriteEntry.COLUMN_BACKDROP, mMovie.getBackDropPath());
                values.put(FavoriteEntry.COLUMN_POSTER, mMovie.getPosterPath());
                values.put(FavoriteEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                values.put(FavoriteEntry.COLUMN_RELEASEDATE, mMovie.getReleaseDate());
                values.put(FavoriteEntry.COLUMN_VOTE, mMovie.getVote());

                mResolver.insert(FavoriteEntry.CONTENT_URI, values);
                mButtonHeartEmpty.setVisibility(View.INVISIBLE);
                mButtonHeartFull.setVisibility(View.VISIBLE);
            }
        });
        // unfavorite: remove the raw in the content provider by movie's id
        mButtonHeartFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deleteCnt = mResolver.delete (FavoriteEntry.CONTENT_URI,
                                FavoriteEntry.COLUMN_ID + " = ? ", new String[]{mMovie.getId() + ""});

                if (deleteCnt == 0){
                    String msg = "Warning: No instance to delete: " + mMovie.getId() + " " + mMovie.getTitle();
                    Log.w(FavoriteMovieProvider.TAG, msg);
                }
                mButtonHeartFull.setVisibility(View.INVISIBLE);
                mButtonHeartEmpty.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    // convert 10-star rating to 5-star
    private double convertRating_10to5 (double rating){
        double result = rating / 2;
        result = Math.round(result* 10.0) / 10.0;
        return result;
    }

}
