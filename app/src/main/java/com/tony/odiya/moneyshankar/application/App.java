package com.tony.odiya.moneyshankar.application;

import android.app.Application;
import android.content.res.Configuration;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Locale;

/**
 * Created by TONY on 12/5/2017.
 */

public class App extends Application {
    public static Locale sDefLocale;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize time-zone information.
        AndroidThreeTen.init(this);
        sDefLocale = Locale.getDefault();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sDefLocale = Locale.getDefault();
    }
}
