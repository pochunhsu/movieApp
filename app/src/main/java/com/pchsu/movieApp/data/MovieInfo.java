package com.pchsu.movieApp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable{
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mBackDropPath);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
    }

    private MovieInfo (Parcel in){
        mTitle = in.readString();
        mBackDropPath = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
    }

    public MovieInfo(){}

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
