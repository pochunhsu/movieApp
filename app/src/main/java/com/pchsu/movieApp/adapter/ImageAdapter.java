package com.pchsu.movieApp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pchsu.movieApp.R;
import com.pchsu.movieApp.data.MovieInfo;
import com.pchsu.movieApp.ui.MainActivity;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private MovieInfo[] mMovies;
    private int mImageWidth, mImageHeight;

    public ImageAdapter(Context context, MovieInfo[] movies) {
        mContext = context;
        mMovies = movies;

        MainActivity mainActivity = (MainActivity) context;

        // prepare the image size for view_holder setting and picasso resize
        // Display 2x2 in portrait mode and 3x1 in landscape mode
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        if (mainActivity.isTwoPane()){
            mImageWidth = metrics.widthPixels / 9;
            mImageHeight = (int) (mImageWidth * 1.5);
        }else {
            int orientation = mContext.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                mImageWidth = metrics.widthPixels / 2;
                mImageHeight = (int) (mImageWidth * 1.5);
            } else { // landscape orientation
                mImageWidth = metrics.widthPixels / 3;
                mImageHeight = (int) (mImageWidth * 1.5);
            }
        }
    }

    public void setMovies(MovieInfo[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
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
        ViewHolder holder;
        ImageView image;
        String ImageUrl;
        if (convertView == null) {
            image = new ImageView(mContext);
            image.setAdjustViewBounds(true);
            image.setLayoutParams(new GridView.LayoutParams(mImageWidth, mImageHeight));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            convertView = image;

            holder = new ViewHolder();
            holder.moviePoster = image;

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            image = holder.moviePoster;
        }

        //ImageUrl = mContext.getString(R.string.imageUrlPath) + mMovies[position].getPosterUrl();

        Picasso.with(mContext)
                .load(mMovies[position].getPosterUrl())
//                .placeholder(R.drawable.cloud_download)
                .error(R.drawable.cloud_error)
                .resize(mImageWidth, mImageHeight) // no need; already set scaleType for image
                .into(image);
        return convertView;
    }

    private static class ViewHolder{
        ImageView moviePoster;
    }
}
