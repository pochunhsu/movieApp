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
import com.pchsu.movieApp.utility.MovieAppUtility;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//import android.app.Fragment;

public class Fragment_movieDisplay extends Fragment {

    public static final String TAG = "MoviePoster";

    private Context mContext;
    private MainActivity mMainActivity;

    private MovieInfo[] mMovies;

    private GridView mGridview;
    private ImageAdapter mAdapter;

    private enum SortSetting{POPULAR, RATING, PLAYING, UPCOMING}
    private SortSetting mSortSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting to enable onCreateOptionMenu callback
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;

        View rootView = inflater.inflate(R.layout.fragment_movie_poster, container, false);
        mGridview = (GridView) rootView.findViewById(R.id.gridDisplay);

        // load the movieInfo array from the stored data
        // if data exists, no reload is required (after screen rotation)
        if (savedInstanceState == null){
            mSortSetting = SortSetting.POPULAR;  // default
            loadMovies();
        }else{
            mMovies = (MovieInfo[]) savedInstanceState.getParcelableArray("movies");
            updateDisplay();
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // reload movie data on different sort setting selected in the menu
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
            mSortSetting = SortSetting.POPULAR;
            loadMovies();
            mMainActivity.setTitle(R.string.title_popular);
            return true;
        }
        if (id == R.id.menu_sort_rating) {
            mSortSetting = SortSetting.RATING;
            loadMovies();
            mMainActivity.setTitle(R.string.title_top_rated);
            return true;
        }
        if (id == R.id.menu_sort_playing) {
            mSortSetting = SortSetting.PLAYING;
            loadMovies();
            mMainActivity.setTitle(R.string.title_now_playing);
            return true;
        }
        if (id == R.id.menu_sort_upcoming) {
            mSortSetting = SortSetting.UPCOMING;
            loadMovies();
            mMainActivity.setTitle(R.string.title_upcoming);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    // used OKHttp API to send URL and request movie data in JSON
    public void loadMovies(){
        if (MovieAppUtility.isNetworkAvailable(mContext)) {
            Uri.Builder uriBuilder = new Uri.Builder();

            // generate the URL for different sorting based on the setting
            String sortURL;
            if(mSortSetting == SortSetting.POPULAR){
                sortURL = "3/movie/popular";
            }else if (mSortSetting == SortSetting.RATING){
                sortURL = "3/movie/top_rated";
            }else if (mSortSetting == SortSetting.PLAYING){
                sortURL = "3/movie/now_playing";
            }else if (mSortSetting == SortSetting.UPCOMING){
                sortURL = "3/movie/upcoming";
            }else{
                sortURL = "3/movie/popular";
                Toast.makeText(mContext, "Invalid sort setting: default to POPULAR", Toast.LENGTH_SHORT).show();
            }

            uriBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .path(sortURL)
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
                            mMovies = parseMovieDetails(jsonData);

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

    // parse the JSON data and store result into MoiveInfo arrays
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

    // get screen ready: set up adapter and on onClick for the GridView
    public void updateDisplay(){

        // set up a new adapter or use the existing one
        if (mAdapter == null) {
            mAdapter = new ImageAdapter(mContext, mMovies);
            mGridview.setAdapter(mAdapter);
        }else {
            mAdapter.setMovies(mMovies);
        }

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(mMainActivity, DetailActivity.class);
                i.putExtra(MainActivity.MOVIE_INFO, mMovies[position]);

                startActivity(i);
            }
        });
    }
}