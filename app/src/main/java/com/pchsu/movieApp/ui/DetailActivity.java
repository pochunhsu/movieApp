package com.pchsu.movieApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;

public class DetailActivity extends AppCompatActivity {


    private MovieInfo mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Parcelable parcelable = intent.getParcelableExtra(MainActivity.MOVIE_INFO);
        mMovie = (MovieInfo) parcelable;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new Fragment_movieDetail())
                    .commit();
        }
    }

    public MovieInfo getMovie() {
        return mMovie;
    }
}
