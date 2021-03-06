package com.pchsu.movieApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.utility.Communication;


public class MainActivity extends AppCompatActivity
                          implements Communication{

    public static String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE_INFO = "MOVIE_INFO";
    private static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_movieDetail);
        // 2-pane table mode
        if (fragment != null) {
            mTwoPane = true;
        } else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new Fragment_movieDisplay())
                    .commit();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    //
    // implementation of callback interface
    //
    public boolean isTwoPane() {
        return mTwoPane;
    }

    public void alertUserAboutError(String error_msg) {
        AlertDialogFragment dialog = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(AlertDialogFragment.ERR_MSG_TAG, error_msg);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), AlertDialogFragment.ERR_MSG_TAG);
    }

    // NOTE: the fragment callee should handle null movie pointer also
    //       In the case of null movie pointer, they should clear the view
    // This function has 2 uses:
    // (1) Table/multi-pane mode : to inform detail fragments to update the views
    // (2) phone/one-pane mode: to start new activity displaying details
    public void onMovieSelected (MovieInfo movie){

        if (isTwoPane()){  // three-pane layout
            Fragment_movieDetail fragment_detail = (Fragment_movieDetail)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_movieDetail);
            Fragment_movieDetail_more fragment_more = (Fragment_movieDetail_more)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_movieDetail_more);
            if (fragment_detail ==null){
                Log.e(TAG, " null detail fragment !");
            }else{
                fragment_detail.updateDetailWithMovie(movie);
            }
            if (fragment_more ==null){
                Log.e(TAG, " null detail_more fragment !");
            }else{
                fragment_more.updateDetailWithMovie(movie);
            }
        }else{  // one-pane layout
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(MainActivity.MOVIE_INFO, movie);
            startActivity(i);
        }
    }

    // tablet mode: pass the intent to set up Share button OnClick in Detail Fragment
    public void setShareIntent(Intent shareIntent){
        if (shareIntent == null || !isTwoPane()) return;

        Fragment_movieDetail fragment_detail = (Fragment_movieDetail)
                getSupportFragmentManager().findFragmentById(R.id.fragment_movieDetail);
        if (fragment_detail == null){
            Log.e(TAG, " null detail fragment !");
        }else {
            fragment_detail.setShareButtonListener(shareIntent);
        }
    }

    // tablet mode: use to update the poster display in real time
    public void renewPosterDisplay(){
        if (isTwoPane()) {
            Fragment_movieDisplay fragment_posters = (Fragment_movieDisplay)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_movieDisplay);
            if (fragment_posters == null){
                Log.e(TAG, " null poster fragment !");
            }else {
                fragment_posters.renewDisplay();
            }
        }
    }

    // tablet mode: fragment can use this interface to request default movie to show
    public MovieInfo requestDefaultMovie(){
        if (isTwoPane()) {
            Fragment_movieDisplay fragment_posters = (Fragment_movieDisplay)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_movieDisplay);
            if (fragment_posters == null){
                Log.e(TAG, " null poster fragment !");
                return null;
            }else {
                return fragment_posters.getDefaultMovie();
            }
        }else{
            Log.e(TAG, "phone app shouldn't need to call this; they use intent");
            return null;
        }
    }
}
