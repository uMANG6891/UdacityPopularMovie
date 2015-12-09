package com.umang.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.MovieReadMoreFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 09/12/15.
 */
public class MovieReadMoreActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_read_more);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_movie_read_more, new MovieReadMoreFragment())
                    .commit();
        }
    }
}
