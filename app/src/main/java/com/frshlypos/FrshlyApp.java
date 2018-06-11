package com.frshlypos;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crashlytics.android.Crashlytics;
import com.frshlypos.utils.SessionManager;

import io.fabric.sdk.android.Fabric;
import io.socket.client.Socket;

/**
 * Created by Akshay.Panchal on 11-Jul-17.
 */

public class FrshlyApp extends Application {

    public SessionManager sessionManager;
    private static FrshlyApp instance;
    public Socket socket;
    public int MAX_MEAL_ITEMS = 24;
    public int MAX_SNACKS_ITEMS = 20;
    public int mobileNumberLength = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        sessionManager = new SessionManager(this);

    }

    public static FrshlyApp getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }


    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
}
