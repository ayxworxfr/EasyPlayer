package com.evildoer.player;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created with Android Studio.
 * Desc: PlayerApplication
 */
public class PlayerApplication extends Application {

    private static PlayerApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        // Custom fonts
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Monospace-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

    }

    public static PlayerApplication getInstance() {
        return sInstance;
    }
}
