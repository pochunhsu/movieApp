package com.pchsu.movieApp.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

public class MovieInfo implements Parcelable{

    private int    mId;              // movie id
    private String mTitle;           // movie title
    private String mBackDropUrl;     // http path to backdrop
    private String mPosterUrl;       // http path to poster
    private String mBackDropFile;    // file path to backdrop
    private String mPosterFile;      // file path to poster
    private String mOverview;        // movie description
    private String mReleaseDate;     // YYYY-MM-DD
    private double mVote;            // scale: 0-10
    private String[] mTrailerLinks;  // string arrays of youtube path
    private Pair[] mReviews;         // pair of strings [author, review content]
    //private boolean mFavorite;     // no need to maintain this attribute
                                     // all instances from the content provider is favorite

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

    public String getBackDropUrl() {
        return mBackDropUrl;
    }

    public void setBackDropUrl(String backDropUrl) {
        this.mBackDropUrl = backDropUrl;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.mPosterUrl = posterUrl;
    }

    public String getBackDropFile() {
        return mBackDropFile;
    }

    public void setBackDropFile(String backDropFile) {
        mBackDropFile = backDropFile;
    }

    public String getPosterFile() {
        return mPosterFile;
    }

    public void setPosterFile(String posterFile) {
        mPosterFile = posterFile;
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

   /* public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mBackDropUrl);
        dest.writeString(mPosterUrl);
        dest.writeString(mBackDropFile);
        dest.writeString(mPosterFile);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mVote);
        dest.writeString(mOverview);
        //dest.writeByte((byte)(mFavorite? 1:0));
    }

    private MovieInfo (Parcel in){
        mId = in.readInt();
        mTitle = in.readString();
        mBackDropUrl = in.readString();
        mPosterUrl = in.readString();
        mBackDropFile = in.readString();
        mPosterFile = in.readString();
        mReleaseDate = in.readString();
        mVote = in.readDouble();
        mOverview = in.readString();
        //mFavorite = in.readByte()!=0 ;
    }

    public MovieInfo() {
        mId = 0;
        mTitle = "default_title";
        mBackDropUrl = "";
        mPosterUrl = "";
        mBackDropFile = "";
        mPosterFile = "";
        mReleaseDate = "YYYY-MM-DD";
        mVote = 10;
        mOverview = "plot";
        mTrailerLinks = null;
        mReviews = null;
        //mFavorite = false;
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
