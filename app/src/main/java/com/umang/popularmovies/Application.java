package com.umang.popularmovies;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by umang on 21/11/15.
 */
public class Application extends android.app.Application {
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
