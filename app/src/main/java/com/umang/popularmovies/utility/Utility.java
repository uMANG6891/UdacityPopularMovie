package com.umang.popularmovies.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.ShareCompat;
import android.view.Display;

import com.umang.popularmovies.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by umang on 21/11/15.
 */
public class Utility {

    private static String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static String REQUIRED_DATE_FORMAT = "dd MMM yyyy";

    public static int getScreenWidth(Activity con) {
        Display display = con.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String getYear(String dateString) {
        dateString = dateString.trim();
        if (dateString.length() == 0 || dateString.equalsIgnoreCase("null")) {
            return "-";
        }
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        SimpleDateFormat sdfYear = new SimpleDateFormat(REQUIRED_DATE_FORMAT, Locale.US);

        Date date = null;
        try {
            date = dbDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdfYear.format(date);
    }

    public static void shareYouTubeVideo(Context con, String movieName, String youtubeId) {
        con.startActivity(Intent.createChooser(createShareIntent(con, movieName, youtubeId), con.getString(R.string.share_video_title, movieName)));
    }

    public static Intent createShareIntent(Context con, String title, String key) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from((Activity) con)
                .setType("text/plain")
                .setText(con.getString(R.string.share_video, title, Constants.YOUTUBE_BASE + key));
        return builder.getIntent();
    }

    public static String parseRating(String rating) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(Double.valueOf(rating));
    }

    public static String shortenComment(Context con, String comment) {
        String[] words = comment.split(" ");
        if (words.length > Constants.MAX_WORDS_IN_COMMENT) {
            words = Arrays.copyOf(words, Constants.MAX_WORDS_IN_COMMENT);
            StringBuilder builder = new StringBuilder();
            for (String s : words) {
                builder.append(s).append(" ");
            }
            comment = builder.toString().trim() + con.getString(R.string.three_dots);

        }
        return comment;
    }
}
