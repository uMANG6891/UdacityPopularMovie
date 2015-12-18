package com.umang.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.fragments.DetailActivityFragment;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivity extends AppCompatActivity implements DetailActivityFragment.AnimateToolbar {

    @Bind(R.id.frag_d_abl_appbar)
    AppBarLayout appbar;
    @Bind(R.id.frag_d_tb_toolbar)
    Toolbar toolbar;

    int mParallaxImageHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setTitle("");

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.backdrop_height);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            DetailActivityFragment fragDetail = new DetailActivityFragment();
            if (intent != null && intent.hasExtra(Constants.EXTRA_MOVIE_ID)) {
                Bundle b = new Bundle();
                b.putInt(Constants.EXTRA_MOVIE_ID, intent.getIntExtra(Constants.EXTRA_MOVIE_ID, 0));
                fragDetail.setArguments(b);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_detail, fragDetail)
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

    @Override
    public void onScroll(int scrollY) {

        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        appbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
    }
}
