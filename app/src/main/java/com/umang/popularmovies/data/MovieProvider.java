package com.umang.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.umang.popularmovies.data.MovieContract.CollectionEntry;
import com.umang.popularmovies.data.MovieContract.CommentEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by umang on 08/12/15.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_ONE = 101;

    static final int COLLECTION = 200;
    static final int COLLECTION_SAVED_FOR = 201;

    static final int FAVOURITE = 300;
    static final int FAVOURITE_ONE = 301;

    static final int COMMENT = 400;


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_ONE);

        matcher.addURI(authority, MovieContract.PATH_COLLECTION, COLLECTION);
        matcher.addURI(authority, MovieContract.PATH_COLLECTION + "/" + CollectionEntry.TAG_SAVED_FOR + "/#", COLLECTION_SAVED_FOR);

        matcher.addURI(authority, MovieContract.PATH_FAVOURITE, FAVOURITE);
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE + "/#", FAVOURITE_ONE);

        matcher.addURI(authority, MovieContract.PATH_COMMENT, COMMENT);

        return matcher;
    }

    // to get movies for saved_for
    private static final SQLiteQueryBuilder sMovieBySavedForQueryBuilder;

    static {
        sMovieBySavedForQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //collection INNER JOIN movie ON collection.movie_id= movie.movie_id
        sMovieBySavedForQueryBuilder.setTables(
                CollectionEntry.TABLE_NAME + " INNER JOIN " + MovieEntry.TABLE_NAME +
                        " ON " + CollectionEntry.TABLE_NAME + "." + CollectionEntry.COLUMN_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID);
    }

    private Cursor getMovieBySavedFor(Uri uri, String[] projection, String sortOrder) {
        String saved_for = String.valueOf(CollectionEntry.getSavedForTypeFromUri(uri));

        return sMovieBySavedForQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                CollectionEntry.TABLE_NAME + "." + CollectionEntry.COLUMN_SAVED_FOR + " = ?",
                new String[]{saved_for},
                null,
                null,
                sortOrder
        );
    }

    // to get all favourite movies for saved_for
    private static final SQLiteQueryBuilder sMovieByFavouriteQueryBuilder;

    static {
        sMovieByFavouriteQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //favourite INNER JOIN movie ON favourite.movie_id= movie.movie_id
        sMovieByFavouriteQueryBuilder.setTables(
                FavouriteEntry.TABLE_NAME + " INNER JOIN " + MovieEntry.TABLE_NAME +
                        " ON " + FavouriteEntry.TABLE_NAME + "." + FavouriteEntry.COLUMN_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID);
    }

    private Cursor getMovieByFavourite(String[] projection, String sortOrder) {
        return sMovieByFavouriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    // to get comments for the movie_id
    private static final SQLiteQueryBuilder sCommentByMovieQueryBuilder;

    static {
        sCommentByMovieQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //comment INNER JOIN movie ON comment.movie_id= movie.movie_id
        sCommentByMovieQueryBuilder.setTables(
                CommentEntry.TABLE_NAME + " INNER JOIN " + MovieEntry.TABLE_NAME +
                        " ON " + CommentEntry.TABLE_NAME + "." + CommentEntry.COLUMN_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID);
    }

    private Cursor getCommentByMovieFor(Uri uri, String[] projection, String sortOrder) {
        String movie_id = String.valueOf(CommentEntry.getMovieIdFromCommentUri(uri));

        return sCommentByMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                CommentEntry.TABLE_NAME + "." + CommentEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movie_id},
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        String movie_id;
        switch (sUriMatcher.match(uri)) {

            // "comment/"
            case COMMENT:
                retCursor = getCommentByMovieFor(uri, projection, sortOrder);
                break;

            // "favourite/#"
            case FAVOURITE_ONE:
                movie_id = String.valueOf(FavouriteEntry.getMovieIdFromFavouriteUri(uri));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{movie_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "favourite/"
            case FAVOURITE:
                retCursor = getMovieByFavourite(projection, sortOrder);
                break;

            // "collection/saved_for/*"
            case COLLECTION_SAVED_FOR:
                retCursor = getMovieBySavedFor(uri, projection, sortOrder);
                break;
            // "collection/"
            case COLLECTION:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CollectionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // "movie/#"
            case MOVIE_ONE:
                movie_id = String.valueOf(MovieEntry.getMovieIdFromUri(uri));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{movie_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "movie"
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                retCursor = getCommentByMovieFor(uri, projection, sortOrder);
        }
        if (getContext() != null)
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COMMENT:
                return CommentEntry.CONTENT_TYPE;

            case FAVOURITE_ONE:
                return FavouriteEntry.CONTENT_ITEM_TYPE;
            case FAVOURITE:
                return FavouriteEntry.CONTENT_TYPE;

            case COLLECTION_SAVED_FOR:
                return CollectionEntry.CONTENT_TYPE;

            case MOVIE_ONE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case MOVIE:
                _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieEntry.buildOneMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
//            case COLLECTION:
//                long _id = db.insert(CollectionEntry.TABLE_NAME, null, values);
//                if (_id > 0) {
//                    returnUri = CollectionEntry.buildOneMovieUri(_id);
//                } else {
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                }
//                break;
//            case FAVOURITE:
//                long _id = db.insert(FavouriteEntry.TABLE_NAME, null, values);
//                if (_id > 0) {
//                    returnUri = FavouriteEntry.buildOneMovieUri(_id);
//                } else {
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                }
            case FAVOURITE:
                _id = db.insert(FavouriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieEntry.buildOneMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COLLECTION:
                rowsDeleted = db.delete(CollectionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITE:
                rowsDeleted = db.delete(FavouriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COMMENT:
                rowsDeleted = db.delete(CommentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COLLECTION:
                rowsUpdated = db.update(CollectionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVOURITE:
                rowsUpdated = db.update(FavouriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COMMENT:
                rowsUpdated = db.update(CommentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long _id = 0;
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                switch (match) {
                    case MOVIE:
                        _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        break;
                    case COLLECTION:
                        _id = db.insert(CollectionEntry.TABLE_NAME, null, value);
                        break;
                    case FAVOURITE:
                        _id = db.insert(FavouriteEntry.TABLE_NAME, null, value);
                        break;
                    case COMMENT:
                        _id = db.insert(CommentEntry.TABLE_NAME, null, value);
                        break;
                }
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;

    }
}
