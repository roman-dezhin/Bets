package biz.ddroid.bets;

import android.app.Application;
import android.content.Context;

import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkConstants;

public class BetApplication extends Application {
    private static BetApplication sInstance;

    public static ServicesClient getServicesClient() {
        return new ServicesClient(NetworkConstants.SERVER_ADDRESS, NetworkConstants.API_ENDPOINT);
    }

    public static BetApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
