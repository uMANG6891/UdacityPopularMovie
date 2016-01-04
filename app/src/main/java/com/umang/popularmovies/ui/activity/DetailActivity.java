package com.umang.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment.AnimateToolbar;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment.MovieTitle;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivity extends AppCompatActivity implements AnimateToolbar, MovieTitle {

    @Bind(R.id.frag_d_abl_appbar)
    AppBarLayout appbar;
    @Bind(R.id.frag_d_tb_toolbar)
    Toolbar toolbar;

    @Bind(R.id.frag_d_tb_toolbar_title)
    TextView tvToolbarTitle;

    int mParallaxImageHeight;
    int PRIMARY_COLOR;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setTitle("");

        PRIMARY_COLOR = ContextCompat.getColor(this, R.color.colorPrimary);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.backdrop_height);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            DetailActivityFragment fragDetail = new DetailActivityFragment();
            if (intent != null && intent.hasExtra(Constants.EXTRA_MOVIE_ID)) {
                Bundle b = new Bundle();
                b.putInt(Constants.EXTRA_MOVIE_ID, intent.getIntExtra(Constants.EXTRA_MOVIE_ID, 0));
                b.putBoolean(Constants.EXTRA_IS_TWO_PANE, intent.getBooleanExtra(Constants.EXTRA_IS_TWO_PANE, false));
                fragDetail.setArguments(b);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_detail, fragDetail)
                    .commit();
        }
    }

    @Override
    public void onScroll(int scrollY) {
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        appbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, PRIMARY_COLOR));
        tvToolbarTitle.setAlpha(alpha);
    }

    @Override
    public void getMovieTitle(String title) {
        tvToolbarTitle.setText(title);
    }
}
