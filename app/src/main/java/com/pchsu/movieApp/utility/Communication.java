package com.pchsu.movieApp.utility;

import android.content.Intent;

import com.pchsu.movieApp.data.MovieInfo;

public interface Communication {
    void alertUserAboutError(String str);
    boolean isTwoPane();
    void onMovieSelected(MovieInfo movie);
    void setShareIntent(Intent shareIntent);
    void renewPosterDisplay();
}
