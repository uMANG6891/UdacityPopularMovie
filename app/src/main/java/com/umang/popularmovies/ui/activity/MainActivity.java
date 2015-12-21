package com.umang.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment.AnimateToolbar;
import com.umang.popularmovies.ui.fragments.MainActivityFragment.ShowMovieDetailCallback;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ShowMovieDetailCallback, AnimateToolbar {

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
//                DetailActivityFragment fragDetail = new DetailActivityFragment();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.fragment_detail, fragDetail)
//                        .commit();
            }
        }
    }

    @Override
    public void onItemSelected(boolean dataFromFirstLoad, int movieId) {
        if (isTwoPaneLayout) {
            Message msg = new Message();
            msg.what = WHAT;
            msg.arg1 = movieId;
            handler.sendMessage(msg);
        } else {
            if (!dataFromFirstLoad) { // data from first load comes when updating detail fragment in two pane mode
                Intent i = new Intent(this, DetailActivity.class);
                i.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
                startActivity(i);
            }
        }
    }

    final int WHAT = 1;
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

                    fragDetail.setArguments(b);
                    findViewById(R.id.fragment_detail).setVisibility(View.VISIBLE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_detail, fragDetail)
                            .commit();
                }
            }
        }
    };

    @Override
    public void onScroll(int scrollY) {

    }
}
