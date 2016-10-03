package biz.ddroid.bets;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

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
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        sInstance = this;
        LeakCanary.install(this);
    }
}
