package com.pchsu.movieApp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.adapter.ReviewListAdapter;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static junit.framework.Assert.assertNotNull;

public class Fragment_movieDetail_more extends ListFragment {

    public static final String TAG = Fragment_movieDetail_more.class.getSimpleName();

    private Context mContext;
    private DetailActivity mActivity;
    private View mRootView;
    private Resources mResources;
    private MovieInfo mMovie;

    private Button[] mButtonTrailer;
    @Bind(R.id.label_noReview) TextView mlabel_noReview;

    public static Fragment_movieDetail_more newInstance() {
        Fragment_movieDetail_more fragment = new Fragment_movieDetail_more();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (DetailActivity) mContext;

        Intent intent = mActivity.getIntent();
        Parcelable parcelable = intent.getParcelableExtra(MainActivity.MOVIE_INFO);
        mMovie = (MovieInfo) parcelable;

        // program the sections' height based on the screen size and the weight integer specified in xml
        mResources = getResources();
        DisplayMetrics metrics = mResources.getDisplayMetrics();
        //int topSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.topSecWeight) / 100;
        //int middleSecHeight = metrics.heightPixels * mResources.getInteger(R.integer.middleSecWeight) / 100;

        // inflate the view and instantiate all elements
        mRootView = inflater.inflate(R.layout.fragment_movie_detail_more, container, false);

        ButterKnife.bind(this, mRootView);
        mButtonTrailer = new Button[2];
        mButtonTrailer[0] = (Button) mRootView.findViewById(R.id.button_trailer1);
        mButtonTrailer[1] = (Button) mRootView.findViewById(R.id.button_trailer2);

        loadTrailerAndReview(mMovie.getId());

        return mRootView;
    }

    // used OKHttp API to send URL and request movie data in JSON
    private boolean loadTrailerAndReview(int id){
        if (MovieAppUtility.isNetworkAvailable(mContext) == false) {
            return false;
        }
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .authority("api.themoviedb.org")
                .path("3/movie")
                .appendPath(id + "")
                .appendQueryParameter("api_key", getString(R.string.apiKey))
                .appendQueryParameter("append_to_response", "trailers,reviews");

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
                mActivity.alertUserAboutError("HTTP request fails ...");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // main_activity.runOnUiThread();
                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {
                        parseMoreMovieInfo(jsonData);

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                    } else {
                        mActivity.alertUserAboutError("HTTP response has error ...");
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

    private void parseMoreMovieInfo (String jsonData)throws JSONException{
        JSONObject jo_root = new JSONObject(jsonData);
        JSONArray ja_videos = jo_root.getJSONObject("trailers").getJSONArray("youtube");
        JSONArray ja_reviews = jo_root.getJSONObject("reviews").getJSONArray("results");

        // parsing JSON for trailer links
        String[] tubeLinks = new String [ja_videos.length()];
        int trailer_cnt =0;
        for(int i =0; i< ja_videos.length(); i++){
            JSONObject jo_video = ja_videos.getJSONObject(i);

            if (jo_video.getString("type").equals("Trailer")) {
                tubeLinks[trailer_cnt] = jo_video.getString("source");
                assertNotNull("trailer link null", tubeLinks[trailer_cnt]);
                trailer_cnt++;
            }
        }
        if (trailer_cnt != 0) {
            mMovie.setTrailerLinks(tubeLinks);
        }

        // parsing JSON for review strings
        if (ja_reviews.length() == 0) {
            return;
        }else {
            Pair<String, String>[] reviews = new Pair[ja_reviews.length()];
            for (int i = 0; i < ja_reviews.length(); i++) {
                JSONObject jo_review = ja_reviews.getJSONObject(i);
                Pair review = new Pair(jo_review.getString("author"), jo_review.getString("content"));
                reviews[i] = review;
            }
            mMovie.setReviews(reviews);
        }
    }

    private void updateDisplay(){
        if (mRootView == null) {
            Log.e(TAG, "The rootView is not inflated yet!");
            return;
        }

        // set up trailer play button
        if (mMovie.getTrailerLinks() == null) {
            Log.v(TAG, " Request got no trailers!");
        }else {
            int cnt = mMovie.getTrailerLinks().length;
            for (int i = 0; i < Math.min(2, cnt); i++) {
                if (mMovie.getTrailerLinks()[i] != null) {
                    mButtonTrailer[i].setVisibility(View.VISIBLE);
                    mButtonTrailer[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mMovie.getTrailerLinks()[0])));
                        }
                    });
                }
            }
        }

        // set up ListView of reviews
        if(mMovie.getReviews() != null) {
            mlabel_noReview.setVisibility(View.INVISIBLE);
            ReviewListAdapter adapter = new ReviewListAdapter(mContext, mMovie.getReviews());
            setListAdapter(adapter);
        }else{
            Log.v(TAG, " Request got no reviews!");
        }

        // set up the shareIntent
        Intent shareIntent = createShareIntent();
        if (shareIntent!=null) {
            mActivity.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        if (mMovie.getTrailerLinks()==null ) return null;
        if (mMovie.getTrailerLinks()[0]==null ) return null;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT , "Movie to share:" + mMovie.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + mMovie.getTrailerLinks()[0]);
        return shareIntent;
    }

    /*
    private void updateDisplay (){
        final VideoView mVideoView = (VideoView) getActivity().findViewById(R.id.trailer_view);
        mVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
        Log.v(TAG, "updateDisplay");
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }
    */
}
