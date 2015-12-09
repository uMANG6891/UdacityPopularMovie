package com.umang.popularmovies.utility;

/**
 * Created by umang on 20/11/15.
 */
public class Constants {
    public static final String MOVIE_DB_API_KEY = "YOUR_MOVIEDB_API_KEY"; // Enter your themoviedb api key here

    public static final String BASE_MOVIE_DB_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";z

    public static final double MOVIE_POSTER_MULTIPLIER = 1.5;
    public static final double MOVIE_BACKDROP_MULTIPLIER = 0.5617977528089888; // ~( 1/1.78)
    public static final String SP_SORT_BY = "sp_sort_by";

    public static String POSTER_SIZE;
    public static String BACKDROP_SIZE;

    public static String[] MOVIE_URL = new String[]{
            "popularity.desc",
            "vote_average.desc&vote_count.gte=1000"
            // for highest rating the vote_count should be greater than 1000
            // to account for movie being rated 10 with only been rated once or twice
    };


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

//    public static final String[] MOVIE_PROJECTION_COLS = {
//            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
//            MovieEntry.COLUMN_MOVIE_ID,
//            MovieEntry.COLUMN_SAVED_FOR,
//            MovieEntry.COLUMN_TITLE,
//            MovieEntry.COLUMN_OVERVIEW,
//            MovieEntry.COLUMN_POSTER_PATH,
//            MovieEntry.COLUMN_BACKDROP_PATH,
//            MovieEntry.COLUMN_RELEASE_DATE,
//            MovieEntry.COLUMN_VOTE_AVERAGE,
//            MovieEntry.COLUMN_VOTE_COUNT,
//            MovieEntry.COLUMN_CAST,
//            MovieEntry.COLUMN_VIDEO_LINK,
//            MovieEntry.COLUMN_REVIEWS
//    };

    public static final int RV_COL_ID = 0;
    public static final int RV_COL_MOVIE_ID = 1;
    public static final int RV_COL_SAVED_FOR = 2;
    public static final int RV_COL_TITLE = 3;
    public static final int RV_COL_OVERVIEW = 4;
    public static final int RV_COL_POSTER_PATH = 5;
    public static final int RV_COL_BACKDROP_PATH = 6;
    public static final int RV_COL_RELEASE_DATE = 7;
    public static final int RV_COL_VOTE_AVERAGE = 8;
    public static final int RV_COL_VOTE_COUNT = 9;
    public static final int RV_COL_CAST = 10;
    public static final int RV_COL_VIDEO_LINK = 11;
    public static final int RV_COL_REVIEWS = 12;
}
