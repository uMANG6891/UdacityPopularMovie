package com.umang.popularmovies.utility;

import android.content.Context;

import com.umang.popularmovies.R;
import com.umang.popularmovies.data.MovieContract.CollectionEntry;
import com.umang.popularmovies.data.MovieContract.CommentEntry;
import com.umang.popularmovies.data.MovieContract.FavouriteEntry;
import com.umang.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by umang on 20/11/15.
 */
public class Constants {

    public static final String BASE_MOVIE_DB_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

//    public static final double MOVIE_POSTER_MULTIPLIER = 1.5;
//    public static final double MOVIE_BACKDROP_MULTIPLIER = 0.5617977528089888; // ~( 1/1.78)

    public static final String SP_SORT_BY = "sp_sort_by";

    public static final int ROW_POPULAR = 0;
    public static final int ROW_HIGHEST_RATED = 1;
    public static final int ROW_MY_FAVOURITES = 2;
    public static final String YOUTUBE_BASE = "http://www.youtube.com/watch?v=";
    public static final String EXTRA_MOVIE_ID = "extra_movie_data";
    public static final String EXTRA_IS_TWO_PANE = "extra_is_two_pane";
    // projections to get movie id from favourites table
    public static final String[] FAVOURITE_PROJECTION_COLS = {
            FavouriteEntry.TABLE_NAME + "." + FavouriteEntry.COLUMN_MOVIE_ID
    };

    // extras passed between activities and fragments
    public static final int FAV_COL_MOVIE_ID = 0;
    // projections to get movie id from collection table
    public static final String[] COLLECTION_PROJECTION_COLS = {
            CollectionEntry.TABLE_NAME + "." + CollectionEntry.COLUMN_MOVIE_ID
    };
    public static final int COLL_COL_MOVIE_ID = 0;
    // projections to get movie by saved_by
    public static final String[] MOVIE_PROJECTION_COLS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_TITLE,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_VOTE_COUNT,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_CAST,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_VIDEO_LINK
    };
    public static final int RV_COL_MSB_ID = 0;
    public static final int RV_COL_MSB_MOVIE_ID = 1;
    public static final int RV_COL_MSB_TITLE = 2;
    public static final int RV_COL_MSB_OVERVIEW = 3;
    public static final int RV_COL_MSB_POSTER_PATH = 4;
    public static final int RV_COL_MSB_BACKDROP_PATH = 5;
    public static final int RV_COL_MSB_RELEASE_DATE = 6;
    public static final int RV_COL_MSB_VOTE_AVERAGE = 7;
    public static final int RV_COL_MSB_VOTE_COUNT = 8;
    public static final int RV_COL_MSB_CAST = 9;
    public static final int RV_COL_MSB_VIDEO_LINK = 10;
    // projections to get comments for the movie
    public static final String[] COMMENT_MOVIE_PROJECTION_COLS = {
            CommentEntry.TABLE_NAME + "." + CommentEntry.COLUMN_AUTHOR,
            CommentEntry.TABLE_NAME + "." + CommentEntry.COLUMN_CONTENT
    };
    public static final int RV_COL_CM_AUTHOR = 0;
    public static final int RV_COL_CM_CONTENT = 1;
    public static final int MAX_WORDS_IN_COMMENT = 40;
    public static String POSTER_SIZE;
    public static String BACKDROP_SIZE;
    public static String[] MOVIE_URL = new String[]{
            "popularity.desc",
            "vote_average.desc&vote_count.gte=1000"
            // for highest rating the vote_count should be greater than 1000
            // to account for movie being rated 10 with only been rated once or twice
    };

    public static String buildGetMovieReview(Context con, int MOVIE_ID) {
        return BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/credits?api_key=" + con.getString(R.string.MOVIE_DB_API_KEY);
    }

    public static String buildGetMovieVideoLink(Context con, int MOVIE_ID) {
        return Constants.BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/videos?api_key=" + con.getString(R.string.MOVIE_DB_API_KEY);
    }

    public static String buildGetMovieComments(Context con, int MOVIE_ID) {
        return Constants.BASE_MOVIE_DB_URL + "movie/" + MOVIE_ID + "/reviews?api_key=" + con.getString(R.string.MOVIE_DB_API_KEY);
    }

    public class MOVIE_JSON {
        public static final String JSON_RESULT = "results";

        public static final String ID = "id";
        public static final String BACKDROP = "backdrop_path";
        public static final String POSTER = "poster_path";
        public static final String TITLE = "original_title";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String VOTE_COUNT = "vote_count";
    }
}
