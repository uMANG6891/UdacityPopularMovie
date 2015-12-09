package com.umang.popularmovies.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract;
import com.umang.popularmovies.ui.activity.MovieReadMoreActivity;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Debug;
import com.umang.popularmovies.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 09/12/15.
 */
public class MovieReadMoreFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.frag_mrm_tv_overview)
    TextView tvOverview;
    @Bind(R.id.frag_mrm_tv_actors)
    TextView tvActors;
    @Bind(R.id.frag_mrm_tv_director)
    TextView tvDirector;
    @Bind(R.id.frag_mrm_tv_producer)
    TextView tvProducer;
    @Bind(R.id.frag_mrm_tv_writer)
    TextView tvWriter;
    @Bind(R.id.frag_mrm_tv_release_date)
    TextView tvReleaseDate;

    FragmentActivity con;
    int MOVIE_ID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        con = getActivity();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_movie_read_more, container, false);
        Intent intent = con.getIntent();
        if (intent != null && intent.hasExtra(MovieReadMoreActivity.EXTRA_MOVIE_ID)) {
            MOVIE_ID = intent.getIntExtra(MovieReadMoreActivity.EXTRA_MOVIE_ID, 0);
        }
        getLoaderManager().initLoader(0, null, this);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                con.finish();
            default:
                return false;
        }
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
            con.setTitle(data.getString(Constants.RV_COL_TITLE));

            tvReleaseDate.setText(Utility.getYear(data.getString(Constants.RV_COL_RELEASE_DATE)));
            tvOverview.setText(data.getString(Constants.RV_COL_OVERVIEW));

            String castJSON = data.getString(Constants.RV_COL_CAST);

            if (castJSON != null) {
                try {
                    JSONObject cast = new JSONObject(castJSON);
                    if (cast.has("actors")) {
                        String actors = "";
                        JSONArray jaActors = cast.getJSONArray("actors");
                        for (int i = 0; i < jaActors.length(); i++) {
                            actors += jaActors.getString(i);
                            if (i != jaActors.length() - 1) {
                                actors += ", ";
                            }
                        }
                        tvActors.setText(actors);
                    } else {
                        tvActors.setText("-");
                    }
                    if (cast.has("crew")) {
                        JSONObject crew = cast.getJSONObject("crew");
                        if (crew.has("director")) {
                            tvDirector.setText(crew.getString("director"));
                        } else {
                            tvDirector.setText("-");
                        }
                        if (crew.has("producer")) {
                            tvProducer.setText(crew.getString("producer"));
                        } else {
                            tvProducer.setText("-");
                        }
                        if (crew.has("writer")) {
                            tvWriter.setText(crew.getString("writer"));
                        } else {
                            tvWriter.setText("-");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
