package com.umang.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;
import com.umang.popularmovies.ui.fragments.MainActivityFragment.ShowMovieDetailCallback;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ShowMovieDetailCallback {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    boolean isTwoPaneLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragment_detail) != null) {
            isTwoPaneLayout = true;
            if (savedInstanceState == null) {
                DetailActivityFragment fragDetail = new DetailActivityFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_detail, fragDetail)
                        .commit();
            }
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public void onItemSelected(int movieId) {
        if (isTwoPaneLayout) {
            DetailActivityFragment fragDetail = new DetailActivityFragment();
            Bundle b = new Bundle();
            b.putInt(Constants.EXTRA_MOVIE_ID, movieId);

            fragDetail.setArguments(b);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_detail, fragDetail)
                    .commit();
        } else {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
            startActivity(i);
        }
    }
}
