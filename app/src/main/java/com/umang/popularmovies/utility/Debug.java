package com.umang.popularmovies.utility;

import android.util.Log;

/**
 * Created by umang on 20/11/15.
 */
public class Debug {
    private static boolean allowPrinting = true;

    public static void e(String tag, String value) {
        if (allowPrinting) {
            Log.e(tag, value);
        }
    }

    public static void e(String tag, double value) {
        if (allowPrinting) {
            Log.e(tag, ":" + value + ":");
        }
    }

    public static void e(String tag, boolean value) {
        if (allowPrinting) {
            Log.e(tag, ":" + value + ":");
        }
    }

}
