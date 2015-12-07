package com.umang.popularmovies.utility;

/**
 * Created by umang on 20/11/15.
 */
public class Constants {
    public static final String MOVIE_DB_API_KEY = "YOUR_API_KEY_HERE"; // Enter your themoviedb api key here

    public static final double MOVIE_POSTER_MULTIPLIER = 1.5;
    public static final double MOVIE_BACKDROP_MULTIPLIER = 0.5617977528089888; // ~( 1/1.78)
    public static final String SP_SORT_BY = "sp_sort_by";

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

}
