package com.pchsu.movieApp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pchsu.movieApp.data.MovieContract.FavoriteEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DB_VERSION = 1;

    static final String DB_NAME = "favoriteMovie.db";

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIE_TABLE =  "CREATE TABLE IF NOT EXISTS " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteEntry.COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_FILE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_FILE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_OVERVIEW +" TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_VOTE + " REAL NOT NULL" +
/*                FavoriteEntry.COLUMN_TRAILER_1 + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_TRAILER_2 + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_REVIEWS + " TEXT NOT NULL " + */
                ");";

        Log.d(FavoriteMovieProvider.TAG, SQL_CREATE_FAVORITE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Temporary for development purposes
        db.execSQL("DROP TABLE IF EXISTS "+ FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
