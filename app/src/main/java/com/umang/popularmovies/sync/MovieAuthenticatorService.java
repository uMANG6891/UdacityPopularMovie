package com.umang.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by umang on 08/12/15.
 */
public class MovieAuthenticatorService extends Service {

    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        movieAuthenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return movieAuthenticator.getIBinder();
    }
}
