package com.umang.popularmovies.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment.AnimateToolbar;
import com.umang.popularmovies.ui.fragments.MainActivityFragment.ShowMovieDetailCallback;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ShowMovieDetailCallback, AnimateToolbar {

    final int WHAT = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    boolean isTwoPaneLayout = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT) {
                if (msg.arg1 == -1) {
                    // hide detail fragment from main_activity if no data is present on main fragment
                    findViewById(R.id.fragment_detail).setVisibility(View.GONE);
                } else {
                    DetailActivityFragment fragDetail = new DetailActivityFragment();
                    Bundle b = new Bundle();
                    b.putInt(Constants.EXTRA_MOVIE_ID, msg.arg1);
                    b.putBoolean(Constants.EXTRA_IS_TWO_PANE, isTwoPaneLayout);

                    fragDetail.setArguments(b);
                    findViewById(R.id.fragment_detail).setVisibility(View.VISIBLE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_detail, fragDetail)
                            .commit();
                }
            }
        }
    };
    private int LAST_MOVIE_LOADED = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragment_detail) != null) {
            isTwoPaneLayout = true;
        }
    }

    @Override
    public void onMovieItemSelected(boolean dataFromFirstLoad, int movieId, View view) {
        if (isTwoPaneLayout) {
            if (LAST_MOVIE_LOADED != movieId) {
                LAST_MOVIE_LOADED = movieId;
                Message msg = new Message();
                msg.what = WHAT;
                msg.arg1 = movieId;
                handler.sendMessage(msg);
            }
        } else {
            if (!dataFromFirstLoad) { // data from first load comes when updating detail fragment in two pane mode
                Intent i = new Intent(this, DetailActivity.class);
                i.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
                i.putExtra(Constants.EXTRA_IS_TWO_PANE, isTwoPaneLayout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    View iv = view.findViewById(R.id.item_mg_iv_movie_poster);

                    Pair<View, String> ivPair = new Pair<>(iv, iv.getTransitionName());
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, ivPair).toBundle();
                    startActivity(i, bundle);
                } else {
                    startActivity(i);
                }
            }
        }
    }

    @Override
    public void onScroll(int scrollY) {

    }
}
