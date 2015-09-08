package com.pchsu.movieApp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.pchsu.movieApp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITE = "favoriteMovie";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favoriteMovie";

        public static final String COLUMN_ID = "api_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASEDATE = "releaseDate";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_TRAILER_1 = "trailer_1";
        public static final String COLUMN_TRAILER_2 = "trailer_2";
        public static final String COLUMN_REVIEWS = "reviews";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
