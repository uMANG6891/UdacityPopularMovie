package com.umang.popularmovies.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umang.popularmovies.Application;
import com.umang.popularmovies.ui.activity.DetailActivity;
import com.umang.popularmovies.R;
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

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    FragmentActivity con;

    @Bind(R.id.main_rv_posters)
    RecyclerView rvPosters;
    @Bind(R.id.main_pb_loading_network)
    ProgressBar pbLoading;
    @Bind(R.id.main_tv_error_text)
    TextView tvErrorInfo;

    SharedPreferences.Editor editor;
    AdapterPosters adapter;

    // for savedInstance
    JSONArray JSON_DATA_FOR_SAVED_INSTANCE;
    ArrayList<HashMap<String, String>> MOVIE_DATA;
    private final String KEY_SAVE_JSON_STRING = "key_save_json_string";

    private boolean loadData = false;

    // movie related urls and image sizes
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    String POSTER_SIZE;
    String BACKDROP_SIZE;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        POSTER_SIZE = getString(R.string.poster_size);
        BACKDROP_SIZE = getString(R.string.backdrop_size);
        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_SAVE_JSON_STRING)) {
            loadData = true;
        } else {
            MOVIE_DATA = convertToArrayList(savedInstanceState.getString(KEY_SAVE_JSON_STRING));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (JSON_DATA_FOR_SAVED_INSTANCE != null) {
            outState.putString(KEY_SAVE_JSON_STRING, JSON_DATA_FOR_SAVED_INSTANCE.toString());
        }
        super.onSaveInstanceState(outState);
    }

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
        if (loadData) {
            loadMovieData();
            showLoading();
        } else {
            setAdapterWithData();
            hideLoading();
        }
        return view;
    }

    private void loadMovieData() {
        adapter.changeBase(null);
        adapter.notifyDataSetChanged();
        FetchMoviesData fetchMoviesData = new FetchMoviesData();
        fetchMoviesData.execute(FetchMoviesData.GET_POPULAR_MOVIES);
        showLoading();
    }

    private void setAdapterWithData() {
        adapter.changeBase(MOVIE_DATA);
        adapter.notifyDataSetChanged();
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
                String[] sortItems = getResources().getStringArray(R.array.sort_by_array);
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle(getString(R.string.dialog_sort_title))
                        .setSingleChoiceItems(sortItems, Application.sp.getInt(Constants.SP_SORT_BY, 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor = Application.sp.edit();
                                editor.putInt(Constants.SP_SORT_BY, which);
                                editor.apply();
                                dialog.dismiss();
                                loadMovieData();
                            }
                        });
                builder.create().show();
                return true;
            default:
                return false;
        }
    }

    public class FetchMoviesData extends AsyncTask<Integer, Void, String> {

        public static final int GET_POPULAR_MOVIES = 0;

        public String API_KEY = Constants.MOVIE_DB_API_KEY;
        public String MOVIE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by="
                + Constants.MOVIE_URL[Application.sp.getInt(Constants.SP_SORT_BY, 0)]
                + "&api_key=" + API_KEY;

        private final String LOG_TAG = FetchMoviesData.class.getSimpleName();

        @Override
        protected String doInBackground(Integer... params) {
            switch (params[0]) {
                case GET_POPULAR_MOVIES:
                    return getPopularMoviesFromServer(MOVIE_URL);
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideLoading();
            if (s == null) {
                showErrorText(getString(R.string.error_getting_data));
            } else {
                if (checkIfResultExists(s)) {
                    s = getResultJsonArrayString(s);
                    MOVIE_DATA = convertToArrayList(s);
                    setAdapterWithData();
                } else {
                    showErrorText(getString(R.string.error_server_error));
                }
            }
        }

        private String getResultJsonArrayString(String s) {
            JSONObject joData = null;
            JSONArray jaMovies = new JSONArray();
            try {
                joData = new JSONObject(s);
                jaMovies = new JSONArray(joData.getString(MOVIE_JSON.JSON_RESULT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jaMovies.toString();
        }

        private boolean checkIfResultExists(String s) {
            JSONObject joData = null;
            try {
                joData = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return joData != null && joData.has(MOVIE_JSON.JSON_RESULT);
        }


        private String getPopularMoviesFromServer(String inputUrl) {
            if (inputUrl == null || inputUrl.length() == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJson = null;


            try {
                Uri builtUri = Uri.parse(inputUrl);

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJson = buffer.toString();
                return moviesJson;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Debug.e(LOG_TAG, "Error closing stream");
                    }
                }
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
    }

    private ArrayList<HashMap<String, String>> convertToArrayList(String s) {
        JSONObject joData;
        try {
            JSON_DATA_FOR_SAVED_INSTANCE = new JSONArray(s);
            HashMap<String, String> row;
            MOVIE_DATA = new ArrayList<>();
            for (int i = 0; i < JSON_DATA_FOR_SAVED_INSTANCE.length(); i++) {
                joData = new JSONObject(JSON_DATA_FOR_SAVED_INSTANCE.get(i).toString());
                row = new HashMap<>();
                row.put(MOVIE_JSON.BACKDROP, BASE_IMAGE_URL + BACKDROP_SIZE + joData.getString(MOVIE_JSON.BACKDROP));
                row.put(MOVIE_JSON.POSTER, BASE_IMAGE_URL + POSTER_SIZE + joData.getString(MOVIE_JSON.POSTER));
                row.put(MOVIE_JSON.ID, joData.getString(MOVIE_JSON.ID));
                row.put(MOVIE_JSON.TITLE, joData.getString(MOVIE_JSON.TITLE));
                row.put(MOVIE_JSON.OVERVIEW, joData.getString(MOVIE_JSON.OVERVIEW));
                row.put(MOVIE_JSON.RELEASE_DATE, joData.getString(MOVIE_JSON.RELEASE_DATE));
                row.put(MOVIE_JSON.VOTE_AVERAGE, joData.getString(MOVIE_JSON.VOTE_AVERAGE));
                row.put(MOVIE_JSON.VOTE_COUNT, joData.getString(MOVIE_JSON.VOTE_COUNT));
                MOVIE_DATA.add(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MOVIE_DATA;
    }


}
