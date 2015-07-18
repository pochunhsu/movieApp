package com.pchsu.movieApp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pchsu.movieApp.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment_movieDetail extends Fragment{
    private Context mContext;
    private DetailActivity mActivity;

    @Bind(R.id.titleImage) ImageView mTitleImage;
    @Bind(R.id.titleLabel) TextView mTitleLabel;

    public Fragment_movieDetail() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (DetailActivity) mContext;

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        String ImageUrl = mContext.getString(R.string.imageUrlPath) + mActivity.getMovie().getBackDropPath();

        Picasso.with(mContext)
                .load(ImageUrl)
                .into(mTitleImage);

        mTitleLabel.setText(mActivity.getMovie().getTitle());

        return rootView;
    }

}
