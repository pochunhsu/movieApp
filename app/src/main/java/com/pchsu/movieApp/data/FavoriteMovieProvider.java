package com.pchsu.movieApp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.pchsu.movieApp.data.MovieContract.FavoriteEntry;

public class FavoriteMovieProvider extends ContentProvider {

    //private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final String TAG = FavoriteMovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;
    SQLiteDatabase mDb;

    static final int FAVORITE = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        Log.d(TAG, "Content Provider onCreate!");
        return (mDbHelper == null) ? false : true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE:
                return FavoriteEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE:
                retCursor = mDbHelper.getReadableDatabase().query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mDbHelper.getWritableDatabase();
        long _id = mDb.insert(FavoriteEntry.TABLE_NAME, null, values);
        Uri returnUri;

        if ( _id > 0 ) {
            //returnUri = FavoriteEntry.buildMovieUri(_id);
            returnUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, _id);
            getContext().getContentResolver().notifyChange(returnUri, null);
        }else{
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case FAVORITE:
                rowsDeleted = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update is not supported");
    }
}
