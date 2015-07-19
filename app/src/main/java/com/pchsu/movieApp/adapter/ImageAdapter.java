package com.pchsu.movieApp.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private MovieInfo[] mMovies;
    private int mImageWidth, mImageHeight;

    public ImageAdapter(Context context, MovieInfo[] movies) {
        mContext = context;
        mMovies = movies;

        // prepare the image size number for picasso resize
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mImageWidth = metrics.widthPixels/2;
        mImageHeight = (int) (mImageWidth * 1.5);
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
            image.setLayoutParams(new GridView.LayoutParams(mImageWidth, mImageHeight));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }else{
            image = (ImageView) convertView;
        }

        ImageUrl = mContext.getString(R.string.imageUrlPath) + mMovies[position].getPosterPath();

        Picasso.with(mContext)
                .load(ImageUrl)
//                .resize(mImageWidth, mImageHeight) // no need; already set scaleType for image
                .into(image);
        return image;
    }
}
