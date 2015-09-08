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
    public void alertUserAboutError(String error_msg) {
        AlertDialogFragment dialog = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(AlertDialogFragment.ERR_MSG_TAG, error_msg);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), AlertDialogFragment.ERR_MSG_TAG);
    }
}
