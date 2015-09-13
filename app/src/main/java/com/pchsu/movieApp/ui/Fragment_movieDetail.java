package com.pchsu.movieApp.ui;

import android.app.Activity;
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
import android.widget.Toast;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.FavoriteMovieProvider;
import com.pchsu.movieApp.data.MovieContract.FavoriteEntry;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.utility.Communication;
import com.pchsu.movieApp.utility.MovieAppUtility;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment_movieDetail extends Fragment{
    public static final String TAG = Fragment_movieDetail.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;
    private Resources mResources;
    private ContentResolver mResolver;
    private MovieInfo mMovie;

    // call back interface
    private Communication mCallBack;

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
    @Bind(R.id.button_share) ImageButton mButtonShare;
    @Bind(R.id.layout_movie_detail) RelativeLayout mLayout;

    public static Fragment_movieDetail newInstance() {
        Fragment_movieDetail fragment = new Fragment_movieDetail();
        return fragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (Communication) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement Communication!" );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (Activity) mContext;
        mResolver = mContext.getContentResolver();

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // delay the screen update until now,
        // because this point promises pane layout check in Activity OnCreate is done

        if (!mCallBack.isTwoPane()){
            Intent intent = mActivity.getIntent();
            Parcelable parcelable = intent.getParcelableExtra(MainActivity.MOVIE_INFO);
            mMovie = (MovieInfo) parcelable;
            updateDetail();

        }else{
            mButtonShare.setVisibility(View.VISIBLE);
        }
    }

    //
    // populate all the views with data;
    // This must be called AFTER all findViewById (ButterKnife.bind) is done
    //
    private void updateDetail(){
        // load content into each elements
        mTitleLabel.setText(mMovie.getTitle());
        mFullTitleLabel.setText(mMovie.getTitle());
        mReleaseDateText.setText(mMovie.getReleaseDate());
        mOverViewText.setText(mMovie.getOverview());

        double rating = convertRating_10to5(mMovie.getVote());
        mRatingText.setText( rating + " / 5.0");
        mRatingBar.setRating((float) rating);

        // check the content provider to see if it's favorite movie
        Cursor cursor = mResolver.query(FavoriteEntry.CONTENT_URI,
                null,
                FavoriteEntry.COLUMN_ID + "=?",
                new String[]{mMovie.getId() + ""},
                null);
        if (cursor.getCount() ==0){
            Log.d(FavoriteMovieProvider.TAG, "query gets no rows");
            mButtonHeartFull.setVisibility(View.INVISIBLE);
            mButtonHeartEmpty.setVisibility(View.VISIBLE);
        }else{
            mButtonHeartEmpty.setVisibility(View.INVISIBLE);
            mButtonHeartFull.setVisibility(View.VISIBLE);
        }
        cursor.close();

        // using picasso api to load images from content provider or the internet
        // Favorite: load images from local storage
        if (mButtonHeartFull.getVisibility() == View.VISIBLE) {
            if (mMovie.getBackDropFile() != null && ! mMovie.getBackDropFile().equals("")){
                Picasso.with(mContext).load(new File(mMovie.getBackDropFile())).into(mTitleImage);
            }else{
                Picasso.with(mContext).load(mMovie.getBackDropUrl()).into(mTitleImage);
            }
            if (mMovie.getPosterFile() != null && ! mMovie.getPosterFile().equals("")){
                Picasso.with(mContext).load(new File(mMovie.getPosterFile())).into(mPosterImage);
            }else{
                Picasso.with(mContext).load(mMovie.getPosterUrl()).into(mPosterImage);
            }
        }else{  // non-Favorite: load images using URL (Picasso might cache the image btw)
            Picasso.with(mContext).load(mMovie.getBackDropUrl()).into(mTitleImage);
            Picasso.with(mContext).load(mMovie.getPosterUrl()).into(mPosterImage);
        }

        // add favorite: store information to the content provider
        //               store poster image to the external storage (SD card)
        mButtonHeartEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // store poster and backdrop image into external storage
                if ( ! MovieAppUtility.isExternalStorageWritable()){
                    mCallBack.alertUserAboutError("No access to external storage for storing poster image!");
                }else if ( ! MovieAppUtility.isNetworkAvailable(mContext)){
                    mCallBack.alertUserAboutError("No access to internet for downloading images!");
                }else{
                    //String imageUrl = mContext.getString(R.string.imageUrlPath) + mMovie.getPosterUrl();
                    File f_poster = MovieAppUtility.downloadImageFile(mContext, mMovie.getPosterUrl(), mMovie.getId() + "-p.jpg");
                    File f_backdrop = MovieAppUtility.downloadImageFile(mContext, mMovie.getBackDropUrl(), mMovie.getId() + "-b.jpg");

                    mMovie.setBackDropFile(f_backdrop.toString());
                    mMovie.setPosterFile(f_poster.toString());
                    // use resolver to store data into content provider
                    ContentValues values = new ContentValues();
                    values.put(FavoriteEntry.COLUMN_ID, mMovie.getId());
                    values.put(FavoriteEntry.COLUMN_TITLE, mMovie.getTitle());
                    values.put(FavoriteEntry.COLUMN_BACKDROP_URL, mMovie.getBackDropUrl());
                    values.put(FavoriteEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl());
                    values.put(FavoriteEntry.COLUMN_BACKDROP_FILE, mMovie.getBackDropFile());
                    values.put(FavoriteEntry.COLUMN_POSTER_FILE, mMovie.getPosterFile());
                    values.put(FavoriteEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                    values.put(FavoriteEntry.COLUMN_RELEASEDATE, mMovie.getReleaseDate());
                    values.put(FavoriteEntry.COLUMN_VOTE, mMovie.getVote());

                    mResolver.insert(FavoriteEntry.CONTENT_URI, values);

                    // toggle the image button
                    mButtonHeartEmpty.setVisibility(View.INVISIBLE);
                    mButtonHeartFull.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, mMovie.getTitle() + " added to favorite", Toast.LENGTH_SHORT);
                    mCallBack.renewPosterDisplay();
                }
            }
        });

        // un-favorite: remove the raw in the content provider by movie's id
        mButtonHeartFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deleteCnt = mResolver.delete (FavoriteEntry.CONTENT_URI,
                        FavoriteEntry.COLUMN_ID + " = ? ", new String[]{mMovie.getId() + ""});

                // verify result
                if (deleteCnt == 0){
                    String msg = "Warning: No instance to delete: " + mMovie.getId() + " " + mMovie.getTitle();
                    Log.w(FavoriteMovieProvider.TAG, msg);
                }

                // update UI
                mButtonHeartFull.setVisibility(View.INVISIBLE);
                mButtonHeartEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, mMovie.getTitle() + " removed from favorite", Toast.LENGTH_SHORT);

                // remove images from the storage
                if (MovieAppUtility.isExternalStorageWritable()){
                    MovieAppUtility.deleteImageFile(mContext,mMovie.getId()+"-p.jpg");
                    MovieAppUtility.deleteImageFile(mContext, mMovie.getId() + "-b.jpg");
                }
                mCallBack.renewPosterDisplay();
            }
        });
    }

    public void updateDetailWithMovie(MovieInfo movie){
        if (movie ==null) {
            mLayout.setVisibility(View.INVISIBLE);
        }else{
            mLayout.setVisibility(View.VISIBLE);
            mMovie = movie;
            updateDetail();
        }
    }

    // This is designed for tablet use
    public void setShareButtonListener(final Intent intent){
        if (mButtonShare == null) {
            Log.e(TAG, "mButtonShare is null when setOnClickListener !");
            return;
        }
        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MovieAppUtility.isNetworkAvailable(mContext)) {
                    startActivity(Intent.createChooser(intent, "Share via"));
                }
            }
        });
    }

    // convert 10-star rating to 5-star
    private double convertRating_10to5 (double rating){
        double result = rating / 2;
        result = Math.round(result* 10.0) / 10.0;
        return result;
    }

}
