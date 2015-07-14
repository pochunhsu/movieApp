package com.pchsu.movieApp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pchsu.movieApp.R;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Date;

public class Fragment_movieDisplay extends Fragment {

    final String TAG = "Update_Moive";
    public Fragment_movieDisplay() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateMovies();
        View rootView = inflater.inflate(R.layout.layout_movie_display, container, false);
        return rootView;
    }

    public void updateMovies(){
        final MainActivity main_activity = (MainActivity)getActivity();
        if (main_activity.isNetworkAvailable()) {
            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .path("3/discover/movie")
                        .appendQueryParameter("sort_by","polularity.desc")
                        .appendQueryParameter("page", "10")
                        .appendQueryParameter("api_key",getString(R.string.apiKey));

            Uri uri = uriBuilder.build();
            Log.v(TAG, uri.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(uri.toString())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // main_activity.runOnUiThread();
                    main_activity.alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    // main_activity.runOnUiThread();
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            // mForecast = parseForecastDetails(jsonData);
                            main_activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO: updateDisplay();
                                }
                            });
                        } else {
                            main_activity.alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                  //  } catch (JSONException e) {
                  //      Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });
        }else {
            Toast.makeText(main_activity, getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
        }
    }

    // figure out the current date
    // return a date of 90 days before in the format of YYYY-MM-DD
    public String getDate(){
        Date dateNow = new Date();
       // Date dateBefore = new Date(dateNow.getTime() - (90*24*60*60*1000) );
       // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       // String dateString = formatter.format(dateBefore);
       String dateString = "";
       return dateString;
    }

}