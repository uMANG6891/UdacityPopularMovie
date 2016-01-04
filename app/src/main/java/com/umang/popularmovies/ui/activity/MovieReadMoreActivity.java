package com.umang.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.MovieReadMoreFragment;

import butterknife.Bind;

/**
 * Created by umang on 09/12/15.
 */
public class MovieReadMoreActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    @Bind(R.id.frag_d_tb_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_read_more);
        setSupportActionBar((Toolbar) findViewById(R.id.frag_mrm_tb_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_movie_read_more, new MovieReadMoreFragment())
                    .commit();
        }
    }
}
