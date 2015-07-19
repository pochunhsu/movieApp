package com.pchsu.movieApp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.adapter.ImageAdapter;
import com.pchsu.movieApp.data.MovieInfo;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import android.app.Fragment;

public class Fragment_movieDisplay extends Fragment {

    final String TAG = "Update_Moive";

    private Context mContext;
    private MainActivity mMainActivity;

    private GridView mGridview;

    public Fragment_movieDisplay() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;

        View rootView = inflater.inflate(R.layout.fragment_movie_poster, container, false);
        mGridview = (GridView) rootView.findViewById(R.id.gridDisplay);

        loadMovies();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setting_sort) {
            return true;
        }

        if (id == R.id.menu_sort_popular) {
            Toast.makeText(mContext, "sort popular", Toast.LENGTH_SHORT);
            return true;
        }
        if (id == R.id.menu_sort_rating) {
            Toast.makeText(mContext, "sort rating", Toast.LENGTH_SHORT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMovies(){
        if (mMainActivity.isNetworkAvailable()) {
            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .path("3/movie/popular")
//                        .path("3/discover/movie")
//                        .appendQueryParameter("sort_by", "polularity.desc")
//                        .appendQueryParameter("release_date.gte", getDateString_minus180())
//                        .appendQueryParameter("release_date.lte", getDateString_plus30())
                        .appendQueryParameter("api_key", getString(R.string.apiKey));

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
                    mMainActivity.alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    // main_activity.runOnUiThread();
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mMainActivity.mMovies = parseMovieDetails(jsonData);
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            mMainActivity.alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });
        }else {
            Toast.makeText(mContext, getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
        }
    }

    // return a date 180 days before in the format of YYYY-MM-DD
    public String getDateString_minus180(){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -180);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date.getTime());
        return dateString;
    }

    // return a date in a month later in the format of YYYY-MM-DD
    public String getDateString_plus30(){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 30);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date.getTime());
        return dateString;
    }

    public MovieInfo[] parseMovieDetails(String jsonData) throws JSONException{
        JSONObject jo_root = new JSONObject(jsonData);
        JSONArray ja_results = jo_root.getJSONArray("results");

        MovieInfo[] movies = new MovieInfo[ja_results.length()];

        for(int i =0; i< ja_results.length(); i++){
            JSONObject jo_movie = ja_results.getJSONObject(i);
            MovieInfo movie = new MovieInfo();

            movie.setTitle(jo_movie.getString("title"));
            movie.setBackDropPath(jo_movie.getString("backdrop_path"));
            movie.setPosterPath(jo_movie.getString("poster_path"));
            movie.setOverview(jo_movie.getString("overview"));
            movie.setReleaseDate(jo_movie.getString("release_date"));
            movie.setVote(jo_movie.getDouble("vote_average"));

            movies[i] = movie;
        }
        return movies;
    }

    public void updateDisplay(){
        mGridview.setAdapter(new ImageAdapter(mContext, mMainActivity.mMovies));

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(mMainActivity, DetailActivity.class);
                i.putExtra(MainActivity.MOVIE_INFO, mMainActivity.mMovies[position]);

                startActivity(i);
            }
        });
    }
}