package com.pchsu.movieApp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
