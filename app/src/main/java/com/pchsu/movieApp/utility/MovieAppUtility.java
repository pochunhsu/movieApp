package com.pchsu.movieApp.utility;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MovieAppUtility {
    public static final String TAG = MovieAppUtility.class.getSimpleName();

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

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void downloadImageFile(Context context, String uRl, String filename) {
        //File direct = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);

        if (imgFile.exists()) return;

        //Log.v(TAG, direct.getAbsolutePath());
        /*File direct = new File(Environment.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES)
                + filename);*/
/*
        if (!direct.exists()) {
            direct.mkdirs();
        }
*/
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

    }
}
