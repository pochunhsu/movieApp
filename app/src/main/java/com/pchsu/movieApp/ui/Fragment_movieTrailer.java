package com.pchsu.movieApp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pchsu.movieApp.R;

public class Fragment_movieTrailer extends Fragment {
    public static Fragment_movieTrailer newInstance() {
        Fragment_movieTrailer fragment = new Fragment_movieTrailer();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the view and instantiate all elements
        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        return rootView;
    }
}
