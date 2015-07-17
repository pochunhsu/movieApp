package com.pchsu.movieApp.data;

public class MovieInfo {
    String mTitle;
    String mBackDropPath;
    String mPosterPath;
    String mOverview;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getBackDropPath() {
        return mBackDropPath;
    }

    public void setBackDropPath(String backDropPath) {
        this.mBackDropPath = backDropPath;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }
}
