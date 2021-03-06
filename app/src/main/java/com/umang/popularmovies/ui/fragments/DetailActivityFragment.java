package com.umang.popularmovies.ui.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;
import com.umang.popularmovies.R;
import com.umang.popularmovies.data.FetchAsyncData;
import com.umang.popularmovies.data.MovieContract;
import com.umang.popularmovies.data.MovieContract.CommentEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.data.MovieContract.MovieEntry;
import com.umang.popularmovies.ui.activity.MovieReadMoreActivity;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ObservableScrollViewCallbacks {

    private final int LOADER_MOVIE_DETAIL = 0;
    private final int LOADER_COMMENTS = 1;
    private final int LOADER_FAVOURITE = 2;
    FragmentActivity con;
    @Bind(R.id.frag_d_rl_trailer_overlay)
    RelativeLayout rlVideoOverlay;
    @Bind(R.id.frag_d_ll_reviews)
    LinearLayout llReviews;
    @Bind(R.id.frag_d_ll_reviews_parent)
    LinearLayout llReviewsParent;
    @Bind(R.id.frag_d_ll_read_more)
    LinearLayout llReadMore;
    @Bind(R.id.frag_d_osv_scroll)
    ObservableScrollView osvScroll;
    @Bind(R.id.frag_d_iv_backdrop)
    ImageView ivBackdrop;
    @Bind(R.id.frag_d_iv_poster)
    ImageView ivPoster;
    @Bind(R.id.frag_d_iv_fav_movie)
    ImageView ivFavMovie;
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
    boolean IS_TWO_PANE_LAYOUT;
    String MOVIE_TITLE;
    String LINK;
    boolean IS_FAVOURITE = false;
    Menu menu;
    MenuInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        con = getActivity();

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        osvScroll.setScrollViewCallbacks(this);
        llReadMore.setOnClickListener(this);
        ivFavMovie.setOnClickListener(this);

        if (getArguments() != null) {
            MOVIE_ID = getArguments().getInt(Constants.EXTRA_MOVIE_ID, -1);
            IS_TWO_PANE_LAYOUT = getArguments().getBoolean(Constants.EXTRA_IS_TWO_PANE, false);
            if (MOVIE_ID != -1) {
                getLoaderManager().initLoader(LOADER_MOVIE_DETAIL, null, this);
                getLoaderManager().initLoader(LOADER_COMMENTS, null, this);
                getLoaderManager().initLoader(LOADER_FAVOURITE, null, this);
            }
        }
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_MOVIE_DETAIL:
                return new CursorLoader(
                        con,
                        MovieEntry.buildOneMovieUri(MOVIE_ID),
                        Constants.MOVIE_PROJECTION_COLS,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        null,
                        null
                );
            case LOADER_COMMENTS:
                return new CursorLoader(
                        con,
                        CommentEntry.buildCommentUri(MOVIE_ID),
                        Constants.COMMENT_MOVIE_PROJECTION_COLS,
                        CommentEntry.COLUMN_MOVIE_ID + " = ?",
                        null,
                        null
                );
            case LOADER_FAVOURITE:
                return new CursorLoader(
                        con,
                        FavouriteEntry.buildFavouriteMovieUri(MOVIE_ID),
                        null,
                        FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_MOVIE_DETAIL:
                if (data.getCount() > 0) {
                    data.moveToFirst();
                    MOVIE_TITLE = data.getString(Constants.RV_COL_MSB_TITLE);
                    Picasso.with(con).load(Constants.BASE_IMAGE_URL + Constants.POSTER_SIZE + data.getString(Constants.RV_COL_MSB_POSTER_PATH)).into(ivPoster);
                    Picasso.with(con).load(Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE + data.getString(Constants.RV_COL_MSB_BACKDROP_PATH)).into(ivBackdrop);
                    tvMovieName.setText(MOVIE_TITLE);
                    if (!IS_TWO_PANE_LAYOUT)
                        ((MovieTitle) con).getMovieTitle(MOVIE_TITLE);
                    tvReleaseDate.setText(Utility.getYear(data.getString(Constants.RV_COL_MSB_RELEASE_DATE)));
                    tvVotes.setText(data.getString(Constants.RV_COL_MSB_VOTE_COUNT).concat(" "));
                    tvRating.setText(Utility.parseRating(data.getString(Constants.RV_COL_MSB_VOTE_AVERAGE)));
                    tvOverview.setText(data.getString(Constants.RV_COL_MSB_OVERVIEW));

                    List<String> urls = new ArrayList<>();
                    String cast = data.getString(Constants.RV_COL_MSB_CAST);
                    if (cast == null || cast.length() == 0) {
                        urls.add(Constants.buildGetMovieReview(con, MOVIE_ID));
                    }
                    LINK = data.getString(Constants.RV_COL_MSB_VIDEO_LINK);
                    if (LINK == null || LINK.length() == 0) {
                        urls.add(Constants.buildGetMovieVideoLink(con, MOVIE_ID));
                        rlVideoOverlay.setVisibility(View.GONE);
                    } else {
                        if (menu != null && inflater != null)
                            onCreateOptionsMenu(menu, inflater);
                        rlVideoOverlay.setVisibility(View.VISIBLE);
                        rlVideoOverlay.setOnClickListener(this);
                    }

                    if (urls.size() > 0) {
                        FetchAsyncData task = new FetchAsyncData(con.getBaseContext());
                        task.execute(urls);
                    }
                }
                break;
            case LOADER_COMMENTS:
                if (data.getCount() == 0) {
                    List<String> urls = new ArrayList<>();
                    urls.add(Constants.buildGetMovieComments(con, MOVIE_ID));
                    FetchAsyncData task = new FetchAsyncData(con.getBaseContext());
                    task.execute(urls);
                    llReviewsParent.setVisibility(View.GONE);
                } else {
                    llReviewsParent.setVisibility(View.VISIBLE);

                    View view;
                    TextView tvAuthor;
                    TextView tvContent;
                    LayoutInflater inflater = getLayoutInflater(new Bundle());
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        view = inflater.inflate(R.layout.item_review, llReviews, false);
                        tvAuthor = (TextView) view.findViewById(R.id.item_r_author);
                        tvContent = (TextView) view.findViewById(R.id.item_r_content);

                        tvAuthor.setText(data.getString(Constants.RV_COL_CM_AUTHOR));
                        tvContent.setText(Utility.shortenComment(con, data.getString(Constants.RV_COL_CM_CONTENT)));
                        llReviews.addView(view);
                    }
                }
                break;
            case LOADER_FAVOURITE:
                IS_FAVOURITE = data.getCount() > 0;
                if (IS_FAVOURITE) {
//                    for (int i = 0; i < data.getCount(); i++) {
//                        data.moveToPosition(i);
//                    }
                    ivFavMovie.setImageResource(R.drawable.ic_action_fav);
                } else {
                    ivFavMovie.setImageResource(R.drawable.ic_action_fav_border);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_d_iv_fav_movie:
                if (IS_FAVOURITE) {
                    con.getContentResolver().delete(FavouriteEntry.CONTENT_URI,
                            FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{String.valueOf(MOVIE_ID)});
                    Toast.makeText(con, MOVIE_TITLE + " " + getString(R.string.removed_from_favourites), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues favMovieValues = new ContentValues();
                    favMovieValues.put(FavouriteEntry.COLUMN_MOVIE_ID, MOVIE_ID);
                    con.getContentResolver().insert(FavouriteEntry.CONTENT_URI, favMovieValues);
                    Toast.makeText(con, MOVIE_TITLE + " " + getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                }
                break;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        this.inflater = inflater;
        menu.clear();
        if (IS_TWO_PANE_LAYOUT) {
            inflater.inflate(R.menu.menu_main, menu);
        }
        if (LINK != null && LINK != "") {
            inflater.inflate(R.menu.menu_detail, menu);
            MenuItem item = menu.findItem(R.id.menu_action_share);
            item.setTitle(getString(R.string.share_video_title, MOVIE_TITLE));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                con.onBackPressed();
                return true;
            case R.id.menu_action_share:
                Utility.shareYouTubeVideo(con, MOVIE_TITLE, LINK);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ((AnimateToolbar) con).onScroll(scrollY);

        ivBackdrop.setTranslationY(scrollY / 2);
        rlVideoOverlay.setTranslationY(scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public interface AnimateToolbar {
        void onScroll(int scrollY);
    }

    public interface MovieTitle {
        void getMovieTitle(String title);
    }
}
