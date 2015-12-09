package com.umang.popularmovies.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.picasso.Picasso;
import com.umang.popularmovies.R;
import com.umang.popularmovies.data.FetchAsyncData;
import com.umang.popularmovies.data.MovieContract;
import com.umang.popularmovies.ui.activity.DetailActivity;
import com.umang.popularmovies.ui.activity.MovieReadMoreActivity;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ObservableScrollViewCallbacks {

    FragmentActivity con;

    @Bind(R.id.frag_d_rl_trailer_overlay)
    RelativeLayout rlVideoOverlay;

    @Bind(R.id.frag_d_ll_reviews)
    LinearLayout llReviews;
    @Bind(R.id.frag_d_ll_reviews_parent)
    LinearLayout llReviewsParent;
    @Bind(R.id.frag_d_ll_read_more)
    LinearLayout llReadMore;

    @Bind(R.id.frag_d_abl_appbar)
    AppBarLayout appbar;
    @Bind(R.id.frag_d_tb_toolbar)
    Toolbar toolbar;
    @Bind(R.id.frag_d_tb_tv_movie_name)
    TextView tvToolbarTitle;

    @Bind(R.id.frag_d_osv_scroll)
    ObservableScrollView osvScroll;

    @Bind(R.id.frag_d_iv_backdrop)
    ImageView ivBackdrop;
    @Bind(R.id.frag_d_iv_poster)
    ImageView ivPoster;
    @Bind(R.id.frag_d_tv_movie_name)
    TextView tvMovieName;
    @Bind(R.id.frag_d_tv_release_date)
    TextView tvReleaseDate;
    @Bind(R.id.frag_d_tv_votes)
    TextView tvVotes;
    @Bind(R.id.frag_d_tv_rating)
    TextView tvRating;
    @Bind(R.id.frag_d_tv_overview)
    TextView tvOverview;

    int MOVIE_ID;
    String LINK;
    int mParallaxImageHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        con = getActivity();

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) con).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) con).getSupportActionBar();
        if (actionbar != null) {
            setHasOptionsMenu(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        osvScroll.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.backdrop_height);
        llReadMore.setOnClickListener(this);

        Intent intent = con.getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE_ID)) {
            MOVIE_ID = intent.getIntExtra(DetailActivity.EXTRA_MOVIE_ID, 0);
        }
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                con,
                MovieContract.MovieEntry.buildOneMovieUri(MOVIE_ID),
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            data.moveToFirst();
            Picasso.with(con).load(Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE + data.getString(Constants.RV_COL_BACKDROP_PATH)).into(ivBackdrop);
            Picasso.with(con).load(Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE + data.getString(Constants.RV_COL_POSTER_PATH)).into(ivPoster);
            tvMovieName.setText(data.getString(Constants.RV_COL_TITLE));
            tvToolbarTitle.setText(data.getString(Constants.RV_COL_TITLE));
            tvReleaseDate.setText(Utility.getYear(data.getString(Constants.RV_COL_RELEASE_DATE)));
            tvVotes.setText(data.getString(Constants.RV_COL_VOTE_COUNT).concat(" "));
            tvRating.setText(data.getString(Constants.RV_COL_VOTE_AVERAGE));
            tvOverview.setText(data.getString(Constants.RV_COL_OVERVIEW));

            List<String> urls = new ArrayList<>();
            if (data.getString(Constants.RV_COL_CAST) == null || data.getString(Constants.RV_COL_CAST).length() == 0) {
                urls.add(Constants.BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/credits?api_key=" + Constants.MOVIE_DB_API_KEY);
            }
            if (data.getString(Constants.RV_COL_VIDEO_LINK) == null || data.getString(Constants.RV_COL_VIDEO_LINK).length() == 0) {
                urls.add(Constants.BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/videos?api_key=" + Constants.MOVIE_DB_API_KEY);
                rlVideoOverlay.setVisibility(View.GONE);
            } else {
                LINK = data.getString(Constants.RV_COL_VIDEO_LINK);
                rlVideoOverlay.setVisibility(View.VISIBLE);
                rlVideoOverlay.setOnClickListener(this);
            }

            if (data.getString(Constants.RV_COL_REVIEWS) == null || data.getString(Constants.RV_COL_REVIEWS).length() == 0) {
                urls.add(Constants.BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/reviews?api_key=" + Constants.MOVIE_DB_API_KEY);
                llReviewsParent.setVisibility(View.GONE);
            } else {
                try {
                    JSONArray reviews = new JSONArray(data.getString(Constants.RV_COL_REVIEWS));
                    if (reviews.length() > 0) {
                        llReviewsParent.setVisibility(View.VISIBLE);
                        JSONObject oneReview;

                        View view;
                        TextView tvAuthor;
                        TextView tvContent;
                        LayoutInflater inflater = getLayoutInflater(new Bundle());
                        for (int i = 0; i < reviews.length(); i++) {
                            oneReview = reviews.getJSONObject(i);
                            view = inflater.inflate(R.layout.item_review, llReviews, false);
                            tvAuthor = (TextView) view.findViewById(R.id.item_r_author);
                            tvContent = (TextView) view.findViewById(R.id.item_r_content);

                            tvAuthor.setText(oneReview.getString("author"));
                            tvContent.setText(oneReview.getString("content"));
                            llReviews.addView(view);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (urls.size() > 0) {
                FetchAsyncData task = new FetchAsyncData(con.getBaseContext());
                task.execute(urls);
            }
//            ((DetailActivity) con).setActionBarTitle(row.get(MOVIE_JSON.TITLE)); // setting the title in the toolbar
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_d_rl_trailer_overlay:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + LINK)));
                break;
            case R.id.frag_d_ll_read_more:
                Intent i = new Intent(con, MovieReadMoreActivity.class);
                i.putExtra(MovieReadMoreActivity.EXTRA_MOVIE_ID, MOVIE_ID);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                con.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        tvToolbarTitle.setAlpha(alpha);
        appbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));

        ivBackdrop.setTranslationY(scrollY / 2);
        rlVideoOverlay.setTranslationY(scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
