package com.umang.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;

import butterknife.ButterKnife;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setTitle("");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_detail, new DetailActivityFragment())
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
