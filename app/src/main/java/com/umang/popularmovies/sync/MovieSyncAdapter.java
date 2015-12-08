package com.umang.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.umang.popularmovies.Application;
import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract.MovieEntry;
import com.umang.popularmovies.utility.Constants;
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
import java.util.Vector;

/**
 * Created by umang on 08/12/15.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds. => 24 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 10001;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String API_KEY = Constants.MOVIE_DB_API_KEY;
        String MOVIE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by="
                + Constants.MOVIE_URL[Application.sp.getInt(Constants.SP_SORT_BY, 0)]
                + "&api_key=" + API_KEY;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJson = null;

        try {
            Uri builtUri = Uri.parse(MOVIE_URL);

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            moviesJson = buffer.toString();
            if (checkIfResultExists(moviesJson)) {
                saveMovies(moviesJson);
            }
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
    }

    private void saveMovies(String s) {
        try {
            JSONObject joData = new JSONObject(s);
            JSONArray jaMovies = new JSONArray(joData.getString(Constants.MOVIE_JSON.JSON_RESULT));

            Vector<ContentValues> cVVector = new Vector<>(jaMovies.length());

            for (int i = 0; i < jaMovies.length(); i++) {
                joData = jaMovies.getJSONObject(i);
                ContentValues weatherValues = new ContentValues();
                weatherValues.put(MovieEntry.COLUMN_MOVIE_ID, joData.getString(Constants.MOVIE_JSON.ID));
                weatherValues.put(MovieEntry.COLUMN_SAVED_FOR, Application.sp.getInt(Constants.SP_SORT_BY, 0));
                weatherValues.put(MovieEntry.COLUMN_TITLE, joData.getString(Constants.MOVIE_JSON.TITLE));
                weatherValues.put(MovieEntry.COLUMN_OVERVIEW, joData.getString(Constants.MOVIE_JSON.OVERVIEW));
                weatherValues.put(MovieEntry.COLUMN_POSTER_PATH, joData.getString(Constants.MOVIE_JSON.POSTER));
                weatherValues.put(MovieEntry.COLUMN_BACKDROP_PATH, joData.getString(Constants.MOVIE_JSON.BACKDROP));
                weatherValues.put(MovieEntry.COLUMN_RELEASE_DATE, joData.getString(Constants.MOVIE_JSON.RELEASE_DATE));
                weatherValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, joData.getString(Constants.MOVIE_JSON.VOTE_AVERAGE));
                weatherValues.put(MovieEntry.COLUMN_VOTE_COUNT, joData.getString(Constants.MOVIE_JSON.VOTE_COUNT));
                weatherValues.put(MovieEntry.COLUMN_CAST, "");
                weatherValues.put(MovieEntry.COLUMN_VIDEO_LINK, "");
                weatherValues.put(MovieEntry.COLUMN_REVIEWS, "");
                cVVector.add(weatherValues);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                        MovieEntry.COLUMN_SAVED_FOR + " = ?",
                        new String[]{String.valueOf(Application.sp.getInt(Constants.SP_SORT_BY, 0))});
                getContext().getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfResultExists(String s) {
        JSONObject joData = null;
        try {
            joData = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return joData != null && joData.has(Constants.MOVIE_JSON.JSON_RESULT);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
