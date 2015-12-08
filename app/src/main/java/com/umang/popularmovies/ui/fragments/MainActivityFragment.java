package com.umang.popularmovies.ui.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.umang.popularmovies.Application;
import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract.MovieEntry;
import com.umang.popularmovies.sync.MovieSyncAdapter;
import com.umang.popularmovies.ui.adapters.AdapterPosters;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Constants.MOVIE_JSON;
import com.umang.popularmovies.utility.Debug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    FragmentActivity con;

    @Bind(R.id.main_rv_posters)
    RecyclerView rvPosters;
    @Bind(R.id.main_pb_loading_network)
    ProgressBar pbLoading;
    @Bind(R.id.main_tv_error_text)
    TextView tvErrorInfo;

    SharedPreferences.Editor editor;
    AdapterPosters adapter;


    private static final int LOADER_MOVIES = 0;


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
        loadMovieData();
        showLoading();

        String[] sortItems = getResources().getStringArray(R.array.sort_by_array);
        con.setTitle(sortItems[Application.sp.getInt(Constants.SP_SORT_BY, 0)]);
        return view;
    }

    private void loadMovieData() {
        adapter.changeBase(null);
        getLoaderManager().initLoader(LOADER_MOVIES, null, this);
        showLoading();
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
                                editor = Application.sp.edit();
                                editor.putInt(Constants.SP_SORT_BY, which);
                                editor.apply();
                                dialog.dismiss();
                                con.setTitle(sortItems[which]);
                                getLoaderManager().restartLoader(LOADER_MOVIES, null, MainActivityFragment.this);
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
            case LOADER_MOVIES:
                return new CursorLoader(
                        con,
                        MovieEntry.buildMovieForSavedForUri(Application.sp.getInt(Constants.SP_SORT_BY, 0)),
                        null,
                        MovieEntry.COLUMN_SAVED_FOR + " = ?",
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
            case LOADER_MOVIES:
                hideLoading();
                if (data.getCount() == 0) {
                    MovieSyncAdapter.syncImmediately(getActivity());
                    showErrorText(getString(R.string.error_getting_data));
                } else {
                    showErrorText("");
                    adapter.changeBase(data);
                }
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
