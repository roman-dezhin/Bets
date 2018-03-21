package biz.ddroid.bets.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    public static final String PREFS_NAME = "BetsPrefsFile";
    public static final String TOKEN = "Token";
    public static final String UID = "UserId";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String EMAIL = "Email";
    public static final String AVATAR = "Avatar";
    public static final String AVATAR_FILE_ID = "AvatarFileId";
    public static final String TOUR_FILTER = "TourFilter";

    public static void setPref(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static String getPref(Context context, String name) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(name, "");
    }
}
