package com.pchsu.movieApp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pchsu.movieApp.R;

public class Fragment_movieReview  extends Fragment {
    public static Fragment_movieReview newInstance() {
        Fragment_movieReview fragment = new Fragment_movieReview();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the view and instantiate all elements
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);
        return rootView;
    }
}

