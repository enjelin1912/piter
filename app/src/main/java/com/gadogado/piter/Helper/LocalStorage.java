package com.gadogado.piter.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.gadogado.piter.BuildConfig;

public class LocalStorage {
    private static final String PITER_LOCAL_STORAGE = BuildConfig.APPLICATION_ID + ".localstorage";
    public static final String USER_INFO = "userInfo";

    public static void setItem(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PITER_LOCAL_STORAGE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getItem(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PITER_LOCAL_STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void clearItem(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PITER_LOCAL_STORAGE, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
