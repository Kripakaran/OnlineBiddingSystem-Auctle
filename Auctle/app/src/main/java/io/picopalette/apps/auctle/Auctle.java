package io.picopalette.apps.auctle;

import android.app.Application;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.picopalette.apps.auctle.utilities.PersistentCookieStore;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

/**
 * Created by ramkumar on 21/10/17.
 */

public class Auctle extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application started", "Loading cookies");
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        VolleySingleton.getInstance(this);
    }
}
