package com.umang.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.umang.popularmovies.data.MovieContract.CollectionEntry;
import com.umang.popularmovies.data.MovieContract.CommentEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by umang on 08/12/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " DATETIME NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_CAST + " TEXT NULL, " +
                MovieEntry.COLUMN_VIDEO_LINK + " TEXT NULL, " +

                // To assure this table saves unique row for movie even with having entry in collection as saved_for 0 and 1
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_COLLECTION_TABLE = "CREATE TABLE " + CollectionEntry.TABLE_NAME + " (" +
                CollectionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CollectionEntry.COLUMN_SAVED_FOR + " INTEGER NOT NULL, " +
                CollectionEntry.COLUMN_MOVIE_ID + " INTEGER NULL, " +

                // Set up the movie_id as a foreign key to movie table.
                " FOREIGN KEY (" + CollectionEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ")) ";

        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteEntry.COLUMN_MOVIE_ID + " INTEGER NULL," +

                // Set up the movie_id as a foreign key to movie table.
                " FOREIGN KEY (" + FavouriteEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ") " +
                // To assure this table saves unique row for favourite movie entry
                " UNIQUE (" + FavouriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_COMMENT_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " (" +
                CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CommentEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                CommentEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                CommentEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                // Set up the movie_id as a foreign key to movie table.
                " FOREIGN KEY (" + CommentEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + CommentEntry.COLUMN_MOVIE_ID + "));";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_COLLECTION_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
        db.execSQL(SQL_CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CollectionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME);
        onCreate(db);
    }
}
