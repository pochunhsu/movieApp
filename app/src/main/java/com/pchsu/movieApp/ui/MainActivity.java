package com.pchsu.movieApp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pchsu.movieApp.R;


public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE_INFO = "MOVIE_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new Fragment_movieDisplay())
                    .commit();
        }
    }

    public void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
