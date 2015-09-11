package com.pchsu.movieApp.utility;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.pchsu.movieApp.data.MovieContract;
import com.pchsu.movieApp.data.MovieInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MovieAppUtility {
    public static final String TAG = MovieAppUtility.class.getSimpleName();

    // Check if the network is available
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    // Checks if external storage is available for read and write
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // download the image from the URL and
    // store it as the filename in the app's picture folder
    // return a File instance of the file
    public static File downloadImageFile(Context context, String uRl, String filename) {
        //File direct = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);

        // if file exists, no need to download it again
        if (imgFile.exists()) return imgFile;

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Image Download")
                .setDescription("Downloading a image from url")
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PICTURES, filename);

        mgr.enqueue(request);
        return imgFile;
    }
    public static void deleteImageFile(Context context, String filename) {
        File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        imgFile.delete();
    }

    //
    // load data from the cursor and store the data as the format of movie array
    //
    public static MovieInfo[] convertCursorToMovies (Cursor cursor){
        if (cursor == null) return null;
        MovieInfo[] movies = new MovieInfo[cursor.getCount()];
        for(int j = 0; j< cursor.getCount(); j++){
            movies[j]= new MovieInfo();
        }
        //
        // load the movie data from the cursor and store into a movie array
        //
        int i =0;
        cursor.moveToPosition(-1);  // get position right before the 1st element
        while(cursor.moveToNext()){
            movies[i].setId(cursor.getInt(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_ID)));
            movies[i].setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_TITLE)));
            movies[i].setBackDropUrl(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_BACKDROP)));
            movies[i].setPosterUrl(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_POSTER)));
            movies[i].setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_OVERVIEW)));
            movies[i].setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RELEASEDATE)));
            movies[i].setVote(cursor.getInt(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_VOTE)));
            i++;
        }
        Log.d(TAG, i + " favorites found !");
        return movies;
    }

    //-----------------------------------------------------------------
    // other utility function not used in this app
    //-----------------------------------------------------------------
    // return a date 180 days before in the format of YYYY-MM-DD
    public static String getDateString_minus180(){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -180);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date.getTime());
        return dateString;
    }
    // return a date in a month later in the format of YYYY-MM-DD
    public static String getDateString_plus30(){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 30);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date.getTime());
        return dateString;
    }
}
