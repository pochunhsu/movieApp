package com.pchsu.movieApp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private MovieInfo[] mMovies;

    public ImageAdapter(Context context, MovieInfo[] movies) {
        mContext = context;
        mMovies = movies;
    }
    @Override
    public int getCount() {
        return mMovies.length;
    }

    @Override
    public Object getItem(int position) {
        return mMovies[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;   // not used
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        String ImageUrl;
        if (convertView == null) {
            image = new ImageView(mContext);
            image.setAdjustViewBounds(true);
            //image.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 500));
        }else{
            image = (ImageView) convertView;
        }
        ImageUrl = mContext.getString(R.string.imageUrlPath) + mMovies[position].getPosterPath();
        Picasso.with(mContext).load(ImageUrl).into(image);
        return image;
    }
}
