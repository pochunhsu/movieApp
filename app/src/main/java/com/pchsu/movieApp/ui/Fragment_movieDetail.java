package com.pchsu.movieApp.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.pchsu.movieApp.data.MovieContract;
import com.pchsu.movieApp.data.MovieContract.FavoriteEntry;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.utility.Communication;
import com.pchsu.movieApp.utility.GlobalConstant;
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

    private Boolean mOnCreatedViewCalled =false;
    private Boolean mOriginalFavoriteState = false;

    private SharedPreferences mChangeInFavorite;

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
        mContext = getActivity();
        mActivity = (Activity) mContext;
        mResolver = mContext.getContentResolver();
        mChangeInFavorite = mContext.getSharedPreferences(GlobalConstant.SharedPreference_favorite, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        mOnCreatedViewCalled = true;

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
            mMovie = mCallBack.requestDefaultMovie();
            updateDetail();
            mButtonShare.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // tablet mode doesn't need this and should avoid it
        // in tablet mode fragment start; the mMoive probably is be ready yet
        if (mCallBack.isTwoPane()) return;

        Cursor cursor = mResolver.query(FavoriteEntry.CONTENT_URI,
                null,
                FavoriteEntry.COLUMN_ID + "=?",
                new String[]{mMovie.getId() + ""},
                null);
        mOriginalFavoriteState = (cursor.getCount()!= 0);
        cursor.close();
    }

    @Override
    public void onPause() {
        super.onPause();

        // this should only happens in Phone mode;
        // table mode uses callback interface for real-time update
        if (mCallBack.isTwoPane()) {
            return;
        }

        Boolean FinalFavoriteSate = (mButtonHeartEmpty.getVisibility() == View.VISIBLE);
        Boolean stateChange = (mOriginalFavoriteState != FinalFavoriteSate);

        if (stateChange){
            if (mButtonHeartFull.getVisibility() == View.VISIBLE){
                if (MovieAppUtility.isNetworkAvailable(mContext)) {
                    storeFavoriteData();
                }else{ // abort the favorite add
                    Toast.makeText(mContext,"No network to add favorite", Toast.LENGTH_SHORT).show();
                    mButtonHeartFull.setVisibility(View.INVISIBLE);
                    mButtonHeartEmpty.setVisibility(View.VISIBLE);
                }
            }else{
                removeFavoriteData();
            }
        }
    }

    //
    // populate all the views with data;
    // This must be called AFTER all findViewById (ButterKnife.bind) is done
    //
    private void updateDetail(){
        if (mOnCreatedViewCalled != true || mMovie == null) return;

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

                if (mCallBack.isTwoPane()) {
                    if (storeFavoriteData()){
                        // update UI
                        mButtonHeartEmpty.setVisibility(View.INVISIBLE);
                        mButtonHeartFull.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, mMovie.getTitle() + " added to favorite", Toast.LENGTH_SHORT).show();
                    }
                    // update the poster display in real time
                    // the order matters: update the db before the renew
                    mCallBack.renewPosterDisplay();
                }else{
                    // update UI
                    mButtonHeartEmpty.setVisibility(View.INVISIBLE);
                    mButtonHeartFull.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, mMovie.getTitle() + " added to favorite", Toast.LENGTH_SHORT).show();
                    // delay the real store/delete to onPause
                    mChangeInFavorite.edit().putInt("id", mMovie.getId()).apply();
                }
            }
        });

        // un-favorite: remove the raw in the content provider by movie's id
        mButtonHeartFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update UI
                mButtonHeartFull.setVisibility(View.INVISIBLE);
                mButtonHeartEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, mMovie.getTitle() + " removed from favorite", Toast.LENGTH_SHORT).show();

                if (mCallBack.isTwoPane()) {
                    // update the poster display in real time
                    // the order matters: update the db before the renew
                    removeFavoriteData();
                    mCallBack.renewPosterDisplay();
                }else{
                    // delay the real store/delete to onPause
                    mChangeInFavorite.edit().putInt("id", mMovie.getId()).apply();
                }
            }
        });
    }

    // store the movie into content provider and storage
    // return true on success
    private boolean storeFavoriteData(){
        if ( ! MovieAppUtility.isExternalStorageWritable()){
            mCallBack.alertUserAboutError("No access to external storage for storing poster image!");
            return false;
        }else if ( ! MovieAppUtility.isNetworkAvailable(mContext)){
            mCallBack.alertUserAboutError("No access to internet for downloading images!");
            return false;
        }else{
            // query the movie to see if it already exists ; Skip the insert if the record exists
            // duplicate inserts get error
            String[] Args = new String[]{Integer.toString(mMovie.getId())};
            Cursor cursor = mResolver.query(MovieContract.FavoriteEntry.CONTENT_URI, null,
                                            FavoriteEntry.COLUMN_ID + "=?",
                                            Args, null);
            if (cursor.getCount() != 0) {
                cursor.close();
                return true;
            }

            // store poster and backdrop image into external storage
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
            return true;
        }
    }

    private void removeFavoriteData(){

        // access content provider to delete the record
        int deleteCnt = mResolver.delete (FavoriteEntry.CONTENT_URI,
                FavoriteEntry.COLUMN_ID + " = ? ", new String[]{mMovie.getId() + ""});
        // verify result
        if (deleteCnt == 0){
            String msg = "Warning: No instance to delete: " + mMovie.getId() + " " + mMovie.getTitle();
            Log.w(FavoriteMovieProvider.TAG, msg);
        }
        // remove images from the storage
        if (MovieAppUtility.isExternalStorageWritable()){
            MovieAppUtility.deleteImageFile(mContext,mMovie.getId()+"-p.jpg");
            MovieAppUtility.deleteImageFile(mContext, mMovie.getId() + "-b.jpg");
        }else{
            Log.w(FavoriteMovieProvider.TAG, "No access to external storage");
        }
    }

    public void updateDetailWithMovie(MovieInfo movie){
        // public method accessible to the other classes needs this check
        // to avoid access states that aren't ready yet
        if (mOnCreatedViewCalled != true) return;
        if (mLayout == null){
            Log.e(TAG, "mLayout is null and accessed in updateDetailWithMovie");
            return;
        }
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
