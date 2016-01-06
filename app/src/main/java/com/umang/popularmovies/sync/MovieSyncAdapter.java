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
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.umang.popularmovies.R;
import com.umang.popularmovies.data.FetchAsyncData;
import com.umang.popularmovies.data.MovieContract;
import com.umang.popularmovies.data.MovieContract.CollectionEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.data.MovieContract.MovieEntry;
import com.umang.popularmovies.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by umang on 08/12/15.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in seconds. => 24 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public static final String EXTRA_SYNC_TYPE = "sync_type";
    public static final int SYNC_MOST_POPULAR = 0;
    public static final int SYNC_HIGHEST_RATED = 1;
    public static final int SYNC_BOTH = 2;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 10001;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
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

    public static MovieSyncAdapter build(Context context) {
        MovieSyncAdapter movieSyncAdapter = new MovieSyncAdapter(context, true);
        return movieSyncAdapter;
    }

    private static void makeSync(Context context, int sync) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(EXTRA_SYNC_TYPE, sync);
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
        MovieSyncAdapter.build(context).syncImmediately(context, SYNC_BOTH);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        int syncType = extras.getInt(EXTRA_SYNC_TYPE, 0);

        String[] MOVIE_URL;
        if (syncType == SYNC_MOST_POPULAR || syncType == SYNC_HIGHEST_RATED) {
            MOVIE_URL = new String[]{getMovieUrl(syncType)};
        } else {
            MOVIE_URL = new String[]{getMovieUrl(SYNC_MOST_POPULAR), getMovieUrl(SYNC_HIGHEST_RATED)};
        }

        for (int i = 0; i < MOVIE_URL.length; i++) {
            String movies = FetchAsyncData.getFromInternet(MOVIE_URL[i]);
            if (movies != null && checkIfResultExists(movies)) {
                if (syncType == SYNC_MOST_POPULAR || syncType == SYNC_HIGHEST_RATED) {
                    saveMovies(movies, syncType);
                } else {
                    int saveFor = i == 0 ? SYNC_MOST_POPULAR : SYNC_HIGHEST_RATED;
                    saveMovies(movies, saveFor);
                }
            }
        }

        removeUnwantedMovieDetailsFromMovieEntryTable();
    }

    private void removeUnwantedMovieDetailsFromMovieEntryTable() {
        // getting all movie ids that exists in favourite and collection table
        List<Integer> MOVIE_IDS = new ArrayList<>();
        Cursor c = getContext().getContentResolver().query(FavouriteEntry.CONTENT_URI, Constants.FAVOURITE_PROJECTION_COLS, null, null, null);
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                MOVIE_IDS.add(c.getInt(Constants.FAV_COL_MOVIE_ID));
            }
            c.close();
        }
        c = getContext().getContentResolver().query(CollectionEntry.CONTENT_URI, Constants.COLLECTION_PROJECTION_COLS, null, null, null);
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                MOVIE_IDS.add(c.getInt(Constants.COLL_COL_MOVIE_ID));
            }
            c.close();
        }

        // Now remove all entries that doesn't exists in any table
        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (int id : MOVIE_IDS) {
            if (first) {
                first = false;
                inQuery.append("'").append(id).append("'");
            } else {
                inQuery.append(", '").append(id).append("'");
            }
        }
        inQuery.append(")");
        getContext().getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MOVIE_ID + " NOT IN " + inQuery.toString(),
                null);
    }

    private String getMovieUrl(int movieType) {
        return Constants.BASE_MOVIE_DB_URL
                + "discover/movie?sort_by="
                + Constants.MOVIE_URL[movieType]
                + "&api_key="
                + getContext().getString(R.string.MOVIE_DB_API_KEY);
    }

    private void saveMovies(String s, int saveMovieFor) {
        try {
            JSONObject joData = new JSONObject(s);
            JSONArray jaMovies = new JSONArray(joData.getString(Constants.MOVIE_JSON.JSON_RESULT));

            Vector<ContentValues> cvvMovies = new Vector<>(jaMovies.length());
            Vector<ContentValues> cvvCollection = new Vector<>(jaMovies.length());

            for (int i = 0; i < jaMovies.length(); i++) {
                joData = jaMovies.getJSONObject(i);
                ContentValues movieValues = new ContentValues();
                ContentValues collectionValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, joData.getString(Constants.MOVIE_JSON.ID));
                movieValues.put(MovieEntry.COLUMN_TITLE, joData.getString(Constants.MOVIE_JSON.TITLE));
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, joData.getString(Constants.MOVIE_JSON.OVERVIEW));
                movieValues.put(MovieEntry.COLUMN_POSTER_PATH, joData.getString(Constants.MOVIE_JSON.POSTER));
                movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, joData.getString(Constants.MOVIE_JSON.BACKDROP));
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, joData.getString(Constants.MOVIE_JSON.RELEASE_DATE));
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, joData.getString(Constants.MOVIE_JSON.VOTE_AVERAGE));
                movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, joData.getString(Constants.MOVIE_JSON.VOTE_COUNT));

                collectionValues.put(CollectionEntry.COLUMN_MOVIE_ID, joData.getString(Constants.MOVIE_JSON.ID));
                collectionValues.put(CollectionEntry.COLUMN_SAVED_FOR, saveMovieFor);

                cvvMovies.add(movieValues);
                cvvCollection.add(collectionValues);
            }
            if (cvvMovies.size() > 0) {
                // delete old data from the collection.
                // Also remove from movie which isn't marked as favourite or in the collection table
                getContext().getContentResolver().delete(MovieContract.CollectionEntry.CONTENT_URI,
                        MovieContract.CollectionEntry.COLUMN_SAVED_FOR + " = ?",
                        new String[]{String.valueOf(saveMovieFor)});

                // add all movies
                ContentValues[] cvArray = new ContentValues[cvvMovies.size()];
                cvvMovies.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);

                // add all collections
                cvArray = new ContentValues[cvvCollection.size()];
                cvvCollection.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(CollectionEntry.CONTENT_URI, cvArray);

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

    public void syncImmediately(Context context, int sync) {
        makeSync(context, sync);
    }

    public void syncImmediately(Context context, int sync, SyncMovies callback) {
        makeSync(context, sync);
        callback.onSyncComplete();
    }

//    public static void initializeSyncAdapter(Context context) {
//        getSyncAccount(context);
//    }

    public interface SyncMovies {
        void onSyncComplete();
    }
}
