package com.evildoer.player;

import android.content.Context;

/**
 * Created with Android Studio.
 * Desc: Injection
 */
public class Injection {

    public static Context provideContext() {
        return PlayerApplication.getInstance();
    }
}
