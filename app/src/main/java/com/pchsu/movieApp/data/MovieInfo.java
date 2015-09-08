package com.pchsu.movieApp.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

public class MovieInfo implements Parcelable{

    private int    mId;               // movie id
    private String mTitle;           // movie title
    private String mBackDropPath;    // http path to backdrop
    private String mPosterPath;      // http path to poster
    private String mOverview;        // movie description
    private String mReleaseDate;     // YYYY-MM-DD
    private double mVote;            // scale: 0-10
    private String[] mTrailerLinks;  // string arrays of youtube path
    private Pair[] mReviews;         // pair of strings [author, review content]
    private boolean favorite;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

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

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public double getVote() {
        return mVote;
    }

    public void setVote(double vote) {
        mVote = vote;
    }

    public String[] getTrailerLinks() {
        return mTrailerLinks;
    }

    public void setTrailerLinks(String[] trailerLinks) {
        mTrailerLinks = trailerLinks;
    }

    public Pair[] getReviews() {
        return mReviews;
    }

    public void setReviews(Pair[] reviews) {
        mReviews = reviews;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mBackDropPath);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mVote);
        dest.writeString(mOverview);
    }

    private MovieInfo (Parcel in){
        mId = in.readInt();
        mTitle = in.readString();
        mBackDropPath = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mVote = in.readDouble();
        mOverview = in.readString();
    }

    public MovieInfo() {
        mId = 0;
        mTitle = "default_title";
        mBackDropPath = "";
        mPosterPath = "";
        mReleaseDate = "YYYY-MM-DD";
        mVote = 10;
        mOverview = "plot";
        mTrailerLinks = null;
        mReviews = null;
    }

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
