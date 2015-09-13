package com.pchsu.movieApp.adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pchsu.movieApp.R;

public class ReviewListAdapter extends BaseAdapter {
    public static final String TAG = ReviewListAdapter.class.getSimpleName();

    private Context mContext;
    private Pair[] mReviews;

    public ReviewListAdapter(Context context, Pair[] reviews){
        mContext = context;
        mReviews = reviews;
    }

    public void setListViewItems(Pair[] reviews){
        mReviews = reviews;
    }

    @Override
    public int getCount() {
        return mReviews.length;
    }

    @Override
    public Object getItem(int position) {
        return mReviews[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // we aren't going to use this. Tag Items for easy reference.
    }

    // best practice to implement getView for adapter is to "recycle" the created ones
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            // the view doesn't exit yet; always need to fill it first time
            convertView = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, null);
            holder = new ViewHolder();
            holder.text_author = (TextView) convertView.findViewById(R.id.text_author);
            holder.text_content = (TextView) convertView.findViewById(R.id.text_review);

            convertView.setTag(holder);
        }else{
            // view recycled: No need to inflate / findViewById
            holder = (ViewHolder) convertView.getTag();
        }

        Pair review = mReviews[position];
        if (review != null) {
            if (review.first!=null) {
                holder.text_author.setText(review.first.toString());
            }else{
                holder.text_author.setText("Anonymous");
            }
            if (review.second!=null) {
                holder.text_content.setText(review.second.toString());
            }else{
                holder.text_author.setText("No content");
            }
        }else{
            Log.e(TAG, "getting null review object");
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView text_author;
        TextView text_content;
    }
}
