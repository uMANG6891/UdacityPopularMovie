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
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract;
import com.umang.popularmovies.ui.activity.MovieReadMoreActivity;
import com.umang.popularmovies.utility.Constants;
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
        tvActors.setMovementMethod(LinkMovementMethod.getInstance());
        tvDirector.setMovementMethod(LinkMovementMethod.getInstance());
        tvProducer.setMovementMethod(LinkMovementMethod.getInstance());
        tvWriter.setMovementMethod(LinkMovementMethod.getInstance());
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
            con.setTitle(data.getString(Constants.RV_COL_MSB_TITLE));

            tvReleaseDate.setText(Utility.getYear(data.getString(Constants.RV_COL_MSB_RELEASE_DATE)));
            tvOverview.setText(data.getString(Constants.RV_COL_MSB_OVERVIEW));

            String castJSON = data.getString(Constants.RV_COL_MSB_CAST);

            if (castJSON != null) {
                try {
                    JSONObject cast = new JSONObject(castJSON);
                    if (cast.has("actors")) {
                        JSONObject joActors;
                        SpannableStringBuilder actors = new SpannableStringBuilder();
                        JSONArray jaActors = cast.getJSONArray("actors");
                        for (int i = 0; i < jaActors.length(); i++) {
                            joActors = jaActors.getJSONObject(i);
                            actors.append(Utility.createActorUrl(con, joActors.getString("name"), joActors.getString("id")));
                            if (i != jaActors.length() - 1) {
                                actors.append(" ");
                            }
                        }
                        tvActors.setText(actors);
                    } else {
                        tvActors.setText("-");
                    }
                    if (cast.has("crew")) {
                        JSONObject crew = cast.getJSONObject("crew");
                        if (crew.has("director")) {
                            JSONObject joPerson = new JSONObject(crew.getString("director"));
                            tvDirector.setText(Utility.createActorUrl(con, joPerson.getString("name"), joPerson.getString("id")));
                        } else {
                            tvDirector.setText("-");
                        }
                        if (crew.has("producer")) {
                            JSONObject joPerson = new JSONObject(crew.getString("producer"));
                            tvProducer.setText(Utility.createActorUrl(con, joPerson.getString("name"), joPerson.getString("id")));
                        } else {
                            tvProducer.setText("-");
                        }
                        if (crew.has("writer")) {
                            JSONObject joPerson = new JSONObject(crew.getString("writer"));
                            tvWriter.setText(Utility.createActorUrl(con, joPerson.getString("name"), joPerson.getString("id")));
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
