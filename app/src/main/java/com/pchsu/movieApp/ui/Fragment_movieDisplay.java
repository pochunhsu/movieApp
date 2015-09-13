package com.pchsu.movieApp.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.adapter.ImageAdapter;
import com.pchsu.movieApp.data.MovieContract;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.utility.Communication;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment_movieDisplay extends Fragment {

    public static final String TAG = Fragment_movieDisplay.class.getSimpleName();
    public static final String TAG_MOVIE_DATA = "MovieData";

    private Context mContext;
    private MainActivity mMainActivity;
    private ContentResolver mResolver;

    private MovieInfo[] mMovies;
    private ImageAdapter mAdapter;

    private int mLastSortSetting;

    private boolean mOnCreatedViewCalled = false;
    // callback interface
    private Communication mCallBack;

    @Bind(R.id.gridDisplay) GridView mGridview;
    @Bind(R.id.label_noMovie) TextView mLabel_noMovie;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (Communication) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement Communication!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;
        mResolver = mContext.getContentResolver();

        // setting to enable onCreateOptionMenu callback
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_poster, container, false);
        ButterKnife.bind(this, rootView);

        // load the movieInfo array from the stored data
        // if data exists in saveInstanceState, no reload is required (after screen rotation)
        if (savedInstanceState == null) {
            // do nothing
            // postpone the work to onActivityCreated
            // because work need to be serialized with inTwoPane in Activity.OnCreate
        } else {
            // regain the movie data from the saved state
            mMovies = (MovieInfo[]) savedInstanceState.getParcelableArray(TAG_MOVIE_DATA);
            updateDisplay();
        }
        mOnCreatedViewCalled = true;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // loadMovies.updateDisplay needs to check pane layout setting
        // so have it delayed to this time point
        if (savedInstanceState == null) {
            if (MovieAppUtility.isNetworkAvailable(mContext)) {
                mMainActivity.setTitle(R.string.title_popular);
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .path("3/movie/popular")
                        .appendQueryParameter("api_key", getString(R.string.apiKey));
                Uri uri = uriBuilder.build();
                loadMovies(uri);
                mLastSortSetting = R.id.menu_sort_popular;
            }
        }
    }

    @Override
    public void onResume() {
        // force refresh in favorite mode in case a movie gets removed from favorite
        super.onResume();
        if (mLastSortSetting == R.id.menu_favorite ||
            ! MovieAppUtility.isNetworkAvailable(mContext)) {
            Cursor cursor = mResolver.query(MovieContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
            mMovies = MovieAppUtility.convertCursorToMovies(cursor);
            updateDisplay();

            if (mMovies == null){
                String str = "";
                if (! MovieAppUtility.isNetworkAvailable(mContext)) {
                    str = "No Network and ";
                }
                mLabel_noMovie.setText(str + "No Local Favorite");
                mLabel_noMovie.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.movie_search).getActionView();
        searchView.setQueryHint("Movie Title");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .path("3/search/movie")
                        .appendQueryParameter("query", query)
                        .appendQueryParameter("api_key", getString(R.string.apiKey));
                Uri uri = uriBuilder.build();
                loadMovies(uri);
                mMainActivity.setTitle("MovieApp " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // reload movie data on different sort setting selected in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String uri_path = null;

        // skip the update if the setting is the same as the old one
        if (mLastSortSetting == id) return super.onOptionsItemSelected(item);

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.setting_sort:
                break;
            case R.id.menu_sort_popular:
                uri_path = "3/movie/popular";
                mMainActivity.setTitle(R.string.title_popular);
                break;
            case R.id.menu_sort_rating:
                uri_path = "3/movie/top_rated";
                mMainActivity.setTitle(R.string.title_top_rated);
                break;
            case R.id.menu_sort_playing:
                uri_path = "3/movie/now_playing";
                mMainActivity.setTitle(R.string.title_now_playing);
                break;
            case R.id.menu_sort_upcoming:
                uri_path = "3/movie/upcoming";
                mMainActivity.setTitle(R.string.title_upcoming);
                break;
            case R.id.menu_favorite:
                uri_path = "favorite";  // a hack; the code will get the uri to content provider
                mMainActivity.setTitle(R.string.title_favorite);
                break;
            default:
                uri_path = "?";
                break;
        }

        if (uri_path == null) return super.onOptionsItemSelected(item);
        if (uri_path.equals("favorite")) {
            Cursor cursor = mResolver.query(MovieContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
            mMovies = MovieAppUtility.convertCursorToMovies(cursor);
            mLastSortSetting = id;
            updateDisplay();

        } else {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.themoviedb.org")
                    .path(uri_path)
                    .appendQueryParameter("api_key", getString(R.string.apiKey));

            Uri uri = uriBuilder.build();
            mLastSortSetting = id;
            loadMovies(uri);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(TAG_MOVIE_DATA, mMovies);
        super.onSaveInstanceState(outState);
    }

    // used OKHttp API to send URL and request movie data in JSON
    private boolean loadMovies(Uri uri) {
        if (!MovieAppUtility.isNetworkAvailable(mContext)) {
            mCallBack.alertUserAboutError("No Network Access !");
            return false;
        }

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
                mCallBack.alertUserAboutError("HTTP request fails ...");
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
                        // OKHTTP might have a bug here.
                        // this is hit everytime the search icon is clicked
                        // BEFORE any text query submmited
                        //mCallBack.alertUserAboutError("HTTP response has error ...");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception caught: ", e);
                }
            }
        });
        return true;
    }

    // parse the JSON data and store result into MoiveInfo arrays
    private MovieInfo[] parseMovieDetails(String jsonData) throws JSONException {
        JSONObject jo_root = new JSONObject(jsonData);
        JSONArray ja_results = jo_root.getJSONArray("results");

        MovieInfo[] movies = new MovieInfo[ja_results.length()];

        for (int i = 0; i < ja_results.length(); i++) {
            JSONObject jo_movie = ja_results.getJSONObject(i);
            MovieInfo movie = new MovieInfo();

            movie.setId(jo_movie.getInt("id"));
            movie.setTitle(jo_movie.getString("title"));
            movie.setBackDropUrl(mContext.getString(R.string.imageUrlPath) + jo_movie.getString("backdrop_path"));
            movie.setPosterUrl(mContext.getString(R.string.imageUrlPath) + jo_movie.getString("poster_path"));
            movie.setOverview(jo_movie.getString("overview"));
            movie.setReleaseDate(jo_movie.getString("release_date"));
            movie.setVote(jo_movie.getDouble("vote_average"));

            movies[i] = movie;
        }
        return movies;
    }

    // get screen ready: set up adapter and on onClick for the GridView
    private void updateDisplay() {
        if (mOnCreatedViewCalled != true) return;

        // if no movies to display, hide the gridView
        if (mMovies == null ){
            mGridview.setVisibility(View.INVISIBLE);
            mLabel_noMovie.setVisibility(View.VISIBLE);
            mLabel_noMovie.setText("No Movies found in search of " + SortRId2String(mLastSortSetting));

            // clear other panes in tablet mode
            if (mCallBack.isTwoPane()) {
                mCallBack.onMovieSelected(null);
            }
        }else {
            mGridview.setVisibility(View.VISIBLE);
            mLabel_noMovie.setVisibility(View.INVISIBLE);

            // set up a new adapter or use the existing one
            if (mAdapter == null) {
                mAdapter = new ImageAdapter(mContext, mMovies);
                mGridview.setAdapter(mAdapter);
            } else {
                mAdapter.setMovies(mMovies);
            }

            mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCallBack.onMovieSelected(mMovies[position]);
                }
            });

            // set the default to details in tablet mode
            if (mCallBack.isTwoPane()) {
                mCallBack.onMovieSelected(mMovies[0]);
            }
        }
    }

    // renew the poster display if the sort setting is favorite
    public void renewDisplay(){
        // public method accessible to the other classes needs this check
        // to avoid access states that aren't ready yet
        if (mOnCreatedViewCalled != true) return;

        if (mLastSortSetting == R.id.menu_favorite){
            Cursor cursor = mResolver.query(MovieContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
            mMovies = MovieAppUtility.convertCursorToMovies(cursor);
            updateDisplay();

            if (mMovies == null){
                mLabel_noMovie.setText("No Local Favorite");
                mLabel_noMovie.setVisibility(View.VISIBLE);
            }
        }else{
            // additional functionality for http renew can be added here
        }
    }

    public MovieInfo getDefaultMovie(){
        if (mMovies != null){
            return mMovies[0];
        }else{
            return null;
        }
    }

    // convert the menu option id to string
    private String SortRId2String (int id){
        String str = "";
        switch (id) {
            case R.id.setting_sort:
                break;
            case R.id.menu_sort_popular:
                str = "Popular";
                break;
            case R.id.menu_sort_rating:
                str = "Top Rated";
                break;
            case R.id.menu_sort_playing:
                str = "Now Playing";
                break;
            case R.id.menu_sort_upcoming:
                str = "Upcoming";
                break;
            case R.id.menu_favorite:
                str = "Favorite";
                break;
            default:
                break;
        }
        return str;
    }
}