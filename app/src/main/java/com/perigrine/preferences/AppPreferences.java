package com.perigrine.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ragamai on 09/10/17.
 */

public class AppPreferences {

    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName();
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;
    private final static String TOKEN = "token";
    private final static String CENTER_ID = "center_id";


    public AppPreferences(Context context) {
        try {
            this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
            this._prefsEditor = _sharedPrefs.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getToken() {
        return _sharedPrefs.getString(TOKEN, "");
    }

    public void setToken(String theme) {
        _prefsEditor.putString(TOKEN, theme);
        _prefsEditor.commit();

    }

    public int getCenterId() {
        return Integer.parseInt(_sharedPrefs.getString(CENTER_ID, ""));
    }

    public void setCenterId(String centerId) {
        _prefsEditor.putString(CENTER_ID, centerId);
        _prefsEditor.commit();

    }

}