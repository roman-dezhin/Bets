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

    public static void saveAvatar(Context context, String imagePath) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AVATAR, imagePath);
        editor.commit();
    }

    public static String getAvatar(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(AVATAR, "");
    }
}
