package com.umang.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by umang on 08/12/15.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.umang.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_COLLECTION = "collection";
    public static final String PATH_FAVOURITE = "favourite";
    public static final String PATH_COMMENT = "comment";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_CAST = "cast";
        public static final String COLUMN_VIDEO_LINK = "video_link";

        public static Uri buildOneMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    // saves list of popular and highest rated movies
    public static final class CollectionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COLLECTION;

        public static final String TAG_SAVED_FOR = "saved_for";

        public static final String TABLE_NAME = "collection";

        public static final String COLUMN_SAVED_FOR = "saved_for"; // 1 for popular, 2 for highest rated
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMovieForSavedForUri(long saved_for) {
            return CONTENT_URI.buildUpon().appendPath(TAG_SAVED_FOR).appendPath(Long.toString(saved_for)).build();
        }

        public static long getSavedForTypeFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

    }

    // saves user marked favourite movies
    public static final class FavouriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;


        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildFavouriteMovieUri(long movie_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movie_id)).build();
        }

        public static long getMovieIdFromFavouriteUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    // saves comment on the movie
    public static final class CommentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENT;


        public static final String TABLE_NAME = "comment";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildCommentUri(long movie_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movie_id)).build();
        }

        public static long getMovieIdFromCommentUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
