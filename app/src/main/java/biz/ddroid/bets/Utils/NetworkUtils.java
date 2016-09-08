package biz.ddroid.bets.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected())
            return false;
        return true;
    }
}
