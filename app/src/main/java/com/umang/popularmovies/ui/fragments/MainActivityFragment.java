package com.umang.popularmovies.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umang.popularmovies.Application;
import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract.CollectionEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.sync.MovieSyncAdapter;
import com.umang.popularmovies.ui.adapters.AdapterPosters;
import com.umang.popularmovies.ui.gist.RecyclerViewItemClickListener;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOADER_COLLECTION_MOVIES = 0;
    private static final int LOADER_MY_FAVOURITE_MOVIES = 1;
    FragmentActivity con;
    @Bind(R.id.main_rv_posters)
    RecyclerView rvPosters;
    @Bind(R.id.main_pb_loading_network)
    ProgressBar pbLoading;
    @Bind(R.id.main_tv_error_text)
    TextView tvErrorInfo;
    @Bind(R.id.main_srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences.Editor editor;
    AdapterPosters adapter;
    String[] sortItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        con = getActivity();

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        adapter = new AdapterPosters(con, null);
        rvPosters.setLayoutManager(new GridLayoutManager(con, con.getResources().getInteger(R.integer.main_grid_columns)));
        rvPosters.setAdapter(adapter);
        rvPosters.addOnItemTouchListener(
                new RecyclerViewItemClickListener(con, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ((ShowMovieDetailCallback) con).onMovieItemSelected(false, adapter.getMovieId(position), view);
                    }
                })
        );
        swipeRefreshLayout.setOnRefreshListener(this);
        sortItems = getResources().getStringArray(R.array.sort_by_array);
        loadMovieData();
        showLoading();

        return view;
    }

    private void loadMovieData() {
        loadAdapterWithData(null);
        int t = Application.sp.getInt(Constants.SP_SORT_BY, 0);
        con.setTitle(sortItems[Application.sp.getInt(Constants.SP_SORT_BY, 0)]);
        if (t >= 0 && t <= 1) {
            swipeRefreshLayout.setEnabled(true);
            getLoaderManager().restartLoader(LOADER_COLLECTION_MOVIES, null, MainActivityFragment.this);
        } else {
            swipeRefreshLayout.setEnabled(false);
            getLoaderManager().restartLoader(LOADER_MY_FAVOURITE_MOVIES, null, MainActivityFragment.this);
        }
        showLoading();
    }

    private void loadAdapterWithData(Cursor data) {
        if (data == null) {
            rvPosters.setVisibility(View.GONE);
        } else {
            rvPosters.setVisibility(View.VISIBLE);
        }
        adapter.changeBase(data);
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        tvErrorInfo.setText("");
    }

    private void hideLoading() {
        pbLoading.setVisibility(View.GONE);
    }

    private void showErrorText(String s) {
        tvErrorInfo.setText(s);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_action_sort:
                final String[] sortItems = getResources().getStringArray(R.array.sort_by_array);
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle(getString(R.string.dialog_sort_title))
                        .setSingleChoiceItems(sortItems, Application.sp.getInt(Constants.SP_SORT_BY, 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                con.setTitle(sortItems[which]);
                                editor = Application.sp.edit();
                                editor.putInt(Constants.SP_SORT_BY, which);
                                editor.apply();
                                dialog.dismiss();
                                loadMovieData();
                                showLoading();
                            }
                        });
                builder.create().show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_COLLECTION_MOVIES:
                return new CursorLoader(
                        con,
                        CollectionEntry.buildMovieForSavedForUri(Application.sp.getInt(Constants.SP_SORT_BY, 0)),
                        Constants.MOVIE_PROJECTION_COLS,
                        CollectionEntry.COLUMN_SAVED_FOR + " = ?",
                        null,
                        null
                );
            case LOADER_MY_FAVOURITE_MOVIES:
                return new CursorLoader(
                        con,
                        FavouriteEntry.CONTENT_URI,
                        Constants.MOVIE_PROJECTION_COLS,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int sortBy = Application.sp.getInt(Constants.SP_SORT_BY, 0);
        switch (loader.getId()) {
            case LOADER_COLLECTION_MOVIES:
                hideLoading();
                if (sortBy >= Constants.ROW_POPULAR && sortBy <= Constants.ROW_HIGHEST_RATED) {
                    if (data.getCount() == 0) {
                        MovieSyncAdapter.build(con)
                                .syncImmediately(getActivity(), MovieSyncAdapter.SYNC_BOTH);
                        showErrorText(getString(R.string.error_getting_data));
                    } else {
                        showErrorText("");
                    }
                    loadAdapterWithData(data);
                    sendMovieIdToInterface(data);
                }
                break;
            case LOADER_MY_FAVOURITE_MOVIES:
                hideLoading();
                if (sortBy == Constants.ROW_MY_FAVOURITES) {
                    if (data.getCount() == 0) {
                        showErrorText(getString(R.string.error_no_favourite_movies_found));
                    } else {
                        showErrorText("");
                    }
                    loadAdapterWithData(data);
                    sendMovieIdToInterface(data);
                }
            default:
                break;
        }
    }

    private void sendMovieIdToInterface(Cursor data) {
        int id = -1;
        if (data != null && data.getCount() != 0) {
            data.moveToFirst();
            id = data.getInt(Constants.RV_COL_MSB_MOVIE_ID);
        }
        ((ShowMovieDetailCallback) con).onMovieItemSelected(true, id, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        MovieSyncAdapter.build(con)
                .syncImmediately(getActivity(),
                        MovieSyncAdapter.SYNC_BOTH,
                        new MovieSyncAdapter.SyncMovies() {
                            @Override
                            public void onSyncComplete() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(con, getString(R.string.refreshing), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    public interface ShowMovieDetailCallback {
        void onMovieItemSelected(boolean dataFromFirstLoad, int movieId, View view);
    }


}
