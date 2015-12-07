package com.umang.popularmovies.utility;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        if (dateString == null || dateString.length() == 0 || dateString.equalsIgnoreCase("null")) {
            return "-";
        }
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.ENGLISH);
        SimpleDateFormat sdfYear = new SimpleDateFormat(REQUIRED_DATE_FORMAT, Locale.ENGLISH);

        Date date = null;
        try {
            date = dbDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdfYear.format(date);
    }

}
