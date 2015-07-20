package com.pchsu.movieApp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pchsu.movieApp.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new Fragment_movieDetail())
                    .commit();
        }
    }
}
